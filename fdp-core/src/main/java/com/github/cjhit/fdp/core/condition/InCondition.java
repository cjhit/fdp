package com.github.cjhit.fdp.core.condition;

import lombok.Data;

/**
 * 文件名：InCondition.java
 * 说明：
 * 作者： 水哥
 * 创建时间：2020-04-25
 *
 */
@Data
public class InCondition extends BaseCondition {

    private Iterable vals;

    public InCondition(String prop, Iterable vals) {
        this.prop = prop;
        this.vals = vals;
    }
}
