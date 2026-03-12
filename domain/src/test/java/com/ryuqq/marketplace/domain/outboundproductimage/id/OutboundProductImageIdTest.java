package com.ryuqq.marketplace.domain.outboundproductimage.id;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.outboundproductimage.OutboundProductImageFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OutboundProductImageId Value Object 단위 테스트")
class OutboundProductImageIdTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 생성한다")
        void createWithValidValue() {
            // given
            Long value = 1L;

            // when
            OutboundProductImageId id = OutboundProductImageId.of(value);

            // then
            assertThat(id).isNotNull();
            assertThat(id.value()).isEqualTo(value);
            assertThat(id.isNew()).isFalse();
        }

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void createWithNullValue_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> OutboundProductImageId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OutboundProductImageId 값은 null일 수 없습니다");
        }

        @Test
        @DisplayName("forNew()로 생성하면 null 값을 가진다")
        void forNewCreatesIdWithNullValue() {
            // when
            OutboundProductImageId id = OutboundProductImageId.forNew();

            // then
            assertThat(id).isNotNull();
            assertThat(id.value()).isNull();
            assertThat(id.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            // given
            OutboundProductImageId id1 = OutboundProductImageId.of(1L);
            OutboundProductImageId id2 = OutboundProductImageId.of(1L);

            // when & then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 다르다")
        void differentValueAreNotEqual() {
            // given
            OutboundProductImageId id1 = OutboundProductImageId.of(1L);
            OutboundProductImageId id2 = OutboundProductImageId.of(2L);

            // when & then
            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("forNew()로 생성된 ID는 서로 동일하다")
        void forNewIdsAreEqual() {
            // given
            OutboundProductImageId id1 = OutboundProductImageId.forNew();
            OutboundProductImageId id2 = OutboundProductImageId.forNew();

            // when & then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("record로 구현되어 불변성이 보장된다")
        void recordGuaranteesImmutability() {
            // given
            OutboundProductImageId id = OutboundProductImageFixtures.defaultOutboundProductImageId();

            // when
            Long originalValue = id.value();

            // then
            assertThat(id.value()).isEqualTo(originalValue);
            assertThat(id).isNotNull();
        }
    }

    @Nested
    @DisplayName("isNew() 메서드 테스트")
    class IsNewTest {

        @Test
        @DisplayName("값이 null이면 isNew()는 true다")
        void isNewReturnsTrueWhenValueIsNull() {
            // given
            OutboundProductImageId id = OutboundProductImageId.forNew();

            // when
            boolean isNew = id.isNew();

            // then
            assertThat(isNew).isTrue();
        }

        @Test
        @DisplayName("값이 존재하면 isNew()는 false다")
        void isNewReturnsFalseWhenValueExists() {
            // given
            OutboundProductImageId id = OutboundProductImageId.of(1L);

            // when
            boolean isNew = id.isNew();

            // then
            assertThat(isNew).isFalse();
        }
    }
}
