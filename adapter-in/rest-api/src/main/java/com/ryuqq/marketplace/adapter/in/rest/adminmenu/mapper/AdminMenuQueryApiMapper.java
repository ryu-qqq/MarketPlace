package com.ryuqq.marketplace.adapter.in.rest.adminmenu.mapper;

import com.ryuqq.marketplace.adapter.in.rest.adminmenu.dto.response.AdminMenuApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.adminmenu.dto.response.AdminMenuApiResponse.MenuGroup;
import com.ryuqq.marketplace.adapter.in.rest.adminmenu.dto.response.AdminMenuApiResponse.MenuItem;
import com.ryuqq.marketplace.domain.adminmenu.aggregate.AdminMenu;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * AdminMenuQueryApiMapper - Admin 메뉴 Query API 변환 매퍼.
 *
 * <p>flat List&lt;AdminMenu&gt; → 트리 AdminMenuApiResponse 변환.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-005: 순수 변환 로직만.
 */
@Component
public class AdminMenuQueryApiMapper {

    /**
     * flat 메뉴 목록을 트리 구조 응답으로 변환.
     *
     * <p>parentId == null → 그룹, parentId != null → 아이템. 하위 아이템이 0개인 그룹은 제외.
     *
     * @param menus flat 메뉴 목록 (parent_id NULLS FIRST, display_order 정렬)
     * @return 트리 구조 응답
     */
    public AdminMenuApiResponse toTreeResponse(List<AdminMenu> menus) {
        Map<Long, AdminMenu> groupMap = new LinkedHashMap<>();
        Map<Long, List<AdminMenu>> childrenMap = new LinkedHashMap<>();

        for (AdminMenu menu : menus) {
            if (menu.parentId() == null) {
                groupMap.put(menu.idValue(), menu);
                childrenMap.putIfAbsent(menu.idValue(), new ArrayList<>());
            } else {
                childrenMap.computeIfAbsent(menu.parentId(), k -> new ArrayList<>()).add(menu);
            }
        }

        List<MenuGroup> menuGroups = new ArrayList<>();
        for (Map.Entry<Long, AdminMenu> entry : groupMap.entrySet()) {
            Long groupId = entry.getKey();
            AdminMenu group = entry.getValue();
            List<AdminMenu> children = childrenMap.getOrDefault(groupId, List.of());

            if (children.isEmpty()) {
                continue;
            }

            List<MenuItem> items =
                    children.stream()
                            .map(
                                    child ->
                                            new MenuItem(
                                                    child.title(), child.url(), child.iconName()))
                            .toList();

            menuGroups.add(new MenuGroup(group.title(), group.iconName(), items));
        }

        return new AdminMenuApiResponse(menuGroups);
    }
}
