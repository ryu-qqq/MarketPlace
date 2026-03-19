package com.ryuqq.marketplace.domain.refund.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundNotFoundException 단위 테스트")
class RefundNotFoundExceptionTest {

    @Nested
    @DisplayName("기본 생성자 테스트")
    class DefaultConstructorTest {

        @Test
        @DisplayName("기본 생성자로 예외를 생성한다")
        void createWithDefaultConstructor() {
            // when
            RefundNotFoundException exception = new RefundNotFoundException();

            // then
            assertThat(exception).isNotNull();
            assertThat(exception).isInstanceOf(RefundException.class);
            assertThat(exception.getErrorCode()).isEqualTo(RefundErrorCode.REFUND_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("RFD-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("환불 클레임을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("ID를 포함한 생성자 테스트")
    class IdConstructorTest {

        @Test
        @DisplayName("환불 클레임 ID를 포함한 메시지로 예외를 생성한다")
        void createWithRefundClaimId() {
            // given
            String claimId = "01900000-0000-7000-8000-000000000010";

            // when
            RefundNotFoundException exception = new RefundNotFoundException(claimId);

            // then
            assertThat(exception.getMessage()).contains(claimId);
            assertThat(exception.code()).isEqualTo("RFD-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("ID 포함 메시지 형식을 확인한다")
        void messageContainsId() {
            // given
            String claimId = "TEST-REFUND-CLAIM-ID";

            // when
            RefundNotFoundException exception = new RefundNotFoundException(claimId);

            // then
            assertThat(exception.getMessage()).contains("ID가 " + claimId).contains("찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("상속 관계 테스트")
    class InheritanceTest {

        @Test
        @DisplayName("RefundNotFoundException은 RefundException을 상속한다")
        void extendsRefundException() {
            RefundNotFoundException exception = new RefundNotFoundException();
            assertThat(exception).isInstanceOf(RefundException.class);
        }

        @Test
        @DisplayName("RefundNotFoundException은 RuntimeException을 상속한다")
        void extendsRuntimeException() {
            RefundNotFoundException exception = new RefundNotFoundException();
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("assertThatThrownBy로 포착할 수 있다")
        void canBeCaughtWithAssertThatThrownBy() {
            assertThatThrownBy(
                            () -> {
                                throw new RefundNotFoundException("CLAIM-ID-123");
                            })
                    .isInstanceOf(RefundNotFoundException.class)
                    .isInstanceOf(RefundException.class);
        }
    }
}
