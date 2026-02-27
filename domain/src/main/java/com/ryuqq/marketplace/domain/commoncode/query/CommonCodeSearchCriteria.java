package com.ryuqq.marketplace.domain.commoncode.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import java.util.Locale;

/**
 * CommonCode 검색 조건 Criteria.
 *
 * <p>공통 코드 목록 조회 시 사용하는 검색 조건과 페이징 정보를 정의합니다.
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * // 특정 타입 코드로 활성화된 코드만 조회
 * CommonCodeSearchCriteria criteria = CommonCodeSearchCriteria.of(
 *     "PAYMENT_METHOD",
 *     true,
 *     QueryContext.defaultOf(CommonCodeSortKey.CREATED_AT)
 * );
 * }</pre>
 *
 * @param commonCodeTypeCode 공통 코드 타입 코드 (정확 일치, null이면 전체 조회)
 * @param active 활성화 상태 필터 (null이면 전체)
 * @param queryContext 정렬 및 페이징 정보
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CommonCodeSearchCriteria(
        String commonCodeTypeCode, Boolean active, QueryContext<CommonCodeSortKey> queryContext) {

    /** Compact Constructor - null 방어 */
    public CommonCodeSearchCriteria {
        if (queryContext == null) {
            queryContext = QueryContext.defaultOf(CommonCodeSortKey.defaultKey());
        }
        if (commonCodeTypeCode != null) {
            commonCodeTypeCode = commonCodeTypeCode.trim().toUpperCase(Locale.ROOT);
            if (commonCodeTypeCode.isBlank()) {
                commonCodeTypeCode = null;
            }
        }
    }

    /**
     * 검색 조건 생성
     *
     * @param commonCodeTypeCode 공통 코드 타입 코드 (정확 일치, null이면 전체 조회)
     * @param active 활성화 상태 필터 (null이면 전체)
     * @param queryContext 정렬 및 페이징 정보
     * @return CommonCodeSearchCriteria
     */
    public static CommonCodeSearchCriteria of(
            String commonCodeTypeCode,
            Boolean active,
            QueryContext<CommonCodeSortKey> queryContext) {
        return new CommonCodeSearchCriteria(commonCodeTypeCode, active, queryContext);
    }

    /**
     * 타입 코드별 기본 검색 조건 생성 (전체 조회, 등록순 내림차순)
     *
     * @param commonCodeTypeCode 공통 코드 타입 코드
     * @return CommonCodeSearchCriteria
     */
    public static CommonCodeSearchCriteria defaultOf(String commonCodeTypeCode) {
        return new CommonCodeSearchCriteria(
                commonCodeTypeCode, null, QueryContext.defaultOf(CommonCodeSortKey.defaultKey()));
    }

    /**
     * 타입 코드별 활성화된 항목만 조회하는 조건 생성
     *
     * @param commonCodeTypeCode 공통 코드 타입 코드
     * @return CommonCodeSearchCriteria
     */
    public static CommonCodeSearchCriteria activeOnly(String commonCodeTypeCode) {
        return new CommonCodeSearchCriteria(
                commonCodeTypeCode, true, QueryContext.defaultOf(CommonCodeSortKey.defaultKey()));
    }

    /** 활성화 상태 필터가 있는지 확인 */
    public boolean hasActiveFilter() {
        return active != null;
    }

    /** 페이지 크기 반환 (편의 메서드) */
    public int size() {
        return queryContext.size();
    }

    /** 오프셋 반환 (편의 메서드) */
    public long offset() {
        return queryContext.offset();
    }

    /** 현재 페이지 번호 반환 (편의 메서드) */
    public int page() {
        return queryContext.page();
    }
}
