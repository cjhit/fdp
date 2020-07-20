package com.github.cjhit.fdp.config;

import com.alibaba.fastjson.JSON;
import com.github.cjhit.fdp.core.RestResult;
import com.github.cjhit.fdp.core.RestResultBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 文件名：FdpAuthInterceptor.java
 * 说明：默认的权限拦截器
 * 作者： 水哥
 * 创建时间：2020-04-19
 */
@Slf4j
public class FdpAuthInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        if (StringUtils.isEmpty(token)) {
            String message = String.format("HTTP请求头中未找到鉴权信息");
            RestResult failureResult = RestResultBuilder.failure(HttpStatus.UNAUTHORIZED, message);
            responseResult(response, HttpStatus.UNAUTHORIZED.value(), failureResult);
            return false;
        }
        return checkPermission(token, request.getRequestURI(), response);
    }

    /**
     * 判断当前token是否有访问url的权限
     *
     * @param token      鉴权token
     * @param requestUri 请求访问的url
     * @param response   http响应
     * @return 权限验证结果
     */
    protected boolean checkPermission(String token, String requestUri, HttpServletResponse response) {
        return true;
    }


    /**
     * 从http请求中获取用户token
     *
     * @param request http请求
     * @return 用户标识
     */
    protected String getToken(HttpServletRequest request) {
        return null;
    }

    protected void responseResult(HttpServletResponse response, int status, Object result) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(status);
        try {
            response.getWriter().write(JSON.toJSONString(result));
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //log.debug("Interceptor postHandler method is running !");
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //log.debug("Interceptor afterCompletion method is running !");
        super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //log.debug("Interceptor afterConcurrentHandlingStarted method is running !");
        super.afterConcurrentHandlingStarted(request, response, handler);
    }


}
