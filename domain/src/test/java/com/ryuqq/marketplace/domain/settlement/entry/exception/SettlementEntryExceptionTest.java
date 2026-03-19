package com.ryuqq.marketplace.domain.settlement.entry.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SettlementEntry 예외 테스트")
class SettlementEntryExceptionTest {

    @Nested
    @DisplayName("SettlementEntryErrorCode 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("ENTRY_NOT_FOUND 에러 코드가 올바르다")
        void entryNotFound() {
            SettlementEntryErrorCode code = SettlementEntryErrorCode.ENTRY_NOT_FOUND;

            assertThat(code.getCode()).isEqualTo("STE-001");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("INVALID_STATUS_TRANSITION 에러 코드가 올바르다")
        void invalidStatusTransition() {
            SettlementEntryErrorCode code = SettlementEntryErrorCode.INVALID_STATUS_TRANSITION;

            assertThat(code.getCode()).isEqualTo("STE-002");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("상태 전이");
        }

        @Test
        @DisplayName("ALREADY_SETTLED 에러 코드가 올바르다")
        void alreadySettled() {
            SettlementEntryErrorCode code = SettlementEntryErrorCode.ALREADY_SETTLED;

            assertThat(code.getCode()).isEqualTo("STE-003");
            assertThat(code.getHttpStatus()).isEqualTo(409);
            assertThat(code.getMessage()).contains("정산 완료");
        }

        @Test
        @DisplayName("SettlementEntryErrorCode는 3가지 값이다")
        void errorCodeValues() {
            SettlementEntryErrorCode[] values = SettlementEntryErrorCode.values();

            assertThat(values)
                    .containsExactlyInAnyOrder(
                            SettlementEntryErrorCode.ENTRY_NOT_FOUND,
                            SettlementEntryErrorCode.INVALID_STATUS_TRANSITION,
                            SettlementEntryErrorCode.ALREADY_SETTLED);
        }
    }

    @Nested
    @DisplayName("SettlementEntryException 테스트")
    class ExceptionClassTest {

        @Test
        @DisplayName("ErrorCode만으로 생성된다")
        void createWithErrorCode() {
            SettlementEntryException exception =
                    new SettlementEntryException(SettlementEntryErrorCode.ENTRY_NOT_FOUND);

            assertThat(exception.code()).isEqualTo("STE-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("커스텀 메시지를 지원한다")
        void createWithCustomMessage() {
            String customMessage = "정산 원장 ID abc-123을 찾을 수 없습니다";

            SettlementEntryException exception =
                    new SettlementEntryException(
                            SettlementEntryErrorCode.ENTRY_NOT_FOUND, customMessage);

            assertThat(exception.code()).isEqualTo("STE-001");
            assertThat(exception.getMessage()).isEqualTo(customMessage);
        }

        @Test
        @DisplayName("INVALID_STATUS_TRANSITION으로 생성한 예외의 HTTP 상태는 400이다")
        void invalidTransitionExceptionHttpStatus() {
            SettlementEntryException exception =
                    new SettlementEntryException(
                            SettlementEntryErrorCode.INVALID_STATUS_TRANSITION,
                            "SETTLED 상태에서 CONFIRMED 상태로 변경할 수 없습니다");

            assertThat(exception.httpStatus()).isEqualTo(400);
            assertThat(exception.getMessage()).contains("SETTLED");
        }
    }

    @Nested
    @DisplayName("SettlementEntryNotFoundException 테스트")
    class NotFoundExceptionTest {

        @Test
        @DisplayName("entryId를 포함한 메시지로 생성된다")
        void createWithEntryId() {
            String entryId = "entry-abc-001";

            SettlementEntryNotFoundException exception =
                    new SettlementEntryNotFoundException(entryId);

            assertThat(exception.code()).isEqualTo("STE-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).contains(entryId);
        }

        @Test
        @DisplayName("SettlementEntryException의 하위 타입이다")
        void isSubTypeOfSettlementEntryException() {
            SettlementEntryNotFoundException exception =
                    new SettlementEntryNotFoundException("entry-001");

            assertThat(exception).isInstanceOf(SettlementEntryException.class);
        }

        @Test
        @DisplayName("메시지에 정산 원장 안내 문구가 포함된다")
        void messageContainsGuideText() {
            SettlementEntryNotFoundException exception =
                    new SettlementEntryNotFoundException("entry-xyz");

            assertThat(exception.getMessage()).contains("정산 원장을 찾을 수 없습니다");
        }

        @Test
        @DisplayName("예외를 정상적으로 던지고 잡을 수 있다")
        void canThrowAndCatch() {
            assertThatCode(
                            () -> {
                                throw new SettlementEntryNotFoundException("entry-throw-test");
                            })
                    .isInstanceOf(SettlementEntryNotFoundException.class)
                    .isInstanceOf(SettlementEntryException.class);
        }
    }
}
