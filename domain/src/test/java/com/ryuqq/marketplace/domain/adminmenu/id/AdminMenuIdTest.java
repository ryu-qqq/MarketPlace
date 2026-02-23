package com.ryuqq.marketplace.domain.adminmenu.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("AdminMenuId 단위 테스트")
class AdminMenuIdTest {

    @Nested
    @DisplayName("of() - ID 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 값으로 AdminMenuId를 생성한다")
        void of_ValidValue_CreatesId() {
            AdminMenuId id = AdminMenuId.of(1L);

            assertThat(id.value()).isEqualTo(1L);
            assertThat(id.isNew()).isFalse();
        }

        @Test
        @DisplayName("null 값으로 생성 시 IllegalArgumentException을 던진다")
        void of_NullValue_ThrowsException() {
            assertThatThrownBy(() -> AdminMenuId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }

    @Nested
    @DisplayName("forNew() - 신규 ID 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 ID는 value가 null이고 isNew가 true이다")
        void forNew_CreatesNewId() {
            AdminMenuId id = AdminMenuId.forNew();

            assertThat(id.value()).isNull();
            assertThat(id.isNew()).isTrue();
        }
    }
}
