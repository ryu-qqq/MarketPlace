package com.ryuqq.marketplace.application.outboundproduct.factory;

import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsPartnerSearchParams;
import com.ryuqq.marketplace.application.seller.dto.query.SellerSearchParams;
import com.ryuqq.marketplace.application.seller.factory.SellerQueryFactory;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchCriteria;
import org.springframework.stereotype.Component;

/** OMS 파트너 Query Factory. OmsPartnerSearchParams → SellerSearchCriteria 변환. */
@Component
public class OmsPartnerQueryFactory {

    private final SellerQueryFactory sellerQueryFactory;

    public OmsPartnerQueryFactory(SellerQueryFactory sellerQueryFactory) {
        this.sellerQueryFactory = sellerQueryFactory;
    }

    public SellerSearchCriteria createCriteria(OmsPartnerSearchParams params) {
        SellerSearchParams sellerParams = toSellerSearchParams(params);
        return sellerQueryFactory.createCriteria(sellerParams);
    }

    private SellerSearchParams toSellerSearchParams(OmsPartnerSearchParams params) {
        String searchField =
                (params.keyword() != null && !params.keyword().isBlank()) ? "SELLER_NAME" : null;
        return SellerSearchParams.of(null, searchField, params.keyword(), params.searchParams());
    }
}
