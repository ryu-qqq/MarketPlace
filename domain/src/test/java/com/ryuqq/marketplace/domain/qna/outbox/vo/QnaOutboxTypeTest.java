package com.ryuqq.marketplace.domain.qna.outbox.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("QnaOutboxType 단위 테스트")
class QnaOutboxTypeTest {

    @Nested
    @DisplayName("description() 테스트")
    class DescriptionTest {

        @Test
        @DisplayName("ANSWER는 답변 등록 설명을 가진다")
        void answerHasCorrectDescription() {
            assertThat(QnaOutboxType.ANSWER.description()).isEqualTo("답변 등록");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("1개의 타입이 정의되어 있다")
        void hasOneType() {
            assertThat(QnaOutboxType.values()).hasSize(1);
        }

        @Test
        @DisplayName("ANSWER 타입이 존재한다")
        void answerTypeExists() {
            assertThat(QnaOutboxType.values()).containsExactly(QnaOutboxType.ANSWER);
        }

        @Test
        @DisplayName("모든 타입은 비어있지 않은 설명을 가진다")
        void allTypesHaveNonBlankDescription() {
            for (QnaOutboxType type : QnaOutboxType.values()) {
                assertThat(type.description()).isNotBlank();
            }
        }
    }
}
