package com.ryuqq.marketplace.adapter.in.rest.adminmenu.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * AdminMenuApiResponse - Admin 메뉴 트리 응답 DTO.
 *
 * <p>API-DTO-001: Record 타입 필수.
 *
 * <p>API-DTO-007: @Schema 어노테이션.
 */
@Schema(description = "Admin 메뉴 트리 응답 DTO")
public record AdminMenuApiResponse(@Schema(description = "메뉴 그룹 목록") List<MenuGroup> menus) {

    public AdminMenuApiResponse {
        menus = List.copyOf(menus);
    }

    @Schema(description = "메뉴 그룹 (최상위)")
    public record MenuGroup(
            @Schema(description = "그룹 제목", example = "판매자 관리") String title,
            @Schema(description = "아이콘명", example = "Users") String iconName,
            @Schema(description = "하위 메뉴 항목") List<MenuItem> items) {

        public MenuGroup {
            items = List.copyOf(items);
        }
    }

    @Schema(description = "메뉴 항목")
    public record MenuItem(
            @Schema(description = "메뉴 제목", example = "판매자 입점 관리") String title,
            @Schema(description = "URL 경로", example = "/seller/management") String url,
            @Schema(description = "아이콘명", example = "UserCheck") String iconName) {}
}
