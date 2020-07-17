package com.github.cjhit.fdp.core.condition;

import lombok.Data;

/**
 * 文件名：EqCondition.java
 * 说明：
 * 作者： 水哥
 * 创建时间：2019-04-10
 *
 */
@Data
public class EqCondition extends BaseCondition {
    private Object val;

    public EqCondition(String prop, Object val) {
        this.prop = prop;
        this.val = val;
    }
}
