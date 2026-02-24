package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductReadManager;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.inboundproduct.exception.InboundProductNotConvertedException;
import org.springframework.stereotype.Component;

/**
 * 세토프 레거시 PK → 내부 ProductGroupId + sellerId 해석기.
 *
 * <p>InboundProduct 테이블에서 inboundSourceId=1(세토프) + externalProductCode=세토프PK 조건으로 내부
 * internalProductGroupId를 조회합니다.
 */
@Component
public class LegacyProductIdResolver {

    private static final long SETOF_EXTERNAL_SOURCE_ID = 1L;

    private final InboundProductReadManager inboundProductReadManager;

    public LegacyProductIdResolver(InboundProductReadManager inboundProductReadManager) {
        this.inboundProductReadManager = inboundProductReadManager;
    }

    /**
     * 세토프 PK → 내부 ProductGroupId + sellerId 해석.
     *
     * @param setofProductGroupId 세토프 상품그룹 PK
     * @return 해석된 내부 ID 정보
     * @throws InboundProductException 해당 InboundProduct가 없거나 아직 변환되지 않은 경우
     */
    public ResolvedLegacyProductId resolve(long setofProductGroupId) {
        InboundProduct inbound =
                inboundProductReadManager.findByInboundSourceIdAndProductCodeOrThrow(
                        SETOF_EXTERNAL_SOURCE_ID, String.valueOf(setofProductGroupId));

        Long internalId = inbound.internalProductGroupId();
        if (internalId == null) {
            throw new InboundProductNotConvertedException(setofProductGroupId);
        }
        return new ResolvedLegacyProductId(internalId, inbound.sellerId());
    }

    public record ResolvedLegacyProductId(long internalProductGroupId, long sellerId) {}
}
