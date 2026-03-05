package com.ryuqq.marketplace.domain.product.aggregate;

import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.exception.ProductInvalidPriceException;
import com.ryuqq.marketplace.domain.product.exception.ProductInvalidStatusTransitionException;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.vo.ProductStatus;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 상품(SKU) Aggregate Root. 실제 판매/재고 관리 대상. ProductGroup의 옵션 조합별로 생성된다. */
public class Product {

    private final ProductId id;
    private final ProductGroupId productGroupId;
    private SkuCode skuCode;
    private Money regularPrice;
    private Money currentPrice;
    private Money salePrice;
    private int discountRate;
    private int stockQuantity;
    private ProductStatus status;
    private int sortOrder;
    private final List<ProductOptionMapping> optionMappings;
    private final Instant createdAt;
    private Instant updatedAt;

    private Product(
            ProductId id,
            ProductGroupId productGroupId,
            SkuCode skuCode,
            Money regularPrice,
            Money currentPrice,
            Money salePrice,
            int discountRate,
            int stockQuantity,
            ProductStatus status,
            int sortOrder,
            List<ProductOptionMapping> optionMappings,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.skuCode = skuCode;
        this.regularPrice = regularPrice;
        this.currentPrice = currentPrice;
        this.salePrice = salePrice;
        this.discountRate = discountRate;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.sortOrder = sortOrder;
        this.optionMappings = new ArrayList<>(optionMappings);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 신규 상품 생성. ACTIVE 상태로 시작.
     *
     * <p>salePrice는 currentPrice와 동일하게, discountRate는 regularPrice 대비 currentPrice로 자동 계산합니다.
     */
    public static Product forNew(
            ProductGroupId productGroupId,
            SkuCode skuCode,
            Money regularPrice,
            Money currentPrice,
            int stockQuantity,
            int sortOrder,
            List<ProductOptionMapping> optionMappings,
            Instant now) {
        validateCurrentPrice(regularPrice, currentPrice);
        validateStockQuantity(stockQuantity);
        return new Product(
                ProductId.forNew(),
                productGroupId,
                skuCode,
                regularPrice,
                currentPrice,
                currentPrice,
                Money.discountRate(regularPrice, currentPrice),
                stockQuantity,
                ProductStatus.ACTIVE,
                sortOrder,
                optionMappings,
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    public static Product reconstitute(
            ProductId id,
            ProductGroupId productGroupId,
            SkuCode skuCode,
            Money regularPrice,
            Money currentPrice,
            Money salePrice,
            int discountRate,
            int stockQuantity,
            ProductStatus status,
            int sortOrder,
            List<ProductOptionMapping> optionMappings,
            Instant createdAt,
            Instant updatedAt) {
        return new Product(
                id,
                productGroupId,
                skuCode,
                regularPrice,
                currentPrice,
                salePrice,
                discountRate,
                stockQuantity,
                status,
                sortOrder,
                optionMappings,
                createdAt,
                updatedAt);
    }

    // ── 비즈니스 메서드 ──

    /** targetStatus에 따라 적절한 상태 전이 메서드를 호출한다. */
    public void changeStatus(ProductStatus targetStatus, Instant now) {
        switch (targetStatus) {
            case ACTIVE -> activate(now);
            case INACTIVE -> deactivate(now);
            case SOLD_OUT -> markSoldOut(now);
            case DELETED -> delete(now);
            default -> throw new IllegalArgumentException("지원하지 않는 상태 변경입니다: " + targetStatus);
        }
    }

    /** 판매 재개. INACTIVE, SOLD_OUT에서만 가능. */
    public void activate(Instant now) {
        if (!status.canActivate()) {
            throw new ProductInvalidStatusTransitionException(status, ProductStatus.ACTIVE);
        }
        this.status = ProductStatus.ACTIVE;
        this.updatedAt = now;
    }

    /** 판매 중지. */
    public void deactivate(Instant now) {
        if (!status.isActive()) {
            throw new ProductInvalidStatusTransitionException(status, ProductStatus.INACTIVE);
        }
        this.status = ProductStatus.INACTIVE;
        this.updatedAt = now;
    }

    /** 품절 처리. */
    public void markSoldOut(Instant now) {
        if (!status.isActive()) {
            throw new ProductInvalidStatusTransitionException(status, ProductStatus.SOLD_OUT);
        }
        this.status = ProductStatus.SOLD_OUT;
        this.updatedAt = now;
    }

    /** 소프트 삭제. 옵션 매핑도 함께 삭제한다. */
    public void delete(Instant now) {
        if (!status.canDelete()) {
            throw new ProductInvalidStatusTransitionException(status, ProductStatus.DELETED);
        }
        this.status = ProductStatus.DELETED;
        this.updatedAt = now;
        optionMappings.forEach(m -> m.delete(now));
    }

    /**
     * 가격 수정.
     *
     * <p>salePrice는 currentPrice와 동일하게, discountRate는 자동 계산합니다.
     */
    public void updatePrice(Money regularPrice, Money currentPrice, Instant now) {
        validateCurrentPrice(regularPrice, currentPrice);
        this.regularPrice = regularPrice;
        this.currentPrice = currentPrice;
        this.salePrice = currentPrice;
        this.discountRate = Money.discountRate(regularPrice, currentPrice);
        this.updatedAt = now;
    }

    /** 재고 수정. */
    public void updateStock(int stockQuantity, Instant now) {
        validateStockQuantity(stockQuantity);
        this.stockQuantity = stockQuantity;
        this.updatedAt = now;
    }

    /** SKU 코드 수정. */
    public void updateSkuCode(SkuCode skuCode, Instant now) {
        this.skuCode = skuCode;
        this.updatedAt = now;
    }

    /** 정렬 순서 수정. */
    public void updateSortOrder(int sortOrder, Instant now) {
        this.sortOrder = sortOrder;
        this.updatedAt = now;
    }

    /** 전체 속성 일괄 수정 (가격 + 재고 + SKU + 정렬 순서). */
    public void update(
            SkuCode skuCode,
            Money regularPrice,
            Money currentPrice,
            int stockQuantity,
            int sortOrder,
            Instant now) {
        updatePrice(regularPrice, currentPrice, now);
        updateStock(stockQuantity, now);
        updateSkuCode(skuCode, now);
        updateSortOrder(sortOrder, now);
    }

    // ── 검증 메서드 ──

    private static void validateCurrentPrice(Money regularPrice, Money currentPrice) {
        if (currentPrice.isGreaterThan(regularPrice)) {
            throw new ProductInvalidPriceException(regularPrice.value(), currentPrice.value());
        }
    }

    private static void validateStockQuantity(int stockQuantity) {
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("재고 수량은 0 이상이어야 합니다: " + stockQuantity);
        }
    }

    // ── 조회 메서드 ──

    /** 재고 존재 여부. */
    public boolean hasStock() {
        return stockQuantity > 0;
    }

    /** 세일 적용 여부. */
    public boolean isOnSale() {
        return salePrice != null && !salePrice.isZero() && discountRate > 0;
    }

    /** 실제 판매 가격 (세일 중이면 salePrice, 아니면 currentPrice). */
    public Money effectivePrice() {
        return isOnSale() ? salePrice : currentPrice;
    }

    // ── Accessor 메서드 ──

    public ProductId id() {
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

    public SkuCode skuCode() {
        return skuCode;
    }

    public String skuCodeValue() {
        return skuCode != null ? skuCode.value() : null;
    }

    public Money regularPrice() {
        return regularPrice;
    }

    public int regularPriceValue() {
        return regularPrice.value();
    }

    public Money currentPrice() {
        return currentPrice;
    }

    public int currentPriceValue() {
        return currentPrice.value();
    }

    public Money salePrice() {
        return salePrice;
    }

    public Integer salePriceValue() {
        return salePrice != null ? salePrice.value() : null;
    }

    public int discountRate() {
        return discountRate;
    }

    public int stockQuantity() {
        return stockQuantity;
    }

    public ProductStatus status() {
        return status;
    }

    public int sortOrder() {
        return sortOrder;
    }

    public List<ProductOptionMapping> optionMappings() {
        return Collections.unmodifiableList(optionMappings);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
