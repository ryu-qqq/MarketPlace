package com.ryuqq.marketplace.domain.channeloptionmapping.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;

/**
 * ChannelOptionMapping 검색 조건 Criteria.
 *
 * <p>채널 옵션 매핑 목록 조회 시 사용하는 검색 조건과 페이징 정보를 정의합니다.
 *
 * @param salesChannelId 판매채널 ID 필터 (null이면 전체)
 * @param canonicalOptionGroupId 캐노니컬 옵션 그룹 ID 필터 (null이면 전체)
 * @param queryContext 정렬 및 페이징 정보
 */
public record ChannelOptionMappingSearchCriteria(
        Long salesChannelId,
        Long canonicalOptionGroupId,
        QueryContext<ChannelOptionMappingSortKey> queryContext) {

    public static ChannelOptionMappingSearchCriteria of(
            Long salesChannelId,
            Long canonicalOptionGroupId,
            QueryContext<ChannelOptionMappingSortKey> queryContext) {
        return new ChannelOptionMappingSearchCriteria(
                salesChannelId, canonicalOptionGroupId, queryContext);
    }

    public static ChannelOptionMappingSearchCriteria defaultCriteria() {
        return new ChannelOptionMappingSearchCriteria(
                null, null, QueryContext.defaultOf(ChannelOptionMappingSortKey.defaultKey()));
    }

    /** 판매채널 필터가 있는지 확인. */
    public boolean hasSalesChannelFilter() {
        return salesChannelId != null;
    }

    /** 캐노니컬 옵션 그룹 필터가 있는지 확인. */
    public boolean hasCanonicalOptionGroupFilter() {
        return canonicalOptionGroupId != null;
    }

    /** 페이지 크기 반환 (편의 메서드). */
    public int size() {
        return queryContext.size();
    }

    /** 오프셋 반환 (편의 메서드). */
    public long offset() {
        return queryContext.offset();
    }

    /** 현재 페이지 번호 반환 (편의 메서드). */
    public int page() {
        return queryContext.page();
    }
}
