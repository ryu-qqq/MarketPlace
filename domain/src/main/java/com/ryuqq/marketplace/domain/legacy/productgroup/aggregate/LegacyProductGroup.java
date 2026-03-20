package com.ryuqq.marketplace.domain.legacy.productgroup.aggregate;

import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductGroupUpdateData;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ManagementType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.Origin;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ProductCondition;
import java.time.Instant;

/**
 * 레거시(세토프) 상품 그룹 Aggregate Root.
 *
 * <p>세토프 DB의 product_group 테이블에 대응합니다. 고시정보, 배송정보, 상세설명은 별도 Aggregate로 분리되었습니다.
 */
public class LegacyProductGroup {

    private final LegacyProductGroupId id;
    private String productGroupName;
    private long sellerId;
    private long brandId;
    private long categoryId;
    private OptionType optionType;
    private ManagementType managementType;
    private long regularPrice;
    private long currentPrice;
    private String soldOutYn;
    private String displayYn;
    private ProductCondition productCondition;
    private Origin origin;
    private String styleCode;
    private final Instant createdAt;
    private Instant updatedAt;

    private LegacyProductGroup(
            LegacyProductGroupId id,
            String productGroupName,
            long sellerId,
            long brandId,
            long categoryId,
            OptionType optionType,
            ManagementType managementType,
            long regularPrice,
            long currentPrice,
            String soldOutYn,
            String displayYn,
            ProductCondition productCondition,
            Origin origin,
            String styleCode,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.productGroupName = productGroupName;
        this.sellerId = sellerId;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.optionType = optionType;
        this.managementType = managementType;
        this.regularPrice = regularPrice;
        this.currentPrice = currentPrice;
        this.soldOutYn = soldOutYn;
        this.displayYn = displayYn;
        this.productCondition = productCondition;
        this.origin = origin;
        this.styleCode = styleCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 레거시 상품 그룹 생성. */
    public static LegacyProductGroup forNew(
            String productGroupName,
            long sellerId,
            long brandId,
            long categoryId,
            OptionType optionType,
            ManagementType managementType,
            long regularPrice,
            long currentPrice,
            String soldOutYn,
            String displayYn,
            ProductCondition productCondition,
            Origin origin,
            String styleCode) {
        Instant now = Instant.now();
        return new LegacyProductGroup(
                LegacyProductGroupId.forNew(),
                productGroupName,
                sellerId,
                brandId,
                categoryId,
                optionType,
                managementType,
                regularPrice,
                currentPrice,
                soldOutYn,
                displayYn,
                productCondition,
                origin,
                styleCode,
                now,
                now);
    }

    /** DB에서 복원. */
    public static LegacyProductGroup reconstitute(
            Long id,
            String productGroupName,
            long sellerId,
            long brandId,
            long categoryId,
            OptionType optionType,
            ManagementType managementType,
            long regularPrice,
            long currentPrice,
            String soldOutYn,
            String displayYn,
            ProductCondition productCondition,
            Origin origin,
            String styleCode,
            Instant createdAt,
            Instant updatedAt) {
        return new LegacyProductGroup(
                LegacyProductGroupId.of(id),
                productGroupName,
                sellerId,
                brandId,
                categoryId,
                optionType,
                managementType,
                regularPrice,
                currentPrice,
                soldOutYn,
                displayYn,
                productCondition,
                origin,
                styleCode,
                createdAt,
                updatedAt);
    }

    /** 가격 수정. */
    public void updatePrice(long regularPrice, long currentPrice, Instant changedAt) {
        this.regularPrice = regularPrice;
        this.currentPrice = currentPrice;
        this.updatedAt = changedAt;
    }

    /** 전시 상태 변경. */
    public void updateDisplayYn(String displayYn, Instant changedAt) {
        this.displayYn = displayYn;
        this.updatedAt = changedAt;
    }

    /** 품절 처리. */
    public void markSoldOut(Instant changedAt) {
        this.soldOutYn = "Y";
        this.displayYn = "N";
        this.updatedAt = changedAt;
    }

    /** 상품그룹 기본정보 수정 (UpdateData 패턴). */
    public void updateProductGroupDetails(
            LegacyProductGroupUpdateData updateData, Instant changedAt) {
        this.productGroupName = updateData.productGroupName();
        this.sellerId = updateData.sellerId();
        this.brandId = updateData.brandId();
        this.categoryId = updateData.categoryId();
        this.optionType = updateData.optionType();
        this.managementType = updateData.managementType();
        this.regularPrice = updateData.regularPrice();
        this.currentPrice = updateData.currentPrice();
        this.soldOutYn = updateData.soldOutYn();
        this.displayYn = updateData.displayYn();
        this.productCondition = updateData.productCondition();
        this.origin = updateData.origin();
        this.styleCode = updateData.styleCode();
        this.updatedAt = changedAt;
    }

    public LegacyProductGroupId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public String productGroupName() {
        return productGroupName;
    }

    public long sellerId() {
        return sellerId;
    }

    public long brandId() {
        return brandId;
    }

    public long categoryId() {
        return categoryId;
    }

    public OptionType optionType() {
        return optionType;
    }

    public ManagementType managementType() {
        return managementType;
    }

    public long regularPrice() {
        return regularPrice;
    }

    public long currentPrice() {
        return currentPrice;
    }

    public String soldOutYn() {
        return soldOutYn;
    }

    public String displayYn() {
        return displayYn;
    }

    public ProductCondition productCondition() {
        return productCondition;
    }

    public Origin origin() {
        return origin;
    }

    public String styleCode() {
        return styleCode;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
