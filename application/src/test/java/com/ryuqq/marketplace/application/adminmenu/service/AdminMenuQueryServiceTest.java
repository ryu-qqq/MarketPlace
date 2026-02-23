package com.ryuqq.marketplace.application.adminmenu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.adminmenu.manager.AdminMenuReadManager;
import com.ryuqq.marketplace.domain.adminmenu.AdminMenuFixtures;
import com.ryuqq.marketplace.domain.adminmenu.aggregate.AdminMenu;
import com.ryuqq.marketplace.domain.adminmenu.vo.AdminRole;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminMenuQueryService 단위 테스트")
class AdminMenuQueryServiceTest {

    @InjectMocks private AdminMenuQueryService sut;

    @Mock private AdminMenuReadManager readManager;

    @Nested
    @DisplayName("execute() - 역할별 접근 가능 메뉴 조회")
    class ExecuteTest {

        @Test
        @DisplayName("ADMIN 역할로 접근 가능한 메뉴를 반환한다")
        void execute_AdminRole_ReturnsAccessibleMenus() {
            // given
            AdminRole role = AdminRole.ADMIN;
            List<AdminMenu> expected =
                    List.of(
                            AdminMenuFixtures.activeGroupMenu(),
                            AdminMenuFixtures.activeItemMenu(2L, 1L));

            given(readManager.findActiveByMaxRoleLevel(role.level())).willReturn(expected);

            // when
            List<AdminMenu> result = sut.execute(role);

            // then
            assertThat(result).hasSize(2);
            then(readManager).should().findActiveByMaxRoleLevel(role.level());
        }

        @Test
        @DisplayName("VIEWER 역할로 조회 시 해당 레벨 이하 메뉴만 반환한다")
        void execute_ViewerRole_ReturnsViewerMenusOnly() {
            // given
            AdminRole role = AdminRole.VIEWER;
            given(readManager.findActiveByMaxRoleLevel(role.level())).willReturn(List.of());

            // when
            List<AdminMenu> result = sut.execute(role);

            // then
            assertThat(result).isEmpty();
            then(readManager).should().findActiveByMaxRoleLevel(0);
        }

        @Test
        @DisplayName("SUPER_ADMIN 역할로 모든 메뉴를 반환한다")
        void execute_SuperAdminRole_ReturnsAllMenus() {
            // given
            AdminRole role = AdminRole.SUPER_ADMIN;
            List<AdminMenu> allMenus =
                    List.of(
                            AdminMenuFixtures.activeGroupMenu(),
                            AdminMenuFixtures.activeItemMenu(2L, 1L),
                            AdminMenuFixtures.superAdminItemMenu(3L, 1L));

            given(readManager.findActiveByMaxRoleLevel(role.level())).willReturn(allMenus);

            // when
            List<AdminMenu> result = sut.execute(role);

            // then
            assertThat(result).hasSize(3);
            then(readManager).should().findActiveByMaxRoleLevel(3);
        }
    }
}
