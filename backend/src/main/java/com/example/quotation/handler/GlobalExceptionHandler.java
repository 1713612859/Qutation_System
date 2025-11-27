package com.example.quotation.handler;

import com.example.quotation.common.ApiResponse;
import com.example.quotation.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * ✅ 自定义业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public ApiResponse<?> handleServiceException(ServiceException e) {
        return ApiResponse.error(e.getMessage());
    }

    /**
     * ✅ 参数验证失败异常（@Valid/@Validated）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        return ApiResponse.error(msg);
    }

    /**
     * ✅ 通用运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<?> handleRuntimeException(RuntimeException e) {
        e.printStackTrace();
        log.error("系统运行错误：{}", e.getMessage());
        return ApiResponse.error("System error：" + e.getMessage());
    }

    /**
     * ✅ 未捕获异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e) {
        e.printStackTrace();
        return ApiResponse.error("Intern server error：" + e.getMessage());
    }
}
