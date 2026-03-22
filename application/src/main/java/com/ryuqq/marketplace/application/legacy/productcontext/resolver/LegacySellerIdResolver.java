package com.ryuqq.marketplace.application.legacy.productcontext.resolver;

import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacySellerIdMappingQueryPort;
import org.springframework.stereotype.Component;

/**
 * 레거시 셀러 ID → 표준 셀러 ID 리졸버.
 *
 * <p>매핑이 없으면 예외를 던집니다.
 */
@Component
public class LegacySellerIdResolver {

    private final LegacySellerIdMappingQueryPort sellerIdMappingQueryPort;

    public LegacySellerIdResolver(LegacySellerIdMappingQueryPort sellerIdMappingQueryPort) {
        this.sellerIdMappingQueryPort = sellerIdMappingQueryPort;
    }

    public long resolve(long legacySellerId) {
        return sellerIdMappingQueryPort
                .findInternalSellerIdByLegacySellerId(legacySellerId)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "레거시 셀러 ID 매핑을 찾을 수 없습니다: " + legacySellerId));
    }
}
