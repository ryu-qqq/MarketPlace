package com.ryuqq.marketplace.domain.qna.outbox.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("QnaOutboxIdempotencyKey Value Object 단위 테스트")
class QnaOutboxIdempotencyKeyTest {

    private static final Long DEFAULT_QNA_ID = 1L;

    @Nested
    @DisplayName("generate() - 멱등키 자동 생성")
    class GenerateTest {

        @Test
        @DisplayName("유효한 파라미터로 멱등키를 생성한다")
        void generateWithValidParams() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            QnaOutboxIdempotencyKey key =
                    QnaOutboxIdempotencyKey.generate(DEFAULT_QNA_ID, QnaOutboxType.ANSWER, now);

            // then
            assertThat(key).isNotNull();
            assertThat(key.value()).isNotBlank();
        }

        @Test
        @DisplayName("생성된 멱등키는 QNBO: 접두사로 시작한다")
        void generatedKeyStartsWithPrefix() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            QnaOutboxIdempotencyKey key =
                    QnaOutboxIdempotencyKey.generate(DEFAULT_QNA_ID, QnaOutboxType.ANSWER, now);

            // then
            assertThat(key.value()).startsWith("QNBO:");
        }

        @Test
        @DisplayName("생성된 멱등키는 QNBO:{qnaId}:{type}:{epochMilli} 형식이다")
        void generatedKeyHasCorrectFormat() {
            // given
            Instant now = Instant.ofEpochMilli(1000000000L);

            // when
            QnaOutboxIdempotencyKey key =
                    QnaOutboxIdempotencyKey.generate(DEFAULT_QNA_ID, QnaOutboxType.ANSWER, now);

            // then
            String expected = "QNBO:" + DEFAULT_QNA_ID + ":ANSWER:1000000000";
            assertThat(key.value()).isEqualTo(expected);
        }

        @Test
        @DisplayName("qnaId가 null이면 예외가 발생한다")
        void generateWithNullQnaIdThrowsException() {
            assertThatThrownBy(
                            () ->
                                    QnaOutboxIdempotencyKey.generate(
                                            null, QnaOutboxType.ANSWER, CommonVoFixtures.now()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("outboxType이 null이면 예외가 발생한다")
        void generateWithNullOutboxTypeThrowsException() {
            assertThatThrownBy(
                            () ->
                                    QnaOutboxIdempotencyKey.generate(
                                            DEFAULT_QNA_ID, null, CommonVoFixtures.now()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("createdAt이 null이면 예외가 발생한다")
        void generateWithNullCreatedAtThrowsException() {
            assertThatThrownBy(
                            () ->
                                    QnaOutboxIdempotencyKey.generate(
                                            DEFAULT_QNA_ID, QnaOutboxType.ANSWER, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("of() - 기존 값으로 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 QNBO: 접두사를 가진 값으로 생성한다")
        void createWithValidPrefixedValue() {
            // given
            String validKey = "QNBO:" + DEFAULT_QNA_ID + ":ANSWER:1000000000";

            // when
            QnaOutboxIdempotencyKey key = QnaOutboxIdempotencyKey.of(validKey);

            // then
            assertThat(key.value()).isEqualTo(validKey);
        }

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void createWithNullThrowsException() {
            assertThatThrownBy(() -> QnaOutboxIdempotencyKey.of(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("잘못된 접두사를 가진 값으로 생성하면 예외가 발생한다")
        void createWithInvalidPrefixThrowsException() {
            assertThatThrownBy(() -> QnaOutboxIdempotencyKey.of("INVALID:key"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("잘못된 멱등키 형식입니다");
        }

        @Test
        @DisplayName("ROBO 접두사는 허용하지 않는다 (환불 멱등키와 구별)")
        void roboKeyIsNotAllowed() {
            assertThatThrownBy(() -> QnaOutboxIdempotencyKey.of("ROBO:some:key:123"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("EXBO 접두사는 허용하지 않는다 (교환 멱등키와 구별)")
        void exboKeyIsNotAllowed() {
            assertThatThrownBy(() -> QnaOutboxIdempotencyKey.of("EXBO:some:key:123"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 value를 가진 키는 동일하다")
        void sameValueAreEqual() {
            String rawKey = "QNBO:" + DEFAULT_QNA_ID + ":ANSWER:1000000000";
            QnaOutboxIdempotencyKey key1 = QnaOutboxIdempotencyKey.of(rawKey);
            QnaOutboxIdempotencyKey key2 = QnaOutboxIdempotencyKey.of(rawKey);

            assertThat(key1).isEqualTo(key2);
            assertThat(key1.hashCode()).isEqualTo(key2.hashCode());
        }

        @Test
        @DisplayName("다른 value를 가진 키는 동일하지 않다")
        void differentValuesAreNotEqual() {
            QnaOutboxIdempotencyKey key1 =
                    QnaOutboxIdempotencyKey.of("QNBO:1:ANSWER:1000000000");
            QnaOutboxIdempotencyKey key2 =
                    QnaOutboxIdempotencyKey.of("QNBO:2:ANSWER:1000000000");

            assertThat(key1).isNotEqualTo(key2);
        }
    }

    @Nested
    @DisplayName("toString() 테스트")
    class ToStringTest {

        @Test
        @DisplayName("toString()은 value를 반환한다")
        void toStringReturnsValue() {
            String rawKey = "QNBO:" + DEFAULT_QNA_ID + ":ANSWER:1000000000";
            QnaOutboxIdempotencyKey key = QnaOutboxIdempotencyKey.of(rawKey);

            assertThat(key.toString()).isEqualTo(rawKey);
        }
    }
}
