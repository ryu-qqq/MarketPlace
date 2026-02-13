package com.ryuqq.marketplace.application.productgroup.internal;

import com.ryuqq.marketplace.application.product.internal.ProductCommandFacade;
import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionCommandFacade;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.application.productnotice.internal.NoticeCommandFacade;
import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeReadManager;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.vo.ProductNoticeEntries;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ProductGroup Command Facade.
 *
 * <p>ProductGroup + Description + Notice + Products를 하나의 트랜잭션으로 처리합니다.
 *
 * <p>ProductGroup (1) : Description (0..1) : Notice (0..1) : Products (0..N)
 */
@Component
public class ProductGroupCommandFacade {

    private final ProductGroupPersistFacade productGroupPersistFacade;
    private final ProductGroupReadManager productGroupReadManager;
    private final DescriptionCommandFacade descriptionCommandFacade;
    private final ProductGroupDescriptionReadManager descriptionReadManager;
    private final NoticeCommandFacade noticeCommandFacade;
    private final ProductNoticeReadManager noticeReadManager;
    private final ProductCommandFacade productCommandFacade;
    private final ProductReadManager productReadManager;

    public ProductGroupCommandFacade(
            ProductGroupPersistFacade productGroupPersistFacade,
            ProductGroupReadManager productGroupReadManager,
            DescriptionCommandFacade descriptionCommandFacade,
            ProductGroupDescriptionReadManager descriptionReadManager,
            NoticeCommandFacade noticeCommandFacade,
            ProductNoticeReadManager noticeReadManager,
            ProductCommandFacade productCommandFacade,
            ProductReadManager productReadManager) {
        this.productGroupPersistFacade = productGroupPersistFacade;
        this.productGroupReadManager = productGroupReadManager;
        this.descriptionCommandFacade = descriptionCommandFacade;
        this.descriptionReadManager = descriptionReadManager;
        this.noticeCommandFacade = noticeCommandFacade;
        this.noticeReadManager = noticeReadManager;
        this.productCommandFacade = productCommandFacade;
        this.productReadManager = productReadManager;
    }

    /**
     * 상품 그룹 등록 번들을 저장합니다.
     *
     * <p>ProductGroup → Description → Notice → Products 순서로 저장하며, 모든 저장이 하나의 트랜잭션으로 처리됩니다.
     *
     * @param bundle 상품 그룹 등록 번들
     * @return 생성된 상품 그룹 ID
     */
    @Transactional
    public Long registerProductGroup(ProductGroupRegistrationBundle bundle) {
        // 1. ProductGroup 저장
        Long productGroupId = productGroupPersistFacade.persist(bundle.productGroup());

        // 2. ProductGroupId로 하위 Aggregate 생성
        bundle.withProductGroupId(ProductGroupId.of(productGroupId));

        // 3. Description + Image 저장
        descriptionCommandFacade.persist(bundle.description());

        // 4. Notice + Entry 저장
        noticeCommandFacade.persist(bundle.notice());

        // 5. Products + OptionMapping 저장
        productCommandFacade.persistAll(bundle.products());

        return productGroupId;
    }

    /**
     * 상품 그룹 수정 번들을 처리합니다.
     *
     * <p>ProductGroup 수정 → Description 수정/생성 → Notice 수정/생성 → Products 교체(soft delete + 새로 생성) 순서로
     * 처리하며, 모든 수정이 하나의 트랜잭션으로 처리됩니다.
     *
     * @param bundle 상품 그룹 수정 번들
     */
    @Transactional
    public void updateProductGroup(ProductGroupUpdateBundle bundle) {
        // 1. ProductGroup 로드 및 수정
        ProductGroup productGroup = productGroupReadManager.getById(bundle.productGroupId());
        productGroup.updateBasicInfo(
                bundle.productGroupName(),
                bundle.brandId(),
                bundle.categoryId(),
                bundle.shippingPolicyId(),
                bundle.refundPolicyId(),
                bundle.changedAt());
        productGroup.replaceImages(bundle.images());
        productGroup.replaceSellerOptionGroups(bundle.optionGroups());
        productGroupPersistFacade.persist(productGroup);

        // 2. Description + Image 수정 또는 생성
        Optional<ProductGroupDescription> existingDesc =
                descriptionReadManager.findByProductGroupId(bundle.productGroupId());
        if (existingDesc.isPresent()) {
            ProductGroupDescription description = existingDesc.get();
            description.updateContent(bundle.descriptionContent());
            description.replaceImages(bundle.descriptionImages());
            descriptionCommandFacade.persist(description);
        } else {
            ProductGroupDescription newDescription =
                    ProductGroupDescription.forNew(
                            bundle.productGroupId(), bundle.descriptionContent());
            newDescription.replaceImages(bundle.descriptionImages());
            descriptionCommandFacade.persist(newDescription);
        }

        // 3. Notice + Entry 수정 또는 생성
        ProductNoticeEntries noticeEntries = bundle.noticeEntries();
        Optional<ProductNotice> existingNotice =
                noticeReadManager.findByProductGroupId(bundle.productGroupId());
        if (existingNotice.isPresent()) {
            ProductNotice notice = existingNotice.get();
            notice.replaceEntries(noticeEntries.toList(), bundle.changedAt());
            noticeCommandFacade.persist(notice);
        } else {
            ProductNotice newNotice =
                    ProductNotice.forNew(
                            bundle.productGroupId(),
                            noticeEntries.noticeCategoryId(),
                            noticeEntries.toList(),
                            bundle.changedAt());
            noticeCommandFacade.persist(newNotice);
        }

        // 4. 기존 Products soft delete
        List<Product> existingProducts =
                productReadManager.findByProductGroupId(bundle.productGroupId());
        for (Product product : existingProducts) {
            product.delete(bundle.changedAt());
            productCommandFacade.persist(product);
        }

        // 5. 새로운 Products + OptionMapping 생성
        productCommandFacade.persistAll(bundle.newProducts());
    }
}
