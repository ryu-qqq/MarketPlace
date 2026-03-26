package com.ryuqq.marketplace.adapter.out.persistence.claim.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * 클레임 수거 배송 JPA 엔티티.
 *
 * <p>claim_shipments 테이블과 매핑됩니다. soft delete 없이 BaseAuditEntity를 상속합니다.
 */
@Entity
@Table(name = "claim_shipments")
public class ClaimShipmentJpaEntity extends BaseAuditEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "method_type", length = 20)
    private String methodType;

    @Column(name = "courier_code", length = 50)
    private String courierCode;

    @Column(name = "courier_name", length = 100)
    private String courierName;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "fee_amount")
    private int feeAmount;

    @Column(name = "fee_payer", length = 10)
    private String feePayer;

    @Column(name = "fee_include_in_package")
    private boolean feeIncludeInPackage;

    @Column(name = "sender_name", length = 100)
    private String senderName;

    @Column(name = "sender_phone", length = 20)
    private String senderPhone;

    @Column(name = "sender_address", length = 500)
    private String senderAddress;

    @Column(name = "sender_address_detail", length = 500)
    private String senderAddressDetail;

    @Column(name = "sender_zipcode", length = 10)
    private String senderZipcode;

    @Column(name = "receiver_name", length = 100)
    private String receiverName;

    @Column(name = "receiver_phone", length = 20)
    private String receiverPhone;

    @Column(name = "receiver_address", length = 500)
    private String receiverAddress;

    @Column(name = "receiver_address_detail", length = 500)
    private String receiverAddressDetail;

    @Column(name = "receiver_zipcode", length = 10)
    private String receiverZipcode;

    @Column(name = "shipped_at")
    private Instant shippedAt;

    @Column(name = "received_at")
    private Instant receivedAt;

    /** JPA 스펙 요구사항 - 기본 생성자. */
    protected ClaimShipmentJpaEntity() {
        super();
    }

    private ClaimShipmentJpaEntity(
            String id,
            String status,
            String methodType,
            String courierCode,
            String courierName,
            String trackingNumber,
            int feeAmount,
            String feePayer,
            boolean feeIncludeInPackage,
            String senderName,
            String senderPhone,
            String senderAddress,
            String senderAddressDetail,
            String senderZipcode,
            String receiverName,
            String receiverPhone,
            String receiverAddress,
            String receiverAddressDetail,
            String receiverZipcode,
            Instant shippedAt,
            Instant receivedAt,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.status = status;
        this.methodType = methodType;
        this.courierCode = courierCode;
        this.courierName = courierName;
        this.trackingNumber = trackingNumber;
        this.feeAmount = feeAmount;
        this.feePayer = feePayer;
        this.feeIncludeInPackage = feeIncludeInPackage;
        this.senderName = senderName;
        this.senderPhone = senderPhone;
        this.senderAddress = senderAddress;
        this.senderAddressDetail = senderAddressDetail;
        this.senderZipcode = senderZipcode;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.receiverAddress = receiverAddress;
        this.receiverAddressDetail = receiverAddressDetail;
        this.receiverZipcode = receiverZipcode;
        this.shippedAt = shippedAt;
        this.receivedAt = receivedAt;
    }

    /**
     * 팩토리 메서드.
     *
     * @param id UUID 문자열
     * @param status 배송 상태
     * @param methodType 배송 방식 타입
     * @param courierCode 택배사 코드
     * @param courierName 택배사 명
     * @param trackingNumber 운송장 번호
     * @param feeAmount 배송비 금액
     * @param feePayer 배송비 부담 주체
     * @param feeIncludeInPackage 패키지 포함 여부
     * @param senderName 발송인 이름
     * @param senderPhone 발송인 연락처
     * @param senderAddress 발송인 기본 주소
     * @param senderAddressDetail 발송인 상세 주소
     * @param senderZipcode 발송인 우편번호
     * @param receiverName 수령인 이름
     * @param receiverPhone 수령인 연락처
     * @param receiverAddress 수령인 기본 주소
     * @param receiverAddressDetail 수령인 상세 주소
     * @param receiverZipcode 수령인 우편번호
     * @param shippedAt 발송 일시
     * @param receivedAt 수령 일시
     * @param createdAt 생성 일시
     * @param updatedAt 수정 일시
     * @return ClaimShipmentJpaEntity 인스턴스
     */
    public static ClaimShipmentJpaEntity create(
            String id,
            String status,
            String methodType,
            String courierCode,
            String courierName,
            String trackingNumber,
            int feeAmount,
            String feePayer,
            boolean feeIncludeInPackage,
            String senderName,
            String senderPhone,
            String senderAddress,
            String senderAddressDetail,
            String senderZipcode,
            String receiverName,
            String receiverPhone,
            String receiverAddress,
            String receiverAddressDetail,
            String receiverZipcode,
            Instant shippedAt,
            Instant receivedAt,
            Instant createdAt,
            Instant updatedAt) {
        return new ClaimShipmentJpaEntity(
                id,
                status,
                methodType,
                courierCode,
                courierName,
                trackingNumber,
                feeAmount,
                feePayer,
                feeIncludeInPackage,
                senderName,
                senderPhone,
                senderAddress,
                senderAddressDetail,
                senderZipcode,
                receiverName,
                receiverPhone,
                receiverAddress,
                receiverAddressDetail,
                receiverZipcode,
                shippedAt,
                receivedAt,
                createdAt,
                updatedAt);
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getMethodType() {
        return methodType;
    }

    public String getCourierCode() {
        return courierCode;
    }

    public String getCourierName() {
        return courierName;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public int getFeeAmount() {
        return feeAmount;
    }

    public String getFeePayer() {
        return feePayer;
    }

    public boolean isFeeIncludeInPackage() {
        return feeIncludeInPackage;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public String getSenderAddressDetail() {
        return senderAddressDetail;
    }

    public String getSenderZipcode() {
        return senderZipcode;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public String getReceiverAddressDetail() {
        return receiverAddressDetail;
    }

    public String getReceiverZipcode() {
        return receiverZipcode;
    }

    public Instant getShippedAt() {
        return shippedAt;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }
}
