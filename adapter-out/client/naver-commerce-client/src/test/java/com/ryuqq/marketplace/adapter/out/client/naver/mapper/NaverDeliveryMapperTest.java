package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.DeliveryInfo;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NaverDeliveryMapper 단위 테스트")
class NaverDeliveryMapperTest {

    // ── 헬퍼 메서드 ──

    private ShippingPolicyResult shippingPolicy(
            String feeType, Long baseFee, Long freeThreshold,
            Long jejuFee, Long islandFee, Long returnFee, Long exchangeFee) {
        return new ShippingPolicyResult(
                1L, 1L, "테스트배송", true, true,
                feeType, "설명", baseFee, freeThreshold,
                jejuFee, islandFee, returnFee, exchangeFee,
                1, 3, null, Instant.now(), Instant.now());
    }

    // ── 테스트 ──

    @Nested
    @DisplayName("mapDeliveryInfo()")
    class MapDeliveryInfoTest {

        @Test
        @DisplayName("FREE 배송 - deliveryFeeType=FREE, baseFee=0")
        void freeShipping() {
            ShippingPolicyResult policy = shippingPolicy(
                    "FREE", 0L, null, null, null, null, null);

            DeliveryInfo result = NaverDeliveryMapper.mapDeliveryInfo(policy);

            assertThat(result.deliveryFee().deliveryFeeType()).isEqualTo("FREE");
            assertThat(result.deliveryFee().baseFee()).isZero();
            assertThat(result.deliveryFee().freeConditionalAmount()).isNull();
        }

        @Test
        @DisplayName("PAID 배송 - deliveryFeeType=PAID, baseFee 설정")
        void paidShipping() {
            ShippingPolicyResult policy = shippingPolicy(
                    "PAID", 3000L, null, null, null, null, null);

            DeliveryInfo result = NaverDeliveryMapper.mapDeliveryInfo(policy);

            assertThat(result.deliveryFee().deliveryFeeType()).isEqualTo("PAID");
            assertThat(result.deliveryFee().baseFee()).isEqualTo(3000);
            assertThat(result.deliveryFee().freeConditionalAmount()).isNull();
        }

        @Test
        @DisplayName("CONDITIONAL_FREE - freeConditionalAmount 설정")
        void conditionalFreeShipping() {
            ShippingPolicyResult policy = shippingPolicy(
                    "CONDITIONAL_FREE", 3000L, 50000L, null, null, null, null);

            DeliveryInfo result = NaverDeliveryMapper.mapDeliveryInfo(policy);

            assertThat(result.deliveryFee().deliveryFeeType()).isEqualTo("CONDITIONAL_FREE");
            assertThat(result.deliveryFee().baseFee()).isEqualTo(3000);
            assertThat(result.deliveryFee().freeConditionalAmount()).isEqualTo(50000L);
        }

        @Test
        @DisplayName("알 수 없는 타입이고 baseFee가 0이면 FREE로 처리")
        void unknownTypeWithZeroBaseFeeIsFree() {
            ShippingPolicyResult policy = shippingPolicy(
                    "UNKNOWN", 0L, null, null, null, null, null);

            DeliveryInfo result = NaverDeliveryMapper.mapDeliveryInfo(policy);

            assertThat(result.deliveryFee().deliveryFeeType()).isEqualTo("FREE");
        }

        @Test
        @DisplayName("알 수 없는 타입이고 baseFee > 0이면 PAID로 처리")
        void unknownTypeWithBaseFeeIsPaid() {
            ShippingPolicyResult policy = shippingPolicy(
                    "UNKNOWN", 5000L, null, null, null, null, null);

            DeliveryInfo result = NaverDeliveryMapper.mapDeliveryInfo(policy);

            assertThat(result.deliveryFee().deliveryFeeType()).isEqualTo("PAID");
        }

        @Test
        @DisplayName("baseFee가 null이면 0으로 처리")
        void nullBaseFeeTreatedAsZero() {
            ShippingPolicyResult policy = shippingPolicy(
                    "FREE", null, null, null, null, null, null);

            DeliveryInfo result = NaverDeliveryMapper.mapDeliveryInfo(policy);

            assertThat(result.deliveryFee().baseFee()).isZero();
        }

        @Test
        @DisplayName("deliveryFeePayType은 항상 PREPAID")
        void payTypeAlwaysPrepaid() {
            ShippingPolicyResult policy = shippingPolicy(
                    "PAID", 3000L, null, null, null, null, null);

            DeliveryInfo result = NaverDeliveryMapper.mapDeliveryInfo(policy);

            assertThat(result.deliveryFee().deliveryFeePayType()).isEqualTo("PREPAID");
        }

        @Test
        @DisplayName("deliveryCompany는 기본값 CJGLS")
        void deliveryCompanyDefault() {
            ShippingPolicyResult policy = shippingPolicy(
                    "FREE", 0L, null, null, null, null, null);

            DeliveryInfo result = NaverDeliveryMapper.mapDeliveryInfo(policy);

            assertThat(result.deliveryCompany()).isEqualTo("CJGLS");
        }

        @Test
        @DisplayName("제주/도서산간 추가비 있으면 deliveryFeeByArea 설정")
        void regionalFees() {
            ShippingPolicyResult policy = shippingPolicy(
                    "PAID", 3000L, null, 3000L, 5000L, null, null);

            DeliveryInfo result = NaverDeliveryMapper.mapDeliveryInfo(policy);

            assertThat(result.deliveryFeeByArea()).isNotNull();
            assertThat(result.deliveryFeeByArea().jejuAreaFee()).isEqualTo(3000L);
            assertThat(result.deliveryFeeByArea().isolatedAreaFee()).isEqualTo(5000L);
        }

        @Test
        @DisplayName("제주/도서산간 추가비 없으면 deliveryFeeByArea는 null")
        void noRegionalFees() {
            ShippingPolicyResult policy = shippingPolicy(
                    "PAID", 3000L, null, null, null, null, null);

            DeliveryInfo result = NaverDeliveryMapper.mapDeliveryInfo(policy);

            assertThat(result.deliveryFeeByArea()).isNull();
        }

        @Test
        @DisplayName("반품/교환 배송비 있으면 claimDeliveryInfo 설정")
        void claimFees() {
            ShippingPolicyResult policy = shippingPolicy(
                    "PAID", 3000L, null, null, null, 3000L, 5000L);

            DeliveryInfo result = NaverDeliveryMapper.mapDeliveryInfo(policy);

            assertThat(result.claimDeliveryInfo()).isNotNull();
            assertThat(result.claimDeliveryInfo().returnDeliveryFee()).isEqualTo(3000L);
            assertThat(result.claimDeliveryInfo().exchangeDeliveryFee()).isEqualTo(5000L);
        }

        @Test
        @DisplayName("반품/교환 배송비 없으면 claimDeliveryInfo는 null")
        void noClaimFees() {
            ShippingPolicyResult policy = shippingPolicy(
                    "FREE", 0L, null, null, null, null, null);

            DeliveryInfo result = NaverDeliveryMapper.mapDeliveryInfo(policy);

            assertThat(result.claimDeliveryInfo()).isNull();
        }
    }
}
