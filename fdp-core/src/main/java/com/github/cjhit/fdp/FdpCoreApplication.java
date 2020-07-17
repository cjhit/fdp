package com.github.cjhit.fdp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 文件名：FdpCoreApplication.java
 * 说明：
 * 作者：水哥
 * 创建时间：2018-12-12
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.github.cjhit.fdp"})
@MapperScan("com.github.cjhit.fdp.dao")
public class FdpCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(FdpCoreApplication.class, args);
    }

}
