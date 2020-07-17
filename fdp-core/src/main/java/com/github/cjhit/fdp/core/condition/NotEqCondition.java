package com.github.cjhit.fdp.core.condition;

import lombok.Data;

/**
 * 文件名：NotEqCondition.java
 * 说明：不等于
 * 作者： 水哥
 * 创建时间：2020-04-25
 *
 */
@Data
public class NotEqCondition extends BaseCondition {

    private Object val;

    public NotEqCondition(String prop, Object val) {
        this.prop = prop;
        this.val = val;
    }
}
