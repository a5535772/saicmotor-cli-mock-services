package com.example.leave.common;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常 — {} {} code={} msg={}",
                request.getMethod(), request.getRequestURI(), e.getCode(), e.getMessage());
        return ApiResponse.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleOther(Exception e, HttpServletRequest request) {
        log.error("未处理异常 — {} {} ", request.getMethod(), request.getRequestURI(), e);
        return ResponseEntity.status(500).body(ApiResponse.fail(500, "服务器内部错误"));
    }
}
