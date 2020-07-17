package com.github.cjhit.fdp.common.cache;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.serializer.support.SerializationDelegate;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 文件名：FdpCacheManager.java
 * 说明：带有TTL功能的单机版缓存（低配版redis缓存)。
 * 使用场景：当项目规模不大，或者不想引入厚重redis组件时，但是又希望缓存能带有失效时间，可直接采用本缓存实现
 * 不适用场景：分布式情况下
 * 参考redis实现1：定时检测失效key并移除
 * 参考redis实现2：为每个key单独开启移除定时器（损耗太大，此处改成有读取该key时再做判断，若失效返回null并删除）
 * 作者：水哥
 * 创建时间：2020-05-18
 */
public class FdpCacheManager implements CacheManager, BeanClassLoaderAware {
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>(16);
    @Nullable
    private SerializationDelegate serialization;
    private Duration defaultDuration = Duration.ofMinutes(10);//默认10分钟的有效期
    private Map<String, Duration> durationMap = new HashMap<>();


    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.serialization = new SerializationDelegate(classLoader);
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = this.cacheMap.get(name);
        if (cache == null) {
            synchronized (this.cacheMap) {
                cache = this.cacheMap.get(name);
                if (cache == null) {
                    cache = createConcurrentMapCache(name);
                    this.cacheMap.put(name, cache);
                }
            }
        }
        return cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(this.cacheMap.keySet());
    }

    protected Cache createConcurrentMapCache(String name) {
        return new FdpCache(name, this.serialization, durationMap.containsKey(name) ? durationMap.get(name) : defaultDuration);
    }

    public void setDefaultDuration(Duration defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    public void setCacheDuration(String name, Duration duration) {
        this.durationMap.put(name, duration);
    }

    public static void main(String[] args) {
        FdpCacheManager manager = new FdpCacheManager();
        manager.getCache("token").put("name", "cjh");
        System.out.println(manager.getCache("token").get("name", String.class));
    }

}
