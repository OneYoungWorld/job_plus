package com.zct.quartz.springboot.dao;

import org.apache.ibatis.annotations.Param;

import com.zct.quartz.springboot.core.model.XxlJobLogGlue;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * job log for glue
 * @author zct 2016-5-19 18:04:56
 */
@Repository
public interface XxlJobLogGlueDao {
	
	public int save(XxlJobLogGlue xxlJobLogGlue);
	
	public List<XxlJobLogGlue> findByJobId(@Param("jobId") int jobId);

	public int removeOld(@Param("jobId") int jobId, @Param("limit") int limit);

	public int deleteByJobId(@Param("jobId") int jobId);
	
}
