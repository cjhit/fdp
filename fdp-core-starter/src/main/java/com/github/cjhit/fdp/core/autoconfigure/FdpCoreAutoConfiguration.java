package com.github.cjhit.fdp.core.autoconfigure;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 文件名：FdpCoreAutoConfiguration.java
 * 说明：
 * 作者：水哥
 * 创建时间：2018-12-13
 */
@Configuration
@ComponentScan(basePackages = {"com.github.cjhit.fdp"})
@MapperScan("com.github.cjhit.fdp.dao")
public class FdpCoreAutoConfiguration {
}
