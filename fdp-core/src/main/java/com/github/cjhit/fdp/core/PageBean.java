package com.github.cjhit.fdp.core;

import com.alibaba.fastjson.annotation.JSONField;
import com.github.cjhit.fdp.common.constants.FdpConstants;
import com.github.cjhit.fdp.core.condition.BaseCondition;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件名：PageBean.java
 * 后面pageBean可拆成 FdpEntity 以及FdpPageableEntity
 * 说明：默认所有的条件均为and条件，除非特殊指定orConditionList
 * <p>
 * 注解说明：
 * 默认排序请采用tk.mybatis.mapper.annotation.Order 注解
 * 数据库字段名请采用javax.persistence.Colum 注解
 * Transient:表示不序列化到数据库
 * JsonIgnore：表示不参与序列化
 * 作者： 水哥
 * 创建时间：2020-04-22
 *
 */
@Data
public class PageBean implements Serializable {
    @ApiModelProperty(hidden = true)
    @Transient
    @JSONField(serialize = false, deserialize = true)
    private Integer currentPage = 1;// 当前页数（列表数据不返回到前台，但前台）
    @ApiModelProperty(hidden = true)
    @Transient
    @JSONField(serialize = false, deserialize = true)
    private Integer pageSize = 10;// 每页数据量
    @ApiModelProperty(hidden = true)
    @Transient
    @JSONField(serialize = false, deserialize = true)
    private Short needPage = FdpConstants.YES;// 是否分页(默认分页)


    //选择字段
    @ApiModelProperty(hidden = true)
    @Transient
    @JSONField(serialize = false, deserialize = false)
    private transient String[] resultProps;//指定查询属性


    //支持多个字段的排序
    @ApiModelProperty(hidden = true)
    @Transient
    @JSONField(serialize = false, deserialize = true)
    private transient String orderBy;// 排序字段,支持逗号分割

    @ApiModelProperty(hidden = true)
    @Transient
    @JSONField(serialize = false, deserialize = true)
    private transient String sortBy;//排序方式,支持逗号分割
    @ApiModelProperty(hidden = true)
    @Transient
    @JSONField(serialize = false, deserialize = false)
    private transient List<BaseCondition> andConditionList;//and条件

    @ApiModelProperty(hidden = true)
    @Transient
    @JSONField(serialize = false, deserialize = false)
    private transient List<BaseCondition> orConditionList;//or条件,最终语句为： “ or （条件1 and  条件2）”


    @ApiModelProperty(hidden = true)
    public void addAndCondition(BaseCondition condition) {
        if (null == andConditionList) {
            andConditionList = new ArrayList<>();
        }
        andConditionList.add(condition);
    }

    @ApiModelProperty(hidden = true)
    public void addOrCondition(BaseCondition condition) {
        if (null == orConditionList) {
            orConditionList = new ArrayList<>();
        }
        orConditionList.add(condition);
    }
}
