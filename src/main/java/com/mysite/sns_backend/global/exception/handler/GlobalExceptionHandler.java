package com.mysite.sns_backend.global.exception.handler;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.mysite.sns_backend.common.response.ApiErrorResponse;
import com.mysite.sns_backend.global.exception.CustomException;
import com.mysite.sns_backend.global.exception.code.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 커스텀 예외 처리 (도메인/비즈니스 로직 기반 예외)
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiErrorResponse> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
            .body(ApiErrorResponse.of(errorCode));
    }

    /**
     * 유효성 검증 실패 처리
     * - 필드 순서 + 메시지 기준 정렬
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .sorted(
                Comparator.comparing(FieldError::getField)
                    .thenComparing(error -> Optional.ofNullable(error.getDefaultMessage()).orElse(""))
            )
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .toList();

        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getStatus())
            .body(ApiErrorResponse.of(ErrorCode.VALIDATION_FAILED, messages));
    }

    /**
     * 리소스가 존재하지 않을 때 처리
     * - NoResourceFoundException 예외 처리
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error("Resource Not Found: {}", e.getMessage());
        return ResponseEntity.status(ErrorCode.NOT_FOUND.getStatus())
            .body(ApiErrorResponse.of(ErrorCode.NOT_FOUND));
    }

    /**
     * 알 수 없는 서버 예외 처리
     * - 개발 중 디버깅용 로그 포함
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception e) {
        log.error("Exception Occurred: {}", e.getMessage(), e); // 전체 스택트레이스 포함 로그
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
            .body(ApiErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
