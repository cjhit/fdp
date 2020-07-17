package com.github.cjhit.fdp.common.devutils;

import lombok.Data;

/**
 * 文件名：GenCodeConfig.java
 * 说明：代码生成器配置
 * 作者：水哥
 */
@Data
public class GenCodeConfig {

    private String controllerPackageName;
    private String servciePageageName;
    private String daoPackageName;
    private String entityPackageName;
    private String author = "水哥";
    private String owner = "水哥";

}
