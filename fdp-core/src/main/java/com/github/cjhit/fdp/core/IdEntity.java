package com.github.cjhit.fdp.core;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件名：IdEntity.java
 * 说明：Id实体
 * 作者：水哥
 * 创建时间：2020-06-18
 */
@Data
public class IdEntity {
    @ApiModelProperty(value = "实体保存后的Id", example = "fbacdb14b37748b1bb2c18c6bc492aa5")
    private String id;

    public IdEntity(String id) {
        this.id = id;
    }
}
