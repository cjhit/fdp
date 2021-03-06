package com.github.cjhit.fdp.config;

import com.github.cjhit.fdp.common.FdpException;
import com.github.cjhit.fdp.core.RestResultBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文件名：FdpDefaultExceptionHandle.java
 * 说明： 框架底层全局异常处理(REST) 仅在配置文件中指明fdp.default.exception.handle = true时才生效
 * 作者： 水哥
 * 创建时间：2020-04-22
 */
@Slf4j
@RestControllerAdvice
//@ConditionalOnExpression("${fdp.default.exception.handle:true}")
@ConditionalOnProperty(prefix = "fdp.default.exception", name = "handle", havingValue = "true", matchIfMissing = false)
public class FdpExceptionHandle {

    @PostConstruct
    private void init() {
        log.info("采用FDP默认异常处理配置：FdpExceptionHandle");
    }

    /**
     * 捕获数据库异常（不显示真实原因到前端界面）
     *
     * @param request http请求
     * @param e       数据库异常
     * @return 处理结果
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object badDataAccessExceptionHandler(HttpServletRequest request, DataAccessException e) {
        String logMsg = String.format("接口 [%s] 返回异常,数据库操作异常: %s", request.getRequestURI(), e.getMessage());
        log.error(logMsg, e);
        String customerMsg = "系统内部异常：数据操作失败，请联系管理员！";
        return RestResultBuilder.failure(HttpStatus.INTERNAL_SERVER_ERROR, customerMsg);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object methodArgumentNotValid(HttpServletRequest request, MethodArgumentNotValidException e) {
        List<FieldError> fieldError = e.getBindingResult().getFieldErrors();
        String errMsg = fieldError.get(0).getDefaultMessage();
        String msg = String.format("接口[%s]参数校验失败, 异常信息: %s", request.getRequestURI(), errMsg);
        log.error(msg);
        return RestResultBuilder.failure(HttpStatus.BAD_REQUEST, errMsg);
    }

    @ExceptionHandler(FdpException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object badRequestExceptionHandler(HttpServletRequest request, FdpException e) {
        String msg = String.format("接口[%s]主动抛出异常, 异常信息: %s", request.getRequestURI(), e.getMessage());
        log.error(msg, e);
        return RestResultBuilder.failure(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object exceptionHandler(HttpServletRequest request, Exception e) {
        String msg = String.format("接口[%s]内部被动异常, 异常信息: %s", request.getRequestURI(), e.getMessage());
        log.error(msg, e);
        return RestResultBuilder.failure(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }


}
