package com.ryuqq.marketplace.application.productgroup.dto.bundle;

import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.vo.ProductNoticeEntries;
import java.time.Instant;
import java.util.List;

/**
 * 상품 그룹 등록 번들.
 *
 * <p>ProductGroup + Description(+Images) + Notice + Products를 한번에 묶어서 관리합니다.
 *
 * <p>ProductGroup을 먼저 저장하고, 생성된 ID로 나머지 Aggregate들을 생성합니다.
 */
public class ProductGroupRegistrationBundle {

    private final ProductGroup productGroup;
    private final DescriptionHtml descriptionContent;
    private final List<DescriptionImage> descriptionImages;
    private final ProductNoticeEntries noticeEntries;
    private final ProductCreations productCreations;
    private final Instant createdAt;

    private ProductGroupDescription description;
    private ProductNotice notice;
    private List<Product> products;

    public ProductGroupRegistrationBundle(
            ProductGroup productGroup,
            DescriptionHtml descriptionContent,
            List<DescriptionImage> descriptionImages,
            ProductNoticeEntries noticeEntries,
            ProductCreations productCreations,
            Instant createdAt) {
        this.productGroup = productGroup;
        this.descriptionContent = descriptionContent;
        this.descriptionImages = descriptionImages;
        this.noticeEntries = noticeEntries;
        this.productCreations = productCreations;
        this.createdAt = createdAt;
    }

    /**
     * ProductGroupId를 설정하고 하위 Aggregate들을 생성합니다.
     *
     * @param productGroupId persist 후 확정된 ProductGroup ID
     */
    public void withProductGroupId(ProductGroupId productGroupId) {
        this.description = ProductGroupDescription.forNew(productGroupId, descriptionContent);
        this.description.replaceImages(descriptionImages);

        this.notice =
                ProductNotice.forNew(
                        productGroupId,
                        noticeEntries.noticeCategoryId(),
                        noticeEntries.toList(),
                        createdAt);

        this.products = productCreations.toProducts(productGroupId, createdAt);
    }

    // === Getter ===

    public ProductGroup productGroup() {
        return productGroup;
    }

    public ProductNoticeEntries noticeEntries() {
        return noticeEntries;
    }

    public ProductGroupDescription description() {
        return description;
    }

    public ProductNotice notice() {
        return notice;
    }

    public List<Product> products() {
        return products;
    }
}
