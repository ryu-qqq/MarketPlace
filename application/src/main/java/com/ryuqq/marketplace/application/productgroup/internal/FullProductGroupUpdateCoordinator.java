package com.ryuqq.marketplace.application.productgroup.internal;

import com.ryuqq.marketplace.application.product.factory.ProductCommandFactory;
import com.ryuqq.marketplace.application.product.internal.ProductCommandCoordinator;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.productgroupimage.internal.ImageCommandCoordinator;
import com.ryuqq.marketplace.application.productintelligence.manager.IntelligenceOutboxCommandManager;
import com.ryuqq.marketplace.application.productnotice.internal.ProductNoticeCommandCoordinator;
import com.ryuqq.marketplace.application.selleroption.dto.result.SellerOptionUpdateResult;
import com.ryuqq.marketplace.application.selleroption.internal.SellerOptionCommandCoordinator;
import com.ryuqq.marketplace.domain.product.vo.ProductUpdateData;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.IntelligenceOutbox;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 그룹 전체 Aggregate 수정 Coordinator.
 *
 * <p>ProductGroup 기본 정보 수정 → per-package Coordinator 위임 순서로 전체 수정을 조율합니다.
 *
 * <p>등록 플로우는 {@link FullProductGroupRegistrationCoordinator}를 사용합니다.
 */
@Component
public class FullProductGroupUpdateCoordinator {

    private final ProductGroupCommandCoordinator productGroupCommandCoordinator;
    private final ImageCommandCoordinator imageCommandCoordinator;
    private final SellerOptionCommandCoordinator sellerOptionCommandCoordinator;
    private final DescriptionCommandCoordinator descriptionCommandCoordinator;
    private final ProductNoticeCommandCoordinator noticeCommandCoordinator;
    private final ProductCommandFactory productCommandFactory;
    private final ProductCommandCoordinator productCommandCoordinator;
    private final IntelligenceOutboxCommandManager intelligenceOutboxCommandManager;

    public FullProductGroupUpdateCoordinator(
            ProductGroupCommandCoordinator productGroupCommandCoordinator,
            ImageCommandCoordinator imageCommandCoordinator,
            SellerOptionCommandCoordinator sellerOptionCommandCoordinator,
            DescriptionCommandCoordinator descriptionCommandCoordinator,
            ProductNoticeCommandCoordinator noticeCommandCoordinator,
            ProductCommandFactory productCommandFactory,
            ProductCommandCoordinator productCommandCoordinator,
            IntelligenceOutboxCommandManager intelligenceOutboxCommandManager) {
        this.productGroupCommandCoordinator = productGroupCommandCoordinator;
        this.imageCommandCoordinator = imageCommandCoordinator;
        this.sellerOptionCommandCoordinator = sellerOptionCommandCoordinator;
        this.descriptionCommandCoordinator = descriptionCommandCoordinator;
        this.noticeCommandCoordinator = noticeCommandCoordinator;
        this.productCommandFactory = productCommandFactory;
        this.productCommandCoordinator = productCommandCoordinator;
        this.intelligenceOutboxCommandManager = intelligenceOutboxCommandManager;
    }

    /**
     * 상품 그룹 전체 수정을 조율합니다.
     *
     * <p>번들에 포함된 per-package Update Command를 사용하여 각 도메인을 독립적으로 수정합니다.
     *
     * <p>AI 재검수는 AI가 실제 분석하는 필드(상품명, 옵션, 상세설명, 고시정보)가 변경된 경우에만 트리거됩니다.
     *
     * @param bundle 수정 번들 (ProductGroupUpdateData + per-package Update Commands)
     */
    @Transactional
    public void update(ProductGroupUpdateBundle bundle) {
        // 1. ProductGroup 기본 정보 (검증 + 조회 + update + persist) → Coordinator
        boolean nameChanged = productGroupCommandCoordinator.update(bundle.basicInfoUpdateData());

        // 2. Images → Coordinator (AI 분석 대상 아님 - 별도 이미지 파이프라인)
        imageCommandCoordinator.update(bundle.imageCommand());

        // 3. OptionGroups → Coordinator (diff 기반, ID 보존)
        SellerOptionUpdateResult optionResult =
                sellerOptionCommandCoordinator.update(bundle.optionGroupCommand());

        // 4. Description → Coordinator
        boolean descriptionChanged =
                descriptionCommandCoordinator.update(bundle.descriptionCommand());

        // 5. Notice → Coordinator
        boolean noticeChanged = noticeCommandCoordinator.update(bundle.noticeCommand());

        // 6. Products → Factory(resolve) + Coordinator(도메인 diff)
        ProductGroupId pgId = bundle.basicInfoUpdateData().productGroupId();
        ProductUpdateData updateData =
                productCommandFactory.toUpdateData(
                        pgId,
                        bundle.productEntries(),
                        bundle.optionGroupCommand().optionGroups(),
                        optionResult.resolvedActiveValueIds(),
                        optionResult.occurredAt());
        productCommandCoordinator.update(pgId, updateData);

        // 7. AI 분석 대상 필드가 변경된 경우에만 Intelligence Outbox 생성
        boolean needsReinspection =
                nameChanged || optionResult.hasChanges() || descriptionChanged || noticeChanged;
        if (needsReinspection) {
            IntelligenceOutbox intelligenceOutbox =
                    IntelligenceOutbox.forNew(pgId.value(), Instant.now());
            intelligenceOutboxCommandManager.persist(intelligenceOutbox);
        }
    }
}
