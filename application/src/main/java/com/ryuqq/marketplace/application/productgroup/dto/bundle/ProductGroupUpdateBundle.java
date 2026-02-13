package com.ryuqq.marketplace.application.productgroup.dto.bundle;

import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupImages;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroups;
import com.ryuqq.marketplace.domain.productnotice.vo.ProductNoticeEntries;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.time.Instant;
import java.util.List;

/**
 * 상품 그룹 수정 번들.
 *
 * <p>ProductGroup 수정 + Description 수정/생성 + Notice 수정/생성 + Products 교체(soft delete + 새로 생성)를 한번에 묶어서
 * 관리합니다.
 */
public class ProductGroupUpdateBundle {

    private final ProductGroupId productGroupId;
    private final ProductGroupName productGroupName;
    private final BrandId brandId;
    private final CategoryId categoryId;
    private final ShippingPolicyId shippingPolicyId;
    private final RefundPolicyId refundPolicyId;
    private final ProductGroupImages images;
    private final SellerOptionGroups optionGroups;
    private final DescriptionHtml descriptionContent;
    private final List<DescriptionImage> descriptionImages;
    private final ProductNoticeEntries noticeEntries;
    private final ProductCreations productCreations;
    private final Instant changedAt;

    public ProductGroupUpdateBundle(
            ProductGroupId productGroupId,
            ProductGroupName productGroupName,
            BrandId brandId,
            CategoryId categoryId,
            ShippingPolicyId shippingPolicyId,
            RefundPolicyId refundPolicyId,
            ProductGroupImages images,
            SellerOptionGroups optionGroups,
            DescriptionHtml descriptionContent,
            List<DescriptionImage> descriptionImages,
            ProductNoticeEntries noticeEntries,
            ProductCreations productCreations,
            Instant changedAt) {
        this.productGroupId = productGroupId;
        this.productGroupName = productGroupName;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.shippingPolicyId = shippingPolicyId;
        this.refundPolicyId = refundPolicyId;
        this.images = images;
        this.optionGroups = optionGroups;
        this.descriptionContent = descriptionContent;
        this.descriptionImages = descriptionImages;
        this.noticeEntries = noticeEntries;
        this.productCreations = productCreations;
        this.changedAt = changedAt;
    }

    /** 새로운 Product 리스트 생성. */
    public List<Product> newProducts() {
        return productCreations.toProducts(productGroupId, changedAt);
    }

    // === Getter ===

    public ProductGroupId productGroupId() {
        return productGroupId;
    }

    public ProductGroupName productGroupName() {
        return productGroupName;
    }

    public BrandId brandId() {
        return brandId;
    }

    public CategoryId categoryId() {
        return categoryId;
    }

    public ShippingPolicyId shippingPolicyId() {
        return shippingPolicyId;
    }

    public RefundPolicyId refundPolicyId() {
        return refundPolicyId;
    }

    public ProductGroupImages images() {
        return images;
    }

    public SellerOptionGroups optionGroups() {
        return optionGroups;
    }

    public DescriptionHtml descriptionContent() {
        return descriptionContent;
    }

    public List<DescriptionImage> descriptionImages() {
        return descriptionImages;
    }

    public ProductNoticeEntries noticeEntries() {
        return noticeEntries;
    }

    public Instant changedAt() {
        return changedAt;
    }
}
