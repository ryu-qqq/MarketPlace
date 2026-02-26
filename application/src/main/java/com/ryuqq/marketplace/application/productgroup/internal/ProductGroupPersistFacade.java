package com.ryuqq.marketplace.application.productgroup.internal;

import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupPersistResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupPersistResult.OptionGroupPersistEntry;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupCommandManager;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageCommandManager;
import com.ryuqq.marketplace.application.selleroption.manager.SellerOptionGroupCommandManager;
import com.ryuqq.marketplace.application.selleroption.manager.SellerOptionValueCommandManager;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ProductGroup Persist Facade (수정 플로우 전용).
 *
 * <p>ProductGroup 엔티티 + 자식(Image, OptionGroup, OptionValue) 저장을 조율합니다.
 *
 * <p>등록 플로우는 {@link FullProductGroupRegistrationCoordinator}에서 Manager를 직접 사용합니다.
 */
@Component
public class ProductGroupPersistFacade {

    private final ProductGroupCommandManager productGroupCommandManager;
    private final ProductGroupImageCommandManager imageCommandManager;
    private final SellerOptionGroupCommandManager optionGroupCommandManager;
    private final SellerOptionValueCommandManager optionValueCommandManager;

    public ProductGroupPersistFacade(
            ProductGroupCommandManager productGroupCommandManager,
            ProductGroupImageCommandManager imageCommandManager,
            SellerOptionGroupCommandManager optionGroupCommandManager,
            SellerOptionValueCommandManager optionValueCommandManager) {
        this.productGroupCommandManager = productGroupCommandManager;
        this.imageCommandManager = imageCommandManager;
        this.optionGroupCommandManager = optionGroupCommandManager;
        this.optionValueCommandManager = optionValueCommandManager;
    }

    /**
     * ProductGroup + 자식 엔티티 저장 (수정 플로우용).
     *
     * @param productGroup ProductGroup 도메인 객체
     * @return ProductGroupPersistResult (productGroupId + imageIds + optionGroupEntries)
     */
    @Transactional
    public ProductGroupPersistResult persist(ProductGroup productGroup) {
        Long productGroupId = productGroupCommandManager.persist(productGroup);
        List<Long> imageIds = imageCommandManager.persistAll(productGroup.images());
        List<OptionGroupPersistEntry> optionGroupEntries = new ArrayList<>();
        for (SellerOptionGroup group : productGroup.sellerOptionGroups()) {
            Long groupId = optionGroupCommandManager.persist(group);
            List<Long> valueIds = optionValueCommandManager.persistAll(group.optionValues());
            optionGroupEntries.add(new OptionGroupPersistEntry(groupId, valueIds));
        }
        return new ProductGroupPersistResult(productGroupId, imageIds, optionGroupEntries);
    }
}
