package com.ryuqq.marketplace.domain.sellerapplication.id;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerApplicationId 테스트")
class SellerApplicationIdTest {

    @Nested
    @DisplayName("of() - ID 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 값으로 ID를 생성한다")
        void createWithValidValue() {
            SellerApplicationId id = SellerApplicationId.of(1L);
            assertThat(id.value()).isEqualTo(1L);
            assertThat(id.isNew()).isFalse();
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> SellerApplicationId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 ID 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 ID는 value가 null이고 isNew가 true이다")
        void createNewId() {
            SellerApplicationId id = SellerApplicationId.forNew();
            assertThat(id.value()).isNull();
            assertThat(id.isNew()).isTrue();
        }
    }
}
