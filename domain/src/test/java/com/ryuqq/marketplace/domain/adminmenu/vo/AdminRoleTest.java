package com.ryuqq.marketplace.domain.adminmenu.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("AdminRole 단위 테스트")
class AdminRoleTest {

    @Nested
    @DisplayName("canAccess() - 역할 접근 권한 확인")
    class CanAccessTest {

        @Test
        @DisplayName("SUPER_ADMIN은 모든 역할에 접근 가능하다")
        void canAccess_SuperAdmin_AccessAll() {
            assertThat(AdminRole.SUPER_ADMIN.canAccess(AdminRole.SUPER_ADMIN)).isTrue();
            assertThat(AdminRole.SUPER_ADMIN.canAccess(AdminRole.ADMIN)).isTrue();
            assertThat(AdminRole.SUPER_ADMIN.canAccess(AdminRole.EDITOR)).isTrue();
            assertThat(AdminRole.SUPER_ADMIN.canAccess(AdminRole.VIEWER)).isTrue();
        }

        @Test
        @DisplayName("VIEWER는 VIEWER만 접근 가능하다")
        void canAccess_Viewer_OnlyViewer() {
            assertThat(AdminRole.VIEWER.canAccess(AdminRole.VIEWER)).isTrue();
            assertThat(AdminRole.VIEWER.canAccess(AdminRole.EDITOR)).isFalse();
            assertThat(AdminRole.VIEWER.canAccess(AdminRole.ADMIN)).isFalse();
            assertThat(AdminRole.VIEWER.canAccess(AdminRole.SUPER_ADMIN)).isFalse();
        }

        @Test
        @DisplayName("ADMIN은 ADMIN 이하만 접근 가능하다")
        void canAccess_Admin_AdminAndBelow() {
            assertThat(AdminRole.ADMIN.canAccess(AdminRole.ADMIN)).isTrue();
            assertThat(AdminRole.ADMIN.canAccess(AdminRole.EDITOR)).isTrue();
            assertThat(AdminRole.ADMIN.canAccess(AdminRole.VIEWER)).isTrue();
            assertThat(AdminRole.ADMIN.canAccess(AdminRole.SUPER_ADMIN)).isFalse();
        }
    }

    @Nested
    @DisplayName("fromName() - 문자열 → enum 변환")
    class FromNameTest {

        @Test
        @DisplayName("유효한 역할명으로 AdminRole을 반환한다")
        void fromName_ValidName_ReturnsRole() {
            assertThat(AdminRole.fromName("SUPER_ADMIN")).isEqualTo(AdminRole.SUPER_ADMIN);
            assertThat(AdminRole.fromName("ADMIN")).isEqualTo(AdminRole.ADMIN);
            assertThat(AdminRole.fromName("EDITOR")).isEqualTo(AdminRole.EDITOR);
            assertThat(AdminRole.fromName("VIEWER")).isEqualTo(AdminRole.VIEWER);
        }

        @Test
        @DisplayName("잘못된 역할명은 IllegalArgumentException을 던진다")
        void fromName_InvalidName_ThrowsException() {
            assertThatThrownBy(() -> AdminRole.fromName("UNKNOWN"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("알 수 없는 AdminRole");
        }
    }

    @Nested
    @DisplayName("fromLevel() - 레벨 → enum 변환")
    class FromLevelTest {

        @Test
        @DisplayName("유효한 레벨로 AdminRole을 반환한다")
        void fromLevel_ValidLevel_ReturnsRole() {
            assertThat(AdminRole.fromLevel(0)).isEqualTo(AdminRole.VIEWER);
            assertThat(AdminRole.fromLevel(1)).isEqualTo(AdminRole.EDITOR);
            assertThat(AdminRole.fromLevel(2)).isEqualTo(AdminRole.ADMIN);
            assertThat(AdminRole.fromLevel(3)).isEqualTo(AdminRole.SUPER_ADMIN);
        }

        @Test
        @DisplayName("잘못된 레벨은 IllegalArgumentException을 던진다")
        void fromLevel_InvalidLevel_ThrowsException() {
            assertThatThrownBy(() -> AdminRole.fromLevel(99))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("알 수 없는 AdminRole level");
        }
    }

    @Nested
    @DisplayName("level() - 레벨 값 반환")
    class LevelTest {

        @Test
        @DisplayName("각 역할의 레벨 값이 올바르다")
        void level_ReturnsCorrectLevel() {
            assertThat(AdminRole.VIEWER.level()).isZero();
            assertThat(AdminRole.EDITOR.level()).isEqualTo(1);
            assertThat(AdminRole.ADMIN.level()).isEqualTo(2);
            assertThat(AdminRole.SUPER_ADMIN.level()).isEqualTo(3);
        }
    }
}
