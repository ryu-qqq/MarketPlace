package com.ryuqq.marketplace.domain.inboundproduct.aggregate;

import com.ryuqq.marketplace.domain.inboundproduct.id.InboundProductId;
import com.ryuqq.marketplace.domain.inboundproduct.vo.ExternalProductCode;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductDiff;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductStatus;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductUpdateData;
import java.time.Instant;

/**
 * InboundProduct Aggregate Root.
 *
 * <p>외부 소스(레거시 세토프, 크롤링)에서 수신한 상품 데이터를 관리합니다. 수신 → 매핑 → 변환(ProductGroup) 파이프라인의 시작점입니다.
 *
 * <p>이미지/옵션/상품/고시정보 등 상세 데이터는 정형화된 {@link InboundProductPayload} VO로 관리합니다.
 */
public class InboundProduct {

    private final InboundProductId id;
    private final Long inboundSourceId;
    private ExternalProductCode externalProductCode;
    private String productName;
    private String externalBrandCode;
    private String externalCategoryCode;
    private Long internalBrandId;
    private Long internalCategoryId;
    private Long internalProductGroupId;
    private final Long sellerId;
    private int regularPrice;
    private int currentPrice;
    private String optionType;
    private InboundProductStatus status;
    private String descriptionHtml;
    private InboundProductPayload payload;
    private int retryCount;
    private final Instant createdAt;
    private Instant updatedAt;

