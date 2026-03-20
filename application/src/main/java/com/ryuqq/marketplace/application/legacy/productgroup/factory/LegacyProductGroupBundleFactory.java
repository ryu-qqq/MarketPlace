package com.ryuqq.marketplace.application.legacy.productgroup.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.legacy.productgroupdescription.dto.command.LegacyUpdateDescriptionCommand;
import com.ryuqq.marketplace.application.legacy.productgroupimage.dto.command.LegacyUpdateImagesCommand;
import com.ryuqq.marketplace.application.legacy.product.dto.command.LegacyUpdateProductsCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.bundle.LegacyProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateProductGroupCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateProductGroupCommand.DeliveryCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateProductGroupCommand.NoticeCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateProductGroupCommand.ProductGroupDetailsCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateProductGroupCommand.UpdateStatusCommand;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productdelivery.aggregate.LegacyProductDelivery;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductGroupUpdateData;
import com.ryuqq.marketplace.domain.legacy.productnotice.aggregate.LegacyProductNotice;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ManagementType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.Origin;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ProductCondition;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ReturnMethod;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ShipmentCompanyCode;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품그룹 번들 생성 전용 Factory.
 *
 * <p>LegacyUpdateProductGroupCommand → LegacyProductGroupUpdateBundle 변환을 담당합니다. updateStatus 플래그를
 * 확인하여 변경된 섹션만 도메인 VO/Command로 변환합니다.
 *
 * <p>APP-TIM-001: TimeProvider는 Factory에서만 사용합니다.
 */
@Component
public class LegacyProductGroupBundleFactory {

    private final TimeProvider timeProvider;

    public LegacyProductGroupBundleFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * 전체 수정 Command로부터 수정 번들을 생성합니다.
     *
     * <p>updateStatus 플래그에 따라 변경된 섹션만 변환합니다. 변경되지 않은 섹션은 null로 설정됩니다.
     *
     * @param command 전체 수정 Command
     * @return 수정 번들
     */
    public LegacyProductGroupUpdateBundle createUpdateBundle(
            LegacyUpdateProductGroupCommand command) {
        UpdateStatusCommand status = command.updateStatus();
        Instant changedAt = timeProvider.now();
        LegacyProductGroupId groupId = LegacyProductGroupId.of(command.productGroupId());

        LegacyProductGroupUpdateData basicInfoUpdateData =
                status.productStatus() ? toUpdateData(command.productGroupDetails()) : null;

        LegacyProductNotice notice = status.noticeStatus() ? toNotice(command.notice()) : null;

        LegacyProductDelivery delivery =
                (status.deliveryStatus() || status.refundStatus())
                        ? toDelivery(command.delivery())
                        : null;

        LegacyUpdateDescriptionCommand descriptionCommand =
                status.descriptionStatus()
                        ? new LegacyUpdateDescriptionCommand(
                                command.productGroupId(), command.detailDescription())
                        : null;

        LegacyUpdateImagesCommand imageCommand =
                (status.imageStatus() && !command.images().isEmpty())
                        ? toImageCommand(command)
                        : null;

        LegacyUpdateProductsCommand productCommand =
                (status.stockOptionStatus() && !command.options().isEmpty())
                        ? toProductCommand(command)
                        : null;

        return new LegacyProductGroupUpdateBundle(
                groupId,
                changedAt,
                basicInfoUpdateData,
                notice,
                delivery,
                descriptionCommand,
                imageCommand,
                productCommand);
    }

    private static LegacyProductGroupUpdateData toUpdateData(ProductGroupDetailsCommand details) {
        return new LegacyProductGroupUpdateData(
                details.productGroupName(),
                details.sellerId(),
                details.brandId(),
                details.categoryId(),
                OptionType.valueOf(details.optionType()),
                ManagementType.valueOf(details.managementType()),
                details.regularPrice(),
                details.currentPrice(),
                details.soldOutYn(),
                details.displayYn(),
                ProductCondition.valueOf(details.productCondition()),
                Origin.valueOf(details.origin()),
                details.styleCode());
    }

    private static LegacyProductNotice toNotice(NoticeCommand notice) {
        return new LegacyProductNotice(
                notice.material(),
                notice.color(),
                notice.size(),
                notice.maker(),
                notice.origin(),
                notice.washingMethod(),
                notice.yearMonthDay(),
                notice.assuranceStandard(),
                notice.asPhone());
    }

    private static LegacyProductDelivery toDelivery(DeliveryCommand delivery) {
        return new LegacyProductDelivery(
                delivery.deliveryArea(),
                delivery.deliveryFee(),
                delivery.deliveryPeriodAverage(),
                ReturnMethod.valueOf(delivery.returnMethodDomestic()),
                ShipmentCompanyCode.valueOf(delivery.returnCourierDomestic()),
                delivery.returnChargeDomestic(),
                delivery.returnExchangeAreaDomestic());
    }

    private static LegacyUpdateImagesCommand toImageCommand(
            LegacyUpdateProductGroupCommand command) {
        return new LegacyUpdateImagesCommand(
                command.productGroupId(),
                command.images().stream()
                        .map(
                                img ->
                                        new LegacyUpdateImagesCommand.ImageEntry(
                                                img.imageType(), img.imageUrl(), img.originUrl()))
                        .toList());
    }

    private static LegacyUpdateProductsCommand toProductCommand(
            LegacyUpdateProductGroupCommand command) {
        return new LegacyUpdateProductsCommand(
                command.productGroupId(),
                command.options().stream()
                        .map(
                                opt ->
                                        new LegacyUpdateProductsCommand.SkuEntry(
                                                opt.productId(),
                                                opt.quantity(),
                                                opt.additionalPrice(),
                                                opt.optionDetails().stream()
                                                        .map(
                                                                d ->
                                                                        new LegacyUpdateProductsCommand
                                                                                .OptionEntry(
                                                                                d.optionGroupId(),
                                                                                d.optionDetailId(),
                                                                                d.optionName(),
                                                                                d.optionValue()))
                                                        .toList()))
                        .toList());
    }
}
