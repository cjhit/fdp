package com.github.cjhit.fdp.core.condition;

import lombok.Data;

/**
 * 文件名：MultiLikeCondition.java
 * 说明：多字段模糊查询条件。
 * 应用于页面上一个输入框值要同时模糊匹配多个字段的情况，如同时模糊匹配用户名/手机号码
 * 作者：水哥
 * 创建时间：2020-05-17
 */
@Data
public class MultiLikeCondition extends BaseCondition {
    private String propVal;

    public MultiLikeCondition(String prop, String propVal) {
        this.prop = prop;
        this.propVal = propVal;
    }
}
