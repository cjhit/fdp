package com.github.cjhit.fdp.core.condition;

import lombok.Data;

/**
 * 文件名：SqlCondition.java
 * 说明：自由sql条件
 * 作者： 水哥
 * 创建时间：2019-04-09
 *
 */
@Data
public class SqlCondition extends BaseCondition {
    private String sql;

    public SqlCondition(String sql) {
        this.sql = sql;
    }
}
