package com.ryuqq.marketplace.domain.brand.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

import java.util.Map;

/**
 * BrandAliasNotFoundException - 브랜드 별칭 미존재 예외
 *
 * <p>브랜드 별칭 ID로 조회 시 별칭이 존재하지 않을 때 발생합니다.
 *
 * <p><strong>발생 상황:</strong>
 *
 * <ul>
 *   <li>존재하지 않는 브랜드 별칭 ID로 조회
 *   <li>브랜드에 속하지 않는 별칭 ID로 조회
 *   <li>삭제된 브랜드 별칭 조회 시도
 * </ul>
 *
 * <p><strong>에러 코드:</strong> BRAND-005
 *
 * <p><strong>HTTP 상태 코드:</strong> 404 (Not Found)
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
public class BrandAliasNotFoundException extends DomainException {

    /**
     * 브랜드 별칭 ID로 조회 실패
     *
     * @param brandId 브랜드 ID
     * @param aliasId 존재하지 않는 별칭 ID
     * @author ryu-qqq
     * @since 2025-11-27
     */
    public BrandAliasNotFoundException(Long brandId, Long aliasId) {
        super(
                BrandErrorCode.BRAND_ALIAS_NOT_FOUND.getCode(),
                "Brand alias not found: brandId=" + brandId + ", aliasId=" + aliasId,
                Map.of("brandId", brandId, "aliasId", aliasId)
        );
    }

    /**
     * 별칭 ID로만 조회 실패
     *
     * @param aliasId 존재하지 않는 별칭 ID
     * @author ryu-qqq
     * @since 2025-11-27
     */
    public BrandAliasNotFoundException(Long aliasId) {
        super(
                BrandErrorCode.BRAND_ALIAS_NOT_FOUND.getCode(),
                "Brand alias not found: " + aliasId,
                Map.of("aliasId", aliasId)
        );
    }
}
