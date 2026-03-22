package com.ryuqq.marketplace.adapter.out.client.setof.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofRefundPolicySyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofSellerAddressSyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofSellerSyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofShippingPolicySyncRequest;
import com.ryuqq.marketplace.domain.refundpolicy.RefundPolicyFixtures;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.SellerAddressFixtures;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.shippingpolicy.ShippingPolicyFixtures;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SetofCommerceSellerSyncMapper 단위 테스트")
class SetofCommerceSellerSyncMapperTest {

    private final SetofCommerceSellerSyncMapper sut = new SetofCommerceSellerSyncMapper();

    @Nested
    @DisplayName("toSellerRequest()")
    class ToSellerRequestTest {

        @Test
        @DisplayName("Seller를 SetofSellerSyncRequest로 변환한다")
        void convertsSeller() {
            Seller seller = SellerFixtures.activeSeller();

            SetofSellerSyncRequest result = sut.toSellerRequest(seller);

            assertThat(result.sellerId()).isEqualTo(seller.idValue());
            assertThat(result.sellerName()).isEqualTo(seller.sellerNameValue());
            assertThat(result.displayName()).isEqualTo(seller.displayNameValue());
            assertThat(result.active()).isEqualTo(seller.isActive());
        }
    }

    @Nested
    @DisplayName("toShippingPolicyRequest()")
    class ToShippingPolicyRequestTest {

        @Test
        @DisplayName("ShippingPolicy를 SetofShippingPolicySyncRequest로 변환한다")
        void convertsShippingPolicy() {
            ShippingPolicy policy = ShippingPolicyFixtures.activeShippingPolicy();

            SetofShippingPolicySyncRequest result = sut.toShippingPolicyRequest(policy);

            assertThat(result.id()).isEqualTo(policy.idValue());
            assertThat(result.sellerId()).isEqualTo(policy.sellerIdValue());
            assertThat(result.policyName()).isEqualTo(policy.policyNameValue());
            assertThat(result.defaultPolicy()).isEqualTo(policy.isDefaultPolicy());
            assertThat(result.active()).isEqualTo(policy.isActive());
            assertThat(result.shippingFeeType()).isEqualTo(policy.shippingFeeType().name());
        }
    }

    @Nested
    @DisplayName("toRefundPolicyRequest()")
    class ToRefundPolicyRequestTest {

        @Test
        @DisplayName("RefundPolicy를 SetofRefundPolicySyncRequest로 변환한다")
        void convertsRefundPolicy() {
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy();

            SetofRefundPolicySyncRequest result = sut.toRefundPolicyRequest(policy);

            assertThat(result.id()).isEqualTo(policy.idValue());
            assertThat(result.sellerId()).isEqualTo(policy.sellerIdValue());
            assertThat(result.policyName()).isEqualTo(policy.policyNameValue());
            assertThat(result.defaultPolicy()).isEqualTo(policy.isDefaultPolicy());
            assertThat(result.active()).isEqualTo(policy.isActive());
            assertThat(result.returnPeriodDays()).isEqualTo(policy.returnPeriodDays());
            assertThat(result.exchangePeriodDays()).isEqualTo(policy.exchangePeriodDays());
        }
    }

    @Nested
    @DisplayName("toSellerAddressRequest()")
    class ToSellerAddressRequestTest {

        @Test
        @DisplayName("SellerAddress를 SetofSellerAddressSyncRequest로 변환한다")
        void convertsSellerAddress() {
            SellerAddress address = SellerAddressFixtures.defaultShippingAddress(1L, 1L);

            SetofSellerAddressSyncRequest result = sut.toSellerAddressRequest(address);

            assertThat(result.id()).isEqualTo(address.idValue());
            assertThat(result.sellerId()).isEqualTo(address.sellerIdValue());
            assertThat(result.addressType()).isEqualTo(address.addressType().name());
            assertThat(result.defaultAddress()).isEqualTo(address.isDefaultAddress());
        }
    }
}
