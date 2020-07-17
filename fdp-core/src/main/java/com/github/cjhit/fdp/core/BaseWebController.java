package com.github.cjhit.fdp.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

/**
 * 文件名：BaseWebController.java
 * 说明：基础模块。适用于单模块基础controller
 * 备注：后续权限可以通过环绕的切面来控制
 * 作者： 水哥
 * 创建时间：2019-01-04
 *
 */
public abstract class BaseWebController<P extends PageBean, ID extends Serializable, DAO extends BaseDao<P>, S extends BaseService<P, ID, DAO>> {

    /**
     * 异步获取分页数据
     *
     * @param entity
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/list")
    @ResponseBody
    public RestResult list(@RequestBody P entity) throws Exception {
        PageResult<P> pageResult = service.findPageResult(entity);
        return RestResultBuilder.success("pageResult", pageResult);
    }

    /**
     * 异步获取分页数据
     *
     * @param entity
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/saveOrUpdate")
    @ResponseBody
    public RestResult saveOrUpdate(P entity) throws Exception {
        if (isPrimaryKeyEmpty(entity)) {
            service.findList(entity);
        } else {
            service.update(entity);
        }
        return RestResultBuilder.success();

    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public RestResult deleteByPk(@PathVariable ID id) throws Exception {
        service.delete(id);
        return RestResultBuilder.success();
    }



    /**
     * 判断主键是否为空
     *
     * @param entity
     * @return
     */
    protected abstract boolean isPrimaryKeyEmpty(P entity);

    @Autowired
    protected S service;

    /**
     * 默认模块名词
     *
     * @return
     */
    protected abstract String getModuleName();

    /**
     * 默认模块前缀
     *
     * @return
     */
    protected abstract String getModulePrefix();

    /**
     * 跳转到模块首页
     *
     * @return
     * @throws Exception
     */
    @GetMapping(value = "")
    public String toIndex() throws Exception {
        return getModulePrefix() + getModuleName() + "List";
    }
}
