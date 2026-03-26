package com.ryuqq.marketplace.domain.qna.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.qna.id.QnaId;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("QnaAnsweredEvent 단위 테스트")
class QnaAnsweredEventTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 값으로 이벤트를 생성한다")
        void createWithValidValues() {
            // given
            QnaId qnaId = QnaId.of(1L);
            Instant now = CommonVoFixtures.now();

            // when
            QnaAnsweredEvent event = new QnaAnsweredEvent(qnaId, 1L, 2L, "EXT-QNA-001", now);

            // then
            assertThat(event.qnaId()).isEqualTo(qnaId);
            assertThat(event.sellerId()).isEqualTo(1L);
            assertThat(event.salesChannelId()).isEqualTo(2L);
            assertThat(event.externalQnaId()).isEqualTo("EXT-QNA-001");
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("DomainEvent 인터페이스를 구현한다")
        void implementsDomainEvent() {
            // given
            QnaAnsweredEvent event = new QnaAnsweredEvent(
                    QnaId.of(1L), 1L, 2L, "EXT-QNA-001", CommonVoFixtures.now());

            // then
            assertThat(event).isInstanceOf(DomainEvent.class);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 이벤트는 동일하다")
        void sameValuesAreEqual() {
            Instant now = CommonVoFixtures.now();
            QnaId qnaId = QnaId.of(1L);

            QnaAnsweredEvent event1 = new QnaAnsweredEvent(qnaId, 1L, 2L, "EXT-QNA-001", now);
            QnaAnsweredEvent event2 = new QnaAnsweredEvent(qnaId, 1L, 2L, "EXT-QNA-001", now);

            assertThat(event1).isEqualTo(event2);
        }
    }
}
