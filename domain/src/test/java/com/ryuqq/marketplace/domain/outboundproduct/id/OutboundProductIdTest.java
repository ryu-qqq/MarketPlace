package com.ryuqq.marketplace.domain.outboundproduct.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OutboundProductId Value Object 단위 테스트")
class OutboundProductIdTest {

    @Nested
    @DisplayName("of() 팩토리 테스트")
    class OfTest {

        @Test
        @DisplayName("양수 값으로 OutboundProductId를 생성한다")
        void createWithPositiveValue() {
            OutboundProductId id = OutboundProductId.of(1L);

            assertThat(id.value()).isEqualTo(1L);
            assertThat(id.isNew()).isFalse();
        }

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void createWithNullThrowsException() {
            assertThatThrownBy(() -> OutboundProductId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }

    @Nested
    @DisplayName("forNew() 팩토리 테스트")
    class ForNewTest {

        @Test
        @DisplayName("forNew()는 null value를 가진 신규 ID를 반환한다")
        void forNewReturnsNullValue() {
            OutboundProductId id = OutboundProductId.forNew();

            assertThat(id.value()).isNull();
            assertThat(id.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("isNew() 테스트")
    class IsNewTest {

        @Test
        @DisplayName("value가 null이면 신규이다")
        void isNewWhenValueIsNull() {
            OutboundProductId id = OutboundProductId.forNew();

            assertThat(id.isNew()).isTrue();
        }

        @Test
        @DisplayName("value가 있으면 신규가 아니다")
        void isNotNewWhenValueIsPresent() {
            OutboundProductId id = OutboundProductId.of(100L);

            assertThat(id.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 value를 가진 ID는 동일하다")
        void sameValueIsEqual() {
            OutboundProductId a = OutboundProductId.of(1L);
            OutboundProductId b = OutboundProductId.of(1L);

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("다른 value를 가진 ID는 동일하지 않다")
        void differentValueIsNotEqual() {
            OutboundProductId a = OutboundProductId.of(1L);
            OutboundProductId b = OutboundProductId.of(2L);

            assertThat(a).isNotEqualTo(b);
        }

        @Test
        @DisplayName("forNew()로 생성한 두 ID는 동일하다")
        void twoForNewIdsAreEqual() {
            OutboundProductId a = OutboundProductId.forNew();
            OutboundProductId b = OutboundProductId.forNew();

            assertThat(a).isEqualTo(b);
        }
    }
}
