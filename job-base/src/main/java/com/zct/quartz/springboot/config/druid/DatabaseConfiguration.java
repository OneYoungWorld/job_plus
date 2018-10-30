package com.zct.quartz.springboot.config.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;
import java.util.Properties;


@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
public class DatabaseConfiguration {

	@Autowired
	private DataSourceProperties properties;


	//注册dataSource
	@Bean(initMethod = "init", destroyMethod = "close")
	public DruidDataSource dataSource() throws SQLException {

		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setDriverClassName(properties.getDriverClassName());
		druidDataSource.setUrl(properties.getUrl());
		druidDataSource.setUsername(properties.getUsername());
		druidDataSource.setPassword(properties.getPassword());
		druidDataSource.setInitialSize(properties.getInitialSize());
		druidDataSource.setMinIdle(properties.getMinIdle());
		druidDataSource.setMaxActive(properties.getMaxActive());
		druidDataSource.setMaxWait(properties.getInitialSize());
		druidDataSource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
		druidDataSource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
		druidDataSource.setValidationQuery(properties.getValidationQuery());
		druidDataSource.setTestWhileIdle(properties.getTestWhileIdle());
		druidDataSource.setTestOnBorrow(properties.getTestOnBorrow());
		druidDataSource.setTestOnReturn(properties.getTestOnReturn());
		druidDataSource.setPoolPreparedStatements(properties.getPoolPreparedStatements());
		druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(properties.getMaxPoolPreparedStatementPerConnectionSize());
		druidDataSource.setFilters(properties.getFilters());
		return druidDataSource;
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());
		//mybatis分页
		PageHelper pageHelper = new PageHelper();
		Properties props = new Properties();
		props.setProperty("dialect", "mysql");
		props.setProperty("reasonable", "true");
		props.setProperty("supportMethodsArguments", "true");
		props.setProperty("returnPageInfo", "check");
		props.setProperty("params", "count=countSql");
		pageHelper.setProperties(props); //添加插件
		sqlSessionFactoryBean.setPlugins(new Interceptor[]{pageHelper});
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath*:com/zct/quartz/springboot/dao/mapper/**/*Mapper.xml"));
		return sqlSessionFactoryBean.getObject();
	}

	@Bean
	public PlatformTransactionManager transactionManager() throws SQLException {
		return new DataSourceTransactionManager(dataSource());
	}
}
