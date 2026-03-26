package com.ryuqq.marketplace.application.seller.service.query;

import com.ryuqq.marketplace.application.seller.dto.response.SellerCompositeResult;
import com.ryuqq.marketplace.application.seller.manager.SellerCompositionReadManager;
import com.ryuqq.marketplace.application.seller.port.in.query.GetSellerForCustomerUseCase;
import org.springframework.stereotype.Service;

/**
 * GetSellerForCustomerService - 고객용 셀러 조회 Service.
 *
 * <p>고객에게 노출 가능한 최소 정보만 반환 (Seller 기본정보 + 회사명)
 */
@Service
public class GetSellerForCustomerService implements GetSellerForCustomerUseCase {

    private final SellerCompositionReadManager compositionReadManager;

    public GetSellerForCustomerService(SellerCompositionReadManager compositionReadManager) {
        this.compositionReadManager = compositionReadManager;
    }

    @Override
    public SellerCompositeResult execute(Long sellerId) {
        return compositionReadManager.getSellerComposite(sellerId);
    }
}
