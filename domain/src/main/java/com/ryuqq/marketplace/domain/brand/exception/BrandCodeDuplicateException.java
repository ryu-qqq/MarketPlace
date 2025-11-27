package com.ryuqq.marketplace.domain.brand.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

import java.util.Map;

/**
 * BrandCodeDuplicateException - 브랜드 코드 중복 예외
 *
 * <p>브랜드 코드가 이미 존재할 때 발생합니다.
 *
 * <p><strong>발생 상황:</strong>
 *
 * <ul>
 *   <li>브랜드 생성 시 동일한 코드가 이미 존재
 *   <li>브랜드 수정 시 변경하려는 코드가 이미 존재
 * </ul>
 *
 * <p><strong>에러 코드:</strong> BRAND-002
 *
 * <p><strong>HTTP 상태 코드:</strong> 409 (Conflict)
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
public class BrandCodeDuplicateException extends DomainException {

    /**
     * 중복된 브랜드 코드
     *
     * @param code 중복된 브랜드 코드
     * @author ryu-qqq
     * @since 2025-11-27
     */
    public BrandCodeDuplicateException(String code) {
        super(
                BrandErrorCode.BRAND_CODE_DUPLICATE.getCode(),
                "Brand code already exists: " + code,
                Map.of("code", code)
        );
    }
}
