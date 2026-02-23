package com.ryuqq.marketplace.adapter.in.rest.adminmenu.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.adminmenu.dto.response.AdminMenuApiResponse;
import com.ryuqq.marketplace.domain.adminmenu.AdminMenuFixtures;
import com.ryuqq.marketplace.domain.adminmenu.aggregate.AdminMenu;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("AdminMenuQueryApiMapper 단위 테스트")
class AdminMenuQueryApiMapperTest {

    private AdminMenuQueryApiMapper sut;

    @BeforeEach
    void setUp() {
        sut = new AdminMenuQueryApiMapper();
    }

    @Nested
    @DisplayName("toTreeResponse() - flat 메뉴 → 트리 변환")
    class ToTreeResponseTest {

        @Test
        @DisplayName("그룹과 아이템을 트리 구조로 변환한다")
        void toTreeResponse_GroupWithItems_ReturnsTree() {
            // given
            AdminMenu group = AdminMenuFixtures.activeGroupMenu();
            AdminMenu item1 = AdminMenuFixtures.activeItemMenu(2L, group.idValue());
            AdminMenu item2 = AdminMenuFixtures.viewerItemMenu(3L, group.idValue());

            // when
            AdminMenuApiResponse result = sut.toTreeResponse(List.of(group, item1, item2));

            // then
            assertThat(result.menus()).hasSize(1);
            assertThat(result.menus().get(0).title()).isEqualTo(group.title());
            assertThat(result.menus().get(0).iconName()).isEqualTo(group.iconName());
            assertThat(result.menus().get(0).items()).hasSize(2);
        }

        @Test
        @DisplayName("하위 아이템이 없는 그룹은 제외한다")
        void toTreeResponse_EmptyGroup_ExcludesGroup() {
            // given
            AdminMenu group = AdminMenuFixtures.activeGroupMenu();

            // when
            AdminMenuApiResponse result = sut.toTreeResponse(List.of(group));

            // then
            assertThat(result.menus()).isEmpty();
        }

        @Test
        @DisplayName("빈 목록이면 빈 응답을 반환한다")
        void toTreeResponse_EmptyList_ReturnsEmptyResponse() {
            // when
            AdminMenuApiResponse result = sut.toTreeResponse(List.of());

            // then
            assertThat(result.menus()).isEmpty();
        }

        @Test
        @DisplayName("아이템 메뉴의 title, url, iconName이 매핑된다")
        void toTreeResponse_ItemFields_MappedCorrectly() {
            // given
            AdminMenu group = AdminMenuFixtures.activeGroupMenu();
            AdminMenu item = AdminMenuFixtures.activeItemMenu(2L, group.idValue());

            // when
            AdminMenuApiResponse result = sut.toTreeResponse(List.of(group, item));

            // then
            AdminMenuApiResponse.MenuItem menuItem = result.menus().get(0).items().get(0);
            assertThat(menuItem.title()).isEqualTo(item.title());
            assertThat(menuItem.url()).isEqualTo(item.url());
            assertThat(menuItem.iconName()).isEqualTo(item.iconName());
        }

        @Test
        @DisplayName("여러 그룹이 있으면 모두 변환한다")
        void toTreeResponse_MultipleGroups_ReturnsAllGroups() {
            // given
            AdminMenu group1 = AdminMenuFixtures.activeGroupMenu();
            AdminMenu item1 = AdminMenuFixtures.activeItemMenu(2L, group1.idValue());

            AdminMenu group2 = AdminMenuFixtures.viewerItemMenu(10L, null);

            // group2는 parentId=null이 아니므로 그룹이 아님 → 별도 그룹 메뉴 생성
            // 실제로는 viewerItemMenu는 parentId가 있으므로 아이템으로 취급
            // 순수 그룹 테스트를 위해 직접 생성
            AdminMenu orderGroup =
                    com.ryuqq.marketplace.domain.adminmenu.aggregate.AdminMenu.reconstitute(
                            com.ryuqq.marketplace.domain.adminmenu.id.AdminMenuId.of(20L),
                            null,
                            "주문 관리",
                            null,
                            "ShoppingCart",
                            1,
                            com.ryuqq.marketplace.domain.adminmenu.vo.AdminRole.VIEWER,
                            true,
                            java.time.Instant.now(),
                            java.time.Instant.now());
            AdminMenu orderItem = AdminMenuFixtures.viewerItemMenu(21L, 20L);

            // when
            AdminMenuApiResponse result =
                    sut.toTreeResponse(List.of(group1, item1, orderGroup, orderItem));

            // then
            assertThat(result.menus()).hasSize(2);
        }
    }
}
