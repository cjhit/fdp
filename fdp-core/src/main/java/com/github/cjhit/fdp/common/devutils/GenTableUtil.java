package com.github.cjhit.fdp.common.devutils;

import com.github.cjhit.fdp.common.FdpException;
import com.github.cjhit.fdp.common.FdpUtil;
import com.github.cjhit.fdp.core.PageBean;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 文件名：CreateTable.java
 * 说明：
 * 作者：水哥
 * 创建时间：2019-03-27
 */
public class GenTableUtil {

    /**
     * 构建建表语句
     *
     * @param clz
     * @param db
     * @param <T>
     * @return
     */
    public static <T extends PageBean> String genCreateTableSql(Class<T> clz, String db) {

        /***
         * CREATE TABLE `biz_device` (
         *   `id` varchar(32) NOT NULL COMMENT '主键',
         *   `name` varchar(64) NOT NULL COMMENT '设备名称',
         *   `sn` varchar(64) NOT NULL COMMENT '设备SN号',
         *   `train_center_id` varchar(32) NOT NULL COMMENT '归属训练中心',
         *   `create_time` varchar(19) NOT NULL COMMENT '创建时间',
         *   PRIMARY KEY (`id`),
         *   UNIQUE KEY `idx_device_sn` (`sn`),
         *   KEY `idx_device_tcid` (`train_center_id`),
         *   KEY `idx_device_name` (`name`)
         * ) ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='设备表';
         *
         * SET FOREIGN_KEY_CHECKS = 1;
         *
         *
         */
//        CREATE TABLE `sys_ope_log` (
//          `id` bigint(18) NOT NULL AUTO_INCREMENT,
//          `ip` varchar(64) NOT NULL,
//          `user_id` varchar(32) DEFAULT NULL,
//          `module` varchar(32) NOT NULL,
//          `operate` varchar(32) NOT NULL,
//          `param` text,
//          `ope_time` varchar(19) NOT NULL,
//          `use_time` bigint(20) NOT NULL,
//                PRIMARY KEY (`id`),
//                KEY `idx_time` (`ope_time`)
//        ) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=gbk;
        List<Field> fieldList = FdpUtil.getAllFieldList(clz);
        String tableName = FdpUtil.getTableName(clz);
        if (!db.equals("mysql")) {
            return null;
        }
        String primaryField = "";
        String primaryFieldType = "";
        for (Field field : fieldList) {
            Transient anno = field.getAnnotation(Transient.class);
            if (null != anno) {
                continue;
            }
            String dbFieldName = FdpUtil.entityFieldToDbField(field, field.getName());
            Id id = field.getAnnotation(Id.class);
            if (null != id) {
                primaryField = dbFieldName;
                primaryFieldType = field.getType().getName();
                break;
            }
        }
        StringBuffer sb = new StringBuffer("CREATE TABLE " + tableName + "( \r\n");
        for (Field field : fieldList) {
            Transient anno = field.getAnnotation(Transient.class);
            if (null != anno) {
                continue;
            }
            String dbFieldName = FdpUtil.entityFieldToDbField(field, field.getName());
            sb.append("`" + dbFieldName + "` ");

            if (field.getType().getName().equals(Long.class.getName())) {
                sb.append(" bigint(18) ");

            } else if (field.getType().getName().equals(Short.class.getName())) {
                sb.append(" smallint(1) ");
            } else if (field.getType().getName().equals(Integer.class.getName())) {
                sb.append(" int(5) ");
            } else if (field.getType().getName().equals(String.class.getName())) {
                sb.append(" varchar(32) ");
            } else if (field.getType().getName().equals(Float.class.getName())) {
                sb.append(" float(5,2) ");
            } else if (field.getType().getName().equals(Double.class.getName())) {
                sb.append(" double(5,2) ");
            } else {
                throw new FdpException("不支持的字段类型：" + field.getType().getName());
            }
            sb.append(" NOT NULL ");
            if (dbFieldName.equals(primaryField)) {
                if (field.getType().equals(Long.class)) {
                    sb.append(" AUTO_INCREMENT ");
                }
            }
            sb.append(", \r\n");
        }
        if (StringUtils.isNotEmpty(primaryField)) {
            sb.append("PRIMARY KEY (`id`) \n");
        }
        if (primaryFieldType.equals(Long.class)) {
            sb.append(") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=gbk;");
        } else {
            sb.append(") ENGINE=InnoDB  DEFAULT CHARSET=gbk;");
        }
        return sb.toString();
    }
}
