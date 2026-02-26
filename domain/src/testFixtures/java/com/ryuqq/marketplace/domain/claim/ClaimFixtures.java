package com.ryuqq.marketplace.domain.claim;

import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.claim.id.ClaimShipmentId;
import com.ryuqq.marketplace.domain.claim.vo.ClaimShipmentMethod;
import com.ryuqq.marketplace.domain.claim.vo.ClaimShipmentStatus;
import com.ryuqq.marketplace.domain.claim.vo.ContactInfo;
import com.ryuqq.marketplace.domain.claim.vo.FeePayer;
import com.ryuqq.marketplace.domain.claim.vo.ShipmentMethodType;
import com.ryuqq.marketplace.domain.claim.vo.ShippingFeeInfo;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.common.vo.Money;

/**
 * ClaimShipment 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 ClaimShipment 관련 객체들을 생성합니다.
 */
public final class ClaimFixtures {

    private ClaimFixtures() {}

    // ===== 기본 상수 =====
    private static final String DEFAULT_CLAIM_SHIPMENT_ID = "CLAIM-SHIP-0001";
    private static final String DEFAULT_TRACKING_NUMBER = "1234567890123";
    private static final String DEFAULT_COURIER_CODE = "CJ";
    private static final String DEFAULT_COURIER_NAME = "CJ대한통운";

    // ===== ID Fixtures =====

    public static ClaimShipmentId defaultClaimShipmentId() {
        return ClaimShipmentId.of(DEFAULT_CLAIM_SHIPMENT_ID);
    }

    public static ClaimShipmentId claimShipmentId(String value) {
        return ClaimShipmentId.of(value);
    }

    // ===== VO Fixtures =====

    public static ClaimShipmentMethod defaultClaimShipmentMethod() {
        return ClaimShipmentMethod.of(
                ShipmentMethodType.COURIER, DEFAULT_COURIER_CODE, DEFAULT_COURIER_NAME);
    }

    public static ClaimShipmentMethod visitMethod() {
        return ClaimShipmentMethod.visit();
    }

    public static ClaimShipmentMethod quickMethod() {
        return ClaimShipmentMethod.of(ShipmentMethodType.QUICK, "QUICK", "퀵서비스");
    }

    public static ContactInfo defaultContactInfo() {
        return ContactInfo.of(
                "홍길동", "010-1234-5678", Address.of("12345", "서울시 강남구 테헤란로 1", "101호"));
    }

    public static ContactInfo senderContactInfo() {
        return ContactInfo.of(
                "구매자", "010-9876-5432", Address.of("54321", "부산시 해운대구 해운대로 99", "202호"));
    }

    public static ContactInfo receiverContactInfo() {
        return ContactInfo.of(
                "판매자", "010-1111-2222", Address.of("11111", "경기도 성남시 분당구 판교로 10", "창고동 1층"));
    }

    public static ShippingFeeInfo defaultShippingFeeInfo() {
        return ShippingFeeInfo.of(Money.of(3000), FeePayer.BUYER, false);
    }

    public static ShippingFeeInfo freeShippingFeeInfo() {
        return ShippingFeeInfo.free();
    }

    public static ShippingFeeInfo sellerPayShippingFeeInfo() {
        return ShippingFeeInfo.of(Money.of(3000), FeePayer.SELLER, true);
    }

    // ===== Aggregate Fixtures =====

    public static ClaimShipment newClaimShipment() {
        return ClaimShipment.forNew(
                defaultClaimShipmentId(),
                defaultClaimShipmentMethod(),
                defaultShippingFeeInfo(),
                senderContactInfo(),
                receiverContactInfo());
    }

    public static ClaimShipment inTransitClaimShipment() {
        ClaimShipment shipment = newClaimShipment();
        shipment.ship(DEFAULT_TRACKING_NUMBER, CommonVoFixtures.yesterday());
        return shipment;
    }

    public static ClaimShipment deliveredClaimShipment() {
        ClaimShipment shipment = inTransitClaimShipment();
        shipment.complete(CommonVoFixtures.now());
        return shipment;
    }

    public static ClaimShipment failedClaimShipment() {
        ClaimShipment shipment = inTransitClaimShipment();
        shipment.fail();
        return shipment;
    }

    public static ClaimShipment reconstitutedClaimShipment(
            ClaimShipmentId id, ClaimShipmentStatus status) {
        return ClaimShipment.reconstitute(
                id,
                status,
                defaultClaimShipmentMethod(),
                DEFAULT_TRACKING_NUMBER,
                defaultShippingFeeInfo(),
                senderContactInfo(),
                receiverContactInfo(),
                CommonVoFixtures.yesterday(),
                status == ClaimShipmentStatus.DELIVERED ? CommonVoFixtures.now() : null);
    }
}
