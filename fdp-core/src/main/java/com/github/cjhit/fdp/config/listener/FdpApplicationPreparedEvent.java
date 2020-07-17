package com.github.cjhit.fdp.config.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

/**
 * 文件名：FdpApplicationPreparedEvent.java
 * 说明：
 * 作者：水哥
 * 创建时间：2019-01-04
 *
 */
@Slf4j
public class FdpApplicationPreparedEvent implements ApplicationListener<ApplicationPreparedEvent> {
    @Override
    public void onApplicationEvent(ApplicationPreparedEvent applicationPreparedEvent) {
        log.info("执行了ApplicationPreparedEvent");
    }
}
