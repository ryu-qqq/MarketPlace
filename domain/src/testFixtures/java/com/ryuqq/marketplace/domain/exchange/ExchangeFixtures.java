package com.ryuqq.marketplace.domain.exchange;

import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.claim.id.ClaimShipmentId;
import com.ryuqq.marketplace.domain.claim.vo.ClaimShipmentMethod;
import com.ryuqq.marketplace.domain.claim.vo.ContactInfo;
import com.ryuqq.marketplace.domain.claim.vo.FeePayer;
import com.ryuqq.marketplace.domain.claim.vo.ShipmentMethodType;
import com.ryuqq.marketplace.domain.claim.vo.ShippingFeeInfo;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimNumber;
import com.ryuqq.marketplace.domain.exchange.vo.AmountAdjustment;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeOption;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeReason;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeReasonType;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;

/**
 * ExchangeClaim 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 ExchangeClaim 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ExchangeFixtures {

    private ExchangeFixtures() {}

    // ===== 기본 상수 =====
    private static final String DEFAULT_CLAIM_ID = "01900000-0000-7000-0000-000000000001";
    private static final String DEFAULT_ORDER_ITEM_ID = "01900000-0000-7000-0000-000000000010";
    private static final long DEFAULT_SELLER_ID = 100L;
    private static final int DEFAULT_EXCHANGE_QTY = 1;
    private static final String DEFAULT_REQUESTED_BY = "buyer@example.com";
    private static final String DEFAULT_PROCESSED_BY = "admin@marketplace.com";
    private static final String DEFAULT_LINKED_ORDER_ID = "ORDER-20260101-9999";
    private static final String DEFAULT_SHIPMENT_ID = "01900000-0000-7000-0000-000000000099";

    // ===== ID Fixtures =====

    public static ExchangeClaimId defaultExchangeClaimId() {
        return ExchangeClaimId.of(DEFAULT_CLAIM_ID);
    }

    public static ExchangeClaimId exchangeClaimId(String value) {
        return ExchangeClaimId.of(value);
    }

    public static ExchangeClaimNumber defaultExchangeClaimNumber() {
        return ExchangeClaimNumber.of("EXC-20260218-0001");
    }

    public static OrderItemId defaultOrderItemId() {
        return OrderItemId.of(DEFAULT_ORDER_ITEM_ID);
    }

    // ===== VO Fixtures =====

    public static ExchangeReason defaultExchangeReason() {
        return new ExchangeReason(ExchangeReasonType.SIZE_CHANGE, "사이즈가 맞지 않아 교환 요청합니다");
    }

    public static ExchangeReason exchangeReason(ExchangeReasonType type, String detail) {
        return new ExchangeReason(type, detail);
    }

    public static ExchangeOption defaultExchangeOption() {
        return new ExchangeOption(1000L, "SKU-RED-M", 1001L, 2001L, "SKU-RED-XL", 1);
    }

    public static ExchangeOption exchangeOption(
            long originalProductId, String originalSkuCode,
            long targetProductGroupId, long targetProductId, String targetSkuCode, int quantity) {
        return new ExchangeOption(
                originalProductId, originalSkuCode,
                targetProductGroupId, targetProductId, targetSkuCode, quantity);
    }

    public static AmountAdjustment defaultAmountAdjustment() {
        return AmountAdjustment.calculate(
                Money.of(30000), Money.of(35000), Money.of(3000), Money.of(3000), FeePayer.BUYER);
    }

    public static AmountAdjustment zeroAmountAdjustment() {
        return AmountAdjustment.calculate(
                Money.of(30000), Money.of(30000), Money.zero(), Money.zero(), FeePayer.SELLER);
    }

    // ===== ClaimShipment Fixtures (인라인 생성) =====

    public static ClaimShipment defaultCollectShipment() {
        ClaimShipmentId shipmentId = ClaimShipmentId.of(DEFAULT_SHIPMENT_ID);
        ClaimShipmentMethod method =
                ClaimShipmentMethod.of(ShipmentMethodType.COURIER, "CJ001", "CJ대한통운");
        ShippingFeeInfo feeInfo = ShippingFeeInfo.of(Money.of(3000), FeePayer.BUYER, false);
        Address address = Address.of("12345", "서울시 강남구 테헤란로 1", "101호");
        ContactInfo sender = ContactInfo.of("홍길동", "010-1234-5678", address);
        ContactInfo receiver = ContactInfo.of("마켓플레이스 물류센터", "02-0000-0000", address);
        return ClaimShipment.forNew(shipmentId, method, feeInfo, sender, receiver);
    }

    // ===== ExchangeClaim Fixtures - forNew =====

    public static ExchangeClaim newExchangeClaim() {
        return ExchangeClaim.forNew(
                defaultExchangeClaimId(),
                defaultExchangeClaimNumber(),
                defaultOrderItemId(),
                DEFAULT_SELLER_ID,
                DEFAULT_EXCHANGE_QTY,
                defaultExchangeReason(),
                defaultExchangeOption(),
                defaultAmountAdjustment(),
                defaultCollectShipment(),
                DEFAULT_REQUESTED_BY,
                CommonVoFixtures.now());
    }

    // ===== ExchangeClaim Fixtures - reconstitute 상태별 =====

    public static ExchangeClaim requestedExchangeClaim() {
        return reconstitute(ExchangeStatus.REQUESTED);
    }

    public static ExchangeClaim collectingExchangeClaim() {
        return reconstitute(ExchangeStatus.COLLECTING);
    }

    public static ExchangeClaim collectedExchangeClaim() {
        return reconstitute(ExchangeStatus.COLLECTED);
    }

    public static ExchangeClaim preparingExchangeClaim() {
        return reconstitute(ExchangeStatus.PREPARING);
    }

    public static ExchangeClaim shippingExchangeClaim() {
        return reconstitute(ExchangeStatus.SHIPPING);
    }

    public static ExchangeClaim completedExchangeClaim() {
        return reconstitute(ExchangeStatus.COMPLETED);
    }

    public static ExchangeClaim rejectedExchangeClaim() {
        return reconstitute(ExchangeStatus.REJECTED);
    }

    public static ExchangeClaim cancelledExchangeClaim() {
        return reconstitute(ExchangeStatus.CANCELLED);
    }

    private static ExchangeClaim reconstitute(ExchangeStatus status) {
        return ExchangeClaim.reconstitute(
                defaultExchangeClaimId(),
                defaultExchangeClaimNumber(),
                defaultOrderItemId(),
                DEFAULT_SELLER_ID,
                DEFAULT_EXCHANGE_QTY,
                status,
                defaultExchangeReason(),
                defaultExchangeOption(),
                defaultAmountAdjustment(),
                defaultCollectShipment(),
                null,
                status == ExchangeStatus.SHIPPING || status == ExchangeStatus.COMPLETED
                        ? DEFAULT_LINKED_ORDER_ID
                        : null,
                DEFAULT_REQUESTED_BY,
                status != ExchangeStatus.REQUESTED ? DEFAULT_PROCESSED_BY : null,
                CommonVoFixtures.yesterday(),
                status != ExchangeStatus.REQUESTED ? CommonVoFixtures.yesterday() : null,
                status == ExchangeStatus.COMPLETED ? CommonVoFixtures.now() : null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now());
    }
}
