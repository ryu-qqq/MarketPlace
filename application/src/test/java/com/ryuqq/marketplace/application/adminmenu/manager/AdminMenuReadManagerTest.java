package com.ryuqq.marketplace.application.adminmenu.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.adminmenu.port.out.query.AdminMenuQueryPort;
import com.ryuqq.marketplace.domain.adminmenu.AdminMenuFixtures;
import com.ryuqq.marketplace.domain.adminmenu.aggregate.AdminMenu;
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
@DisplayName("AdminMenuReadManager 단위 테스트")
class AdminMenuReadManagerTest {

    @InjectMocks private AdminMenuReadManager sut;

    @Mock private AdminMenuQueryPort queryPort;

    @Nested
    @DisplayName("findActiveByMaxRoleLevel() - 역할 레벨 이하 메뉴 조회")
    class FindActiveByMaxRoleLevelTest {

        @Test
        @DisplayName("역할 레벨 이하의 활성 메뉴 목록을 반환한다")
        void findActiveByMaxRoleLevel_ValidLevel_ReturnsList() {
            // given
            int roleLevel = 2;
            List<AdminMenu> expected =
                    List.of(
                            AdminMenuFixtures.activeGroupMenu(),
                            AdminMenuFixtures.activeItemMenu(2L, 1L));

            given(queryPort.findActiveByMaxRoleLevel(roleLevel)).willReturn(expected);

            // when
            List<AdminMenu> result = sut.findActiveByMaxRoleLevel(roleLevel);

            // then
            assertThat(result).hasSize(2);
            then(queryPort).should().findActiveByMaxRoleLevel(roleLevel);
        }

        @Test
        @DisplayName("해당 레벨의 메뉴가 없으면 빈 목록을 반환한다")
        void findActiveByMaxRoleLevel_NoMenus_ReturnsEmptyList() {
            // given
            int roleLevel = 0;
            given(queryPort.findActiveByMaxRoleLevel(roleLevel)).willReturn(List.of());

            // when
            List<AdminMenu> result = sut.findActiveByMaxRoleLevel(roleLevel);

            // then
            assertThat(result).isEmpty();
            then(queryPort).should().findActiveByMaxRoleLevel(roleLevel);
        }
    }
}
