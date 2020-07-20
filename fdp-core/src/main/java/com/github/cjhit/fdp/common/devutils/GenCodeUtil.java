package com.github.cjhit.fdp.common.devutils;

import com.github.cjhit.fdp.common.FdpException;
import com.github.cjhit.fdp.common.FdpUtil;
import com.github.cjhit.fdp.core.PageBean;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Id;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class GenCodeUtil {

    private static GenCodeConfig getDefaultConfig() {
        GenCodeConfig config = new GenCodeConfig();
        config.setControllerPackageName("controller");
        config.setEntityPackageName("entity");
        config.setServciePageageName("service");
        config.setDaoPackageName("dao");
        return config;
    }

    public static <T extends PageBean> void gen(Class<T> clz) {
        gen(clz, getDefaultConfig());
    }

    /**
     * 自动生成代码
     * 定义：
     * className:完整路径名  如com.fdp.common.entity.sys.User
     * pageageName:包名  如com.fdp.common.entity.sys
     * simpleName: User
     * pageageName  + "." + simpleName = className
     *
     * @param clz    实体
     * @param config 配置
     * @param <T>    实体
     */
    public static <T extends PageBean> void gen(Class<T> clz, GenCodeConfig config) {
        String entityClassName = clz.getName();
        //需符合 x.y.z.entity.xx.Test 如：com.fdp.common.entity.sys.User
        if (!entityClassName.contains(config.getEntityPackageName())) {
            throw new FdpException("实体所在报名不符合配置项中指定的实体包名：" + config.getEntityPackageName());
        }
        //获取主键类型
        String idType = getIdType(clz);

        int index = entityClassName.lastIndexOf(".");
        String entitySimpleName = entityClassName.substring(index + 1);
        String entityPackageName = entityClassName.substring(0, index);
        log.info("entityClassName:" + entityClassName + ";simpleName:" + entitySimpleName + ";entityPackageName:" + entityPackageName);

        //获取实体文件所在绝对位置 /D:/gitPlace/fdp/fdp-example/target/test-classes/ 或 D:\gitPlace\fdp\fdp-example\target\classes/
        String entityClassPath = clz.getResource("/").getPath();
        String entitySrcPath = getEntitySrcPath(entityClassPath, entityPackageName);
        log.info("实体源码路径：" + entitySrcPath + "；实体编译后路径：" + entityClassPath);
        genService(idType, config, entitySimpleName, entityPackageName, entityClassName, entitySrcPath);
        genDao(config, entitySimpleName, entityPackageName, entityClassName, entitySrcPath);
        log.info("生成成功！");
    }

    /**
     * 根据实体编译后的路径获取实体源码路径
     *
     * @param entityClassPath   实体路径
     * @param entityPackageName 实体包名
     * @return 实体源码路径
     */
    private static String getEntitySrcPath(String entityClassPath, String entityPackageName) {
        ///D:/gitPlace/fdp/fdp-example/target/test-classes/ 或 D:\gitPlace\fdp\fdp-example\target\classes/
        String key = "target";
        int index = entityClassPath.indexOf("target");
        String projectPath = entityClassPath.substring(0, index);
        String srcPath = projectPath;
        if (entityClassPath.contains("test-classes")) {
            srcPath = srcPath + "src/test/java/";
        } else {
            srcPath = srcPath + "src/main/java/";
        }
        entityPackageName = entityPackageName.replaceAll("\\.", "/");
        srcPath += entityPackageName;
        return srcPath;
    }

    private static <T extends PageBean> String genService(String idType, GenCodeConfig config, String entitySimpleName, String entityPackageName, String entityClassName, String entitySrcPath) {

        String key = "." + config.getEntityPackageName();
        int index = entityClassName.indexOf(key);
        String prefix = entityPackageName.substring(0, index);
        String suffix = entityPackageName.substring(key.length() + index);

        String serviceSimpleName = entitySimpleName + upperCaseFirstChar(config.getServciePageageName());
        String servicePackageName = StringUtils.isEmpty(suffix) ? prefix + "." + config.getServciePageageName() : prefix + "." + config.getServciePageageName() + "." + suffix;
        String daoSimpleName = entitySimpleName + upperCaseFirstChar(config.getDaoPackageName());
        String daoPackageName = StringUtils.isEmpty(suffix) ? prefix + "." + config.getDaoPackageName() : prefix + "." + config.getDaoPackageName() + "." + suffix;
        String daoClassName = daoPackageName + "." + daoSimpleName;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("daoSimpleName", daoSimpleName);
        paramMap.put("daoPackageName", daoPackageName);
        paramMap.put("daoClassName", daoClassName);
        paramMap.put("serviceSimpleName", serviceSimpleName);
        paramMap.put("servicePackageName", servicePackageName);
        paramMap.put("entityClassName", entityClassName);
        paramMap.put("entitySimpleName", entitySimpleName);
        paramMap.put("owner", config.getOwner());
        paramMap.put("author", config.getAuthor());
        paramMap.put("createDate", getCreateDate());
        paramMap.put("idType", idType);
        Template daoTemplate = getTemplate("service.ftl");

        String destPath = entitySrcPath.replaceAll("\\/" + config.getEntityPackageName(), "/" + config.getServciePageageName());
        mkdirs(destPath);
        String finalPath = destPath + "/" + serviceSimpleName + ".java";
        log.info("service文件位置：" + finalPath);
        createFile(daoTemplate, paramMap, finalPath);
        return finalPath;
    }


    private static String genDao(GenCodeConfig config, String entitySimpleName, String entityPackageName, String entityClassName, String entitySrcPath) {
        String key = "." + config.getEntityPackageName();
        int index = entityClassName.indexOf(key);
        String prefix = entityPackageName.substring(0, index);
        String suffix = entityPackageName.substring(key.length() + index);

        String daoSimpleName = entitySimpleName + upperCaseFirstChar(config.getDaoPackageName());
        String daoPackageName = StringUtils.isEmpty(suffix) ? prefix + "." + config.getDaoPackageName() : prefix + "." + config.getDaoPackageName() + "." + suffix;
//        String daoClassName = daoPackageName + "." + daoSimpleName;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("daoPackageName", daoPackageName);
        paramMap.put("entityClassName", entityClassName);
        paramMap.put("daoSimpleName", daoSimpleName);
        paramMap.put("entitySimpleName", entitySimpleName);
        paramMap.put("owner", config.getOwner());
        paramMap.put("author", config.getAuthor());
        paramMap.put("createDate", getCreateDate());
        Template daoTemplate = getTemplate("dao.ftl");

        String destPath = entitySrcPath.replaceAll("\\/" + config.getEntityPackageName(), "/" + config.getDaoPackageName());
        mkdirs(destPath);
        String finalPath = destPath + "/" + daoSimpleName + ".java";
        log.info("dao文件位置：" + finalPath);
        createFile(daoTemplate, paramMap, finalPath);
        return finalPath;
    }

    private static <T extends PageBean> String getIdType(Class<T> clz) {
        List<Field> fieldList = FdpUtil.getAllFieldList(clz);
        for (Field field : fieldList) {
            Annotation annotation = field.getAnnotation(Id.class);
            if (null != annotation) {
                return field.getType().getSimpleName();
            }
        }
        return "UNKNOWN";
    }

    public static void mkdirs(String filePath) {
        File file = new File(filePath);
        file.mkdirs();
    }

    private static void createFile(Template template, Map<String, Object> paramMap, String dest) {
        File file = new File(dest);
        try {
            FileWriter fw = new FileWriter(file);
            template.process(paramMap, fw);
        } catch (Exception e) {
            log.error("生成文件失败！" + e.getMessage(), e.getMessage());
            throw new FdpException("生成文件失败：" + e.getMessage());
        }
    }

    private static Template getTemplate(String name) {
        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(GenCodeUtil.class, "/devutils/templates");
        try {
            Template temp = cfg.getTemplate(name);
            return temp;
        } catch (IOException e) {
            throw new FdpException("模板文件不存在：" + name);
        }
    }


    private static String getCreateDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDateTime.now().format(formatter);
    }

    private static String upperCaseFirstChar(String str) {
        if (str.length() > 1) {
            return (str.charAt(0) + "").toUpperCase() + str.substring(1);
        }
        return str.toUpperCase();
    }

}
