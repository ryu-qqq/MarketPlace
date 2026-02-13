package com.ryuqq.marketplace.application.productgroup.internal;

import com.ryuqq.marketplace.application.imageupload.internal.ImageUploadOutboxCreator;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupCommandManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupImageCommandManager;
import com.ryuqq.marketplace.application.productgroup.manager.SellerOptionGroupCommandManager;
import com.ryuqq.marketplace.application.productgroup.manager.SellerOptionValueCommandManager;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ProductGroup Persist Facade.
 *
 * <p>ProductGroup 엔티티 + 자식(Image, OptionGroup, OptionValue) 저장을 조율합니다.
 *
 * <p>delete-insert 전략: 수정 시 기존 자식 삭제 → 새 자식 저장.
 */
@Component
public class ProductGroupPersistFacade {

    private final ProductGroupCommandManager productGroupCommandManager;
    private final ProductGroupImageCommandManager imageCommandManager;
    private final SellerOptionGroupCommandManager optionGroupCommandManager;
    private final SellerOptionValueCommandManager optionValueCommandManager;
    private final ImageUploadOutboxCreator outboxCreator;

    public ProductGroupPersistFacade(
            ProductGroupCommandManager productGroupCommandManager,
            ProductGroupImageCommandManager imageCommandManager,
            SellerOptionGroupCommandManager optionGroupCommandManager,
            SellerOptionValueCommandManager optionValueCommandManager,
            ImageUploadOutboxCreator outboxCreator) {
        this.productGroupCommandManager = productGroupCommandManager;
        this.imageCommandManager = imageCommandManager;
        this.optionGroupCommandManager = optionGroupCommandManager;
        this.optionValueCommandManager = optionValueCommandManager;
        this.outboxCreator = outboxCreator;
    }

    /**
     * ProductGroup + 자식 엔티티 저장.
     *
     * <p>1. ProductGroup 저장 → productGroupId 획득
     *
     * <p>2. 수정 시: 기존 자식 삭제 (OptionValue → OptionGroup → Image)
     *
     * <p>3. Image 저장
     *
     * <p>4. OptionGroup → OptionValue 저장
     *
     * @param productGroup ProductGroup 도메인 객체
     * @return 저장된 productGroupId
     */
    @Transactional
    public Long persist(ProductGroup productGroup) {
        Long productGroupId = productGroupCommandManager.persist(productGroup);

        if (productGroup.idValue() != null) {
            deleteChildren(productGroupId);
        }

        List<Long> imageIds = imageCommandManager.persistAll(productGroupId, productGroup.images());
        saveOptionGroupsAndValues(productGroupId, productGroup);

        outboxCreator.createForProductGroupImages(imageIds, productGroup.images(), Instant.now());

        return productGroupId;
    }

    private void deleteChildren(Long productGroupId) {
        List<Long> groupIds =
                optionGroupCommandManager.findGroupIdsByProductGroupId(productGroupId);
        if (!groupIds.isEmpty()) {
            optionValueCommandManager.deleteByGroupIdIn(groupIds);
        }
        optionGroupCommandManager.deleteByProductGroupId(productGroupId);
        imageCommandManager.deleteByProductGroupId(productGroupId);
    }

    private void saveOptionGroupsAndValues(Long productGroupId, ProductGroup productGroup) {
        for (SellerOptionGroup group : productGroup.sellerOptionGroups()) {
            Long groupId = optionGroupCommandManager.persist(productGroupId, group);
            optionValueCommandManager.persistAll(groupId, group.optionValues());
        }
    }
}
