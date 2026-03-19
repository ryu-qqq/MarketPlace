package com.ryuqq.marketplace.domain.cancel.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelOwnershipMismatchException 단위 테스트")
class CancelOwnershipMismatchExceptionTest {

    @Nested
    @DisplayName("기본 생성자 테스트")
    class DefaultConstructorTest {

        @Test
        @DisplayName("기본 생성자로 생성하면 CAN-008 에러코드를 가진다")
        void createWithDefaultConstructor() {
            CancelOwnershipMismatchException exception = new CancelOwnershipMismatchException();

            assertThat(exception.code()).isEqualTo("CAN-008");
        }

        @Test
        @DisplayName("기본 생성자로 생성하면 HTTP 403 상태코드를 가진다")
        void defaultConstructorHas403Status() {
            CancelOwnershipMismatchException exception = new CancelOwnershipMismatchException();

            assertThat(exception.httpStatus()).isEqualTo(403);
        }

        @Test
        @DisplayName("기본 생성자로 생성하면 소유권 불일치 메시지를 가진다")
        void defaultConstructorHasOwnershipMessage() {
            CancelOwnershipMismatchException exception = new CancelOwnershipMismatchException();

            assertThat(exception.getMessage()).contains("소유권");
        }
    }

    @Nested
    @DisplayName("missingIds를 포함한 생성자 테스트")
    class MissingIdsConstructorTest {

        @Test
        @DisplayName("missingIds로 생성하면 메시지에 ID 목록이 포함된다")
        void createWithMissingIds() {
            List<String> missingIds = List.of("cancel-001", "cancel-002");
            CancelOwnershipMismatchException exception =
                    new CancelOwnershipMismatchException(missingIds);

            assertThat(exception.getMessage()).contains("cancel-001");
            assertThat(exception.getMessage()).contains("cancel-002");
        }

        @Test
        @DisplayName("missingIds로 생성해도 CAN-008 에러코드를 가진다")
        void createWithMissingIdsHasCorrectErrorCode() {
            CancelOwnershipMismatchException exception =
                    new CancelOwnershipMismatchException(List.of("cancel-001"));

            assertThat(exception.code()).isEqualTo("CAN-008");
        }

        @Test
        @DisplayName("missingIds로 생성해도 HTTP 403 상태코드를 가진다")
        void createWithMissingIdsHas403Status() {
            CancelOwnershipMismatchException exception =
                    new CancelOwnershipMismatchException(List.of("cancel-001"));

            assertThat(exception.httpStatus()).isEqualTo(403);
        }

        @Test
        @DisplayName("단일 ID로 생성하면 해당 ID가 메시지에 포함된다")
        void createWithSingleMissingId() {
            String cancelId = "cancel-999";
            CancelOwnershipMismatchException exception =
                    new CancelOwnershipMismatchException(List.of(cancelId));

            assertThat(exception.getMessage()).contains(cancelId);
        }

        @Test
        @DisplayName("빈 ID 목록으로 생성할 수 있다")
        void createWithEmptyMissingIds() {
            CancelOwnershipMismatchException exception =
                    new CancelOwnershipMismatchException(List.of());

            assertThat(exception.code()).isEqualTo("CAN-008");
            assertThat(exception.httpStatus()).isEqualTo(403);
        }
    }

    @Nested
    @DisplayName("상속 구조 검증")
    class InheritanceTest {

        @Test
        @DisplayName("CancelOwnershipMismatchException은 CancelException을 상속한다")
        void extendsFromCancelException() {
            CancelOwnershipMismatchException exception = new CancelOwnershipMismatchException();

            assertThat(exception).isInstanceOf(CancelException.class);
        }

        @Test
        @DisplayName("CancelOwnershipMismatchException은 DomainException을 상속한다")
        void extendsFromDomainException() {
            CancelOwnershipMismatchException exception = new CancelOwnershipMismatchException();

            assertThat(exception)
                    .isInstanceOf(
                            com.ryuqq.marketplace.domain.common.exception.DomainException.class);
        }
    }

    @Nested
    @DisplayName("CancelExceptionTest에서 누락된 CANCEL_OWNERSHIP_MISMATCH 에러코드 검증")
    class OwnershipErrorCodeTest {

        @Test
        @DisplayName("CANCEL_OWNERSHIP_MISMATCH 에러 코드가 올바르다")
        void cancelOwnershipMismatchErrorCode() {
            CancelErrorCode code = CancelErrorCode.CANCEL_OWNERSHIP_MISMATCH;

            assertThat(code.getCode()).isEqualTo("CAN-008");
            assertThat(code.getHttpStatus()).isEqualTo(403);
            assertThat(code.getMessage()).contains("소유권");
        }
    }
}
