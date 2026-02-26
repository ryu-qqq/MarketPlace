package com.ryuqq.marketplace.domain.legacy.product.vo;

import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.legacy.optiondetail.id.LegacyOptionDetailId;
import com.ryuqq.marketplace.domain.legacy.optiongroup.id.LegacyOptionGroupId;
import com.ryuqq.marketplace.domain.legacy.product.id.LegacyProductId;
import java.time.Instant;

/**
 * 레거시(세토프) 상품-옵션 매핑 Value Object.
 *
 * <p>product_option 테이블에 대응하며, DeletionStatus로 soft-delete를 관리합니다.
 */
@SuppressWarnings("PMD.DomainTooManyMethods")
public class LegacyProductOption {

    private final Long id;
    private final LegacyProductId productId;
    private final LegacyOptionGroupId optionGroupId;
    private final LegacyOptionDetailId optionDetailId;
    private long additionalPrice;
    private DeletionStatus deletionStatus;

    private LegacyProductOption(
            Long id,
            LegacyProductId productId,
            LegacyOptionGroupId optionGroupId,
            LegacyOptionDetailId optionDetailId,
            long additionalPrice,
            DeletionStatus deletionStatus) {
        this.id = id;
        this.productId = productId;
        this.optionGroupId = optionGroupId;
        this.optionDetailId = optionDetailId;
        this.additionalPrice = additionalPrice;
        this.deletionStatus = deletionStatus;
    }

    /** 신규 옵션 매핑 생성. */
    public static LegacyProductOption forNew(
            LegacyProductId productId,
            LegacyOptionGroupId optionGroupId,
            LegacyOptionDetailId optionDetailId,
            long additionalPrice) {
        return new LegacyProductOption(
                null,
                productId,
                optionGroupId,
                optionDetailId,
                additionalPrice,
                DeletionStatus.active());
    }

    /** DB에서 복원. */
    public static LegacyProductOption reconstitute(
            Long id,
            LegacyProductId productId,
            LegacyOptionGroupId optionGroupId,
            LegacyOptionDetailId optionDetailId,
            long additionalPrice,
            DeletionStatus deletionStatus) {
        return new LegacyProductOption(
                id, productId, optionGroupId, optionDetailId, additionalPrice, deletionStatus);
    }

    /** 추가금액 갱신 (유지 대상 옵션의 가격 변경 시). */
    public void updateAdditionalPrice(long additionalPrice) {
        this.additionalPrice = additionalPrice;
    }

    /** 옵션 삭제 (soft delete). */
    public void delete(Instant occurredAt) {
        this.deletionStatus = DeletionStatus.deletedAt(occurredAt);
    }

    public Long id() {
        return id;
    }

    public LegacyProductId productId() {
        return productId;
    }

    public LegacyOptionGroupId optionGroupId() {
        return optionGroupId;
    }

    public LegacyOptionDetailId optionDetailId() {
        return optionDetailId;
    }

    public long additionalPrice() {
        return additionalPrice;
    }

    public DeletionStatus deletionStatus() {
        return deletionStatus;
    }

    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }
}
