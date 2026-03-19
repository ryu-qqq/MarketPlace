package com.ryuqq.marketplace.domain.cancel.outbox.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelOutboxIdempotencyKey 단위 테스트")
class CancelOutboxIdempotencyKeyTest {

    private static final String ORDER_ITEM_ID = "01940001-0000-7000-8000-000000000001";

    @Nested
    @DisplayName("generate() - 멱등키 생성")
    class GenerateTest {

        @Test
        @DisplayName("orderItemId, outboxType, createdAt으로 멱등키를 생성한다")
        void generateWithValidArguments() {
            Instant now = CommonVoFixtures.now();

            CancelOutboxIdempotencyKey key =
                    CancelOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, CancelOutboxType.APPROVE, now);

            assertThat(key.value()).isNotBlank();
            assertThat(key.value()).startsWith("COBO:");
        }

        @Test
        @DisplayName("생성된 멱등키는 COBO:{orderItemId}:{outboxType}:{epochMilli} 형식이다")
        void generatedKeyHasCorrectFormat() {
            Instant now = CommonVoFixtures.now();

            CancelOutboxIdempotencyKey key =
                    CancelOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, CancelOutboxType.APPROVE, now);

            String expected = "COBO:" + ORDER_ITEM_ID + ":APPROVE:" + now.toEpochMilli();
            assertThat(key.value()).isEqualTo(expected);
        }

        @Test
        @DisplayName("SELLER_CANCEL 타입의 멱등키를 생성한다")
        void generateForSellerCancelType() {
            Instant now = CommonVoFixtures.now();

            CancelOutboxIdempotencyKey key =
                    CancelOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, CancelOutboxType.SELLER_CANCEL, now);

            assertThat(key.value()).contains("SELLER_CANCEL");
        }

        @Test
        @DisplayName("orderItemId가 null이면 예외가 발생한다")
        void generateWithNullOrderItemId_ThrowsException() {
            Instant now = CommonVoFixtures.now();

            assertThatThrownBy(
                            () ->
                                    CancelOutboxIdempotencyKey.generate(
                                            null, CancelOutboxType.APPROVE, now))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("outboxType이 null이면 예외가 발생한다")
        void generateWithNullOutboxType_ThrowsException() {
            Instant now = CommonVoFixtures.now();

            assertThatThrownBy(() -> CancelOutboxIdempotencyKey.generate(ORDER_ITEM_ID, null, now))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("createdAt이 null이면 예외가 발생한다")
        void generateWithNullCreatedAt_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    CancelOutboxIdempotencyKey.generate(
                                            ORDER_ITEM_ID, CancelOutboxType.APPROVE, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("of() - 기존 멱등키 복원")
    class OfTest {

        @Test
        @DisplayName("유효한 형식의 문자열로 멱등키를 생성한다")
        void createWithValidValue() {
            String validKey = "COBO:" + ORDER_ITEM_ID + ":APPROVE:1700000000000";

            CancelOutboxIdempotencyKey key = CancelOutboxIdempotencyKey.of(validKey);

            assertThat(key.value()).isEqualTo(validKey);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> CancelOutboxIdempotencyKey.of(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("COBO: 접두사가 없으면 예외가 발생한다")
        void createWithInvalidPrefix_ThrowsException() {
            assertThatThrownBy(() -> CancelOutboxIdempotencyKey.of("INVALID:key:value"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("잘못된 멱등키 형식");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithEmptyString_ThrowsException() {
            assertThatThrownBy(() -> CancelOutboxIdempotencyKey.of(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("동등성 검증")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValuesAreEqual() {
            Instant now = Instant.ofEpochMilli(1700000000000L);

            CancelOutboxIdempotencyKey key1 =
                    CancelOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, CancelOutboxType.APPROVE, now);
            CancelOutboxIdempotencyKey key2 =
                    CancelOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, CancelOutboxType.APPROVE, now);

            assertThat(key1).isEqualTo(key2);
            assertThat(key1.hashCode()).isEqualTo(key2.hashCode());
        }

        @Test
        @DisplayName("다른 outboxType이면 다르다")
        void differentOutboxTypeAreNotEqual() {
            Instant now = CommonVoFixtures.now();

            CancelOutboxIdempotencyKey key1 =
                    CancelOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, CancelOutboxType.APPROVE, now);
            CancelOutboxIdempotencyKey key2 =
                    CancelOutboxIdempotencyKey.generate(
                            ORDER_ITEM_ID, CancelOutboxType.REJECT, now);

            assertThat(key1).isNotEqualTo(key2);
        }
    }

    @Nested
    @DisplayName("toString() 테스트")
    class ToStringTest {

        @Test
        @DisplayName("toString은 value를 반환한다")
        void toStringReturnsValue() {
            String validKey = "COBO:" + ORDER_ITEM_ID + ":APPROVE:1700000000000";
            CancelOutboxIdempotencyKey key = CancelOutboxIdempotencyKey.of(validKey);

            assertThat(key.toString()).isEqualTo(validKey);
        }
    }
}
