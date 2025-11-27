package com.ryuqq.marketplace.domain.brand.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

import java.util.Map;

/**
 * BrandNotFoundException - 브랜드 미존재 예외
 *
 * <p>브랜드 ID 또는 코드로 조회 시 브랜드가 존재하지 않을 때 발생합니다.
 *
 * <p><strong>발생 상황:</strong>
 *
 * <ul>
 *   <li>존재하지 않는 브랜드 ID로 조회
 *   <li>존재하지 않는 브랜드 코드로 조회
 * </ul>
 *
 * <p><strong>에러 코드:</strong> BRAND-001
 *
 * <p><strong>HTTP 상태 코드:</strong> 404 (Not Found)
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
public class BrandNotFoundException extends DomainException {

    /**
     * 브랜드 ID로 조회 실패
     *
     * @param brandId 존재하지 않는 브랜드 ID
     * @author ryu-qqq
     * @since 2025-11-27
     */
    public BrandNotFoundException(Long brandId) {
        super(
                BrandErrorCode.BRAND_NOT_FOUND.getCode(),
                "Brand not found: " + brandId,
                Map.of("brandId", brandId)
        );
    }

    /**
     * 브랜드 코드로 조회 실패
     *
     * @param code 존재하지 않는 브랜드 코드
     * @author ryu-qqq
     * @since 2025-11-27
     */
    public BrandNotFoundException(String code) {
        super(
                BrandErrorCode.BRAND_NOT_FOUND.getCode(),
                "Brand not found by code: " + code,
                Map.of("code", code)
        );
    }
}
