package com.ryuqq.marketplace.domain.shop.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShopId Value Object 단위 테스트")
class ShopIdTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("of()로 ShopId를 생성한다")
        void createWithOf() {
            // when
            ShopId shopId = ShopId.of(123L);

            // then
            assertThat(shopId.value()).isEqualTo(123L);
            assertThat(shopId.isNew()).isFalse();
        }

        @Test
        @DisplayName("of()에 null을 전달하면 예외를 발생시킨다")
        void ofWithNullThrowsException() {
            // when & then
            assertThatThrownBy(() -> ShopId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ShopId")
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("forNew()로 새로운 Shop용 ID를 생성한다")
        void createWithForNew() {
            // when
            ShopId shopId = ShopId.forNew();

            // then
            assertThat(shopId.value()).isNull();
            assertThat(shopId.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("상태 확인 테스트")
    class StateCheckTest {

        @Test
        @DisplayName("isNew()는 value가 null이면 true를 반환한다")
        void isNewReturnsTrueWhenValueIsNull() {
            // given
            ShopId shopId = ShopId.forNew();

            // then
            assertThat(shopId.isNew()).isTrue();
        }

        @Test
        @DisplayName("isNew()는 value가 있으면 false를 반환한다")
        void isNewReturnsFalseWhenValueExists() {
            // given
            ShopId shopId = ShopId.of(1L);

            // then
            assertThat(shopId.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 ShopId는 동등하다")
        void sameValueEquals() {
            // given
            ShopId shopId1 = ShopId.of(100L);
            ShopId shopId2 = ShopId.of(100L);

            // then
            assertThat(shopId1).isEqualTo(shopId2);
            assertThat(shopId1.hashCode()).isEqualTo(shopId2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 ShopId는 동등하지 않다")
        void differentValueNotEquals() {
            // given
            ShopId shopId1 = ShopId.of(100L);
            ShopId shopId2 = ShopId.of(200L);

            // then
            assertThat(shopId1).isNotEqualTo(shopId2);
        }

        @Test
        @DisplayName("forNew()로 생성한 ShopId는 서로 동등하다")
        void forNewInstancesAreEqual() {
            // given
            ShopId shopId1 = ShopId.forNew();
            ShopId shopId2 = ShopId.forNew();

            // then (record는 값 기반 동등성이므로 둘 다 null 값으로 동등함)
            assertThat(shopId1).isEqualTo(shopId2);
            assertThat(shopId1.hashCode()).isEqualTo(shopId2.hashCode());
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("ShopId는 record이므로 불변이다")
        void shopIdIsImmutable() {
            // given
            ShopId shopId = ShopId.of(100L);
            Long originalValue = shopId.value();

            // when (record는 setter가 없으므로 값 변경 불가)
            // then
            assertThat(shopId.value()).isEqualTo(originalValue);
        }
    }
}
