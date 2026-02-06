package com.ryuqq.marketplace.application.selleraddress.service.query;

import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressMetadataResult;
import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressReadManager;
import com.ryuqq.marketplace.application.selleraddress.port.in.query.GetSellerAddressMetadataUseCase;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import java.util.List;
import org.springframework.stereotype.Service;

/** 셀러 주소 메타데이터 조회 Service. */
@Service
public class GetSellerAddressMetadataService implements GetSellerAddressMetadataUseCase {

    private final SellerAddressReadManager readManager;

    public GetSellerAddressMetadataService(SellerAddressReadManager readManager) {
        this.readManager = readManager;
    }

    @Override
    public SellerAddressMetadataResult execute(Long sellerId) {
        SellerId id = SellerId.of(sellerId);
        List<SellerAddress> addresses = readManager.findAllBySellerId(id);

        long totalCount = addresses.size();
        long shippingCount = addresses.stream().filter(SellerAddress::isShippingAddress).count();
        long returnCount = addresses.stream().filter(SellerAddress::isReturnAddress).count();

        boolean hasDefaultShipping =
                addresses.stream()
                        .filter(SellerAddress::isShippingAddress)
                        .anyMatch(SellerAddress::isDefaultAddress);

        boolean hasDefaultReturn =
                addresses.stream()
                        .filter(SellerAddress::isReturnAddress)
                        .anyMatch(SellerAddress::isDefaultAddress);

        return new SellerAddressMetadataResult(
                totalCount, shippingCount, returnCount, hasDefaultShipping, hasDefaultReturn);
    }
}
