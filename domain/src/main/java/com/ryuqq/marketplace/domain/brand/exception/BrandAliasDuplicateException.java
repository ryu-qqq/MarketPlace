package com.ryuqq.marketplace.domain.brand.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

import java.util.Map;

/**
 * BrandAliasDuplicateException - 브랜드 별칭 중복 예외
 *
 * <p>동일한 정규화된 별칭(normalized alias)과 범위(scope)가 이미 존재할 때 발생합니다.
 *
 * <p><strong>발생 상황:</strong>
 *
 * <ul>
 *   <li>브랜드 별칭 생성 시 동일한 정규화된 별칭과 범위가 이미 존재
 *   <li>브랜드 별칭 수정 시 변경하려는 별칭이 이미 존재
 * </ul>
 *
 * <p><strong>중복 조건:</strong> normalizedAlias + scope 조합이 유니크해야 함
 *
 * <p><strong>에러 코드:</strong> BRAND-006
 *
 * <p><strong>HTTP 상태 코드:</strong> 409 (Conflict)
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
public class BrandAliasDuplicateException extends DomainException {

    /**
     * 중복된 브랜드 별칭 (범위 포함)
     *
     * @param brandId         브랜드 ID
     * @param normalizedAlias 정규화된 별칭
     * @param scope           별칭 범위
     * @author ryu-qqq
     * @since 2025-11-27
     */
    public BrandAliasDuplicateException(Long brandId, String normalizedAlias, String scope) {
        super(
                BrandErrorCode.BRAND_ALIAS_DUPLICATE.getCode(),
                "Brand alias already exists: brandId=" + brandId
                        + ", normalizedAlias=" + normalizedAlias
                        + ", scope=" + scope,
                Map.of(
                        "brandId", brandId,
                        "normalizedAlias", normalizedAlias,
                        "scope", scope
                )
        );
    }

    /**
     * 중복된 브랜드 별칭 (범위 없음)
     *
     * @param brandId         브랜드 ID
     * @param normalizedAlias 정규화된 별칭
     * @author ryu-qqq
     * @since 2025-11-27
     */
    public BrandAliasDuplicateException(Long brandId, String normalizedAlias) {
        super(
                BrandErrorCode.BRAND_ALIAS_DUPLICATE.getCode(),
                "Brand alias already exists: brandId=" + brandId
                        + ", normalizedAlias=" + normalizedAlias,
                Map.of(
                        "brandId", brandId,
                        "normalizedAlias", normalizedAlias
                )
        );
    }
}
