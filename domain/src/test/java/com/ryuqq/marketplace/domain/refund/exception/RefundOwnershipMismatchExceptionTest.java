package com.ryuqq.marketplace.domain.refund.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundOwnershipMismatchException 단위 테스트")
class RefundOwnershipMismatchExceptionTest {

    @Nested
    @DisplayName("기본 생성자 테스트")
    class DefaultConstructorTest {

        @Test
        @DisplayName("기본 생성자로 예외를 생성한다")
        void createWithDefaultConstructor() {
            // when
            RefundOwnershipMismatchException exception = new RefundOwnershipMismatchException();

            // then
            assertThat(exception).isNotNull();
            assertThat(exception).isInstanceOf(RefundException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(RefundErrorCode.REFUND_OWNERSHIP_MISMATCH);
            assertThat(exception.code()).isEqualTo("RFD-009");
            assertThat(exception.httpStatus()).isEqualTo(403);
            assertThat(exception.getMessage()).isEqualTo("환불 클레임 소유권이 일치하지 않습니다");
        }
    }

    @Nested
    @DisplayName("소유권 불일치 ID 목록을 포함한 생성자 테스트")
    class MissingIdsConstructorTest {

        @Test
        @DisplayName("불일치 ID 목록을 포함한 메시지로 예외를 생성한다")
        void createWithMissingIds() {
            // given
            List<String> missingIds = List.of("REFUND-CLAIM-001", "REFUND-CLAIM-002");

            // when
            RefundOwnershipMismatchException exception =
                    new RefundOwnershipMismatchException(missingIds);

            // then
            assertThat(exception.getMessage()).contains("REFUND-CLAIM-001");
            assertThat(exception.getMessage()).contains("REFUND-CLAIM-002");
            assertThat(exception.getMessage()).contains("소유권 불일치 또는 존재하지 않는 환불 건");
            assertThat(exception.code()).isEqualTo("RFD-009");
            assertThat(exception.httpStatus()).isEqualTo(403);
        }

        @Test
        @DisplayName("단일 ID 불일치 시 메시지를 생성한다")
        void createWithSingleMissingId() {
            // given
            List<String> missingIds = List.of("REFUND-CLAIM-SINGLE");

            // when
            RefundOwnershipMismatchException exception =
                    new RefundOwnershipMismatchException(missingIds);

            // then
            assertThat(exception.getMessage()).contains("REFUND-CLAIM-SINGLE");
        }

        @Test
        @DisplayName("빈 목록으로도 예외를 생성할 수 있다")
        void createWithEmptyList() {
            // given
            List<String> emptyIds = List.of();

            // when
            RefundOwnershipMismatchException exception =
                    new RefundOwnershipMismatchException(emptyIds);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).contains("소유권 불일치 또는 존재하지 않는 환불 건");
        }
    }

    @Nested
    @DisplayName("상속 관계 테스트")
    class InheritanceTest {

        @Test
        @DisplayName("RefundOwnershipMismatchException은 RefundException을 상속한다")
        void extendsRefundException() {
            RefundOwnershipMismatchException exception = new RefundOwnershipMismatchException();
            assertThat(exception).isInstanceOf(RefundException.class);
        }

        @Test
        @DisplayName("RefundOwnershipMismatchException은 RuntimeException을 상속한다")
        void extendsRuntimeException() {
            RefundOwnershipMismatchException exception = new RefundOwnershipMismatchException();
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("assertThatThrownBy로 포착할 수 있다")
        void canBeCaughtAsRefundException() {
            assertThatThrownBy(
                            () -> {
                                throw new RefundOwnershipMismatchException(List.of("ID-001"));
                            })
                    .isInstanceOf(RefundOwnershipMismatchException.class)
                    .isInstanceOf(RefundException.class)
                    .hasMessageContaining("ID-001");
        }
    }

    @Nested
    @DisplayName("ExchangeOwnershipMismatch와 구별 테스트")
    class DistinctionTest {

        @Test
        @DisplayName("환불 소유권 에러 코드는 RFD-009이다 (교환과 다름)")
        void refundOwnershipMismatchHasCorrectErrorCode() {
            RefundOwnershipMismatchException exception = new RefundOwnershipMismatchException();
            assertThat(exception.code()).isEqualTo("RFD-009");
            assertThat(exception.httpStatus()).isEqualTo(403);
        }
    }
}
