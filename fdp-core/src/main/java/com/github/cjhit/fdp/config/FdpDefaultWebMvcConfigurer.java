package com.github.cjhit.fdp.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 文件名：RoleDao.java
 * 说明：默认web mvc 配置
 * spring5+ 建议使用WebMvcConfigurationSupport
 * 作者： 水哥
 * 创建时间：2020-04-13
 *
 */
@Slf4j
@Configuration
//@ConditionalOnExpression("${fdp.default.mvc.configurer:true}")
@ConditionalOnProperty(prefix = "fdp.default.mvc", name = "configurer", havingValue = "true", matchIfMissing = false)
public class FdpDefaultWebMvcConfigurer extends WebMvcConfigurationSupport {
    /**
     * 跨域支持
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .maxAge(3600 * 24);
    }

    /**
     * 添加静态资源--过滤swagger-api (开源的在线API文档)
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/public/");
        super.addResourceHandlers(registry);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("初始化消息解析器...");
        //1、定义一个convert转换消息的对象
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        //2、添加fastjson的配置信息
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,//null值不反悔
                SerializerFeature.WriteNullStringAsEmpty,//空字符串返回""
                SerializerFeature.DisableCircularReferenceDetect,//返回循环引用
                SerializerFeature.WriteNullListAsEmpty,//空集合返回[]
                SerializerFeature.WriteDateUseDateFormat);
        //3、在convert中添加配置信息
        fastConverter.setFastJsonConfig(fastJsonConfig);
        fastConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
        //4、将convert添加到converters中
        converters.add(fastConverter);
        //5、追加默认转换器
        super.addDefaultHttpMessageConverters(converters);
        log.info("消息解析器设置完成");
    }

    /**
     * 增加拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new FdpDefaultInterceptor()).addPathPatterns("/**");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        log.info("设置默认首页");
        registry.addViewController("/").setViewName("forward:index.html");
    }
}

