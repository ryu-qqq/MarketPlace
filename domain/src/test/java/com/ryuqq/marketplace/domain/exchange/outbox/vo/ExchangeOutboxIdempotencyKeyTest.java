package com.ryuqq.marketplace.domain.exchange.outbox.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeOutboxIdempotencyKey Value Object 단위 테스트")
class ExchangeOutboxIdempotencyKeyTest {

    private static final String ORDER_ITEM_ID = "01900000-0000-7000-0000-000000000010";

    @Nested
    @DisplayName("generate() - 멱등키 자동 생성")
    class GenerateTest {

        @Test
        @DisplayName("유효한 파라미터로 멱등키를 생성한다")
        void generateWithValidParams() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            ExchangeOutboxIdempotencyKey key =
                    ExchangeOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, ExchangeOutboxType.COLLECT, now);

            // then
            assertThat(key).isNotNull();
            assertThat(key.value()).isNotBlank();
        }

        @Test
        @DisplayName("생성된 멱등키는 EXBO: 접두사로 시작한다")
        void generatedKeyStartsWithPrefix() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            ExchangeOutboxIdempotencyKey key =
                    ExchangeOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, ExchangeOutboxType.COLLECT, now);

            // then
            assertThat(key.value()).startsWith("EXBO:");
        }

        @Test
        @DisplayName("생성된 멱등키는 EXBO:{orderItemId}:{type}:{epochMilli} 형식이다")
        void generatedKeyHasCorrectFormat() {
            // given
            Instant now = Instant.ofEpochMilli(1000000000L);

            // when
            ExchangeOutboxIdempotencyKey key =
                    ExchangeOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, ExchangeOutboxType.SHIP, now);

            // then
            String expected = "EXBO:" + ORDER_ITEM_ID + ":SHIP:1000000000";
            assertThat(key.value()).isEqualTo(expected);
        }

        @Test
        @DisplayName("orderItemId가 null이면 예외가 발생한다")
        void generateWithNullOrderItemIdThrowsException() {
            assertThatThrownBy(
                            () ->
                                    ExchangeOutboxIdempotencyKey.generate(
                                            null,
                                            ExchangeOutboxType.COLLECT,
                                            CommonVoFixtures.now()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("outboxType이 null이면 예외가 발생한다")
        void generateWithNullOutboxTypeThrowsException() {
            assertThatThrownBy(
                            () ->
                                    ExchangeOutboxIdempotencyKey.generate(
                                            ORDER_ITEM_ID, null, CommonVoFixtures.now()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("createdAt이 null이면 예외가 발생한다")
        void generateWithNullCreatedAtThrowsException() {
            assertThatThrownBy(
                            () ->
                                    ExchangeOutboxIdempotencyKey.generate(
                                            ORDER_ITEM_ID, ExchangeOutboxType.COLLECT, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("of() - 기존 값으로 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 EXBO: 접두사를 가진 값으로 생성한다")
        void createWithValidPrefixedValue() {
            // given
            String validKey = "EXBO:" + ORDER_ITEM_ID + ":COLLECT:1000000000";

            // when
            ExchangeOutboxIdempotencyKey key = ExchangeOutboxIdempotencyKey.of(validKey);

            // then
            assertThat(key.value()).isEqualTo(validKey);
        }

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void createWithNullThrowsException() {
            assertThatThrownBy(() -> ExchangeOutboxIdempotencyKey.of(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("잘못된 접두사를 가진 값으로 생성하면 예외가 발생한다")
        void createWithInvalidPrefixThrowsException() {
            assertThatThrownBy(() -> ExchangeOutboxIdempotencyKey.of("INVALID:key"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("잘못된 멱등키 형식입니다");
        }

        @Test
        @DisplayName("빈 문자열로 생성하면 예외가 발생한다")
        void createWithEmptyStringThrowsException() {
            assertThatThrownBy(() -> ExchangeOutboxIdempotencyKey.of(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 value를 가진 키는 동일하다")
        void sameValueAreEqual() {
            String rawKey = "EXBO:" + ORDER_ITEM_ID + ":COLLECT:1000000000";
            ExchangeOutboxIdempotencyKey key1 = ExchangeOutboxIdempotencyKey.of(rawKey);
            ExchangeOutboxIdempotencyKey key2 = ExchangeOutboxIdempotencyKey.of(rawKey);

            assertThat(key1).isEqualTo(key2);
            assertThat(key1.hashCode()).isEqualTo(key2.hashCode());
        }

        @Test
        @DisplayName("다른 value를 가진 키는 동일하지 않다")
        void differentValuesAreNotEqual() {
            ExchangeOutboxIdempotencyKey key1 =
                    ExchangeOutboxIdempotencyKey.of(
                            "EXBO:" + ORDER_ITEM_ID + ":COLLECT:1000000000");
            ExchangeOutboxIdempotencyKey key2 =
                    ExchangeOutboxIdempotencyKey.of("EXBO:" + ORDER_ITEM_ID + ":SHIP:1000000000");

            assertThat(key1).isNotEqualTo(key2);
        }
    }

    @Nested
    @DisplayName("toString() 테스트")
    class ToStringTest {

        @Test
        @DisplayName("toString()은 value를 반환한다")
        void toStringReturnsValue() {
            String rawKey = "EXBO:" + ORDER_ITEM_ID + ":COLLECT:1000000000";
            ExchangeOutboxIdempotencyKey key = ExchangeOutboxIdempotencyKey.of(rawKey);

            assertThat(key.toString()).isEqualTo(rawKey);
        }
    }
}
