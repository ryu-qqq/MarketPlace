package com.ryuqq.marketplace.application.seller.service.query;

import com.ryuqq.marketplace.application.seller.dto.response.SellerCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPublicProfileResult;
import com.ryuqq.marketplace.application.seller.manager.SellerCompositionReadManager;
import com.ryuqq.marketplace.application.seller.port.in.query.GetSellerPublicProfileUseCase;
import org.springframework.stereotype.Service;

/**
 * GetSellerPublicProfileService - 셀러 공개 프로필 조회 Service.
 *
 * <p>셀러 Composite 조회 후 공개 프로필 필드만 추출하여 반환합니다.
 */
@Service
public class GetSellerPublicProfileService implements GetSellerPublicProfileUseCase {

    private final SellerCompositionReadManager compositionReadManager;

    public GetSellerPublicProfileService(SellerCompositionReadManager compositionReadManager) {
        this.compositionReadManager = compositionReadManager;
    }

    @Override
    public SellerPublicProfileResult execute(Long sellerId) {
        SellerCompositeResult composite = compositionReadManager.getSellerComposite(sellerId);
        SellerCompositeResult.SellerInfo seller = composite.seller();
        SellerCompositeResult.BusinessInfo business = composite.businessInfo();

        return new SellerPublicProfileResult(
                seller.sellerName(),
                seller.displayName(),
                business.companyName(),
                business.representative());
    }
}
