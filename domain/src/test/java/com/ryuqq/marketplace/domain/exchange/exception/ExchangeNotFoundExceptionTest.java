package com.ryuqq.marketplace.domain.exchange.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeNotFoundException 단위 테스트")
class ExchangeNotFoundExceptionTest {

    @Nested
    @DisplayName("기본 생성자 테스트")
    class DefaultConstructorTest {

        @Test
        @DisplayName("기본 생성자로 예외를 생성한다")
        void createWithDefaultConstructor() {
            // when
            ExchangeNotFoundException exception = new ExchangeNotFoundException();

            // then
            assertThat(exception).isNotNull();
            assertThat(exception).isInstanceOf(ExchangeException.class);
            assertThat(exception.getErrorCode()).isEqualTo(ExchangeErrorCode.EXCHANGE_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("EXC-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("교환 클레임을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("ID를 포함한 생성자 테스트")
    class IdConstructorTest {

        @Test
        @DisplayName("교환 클레임 ID를 포함한 메시지로 예외를 생성한다")
        void createWithExchangeClaimId() {
            // given
            String claimId = "01900000-0000-7000-0000-000000000001";

            // when
            ExchangeNotFoundException exception = new ExchangeNotFoundException(claimId);

            // then
            assertThat(exception.getMessage()).contains(claimId);
            assertThat(exception.getMessage()).contains("찾을 수 없습니다");
            assertThat(exception.code()).isEqualTo("EXC-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("ID 포함 메시지 형식을 확인한다")
        void messageContainsId() {
            // given
            String claimId = "TEST-CLAIM-ID-123";

            // when
            ExchangeNotFoundException exception = new ExchangeNotFoundException(claimId);

            // then
            assertThat(exception.getMessage()).contains("ID가 " + claimId).contains("찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("상속 관계 테스트")
    class InheritanceTest {

        @Test
        @DisplayName("ExchangeNotFoundException은 ExchangeException을 상속한다")
        void extendsExchangeException() {
            ExchangeNotFoundException exception = new ExchangeNotFoundException();
            assertThat(exception).isInstanceOf(ExchangeException.class);
        }

        @Test
        @DisplayName("ExchangeNotFoundException은 RuntimeException을 상속한다")
        void extendsRuntimeException() {
            ExchangeNotFoundException exception = new ExchangeNotFoundException();
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("assertThatThrownBy로 포착할 수 있다")
        void canBeCaughtWithAssertThatThrownBy() {
            assertThatThrownBy(
                            () -> {
                                throw new ExchangeNotFoundException("CLAIM-ID-123");
                            })
                    .isInstanceOf(ExchangeNotFoundException.class)
                    .isInstanceOf(ExchangeException.class);
        }
    }
}
