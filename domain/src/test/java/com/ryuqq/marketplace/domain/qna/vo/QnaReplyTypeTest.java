package com.ryuqq.marketplace.domain.qna.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("QnaReplyType 단위 테스트")
class QnaReplyTypeTest {

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("2개의 답변 유형이 정의되어 있다")
        void hasTwoTypes() {
            assertThat(QnaReplyType.values()).hasSize(2);
        }

        @Test
        @DisplayName("모든 답변 유형이 존재한다")
        void allTypesExist() {
            assertThat(QnaReplyType.values())
                    .containsExactly(QnaReplyType.SELLER_ANSWER, QnaReplyType.BUYER_FOLLOW_UP);
        }
    }
}
