package com.ryuqq.marketplace.domain.refund.outbox.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundOutboxIdempotencyKey Value Object 단위 테스트")
class RefundOutboxIdempotencyKeyTest {

    private static final String ORDER_ITEM_ID = "01940001-0000-7000-8000-000000000001";

    @Nested
    @DisplayName("generate() - 멱등키 자동 생성")
    class GenerateTest {

        @Test
        @DisplayName("유효한 파라미터로 멱등키를 생성한다")
        void generateWithValidParams() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            RefundOutboxIdempotencyKey key =
                    RefundOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, RefundOutboxType.REQUEST, now);

            // then
            assertThat(key).isNotNull();
            assertThat(key.value()).isNotBlank();
        }

        @Test
        @DisplayName("생성된 멱등키는 ROBO: 접두사로 시작한다")
        void generatedKeyStartsWithPrefix() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            RefundOutboxIdempotencyKey key =
                    RefundOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, RefundOutboxType.APPROVE, now);

            // then
            assertThat(key.value()).startsWith("ROBO:");
        }

        @Test
        @DisplayName("생성된 멱등키는 ROBO:{orderItemId}:{type}:{epochMilli} 형식이다")
        void generatedKeyHasCorrectFormat() {
            // given
            Instant now = Instant.ofEpochMilli(1000000000L);

            // when
            RefundOutboxIdempotencyKey key =
                    RefundOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, RefundOutboxType.COLLECT, now);

            // then
            String expected = "ROBO:" + ORDER_ITEM_ID + ":COLLECT:1000000000";
            assertThat(key.value()).isEqualTo(expected);
        }

        @Test
        @DisplayName("orderItemId가 null이면 예외가 발생한다")
        void generateWithNullOrderItemIdThrowsException() {
            assertThatThrownBy(
                            () ->
                                    RefundOutboxIdempotencyKey.generate(
                                            null, RefundOutboxType.REQUEST, CommonVoFixtures.now()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("outboxType이 null이면 예외가 발생한다")
        void generateWithNullOutboxTypeThrowsException() {
            assertThatThrownBy(
                            () ->
                                    RefundOutboxIdempotencyKey.generate(
                                            ORDER_ITEM_ID, null, CommonVoFixtures.now()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("createdAt이 null이면 예외가 발생한다")
        void generateWithNullCreatedAtThrowsException() {
            assertThatThrownBy(
                            () ->
                                    RefundOutboxIdempotencyKey.generate(
                                            ORDER_ITEM_ID, RefundOutboxType.REQUEST, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("of() - 기존 값으로 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 ROBO: 접두사를 가진 값으로 생성한다")
        void createWithValidPrefixedValue() {
            // given
            String validKey = "ROBO:" + ORDER_ITEM_ID + ":REQUEST:1000000000";

            // when
            RefundOutboxIdempotencyKey key = RefundOutboxIdempotencyKey.of(validKey);

            // then
            assertThat(key.value()).isEqualTo(validKey);
        }

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void createWithNullThrowsException() {
            assertThatThrownBy(() -> RefundOutboxIdempotencyKey.of(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("잘못된 접두사를 가진 값으로 생성하면 예외가 발생한다")
        void createWithInvalidPrefixThrowsException() {
            assertThatThrownBy(() -> RefundOutboxIdempotencyKey.of("INVALID:key"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("잘못된 멱등키 형식입니다");
        }

        @Test
        @DisplayName("EXBO 접두사는 허용하지 않는다 (교환 멱등키와 구별)")
        void exboKeyIsNotAllowed() {
            assertThatThrownBy(() -> RefundOutboxIdempotencyKey.of("EXBO:some:key:123"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 value를 가진 키는 동일하다")
        void sameValueAreEqual() {
            String rawKey = "ROBO:" + ORDER_ITEM_ID + ":REQUEST:1000000000";
            RefundOutboxIdempotencyKey key1 = RefundOutboxIdempotencyKey.of(rawKey);
            RefundOutboxIdempotencyKey key2 = RefundOutboxIdempotencyKey.of(rawKey);

            assertThat(key1).isEqualTo(key2);
            assertThat(key1.hashCode()).isEqualTo(key2.hashCode());
        }

        @Test
        @DisplayName("다른 value를 가진 키는 동일하지 않다")
        void differentValuesAreNotEqual() {
            RefundOutboxIdempotencyKey key1 =
                    RefundOutboxIdempotencyKey.of("ROBO:" + ORDER_ITEM_ID + ":REQUEST:1000000000");
            RefundOutboxIdempotencyKey key2 =
                    RefundOutboxIdempotencyKey.of("ROBO:" + ORDER_ITEM_ID + ":APPROVE:1000000000");

            assertThat(key1).isNotEqualTo(key2);
        }
    }

    @Nested
    @DisplayName("toString() 테스트")
    class ToStringTest {

        @Test
        @DisplayName("toString()은 value를 반환한다")
        void toStringReturnsValue() {
            String rawKey = "ROBO:" + ORDER_ITEM_ID + ":COLLECT:1000000000";
            RefundOutboxIdempotencyKey key = RefundOutboxIdempotencyKey.of(rawKey);

            assertThat(key.toString()).isEqualTo(rawKey);
        }
    }
}
