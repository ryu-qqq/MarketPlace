package com.ryuqq.marketplace.domain.shipment.outbox.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShipmentOutboxIdempotencyKey Value Object 단위 테스트")
class ShipmentOutboxIdempotencyKeyTest {

    private static final String ORDER_ITEM_ID = "01940001-0000-7000-8000-000000000001";
    private static final Instant FIXED_TIME = Instant.ofEpochMilli(1700000000000L);

    @Nested
    @DisplayName("generate() - 멱등키 생성")
    class GenerateTest {

        @Test
        @DisplayName("올바른 형식의 멱등키를 생성한다")
        void generateWithCorrectFormat() {
            ShipmentOutboxIdempotencyKey key =
                    ShipmentOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, ShipmentOutboxType.SHIP, FIXED_TIME);

            assertThat(key.value()).startsWith("SHPO:");
            assertThat(key.value()).contains(ORDER_ITEM_ID);
            assertThat(key.value()).contains("SHIP");
            assertThat(key.value()).contains(String.valueOf(FIXED_TIME.toEpochMilli()));
        }

        @Test
        @DisplayName("멱등키 형식은 SHPO:{orderItemId}:{outboxType}:{epochMilli}이다")
        void generateKeyFormat() {
            ShipmentOutboxIdempotencyKey key =
                    ShipmentOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, ShipmentOutboxType.CONFIRM, FIXED_TIME);

            String expected = "SHPO:" + ORDER_ITEM_ID + ":CONFIRM:" + FIXED_TIME.toEpochMilli();
            assertThat(key.value()).isEqualTo(expected);
        }

        @Test
        @DisplayName("같은 인자로 생성한 멱등키는 동일하다")
        void sameArgumentsProduceSameKey() {
            ShipmentOutboxIdempotencyKey key1 =
                    ShipmentOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, ShipmentOutboxType.SHIP, FIXED_TIME);
            ShipmentOutboxIdempotencyKey key2 =
                    ShipmentOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, ShipmentOutboxType.SHIP, FIXED_TIME);

            assertThat(key1).isEqualTo(key2);
        }

        @Test
        @DisplayName("다른 OutboxType으로 생성한 멱등키는 다르다")
        void differentOutboxTypesProduceDifferentKeys() {
            ShipmentOutboxIdempotencyKey shipKey =
                    ShipmentOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, ShipmentOutboxType.SHIP, FIXED_TIME);
            ShipmentOutboxIdempotencyKey deliverKey =
                    ShipmentOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, ShipmentOutboxType.DELIVER, FIXED_TIME);

            assertThat(shipKey).isNotEqualTo(deliverKey);
        }

        @Test
        @DisplayName("orderItemId가 null이면 예외가 발생한다")
        void generateWithNullOrderItemId_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    ShipmentOutboxIdempotencyKey.generate(
                                            null, ShipmentOutboxType.SHIP, FIXED_TIME))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("outboxType이 null이면 예외가 발생한다")
        void generateWithNullOutboxType_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    ShipmentOutboxIdempotencyKey.generate(
                                            ORDER_ITEM_ID, null, FIXED_TIME))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("createdAt이 null이면 예외가 발생한다")
        void generateWithNullCreatedAt_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    ShipmentOutboxIdempotencyKey.generate(
                                            ORDER_ITEM_ID, ShipmentOutboxType.SHIP, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("of() - 기존 값으로 생성")
    class OfTest {

        @Test
        @DisplayName("올바른 형식의 문자열로 생성한다")
        void createWithValidFormat() {
            String validKey = "SHPO:" + ORDER_ITEM_ID + ":SHIP:1700000000000";

            ShipmentOutboxIdempotencyKey key = ShipmentOutboxIdempotencyKey.of(validKey);

            assertThat(key.value()).isEqualTo(validKey);
        }

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void createWithNullValue_ThrowsException() {
            assertThatThrownBy(() -> ShipmentOutboxIdempotencyKey.of(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("잘못된 형식의 문자열로 생성하면 예외가 발생한다")
        void createWithInvalidFormat_ThrowsException() {
            assertThatThrownBy(() -> ShipmentOutboxIdempotencyKey.of("INVALID:key"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("잘못된 멱등키 형식");
        }

        @Test
        @DisplayName("SHPO: 접두사가 없으면 예외가 발생한다")
        void createWithoutPrefix_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    ShipmentOutboxIdempotencyKey.of(
                                            "OTHER:" + ORDER_ITEM_ID + ":SHIP:1700000000000"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            ShipmentOutboxIdempotencyKey key1 =
                    ShipmentOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, ShipmentOutboxType.SHIP, FIXED_TIME);
            ShipmentOutboxIdempotencyKey key2 =
                    ShipmentOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, ShipmentOutboxType.SHIP, FIXED_TIME);

            assertThat(key1).isEqualTo(key2);
            assertThat(key1.hashCode()).isEqualTo(key2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 다르다")
        void differentValuesAreNotEqual() {
            ShipmentOutboxIdempotencyKey key1 =
                    ShipmentOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, ShipmentOutboxType.SHIP, FIXED_TIME);
            ShipmentOutboxIdempotencyKey key2 =
                    ShipmentOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, ShipmentOutboxType.DELIVER, FIXED_TIME);

            assertThat(key1).isNotEqualTo(key2);
        }
    }

    @Nested
    @DisplayName("toString() 테스트")
    class ToStringTest {

        @Test
        @DisplayName("toString()은 value 값을 반환한다")
        void toStringReturnsValue() {
            ShipmentOutboxIdempotencyKey key =
                    ShipmentOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, ShipmentOutboxType.SHIP, FIXED_TIME);

            assertThat(key.toString()).isEqualTo(key.value());
        }
    }
}
