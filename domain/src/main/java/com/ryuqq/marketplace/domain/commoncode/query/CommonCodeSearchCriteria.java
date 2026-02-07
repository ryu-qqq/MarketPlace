package com.ryuqq.marketplace.domain.commoncode.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import java.util.Locale;

/**
 * CommonCode 검색 조건 Criteria.
 *
 * <p>공통 코드 목록 조회 시 사용하는 검색 조건과 페이징 정보를 정의합니다.
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * // 특정 타입의 활성화된 코드만 조회
 * CommonCodeSearchCriteria criteria = CommonCodeSearchCriteria.of(
 *     CommonCodeTypeId.of(1L),
 *     true,
 *     "CARD",
 *     QueryContext.defaultOf(CommonCodeSortKey.CREATED_AT)
 * );
 * }</pre>
 *
 * @param commonCodeTypeId 공통 코드 타입 ID (필수)
 * @param active 활성화 상태 필터 (null이면 전체)
 * @param code 코드 검색 (null이면 전체)
 * @param queryContext 정렬 및 페이징 정보
 * @author ryu-qqq
 * @since 1.0.0
 */
public record CommonCodeSearchCriteria(
        CommonCodeTypeId commonCodeTypeId,
        Boolean active,
        String code,
        QueryContext<CommonCodeSortKey> queryContext) {

    /** Compact Constructor - null 방어 */
    public CommonCodeSearchCriteria {
        if (commonCodeTypeId == null) {
            throw new IllegalArgumentException("공통 코드 타입 ID는 필수입니다");
        }
        if (queryContext == null) {
            queryContext = QueryContext.defaultOf(CommonCodeSortKey.defaultKey());
        }
        if (code != null) {
            code = code.trim().toUpperCase(Locale.ROOT);
            if (code.isBlank()) {
                code = null;
            }
        }
    }

    /**
     * 검색 조건 생성
     *
     * @param commonCodeTypeId 공통 코드 타입 ID (필수)
     * @param active 활성화 상태 필터 (null이면 전체)
     * @param code 코드 검색 (null이면 전체)
     * @param queryContext 정렬 및 페이징 정보
     * @return CommonCodeSearchCriteria
     */
    public static CommonCodeSearchCriteria of(
            CommonCodeTypeId commonCodeTypeId,
            Boolean active,
            String code,
            QueryContext<CommonCodeSortKey> queryContext) {
        return new CommonCodeSearchCriteria(commonCodeTypeId, active, code, queryContext);
    }

    /**
     * 타입 ID별 기본 검색 조건 생성 (전체 조회, 등록순 내림차순)
     *
     * @param commonCodeTypeId 공통 코드 타입 ID (필수)
     * @return CommonCodeSearchCriteria
     */
    public static CommonCodeSearchCriteria defaultOf(CommonCodeTypeId commonCodeTypeId) {
        return new CommonCodeSearchCriteria(
                commonCodeTypeId,
                null,
                null,
                QueryContext.defaultOf(CommonCodeSortKey.defaultKey()));
    }

    /**
     * 타입 ID별 활성화된 항목만 조회하는 조건 생성
     *
     * @param commonCodeTypeId 공통 코드 타입 ID (필수)
     * @return CommonCodeSearchCriteria
     */
    public static CommonCodeSearchCriteria activeOnly(CommonCodeTypeId commonCodeTypeId) {
        return new CommonCodeSearchCriteria(
                commonCodeTypeId,
                true,
                null,
                QueryContext.defaultOf(CommonCodeSortKey.defaultKey()));
    }

    /** 공통 코드 타입 ID 원시값 반환 (편의 메서드) */
    public Long commonCodeTypeIdValue() {
        return commonCodeTypeId.value();
    }

    /** 코드 검색 조건이 있는지 확인 */
    public boolean hasCodeFilter() {
        return code != null && !code.isBlank();
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
