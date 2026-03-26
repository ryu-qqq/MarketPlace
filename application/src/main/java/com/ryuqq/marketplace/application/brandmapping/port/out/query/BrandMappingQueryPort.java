package com.ryuqq.marketplace.application.brandmapping.port.out.query;

import java.util.Optional;

/** 브랜드 매핑 역조회 포트. */
public interface BrandMappingQueryPort {

    /**
     * 내부 브랜드 ID → 판매채널 브랜드 ID 역조회.
     *
     * @param salesChannelId 판매채널 ID
     * @param internalBrandId 내부 브랜드 ID
     * @return 판매채널 브랜드 ID (매핑 없으면 empty)
     */
    Optional<Long> findSalesChannelBrandId(Long salesChannelId, Long internalBrandId);

    /**
     * 내부 브랜드 ID → 외부 브랜드 코드 역조회.
     *
     * @param salesChannelId 판매채널 ID
     * @param internalBrandId 내부 브랜드 ID
     * @return 외부 브랜드 코드 (매핑 없으면 empty)
     */
    Optional<String> findExternalBrandCode(Long salesChannelId, Long internalBrandId);
}
