package com.ryuqq.marketplace.domain.adminmenu.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.adminmenu.AdminMenuFixtures;
import com.ryuqq.marketplace.domain.adminmenu.vo.AdminRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("AdminMenu Aggregate 단위 테스트")
class AdminMenuTest {

    @Nested
    @DisplayName("forNew() - 신규 메뉴 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 그룹 메뉴를 생성한다")
        void forNew_GroupMenu_CreatesNewMenu() {
            AdminMenu menu = AdminMenuFixtures.newGroupMenu();

            assertThat(menu.isNew()).isTrue();
            assertThat(menu.parentId()).isNull();
            assertThat(menu.url()).isNull();
            assertThat(menu.isGroup()).isTrue();
            assertThat(menu.isActive()).isTrue();
        }

        @Test
        @DisplayName("신규 아이템 메뉴를 생성한다")
        void forNew_ItemMenu_CreatesNewMenu() {
            AdminMenu menu = AdminMenuFixtures.newItemMenu(1L);

            assertThat(menu.isNew()).isTrue();
            assertThat(menu.parentId()).isEqualTo(1L);
            assertThat(menu.url()).isNotNull();
            assertThat(menu.isGroup()).isFalse();
        }
    }

    @Nested
    @DisplayName("isAccessibleBy() - 역할별 접근 권한 확인")
    class IsAccessibleByTest {

        @Test
        @DisplayName("ADMIN 메뉴에 SUPER_ADMIN은 접근 가능하다")
        void isAccessibleBy_HigherRole_ReturnsTrue() {
            AdminMenu menu = AdminMenuFixtures.activeGroupMenu();

            assertThat(menu.isAccessibleBy(AdminRole.SUPER_ADMIN)).isTrue();
        }

        @Test
        @DisplayName("ADMIN 메뉴에 ADMIN은 접근 가능하다")
        void isAccessibleBy_SameRole_ReturnsTrue() {
            AdminMenu menu = AdminMenuFixtures.activeGroupMenu();

            assertThat(menu.isAccessibleBy(AdminRole.ADMIN)).isTrue();
        }

        @Test
        @DisplayName("ADMIN 메뉴에 VIEWER는 접근 불가하다")
        void isAccessibleBy_LowerRole_ReturnsFalse() {
            AdminMenu menu = AdminMenuFixtures.activeGroupMenu();

            assertThat(menu.isAccessibleBy(AdminRole.VIEWER)).isFalse();
        }
    }

    @Nested
    @DisplayName("isGroup() - 그룹 메뉴 여부")
    class IsGroupTest {

        @Test
        @DisplayName("url이 null이면 그룹 메뉴이다")
        void isGroup_NullUrl_ReturnsTrue() {
            AdminMenu menu = AdminMenuFixtures.activeGroupMenu();
            assertThat(menu.isGroup()).isTrue();
        }

        @Test
        @DisplayName("url이 존재하면 그룹 메뉴가 아니다")
        void isGroup_WithUrl_ReturnsFalse() {
            AdminMenu menu = AdminMenuFixtures.activeItemMenu(2L, 1L);
            assertThat(menu.isGroup()).isFalse();
        }
    }

    @Nested
    @DisplayName("reconstitute() - 기존 메뉴 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("기존 메뉴를 복원한다")
        void reconstitute_ExistingMenu_RestoresAllFields() {
            AdminMenu menu = AdminMenuFixtures.activeGroupMenu();

            assertThat(menu.isNew()).isFalse();
            assertThat(menu.idValue()).isEqualTo(AdminMenuFixtures.DEFAULT_ID);
            assertThat(menu.title()).isEqualTo(AdminMenuFixtures.DEFAULT_TITLE);
            assertThat(menu.iconName()).isEqualTo(AdminMenuFixtures.DEFAULT_ICON_NAME);
            assertThat(menu.displayOrder()).isEqualTo(AdminMenuFixtures.DEFAULT_DISPLAY_ORDER);
            assertThat(menu.requiredRole()).isEqualTo(AdminMenuFixtures.DEFAULT_REQUIRED_ROLE);
            assertThat(menu.createdAt()).isNotNull();
            assertThat(menu.updatedAt()).isNotNull();
        }
    }
}
