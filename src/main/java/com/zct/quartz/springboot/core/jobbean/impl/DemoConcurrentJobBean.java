package com.zct.quartz.springboot.core.jobbean.impl;

//@Deprecated
//@DisallowConcurrentExecution	// 串行；线程数要多配置几个，否则不生效；
//public class DemoConcurrentJobBean extends LocalNomalJobBean {
//
//	@Override
//	public Object handle(String... param) {
//		
//		try {
//			TimeUnit.SECONDS.sleep(10);
//		} catch (InterruptedException e) {
//			logger.error(e.getMessage(), e);
//		}
//		
//		return false;
//	}
//
//}
