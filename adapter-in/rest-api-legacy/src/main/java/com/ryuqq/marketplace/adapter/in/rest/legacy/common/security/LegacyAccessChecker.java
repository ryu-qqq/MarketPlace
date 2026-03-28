package com.ryuqq.marketplace.adapter.in.rest.legacy.common.security;

import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacySellerIdResolver;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.ResolveLegacyProductGroupSellerIdUseCase;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 레거시 API 전용 접근 권한 검사기.
 *
 * <p>{@link LegacyAuthContextHolder}에서 인증 정보를 직접 조회합니다. 표준 API의 {@code MarketAccessChecker}와 달리
 * Gateway 헤더가 아닌 JWT claims 기반으로 동작합니다.
 *
 * <p>역할 기반 권한:
 *
 * <ul>
 *   <li>MASTER — 모든 리소스 접근 가능 (SUPER_ADMIN 동등)
 *   <li>SELLER — 본인 소유 리소스만 접근 가능
 * </ul>
 *
 * <p>Spring Security {@code @PreAuthorize}에서 SpEL로 사용:
 *
 * <pre>{@code
 * @PreAuthorize("@legacyAccess.isProductOwnerOrMaster(#productGroupId)")
 * @PreAuthorize("@legacyAccess.authenticated()")
 * }</pre>
 */
@Component("legacyAccess")
public class LegacyAccessChecker {

    private static final String MASTER_ROLE = "MASTER";

    private final ResolveLegacyProductGroupSellerIdUseCase resolveLegacyProductGroupSellerIdUseCase;
    private final LegacySellerIdResolver sellerIdResolver;

    public LegacyAccessChecker(
            ResolveLegacyProductGroupSellerIdUseCase resolveLegacyProductGroupSellerIdUseCase,
            LegacySellerIdResolver sellerIdResolver) {
        this.resolveLegacyProductGroupSellerIdUseCase = resolveLegacyProductGroupSellerIdUseCase;
        this.sellerIdResolver = sellerIdResolver;
    }

    /**
     * 인증 여부 확인.
     *
     * @return LegacyAuthContext가 세팅되어 있으면 true
     */
    public boolean authenticated() {
        try {
            LegacyAuthContextHolder.getContext();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * MASTER 역할 여부 확인.
     *
     * @return MASTER이면 true
     */
    public boolean isMaster() {
        return MASTER_ROLE.equals(LegacyAuthContextHolder.getRoleType());
    }

    /**
     * 현재 인증된 셀러 ID를 반환합니다. MASTER이면 null (전체 접근), SELLER이면 본인 셀러 ID.
     *
     * <p>목록 조회 시 sellerId 필터링에 사용합니다.
     *
     * @return MASTER이면 null, SELLER이면 셀러 ID
     */
    public Long resolveSellerIdOrNull() {
        if (isMaster()) {
            return null;
        }
        return LegacyAuthContextHolder.getSellerId();
    }

    /**
     * 현재 인증된 셀러 ID를 반환합니다.
     *
     * @return 셀러 ID
     */
    public long getCurrentSellerId() {
        return LegacyAuthContextHolder.getSellerId();
    }

    /**
     * 레거시 상품그룹 소유자 또는 MASTER 검증.
     *
     * <p>MASTER는 자동 통과. SELLER는 상품그룹의 sellerId와 현재 인증된 sellerId가 일치해야 통과.
     *
     * @param productGroupId 레거시 상품그룹 ID
     * @return 소유자이거나 MASTER이면 true
     */
    public boolean isProductOwnerOrMaster(long productGroupId) {
        if (isMaster()) {
            return true;
        }

        long legacySellerId = LegacyAuthContextHolder.getSellerId();
        long internalSellerId = sellerIdResolver.resolve(legacySellerId);
        Optional<Long> productSellerId =
                resolveLegacyProductGroupSellerIdUseCase.execute(productGroupId);

        return productSellerId.isPresent() && productSellerId.get() == internalSellerId;
    }
}
