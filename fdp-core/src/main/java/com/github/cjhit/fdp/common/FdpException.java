package com.github.cjhit.fdp.common;

/**
 * 文件名：FdpException.java
 * 说明：框架底层异常
 * 作者： 水哥
 * 创建时间：2020-04-24
 *
 */
public class FdpException extends RuntimeException {
    public FdpException() {
        super();
    }

    public FdpException(String message) {
        super(message);
    }

    public FdpException(String message, Throwable cause) {
        super(message, cause);
    }

    public FdpException(Throwable cause) {
        super(cause);
    }

    protected FdpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
