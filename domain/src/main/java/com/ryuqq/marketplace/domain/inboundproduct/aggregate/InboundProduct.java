package com.ryuqq.marketplace.domain.inboundproduct.aggregate;

import com.ryuqq.marketplace.domain.inboundproduct.id.InboundProductId;
import com.ryuqq.marketplace.domain.inboundproduct.vo.ExternalProductCode;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductStatus;
import java.time.Instant;

/**
 * InboundProduct Aggregate Root — 순수 매핑 레지스트리.
 *
 * <p>외부 소스(크롤링 등)에서 수신한 상품의 외부 코드 ↔ 내부 ID 매핑을 관리합니다. 상품 데이터(가격, 이미지, 옵션 등)는 저장하지 않으며, 수신 시점에 동기적으로
 * 내부 ProductGroup을 생성/갱신합니다.
 */
public class InboundProduct {

    private final InboundProductId id;
    private final Long inboundSourceId;
    private ExternalProductCode externalProductCode;
    private String externalBrandCode;
    private String externalCategoryCode;
    private Long internalBrandId;
    private Long internalCategoryId;
    private Long internalProductGroupId;
    private final Long sellerId;
    private InboundProductStatus status;
    private Long resolvedShippingPolicyId;
    private Long resolvedRefundPolicyId;
    private Long resolvedNoticeCategoryId;
    private final Instant createdAt;
    private Instant updatedAt;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private InboundProduct(
            InboundProductId id,
            Long inboundSourceId,
            ExternalProductCode externalProductCode,
            String externalBrandCode,
            String externalCategoryCode,
            Long internalBrandId,
            Long internalCategoryId,
            Long internalProductGroupId,
            Long sellerId,
            InboundProductStatus status,
            Long resolvedShippingPolicyId,
            Long resolvedRefundPolicyId,
            Long resolvedNoticeCategoryId,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.inboundSourceId = inboundSourceId;
        this.externalProductCode = externalProductCode;
        this.externalBrandCode = externalBrandCode;
        this.externalCategoryCode = externalCategoryCode;
        this.internalBrandId = internalBrandId;
        this.internalCategoryId = internalCategoryId;
        this.internalProductGroupId = internalProductGroupId;
        this.sellerId = sellerId;
        this.status = status;
        this.resolvedShippingPolicyId = resolvedShippingPolicyId;
        this.resolvedRefundPolicyId = resolvedRefundPolicyId;
        this.resolvedNoticeCategoryId = resolvedNoticeCategoryId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 InboundProduct 매핑 레코드 생성. */
    public static InboundProduct forNew(
            Long inboundSourceId,
            ExternalProductCode externalProductCode,
            String externalBrandCode,
            String externalCategoryCode,
            Long sellerId,
            Instant now) {
        return new InboundProduct(
                InboundProductId.forNew(),
                inboundSourceId,
                externalProductCode,
                externalBrandCode,
                externalCategoryCode,
                null,
                null,
                null,
                sellerId,
                InboundProductStatus.RECEIVED,
                null,
                null,
                null,
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static InboundProduct reconstitute(
            InboundProductId id,
            Long inboundSourceId,
            ExternalProductCode externalProductCode,
            String externalBrandCode,
            String externalCategoryCode,
            Long internalBrandId,
            Long internalCategoryId,
            Long internalProductGroupId,
            Long sellerId,
            InboundProductStatus status,
            Long resolvedShippingPolicyId,
            Long resolvedRefundPolicyId,
            Long resolvedNoticeCategoryId,
            Instant createdAt,
            Instant updatedAt) {
        return new InboundProduct(
                id,
                inboundSourceId,
                externalProductCode,
                externalBrandCode,
                externalCategoryCode,
                internalBrandId,
                internalCategoryId,
                internalProductGroupId,
                sellerId,
                status,
                resolvedShippingPolicyId,
                resolvedRefundPolicyId,
                resolvedNoticeCategoryId,
                createdAt,
                updatedAt);
    }

    /** 브랜드/카테고리 매핑 적용. RECEIVED 또는 PENDING_MAPPING 상태에서만 가능. */
    public void applyMapping(Long internalBrandId, Long internalCategoryId, Instant now) {
        if (!status.canApplyMapping()) {
            throw new IllegalStateException(
                    "매핑 적용은 RECEIVED 또는 PENDING_MAPPING 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.internalBrandId = internalBrandId;
        this.internalCategoryId = internalCategoryId;
        this.status = InboundProductStatus.MAPPED;
        this.updatedAt = now;
    }

    /** 정책 해석 결과 적용. MAPPED 상태에서만 가능. */
    public void applyResolution(
            Long shippingPolicyId, Long refundPolicyId, Long noticeCategoryId, Instant now) {
        if (!status.isMapped()) {
            throw new IllegalStateException("해석 적용은 MAPPED 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.resolvedShippingPolicyId = shippingPolicyId;
        this.resolvedRefundPolicyId = refundPolicyId;
        this.resolvedNoticeCategoryId = noticeCategoryId;
        this.updatedAt = now;
    }

    /** 내부 ProductGroup 변환 완료 처리. */
    public void markConverted(Long internalProductGroupId, Instant now) {
        this.internalProductGroupId = internalProductGroupId;
        this.status = InboundProductStatus.CONVERTED;
        this.updatedAt = now;
    }

    /** 외부 상품 코드 변경. */
    public void assignExternalProductCode(ExternalProductCode code, Instant now) {
        this.externalProductCode = code;
        this.updatedAt = now;
    }

    public boolean isConverted() {
        return status.isConverted();
    }

    public boolean isNew() {
        return id.isNew();
    }

    // Getters

    public InboundProductId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long inboundSourceId() {
        return inboundSourceId;
    }

    public ExternalProductCode externalProductCode() {
        return externalProductCode;
    }

    public String externalProductCodeValue() {
        return externalProductCode.value();
    }

    public String externalBrandCode() {
        return externalBrandCode;
    }

    public String externalCategoryCode() {
        return externalCategoryCode;
    }

    public Long internalBrandId() {
        return internalBrandId;
    }

    public Long internalCategoryId() {
        return internalCategoryId;
    }

    public Long internalProductGroupId() {
        return internalProductGroupId;
    }

    public Long sellerId() {
        return sellerId;
    }

    public InboundProductStatus status() {
        return status;
    }

    public Long resolvedShippingPolicyId() {
        return resolvedShippingPolicyId;
    }

    public Long resolvedRefundPolicyId() {
        return resolvedRefundPolicyId;
    }

    public Long resolvedNoticeCategoryId() {
        return resolvedNoticeCategoryId;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
