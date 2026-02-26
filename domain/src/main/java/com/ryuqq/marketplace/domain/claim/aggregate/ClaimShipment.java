package com.ryuqq.marketplace.domain.claim.aggregate;

import com.ryuqq.marketplace.domain.claim.id.ClaimShipmentId;
import com.ryuqq.marketplace.domain.claim.vo.ClaimShipmentMethod;
import com.ryuqq.marketplace.domain.claim.vo.ClaimShipmentStatus;
import com.ryuqq.marketplace.domain.claim.vo.ContactInfo;
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
