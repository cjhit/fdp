package com.github.cjhit.fdp.core;

import com.github.cjhit.fdp.common.FdpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件名：BaseRestController.java
 * 说明：
 * 作者： 水哥
 * 创建时间：2019-03-22
 *
 */
public abstract class BaseRestController<P extends PageBean, ID extends Serializable, DAO extends BaseDao<P>, S extends BaseService<P, ID, DAO>> {


    /**
     * 分页结果
     *
     * @param entity
     * @return
     * @throws Exception
     */
    protected RestResult pageResult(@RequestBody P entity) {
        PageResult<P> pageResult = service.findPageResult(entity);
        return RestResultBuilder.success("pageResult", pageResult);
    }

    protected RestResult list(P entity) {
        List<P> list = this.service.findList(entity);
        return RestResultBuilder.success("list", list);
    }

    /**
     * 新增或保存（此操作是幂等的）
     *
     * @param entity
     * @return
     * @throws Exception
     */
    protected RestResult saveOrUpdate(P entity) {
        if (isPrimaryKeyEmpty(entity)) {
            service.save(entity);
        } else {
            service.update(entity);
        }
        return RestResultBuilder.success();

    }

    protected RestResult get(@PathVariable ID id) {
        P entity = service.getById(id);
        return RestResultBuilder.success("obj", entity);
    }


    protected RestResult delete(@PathVariable ID id) {
        service.delete(id);
        return RestResultBuilder.success();
    }

    /**
     * 根据列表，构建ID-MAP
     *
     * @param list
     * @return
     */
    public Map<ID, P> genPrimaryKeyMap(List<P> list) {
        Map<ID, P> retMap = new HashMap<>();
        if (FdpUtil.isNotEmpty(list)) {
            for (P p : list) {
                retMap.put(getPrimaryKey(p), p);
            }
        }

        return retMap;
    }


    /**
     * 判断主键是否为空（主键只会是string 或 long）
     *
     * @param entity
     * @return
     */
    protected boolean isPrimaryKeyEmpty(P entity) {
        ID id = getPrimaryKey(entity);
        if (id instanceof String) {
            return StringUtils.isEmpty((String) id);
        }
        return null == id;
    }

    /**
     * 获取主键(通过反射取注解),子类中可重写以提高速度，避免反射
     *
     * @param entity
     * @return
     */
    protected ID getPrimaryKey(P entity) {
        List<Field> fields = FdpUtil.getAllFieldList(entity.getClass());
        for (Field field : fields) {
            Id id = field.getAnnotation(Id.class);
            if (null != id) {
                Object val = FdpUtil.getObjPropVal(entity, field.getName());
                if (null != val) {
                    return (ID) val;
                }
            }
        }
        return null;
    }


    @Autowired
    protected S service;

}
