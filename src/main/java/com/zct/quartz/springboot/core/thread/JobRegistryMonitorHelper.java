package com.zct.quartz.springboot.core.thread;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.enums.RegistryConfig;
import com.zct.quartz.springboot.core.model.XxlJobGroup;
import com.zct.quartz.springboot.core.model.XxlJobRegistry;
import com.zct.quartz.springboot.dao.XxlJobGroupDao;
import com.zct.quartz.springboot.dao.XxlJobInfoDao;
import com.zct.quartz.springboot.dao.XxlJobLogDao;
import com.zct.quartz.springboot.dao.XxlJobRegistryDao;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * job registry instance
 *
 * @author zct 2016-10-02 19:10:24
 */
@Component
public class JobRegistryMonitorHelper {
    private static Logger logger = LoggerFactory.getLogger(JobRegistryMonitorHelper.class);

    private static JobRegistryMonitorHelper instance = new JobRegistryMonitorHelper();

    public static JobRegistryMonitorHelper getInstance() {
        return instance;
    }

    private Thread registryThread;
    private volatile boolean toStop = false;
    @Autowired
    public XxlJobLogDao xxlJobLogDao;
    @Autowired
    public XxlJobInfoDao xxlJobInfoDao;
    @Autowired
    public XxlJobRegistryDao xxlJobRegistryDao;
    @Autowired
    public XxlJobGroupDao xxlJobGroupDao;
    @Autowired
    public AdminBiz adminBiz;
    public void start() {
        registryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!toStop) {
                    try {
                        // auto registry group
                        List<XxlJobGroup> groupList = xxlJobGroupDao.findByAddressType(0);
                        if (CollectionUtils.isNotEmpty(groupList)) {

                            // remove dead address (admin/executor)
                            xxlJobRegistryDao.removeDead(RegistryConfig.DEAD_TIMEOUT);

                            // fresh online address (admin/executor)
                            HashMap<String, List<String>> appAddressMap = new HashMap<String, List<String>>();
                            List<XxlJobRegistry> list = xxlJobRegistryDao.findAll(RegistryConfig.DEAD_TIMEOUT);
                            if (list != null) {
                                for (XxlJobRegistry item : list) {
                                    if (RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
                                        String appName = item.getRegistryKey();
                                        List<String> registryList = appAddressMap.get(appName);
                                        if (registryList == null) {
                                            registryList = new ArrayList<String>();
                                        }

                                        if (!registryList.contains(item.getRegistryValue())) {
                                            registryList.add(item.getRegistryValue());
                                        }
                                        appAddressMap.put(appName, registryList);
                                    }
                                }
                            }

                            // fresh group address
                            for (XxlJobGroup group : groupList) {
                                List<String> registryList = appAddressMap.get(group.getAppName());
                                String addressListStr = null;
                                if (CollectionUtils.isNotEmpty(registryList)) {
                                    Collections.sort(registryList);
                                    addressListStr = StringUtils.join(registryList, ",");
                                }
                                group.setAddressList(addressListStr);
                                xxlJobGroupDao.update(group);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("job registry instance error:{}", e);
                    }
                    try {
                        TimeUnit.SECONDS.sleep(RegistryConfig.BEAT_TIMEOUT);
                    } catch (InterruptedException e) {
                        logger.error("job registry instance error:{}", e);
                    }
                }
            }
        });
        registryThread.setDaemon(true);
        registryThread.start();
    }

    public void toStop() {
        toStop = true;
        // interrupt and wait
        registryThread.interrupt();
        try {
            registryThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
