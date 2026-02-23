package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductReadManager;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import org.springframework.stereotype.Component;

/**
 * 외부 ID → 내부 ProductGroupId 해석 컴포넌트.
 *
 * <p>inboundSourceId + externalProductCode로 InboundProduct를 조회하고, CONVERTED 상태 검증 후 내부
 * ProductGroupId를 반환합니다.
 */
@Component
public class InboundProductIdResolver {

    private final InboundProductReadManager readManager;

    public InboundProductIdResolver(InboundProductReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * inboundSourceId + externalProductCode → 내부 ProductGroupId 해석.
     *
     * @param inboundSourceId 외부 소스 ID
     * @param externalProductCode 외부 상품 코드
     * @return 내부 ProductGroupId
     * @throws IllegalStateException 변환 완료되지 않은 인바운드 상품인 경우
     */
    public ProductGroupId resolve(long inboundSourceId, String externalProductCode) {
        InboundProduct inbound =
                readManager.findByInboundSourceIdAndProductCodeOrThrow(
                        inboundSourceId, externalProductCode);
        if (!inbound.status().isConverted()) {
            throw new IllegalStateException("변환 완료되지 않은 인바운드 상품: " + externalProductCode);
        }
        return ProductGroupId.of(inbound.internalProductGroupId());
    }
}
