package com.ryuqq.marketplace.domain.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RetryableExecutionException 단위 테스트")
class RetryableExecutionExceptionTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("메시지와 원인 예외로 생성한다")
        void createWithMessageAndCause() {
            RuntimeException cause = new RuntimeException("DB 커넥션 실패");
            RetryableExecutionException exception =
                    new RetryableExecutionException("인프라 오류로 실행 실패", cause);

            assertThat(exception.getMessage()).isEqualTo("인프라 오류로 실행 실패");
            assertThat(exception.getCause()).isEqualTo(cause);
        }

        @Test
        @DisplayName("RetryableExecutionException은 RuntimeException이다")
        void isRuntimeException() {
            RetryableExecutionException exception =
                    new RetryableExecutionException("메시지", new RuntimeException());

            assertThat(exception).isInstanceOf(RuntimeException.class);
        }
    }
}
