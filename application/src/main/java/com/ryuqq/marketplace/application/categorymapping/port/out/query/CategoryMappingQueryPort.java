package com.ryuqq.marketplace.application.categorymapping.port.out.query;

import java.util.Optional;

/** 카테고리 매핑 역조회 포트. */
public interface CategoryMappingQueryPort {

    /**
     * 내부 카테고리 ID → 판매채널 카테고리 ID 역조회.
     *
     * @param salesChannelId 판매채널 ID
     * @param internalCategoryId 내부 카테고리 ID
     * @return 판매채널 카테고리 ID (매핑 없으면 empty)
     */
    Optional<Long> findSalesChannelCategoryId(Long salesChannelId, Long internalCategoryId);
}
