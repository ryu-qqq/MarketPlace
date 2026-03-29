package com.ryuqq.marketplace.adapter.in.rest.legacy.common.controller;

import com.ryuqq.authhub.sdk.exception.AuthHubException;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyErrorResponse;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** 레거시 패키지 전용 글로벌 예외 처리기. */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.ryuqq.marketplace.adapter.in.rest.legacy")
public class LegacyGlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(LegacyGlobalExceptionHandler.class);
    private final ErrorMapperRegistry errorMapperRegistry;

    public LegacyGlobalExceptionHandler(ErrorMapperRegistry errorMapperRegistry) {
        this.errorMapperRegistry = errorMapperRegistry;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<LegacyErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        String detail = extractValidationMessage(ex.getBindingResult().getFieldErrors());
        log.warn("Legacy validation failed: {}", detail);
        return ResponseEntity.badRequest()
                .body(
                        LegacyErrorResponse.of(
                                HttpStatus.BAD_REQUEST.value(),
                                "Validation failed for request",
                                detail));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<LegacyErrorResponse> handleBindException(BindException ex) {
        String detail = extractValidationMessage(ex.getBindingResult().getFieldErrors());
        log.warn("Legacy binding failed: {}", detail);
        return ResponseEntity.badRequest()
                .body(
                        LegacyErrorResponse.of(
                                HttpStatus.BAD_REQUEST.value(),
                                "Validation failed for request",
                                detail));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<LegacyErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex) {
        String detail =
                ex.getConstraintViolations().stream()
                        .map(v -> v.getMessage())
                        .distinct()
                        .collect(Collectors.joining(", "));
        log.warn("Legacy constraint violation: {}", detail);
        return ResponseEntity.badRequest()
                .body(
                        LegacyErrorResponse.of(
                                HttpStatus.BAD_REQUEST.value(),
                                "Validation failed for request",
                                detail));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<LegacyErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        log.warn("Legacy invalid JSON body", ex);
        return ResponseEntity.badRequest()
                .body(
                        LegacyErrorResponse.of(
                                HttpStatus.BAD_REQUEST.value(),
                                "요청 본문(JSON) 형식이 올바르지 않습니다.",
                                "INVALID_FORMAT"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<LegacyErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        String name = Optional.of(ex.getName()).orElse("unknown");
        Object value = ex.getValue();
        String required =
                ex.getRequiredType() != null
                        ? ex.getRequiredType().getSimpleName()
                        : "required type";
        String msg =
                "파라미터 '%s'의 값 '%s'는 %s 타입으로 변환할 수 없습니다"
                        .formatted(name, String.valueOf(value), required);
        log.warn(
                "Legacy type mismatch: parameter={}, value={}, requiredType={}",
                name,
                value,
                required);
        return ResponseEntity.badRequest()
                .body(LegacyErrorResponse.of(HttpStatus.BAD_REQUEST.value(), msg, "TYPE_MISMATCH"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<LegacyErrorResponse> handleMissingParam(
            MissingServletRequestParameterException ex) {
        String param = Optional.of(ex.getParameterName()).orElse("unknown");
        String msg = "필수 파라미터 '%s'가 누락되었습니다".formatted(param);
        log.warn("Legacy missing parameter: parameter={}, type={}", param, ex.getParameterType());
        return ResponseEntity.badRequest()
                .body(
                        LegacyErrorResponse.of(
                                HttpStatus.BAD_REQUEST.value(), msg, "MISSING_PARAMETER"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<LegacyErrorResponse> handleNoResource(NoResourceFoundException ex) {
        log.debug("Legacy no resource found: path={}", ex.getResourcePath());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        LegacyErrorResponse.of(
                                HttpStatus.NOT_FOUND.value(),
                                "요청한 리소스를 찾을 수 없습니다.",
                                "RESOURCE_NOT_FOUND"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<LegacyErrorResponse> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex) {
        String method = Optional.of(ex.getMethod()).orElse("UNKNOWN");
        Set<HttpMethod> supported =
                Optional.ofNullable(ex.getSupportedHttpMethods()).orElse(Collections.emptySet());
        String supportedStr =
                supported.isEmpty()
                        ? "없음"
                        : supported.stream()
                                .map(HttpMethod::name)
                                .collect(Collectors.joining(", "));
        String message = "%s 메서드는 지원하지 않습니다. 지원되는 메서드: %s".formatted(method, supportedStr);
        log.warn("Legacy method not allowed: method={}, supported={}", method, supportedStr);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(
                        LegacyErrorResponse.of(
                                HttpStatus.METHOD_NOT_ALLOWED.value(),
                                message,
                                "METHOD_NOT_ALLOWED"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<LegacyErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        String detail = Optional.ofNullable(ex.getMessage()).orElse("잘못된 요청입니다.");
        String exceptionClassName = ex.getClass().getSimpleName();
        log.warn("Legacy illegal argument: {}", detail);
        return ResponseEntity.badRequest()
                .body(
                        LegacyErrorResponse.of(
                                HttpStatus.BAD_REQUEST.value(), detail, exceptionClassName));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<LegacyErrorResponse> handleIllegalState(IllegalStateException ex) {
        String detail = Optional.ofNullable(ex.getMessage()).orElse("상태 충돌이 발생했습니다.");
        String exceptionClassName = ex.getClass().getSimpleName();
        log.warn("Legacy illegal state: {}", detail);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        LegacyErrorResponse.of(
                                HttpStatus.CONFLICT.value(), detail, exceptionClassName));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<LegacyErrorResponse> handleNullPointer(NullPointerException ex) {
        log.error("Legacy NullPointerException", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        LegacyErrorResponse.of(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "서버 내부 오류가 발생했습니다.",
                                "INTERNAL_ERROR"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<LegacyErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Legacy access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(
                        LegacyErrorResponse.of(
                                HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다.", "ACCESS_DENIED"));
    }

    @ExceptionHandler(AuthHubException.class)
    public ResponseEntity<LegacyErrorResponse> handleAuthHubException(AuthHubException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String errorCode = "EXTERNAL_AUTH_" + ex.getErrorCode();
        log.warn(
                "Legacy AuthHub API error: code={}, status={}, message={}",
                errorCode,
                ex.getStatusCode(),
                ex.getErrorMessage());
        return ResponseEntity.status(status)
                .body(LegacyErrorResponse.of(status.value(), ex.getErrorMessage(), errorCode));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<LegacyErrorResponse> handleDomainException(
            DomainException ex, HttpServletRequest req, Locale locale) {
        var mapped =
                errorMapperRegistry
                        .map(ex, locale)
                        .orElseGet(() -> errorMapperRegistry.defaultMapping(ex));
        HttpStatus status = mapped.status();
        String detail = Optional.ofNullable(mapped.detail()).orElse("요청 처리에 실패했습니다.");

        if (status.is5xxServerError()) {
            log.error(
                    "Legacy domain exception: code={}, status={}, message={}",
                    ex.code(),
                    status.value(),
                    detail,
                    ex);
        } else if (status == HttpStatus.NOT_FOUND) {
            log.debug("Legacy domain not found: code={}, message={}", ex.code(), detail);
        } else {
            log.warn(
                    "Legacy domain exception: code={}, status={}, message={}",
                    ex.code(),
                    status.value(),
                    detail);
        }

        return ResponseEntity.status(status)
                .body(LegacyErrorResponse.of(status.value(), detail, ex.code()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<LegacyErrorResponse> handleException(Exception ex) {
        log.error("Legacy unexpected exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        LegacyErrorResponse.of(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "서버 오류가 발생했습니다.",
                                "INTERNAL_ERROR"));
    }

    private String extractValidationMessage(Iterable<FieldError> fieldErrors) {
        Map<String, String> messages = new LinkedHashMap<>();
        for (FieldError fieldError : fieldErrors) {
            messages.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return messages.values().stream().collect(Collectors.joining(", "));
    }
}
