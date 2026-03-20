package com.ryuqq.marketplace.application.legacy.productgroup.internal;

import com.ryuqq.marketplace.application.legacy.productgroupdescription.internal.LegacyDescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroupimage.internal.LegacyImageCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productnotice.internal.LegacyNoticeCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.product.internal.LegacyDeliveryCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.product.internal.LegacyOptionUpdateCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.bundle.LegacyProductGroupUpdateBundle;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품그룹 전체 수정 Coordinator.
 *
 * <p>Bundle에 포함된 per-package Command/VO를 사용하여 각 도메인을 독립적으로 수정합니다. null인 필드는 변경 대상이 아니므로 건너뜁니다.
 *
 * <p>변환 로직은 {@link
 * com.ryuqq.marketplace.application.legacy.productgroup.factory.LegacyProductGroupBundleFactory}가
 * 담당합니다.
 */
@Component
public class LegacyProductGroupFullUpdateCoordinator {

    private final LegacyProductGroupCommandCoordinator productGroupCommandCoordinator;
    private final LegacyNoticeCommandCoordinator noticeCommandCoordinator;
    private final LegacyDeliveryCommandCoordinator deliveryCommandCoordinator;
    private final LegacyDescriptionCommandCoordinator descriptionCommandCoordinator;
    private final LegacyImageCommandCoordinator imageCommandCoordinator;
    private final LegacyOptionUpdateCoordinator optionUpdateCoordinator;

    public LegacyProductGroupFullUpdateCoordinator(
            LegacyProductGroupCommandCoordinator productGroupCommandCoordinator,
            LegacyNoticeCommandCoordinator noticeCommandCoordinator,
            LegacyDeliveryCommandCoordinator deliveryCommandCoordinator,
            LegacyDescriptionCommandCoordinator descriptionCommandCoordinator,
            LegacyImageCommandCoordinator imageCommandCoordinator,
            LegacyOptionUpdateCoordinator optionUpdateCoordinator) {
        this.productGroupCommandCoordinator = productGroupCommandCoordinator;
        this.noticeCommandCoordinator = noticeCommandCoordinator;
        this.deliveryCommandCoordinator = deliveryCommandCoordinator;
        this.descriptionCommandCoordinator = descriptionCommandCoordinator;
        this.imageCommandCoordinator = imageCommandCoordinator;
        this.optionUpdateCoordinator = optionUpdateCoordinator;
    }

    /**
     * 번들 기반 전체 수정을 조율합니다.
     *
     * <p>null이 아닌 필드만 per-package Coordinator에 위임합니다.
     *
     * @param bundle 수정 번들 (Factory가 생성)
     */
    @Transactional
    public void execute(LegacyProductGroupUpdateBundle bundle) {
        LegacyProductGroupId groupId = bundle.groupId();
        Instant changedAt = bundle.changedAt();

        if (bundle.basicInfoUpdateData() != null) {
            productGroupCommandCoordinator.updateBasicInfo(
                    groupId, bundle.basicInfoUpdateData(), changedAt);
        }

        if (bundle.notice() != null) {
            noticeCommandCoordinator.update(groupId, bundle.notice(), changedAt);
        }

        if (bundle.delivery() != null) {
            deliveryCommandCoordinator.update(groupId, bundle.delivery(), changedAt);
        }

        if (bundle.descriptionCommand() != null) {
            descriptionCommandCoordinator.update(bundle.descriptionCommand());
        }

        if (bundle.imageCommand() != null) {
            imageCommandCoordinator.update(bundle.imageCommand());
        }

        if (bundle.productCommand() != null) {
            optionUpdateCoordinator.execute(bundle.productCommand());
        }
    }
}
