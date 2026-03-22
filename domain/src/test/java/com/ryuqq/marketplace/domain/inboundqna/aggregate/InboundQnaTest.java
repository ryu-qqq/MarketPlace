package com.ryuqq.marketplace.domain.inboundqna.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.inboundqna.InboundQnaFixtures;
import com.ryuqq.marketplace.domain.inboundqna.vo.InboundQnaStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundQna Aggregate 단위 테스트")
class InboundQnaTest {

    @Nested
    @DisplayName("forNew")
    class ForNew {

        @Test
        @DisplayName("신규 InboundQna 생성 시 RECEIVED 상태")
        void shouldCreateWithReceivedStatus() {
            InboundQna qna = InboundQnaFixtures.newInboundQna();

            assertThat(qna.status()).isEqualTo(InboundQnaStatus.RECEIVED);
            assertThat(qna.id().isNew()).isTrue();
            assertThat(qna.internalQnaId()).isNull();
            assertThat(qna.failureReason()).isNull();
        }
    }

    @Nested
    @DisplayName("markConverted")
    class MarkConverted {

        @Test
        @DisplayName("RECEIVED 상태에서 CONVERTED로 전이 성공")
        void shouldTransitionToConverted() {
            InboundQna qna = InboundQnaFixtures.receivedInboundQna();
            Instant now = CommonVoFixtures.now();

            qna.markConverted(100L, now);

            assertThat(qna.status()).isEqualTo(InboundQnaStatus.CONVERTED);
            assertThat(qna.internalQnaId()).isEqualTo(100L);
            assertThat(qna.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("CONVERTED 상태에서 markConverted 호출 시 예외")
        void shouldThrowWhenAlreadyConverted() {
            InboundQna qna = InboundQnaFixtures.convertedInboundQna();

            assertThatThrownBy(() -> qna.markConverted(200L, CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("FAILED 상태에서 markConverted 호출 시 예외")
        void shouldThrowWhenFailed() {
            InboundQna qna = InboundQnaFixtures.failedInboundQna();

            assertThatThrownBy(() -> qna.markConverted(200L, CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("markFailed")
    class MarkFailed {

        @Test
        @DisplayName("RECEIVED 상태에서 FAILED로 전이 성공")
        void shouldTransitionToFailed() {
            InboundQna qna = InboundQnaFixtures.receivedInboundQna();
            Instant now = CommonVoFixtures.now();

            qna.markFailed("매핑 실패", now);

            assertThat(qna.status()).isEqualTo(InboundQnaStatus.FAILED);
            assertThat(qna.failureReason()).isEqualTo("매핑 실패");
            assertThat(qna.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("CONVERTED 상태에서 markFailed 호출 시 예외")
        void shouldThrowWhenAlreadyConverted() {
            InboundQna qna = InboundQnaFixtures.convertedInboundQna();

            assertThatThrownBy(() -> qna.markFailed("실패", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
