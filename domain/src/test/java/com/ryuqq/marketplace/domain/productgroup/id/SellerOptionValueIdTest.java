package com.ryuqq.marketplace.domain.productgroup.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerOptionValueId Value Object 단위 테스트")
class SellerOptionValueIdTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("of()로 SellerOptionValueId를 생성한다")
        void createWithOf() {
            // when
            SellerOptionValueId id = SellerOptionValueId.of(123L);

            // then
            assertThat(id.value()).isEqualTo(123L);
        }

        @Test
        @DisplayName("of()에 null을 전달하면 예외를 발생시킨다")
        void ofWithNullThrowsException() {
            // when & then
            assertThatThrownBy(() -> SellerOptionValueId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("forNew()로 새로운 SellerOptionValueId를 생성한다")
        void createWithForNew() {
            // when
            SellerOptionValueId id = SellerOptionValueId.forNew();

            // then
            assertThat(id.value()).isNull();
            assertThat(id.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("상태 확인 테스트")
    class StateCheckTest {

        @Test
        @DisplayName("isNew()는 value가 null이면 true를 반환한다")
        void isNewReturnsTrueWhenValueIsNull() {
            // given
            SellerOptionValueId id = SellerOptionValueId.forNew();

            // then
            assertThat(id.isNew()).isTrue();
        }

        @Test
        @DisplayName("isNew()는 value가 있으면 false를 반환한다")
        void isNewReturnsFalseWhenValueExists() {
            // given
            SellerOptionValueId id = SellerOptionValueId.of(1L);

            // then
            assertThat(id.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 SellerOptionValueId는 동등하다")
        void sameValueEquals() {
            // given
            SellerOptionValueId id1 = SellerOptionValueId.of(100L);
            SellerOptionValueId id2 = SellerOptionValueId.of(100L);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 SellerOptionValueId는 동등하지 않다")
        void differentValueNotEquals() {
            // given
            SellerOptionValueId id1 = SellerOptionValueId.of(100L);
            SellerOptionValueId id2 = SellerOptionValueId.of(200L);

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
