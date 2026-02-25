package com.ryuqq.marketplace.application.productgroup.internal;

import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.product.internal.ProductCommandCoordinator;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle.BoundDomainObjects;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle.OptionRegistrationData;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.productgroupimage.internal.ImageCommandCoordinator;
import com.ryuqq.marketplace.application.productintelligence.manager.IntelligenceOutboxCommandManager;
import com.ryuqq.marketplace.application.productnotice.internal.ProductNoticeCommandCoordinator;
import com.ryuqq.marketplace.application.selleroption.internal.SellerOptionCommandCoordinator;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.vo.ProductCreationData;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.IntelligenceOutbox;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 그룹 전체 Aggregate 등록 Coordinator.
 *
 * <p>ProductGroup 기본 정보 -> per-package Coordinator 위임 순서로 전체 등록을 조율합니다.
 *
 * <p>ProductGroup 기본 정보는 {@link ProductGroupCommandCoordinator}에 위임합니다.
 *
 * <p>수정 플로우는 {@link FullProductGroupUpdateCoordinator}를 사용합니다.
 */
@Component
public class FullProductGroupRegistrationCoordinator {

    private final ProductGroupCommandCoordinator productGroupCommandCoordinator;
    private final ImageCommandCoordinator imageCommandCoordinator;
    private final SellerOptionCommandCoordinator sellerOptionCommandCoordinator;
    private final DescriptionCommandCoordinator descriptionCommandCoordinator;
    private final ProductNoticeCommandCoordinator noticeCommandCoordinator;
    private final ProductCommandCoordinator productCommandCoordinator;
    private final IntelligenceOutboxCommandManager intelligenceOutboxCommandManager;

    public FullProductGroupRegistrationCoordinator(
            ProductGroupCommandCoordinator productGroupCommandCoordinator,
            ImageCommandCoordinator imageCommandCoordinator,
            SellerOptionCommandCoordinator sellerOptionCommandCoordinator,
            DescriptionCommandCoordinator descriptionCommandCoordinator,
            ProductNoticeCommandCoordinator noticeCommandCoordinator,
            ProductCommandCoordinator productCommandCoordinator,
            IntelligenceOutboxCommandManager intelligenceOutboxCommandManager) {
        this.productGroupCommandCoordinator = productGroupCommandCoordinator;
        this.imageCommandCoordinator = imageCommandCoordinator;
        this.sellerOptionCommandCoordinator = sellerOptionCommandCoordinator;
        this.descriptionCommandCoordinator = descriptionCommandCoordinator;
        this.noticeCommandCoordinator = noticeCommandCoordinator;
        this.productCommandCoordinator = productCommandCoordinator;
        this.intelligenceOutboxCommandManager = intelligenceOutboxCommandManager;
    }

    /**
     * 상품 그룹 전체 등록을 조율합니다.
     *
     * <p>번들에 포함된 등록 데이터를 사용하여 도메인 객체를 생성하고 각 per-package Coordinator에 위임합니다.
     *
     * @param bundle 등록 번들 (ProductGroup + per-package 등록 데이터)
     * @return 생성된 상품 그룹 ID
     */
    @Transactional
    public Long register(ProductGroupRegistrationBundle bundle) {
        // 1. ProductGroup 기본 정보 (검증 + persist) -> Coordinator
        Long productGroupId = productGroupCommandCoordinator.register(bundle.productGroup());
        ProductGroupId pgId = ProductGroupId.of(productGroupId);

        // 2. per-package 도메인 객체 생성 (productGroupId 바인딩)
        BoundDomainObjects bound = bundle.bindAll(pgId);

        // 3. Images -> Coordinator (persist + outbox)
        imageCommandCoordinator.register(bound.images());

        // 4. OptionGroups -> Coordinator (Validator + persist)
        List<SellerOptionValueId> allOptionValueIds =
                sellerOptionCommandCoordinator.register(bound.optionGroups(), bound.optionType());

        // 5. Description -> Coordinator (persist + outbox)
        descriptionCommandCoordinator.persist(bound.description());

        // 6. Notice -> Coordinator (Validator + persist)
        noticeCommandCoordinator.register(bound.notice());

        // 7. Products -> 이름 기반 resolve 후 등록
        Map<String, Map<String, SellerOptionValueId>> nameMap =
                buildRegistrationOptionNameMap(bundle.optionData().groups(), allOptionValueIds);

        Instant now = bundle.createdAt();
        List<Product> products =
                bundle.products().stream()
                        .map(
                                entry -> {
                                    List<SellerOptionValueId> resolvedIds =
                                            resolveOptionIds(entry.selectedOptions(), nameMap);
                                    return new ProductCreationData(
                                                    SkuCode.of(entry.skuCode()),
                                                    Money.of(entry.regularPrice()),
                                                    Money.of(entry.currentPrice()),
                                                    entry.stockQuantity(),
                                                    entry.sortOrder(),
                                                    resolvedIds)
                                            .toProduct(pgId, now);
                                })
                        .toList();
        productCommandCoordinator.register(products);

        // 8. Intelligence Outbox 저장 (PENDING) -- 스케줄러가 비동기로 분석 파이프라인 실행
        IntelligenceOutbox intelligenceOutbox =
                IntelligenceOutbox.forNew(productGroupId, bundle.createdAt());
        intelligenceOutboxCommandManager.persist(intelligenceOutbox);

        return productGroupId;
    }

    /**
     * 등록용 옵션 이름 맵 생성.
     *
     * <p>OptionGroupEntry의 그룹/값 이름 순서와 resolve된 ID 순서가 일치하는 전제하에 매핑합니다.
     */
    private Map<String, Map<String, SellerOptionValueId>> buildRegistrationOptionNameMap(
            List<OptionRegistrationData.OptionGroupEntry> optionGroups,
            List<SellerOptionValueId> allOptionValueIds) {
        Map<String, Map<String, SellerOptionValueId>> nameMap = new LinkedHashMap<>();
        int index = 0;
        for (OptionRegistrationData.OptionGroupEntry group : optionGroups) {
            Map<String, SellerOptionValueId> valueMap = new LinkedHashMap<>();
            for (OptionRegistrationData.OptionGroupEntry.OptionValueEntry value :
                    group.optionValues()) {
                valueMap.put(value.optionValueName(), allOptionValueIds.get(index++));
            }
            nameMap.put(group.optionGroupName(), valueMap);
        }
        return nameMap;
    }

    /** selectedOptions + 옵션 이름 맵 -> List&lt;SellerOptionValueId&gt; 변환. */
    private List<SellerOptionValueId> resolveOptionIds(
            List<SelectedOption> selectedOptions,
            Map<String, Map<String, SellerOptionValueId>> optionNameMap) {
        return selectedOptions.stream()
                .map(
                        so -> {
                            Map<String, SellerOptionValueId> valueMap =
                                    optionNameMap.get(so.optionGroupName());
                            if (valueMap == null) {
                                throw new IllegalArgumentException(
                                        "존재하지 않는 옵션 그룹: " + so.optionGroupName());
                            }
                            SellerOptionValueId valueId = valueMap.get(so.optionValueName());
                            if (valueId == null) {
                                throw new IllegalArgumentException(
                                        "존재하지 않는 옵션 값: "
                                                + so.optionGroupName()
                                                + " > "
                                                + so.optionValueName());
                            }
                            return valueId;
                        })
                .toList();
    }
}
