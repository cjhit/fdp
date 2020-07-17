package com.github.cjhit.fdp.core;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件名：RestEmptyResult.java
 * 说明：
 * 作者：水哥
 * 创建时间：2020-06-18
 */
@Data
@ApiModel(value = "RestEmptResult(空响应对象，仅返回code及msg，无其他业务数据)")
public class RestEmptyResult {
    @ApiModelProperty(value = "响应码，200为成功", example = "200")
    protected Integer code = 200;//http code
    @ApiModelProperty(value = "提示信息", example = "success")
    protected String msg = "success";

    public RestEmptyResult() {
    }

    public RestEmptyResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
