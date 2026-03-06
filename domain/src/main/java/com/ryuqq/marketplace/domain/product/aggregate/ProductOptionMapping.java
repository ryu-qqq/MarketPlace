package com.ryuqq.marketplace.domain.product.aggregate;

import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.id.ProductOptionMappingId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.time.Instant;

/** 상품-옵션값 매핑 (Child Entity of Product). Product가 어떤 SellerOptionValue 조합인지를 나타낸다. */
public class ProductOptionMapping {

    private final ProductOptionMappingId id;
    private final ProductId productId;
    private final SellerOptionValueId sellerOptionValueId;
    private DeletionStatus deletionStatus;

    private ProductOptionMapping(
            ProductOptionMappingId id,
            ProductId productId,
            SellerOptionValueId sellerOptionValueId,
            DeletionStatus deletionStatus) {
        this.id = id;
        this.productId = productId;
        this.sellerOptionValueId = sellerOptionValueId;
        this.deletionStatus = deletionStatus;
    }

    /** 신규 매핑 생성. */
    public static ProductOptionMapping forNew(
            ProductId productId, SellerOptionValueId sellerOptionValueId) {
        return new ProductOptionMapping(
                ProductOptionMappingId.forNew(),
                productId,
                sellerOptionValueId,
                DeletionStatus.active());
    }

    /** 영속성에서 복원 시 사용. */
    public static ProductOptionMapping reconstitute(
            ProductOptionMappingId id,
            ProductId productId,
            SellerOptionValueId sellerOptionValueId,
            DeletionStatus deletionStatus) {
        return new ProductOptionMapping(id, productId, sellerOptionValueId, deletionStatus);
    }

    /** 소프트 삭제. */
    public void delete(Instant occurredAt) {
        this.deletionStatus = DeletionStatus.deletedAt(occurredAt);
    }

    public boolean isDeleted() {
        return deletionStatus.isDeleted();
    }

    public DeletionStatus deletionStatus() {
        return deletionStatus;
    }

    public ProductOptionMappingId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public ProductId productId() {
        return productId;
    }

    public Long productIdValue() {
        return productId.value();
    }

    public SellerOptionValueId sellerOptionValueId() {
        return sellerOptionValueId;
    }

    public Long sellerOptionValueIdValue() {
        return sellerOptionValueId.value();
    }
}
