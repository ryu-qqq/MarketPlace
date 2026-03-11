package com.ryuqq.marketplace.domain.order.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExternalOrderReference Value Object 테스트")
class ExternalOrderReferenceTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 ExternalOrderReference를 생성한다")
        void createWithValidValues() {
            // given
            Instant orderedAt = CommonVoFixtures.yesterday();

            // when
            ExternalOrderReference ref =
                    ExternalOrderReference.of(1L, 10L, "NAVER", "네이버", "EXT-ORDER-001", orderedAt);

            // then
            assertThat(ref.salesChannelId()).isEqualTo(1L);
            assertThat(ref.shopId()).isEqualTo(10L);
            assertThat(ref.shopCode()).isEqualTo("NAVER");
            assertThat(ref.shopName()).isEqualTo("네이버");
            assertThat(ref.externalOrderNo()).isEqualTo("EXT-ORDER-001");
            assertThat(ref.externalOrderedAt()).isEqualTo(orderedAt);
        }

        @Test
        @DisplayName("외부 주문번호가 null이면 예외가 발생한다")
        void createWithNullExternalOrderNo_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    ExternalOrderReference.of(
                                            1L,
                                            10L,
                                            null,
                                            null,
                                            null,
                                            CommonVoFixtures.yesterday()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("외부 주문번호는 필수");
        }

        @Test
        @DisplayName("외부 주문번호가 빈 문자열이면 예외가 발생한다")
        void createWithBlankExternalOrderNo_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    ExternalOrderReference.of(
                                            1L,
                                            10L,
                                            null,
                                            null,
                                            "   ",
                                            CommonVoFixtures.yesterday()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("외부 주문번호는 필수");
        }

        @Test
        @DisplayName("외부 주문시간이 null이면 예외가 발생한다")
        void createWithNullExternalOrderedAt_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    ExternalOrderReference.of(
                                            1L, 10L, null, null, "EXT-ORDER-001", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("외부 주문시간은 필수");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값의 ExternalOrderReference는 동일하다")
        void sameValuesAreEqual() {
            // given
            Instant orderedAt = CommonVoFixtures.yesterday();

            // when
            ExternalOrderReference ref1 =
                    ExternalOrderReference.of(1L, 10L, null, null, "EXT-ORDER-001", orderedAt);
            ExternalOrderReference ref2 =
                    ExternalOrderReference.of(1L, 10L, null, null, "EXT-ORDER-001", orderedAt);

            // then
            assertThat(ref1).isEqualTo(ref2);
            assertThat(ref1.hashCode()).isEqualTo(ref2.hashCode());
        }
    }
}
