package com.ryuqq.marketplace.application.outboundsync.port.out.query;

import java.util.Optional;

/**
 * 아웃바운드 매핑 역조회 포트.
 *
 * <p>내부 카테고리/브랜드 ID → 외부 판매채널 카테고리/브랜드 ID 변환을 위한 조회 포트입니다.
 */
public interface OutboundMappingQueryPort {

    /**
     * 판매채널의 외부 카테고리 ID 역조회.
     *
     * <p>CategoryMapping → CategoryPreset → Shop JOIN으로 salesChannelId 기준 필터링.
     *
     * @param salesChannelId 판매채널 ID
     * @param internalCategoryId 내부 카테고리 ID
     * @return 판매채널 카테고리 ID (매핑 없으면 empty)
     */
    Optional<Long> findSalesChannelCategoryId(Long salesChannelId, Long internalCategoryId);

    /**
     * 판매채널의 외부 브랜드 ID 역조회.
     *
     * <p>BrandMapping → BrandPreset → Shop JOIN으로 salesChannelId 기준 필터링.
     *
     * @param salesChannelId 판매채널 ID
     * @param internalBrandId 내부 브랜드 ID
     * @return 판매채널 브랜드 ID (매핑 없으면 empty)
     */
    Optional<Long> findSalesChannelBrandId(Long salesChannelId, Long internalBrandId);
}
