package com.github.cjhit.fdp.core;

import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 文件名：BaseDao.java
 * 说明：
 * 作者： 水哥
 * 创建时间：2020-04-22
 *
 */
public interface BaseDao<T> extends Mapper<T> {
    public void batchSave(List<T> list);
}
