package com.ryuqq.marketplace.adapter.in.rest.common.security;

import com.ryuqq.authhub.sdk.access.BaseAccessChecker;
import com.ryuqq.authhub.sdk.context.UserContextHolder;
import com.ryuqq.marketplace.application.legacyproduct.port.in.query.ResolveLegacyProductGroupSellerIdUseCase;
import com.ryuqq.marketplace.application.seller.port.in.query.ResolveSellerIdByOrganizationUseCase;
import com.ryuqq.marketplace.domain.adminmenu.vo.AdminRole;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

/**
 * MarketPlace 접근 권한 검사기.
 *
 * <p>AuthHub SDK의 BaseAccessChecker를 상속하여 MarketPlace 도메인별 권한 검사 메서드를 제공합니다.
 *
 * <p>Spring Security {@code @PreAuthorize}에서 SpEL로 사용:
 *
 * <pre>{@code
 * @PreAuthorize("@access.hasPermission('brand:read')")
 * @PreAuthorize("@access.authenticated()")
 * @PreAuthorize("@access.isSellerOwnerOr(#sellerId, 'seller:write')")
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component("access")
public class MarketAccessChecker extends BaseAccessChecker {

    private final ResolveSellerIdByOrganizationUseCase resolveSellerIdUseCase;
    private final ResolveLegacyProductGroupSellerIdUseCase resolveLegacyProductGroupSellerIdUseCase;

    public MarketAccessChecker(
            ResolveSellerIdByOrganizationUseCase resolveSellerIdUseCase,
            ResolveLegacyProductGroupSellerIdUseCase resolveLegacyProductGroupSellerIdUseCase) {
        this.resolveSellerIdUseCase = resolveSellerIdUseCase;
        this.resolveLegacyProductGroupSellerIdUseCase = resolveLegacyProductGroupSellerIdUseCase;
    }

    /**
     * 셀러 리소스 소유자 검증.
     *
     * <p>현재 사용자의 organizationId로 sellerId를 조회하여 URL의 sellerId와 비교합니다. SUPER_ADMIN은 자동 통과하고, 본인 소유가
     * 아닌 경우 지정된 권한 보유 여부로 fallback합니다.
     *
     * @param sellerId URL의 셀러 ID
     * @param permission fallback 권한
     * @return 소유자이거나 권한 보유 시 true
     */
    public boolean isSellerOwnerOr(long sellerId, String permission) {
        if (superAdmin()) {
            return true;
        }

        String organizationId = getCurrentOrganizationId();
        if (organizationId == null || organizationId.isBlank()) {
            return hasPermission(permission);
        }

        Optional<Long> resolvedSellerId = resolveSellerIdUseCase.execute(organizationId);
        if (resolvedSellerId.isPresent() && resolvedSellerId.get() == sellerId) {
            return true;
        }

        return hasPermission(permission);
    }

    /**
     * 현재 인증된 사용자의 sellerId를 반환합니다.
     *
     * <p>organizationId로 sellerId를 조회하며, 조회 실패 시 AccessDeniedException을 발생시킵니다.
     *
     * @return 현재 사용자의 sellerId
     * @throws AccessDeniedException 셀러 정보를 찾을 수 없는 경우
     */
    public long resolveCurrentSellerId() {
        String organizationId = getCurrentOrganizationId();
        return resolveSellerIdUseCase
                .execute(organizationId)
                .orElseThrow(() -> new AccessDeniedException("현재 사용자의 셀러 정보를 찾을 수 없습니다"));
    }

    /**
     * 레거시 상품그룹 소유자 검증.
     *
     * <p>SUPER_ADMIN은 자동 통과합니다. 그 외 사용자는 현재 인증된 셀러 ID와 상품그룹의 셀러 ID가 일치해야 합니다.
     *
     * @param productGroupId 레거시 상품그룹 ID
     * @return 소유자이거나 SUPER_ADMIN이면 true
     */
    public boolean isLegacyProductOwnerOrSuperAdmin(long productGroupId) {
        if (superAdmin()) {
            return true;
        }

        String organizationId = getCurrentOrganizationId();
        if (organizationId == null || organizationId.isBlank()) {
            return false;
        }

        Optional<Long> currentSellerId = resolveSellerIdUseCase.execute(organizationId);
        if (currentSellerId.isEmpty()) {
            return false;
        }

        Optional<Long> productSellerId =
                resolveLegacyProductGroupSellerIdUseCase.execute(productGroupId);
        return productSellerId.isPresent() && productSellerId.get().equals(currentSellerId.get());
    }

    /**
     * 셀러 관리 권한 확인.
     *
     * @return 셀러 관리 권한 보유 여부
     */
    public boolean canManageSeller() {
        return hasPermission("seller:write");
    }

    /**
     * 상품 관리 권한 확인.
     *
     * @return 상품 관리 권한 보유 여부
     */
    public boolean canManageProduct() {
        return hasPermission("product:write");
    }

    /**
     * 현재 사용자의 가장 높은 AdminRole을 반환.
     *
     * <p>UserContextHolder에서 역할 목록을 조회하고, AdminRole로 변환하여 가장 높은 레벨을 반환합니다. 매칭되는 역할이 없으면
     * AdminRole.VIEWER를 기본값으로 반환합니다.
     *
     * @return 가장 높은 AdminRole
     */
    public AdminRole resolveHighestRole() {
        Set<String> roles = UserContextHolder.getContext().getRoles();
        AdminRole highest = AdminRole.VIEWER;
        for (String roleName : roles) {
            try {
                String normalized = roleName.startsWith("ROLE_") ? roleName.substring(5) : roleName;
                AdminRole role = AdminRole.fromName(normalized);
                if (role.level() > highest.level()) {
                    highest = role;
                }
            } catch (IllegalArgumentException ignored) {
                // 알 수 없는 역할명은 무시
            }
        }
        return highest;
    }
}
