package com.ryuqq.marketplace.domain.exchange.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeOwnershipMismatchException 단위 테스트")
class ExchangeOwnershipMismatchExceptionTest {

    @Nested
    @DisplayName("기본 생성자 테스트")
    class DefaultConstructorTest {

        @Test
        @DisplayName("기본 생성자로 예외를 생성한다")
        void createWithDefaultConstructor() {
            // when
            ExchangeOwnershipMismatchException exception = new ExchangeOwnershipMismatchException();

            // then
            assertThat(exception).isNotNull();
            assertThat(exception).isInstanceOf(ExchangeException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(ExchangeErrorCode.EXCHANGE_OWNERSHIP_MISMATCH);
            assertThat(exception.code()).isEqualTo("EXC-010");
            assertThat(exception.httpStatus()).isEqualTo(403);
            assertThat(exception.getMessage()).isEqualTo("요청한 교환 건의 소유권이 일치하지 않습니다");
        }
    }

    @Nested
    @DisplayName("소유권 불일치 ID 목록을 포함한 생성자 테스트")
    class MissingIdsConstructorTest {

        @Test
        @DisplayName("불일치 ID 목록을 포함한 메시지로 예외를 생성한다")
        void createWithMissingIds() {
            // given
            List<String> missingIds = List.of("CLAIM-ID-001", "CLAIM-ID-002");

            // when
            ExchangeOwnershipMismatchException exception =
                    new ExchangeOwnershipMismatchException(missingIds);

            // then
            assertThat(exception.getMessage()).contains("CLAIM-ID-001");
            assertThat(exception.getMessage()).contains("CLAIM-ID-002");
            assertThat(exception.getMessage()).contains("소유권 불일치 또는 존재하지 않는 교환 건");
            assertThat(exception.code()).isEqualTo("EXC-010");
            assertThat(exception.httpStatus()).isEqualTo(403);
        }

        @Test
        @DisplayName("단일 ID 불일치 시 메시지를 생성한다")
        void createWithSingleMissingId() {
            // given
            List<String> missingIds = List.of("CLAIM-ID-SINGLE");

            // when
            ExchangeOwnershipMismatchException exception =
                    new ExchangeOwnershipMismatchException(missingIds);

            // then
            assertThat(exception.getMessage()).contains("CLAIM-ID-SINGLE");
        }

        @Test
        @DisplayName("빈 목록으로도 예외를 생성할 수 있다")
        void createWithEmptyList() {
            // given
            List<String> emptyIds = List.of();

            // when
            ExchangeOwnershipMismatchException exception =
                    new ExchangeOwnershipMismatchException(emptyIds);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).contains("소유권 불일치 또는 존재하지 않는 교환 건");
        }
    }

    @Nested
    @DisplayName("상속 관계 테스트")
    class InheritanceTest {

        @Test
        @DisplayName("ExchangeOwnershipMismatchException은 ExchangeException을 상속한다")
        void extendsExchangeException() {
            ExchangeOwnershipMismatchException exception = new ExchangeOwnershipMismatchException();
            assertThat(exception).isInstanceOf(ExchangeException.class);
        }

        @Test
        @DisplayName("ExchangeOwnershipMismatchException은 RuntimeException을 상속한다")
        void extendsRuntimeException() {
            ExchangeOwnershipMismatchException exception = new ExchangeOwnershipMismatchException();
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("assertThatThrownBy로 포착할 수 있다")
        void canBeCaughtAsExchangeException() {
            assertThatThrownBy(
                            () -> {
                                throw new ExchangeOwnershipMismatchException(List.of("ID-001"));
                            })
                    .isInstanceOf(ExchangeOwnershipMismatchException.class)
                    .isInstanceOf(ExchangeException.class)
                    .hasMessageContaining("ID-001");
        }
    }
}
