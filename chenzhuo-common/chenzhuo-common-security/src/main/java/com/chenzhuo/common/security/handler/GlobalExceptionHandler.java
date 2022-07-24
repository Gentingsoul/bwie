package com.chenzhuo.common.security.handler;

import javax.servlet.http.HttpServletRequest;

import com.chenzhuo.common.core.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.chenzhuo.common.core.constant.HttpStatus;
import com.chenzhuo.common.core.exception.DemoModeException;
import com.chenzhuo.common.core.exception.InnerAuthException;
import com.chenzhuo.common.core.exception.ServiceException;
import com.chenzhuo.common.core.exception.auth.NotPermissionException;
import com.chenzhuo.common.core.exception.auth.NotRoleException;
import com.chenzhuo.common.core.utils.StringUtils;

/**
 * 全局异常处理器
 * 
 * @author chenzhuo
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 权限码异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public Result handleNotPermissionException(NotPermissionException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限码校验失败'{}'", requestURI, e.getMessage());
        return Result.error(HttpStatus.FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 角色权限异常
     */
    @ExceptionHandler(NotRoleException.class)
    public Result handleNotRoleException(NotRoleException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',角色权限校验失败'{}'", requestURI, e.getMessage());
        return Result.error(HttpStatus.FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
            HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return Result.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public Result handleServiceException(ServiceException e, HttpServletRequest request)
    {
        log.error(e.getMessage(), e);
        Integer code = e.getCode();
        return StringUtils.isNotNull(code) ? Result.error(code, e.getMessage()) : Result.error(e.getMessage());
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return Result.error(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return Result.error(e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public Result handleBindException(BindException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return Result.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.error(message);
    }

    /**
     * 内部认证异常
     */
    @ExceptionHandler(InnerAuthException.class)
    public Result handleInnerAuthException(InnerAuthException e)
    {
        return Result.error(e.getMessage());
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public Result handleDemoModeException(DemoModeException e)
    {
        return Result.error("演示模式，不允许操作");
    }
}
