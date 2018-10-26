package com.zct.quartz.springboot.controller;

import com.zct.quartz.springboot.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import com.xxl.job.core.log.XxlJobLogger;

/**
 * 
 * 【测试接口】
 * @author xxl
 * @date 2018年1月23日 下午12:36:06 
 * @version V1.0
 */
//value必须小写开头
@JobHander(value = "testHandler")
@Service
public class TestHandler extends IJobHandler {
    
	@Autowired
	private TestService testService;
	
    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        XxlJobLogger.log("--------- 调用测试接口开始--------- ");
        long startTime = System.currentTimeMillis();
        try {
            XxlJobLogger.log("--------- 调用测试接口结束--------- ");
            testService.test(); 
        } catch (Exception e) {
            XxlJobLogger.log(">>>>>>>调用测试接口任务执行失败，原因：" + e.getMessage());
            ReturnT<String> fail = ReturnT.FAIL;
            fail.setMsg("调用测试接口任务执行失败，原因：" + e.getMessage());
            return ReturnT.FAIL;
        }
        XxlJobLogger.log("本次用时：" + (System.currentTimeMillis() - startTime) / 1000.00f + " 秒");
        ReturnT<String> success = ReturnT.SUCCESS;
        success.setMsg("调用测试接口任务执行成功！");
        return ReturnT.SUCCESS;
    }

}
