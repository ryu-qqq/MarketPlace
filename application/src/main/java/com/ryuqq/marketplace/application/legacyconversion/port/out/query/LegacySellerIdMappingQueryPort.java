package com.ryuqq.marketplace.application.legacyconversion.port.out.query;

import java.util.Optional;

/** 레거시 셀러 ID 매핑 조회 포트. */
public interface LegacySellerIdMappingQueryPort {

    /**
     * 레거시 셀러 ID로 내부 셀러 ID 조회.
     *
     * @param legacySellerId luxurydb seller.seller_id
     * @return 내부 셀러 ID (market sellers.id)
     */
    Optional<Long> findInternalSellerIdByLegacySellerId(long legacySellerId);

    /**
     * 레거시 셀러 ID로 셀러명 조회.
     *
     * @param legacySellerId luxurydb seller.seller_id
     * @return 셀러명
     */
    Optional<String> findSellerNameByLegacySellerId(long legacySellerId);

    /**
     * 내부 셀러 ID로 레거시 셀러 ID 역조회.
     *
     * @param internalSellerId market sellers.id
     * @return 레거시 셀러 ID
     */
    Optional<Long> findLegacySellerIdByInternalSellerId(long internalSellerId);
}
