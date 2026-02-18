package com.ryuqq.marketplace.adapter.in.rest.common.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ryuqq.authhub.sdk.exception.AuthHubException;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Tag("unit")
@DisplayName("GlobalExceptionHandler 단위 테스트")
class GlobalExceptionHandlerTest {

    private ErrorMapperRegistry errorMapperRegistry;
    private GlobalExceptionHandler sut;
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        errorMapperRegistry = mock(ErrorMapperRegistry.class);
        sut = new GlobalExceptionHandler(errorMapperRegistry);
        mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/api/v1/test");
    }

    @Nested
    @DisplayName("400 - Validation 예외 처리")
    class ValidationExceptionTest {

        @Test
        @DisplayName("MethodArgumentNotValidException을 400으로 처리한다")
        void handleValidationException_Returns400() {
            // given
            BeanPropertyBindingResult bindingResult =
                    new BeanPropertyBindingResult(new Object(), "request");
            bindingResult.addError(new FieldError("request", "name", "must not be blank"));

            MethodArgumentNotValidException ex =
                    new MethodArgumentNotValidException(null, bindingResult);

            // when
            ResponseEntity<ProblemDetail> response = sut.handleValidationException(ex, mockRequest);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
            assertThat(response.getHeaders().getFirst("x-error-code"))
                    .isEqualTo("VALIDATION_FAILED");
        }

        @Test
        @DisplayName("ConstraintViolationException을 400으로 처리한다")
        void handleConstraintViolation_Returns400() {
            // given
            @SuppressWarnings("unchecked")
            ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
            Path path = mock(Path.class);
            when(path.toString()).thenReturn("id");
            when(violation.getPropertyPath()).thenReturn(path);
            when(violation.getMessage()).thenReturn("must be positive");
            ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

            // when
            ResponseEntity<ProblemDetail> response = sut.handleConstraintViolation(ex, mockRequest);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders().getFirst("x-error-code"))
                    .isEqualTo("CONSTRAINT_VIOLATION");
        }
    }

    @Nested
    @DisplayName("400 - 기타 Bad Request 예외 처리")
    class BadRequestExceptionTest {

        @Test
        @DisplayName("IllegalArgumentException을 400으로 처리한다")
        void handleIllegalArgument_Returns400() {
            // given
            IllegalArgumentException ex = new IllegalArgumentException("Invalid value");

            // when
            ResponseEntity<ProblemDetail> response =
                    sut.handleIllegalArgumentException(ex, mockRequest);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getDetail()).isEqualTo("Invalid value");
            assertThat(response.getHeaders().getFirst("x-error-code"))
                    .isEqualTo("INVALID_ARGUMENT");
        }

        @Test
        @DisplayName("IllegalArgumentException 메시지가 null이면 기본 메시지를 사용한다")
        void handleIllegalArgument_NullMessage_UsesDefault() {
            // given
            IllegalArgumentException ex = new IllegalArgumentException((String) null);

            // when
            ResponseEntity<ProblemDetail> response =
                    sut.handleIllegalArgumentException(ex, mockRequest);

            // then
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getDetail()).isEqualTo("Invalid argument");
        }

        @Test
        @DisplayName("HttpMessageNotReadableException을 400으로 처리한다")
        void handleHttpMessageNotReadable_Returns400() {
            // given
            HttpMessageNotReadableException ex =
                    new HttpMessageNotReadableException(
                            "JSON parse error", new MockHttpInputMessage(new byte[0]));

            // when
            ResponseEntity<ProblemDetail> response =
                    sut.handleHttpMessageNotReadable(ex, mockRequest);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders().getFirst("x-error-code")).isEqualTo("INVALID_FORMAT");
        }

        @Test
        @DisplayName("MethodArgumentTypeMismatchException을 400으로 처리한다")
        void handleTypeMismatch_Returns400() {
            // given
            MethodArgumentTypeMismatchException ex =
                    new MethodArgumentTypeMismatchException(
                            "abc", Long.class, "id", null, new NumberFormatException());

            // when
            ResponseEntity<ProblemDetail> response = sut.handleTypeMismatch(ex, mockRequest);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getDetail()).contains("id", "abc", "Long");
            assertThat(response.getHeaders().getFirst("x-error-code")).isEqualTo("TYPE_MISMATCH");
        }

        @Test
        @DisplayName("MissingServletRequestParameterException을 400으로 처리한다")
        void handleMissingParam_Returns400() {
            // given
            MissingServletRequestParameterException ex =
                    new MissingServletRequestParameterException("page", "int");

            // when
            ResponseEntity<ProblemDetail> response = sut.handleMissingParam(ex, mockRequest);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getDetail()).contains("page");
            assertThat(response.getHeaders().getFirst("x-error-code"))
                    .isEqualTo("MISSING_PARAMETER");
        }
    }

    @Nested
    @DisplayName("404 - 리소스 없음")
    class NotFoundExceptionTest {

        @Test
        @DisplayName("NoResourceFoundException을 404로 처리한다")
        void handleNoResource_Returns404() {
            // given
            NoResourceFoundException ex =
                    new NoResourceFoundException(HttpMethod.GET, "/api/v1/nonexistent");

            // when
            ResponseEntity<ProblemDetail> response = sut.handleNoResource(ex, mockRequest);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getHeaders().getFirst("x-error-code"))
                    .isEqualTo("RESOURCE_NOT_FOUND");
        }
    }

    @Nested
    @DisplayName("405 - 메서드 미지원")
    class MethodNotAllowedTest {

        @Test
        @DisplayName("HttpRequestMethodNotSupportedException을 405로 처리한다")
        void handleMethodNotAllowed_Returns405() {
            // given
            HttpRequestMethodNotSupportedException ex =
                    new HttpRequestMethodNotSupportedException("DELETE", List.of("GET", "POST"));

            // when
            ResponseEntity<ProblemDetail> response = sut.handleMethodNotAllowed(ex, mockRequest);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getDetail()).contains("DELETE");
        }

        @Test
        @DisplayName("supportedMethods가 null이어도 정상 처리한다")
        void handleMethodNotAllowed_NullSupported_Returns405() {
            // given
            HttpRequestMethodNotSupportedException ex =
                    new HttpRequestMethodNotSupportedException("PATCH");

            // when
            ResponseEntity<ProblemDetail> response = sut.handleMethodNotAllowed(ex, mockRequest);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @Nested
    @DisplayName("403 - 접근 거부")
    class AccessDeniedTest {

        @Test
        @DisplayName("AccessDeniedException을 403으로 처리한다")
        void handleAccessDenied_Returns403() {
            // given
            AccessDeniedException ex = new AccessDeniedException("Access is denied");

            // when
            ResponseEntity<ProblemDetail> response = sut.handleAccessDenied(ex, mockRequest);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getHeaders().getFirst("x-error-code")).isEqualTo("ACCESS_DENIED");
        }
    }

    @Nested
    @DisplayName("409 - 상태 충돌")
    class StateConflictTest {

        @Test
        @DisplayName("IllegalStateException을 409로 처리한다")
        void handleIllegalState_Returns409() {
            // given
            IllegalStateException ex = new IllegalStateException("Already processed");

            // when
            ResponseEntity<ProblemDetail> response = sut.handleIllegalState(ex, mockRequest);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getDetail()).isEqualTo("Already processed");
            assertThat(response.getHeaders().getFirst("x-error-code")).isEqualTo("STATE_CONFLICT");
        }

        @Test
        @DisplayName("IllegalStateException 메시지가 null이면 기본 메시지를 사용한다")
        void handleIllegalState_NullMessage_UsesDefault() {
            // given
            IllegalStateException ex = new IllegalStateException((String) null);

            // when
            ResponseEntity<ProblemDetail> response = sut.handleIllegalState(ex, mockRequest);

            // then
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getDetail()).isEqualTo("State conflict");
        }
    }

    @Nested
    @DisplayName("AuthHub 예외 처리")
    class AuthHubExceptionTest {

        @Test
        @DisplayName("AuthHubException을 외부 인증 에러로 처리한다")
        void handleAuthHubException_ReturnsExternalAuthError() {
            // given
            AuthHubException ex = new AuthHubException(401, "AUTH_EXPIRED", "Token expired");

            // when
            ResponseEntity<ProblemDetail> response = sut.handleAuthHubException(ex, mockRequest);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getHeaders().getFirst("x-error-code"))
                    .isEqualTo("EXTERNAL_AUTH_AUTH_EXPIRED");
        }

        @Test
        @DisplayName("알 수 없는 HTTP 상태 코드면 500으로 처리한다")
        void handleAuthHubException_UnknownStatus_Returns500() {
            // given
            AuthHubException ex = new AuthHubException(999, "UNKNOWN", "Unknown error");

            // when
            ResponseEntity<ProblemDetail> response = sut.handleAuthHubException(ex, mockRequest);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Nested
    @DisplayName("500 - 내부 서버 오류")
    class InternalErrorTest {

        @Test
        @DisplayName("예상치 못한 Exception을 500으로 처리한다")
        void handleGlobal_Returns500() {
            // given
            Exception ex = new RuntimeException("Unexpected error");

            // when
            ResponseEntity<ProblemDetail> response = sut.handleGlobal(ex, mockRequest);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getHeaders().getFirst("x-error-code")).isEqualTo("INTERNAL_ERROR");
        }
    }

    @Nested
    @DisplayName("DomainException 처리")
    class DomainExceptionTest {

        @Test
        @DisplayName("매칭 매퍼가 있으면 매핑된 응답을 반환한다")
        void handleDomain_WithMapper_ReturnsMappedResponse() {
            // given
            DomainException ex = createDomainException("TEST-001", 404, "Not found");
            ErrorMapper.MappedError mapped =
                    new ErrorMapper.MappedError(
                            HttpStatus.NOT_FOUND,
                            "Test Not Found",
                            "Not found",
                            URI.create("/errors/test/test-001"));
            when(errorMapperRegistry.map(eq(ex), any(Locale.class)))
                    .thenReturn(Optional.of(mapped));

            // when
            ResponseEntity<ProblemDetail> response =
                    sut.handleDomain(ex, mockRequest, Locale.KOREA);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTitle()).isEqualTo("Test Not Found");
            assertThat(response.getBody().getType()).isEqualTo(URI.create("/errors/test/test-001"));
        }

        @Test
        @DisplayName("매칭 매퍼가 없으면 기본 매핑을 사용한다")
        void handleDomain_WithoutMapper_UsesDefault() {
            // given
            DomainException ex = createDomainException("TEST-001", 400, "Bad request");
            ErrorMapper.MappedError defaultMapped =
                    new ErrorMapper.MappedError(
                            HttpStatus.BAD_REQUEST,
                            "Bad Request",
                            "Bad request",
                            URI.create("about:blank"));
            when(errorMapperRegistry.map(eq(ex), any(Locale.class))).thenReturn(Optional.empty());
            when(errorMapperRegistry.defaultMapping(ex)).thenReturn(defaultMapped);

            // when
            ResponseEntity<ProblemDetail> response =
                    sut.handleDomain(ex, mockRequest, Locale.KOREA);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("DomainException args가 있으면 응답에 포함한다")
        void handleDomain_WithArgs_IncludesArgs() {
            // given
            DomainException ex =
                    new DomainException(
                            new ErrorCode() {
                                @Override
                                public String getCode() {
                                    return "TEST-001";
                                }

                                @Override
                                public int getHttpStatus() {
                                    return 404;
                                }

                                @Override
                                public String getMessage() {
                                    return "Not found";
                                }
                            },
                            "Not found",
                            Map.of("entityId", 42L)) {};

            ErrorMapper.MappedError mapped =
                    new ErrorMapper.MappedError(
                            HttpStatus.NOT_FOUND,
                            "Test Not Found",
                            "Not found",
                            URI.create("/errors/test"));
            when(errorMapperRegistry.map(eq(ex), any(Locale.class)))
                    .thenReturn(Optional.of(mapped));

            // when
            ResponseEntity<ProblemDetail> response =
                    sut.handleDomain(ex, mockRequest, Locale.KOREA);

            // then
            assertThat(response.getBody()).isNotNull();
            @SuppressWarnings("unchecked")
            Map<String, Object> args =
                    (Map<String, Object>) response.getBody().getProperties().get("args");
            assertThat(args).containsEntry("entityId", 42L);
        }

        @Test
        @DisplayName("5xx DomainException도 정상 처리한다")
        void handleDomain_5xxError_Returns5xx() {
            // given
            DomainException ex = createDomainException("TEST-500", 500, "Server error");
            ErrorMapper.MappedError mapped =
                    new ErrorMapper.MappedError(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Server Error",
                            "Server error",
                            URI.create("/errors/test"));
            when(errorMapperRegistry.map(eq(ex), any(Locale.class)))
                    .thenReturn(Optional.of(mapped));

            // when
            ResponseEntity<ProblemDetail> response =
                    sut.handleDomain(ex, mockRequest, Locale.KOREA);

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Nested
    @DisplayName("RFC 7807 응답 구조 검증")
    class Rfc7807Test {

        @Test
        @DisplayName("응답에 timestamp와 code 속성이 포함된다")
        void response_ContainsTimestampAndCode() {
            // given
            IllegalArgumentException ex = new IllegalArgumentException("test");

            // when
            ResponseEntity<ProblemDetail> response =
                    sut.handleIllegalArgumentException(ex, mockRequest);

            // then
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getProperties()).containsKey("timestamp");
            assertThat(response.getBody().getProperties())
                    .containsEntry("code", "INVALID_ARGUMENT");
        }

        @Test
        @DisplayName("요청 URI가 instance로 설정된다")
        void response_SetsInstance() {
            // given
            when(mockRequest.getRequestURI()).thenReturn("/api/v1/products");
            when(mockRequest.getQueryString()).thenReturn(null);
            IllegalArgumentException ex = new IllegalArgumentException("test");

            // when
            ResponseEntity<ProblemDetail> response =
                    sut.handleIllegalArgumentException(ex, mockRequest);

            // then
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getInstance()).isEqualTo(URI.create("/api/v1/products"));
        }

        @Test
        @DisplayName("쿼리스트링이 있으면 instance에 포함된다")
        void response_WithQueryString_IncludesInInstance() {
            // given
            when(mockRequest.getRequestURI()).thenReturn("/api/v1/products");
            when(mockRequest.getQueryString()).thenReturn("page=1&size=10");
            IllegalArgumentException ex = new IllegalArgumentException("test");

            // when
            ResponseEntity<ProblemDetail> response =
                    sut.handleIllegalArgumentException(ex, mockRequest);

            // then
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getInstance())
                    .isEqualTo(URI.create("/api/v1/products?page=1&size=10"));
        }
    }

    private static DomainException createDomainException(
            String code, int httpStatus, String message) {
        return new DomainException(
                new ErrorCode() {
                    @Override
                    public String getCode() {
                        return code;
                    }

                    @Override
                    public int getHttpStatus() {
                        return httpStatus;
                    }

                    @Override
                    public String getMessage() {
                        return message;
                    }
                },
                message) {};
    }
}
