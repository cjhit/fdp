package com.github.cjhit.fdp.common.cache;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * 文件名：FdpCacheClearJob.java
 * 说明：
 * 作者：水哥
 * 创建时间：2020-05-18
 */
@Slf4j
public class FdpCacheClearJob extends Thread {
    private String name;
    private ConcurrentMap<Object, Object> store;
    private ConcurrentMap<Object, LocalDateTime> storeTTL;

    public FdpCacheClearJob(String name, ConcurrentMap<Object, Object> store, ConcurrentMap<Object, LocalDateTime> storeTTL) {
        this.name = name;
        this.store = store;
        this.storeTTL = storeTTL;
    }

    @Override
    public void run() {
        log.info("正在清理：" + name + "过期缓存...");
        Iterator<Map.Entry<Object, LocalDateTime>> entries = storeTTL.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Object, LocalDateTime> entry = entries.next();
            if (entry.getValue().isBefore(LocalDateTime.now())) {
                log.warn("key：" + entry.getKey() + "已过期；过期时间为：" + format(entry.getValue()) + " 。正在删除");
                storeTTL.remove(entry.getKey());
                store.remove(entry.getKey());
            }
        }
        log.info("清理结束");
    }

    private String format(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
