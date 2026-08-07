package com.mai.common.advice;

import com.mai.common.domain.R;
import com.mai.common.exception.BadRequestException;
import com.mai.common.exception.CommonException;
import com.mai.common.exception.DbException;
import com.mai.common.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.NestedServletException;

import java.net.BindException;
import java.util.stream.Collectors;

/**
 * <p>
 * 全局异常处理器，统一拦截各类异常并返回标准化的R响应体
 * </p>
 */
@RestControllerAdvice
@Slf4j
public class CommonExceptionAdvice {

    /**
     * <p>
     * 处理数据库操作异常，记录日志并返回标准化错误响应
     * </p>
     *
     * @param e 数据库异常
     * @return 标准化错误响应实体
     */
    @ExceptionHandler(DbException.class)
    public Object handleDbException(DbException e) {
        log.error("mysql数据库操作异常 -> ", e);
        return processResponse(e);
    }

    /**
     * <p>
     * 处理自定义通用异常，记录异常类型和消息
     * </p>
     *
     * @param e 自定义通用异常
     * @return 标准化错误响应实体
     */
    @ExceptionHandler(CommonException.class)
    public Object handleBadRequestException(CommonException e) {
        log.error("自定义异常 -> {} , 异常原因：{}  ",e.getClass().getName(), e.getMessage());
        log.debug("", e);
        return processResponse(e);
    }

    /**
     * <p>
     * 处理请求参数校验异常，提取所有校验错误信息并拼接返回
     * </p>
     *
     * @param e 方法参数校验异常
     * @return 标准化错误响应实体
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getAllErrors()
                .stream().map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("|"));
        log.error("请求参数校验异常 -> {}", msg);
        log.debug("", e);
        return processResponse(new BadRequestException(msg));
    }

    /**
     * <p>
     * 处理请求参数绑定异常，返回参数格式错误提示
     * </p>
     *
     * @param e 绑定异常
     * @return 标准化错误响应实体
     */
    @ExceptionHandler(BindException.class)
    public Object handleBindException(BindException e) {
        log.error("请求参数绑定异常 ->BindException， {}", e.getMessage());
        log.debug("", e);
        return processResponse(new BadRequestException("请求参数格式错误"));
    }

    /**
     * <p>
     * 处理Servlet嵌套异常，返回请求参数处理异常提示
     * </p>
     *
     * @param e Servlet嵌套异常
     * @return 标准化错误响应实体
     */
    @ExceptionHandler(NestedServletException.class)
    public Object handleNestedServletException(NestedServletException e) {
        log.error("参数异常 -> NestedServletException，{}", e.getMessage());
        log.debug("", e);
        return processResponse(new BadRequestException("请求参数处理异常"));
    }

    /**
     * <p>
     * 兜底异常处理器，捕获所有未处理的异常并返回服务器内部错误
     * </p>
     *
     * @param e 未分类的异常
     * @return 标准化错误响应实体
     */
    @ExceptionHandler(Exception.class)
    public Object handleRuntimeException(Exception e) {
        log.error("其他异常 uri : {} -> ", WebUtils.getRequest().getRequestURI(), e);
        return processResponse(new CommonException("服务器内部异常", 500));
    }

    private ResponseEntity<R<Void>> processResponse(CommonException e){
        return ResponseEntity.status(e.getCode()).body(R.error(e));
    }
}