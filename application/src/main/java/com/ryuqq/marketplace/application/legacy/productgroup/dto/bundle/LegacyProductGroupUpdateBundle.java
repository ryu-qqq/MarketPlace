package com.ryuqq.marketplace.application.legacy.productgroup.dto.bundle;

import com.ryuqq.marketplace.application.legacy.description.dto.command.LegacyUpdateDescriptionCommand;
import com.ryuqq.marketplace.application.legacy.image.dto.command.LegacyUpdateImagesCommand;
import com.ryuqq.marketplace.application.legacy.product.dto.command.LegacyUpdateProductsCommand;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductDelivery;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductGroupUpdateData;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductNotice;
import java.time.Instant;

/**
 * 레거시 상품그룹 전체 수정 번들.
 *
 * <p>updateStatus 플래그 기반으로 변경된 섹션만 포함합니다. null인 필드는 변경 대상이 아닙니다.
 *
 * <p>BundleFactory가 Command → Bundle 변환을 담당하고, Coordinator는 Bundle만 받아서 per-package Coordinator에
 * 위임합니다.
 *
 * @param groupId 대상 상품그룹 ID (not null)
 * @param changedAt 변경 시각 (not null, Factory가 TimeProvider로 생성)
 * @param basicInfoUpdateData 기본정보 업데이트 데이터 (nullable - 변경 없으면 null)
 * @param notice 고시정보 (nullable - 변경 없으면 null)
 * @param delivery 배송/반품정보 (nullable - 변경 없으면 null)
 * @param descriptionCommand 상세설명 수정 Command (nullable - 변경 없으면 null)
 * @param imageCommand 이미지 수정 Command (nullable - 변경 없으면 null)
 * @param productCommand 상품/옵션 수정 Command (nullable - 변경 없으면 null)
 */
public record LegacyProductGroupUpdateBundle(
        LegacyProductGroupId groupId,
        Instant changedAt,
        LegacyProductGroupUpdateData basicInfoUpdateData,
        LegacyProductNotice notice,
        LegacyProductDelivery delivery,
        LegacyUpdateDescriptionCommand descriptionCommand,
        LegacyUpdateImagesCommand imageCommand,
        LegacyUpdateProductsCommand productCommand) {}
