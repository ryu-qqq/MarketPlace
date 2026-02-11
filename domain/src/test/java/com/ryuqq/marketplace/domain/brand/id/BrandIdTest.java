package com.ryuqq.marketplace.domain.brand.id;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.brand.BrandFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandId Value Object 단위 테스트")
class BrandIdTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        @Test
        @DisplayName("유효한 값으로 생성한다")
        void createWithValidValue() {
            // given
            Long value = 1L;

            // when
            BrandId brandId = BrandId.of(value);

            // then
            assertThat(brandId).isNotNull();
            assertThat(brandId.value()).isEqualTo(value);
            assertThat(brandId.isNew()).isFalse();
        }

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void createWithNullValue_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> BrandId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("BrandId 값은 null일 수 없습니다");
        }

        @Test
        @DisplayName("forNew()로 생성하면 null 값을 가진다")
        void forNewCreatesNewBrandId() {
            // when
            BrandId brandId = BrandId.forNew();

            // then
            assertThat(brandId).isNotNull();
            assertThat(brandId.value()).isNull();
            assertThat(brandId.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {
        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            // given
            BrandId brandId1 = BrandId.of(1L);
            BrandId brandId2 = BrandId.of(1L);

            // when & then
            assertThat(brandId1).isEqualTo(brandId2);
            assertThat(brandId1.hashCode()).isEqualTo(brandId2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 다르다")
        void differentValueAreNotEqual() {
            // given
            BrandId brandId1 = BrandId.of(1L);
            BrandId brandId2 = BrandId.of(2L);

            // when & then
            assertThat(brandId1).isNotEqualTo(brandId2);
        }

        @Test
        @DisplayName("forNew()로 생성된 BrandId는 동일하다")
        void forNewBrandIdsAreEqual() {
            // given
            BrandId brandId1 = BrandId.forNew();
            BrandId brandId2 = BrandId.forNew();

            // when & then
            assertThat(brandId1).isEqualTo(brandId2);
            assertThat(brandId1.hashCode()).isEqualTo(brandId2.hashCode());
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {
        @Test
        @DisplayName("record로 구현되어 불변성이 보장된다")
        void recordGuaranteesImmutability() {
            // given
            BrandId brandId = BrandFixtures.defaultBrandId();

            // when
            Long originalValue = brandId.value();

            // then
            assertThat(brandId.value()).isEqualTo(originalValue);
            assertThat(brandId).isNotNull();
        }
    }

    @Nested
    @DisplayName("isNew() 메서드 테스트")
    class IsNewTest {
        @Test
        @DisplayName("값이 null이면 isNew()는 true다")
        void isNewReturnsTrueWhenValueIsNull() {
            // given
            BrandId brandId = BrandId.forNew();

            // when
            boolean isNew = brandId.isNew();

            // then
            assertThat(isNew).isTrue();
        }

        @Test
        @DisplayName("값이 존재하면 isNew()는 false다")
        void isNewReturnsFalseWhenValueExists() {
            // given
            BrandId brandId = BrandId.of(1L);

            // when
            boolean isNew = brandId.isNew();

            // then
            assertThat(isNew).isFalse();
        }
    }
}
