package com.github.cjhit.fdp.common.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.core.serializer.support.SerializationDelegate;
import org.springframework.lang.Nullable;
import tk.mybatis.mapper.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

/**
 * 文件名：FdpCache.java
 * 说明：参考自ConcurrentMapCache,加入失效功能
 * 作者：水哥
 * 创建时间：2020-05-18
 */
@Slf4j
public class FdpCache extends AbstractValueAdaptingCache {

    private final String name;
    private final ConcurrentMap<Object, Object> store = new ConcurrentHashMap<>(256);
    private final ConcurrentMap<Object, LocalDateTime> storeTTL = new ConcurrentHashMap<>(256);
    private Duration duration;//预设的该缓存项的有效期
    @Nullable
    private final SerializationDelegate serialization;

    protected FdpCache(String name, @Nullable SerializationDelegate serialization, Duration duration) {

        super(true);
        Assert.notNull(name, "Name must not be null");
        this.name = name;
        this.serialization = serialization;
        this.duration = duration;
        this.startClearExpiredJob();
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final ConcurrentMap<Object, Object> getNativeCache() {
        return this.store;
    }

    @Override
    @Nullable
    protected Object lookup(Object key) {
        return this.store.get(key);
    }


    @Override
    public ValueWrapper get(Object key) {
        if (isKeyExpired(key)) {
            //log.info("key:" + key + "不存在或已失效");
            evict(key);
            return null;
        }
        return super.get(key);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        if (isKeyExpired(key)) {
            //log.info("key:" + key + "不存在或已失效");
            evict(key);
            return null;
        }
        return super.get(key, type);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        if (isKeyExpired(key)) {
            //log.info("key:" + key + "不存在或已失效");
            evict(key);
            return null;
        }
        return (T) fromStoreValue(this.store.computeIfAbsent(key, k -> {
            try {
                return toStoreValue(valueLoader.call());
            } catch (Throwable ex) {
                throw new ValueRetrievalException(key, valueLoader, ex);
            }
        }));
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        this.store.put(key, toStoreValue(value));
        //同时存入有效性信息
        this.storeTTL.put(key, computeExpiredTime());
    }

    public void refresh(Object key) {
        //log.info("刷新缓存有效性：" + key);
        this.storeTTL.put(key, computeExpiredTime());
    }

    @Override
    @Nullable
    public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        Object existing = this.store.putIfAbsent(key, toStoreValue(value));
        return toValueWrapper(existing);
    }

    @Override
    public void evict(Object key) {
        this.store.remove(key);
        this.storeTTL.remove(key);
    }

    @Override
    public void clear() {
        this.store.clear();
    }

    @Override
    protected Object toStoreValue(@Nullable Object userValue) {
        Object storeValue = super.toStoreValue(userValue);
        if (this.serialization != null) {
            try {
                return serializeValue(this.serialization, storeValue);
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Failed to serialize cache value '" + userValue +
                        "'. Does it implement Serializable?", ex);
            }
        } else {
            return storeValue;
        }
    }

    private Object serializeValue(SerializationDelegate serialization, Object storeValue) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            serialization.serialize(storeValue, out);
            return out.toByteArray();
        } finally {
            out.close();
        }
    }

    @Override
    protected Object fromStoreValue(@Nullable Object storeValue) {
        if (storeValue != null && this.serialization != null) {
            try {
                return super.fromStoreValue(deserializeValue(this.serialization, storeValue));
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Failed to deserialize cache value '" + storeValue + "'", ex);
            }
        } else {
            return super.fromStoreValue(storeValue);
        }

    }

    /**
     * 计算失效时间
     *
     * @return
     */
    private LocalDateTime computeExpiredTime() {
        LocalDateTime now = LocalDateTime.now();
        now = now.plus(duration);
        return now;
    }

    private boolean isKeyExpired(Object key) {
        if (!this.storeTTL.containsKey(key)) {
            return true;
        }
        LocalDateTime dateTime = this.storeTTL.get(key);
        //log.info("key:" + key + " 的失效时间是：" + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ";当前时间：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return dateTime.isBefore(LocalDateTime.now());
    }


    private Object deserializeValue(SerializationDelegate serialization, Object storeValue) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream((byte[]) storeValue);
        try {
            return serialization.deserialize(in);
        } finally {
            in.close();
        }
    }

    /**
     * 定时清理过期key任务
     */
    private void startClearExpiredJob() {
        log.info("设置缓存清理任务");
        FdpCacheClearJob clearJob = new FdpCacheClearJob(this.name, this.store, this.storeTTL);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(clearJob, 1, 1, TimeUnit.HOURS);//1小时清理1次
//        service.scheduleAtFixedRate(clearJob, 1, 1, TimeUnit.HOURS);//1小时清理1次

        log.info("设置完成");
    }

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        now = now.plus(Duration.ofMinutes(32));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println(now.format(formatter));
    }
}
