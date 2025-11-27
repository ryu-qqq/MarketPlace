package com.ryuqq.marketplace.domain.brand.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

import java.util.Map;

/**
 * CanonicalNameDuplicateException - 표준 브랜드명 중복 예외
 *
 * <p>표준 브랜드명(canonical name)이 이미 존재할 때 발생합니다.
 *
 * <p><strong>발생 상황:</strong>
 *
 * <ul>
 *   <li>브랜드 생성 시 동일한 표준명이 이미 존재
 *   <li>브랜드 수정 시 변경하려는 표준명이 이미 존재
 * </ul>
 *
 * <p><strong>에러 코드:</strong> BRAND-003
 *
 * <p><strong>HTTP 상태 코드:</strong> 409 (Conflict)
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
public class CanonicalNameDuplicateException extends DomainException {

    /**
     * 중복된 표준 브랜드명
     *
     * @param canonicalName 중복된 표준 브랜드명
     * @author ryu-qqq
     * @since 2025-11-27
     */
    public CanonicalNameDuplicateException(String canonicalName) {
        super(
                BrandErrorCode.CANONICAL_NAME_DUPLICATE.getCode(),
                "Canonical name already exists: " + canonicalName,
                Map.of("canonicalName", canonicalName)
        );
    }
}
