package com.ryuqq.marketplace.domain.saleschannelbrand.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelBrandId Value Object 단위 테스트")
class SalesChannelBrandIdTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 SalesChannelBrandId를 생성한다")
        void createWithValidValue() {
            // given
            Long value = 123L;

            // when
            SalesChannelBrandId id = SalesChannelBrandId.of(value);

            // then
            assertThat(id.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void createWithNullValue_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> SalesChannelBrandId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("forNew()로 새로운 ID를 생성한다")
        void createWithForNew() {
            // when
            SalesChannelBrandId id = SalesChannelBrandId.forNew();

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
            SalesChannelBrandId id = SalesChannelBrandId.forNew();

            // then
            assertThat(id.isNew()).isTrue();
        }

        @Test
        @DisplayName("isNew()는 value가 있으면 false를 반환한다")
        void isNewReturnsFalseWhenValueExists() {
            // given
            SalesChannelBrandId id = SalesChannelBrandId.of(1L);

            // then
            assertThat(id.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 ID는 동등하다")
        void sameValueAreEqual() {
            // given
            SalesChannelBrandId id1 = SalesChannelBrandId.of(100L);
            SalesChannelBrandId id2 = SalesChannelBrandId.of(100L);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 ID는 동등하지 않다")
        void differentValueAreNotEqual() {
            // given
            SalesChannelBrandId id1 = SalesChannelBrandId.of(100L);
            SalesChannelBrandId id2 = SalesChannelBrandId.of(200L);

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("record는 불변 객체이다")
        void recordIsImmutable() {
            // given
            SalesChannelBrandId id = SalesChannelBrandId.of(100L);
            Long originalValue = id.value();

            // then
            assertThat(id.value()).isEqualTo(originalValue);
        }
    }
}
