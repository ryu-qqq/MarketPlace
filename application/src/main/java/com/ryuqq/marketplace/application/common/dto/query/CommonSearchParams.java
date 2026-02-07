package com.ryuqq.marketplace.application.common.dto.query;

import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.common.vo.SortKey;
import java.time.LocalDate;

/**
 * 공통 검색 파라미터
 *
 * <p>모든 Search Query에서 공통으로 사용하는 파라미터입니다. Composition 방식으로 사용되며, Query DTO는 delegate 메서드를 통해 이
 * 파라미터들을 노출해야 합니다.
 *
 * <p><strong>사용 규칙:</strong>
 *
 * <ul>
 *   <li>SearchQuery는 이 record를 필드로 포함해야 함
 *   <li>SearchQuery는 delegate 메서드를 제공하여 직접 접근 허용
 *   <li>중첩 접근(query.searchParams().page()) 금지 - delegate 사용
 * </ul>
 *
 * <p><strong>기본값:</strong>
 *
 * <ul>
 *   <li>includeDeleted: false
 *   <li>sortKey: "createdAt"
 *   <li>sortDirection: "DESC"
 *   <li>page: 0
 *   <li>size: 20
 * </ul>
 *
 * @param includeDeleted 삭제된 항목 포함 여부 (기본: false)
 * @param startDate 조회 시작일 (null이면 제한 없음)
 * @param endDate 조회 종료일 (null이면 제한 없음)
 * @param sortKey 정렬 기준 (기본: createdAt)
 * @param sortDirection 정렬 방향 (기본: DESC)
 * @param page 페이지 번호 (기본: 0)
 * @param size 페이지 크기 (기본: 20)
 * @author development-team
 * @since 1.0.0
 */
public record CommonSearchParams(
        Boolean includeDeleted,
        LocalDate startDate,
        LocalDate endDate,
        String sortKey,
        String sortDirection,
        Integer page,
        Integer size) {

    private static final Boolean DEFAULT_INCLUDE_DELETED = false;
    private static final String DEFAULT_SORT_KEY = "createdAt";
    private static final String DEFAULT_SORT_DIRECTION = "DESC";
    private static final Integer DEFAULT_PAGE = 0;
    private static final Integer DEFAULT_SIZE = 20;

    /**
     * Compact Constructor - null 방어를 위한 기본값 설정
     *
     * <p>REST API 경계에서 기본값이 설정되지만, 혹시 모를 null 전달에 대비하여 Application 레이어에서도 방어합니다.
     */
    public CommonSearchParams {
        if (includeDeleted == null) {
            includeDeleted = DEFAULT_INCLUDE_DELETED;
        }
        if (sortKey == null) {
            sortKey = DEFAULT_SORT_KEY;
        }
        if (sortDirection == null) {
            sortDirection = DEFAULT_SORT_DIRECTION;
        }
        if (page == null) {
            page = DEFAULT_PAGE;
        }
        if (size == null) {
            size = DEFAULT_SIZE;
        }
    }

    /**
     * CommonSearchParams 생성 (static factory method)
     *
     * @param includeDeleted 삭제된 항목 포함 여부
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @param sortKey 정렬 기준
     * @param sortDirection 정렬 방향
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return CommonSearchParams 인스턴스
     */
    public static CommonSearchParams of(
            Boolean includeDeleted,
            LocalDate startDate,
            LocalDate endDate,
            String sortKey,
            String sortDirection,
            Integer page,
            Integer size) {
        return new CommonSearchParams(
                includeDeleted, startDate, endDate, sortKey, sortDirection, page, size);
    }

    /**
     * QueryContext 변환
     *
     * <p>Application 레이어의 검색 파라미터를 Domain 레이어의 QueryContext로 변환합니다.
     *
     * @param sortKeyClass SortKey 구현체 클래스
     * @param <K> SortKey 타입
     * @return QueryContext
     */
    public <K extends SortKey> QueryContext<K> toQueryContext(Class<K> sortKeyClass) {
        K resolvedSortKey = resolveSortKey(sortKeyClass);
        SortDirection direction = SortDirection.fromString(sortDirection);
        PageRequest pageRequest = PageRequest.of(page, size);
        return QueryContext.of(resolvedSortKey, direction, pageRequest, includeDeleted);
    }

    /** 문자열 sortKey를 해당 SortKey enum으로 변환 */
    @SuppressWarnings("unchecked")
    private <K extends SortKey> K resolveSortKey(Class<K> sortKeyClass) {
        if (!sortKeyClass.isEnum()) {
            throw new IllegalArgumentException(
                    "SortKey class must be an enum: " + sortKeyClass.getName());
        }

        K[] constants = sortKeyClass.getEnumConstants();
        // sortKey 문자열과 일치하는 enum 찾기
        for (K constant : constants) {
            if (constant.fieldName().equalsIgnoreCase(sortKey)) {
                return constant;
            }
            // enum 이름으로도 검색 (CREATED_AT 형태)
            if (constant.name().equalsIgnoreCase(sortKey.replace("_", ""))) {
                return constant;
            }
            if (((Enum<?>) constant).name().equalsIgnoreCase(sortKey)) {
                return constant;
            }
        }
        // 기본값 반환 (첫 번째 enum 값)
        return constants[0];
    }
}
