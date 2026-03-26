package com.ryuqq.marketplace.domain.exchange.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeException 및 ExchangeErrorCode 단위 테스트")
class ExchangeExceptionTest {

    @Nested
    @DisplayName("ExchangeException 생성 테스트")
    class ExchangeExceptionCreationTest {

        @Test
        @DisplayName("ErrorCode만으로 예외를 생성한다")
        void createWithErrorCodeOnly() {
            // when
            ExchangeException exception =
                    new ExchangeException(ExchangeErrorCode.EXCHANGE_NOT_FOUND);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(ExchangeErrorCode.EXCHANGE_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("EXC-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("교환 클레임을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("ErrorCode와 커스텀 메시지로 예외를 생성한다")
        void createWithErrorCodeAndCustomMessage() {
            // given
            String customMessage = "REQUESTED 상태에서 SHIPPING 상태로 변경할 수 없습니다";

            // when
            ExchangeException exception =
                    new ExchangeException(
                            ExchangeErrorCode.INVALID_STATUS_TRANSITION, customMessage);

            // then
            assertThat(exception.getMessage()).isEqualTo(customMessage);
            assertThat(exception.code()).isEqualTo("EXC-002");
            assertThat(exception.httpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("ErrorCode와 원인 예외로 예외를 생성한다")
        void createWithErrorCodeAndCause() {
            // given
            RuntimeException cause = new RuntimeException("원인 예외");

            // when
            ExchangeException exception =
                    new ExchangeException(ExchangeErrorCode.EXCHANGE_NOT_FOUND, cause);

            // then
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.getErrorCode()).isEqualTo(ExchangeErrorCode.EXCHANGE_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("ExchangeErrorCode 코드 정의 테스트")
    class ExchangeErrorCodeTest {

        @Test
        @DisplayName("EXCHANGE_NOT_FOUND는 404 상태 코드를 가진다")
        void exchangeNotFoundHas404Status() {
            ExchangeErrorCode code = ExchangeErrorCode.EXCHANGE_NOT_FOUND;
            assertThat(code.getCode()).isEqualTo("EXC-001");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).isNotBlank();
        }

        @Test
        @DisplayName("INVALID_STATUS_TRANSITION는 400 상태 코드를 가진다")
        void invalidStatusTransitionHas400Status() {
            ExchangeErrorCode code = ExchangeErrorCode.INVALID_STATUS_TRANSITION;
            assertThat(code.getCode()).isEqualTo("EXC-002");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).isNotBlank();
        }

        @Test
        @DisplayName("ALREADY_COMPLETED는 409 상태 코드를 가진다")
        void alreadyCompletedHas409Status() {
            ExchangeErrorCode code = ExchangeErrorCode.ALREADY_COMPLETED;
            assertThat(code.getCode()).isEqualTo("EXC-003");
            assertThat(code.getHttpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("INVALID_EXCHANGE_QTY는 400 상태 코드를 가진다")
        void invalidExchangeQtyHas400Status() {
            ExchangeErrorCode code = ExchangeErrorCode.INVALID_EXCHANGE_QTY;
            assertThat(code.getCode()).isEqualTo("EXC-007");
            assertThat(code.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("TARGET_UPDATE_NOT_ALLOWED는 400 상태 코드를 가진다")
        void targetUpdateNotAllowedHas400Status() {
            ExchangeErrorCode code = ExchangeErrorCode.TARGET_UPDATE_NOT_ALLOWED;
            assertThat(code.getCode()).isEqualTo("EXC-008");
            assertThat(code.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("REASON_UPDATE_NOT_ALLOWED는 400 상태 코드를 가진다")
        void reasonUpdateNotAllowedHas400Status() {
            ExchangeErrorCode code = ExchangeErrorCode.REASON_UPDATE_NOT_ALLOWED;
            assertThat(code.getCode()).isEqualTo("EXC-009");
            assertThat(code.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("EXCHANGE_OWNERSHIP_MISMATCH는 403 상태 코드를 가진다")
        void exchangeOwnershipMismatchHas403Status() {
            ExchangeErrorCode code = ExchangeErrorCode.EXCHANGE_OWNERSHIP_MISMATCH;
            assertThat(code.getCode()).isEqualTo("EXC-010");
            assertThat(code.getHttpStatus()).isEqualTo(403);
        }

        @Test
        @DisplayName("모든 에러 코드는 고유한 코드 문자열을 가진다")
        void allErrorCodesHaveUniqueCodeStrings() {
            ExchangeErrorCode[] codes = ExchangeErrorCode.values();
            long distinctCount =
                    java.util.Arrays.stream(codes)
                            .map(ExchangeErrorCode::getCode)
                            .distinct()
                            .count();
            assertThat(distinctCount).isEqualTo(codes.length);
        }
    }

    @Nested
    @DisplayName("RuntimeException 상속 테스트")
    class RuntimeExceptionTest {

        @Test
        @DisplayName("ExchangeException은 RuntimeException을 상속한다")
        void exceptionIsRuntimeException() {
            // given
            ExchangeException exception =
                    new ExchangeException(ExchangeErrorCode.EXCHANGE_NOT_FOUND);

            // then
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("assertThatThrownBy로 ExchangeException을 포착할 수 있다")
        void canBeCaughtWithAssertThatThrownBy() {
            // when & then
            assertThatThrownBy(
                            () -> {
                                throw new ExchangeException(ExchangeErrorCode.EXCHANGE_NOT_FOUND);
                            })
                    .isInstanceOf(ExchangeException.class)
                    .isInstanceOf(DomainException.class);
        }
    }
}
