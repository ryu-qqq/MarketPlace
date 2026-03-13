package com.ryuqq.marketplace.domain.outboundproduct.aggregate;

import com.ryuqq.marketplace.domain.outboundproduct.id.OutboundProductId;
import com.ryuqq.marketplace.domain.outboundproduct.vo.OutboundProductStatus;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import java.time.Instant;

/**
 * OutboundProduct Aggregate Root.
 *
 * <p>내부 ProductGroup과 외부 세일즈 채널 상품 간의 ID 매핑을 관리합니다. OutboundSyncOutbox(동기화 이벤트, 1회성)와 달리 영속적으로 유지되는
 * 매핑 정보입니다.
 */
public class OutboundProduct {

    private final OutboundProductId id;
    private final ProductGroupId productGroupId;
    private final SalesChannelId salesChannelId;
    private final long shopId;
    private String externalProductId;
    private OutboundProductStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private OutboundProduct(
            OutboundProductId id,
            ProductGroupId productGroupId,
            SalesChannelId salesChannelId,
            long shopId,
            String externalProductId,
            OutboundProductStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.salesChannelId = salesChannelId;
        this.shopId = shopId;
        this.externalProductId = externalProductId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 외부 상품 ID 없이 신규 생성 (검수 후 아웃바운드 동기화 시). */
    public static OutboundProduct forNew(
            ProductGroupId productGroupId,
            SalesChannelId salesChannelId,
            long shopId,
            Instant now) {
        return new OutboundProduct(
                OutboundProductId.forNew(),
                productGroupId,
                salesChannelId,
                shopId,
                null,
                OutboundProductStatus.PENDING_REGISTRATION,
                now,
                now);
    }

    /** 외부 상품 ID를 이미 알고 있는 경우의 신규 생성 (세토프 레거시 인바운드). */
    public static OutboundProduct forNewWithExternalId(
            ProductGroupId productGroupId,
            SalesChannelId salesChannelId,
            long shopId,
            String externalProductId,
            Instant now) {
        return new OutboundProduct(
                OutboundProductId.forNew(),
                productGroupId,
                salesChannelId,
                shopId,
                externalProductId,
                OutboundProductStatus.REGISTERED,
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    public static OutboundProduct reconstitute(
            OutboundProductId id,
            ProductGroupId productGroupId,
            SalesChannelId salesChannelId,
            long shopId,
            String externalProductId,
            OutboundProductStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new OutboundProduct(
                id,
                productGroupId,
                salesChannelId,
                shopId,
                externalProductId,
                status,
                createdAt,
                updatedAt);
    }

    /** 외부 채널 등록 성공 시 외부 상품 ID 설정. */
    public void registerExternalProduct(String externalProductId, Instant now) {
        if (!status.isPendingRegistration()) {
            throw new IllegalStateException(
                    "PENDING_REGISTRATION 상태에서만 외부 상품을 등록할 수 있습니다. 현재 상태: " + status);
        }
        this.externalProductId = externalProductId;
        this.status = OutboundProductStatus.REGISTERED;
        this.updatedAt = now;
    }

    /** 외부 채널 등록 실패 처리. */
    public void markRegistrationFailed(Instant now) {
        if (!status.isPendingRegistration()) {
            throw new IllegalStateException(
                    "PENDING_REGISTRATION 상태에서만 실패 처리할 수 있습니다. 현재 상태: " + status);
        }
        this.status = OutboundProductStatus.REGISTRATION_FAILED;
        this.updatedAt = now;
    }

    /** 실패 후 재시도 대기 상태로 전환. */
    public void retryRegistration(Instant now) {
        if (!status.isRegistrationFailed()) {
            throw new IllegalStateException(
                    "REGISTRATION_FAILED 상태에서만 재시도할 수 있습니다. 현재 상태: " + status);
        }
        this.status = OutboundProductStatus.PENDING_REGISTRATION;
        this.updatedAt = now;
    }

    /** 외부 채널 삭제 성공 시. externalProductId는 보존 (재등록 참조용). REGISTERED에서만 가능. */
    public void deregister(Instant now) {
        if (!status.isRegistered()) {
            throw new IllegalStateException("REGISTERED 상태에서만 등록 해제할 수 있습니다. 현재 상태: " + status);
        }
        this.status = OutboundProductStatus.DEREGISTERED;
        this.updatedAt = now;
    }

    /** 재활성화 시 재등록 대기 상태로 전환. DEREGISTERED에서만 가능. */
    public void prepareReregistration(Instant now) {
        if (!status.isDeregistered()) {
            throw new IllegalStateException("DEREGISTERED 상태에서만 재등록 준비할 수 있습니다. 현재 상태: " + status);
        }
        this.status = OutboundProductStatus.PENDING_REGISTRATION;
        this.updatedAt = now;
    }

    public boolean isRegistered() {
        return status.isRegistered();
    }

    public boolean isDeregistered() {
        return status.isDeregistered();
    }

    public boolean isNew() {
        return id.isNew();
    }

    // Getters

    public OutboundProductId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public ProductGroupId productGroupId() {
        return productGroupId;
    }

    public Long productGroupIdValue() {
        return productGroupId.value();
    }

    public SalesChannelId salesChannelId() {
        return salesChannelId;
    }

    public Long salesChannelIdValue() {
        return salesChannelId.value();
    }

    public long shopId() {
        return shopId;
    }

    public String externalProductId() {
        return externalProductId;
    }

    public OutboundProductStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
