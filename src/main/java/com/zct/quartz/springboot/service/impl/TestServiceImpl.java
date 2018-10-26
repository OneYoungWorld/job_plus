package com.zct.quartz.springboot.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xxl.job.core.log.XxlJobLogger;
import com.zct.quartz.springboot.redis.RedisUtil;
import com.zct.quartz.springboot.service.TestService;

@Component
public class TestServiceImpl implements TestService {
	@Autowired
	private RedisUtil redisUtil;

	@Override
	public void test() throws Exception {
		 XxlJobLogger.log("--------- 调用测试接口开始--------- ");
		String Key = "REPLYTIME";
		String CustomerNo = "";
		if (redisUtil.exists(Key)) {
			System.out.println("使用缓存");
			CustomerNo = redisUtil.get(Key);
		} else {
//			List<LcCont_Lis> list = lisService.queryLcCont("", "");
//			CustomerNo = list.get(0).getAppntno();
//			redisUtil.set("CustomerNo"+CustomerNo, CustomerNo);
		}
	}

}
