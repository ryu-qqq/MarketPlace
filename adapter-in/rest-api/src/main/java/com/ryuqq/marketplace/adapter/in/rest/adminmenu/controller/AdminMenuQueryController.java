package com.ryuqq.marketplace.adapter.in.rest.adminmenu.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.adminmenu.AdminMenuAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.adminmenu.dto.response.AdminMenuApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.adminmenu.mapper.AdminMenuQueryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.application.adminmenu.port.in.query.GetAccessibleMenusUseCase;
import com.ryuqq.marketplace.domain.adminmenu.aggregate.AdminMenu;
import com.ryuqq.marketplace.domain.adminmenu.vo.AdminRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AdminMenuQueryController - Admin 메뉴 조회 API.
 *
 * <p>인증된 사용자의 역할에 따라 접근 가능한 메뉴를 트리 형태로 반환합니다.
 *
 * <p>API-CTR-001: @RestController 어노테이션 필수.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-004: ResponseEntity + ApiResponse 래핑.
 *
 * <p>API-CTR-007: Controller 비즈니스 로직 금지.
 *
 * <p>API-CTR-010: CQRS Controller 분리.
 */
@Tag(name = "Admin 메뉴 조회", description = "역할별 Admin 메뉴 조회 API")
@RestController
@RequestMapping(AdminMenuAdminEndpoints.ADMIN_MENUS)
public class AdminMenuQueryController {

    private final GetAccessibleMenusUseCase getAccessibleMenusUseCase;
    private final AdminMenuQueryApiMapper mapper;
    private final MarketAccessChecker accessChecker;

    public AdminMenuQueryController(
            GetAccessibleMenusUseCase getAccessibleMenusUseCase,
            AdminMenuQueryApiMapper mapper,
            MarketAccessChecker accessChecker) {
        this.getAccessibleMenusUseCase = getAccessibleMenusUseCase;
        this.mapper = mapper;
        this.accessChecker = accessChecker;
    }

    /**
     * 역할별 접근 가능한 메뉴 조회 API.
     *
     * <p>인증된 사용자의 역할 중 가장 높은 레벨로 접근 가능한 메뉴를 트리 형태로 반환합니다.
     *
     * @return 메뉴 트리 응답
     */
    @Operation(
            summary = "역할별 메뉴 조회",
            description = "인증된 사용자의 역할에 따라 접근 가능한 Admin 메뉴를 트리 형태로 반환합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "인증 필요")
    })
    @PreAuthorize("@access.authenticated()")
    @RequirePermission(value = "admin-menu:read", description = "Admin 메뉴 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<AdminMenuApiResponse>> getAccessibleMenus() {
        AdminRole highestRole = accessChecker.resolveHighestRole();
        List<AdminMenu> menus = getAccessibleMenusUseCase.execute(highestRole);
        AdminMenuApiResponse response = mapper.toTreeResponse(menus);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
