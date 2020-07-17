package com.github.cjhit.fdp.core;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * 文件名：RestResultBuilder.java
 * 说明： REST返回结果构造就
 * 作者： 水哥
 * 创建时间：2020-04-26
 *
 */
public class RestResultBuilder {

    /**
     * 空结果
     *
     * @return
     */
    public static RestEmptyResult emptyResult() {
        return new RestEmptyResult();
    }

    /**
     * 空结果（兼容旧代码）
     *
     * @return
     */
    public static RestResult success() {
        return new RestResult();
    }


    /**
     * 返回实体结果
     *
     * @param entity
     * @param <T>
     * @return
     */
    public static <T> RestEntityResult<T> entityResult(T entity) {
        return new RestEntityResult<T>(entity);
    }

    /**
     * 返回分页结果
     *
     * @param pageResult
     * @param <T>
     * @return
     */
    public static <T> RestPageResult<T> pageResult(PageResult<T> pageResult) {
        return new RestPageResult<>(pageResult);
    }

    /**
     * 返回列表结果
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> RestListEntityResult<T> listResult(List<T> list) {
        return new RestListEntityResult<>(list);
    }

    /**
     * 返回id结果
     *
     * @param id
     * @return
     */
    public static RestIdResult idReSult(String id) {
        return new RestIdResult(new IdEntity(id));
    }

    public static RestResult success(String key, Object value) {
        return new RestResult(key, value);
    }

    public static RestResult success(JSONObject obj) {
        return new RestResult(obj);
    }


    public static RestResult failure(HttpStatus code, String message) {
        return new RestResult(code.value(), message);
    }
}
