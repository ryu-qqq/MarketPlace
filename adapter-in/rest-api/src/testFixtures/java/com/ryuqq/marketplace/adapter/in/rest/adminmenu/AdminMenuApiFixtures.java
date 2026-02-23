package com.ryuqq.marketplace.adapter.in.rest.adminmenu;

import com.ryuqq.marketplace.adapter.in.rest.adminmenu.dto.response.AdminMenuApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.adminmenu.dto.response.AdminMenuApiResponse.MenuGroup;
import com.ryuqq.marketplace.adapter.in.rest.adminmenu.dto.response.AdminMenuApiResponse.MenuItem;
import java.util.List;

/**
 * AdminMenu API 테스트 Fixtures.
 *
 * <p>AdminMenu REST API 테스트에서 사용하는 응답 객체를 생성합니다.
 */
public final class AdminMenuApiFixtures {

    private AdminMenuApiFixtures() {}

    /** 판매자 관리 그룹 + 아이템 2개 포함 트리 응답. */
    public static AdminMenuApiResponse treeResponse() {
        return new AdminMenuApiResponse(
                List.of(
                        new MenuGroup(
                                "판매자 관리",
                                "Users",
                                List.of(
                                        new MenuItem(
                                                "판매자 입점 관리", "/seller/application", "UserCheck"),
                                        new MenuItem(
                                                "판매자 정보 관리", "/seller/management", "UserCog")))));
    }

    /** 여러 그룹 포함 트리 응답. */
    public static AdminMenuApiResponse multiGroupTreeResponse() {
        return new AdminMenuApiResponse(
                List.of(
                        new MenuGroup(
                                "판매자 관리",
                                "Users",
                                List.of(
                                        new MenuItem(
                                                "판매자 입점 관리", "/seller/application", "UserCheck"))),
                        new MenuGroup(
                                "주문 관리",
                                "ShoppingCart",
                                List.of(
                                        new MenuItem("통합주문 관리", "/order/list", "ClipboardList"),
                                        new MenuItem("발송/배송 관리", "/order/shipping", "Truck")))));
    }

    /** 빈 트리 응답. */
    public static AdminMenuApiResponse emptyTreeResponse() {
        return new AdminMenuApiResponse(List.of());
    }
}
