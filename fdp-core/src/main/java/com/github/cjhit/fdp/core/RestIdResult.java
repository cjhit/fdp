package com.github.cjhit.fdp.core;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 文件名：RestEmptyResult.java
 * 说明：
 * 作者：水哥
 * 创建时间：2020-06-18
 */
@Data
@ApiModel(value = "RestIdResult(响应对象，返回新增后的实体的id)")
public class RestIdResult extends RestEmptyResult {
    private IdEntity data;

    public RestIdResult() {
    }

    public RestIdResult(IdEntity data) {
        this.data = data;
    }
}
