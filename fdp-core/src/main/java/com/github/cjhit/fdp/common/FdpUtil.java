package com.github.cjhit.fdp.common;

import com.github.cjhit.fdp.core.PageBean;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 文件名：FdpUtil.java
 * 说明：通用帮助类
 * 作者： 水哥
 * 创建时间：2020-04-24
 *
 */
public class FdpUtil {


    public static boolean isEmpty(List list) {
        return null == list || list.size() == 0;
    }

    public static boolean isNotEmpty(List list) {
        return null != list && list.size() > 0;
    }

    public static boolean isMapEmpty(Map map) {
        return null == map || map.size() == 0;
    }

    public static boolean isMapNotEmpty(Map map) {
        return null != map && map.size() > 0;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 实体字段 =》 数据库字段
     * 如： siteName => site_name
     *
     * @param fieldName
     * @return
     */
    public static String entityFieldToDbField(Class clz, String fieldName) {

        try {
            //Field field = obj.getClass().getDeclaredField(fieldName);//为了防止继承的问题，此处不能这么取
            List<Field> fieldList = getAllFieldList(clz);
            Field field = null;
            for (Field f : fieldList) {
                if (f.getName().equals(fieldName)) {
                    field = f;
                    break;
                }
            }
            if (null == field) {
                throw new FdpException("entityFieldToDbField error : file " + fieldName + " not in " + clz);
            }
            return entityFieldToDbField(field, fieldName);

        } catch (Exception e) {
            throw new FdpException("entityFieldToDbField error:" + e.getMessage(), e);
        }
    }

    /**
     * 实体字段名称 转 是数据库字段名
     *
     * @param field            字段
     * @param fieldName：实体字段名称
     * @return
     */
    public static String entityFieldToDbField(Field field, String fieldName) {
        Column anno = field.getAnnotation(Column.class);
        if (null != anno) {//先判断有没有注解
            return anno.name();
        }
        return camelToUnderLiner(fieldName);
    }


    public static String getTableName(Class clazz) {
        Annotation ano = clazz.getAnnotation(Table.class);
        if (null != ano) {
            return ((Table) ano).name();
        }
        String className = clazz.getSimpleName();
        return camelToUnderLiner(className);
    }

    /**
     * 骆驼命名法转下划线
     *
     * @param src
     * @return
     */
    private static String camelToUnderLiner(String src) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < src.length(); i++) {
            if (isUpperCase(src.charAt(i))) {
                String srt = ("_" + src.charAt(i)).toLowerCase();
                sb.append(srt);
            } else {
                sb.append(src.charAt(i) + "");
            }
        }
        return sb.toString();
    }

    /**
     * 获取指定类的所有字段列表
     *
     * @param clz
     * @return
     */
    public static List<Field> getAllFieldList(Class clz) {
        //备注： 理论上应按照数据库中索引的顺序（即索引注解）排序字段，后续构建sql即可正常使用上索引字段
        // 实际通过mysql explain sql 发现，and条件排序先后顺序不影响索引的实现，因此，此处无需修改
        List<Field> list = new ArrayList<>();
        while (!clz.getName().equals(PageBean.class.getName())
                && !clz.getName().equals(Object.class.getName())) {
            Field[] arr = clz.getDeclaredFields();
            for (Field field : arr) {
                list.add(field);
            }
            clz = clz.getSuperclass();
        }
        return list;
    }

    public static String getOrderByClause(String orderBy, String sortBy, Class clz) {
        if (StringUtils.isBlank(orderBy)) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        if (orderBy.contains(",")) {
            String[] orderByArr = orderBy.split(",");
            String[] sortByArr = null;
            if (StringUtils.isNotBlank(sortBy)) {
                sortByArr = sortBy.split(",");
            } else {
                sortByArr = new String[orderByArr.length];
                for (int i = 0; i < sortByArr.length; i++) {
                    sortByArr[i] = "asc";
                }
            }
            for (int i = 0; i < orderByArr.length; i++) {
                sb.append(entityFieldToDbField(clz, orderByArr[i]) + " " + sortByArr[i] + " ,");
            }
            return sb.substring(0, sb.length() - 1);
        } else {
            if (StringUtils.isBlank(sortBy)) {
                sortBy = "asc";
            }
            return entityFieldToDbField(clz, orderBy) + " " + sortBy + " ";
        }
    }

    /*
     * 是否是大写
     */
    public static boolean isUpperCase(char c) {
        return c >= 65 && c <= 90;
    }

    /*
     * 是否是小写
     */
    public static boolean isLowerCase(char c) {
        return c >= 97 && c <= 122;
    }


    /**
     * 根据属性名获取属性的值
     *
     * @param obj:对象
     * @param attrName:属性名称
     * @return
     */
    public static Object getObjPropVal(Object obj, String attrName) {
        Method getMethod;
        try {
            getMethod = obj.getClass().getMethod(getGetMethodName(attrName));
            return getMethod.invoke(obj);
        } catch (Exception e) {
        }
        return null;
    }

    public static Object setObjPropVal(Object obj, String attrName, Class clz, Object val) {
        Method getMethod;
        try {
            getMethod = obj.getClass().getMethod(getSetMethodName(attrName), clz);
            return getMethod.invoke(obj, val);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 通过字段名称获取该字段对应的get方法名称
     *
     * @return
     */
    public static String getGetMethodName(String fieldName) {
        String firstChar = fieldName.substring(0, 1);
        firstChar = firstChar.toUpperCase();
        return "get" + firstChar + fieldName.substring(1);
    }

    public static String getSetMethodName(String fieldName) {
        String firstChar = fieldName.substring(0, 1);
        firstChar = firstChar.toUpperCase();
        return "set" + firstChar + fieldName.substring(1);
    }

    public static int getRandomNum(int start, int end) {
        int num = (int) (Math.random() * (end - start + 1) + start);
        return num;
    }

    /**
     * 获取指定位数的数字验证码
     *
     * @param len
     * @return
     */
    public static String getNumCaptcha(int len) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            sb.append(getRandomNum(0, 9));
        }
        return sb.toString();
    }


    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(getNumCaptcha(4));
        }
    }


    /**
     * 解析multipart协议，返回指定字段名称的文件列表
     *
     * @param request
     * @param tmpPath:缓存文件存放路径
     * @param formMap：表单map
     * @param fileFieldName：上传文件字段名，如：avatarFile、videoFile等
     * @return
     */
    public static List<FileItem> parseMultipartRequest(javax.servlet.http.HttpServletRequest request, String tmpPath, Map<String, String> formMap, String fileFieldName) throws FileUploadException {
        List<FileItem> fileList = new ArrayList<>();
        DiskFileItemFactory fac = new DiskFileItemFactory(1024, new File(tmpPath));
        ServletFileUpload sfp = new ServletFileUpload(fac);
        List<FileItem> allItemList = sfp.parseRequest(request);
        if (isEmpty(allItemList)) {
            return new ArrayList<>();
        }
        for (FileItem fileItem : allItemList) {
            //System.out.println("===" + fileItem.getContentType());
            if (fileItem.isFormField()) {
                formMap.put(fileItem.getFieldName(), decode(fileItem.getString()));
//                formMap.put(fileItem.getFieldName(), fileItem.getString());
            } else {
                if (null == fileFieldName || fileItem.getFieldName().equals(fileFieldName)) {
                    fileList.add(fileItem);
                }
            }
        }
        return fileList;
    }

    private static String decode(String msg) {
        try {
            //return URLDecoder.decode(msg, "UTF-8");
            return new String(msg.getBytes("ISO8859-1"), "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    public static <T> T copy(T instance) {
        try {
            T newInstace = (T) instance.getClass().newInstance();
            BeanUtils.copyProperties(instance, newInstace);
            return newInstace;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
