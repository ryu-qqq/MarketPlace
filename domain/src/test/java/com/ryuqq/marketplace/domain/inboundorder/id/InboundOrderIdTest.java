package com.ryuqq.marketplace.domain.inboundorder.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundOrderId 단위 테스트")
class InboundOrderIdTest {

    @Nested
    @DisplayName("of() - ID 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 Long 값으로 ID를 생성한다")
        void createWithValidValue() {
            InboundOrderId id = InboundOrderId.of(1L);

            assertThat(id.value()).isEqualTo(1L);
            assertThat(id.isNew()).isFalse();
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> InboundOrderId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 ID 생성")
    class ForNewTest {

        @Test
        @DisplayName("forNew는 null value를 가진 신규 ID를 생성한다")
        void createForNew() {
            InboundOrderId id = InboundOrderId.forNew();

            assertThat(id.value()).isNull();
            assertThat(id.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("동등성 검증")
    class EqualityTest {

        @Test
        @DisplayName("동일한 값의 InboundOrderId는 같다")
        void sameValuesAreEqual() {
            InboundOrderId id1 = InboundOrderId.of(1L);
            InboundOrderId id2 = InboundOrderId.of(1L);

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값의 InboundOrderId는 같지 않다")
        void differentValuesAreNotEqual() {
            InboundOrderId id1 = InboundOrderId.of(1L);
            InboundOrderId id2 = InboundOrderId.of(2L);

            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
