package com.github.cjhit.fdp.core.condition;

import lombok.Data;

/**
 * 文件名：GreaterThanOrEqualToCondition.java
 * 说明：
 * 作者： 水哥
 * 创建时间：2019-03-26
 *
 */
@Data
public class GreaterThanOrEqualToCondition extends BaseCondition {
    private Object val;

    public GreaterThanOrEqualToCondition(String prop, Object val) {
        this.prop = prop;
        this.val = val;
    }
}
