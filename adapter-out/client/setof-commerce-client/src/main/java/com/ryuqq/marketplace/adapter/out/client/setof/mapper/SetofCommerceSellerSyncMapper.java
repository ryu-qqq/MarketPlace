package com.ryuqq.marketplace.adapter.out.client.setof.mapper;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofRefundPolicySyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofSellerAddressSyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofSellerSyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofShippingPolicySyncRequest;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import org.springframework.stereotype.Component;

@Component
public class SetofCommerceSellerSyncMapper {

    public SetofSellerSyncRequest toSellerRequest(Seller seller) {
        return new SetofSellerSyncRequest(
                seller.idValue(),
                seller.sellerNameValue(),
                seller.displayNameValue(),
                seller.logoUrlValue(),
                seller.descriptionValue(),
                seller.isActive());
    }

    public SetofShippingPolicySyncRequest toShippingPolicyRequest(ShippingPolicy policy) {
        return new SetofShippingPolicySyncRequest(
                policy.idValue(),
                policy.sellerIdValue(),
                policy.policyNameValue(),
                policy.isDefaultPolicy(),
                policy.isActive(),
                policy.shippingFeeType().name(),
                policy.baseFeeValue(),
                policy.freeThresholdValue(),
                policy.jejuExtraFeeValue(),
                policy.islandExtraFeeValue(),
                policy.returnFeeValue(),
                policy.exchangeFeeValue(),
                policy.leadTimeMinDays(),
                policy.leadTimeMaxDays());
    }

    public SetofRefundPolicySyncRequest toRefundPolicyRequest(RefundPolicy policy) {
        return new SetofRefundPolicySyncRequest(
                policy.idValue(),
                policy.sellerIdValue(),
                policy.policyNameValue(),
                policy.isDefaultPolicy(),
                policy.isActive(),
                policy.returnPeriodDays(),
                policy.exchangePeriodDays(),
                policy.nonReturnableConditions().stream().map(Enum::name).toList(),
                policy.isPartialRefundEnabled(),
                policy.isInspectionRequired(),
                policy.inspectionPeriodDays(),
                policy.additionalInfo());
    }

    public SetofSellerAddressSyncRequest toSellerAddressRequest(SellerAddress address) {
        return new SetofSellerAddressSyncRequest(
                address.idValue(),
                address.sellerIdValue(),
                address.addressType().name(),
                address.addressNameValue(),
                address.addressZipCode(),
                address.addressRoad(),
                address.addressDetail(),
                address.isDefaultAddress());
    }
}
