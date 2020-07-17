package com.github.cjhit.fdp.core;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 文件名：RestPageResult.java
 * 说明：查询单一实体相应对象
 * 作者：水哥
 * 创建时间：2020-06-18
 */
@Data
@ApiModel(value = "RestEntityResult(实体响应对象)")
public class RestEntityResult<T> extends RestEmptyResult {
    private T data;

    public RestEntityResult() {
    }

    public RestEntityResult(T data) {
        this.data = data;
    }
}
