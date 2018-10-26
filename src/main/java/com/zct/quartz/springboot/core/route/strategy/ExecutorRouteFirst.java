package com.zct.quartz.springboot.core.route.strategy;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.zct.quartz.springboot.core.route.ExecutorRouter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Created by zct on 17/3/10.
 */
@Component
public class ExecutorRouteFirst extends ExecutorRouter {
    @Autowired
    public com.zct.quartz.springboot.core.trigger.XxlJobTrigger XxlJobTrigger;
    public String route(int jobId, ArrayList<String> addressList) {
        return addressList.get(0);
    }

    @Override
    public ReturnT<String> routeRun(TriggerParam triggerParam, ArrayList<String> addressList) {

        // address
        String address = route(triggerParam.getJobId(), addressList);

        // run executor
        ReturnT<String> runResult = XxlJobTrigger.runExecutor(triggerParam, address);
        runResult.setContent(address);
        return runResult;
    }
}
