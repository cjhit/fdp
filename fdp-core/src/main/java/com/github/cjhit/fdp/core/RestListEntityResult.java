package com.github.cjhit.fdp.core;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * 文件名：RestListEntityResult.java
 * 说明：
 * 作者：水哥
 * 创建时间：2020-06-18
 */
@Data
@ApiModel(value = "RestListEntityResult")
public class RestListEntityResult<T> extends RestEmptyResult {
    private List<T> data;

    public RestListEntityResult() {
    }

    public RestListEntityResult(List<T> dataList) {
        this.data = dataList;
    }
}
