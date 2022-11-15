package com.kkb.common.exception;


import com.kkb.common.core.KkbResponse;
import com.kkb.common.core.exception.KkbBusinessException;
import com.kkb.common.core.exception.KkbStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * 处理全局异常 <br/>
 *
 * @author twb
 */
@RestControllerAdvice
public class GlobalExceptionControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionControllerAdvice.class);

    @ExceptionHandler({IOException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public KkbResponse<Void> processException(NativeWebRequest request, IOException e) {
        logger.error("拦截异常", e);
        return new KkbResponse<>(KkbStatus.FAILURE.getCode(),"IOException，请稍后重试");
    }

    @ExceptionHandler({BindException.class})
    @ResponseStatus(HttpStatus.OK)
    public KkbResponse<Void> processException(NativeWebRequest request, BindException e) {
        logger.error("拦截异常", e);
        BindingResult result = e.getBindingResult();
        List<FieldError> list = result.getFieldErrors();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            FieldError error = list.get(i);
            builder.append(error.getField()).append(":").append(error.getDefaultMessage()).append(";");
        }
        return new KkbResponse<>(KkbStatus.FAILURE.getCode(),builder.toString());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    public KkbResponse<Void> processException(NativeWebRequest request, MethodArgumentNotValidException e) {
        logger.error("拦截异常", e);
        BindingResult result = e.getBindingResult();
        List<FieldError> list = result.getFieldErrors();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            FieldError error = list.get(i);
            builder.append(error.getField()).append(":").append(error.getDefaultMessage()).append(";");
        }
        return new KkbResponse<>(KkbStatus.FAILURE.getCode(),builder.toString());
    }


    @ExceptionHandler(ConversionNotSupportedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public KkbResponse<Void> processException(NativeWebRequest request, ConversionNotSupportedException e) {
        logger.error("拦截异常", e);
        return KkbResponse.failure();
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public KkbResponse<Void> processException(NativeWebRequest request, HttpMediaTypeNotAcceptableException e) {
        logger.error("拦截异常", e);
        return new KkbResponse<>(KkbStatus.FAILURE.getCode(),e.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public KkbResponse<Void> processException(NativeWebRequest request, HttpMediaTypeNotSupportedException e) {
        logger.error("拦截异常", e);
        return new KkbResponse<>(KkbStatus.FAILURE.getCode(),e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public KkbResponse<Void> processException(NativeWebRequest request, HttpMessageNotReadableException e) {
        logger.error("拦截异常", e);
        return new KkbResponse<>(KkbStatus.FAILURE.getCode(),e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public KkbResponse<Void> processException(NativeWebRequest request, HttpMessageNotWritableException e) {
        logger.error("拦截异常", e);
        return new KkbResponse<>(KkbStatus.FAILURE.getCode(),e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public KkbResponse<Void> processException(NativeWebRequest request, HttpRequestMethodNotSupportedException e) {
        logger.error("拦截异常", e);
        return new KkbResponse<>(KkbStatus.FAILURE.getCode(),e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public KkbResponse<Void> processException(NativeWebRequest request, MissingServletRequestParameterException e) {
        logger.error("拦截异常", e);
        return new KkbResponse<>(KkbStatus.FAILURE.getCode(),e.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public KkbResponse<Void> processException(SQLException e) {
        logger.error("拦截异常", e);
        return KkbResponse.failure();
    }

    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public KkbResponse<Void> processException(NativeWebRequest request, TypeMismatchException e) {
        logger.error("拦截异常", e);
        return new KkbResponse<>(KkbStatus.FAILURE.getCode(),e.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.OK)
    public KkbResponse<Void> processException(NativeWebRequest request, DuplicateKeyException e) {
        logger.error("拦截异常", e);
        return new KkbResponse<>(KkbStatus.FAILURE.getCode(),"主键/关键字重复");
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.OK)
    public KkbResponse<Void> processException(NativeWebRequest request, RuntimeException e) {
        logger.error("拦截异常", e);
        return KkbResponse.failure();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public KkbResponse<Void> processException(NativeWebRequest request, Exception e) {
        logger.error("拦截异常", e);
        return KkbResponse.failure();
    }


    /**
     * 业务返回异常处理
     *
     * @param exception
     * @return
     */
    @ExceptionHandler({KkbBusinessException.class})
    @ResponseBody
    public KkbResponse<?> handle(RuntimeException exception) {
        if (exception instanceof KkbBusinessException) {
            KkbBusinessException re = (KkbBusinessException) exception;
            int code = re.getCode();
            String msg = re.getMsg();
            Object data = re.getData();
            Throwable error = re.getError();
            if (null != error) {
                logger.error("global exception handler - {}", error.getMessage(), error);
            }
            logger.info("business exception for response. msg: {}", msg);
            return KkbResponse.of(code, msg, data);
        }
        throw exception;
    }

}
