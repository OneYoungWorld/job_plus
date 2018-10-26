package com.zct.quartz.springboot.core.jobbean;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * http job bean
 * “@DisallowConcurrentExecution” diable concurrent, thread size can not be only one, better given more
 * @author zct 2015-12-17 18:20:34
 */
//@DisallowConcurrentExecution
@Component
public class RemoteHttpJobBean extends QuartzJobBean {
	private static Logger logger = LoggerFactory.getLogger(RemoteHttpJobBean.class);
	@Autowired
	private com.zct.quartz.springboot.core.trigger.XxlJobTrigger XxlJobTrigger;
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {

		// load jobId
		JobKey jobKey = context.getTrigger().getJobKey();
		Integer jobId = Integer.valueOf(jobKey.getName());

		// trigger
		XxlJobTrigger.trigger(jobId);
	}

}