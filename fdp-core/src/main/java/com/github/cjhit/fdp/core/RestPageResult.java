package com.github.cjhit.fdp.core;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 文件名：RestPageResult.java
 * 说明：分页查询结果
 * 作者：水哥
 * 创建时间：2020-06-18
 */
@Data
@ApiModel(value = "RestPageResult(分页查询结果响应对象)")
public class RestPageResult<T> extends RestEmptyResult {
    private PageResult<T> data;

    public RestPageResult() {
    }

    public RestPageResult(PageResult<T> data) {
        this.data = data;
    }
}
