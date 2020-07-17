package com.github.cjhit.fdp.common.constants;

/**
 * 文件名：LikeType.java
 * 说明：模糊匹配类型枚举
 * 作者：水哥
 * 创建时间：2018-12-24
 *
 */
public enum LikeType {
    PREFIX("prefix"), SUFFIX("suffix"), ALL("all");
    private String name;

    private LikeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
