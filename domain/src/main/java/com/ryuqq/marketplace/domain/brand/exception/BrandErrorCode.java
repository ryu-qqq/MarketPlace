package com.ryuqq.marketplace.domain.brand.exception;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;

/**
 * BrandErrorCode - 브랜드 도메인 에러 코드 정의
 *
 * <p>브랜드 도메인에서 발생하는 모든 비즈니스 예외의 에러 코드를 정의합니다.
 *
 * <p><strong>에러 코드 구조:</strong>
 *
 * <ul>
 *   <li>BRAND-001: 브랜드 미존재
 *   <li>BRAND-002: 브랜드 코드 중복
 *   <li>BRAND-003: 표준 브랜드명 중복
 *   <li>BRAND-004: 차단된 브랜드
 *   <li>BRAND-005: 브랜드 별칭 미존재
 *   <li>BRAND-006: 브랜드 별칭 중복
 * </ul>
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
public enum BrandErrorCode implements ErrorCode {

    /**
     * 브랜드를 찾을 수 없음
     *
     * <p>HTTP 상태 코드: 404 (Not Found)
     *
     * @author ryu-qqq
     * @since 2025-11-27
     */
    BRAND_NOT_FOUND("BRAND-001", 404, "브랜드를 찾을 수 없습니다"),

    /**
     * 브랜드 코드 중복
     *
     * <p>HTTP 상태 코드: 409 (Conflict)
     *
     * @author ryu-qqq
     * @since 2025-11-27
     */
    BRAND_CODE_DUPLICATE("BRAND-002", 409, "브랜드 코드가 중복됩니다"),

    /**
     * 표준 브랜드명 중복
     *
     * <p>HTTP 상태 코드: 409 (Conflict)
     *
     * @author ryu-qqq
     * @since 2025-11-27
     */
    CANONICAL_NAME_DUPLICATE("BRAND-003", 409, "표준 브랜드명이 중복됩니다"),

    /**
     * 차단된 브랜드
     *
     * <p>HTTP 상태 코드: 403 (Forbidden)
     *
     * <p>상품 매핑 불가 상황
     *
     * @author ryu-qqq
     * @since 2025-11-27
     */
    BRAND_BLOCKED("BRAND-004", 403, "차단된 브랜드입니다"),

    /**
     * 브랜드 별칭을 찾을 수 없음
     *
     * <p>HTTP 상태 코드: 404 (Not Found)
     *
     * @author ryu-qqq
     * @since 2025-11-27
     */
    BRAND_ALIAS_NOT_FOUND("BRAND-005", 404, "브랜드 별칭을 찾을 수 없습니다"),

    /**
     * 브랜드 별칭 중복
     *
     * <p>HTTP 상태 코드: 409 (Conflict)
     *
     * @author ryu-qqq
     * @since 2025-11-27
     */
    BRAND_ALIAS_DUPLICATE("BRAND-006", 409, "브랜드 별칭이 중복됩니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    BrandErrorCode(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
