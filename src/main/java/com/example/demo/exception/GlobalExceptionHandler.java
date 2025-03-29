package com.example.demo.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局異常處理器
 * 處理應用程序中的各種異常並返回適當的錯誤響應
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 處理數據完整性違規異常
     * 當數據庫操作違反約束條件時觸發
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        
        String errorMessage = ex.getMostSpecificCause().getMessage();
        body.put("message", "數據驗證錯誤");
        body.put("details", errorMessage);
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 處理所有其他未捕獲的異常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        
        body.put("message", "發生錯誤");
        body.put("details", ex.getMessage());
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
