package com.ryuqq.marketplace.adapter.in.rest.common.security;

import com.ryuqq.authhub.sdk.access.BaseAccessChecker;
import com.ryuqq.marketplace.application.seller.port.in.query.ResolveSellerIdByOrganizationUseCase;
import java.util.Optional;
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

    public MarketAccessChecker(ResolveSellerIdByOrganizationUseCase resolveSellerIdUseCase) {
        this.resolveSellerIdUseCase = resolveSellerIdUseCase;
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
}
