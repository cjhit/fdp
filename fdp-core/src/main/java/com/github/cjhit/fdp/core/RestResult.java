package com.github.cjhit.fdp.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件名：RestResult.java
 * 说明： REST返回结果
 * 作者： 水哥
 * 创建时间：2020-04-22
 *
 */
@Data
@ApiModel(value = "RestResult(一般响应对象，业务数据在data中)")
public class RestResult extends RestEmptyResult {
    @ApiModelProperty(value = "业务数据JSON")
    private JSONObject data;

    public RestResult() {
    }

    public RestResult(Integer code, String msg) {
        super(code, msg);
    }

    public RestResult(String key, Object val) {
        this.data = new JSONObject();
        data.put(key, val);
    }

    public RestResult(JSONObject obj) {
        this.data = obj;
    }

    public void addProperty(String key, Object val) {
        if (null == data) {
            data = new JSONObject();
        }
        data.put(key, val);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
