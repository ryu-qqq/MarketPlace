package com.ryuqq.marketplace.domain.seller.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAuthOutboxIdempotencyKey VO 테스트")
class SellerAuthOutboxIdempotencyKeyTest {

    @Nested
    @DisplayName("generate() - 새 멱등키 생성")
    class GenerateTest {

        @Test
        @DisplayName("SellerId와 시간으로 멱등키를 생성한다")
        void generate_WithSellerId() {
            // given
            SellerId sellerId = SellerId.of(123L);
            Instant createdAt = Instant.ofEpochMilli(1706612400000L);

            // when
            SellerAuthOutboxIdempotencyKey key =
                    SellerAuthOutboxIdempotencyKey.generate(sellerId, createdAt);

            // then
            assertThat(key.value()).isEqualTo("SAO:123:1706612400000");
        }

        @Test
        @DisplayName("SellerId가 null이면 0으로 생성한다")
        void generate_WithNullSellerId() {
            // given
            Instant createdAt = Instant.ofEpochMilli(1706612400000L);

            // when
            SellerAuthOutboxIdempotencyKey key =
                    SellerAuthOutboxIdempotencyKey.generate(null, createdAt);

            // then
            assertThat(key.value()).isEqualTo("SAO:0:1706612400000");
        }

        @Test
        @DisplayName("createdAt이 null이면 예외가 발생한다")
        void generate_WithNullCreatedAt_ThrowsException() {
            // given
            SellerId sellerId = SellerId.of(123L);

            // when & then
            assertThatThrownBy(() -> SellerAuthOutboxIdempotencyKey.generate(sellerId, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("createdAt");
        }
    }

    @Nested
    @DisplayName("of() - 기존 값으로 재구성")
    class OfTest {

        @Test
        @DisplayName("유효한 멱등키 값으로 재구성한다")
        void of_ValidValue() {
            // given
            String value = "SAO:123:1706612400000";

            // when
            SellerAuthOutboxIdempotencyKey key = SellerAuthOutboxIdempotencyKey.of(value);

            // then
            assertThat(key.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("잘못된 형식이면 예외가 발생한다")
        void of_InvalidFormat_ThrowsException() {
            // given
            String invalidValue = "INVALID:123:1706612400000";

            // when & then
            assertThatThrownBy(() -> SellerAuthOutboxIdempotencyKey.of(invalidValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("잘못된 멱등키 형식");
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void of_NullValue_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> SellerAuthOutboxIdempotencyKey.of(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("equals/hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 값이면 equals가 true이다")
        void equals_SameValue() {
            // given
            SellerAuthOutboxIdempotencyKey key1 =
                    SellerAuthOutboxIdempotencyKey.of("SAO:123:1706612400000");
            SellerAuthOutboxIdempotencyKey key2 =
                    SellerAuthOutboxIdempotencyKey.of("SAO:123:1706612400000");

            // when & then
            assertThat(key1).isEqualTo(key2);
            assertThat(key1.hashCode()).isEqualTo(key2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 equals가 false이다")
        void equals_DifferentValue() {
            // given
            SellerAuthOutboxIdempotencyKey key1 =
                    SellerAuthOutboxIdempotencyKey.of("SAO:123:1706612400000");
            SellerAuthOutboxIdempotencyKey key2 =
                    SellerAuthOutboxIdempotencyKey.of("SAO:456:1706612400000");

            // when & then
            assertThat(key1).isNotEqualTo(key2);
        }
    }

    @Nested
    @DisplayName("headerName() - HTTP 헤더명")
    class HeaderNameTest {

        @Test
        @DisplayName("헤더명은 X-Idempotency-Key이다")
        void headerName() {
            // when & then
            assertThat(SellerAuthOutboxIdempotencyKey.headerName()).isEqualTo("X-Idempotency-Key");
        }
    }

    @Nested
    @DisplayName("toString()")
    class ToStringTest {

        @Test
        @DisplayName("toString은 value를 반환한다")
        void toString_ReturnsValue() {
            // given
            String value = "SAO:123:1706612400000";
            SellerAuthOutboxIdempotencyKey key = SellerAuthOutboxIdempotencyKey.of(value);

            // when & then
            assertThat(key.toString()).isEqualTo(value);
        }
    }
}
