package com.ryuqq.marketplace.domain.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("DomainException 단위 테스트")
class DomainExceptionTest {

    /** 테스트용 ErrorCode 구현체 */
    enum TestErrorCode implements ErrorCode {
        TEST_NOT_FOUND("TEST-001", 404, "테스트 리소스를 찾을 수 없습니다"),
        TEST_BAD_REQUEST("TEST-002", 400, "잘못된 요청입니다"),
        TEST_CONFLICT("TEST-003", 409, "충돌이 발생했습니다");

        private final String code;
        private final int httpStatus;
        private final String message;

        TestErrorCode(String code, int httpStatus, String message) {
            this.code = code;
            this.httpStatus = httpStatus;
            this.message = message;
        }

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
    }

    /** 테스트용 DomainException 구현체 */
    static class TestDomainException extends DomainException {
        TestDomainException(ErrorCode errorCode) {
            super(errorCode);
        }

        TestDomainException(ErrorCode errorCode, String message) {
            super(errorCode, message);
        }

        TestDomainException(ErrorCode errorCode, String message, Map<String, Object> args) {
            super(errorCode, message, args);
        }

        TestDomainException(ErrorCode errorCode, Throwable cause) {
            super(errorCode, cause);
        }
    }

    @Nested
    @DisplayName("ErrorCode만으로 생성 테스트")
    class ErrorCodeOnlyTest {

        @Test
        @DisplayName("ErrorCode로 생성하면 code(), httpStatus(), getMessage()가 올바르다")
        void createWithErrorCode() {
            TestDomainException exception = new TestDomainException(TestErrorCode.TEST_NOT_FOUND);

            assertThat(exception.code()).isEqualTo("TEST-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("테스트 리소스를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("args()는 빈 Map을 반환한다")
        void argsIsEmptyMap() {
            TestDomainException exception = new TestDomainException(TestErrorCode.TEST_NOT_FOUND);

            assertThat(exception.args()).isEmpty();
        }

        @Test
        @DisplayName("getErrorCode()는 ErrorCode 객체를 반환한다")
        void getErrorCodeReturnsErrorCode() {
            TestDomainException exception = new TestDomainException(TestErrorCode.TEST_NOT_FOUND);

            assertThat(exception.getErrorCode()).isEqualTo(TestErrorCode.TEST_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("커스텀 메시지로 생성 테스트")
    class CustomMessageTest {

        @Test
        @DisplayName("커스텀 메시지로 생성하면 해당 메시지를 사용한다")
        void createWithCustomMessage() {
            TestDomainException exception =
                    new TestDomainException(TestErrorCode.TEST_NOT_FOUND, "커스텀 에러 메시지: id=999");

            assertThat(exception.getMessage()).isEqualTo("커스텀 에러 메시지: id=999");
            assertThat(exception.code()).isEqualTo("TEST-001");
        }
    }

    @Nested
    @DisplayName("컨텍스트 정보 포함 생성 테스트")
    class ContextArgsTest {

        @Test
        @DisplayName("args 포함 생성 시 args()로 조회 가능하다")
        void createWithArgsAndRetrieve() {
            Map<String, Object> args = Map.of("orderId", 123L, "status", "PENDING");
            TestDomainException exception =
                    new TestDomainException(TestErrorCode.TEST_BAD_REQUEST, "잘못된 상태", args);

            assertThat(exception.args()).containsEntry("orderId", 123L);
            assertThat(exception.args()).containsEntry("status", "PENDING");
        }

        @Test
        @DisplayName("args()가 반환하는 Map은 불변이다")
        void argsIsUnmodifiable() {
            Map<String, Object> args = Map.of("key", "value");
            TestDomainException exception =
                    new TestDomainException(TestErrorCode.TEST_BAD_REQUEST, "메시지", args);

            org.assertj.core.api.Assertions.assertThatThrownBy(
                            () -> exception.args().put("newKey", "newValue"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("null args로 생성하면 args()는 빈 Map이다")
        void nullArgsResultsInEmptyMap() {
            TestDomainException exception =
                    new TestDomainException(TestErrorCode.TEST_BAD_REQUEST, "메시지", null);

            assertThat(exception.args()).isEmpty();
        }
    }

    @Nested
    @DisplayName("원인 예외로 생성 테스트")
    class CauseTest {

        @Test
        @DisplayName("원인 예외로 생성하면 getCause()로 조회 가능하다")
        void createWithCause() {
            RuntimeException cause = new RuntimeException("원인 예외");
            TestDomainException exception =
                    new TestDomainException(TestErrorCode.TEST_NOT_FOUND, cause);

            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.code()).isEqualTo("TEST-001");
        }

        @Test
        @DisplayName("원인 예외로 생성하면 ErrorCode의 메시지를 사용한다")
        void causeExceptionUsesErrorCodeMessage() {
            RuntimeException cause = new RuntimeException("원인");
            TestDomainException exception =
                    new TestDomainException(TestErrorCode.TEST_NOT_FOUND, cause);

            assertThat(exception.getMessage()).isEqualTo("테스트 리소스를 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("RuntimeException 상속 검증")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException은 RuntimeException이다")
        void domainExceptionIsRuntimeException() {
            TestDomainException exception = new TestDomainException(TestErrorCode.TEST_NOT_FOUND);

            assertThat(exception).isInstanceOf(RuntimeException.class);
        }
    }
}
