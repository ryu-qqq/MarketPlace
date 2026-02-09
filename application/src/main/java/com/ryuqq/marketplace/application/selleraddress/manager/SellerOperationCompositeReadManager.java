package com.ryuqq.marketplace.application.selleraddress.manager;

import com.ryuqq.marketplace.application.selleraddress.dto.composite.SellerOperationCompositeResult;
import com.ryuqq.marketplace.application.selleraddress.port.out.query.SellerOperationCompositeQueryPort;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SellerOperationCompositeReadManager - 셀러 운영 메타데이터 Composite 조회 매니저.
 *
 * <p>크로스 도메인 조인을 통한 성능 최적화된 조회를 담당합니다.
 */
@Component
public class SellerOperationCompositeReadManager {

    private final SellerOperationCompositeQueryPort compositeQueryPort;

    public SellerOperationCompositeReadManager(
            SellerOperationCompositeQueryPort compositeQueryPort) {
        this.compositeQueryPort = compositeQueryPort;
    }

    /**
     * 셀러 운영 메타데이터 조회.
     *
     * @param sellerId 셀러 ID
     * @return 운영 메타데이터 Composite 결과
     */
    @Transactional(readOnly = true)
    public SellerOperationCompositeResult getOperationMetadata(SellerId sellerId) {
        return compositeQueryPort.findOperationMetadataBySellerId(sellerId);
    }
}
