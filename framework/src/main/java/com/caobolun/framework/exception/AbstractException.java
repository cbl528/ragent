package com.caobolun.framework.exception;

import com.caobolun.framework.errorcode.IErrorCode;
import lombok.Getter;

/**
 * 抽象异常类
 */
@Getter
public abstract class AbstractException extends RuntimeException {

    public final String errorCode;

    public final String errorMessage;

    public AbstractException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable);
        this.errorCode = errorCode.code();
        this.errorMessage = errorCode.message();
    }
}