    private InboundProduct(
            InboundProductId id,
            Long inboundSourceId,
            ExternalProductCode externalProductCode,
            String productName,
            String externalBrandCode,
            String externalCategoryCode,
            Long internalBrandId,
            Long internalCategoryId,
            Long internalProductGroupId,
            Long sellerId,
            int regularPrice,
            int currentPrice,
            String optionType,
            InboundProductStatus status,
            String descriptionHtml,
            InboundProductPayload payload,
            int retryCount,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.inboundSourceId = inboundSourceId;
        this.externalProductCode = externalProductCode;
        this.productName = productName;
        this.externalBrandCode = externalBrandCode;
        this.externalCategoryCode = externalCategoryCode;
        this.internalBrandId = internalBrandId;
        this.internalCategoryId = internalCategoryId;
        this.internalProductGroupId = internalProductGroupId;
        this.sellerId = sellerId;
        this.regularPrice = regularPrice;
        this.currentPrice = currentPrice;
        this.optionType = optionType;
        this.status = status;
        this.descriptionHtml = descriptionHtml;
        this.payload = payload;
        this.retryCount = retryCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 InboundProduct 생성 팩토리. */
    public static InboundProduct forNew(
            Long inboundSourceId,
            ExternalProductCode externalProductCode,
            String productName,
            String externalBrandCode,
            String externalCategoryCode,
            Long sellerId,
            int regularPrice,
            int currentPrice,
            String optionType,
            String descriptionHtml,
            InboundProductPayload payload,
            Instant now) {
        return new InboundProduct(
                InboundProductId.forNew(),
                inboundSourceId,
                externalProductCode,
                productName,
                externalBrandCode,
                externalCategoryCode,
                null,
                null,
                null,
                sellerId,
                regularPrice,
                currentPrice,
                optionType,
                InboundProductStatus.RECEIVED,
                descriptionHtml,
                payload,
                0,
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    public static InboundProduct reconstitute(
            InboundProductId id,
            Long inboundSourceId,
            ExternalProductCode externalProductCode,
            String productName,
            String externalBrandCode,
            String externalCategoryCode,
            Long internalBrandId,
            Long internalCategoryId,
            Long internalProductGroupId,
            Long sellerId,
            int regularPrice,
            int currentPrice,
            String optionType,
            InboundProductStatus status,
            String descriptionHtml,
            InboundProductPayload payload,
            int retryCount,
            Instant createdAt,
            Instant updatedAt) {
        return new InboundProduct(
                id,
                inboundSourceId,
                externalProductCode,
                productName,
                externalBrandCode,
                externalCategoryCode,
                internalBrandId,
                internalCategoryId,
                internalProductGroupId,
                sellerId,
                regularPrice,
                currentPrice,
                optionType,
                status,
                descriptionHtml,
                payload,
                retryCount,
                createdAt,
                updatedAt);
    }

    /** 브랜드/카테고리 매핑 적용. RECEIVED 또는 PENDING_MAPPING 상태에서만 가능. */
    public void applyMapping(Long internalBrandId, Long internalCategoryId, Instant now) {
        if (!status.canApplyMapping() && !status.isConverted()) {
            throw new IllegalStateException(
                    "매핑 적용은 RECEIVED, PENDING_MAPPING, CONVERTED 상태에서만 가능합니다. 현재 상태: " + status);
        }
        this.internalBrandId = internalBrandId;
        this.internalCategoryId = internalCategoryId;
        this.status = InboundProductStatus.MAPPED;
        this.updatedAt = now;
    }

    /** 매핑 실패 처리. */
    public void markMappingFailed(Instant now) {
        this.status = InboundProductStatus.PENDING_MAPPING;
        this.updatedAt = now;
    }

    /** 비동기 변환 대기 상태로 전이. MAPPED 또는 CONVERT_FAILED 상태에서만 가능. */
    public void markPendingConversion(Instant now) {
        if (!status.canMarkPendingConversion()) {
            throw new IllegalStateException(
                    "PENDING_CONVERSION 전이는 MAPPED 또는 CONVERT_FAILED 상태에서만 가능합니다. 현재 상태: "
                            + status);
        }
        boolean isRetry = status.isConvertFailed();
        this.status = InboundProductStatus.PENDING_CONVERSION;
        if (!isRetry) {
            this.retryCount = 0;
        }
        this.updatedAt = now;
    }

    /** 외부 상품 코드 할당. 레거시 등록 시 변환 완료 후 productGroupId로 교체할 때 사용. */
    public void assignExternalProductCode(ExternalProductCode code, Instant now) {
        this.externalProductCode = code;
        this.updatedAt = now;
    }

    /** ProductGroup 변환 완료 처리. */
    public void markConverted(Long internalProductGroupId, Instant now) {
        this.internalProductGroupId = internalProductGroupId;
        this.status = InboundProductStatus.CONVERTED;
        this.updatedAt = now;
    }

    /** 복구 불가능한 페이로드 오류로 영구 실패 처리. 재시도 대상에서 제외됩니다. */
    public void markPermanentlyFailed(Instant now) {
        this.status = InboundProductStatus.PERMANENTLY_FAILED;
        this.updatedAt = now;
    }

    /** ProductGroup 변환 실패 처리. 재시도 횟수를 증가시킵니다. */
    public void markConvertFailed(Instant now) {
        this.status = InboundProductStatus.CONVERT_FAILED;
        this.retryCount++;
        this.updatedAt = now;
    }

    /** 재시도 횟수가 최대 허용 횟수에 도달했는지 확인. */
    public boolean isRetryExhausted(int maxRetry) {
        return this.retryCount >= maxRetry;
    }

    /** 재수신 시 변경 감지. */
    public InboundProductDiff detectChanges(InboundProductUpdateData newData) {
        return new InboundProductDiff(
                InboundProductDiff.changed(this.productName, newData.productName()),
                InboundProductDiff.changed(this.regularPrice, newData.regularPrice())
                        || InboundProductDiff.changed(this.currentPrice, newData.currentPrice()),
                InboundProductDiff.changed(this.externalBrandCode, newData.externalBrandCode()),
                InboundProductDiff.changed(
                        this.externalCategoryCode, newData.externalCategoryCode()),
                InboundProductDiff.changed(this.payload, newData.payload()));
    }

    /** 재수신 데이터 반영. */
    public void applyUpdate(InboundProductUpdateData newData, Instant now) {
        this.productName = newData.productName();
        this.externalBrandCode = newData.externalBrandCode();
        this.externalCategoryCode = newData.externalCategoryCode();
        this.regularPrice = newData.regularPrice();
        this.currentPrice = newData.currentPrice();
        this.optionType = newData.optionType();
        this.descriptionHtml = newData.descriptionHtml();
        this.payload = newData.payload();
        this.updatedAt = now;
    }

    public boolean isMapped() {
        return status.isMapped();
    }

    public boolean isPendingConversion() {
        return status.isPendingConversion();
    }

    public boolean isConverted() {
        return status.isConverted();
    }

    public boolean isReadyForConversion() {
        return status.isReadyForConversion();
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

    public String productName() {
        return productName;
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

    public int regularPrice() {
        return regularPrice;
    }

    public int currentPrice() {
        return currentPrice;
    }

    public String optionType() {
        return optionType;
    }

    public InboundProductStatus status() {
        return status;
    }

    public String descriptionHtml() {
        return descriptionHtml;
    }

    public InboundProductPayload payload() {
        return payload;
    }

    public int retryCount() {
        return retryCount;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
