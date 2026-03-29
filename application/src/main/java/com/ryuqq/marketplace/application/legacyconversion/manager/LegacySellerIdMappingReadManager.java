package com.ryuqq.marketplace.application.legacyconversion.manager;

import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacySellerIdMappingQueryPort;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 셀러 ID 매핑 조회 Manager. */
@Component
public class LegacySellerIdMappingReadManager {

    private final LegacySellerIdMappingQueryPort queryPort;

    public LegacySellerIdMappingReadManager(LegacySellerIdMappingQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * 레거시 셀러 ID로 내부 셀러 ID 조회.
     *
     * @param legacySellerId luxurydb seller.seller_id
     * @return 내부 SellerId
     * @throws IllegalStateException 매핑 미발견 시
     */
    @Transactional(readOnly = true)
    public SellerId resolveInternalSellerId(long legacySellerId) {
        return queryPort
                .findInternalSellerIdByLegacySellerId(legacySellerId)
                .map(SellerId::of)
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "레거시 셀러 매핑 미발견: legacySellerId=" + legacySellerId));
    }

    /**
     * 레거시 셀러 ID로 내부 셀러 ID Optional 조회.
     *
     * @param legacySellerId luxurydb seller.seller_id
     * @return 내부 셀러 ID Optional
     */
    @Transactional(readOnly = true)
    public Optional<Long> findInternalSellerIdByLegacySellerId(long legacySellerId) {
        return queryPort.findInternalSellerIdByLegacySellerId(legacySellerId);
    }

    /**
     * 레거시 셀러 ID로 셀러명 조회.
     *
     * @param legacySellerId luxurydb seller.seller_id
     * @return 셀러명 Optional
     */
    @Transactional(readOnly = true)
    public Optional<String> findSellerNameByLegacySellerId(long legacySellerId) {
        return queryPort.findSellerNameByLegacySellerId(legacySellerId);
    }

    /**
     * 내부 셀러 ID로 레거시 셀러 ID 역조회.
     *
     * @param internalSellerId market sellers.id
     * @return 레거시 셀러 ID Optional
     */
    @Transactional(readOnly = true)
    public Optional<Long> findLegacySellerIdByInternalSellerId(long internalSellerId) {
        return queryPort.findLegacySellerIdByInternalSellerId(internalSellerId);
    }
}
