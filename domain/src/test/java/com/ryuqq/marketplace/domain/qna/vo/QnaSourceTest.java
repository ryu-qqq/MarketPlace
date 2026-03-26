package com.ryuqq.marketplace.domain.qna.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("QnaSource 단위 테스트")
class QnaSourceTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 값으로 QnaSource를 생성한다")
        void createWithValidValues() {
            // when
            QnaSource source = new QnaSource(1L, "EXT-QNA-001");

            // then
            assertThat(source.salesChannelId()).isEqualTo(1L);
            assertThat(source.externalQnaId()).isEqualTo("EXT-QNA-001");
        }

        @Test
        @DisplayName("externalQnaId가 null이면 예외가 발생한다")
        void createWithNullExternalQnaIdThrowsException() {
            assertThatThrownBy(() -> new QnaSource(1L, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("externalQnaId must not be blank");
        }

        @Test
        @DisplayName("externalQnaId가 빈 문자열이면 예외가 발생한다")
        void createWithEmptyExternalQnaIdThrowsException() {
            assertThatThrownBy(() -> new QnaSource(1L, ""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("externalQnaId must not be blank");
        }

        @Test
        @DisplayName("externalQnaId가 공백 문자열이면 예외가 발생한다")
        void createWithBlankExternalQnaIdThrowsException() {
            assertThatThrownBy(() -> new QnaSource(1L, "   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("externalQnaId must not be blank");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 QnaSource는 동일하다")
        void sameValuesAreEqual() {
            QnaSource source1 = new QnaSource(1L, "EXT-QNA-001");
            QnaSource source2 = new QnaSource(1L, "EXT-QNA-001");

            assertThat(source1).isEqualTo(source2);
            assertThat(source1.hashCode()).isEqualTo(source2.hashCode());
        }

        @Test
        @DisplayName("다른 salesChannelId를 가진 QnaSource는 동일하지 않다")
        void differentSalesChannelIdAreNotEqual() {
            QnaSource source1 = new QnaSource(1L, "EXT-QNA-001");
            QnaSource source2 = new QnaSource(2L, "EXT-QNA-001");

            assertThat(source1).isNotEqualTo(source2);
        }

        @Test
        @DisplayName("다른 externalQnaId를 가진 QnaSource는 동일하지 않다")
        void differentExternalQnaIdAreNotEqual() {
            QnaSource source1 = new QnaSource(1L, "EXT-QNA-001");
            QnaSource source2 = new QnaSource(1L, "EXT-QNA-002");

            assertThat(source1).isNotEqualTo(source2);
        }
    }
}
