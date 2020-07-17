package com.github.cjhit.fdp.core;

import com.github.cjhit.fdp.common.FdpUtil;
import com.github.cjhit.fdp.common.constants.FdpConstants;
import com.github.cjhit.fdp.common.constants.LikeType;
import com.github.cjhit.fdp.core.condition.*;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Transient;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 文件名：BaseService.java
 * 说明：
 * 作者： 水哥
 * 创建时间：2020-04-22
 */
public class BaseService<T extends PageBean, ID extends Serializable, D extends BaseDao<T>> {


    @Autowired
    protected D dao;

    private Class<?> clazz = null;

    /**
     * 获取实体类class
     *
     * @return
     */
    public Class<?> getEntityClass() {
        if (clazz == null) {
            clazz = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        return clazz;
    }


    /**
     * 根据主键获取记录
     *
     * @param id
     * @return
     */
    public T getById(ID id) {
        return dao.selectByPrimaryKey(id);
    }


    /**
     * 根据属性值列表获取列表
     *
     * @param valList
     * @return
     */
    public List<T> getListByPropVals(String prop, Collection<? extends Serializable> valList) {
        Class clz = getEntityClass();
        Example example = new Example(clz);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn(prop, valList);
        PageHelper.clearPage();
        return dao.selectByExample(example);
    }

    /**
     * 根据某个属性值获取唯一的一条记录
     *
     * @param key
     * @param val
     * @return
     */
    public T getOne(String key, Object val) {
        Example example = new Example(getEntityClass());
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(key, val);
        List<T> list = dao.selectByExample(example);
        if (null != list && list.size() > 1) {
            throw new RuntimeException("record not unique.");
        }
        if (null != list && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 查询所有数据
     *
     * @return
     */
    public List<T> findAll(T entity) {
        //有指定排序的采用指定的，否则采用实体类中注解设置的
        if (StringUtils.isNotBlank(entity.getOrderBy())) {
            Example example = new Example(entity.getClass());
            example.setOrderByClause(FdpUtil.getOrderByClause(entity.getOrderBy(), entity.getSortBy(), entity.getClass()));
            return dao.selectByExample(example);
        }
        return dao.selectAll();

    }

    public List<T> findAll() {
        return dao.selectAll();
    }


    /**
     * 获取分页结果
     *
     * @param entity
     * @return
     */
    public PageResult<T> findPageResult(T entity) {
        Example example = generateExample(entity);
        int count = dao.selectCountByExample(example);
        List<T> list = null;
        if (count > 0) {
            list = findPageList(entity);
        }
        return new PageResult<T>(count, list, entity.getPageSize(), entity.getCurrentPage());
    }

    /**
     * 查询分页列表
     *
     * @param entity
     * @return
     */
    public List<T> findPageList(T entity) {
        entity.setNeedPage(FdpConstants.YES);
        return findListByEntity(entity);
    }

    /**
     * 查询列表（不分页）
     *
     * @param entity
     * @return
     */
    public List<T> findList(T entity) {
        entity.setNeedPage(FdpConstants.NO);
        return findListByEntity(entity);
    }

    /**
     * 查询数据列表，同时支持分页/不分页
     *
     * @param entity
     * @return
     */
    private List<T> findListByEntity(T entity) {
        Example example = generateExample(entity);
        try {
            PageHelper.clearPage();
            if (entity.getNeedPage().equals(FdpConstants.YES)) { //分页
                PageHelper.startPage(entity.getCurrentPage(), entity.getPageSize(), false);
            }
            return dao.selectByExample(example);
        } catch (Exception e) {
            throw e;
        } finally {
            PageHelper.clearPage();
        }
    }


    /**
     * 根据条件统计数据
     *
     * @param entity
     * @return
     */
    public int countByCondition(T entity) {
        Example example = generateExample(entity);
        return dao.selectCountByExample(example);
    }

    /**
     * 插入数据
     *
     * @param entity
     * @return
     */
    public void save(T entity) {
        dao.insert(entity);
    }

    /**
     * 根据主键更新,属性不为null的值不会进行更新
     *
     * @param entity
     * @return
     */
    public void update(T entity) {
        dao.updateByPrimaryKeySelective(entity);
    }


    /**
     * 根据主键更新实体全部字段
     *
     * @param entity
     */
    public void updateAll(T entity) {
        dao.updateByPrimaryKey(entity);
    }

    /**
     * 删除数据
     *
     * @param id
     * @return
     */
    public void delete(ID id) {
        dao.deleteByPrimaryKey(id);
    }


    public void deleteByConditon(T entity) {
        Example example = generateExample(entity);
        dao.deleteByExample(example);
    }


    public void batchSave(List<T> list) {
        dao.batchSave(list);
    }


    /**
     * 根据example查询列表
     *
     * @param example
     * @return
     */
    public List<T> selectByExample(Example example) {
        return dao.selectByExample(example);
    }

    /**
     * 根据example查询总数
     *
     * @param example
     * @return
     */
    public int selectCountByExample(Example example) {
        return dao.selectCountByExample(example);
    }


    /**
     * 从实体对象中构建查询条件
     *
     * @param entity
     * @return
     */
    private Example generateExample(T entity) {
        //TODO 配合索引，调整顺序
        Example example = new Example(entity.getClass());
        if (null != entity.getResultProps() && entity.getResultProps().length > 0) {
            example.selectProperties(entity.getResultProps());
        }
        Example.Criteria criteria = example.createCriteria();


        List<Field> fieldList = FdpUtil.getAllFieldList(entity.getClass());
        //过滤条件
        for (Field field : fieldList) {
            //有包含Transient注解的，则不是正常的属性
            Transient anno = field.getAnnotation(Transient.class);
            if (null != anno) {
                continue;
            }
            andEqCondition(criteria, entity, field);
        }

        //and
        if (FdpUtil.isNotEmpty(entity.getAndConditionList())) {
            for (BaseCondition condition : entity.getAndConditionList()) {
                addAndCondition(criteria, condition, entity);
            }
        }
        //or条件, 最终语句为： “or （条件1 and 条件2）”
        if (FdpUtil.isNotEmpty(entity.getOrConditionList())) {
            Example.Criteria orCriteria = example.or();
            for (BaseCondition condition : entity.getOrConditionList()) {
                addAndCondition(orCriteria, condition, entity);
            }
        }


        if (StringUtils.isNotBlank(entity.getOrderBy())) { //未设置时，tk会自动取默认排序
            example.setOrderByClause(FdpUtil.getOrderByClause(entity.getOrderBy(), entity.getSortBy(), entity.getClass()));
        }
        return example;
    }

    private void addAndCondition(Example.Criteria criteria, BaseCondition condition, T entity) {
        if (condition instanceof GreaterThanCondition) {
            criteria.andGreaterThan(condition.getProp(), ((GreaterThanCondition) condition).getVal());
        } else if (condition instanceof GreaterThanOrEqualToCondition) {
            criteria.andGreaterThanOrEqualTo(condition.getProp(), ((GreaterThanOrEqualToCondition) condition).getVal());
        } else if (condition instanceof InCondition) {
            criteria.andIn(condition.getProp(), ((InCondition) condition).getVals());
        } else if (condition instanceof IsNotNullCondition) {
            criteria.andIsNotNull(condition.getProp());
        } else if (condition instanceof IsNullCondition) {
            criteria.andIsNull(condition.getProp());
        } else if (condition instanceof LessThanCondition) {
            criteria.andLessThan(condition.getProp(), ((LessThanCondition) condition).getVal());
        } else if (condition instanceof LessThanOrEqualToCondition) {
            criteria.andLessThanOrEqualTo(condition.getProp(), ((LessThanOrEqualToCondition) condition).getVal());
        } else if (condition instanceof LikeCondition) {
            addLikeCondition(criteria, (LikeCondition) condition);
        } else if (condition instanceof NotEqCondition) {
            criteria.andNotEqualTo(condition.getProp(), ((NotEqCondition) condition).getVal());
        } else if (condition instanceof SqlCondition) {
            criteria.andCondition(((SqlCondition) condition).getSql());
        } else if (condition instanceof EqCondition) {
            criteria.andEqualTo(condition.getProp(), ((EqCondition) condition).getVal());
        } else if (condition instanceof MultiLikeCondition) {
            criteria.andCondition(getMultiLikeCondionSql((MultiLikeCondition) condition, entity));
        }
    }


    /**
     * 拼凑多字段模糊查询的sql语句
     * 最终结果如：(nick_name like '%1%' or phone like '%1%' )
     *
     * @param condition
     * @param entity
     * @return
     */
    private String getMultiLikeCondionSql(MultiLikeCondition condition, T entity) {
        List<String> sqlFragmentList = new ArrayList<>();
        String[] fieldArr = condition.getProp().split(",");
        for (String field : fieldArr) {
            String dbField = FdpUtil.entityFieldToDbField(entity.getClass(), field);
            sqlFragmentList.add(dbField + " like '%" + condition.getPropVal() + "%' ");
        }
        String sql = " (" + StringUtils.join(sqlFragmentList, " or ") + ")";
        return sql;
    }

    private void andEqCondition(Example.Criteria criteria, T entity, Field field) {

        String methodName = getGetMethodName(field.getName());
        try {
            Method method = entity.getClass().getMethod(methodName);
            Object obj = method.invoke(entity);
            if (null != obj) {
                criteria.andEqualTo(field.getName(), obj);
            }
        } catch (Exception e) {
            throw new RuntimeException("addCondition err:" + e.getMessage());
        }
    }

    private void addLikeCondition(Example.Criteria criteria, LikeCondition likeCondition) {

        String val = "%" + likeCondition.getPropVal() + "%";
        if (likeCondition.getLikeType().equals(LikeType.PREFIX)) {
            val = "%" + likeCondition.getPropVal();
        } else if (likeCondition.getLikeType().equals(LikeType.PREFIX)) {
            val = likeCondition.getPropVal() + "%";
        }
        criteria.andLike(likeCondition.getProp(), val);
    }


    private static String getGetMethodName(String fieldName) {
        String firstChar = fieldName.substring(0, 1);
        firstChar = firstChar.toUpperCase();
        return "get" + firstChar + fieldName.substring(1);
    }
}
