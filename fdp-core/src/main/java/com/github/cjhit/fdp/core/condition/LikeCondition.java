package com.github.cjhit.fdp.core.condition;

import com.github.cjhit.fdp.common.constants.LikeType;
import lombok.Data;

/**
 * 文件名：LikeCondition.java
 * 说明：模糊查询条件
 * 作者： 水哥
 * 创建时间：2020-04-24
 *
 */
@Data
public class LikeCondition extends BaseCondition {
    private LikeType likeType;
    private String propVal;

    public LikeCondition(String prop, LikeType likeType, String propVal) {
        this.prop = prop;
        this.likeType = likeType;
        this.propVal = propVal;
    }

    public String getLikeString() {
        if (likeType.equals(LikeType.PREFIX)) {
            return "%" + prop;
        } else if (likeType.equals(LikeType.PREFIX)) {
            return prop + "%";
        } else {
            return "%" + prop + "%";
        }
    }

    public static void main(String[] args) {
        LikeCondition condition1 = new LikeCondition("userName", LikeType.ALL, "cjh");
        LikeCondition condition2 = new LikeCondition("userName", LikeType.PREFIX, "a");
        LikeCondition condition3 = new LikeCondition("userName", LikeType.SUFFIX, "b");
        System.out.println(condition1.getLikeString());
        System.out.println(condition2.getLikeString());
        System.out.println(condition3.getLikeString());

    }
}

