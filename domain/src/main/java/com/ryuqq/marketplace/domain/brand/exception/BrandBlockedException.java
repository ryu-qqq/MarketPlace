package com.ryuqq.marketplace.domain.brand.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

import java.util.Map;

/**
 * BrandBlockedException - 차단된 브랜드 예외
 *
 * <p>차단된 브랜드를 사용하려고 할 때 발생합니다.
 *
 * <p><strong>발생 상황:</strong>
 *
 * <ul>
 *   <li>상품에 차단된 브랜드 매핑 시도
 *   <li>차단된 브랜드로 상품 생성 시도
 *   <li>차단된 브랜드 정보 조회 시도
 * </ul>
 *
 * <p><strong>에러 코드:</strong> BRAND-004
 *
 * <p><strong>HTTP 상태 코드:</strong> 403 (Forbidden)
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
public class BrandBlockedException extends DomainException {

    /**
     * 차단된 브랜드 사용 시도
     *
     * @param brandId 차단된 브랜드 ID
     * @author ryu-qqq
     * @since 2025-11-27
     */
    public BrandBlockedException(Long brandId) {
        super(
                BrandErrorCode.BRAND_BLOCKED.getCode(),
                "Brand is blocked: " + brandId,
                Map.of("brandId", brandId)
        );
    }

    /**
     * 차단된 브랜드 사용 시도 (상세 정보 포함)
     *
     * @param brandId 차단된 브랜드 ID
     * @param reason  차단 사유
     * @author ryu-qqq
     * @since 2025-11-27
     */
    public BrandBlockedException(Long brandId, String reason) {
        super(
                BrandErrorCode.BRAND_BLOCKED.getCode(),
                "Brand is blocked: " + brandId + " (reason: " + reason + ")",
                Map.of("brandId", brandId, "reason", reason)
        );
    }
}
