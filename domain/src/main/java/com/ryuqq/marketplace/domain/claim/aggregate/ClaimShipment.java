package com.ryuqq.marketplace.domain.claim.aggregate;

import com.ryuqq.marketplace.domain.claim.id.ClaimShipmentId;
import com.ryuqq.marketplace.domain.claim.vo.ClaimShipmentMethod;
import com.ryuqq.marketplace.domain.claim.vo.ClaimShipmentStatus;
import com.ryuqq.marketplace.domain.claim.vo.ContactInfo;
import com.ryuqq.marketplace.domain.claim.vo.ShipmentMethodType;
import com.ryuqq.marketplace.domain.claim.vo.ShippingFeeInfo;
import java.time.Instant;

/** 클레임 수거 배송 정보. */
public class ClaimShipment {

    private final ClaimShipmentId id;
    private ClaimShipmentStatus status;
    private ClaimShipmentMethod method;
    private String trackingNumber;
    private final ShippingFeeInfo feeInfo;
    private final ContactInfo sender;
    private final ContactInfo receiver;
    private Instant shippedAt;
    private Instant receivedAt;

    private ClaimShipment(
            ClaimShipmentId id,
            ClaimShipmentStatus status,
            ClaimShipmentMethod method,
            String trackingNumber,
            ShippingFeeInfo feeInfo,
            ContactInfo sender,
            ContactInfo receiver,
            Instant shippedAt,
            Instant receivedAt) {
        this.id = id;
        this.status = status;
        this.method = method;
        this.trackingNumber = trackingNumber;
        this.feeInfo = feeInfo;
        this.sender = sender;
        this.receiver = receiver;
        this.shippedAt = shippedAt;
        this.receivedAt = receivedAt;
    }

    public static ClaimShipment forNew(
            ClaimShipmentId id,
            ClaimShipmentMethod method,
            ShippingFeeInfo feeInfo,
            ContactInfo sender,
            ContactInfo receiver) {
        return new ClaimShipment(
                id,
                ClaimShipmentStatus.PENDING,
                method,
                null,
                feeInfo,
                sender,
                receiver,
                null,
                null);
    }

    /**
     * 외부 채널 동기화 전용 팩토리 메서드.
     *
     * <p>수거 배송사 코드와 송장번호만 있는 경우 사용합니다. sender/receiver 정보는 null로 허용합니다.
     *
     * @param id ClaimShipmentId
     * @param courierCode 택배사 코드
     * @param courierName 택배사 명
     * @param trackingNumber 송장번호
     * @param feeInfo 배송비 정보 (null이면 무료 배송)
     * @param now 현재 시간
     * @return ClaimShipment (수거중 상태)
     */
    public static ClaimShipment forSync(
            ClaimShipmentId id,
            String courierCode,
            String courierName,
            String trackingNumber,
            ShippingFeeInfo feeInfo,
            Instant now) {
        ClaimShipmentMethod method =
                ClaimShipmentMethod.of(ShipmentMethodType.COURIER, courierCode, courierName);
        return new ClaimShipment(
                id,
                ClaimShipmentStatus.IN_TRANSIT,
                method,
                trackingNumber,
                feeInfo != null ? feeInfo : ShippingFeeInfo.free(),
                null,
                null,
                now,
                null);
    }

    public static ClaimShipment reconstitute(
            ClaimShipmentId id,
            ClaimShipmentStatus status,
            ClaimShipmentMethod method,
            String trackingNumber,
            ShippingFeeInfo feeInfo,
            ContactInfo sender,
            ContactInfo receiver,
            Instant shippedAt,
            Instant receivedAt) {
        return new ClaimShipment(
                id,
                status,
                method,
                trackingNumber,
                feeInfo,
                sender,
                receiver,
                shippedAt,
                receivedAt);
    }

    public void ship(String trackingNumber, Instant now) {
        this.status = ClaimShipmentStatus.IN_TRANSIT;
        this.trackingNumber = trackingNumber;
        this.shippedAt = now;
    }

    public void complete(Instant now) {
        this.status = ClaimShipmentStatus.DELIVERED;
        this.receivedAt = now;
    }

    public void fail() {
        this.status = ClaimShipmentStatus.FAILED;
    }

    public void updateMethod(ClaimShipmentMethod method) {
        this.method = method;
    }

    public void updateTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public ClaimShipmentId id() {
        return id;
    }

    public ClaimShipmentStatus status() {
        return status;
    }

    public ClaimShipmentMethod method() {
        return method;
    }

    public String trackingNumber() {
        return trackingNumber;
    }

    public ShippingFeeInfo feeInfo() {
        return feeInfo;
    }

    public ContactInfo sender() {
        return sender;
    }

    public ContactInfo receiver() {
        return receiver;
    }

    public Instant shippedAt() {
        return shippedAt;
    }

    public Instant receivedAt() {
        return receivedAt;
    }

    public boolean isDelivered() {
        return status == ClaimShipmentStatus.DELIVERED;
    }
}
