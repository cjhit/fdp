package com.github.cjhit.fdp.core.condition;

import lombok.Data;

/**
 * 文件名：LessThanOrEqualToCondition.java
 * 说明：
 * 作者： 水哥
 * 创建时间：2019-03-26
 *
 */
@Data
public class LessThanOrEqualToCondition extends BaseCondition {
    private Object val;

    public LessThanOrEqualToCondition(String prop, Object val) {
        this.prop = prop;
        this.val = val;
    }
}