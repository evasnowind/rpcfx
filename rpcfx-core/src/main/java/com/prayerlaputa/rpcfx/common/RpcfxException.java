package com.prayerlaputa.rpcfx.common;

import javax.naming.LimitExceededException;

/**
 *
 * 从dubbo抄过来的RpcException类
 *
 * @author chenglong.yu
 * created on 2020/12/13
 */
public class RpcfxException extends RuntimeException {


    public static final int UNKNOWN_EXCEPTION = 0;
    public static final int NETWORK_EXCEPTION = 1;
    public static final int TIMEOUT_EXCEPTION = 2;
    public static final int BIZ_EXCEPTION = 3;
    public static final int FORBIDDEN_EXCEPTION = 4;
    public static final int SERIALIZATION_EXCEPTION = 5;
    public static final int NO_INVOKER_AVAILABLE_AFTER_FILTER = 6;
    public static final int LIMIT_EXCEEDED_EXCEPTION = 7;
    public static final int TIMEOUT_TERMINATE = 8;
    private static final long serialVersionUID = 7815426752583648734L;
    /**
     * RpcException cannot be extended, use error code for exception type to keep compatibility
     */
    private int code;

    public RpcfxException() {
        super();
    }

    public RpcfxException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcfxException(String message) {
        super(message);
    }

    public RpcfxException(Throwable cause) {
        super(cause);
    }

    public RpcfxException(int code) {
        super();
        this.code = code;
    }

    public RpcfxException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public RpcfxException(int code, String message) {
        super(message);
        this.code = code;
    }

    public RpcfxException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isBiz() {
        return code == BIZ_EXCEPTION;
    }

    public boolean isForbidded() {
        return code == FORBIDDEN_EXCEPTION;
    }

    public boolean isTimeout() {
        return code == TIMEOUT_EXCEPTION;
    }

    public boolean isNetwork() {
        return code == NETWORK_EXCEPTION;
    }

    public boolean isSerialization() {
        return code == SERIALIZATION_EXCEPTION;
    }

    public boolean isNoInvokerAvailableAfterFilter() {
        return code == NO_INVOKER_AVAILABLE_AFTER_FILTER;
    }

    public boolean isLimitExceed() {
        return code == LIMIT_EXCEEDED_EXCEPTION || getCause() instanceof LimitExceededException;
    }
}
