package com.zct.quartz.springboot.dao;

import org.apache.ibatis.annotations.Param;

import com.zct.quartz.springboot.core.model.XxlJobRegistry;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zct on 16/9/30.
 */
@Repository
public interface XxlJobRegistryDao {

    public int removeDead(@Param("timeout") int timeout);

    public List<XxlJobRegistry> findAll(@Param("timeout") int timeout);

    public int registryUpdate(@Param("registryGroup") String registryGroup,
                              @Param("registryKey") String registryKey,
                              @Param("registryValue") String registryValue);

    public int registrySave(@Param("registryGroup") String registryGroup,
                            @Param("registryKey") String registryKey,
                            @Param("registryValue") String registryValue);

    public int registryDelete(@Param("registryGroup") String registGroup,
                              @Param("registryKey") String registryKey,
                              @Param("registryValue") String registryValue);

}
