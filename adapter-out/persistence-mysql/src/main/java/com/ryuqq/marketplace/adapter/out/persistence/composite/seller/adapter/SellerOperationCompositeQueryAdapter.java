package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerOperationCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerOperationCompositeDto.AddressSummaryDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerOperationCompositeDto.PolicySummaryDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.repository.SellerOperationCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.selleraddress.dto.composite.SellerOperationCompositeResult;
import com.ryuqq.marketplace.application.selleraddress.port.out.query.SellerOperationCompositeQueryPort;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerOperationCompositeQueryAdapter - 셀러 운영 메타데이터 Composite 조회 Adapter.
 *
 * <p>주소, 배송정책, 환불정책의 raw 데이터를 조회하고 집계하여 변환.
 *
 * <p>PER-ADP-001: Adapter는 @Component로 등록.
 *
 * <p>PER-ADP-002: Port 인터페이스 구현.
 */
@Component
public class SellerOperationCompositeQueryAdapter implements SellerOperationCompositeQueryPort {

    private final SellerOperationCompositeQueryDslRepository repository;

    public SellerOperationCompositeQueryAdapter(
            SellerOperationCompositeQueryDslRepository repository) {
        this.repository = repository;
    }

    @Override
    public SellerOperationCompositeResult findOperationMetadataBySellerId(SellerId sellerId) {
        SellerOperationCompositeDto dto = repository.findBySellerId(sellerId.value());
        return toResult(dto);
    }

    private SellerOperationCompositeResult toResult(SellerOperationCompositeDto dto) {
        List<AddressSummaryDto> addresses = dto.addresses();
        List<PolicySummaryDto> shippingPolicies = dto.shippingPolicies();
        List<PolicySummaryDto> refundPolicies = dto.refundPolicies();

        long addressTotalCount = addresses.size();
        long shippingAddressCount =
                addresses.stream()
                        .filter(a -> AddressType.SHIPPING.name().equals(a.addressType()))
                        .count();
        long returnAddressCount =
                addresses.stream()
                        .filter(a -> AddressType.RETURN.name().equals(a.addressType()))
                        .count();
        boolean hasDefaultShippingAddress =
                addresses.stream()
                        .anyMatch(
                                a ->
                                        AddressType.SHIPPING.name().equals(a.addressType())
                                                && a.defaultAddress());
        boolean hasDefaultReturnAddress =
                addresses.stream()
                        .anyMatch(
                                a ->
                                        AddressType.RETURN.name().equals(a.addressType())
                                                && a.defaultAddress());

        long shippingPolicyCount = shippingPolicies.size();
        boolean hasDefaultShippingPolicy =
                shippingPolicies.stream().anyMatch(PolicySummaryDto::defaultPolicy);

        long refundPolicyCount = refundPolicies.size();
        boolean hasDefaultRefundPolicy =
                refundPolicies.stream().anyMatch(PolicySummaryDto::defaultPolicy);

        return new SellerOperationCompositeResult(
                addressTotalCount,
                shippingAddressCount,
                returnAddressCount,
                hasDefaultShippingAddress,
                hasDefaultReturnAddress,
                shippingPolicyCount,
                hasDefaultShippingPolicy,
                refundPolicyCount,
                hasDefaultRefundPolicy);
    }
}
