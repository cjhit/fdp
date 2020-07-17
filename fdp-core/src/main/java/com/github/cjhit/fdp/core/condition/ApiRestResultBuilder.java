package com.github.cjhit.fdp.core.condition;

import com.github.cjhit.fdp.core.PageBean;
import com.github.cjhit.fdp.core.PageResult;
import com.github.cjhit.fdp.core.RestEmptyResult;
import com.github.cjhit.fdp.core.RestPageResult;

/**
 * 文件名：ApiRestResultBuilder.java
 * 说明：
 * 作者：水哥
 * 创建时间：2020-06-18
 */
public class ApiRestResultBuilder<T extends PageBean> {
    public static RestEmptyResult success() {
        return new RestEmptyResult();
    }

    public static RestPageResult success(PageResult pageResult) {
        return new RestPageResult(pageResult);
    }

}
