package com.ryuqq.marketplace.application.selleraddress.service.query;

import com.ryuqq.marketplace.application.selleraddress.dto.composite.SellerOperationCompositeResult;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerOperationMetadataResult;
import com.ryuqq.marketplace.application.selleraddress.manager.SellerOperationCompositeReadManager;
import com.ryuqq.marketplace.application.selleraddress.port.in.query.GetSellerOperationMetadataUseCase;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Service;

/** 셀러 운영 메타데이터 조회 Service. */
@Service
public class GetSellerOperationMetadataService implements GetSellerOperationMetadataUseCase {

    private final SellerOperationCompositeReadManager compositeReadManager;

    public GetSellerOperationMetadataService(
            SellerOperationCompositeReadManager compositeReadManager) {
        this.compositeReadManager = compositeReadManager;
    }

    @Override
    public SellerOperationMetadataResult execute(Long sellerId) {
        SellerId id = SellerId.of(sellerId);
        SellerOperationCompositeResult composite = compositeReadManager.getOperationMetadata(id);

        return new SellerOperationMetadataResult(
                composite.addressTotalCount(),
                composite.shippingAddressCount(),
                composite.returnAddressCount(),
                composite.hasDefaultShippingAddress(),
                composite.hasDefaultReturnAddress(),
                composite.shippingPolicyCount(),
                composite.refundPolicyCount(),
                composite.hasDefaultShippingPolicy(),
                composite.hasDefaultRefundPolicy());
    }
}
