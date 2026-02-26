package com.ryuqq.marketplace.domain.seller.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerId Value Object 테스트")
class SellerIdTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("of()로 SellerId를 생성한다")
        void createWithOf() {
            // when
            SellerId sellerId = SellerId.of(123L);

            // then
            assertThat(sellerId.value()).isEqualTo(123L);
        }

        @Test
        @DisplayName("of()에 null을 전달하면 예외를 발생시킨다")
        void ofWithNullThrowsException() {
            // when & then
            assertThatThrownBy(() -> SellerId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("forNew()로 새로운 셀러용 ID를 생성한다")
        void createWithForNew() {
            // when
            SellerId sellerId = SellerId.forNew();

            // then
            assertThat(sellerId.value()).isNull();
            assertThat(sellerId.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("상태 확인 테스트")
    class StateCheckTest {

        @Test
        @DisplayName("isNew()는 value가 null이면 true를 반환한다")
        void isNewReturnsTrueWhenValueIsNull() {
            // given
            SellerId sellerId = SellerId.forNew();

            // then
            assertThat(sellerId.isNew()).isTrue();
        }

        @Test
        @DisplayName("isNew()는 value가 있으면 false를 반환한다")
        void isNewReturnsFalseWhenValueExists() {
            // given
            SellerId sellerId = SellerId.of(1L);

            // then
            assertThat(sellerId.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 SellerId는 동등하다")
        void sameValueEquals() {
            // given
            SellerId sellerId1 = SellerId.of(100L);
            SellerId sellerId2 = SellerId.of(100L);

            // then
            assertThat(sellerId1).isEqualTo(sellerId2);
            assertThat(sellerId1.hashCode()).isEqualTo(sellerId2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 SellerId는 동등하지 않다")
        void differentValueNotEquals() {
            // given
            SellerId sellerId1 = SellerId.of(100L);
            SellerId sellerId2 = SellerId.of(200L);

            // then
            assertThat(sellerId1).isNotEqualTo(sellerId2);
        }
    }
}
