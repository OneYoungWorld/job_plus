package com.zct.quartz.springboot.dao;

import org.apache.ibatis.annotations.Param;

import com.zct.quartz.springboot.core.model.XxlJobGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zct on 16/9/30.
 */
@Repository
public interface XxlJobGroupDao {

    public List<XxlJobGroup> findAll();

    public List<XxlJobGroup> findByAddressType(@Param("addressType") int addressType);

    public int save(XxlJobGroup xxlJobGroup);

    public int update(XxlJobGroup xxlJobGroup);

    public int remove(@Param("id") int id);

    public XxlJobGroup load(@Param("id") int id);
}
