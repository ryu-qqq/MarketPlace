package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateProductGroupCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateProductGroupCommand.DeliveryCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateProductGroupCommand.NoticeCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateProductGroupCommand.ProductGroupDetailsCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateProductGroupCommand.UpdateStatusCommand;
import com.ryuqq.marketplace.application.legacyproduct.factory.LegacyProductGroupCommandFactory;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductDeliveryCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductDescriptionCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductGroupCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductGroupReadManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductNoticeCommandManager;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductDelivery;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductDescription;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductGroupUpdateData;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductNotice;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ManagementType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.Origin;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ProductCondition;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ReturnMethod;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ShipmentCompanyCode;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품그룹 전체 수정 Coordinator.
 *
 * <p>updateStatus 플래그에 따라 변경된 섹션만 선택적으로 업데이트합니다. 상품그룹 레벨 변경(기본정보, 고시정보, 배송정보, 상세설명)은 직접 처리하고, 이미지와
 * 옵션은 기존 전문 Coordinator에 위임합니다.
 */
@Component
public class LegacyProductGroupFullUpdateCoordinator {

    private final LegacyProductGroupReadManager readManager;
    private final LegacyProductGroupCommandManager commandManager;
    private final LegacyProductNoticeCommandManager noticeCommandManager;
    private final LegacyProductDeliveryCommandManager deliveryCommandManager;
    private final LegacyProductDescriptionCommandManager descriptionCommandManager;
    private final LegacyProductGroupCommandFactory commandFactory;
    private final LegacyImageCoordinator imageCoordinator;
    private final LegacyOptionUpdateCoordinator optionUpdateCoordinator;

    public LegacyProductGroupFullUpdateCoordinator(
            LegacyProductGroupReadManager readManager,
            LegacyProductGroupCommandManager commandManager,
            LegacyProductNoticeCommandManager noticeCommandManager,
            LegacyProductDeliveryCommandManager deliveryCommandManager,
            LegacyProductDescriptionCommandManager descriptionCommandManager,
            LegacyProductGroupCommandFactory commandFactory,
            LegacyImageCoordinator imageCoordinator,
            LegacyOptionUpdateCoordinator optionUpdateCoordinator) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.noticeCommandManager = noticeCommandManager;
        this.deliveryCommandManager = deliveryCommandManager;
        this.descriptionCommandManager = descriptionCommandManager;
        this.commandFactory = commandFactory;
        this.imageCoordinator = imageCoordinator;
        this.optionUpdateCoordinator = optionUpdateCoordinator;
    }

    @Transactional
    public void execute(LegacyUpdateProductGroupCommand command) {
        LegacyProductGroupId groupId = LegacyProductGroupId.of(command.productGroupId());
        UpdateStatusCommand status = command.updateStatus();
        Instant changedAt = Instant.now();

        boolean productGroupChanged = false;
        LegacyProductGroup productGroup = readManager.getById(groupId);

        if (status.productStatus()) {
            LegacyProductGroupUpdateData updateData = toUpdateData(command.productGroupDetails());
            productGroup.updateProductGroupDetails(updateData, changedAt);
            productGroupChanged = true;
        }

        if (status.noticeStatus()) {
            LegacyProductNotice notice = toNotice(command.notice());
            productGroup.updateNotice(notice, changedAt);
            noticeCommandManager.persist(groupId, productGroup.notice());
            productGroupChanged = true;
        }

        if (status.deliveryStatus() || status.refundStatus()) {
            LegacyProductDelivery delivery = toDelivery(command.delivery());
            productGroup.updateDelivery(delivery, changedAt);
            deliveryCommandManager.persist(groupId, productGroup.delivery());
            productGroupChanged = true;
        }

        if (status.descriptionStatus()) {
            LegacyProductDescription description =
                    new LegacyProductDescription(command.detailDescription());
            productGroup.updateDescription(description, changedAt);
            descriptionCommandManager.persist(groupId, productGroup.description());
            productGroupChanged = true;
        }

        if (productGroupChanged) {
            commandManager.persist(productGroup);
        }

        if (status.imageStatus() && !command.images().isEmpty()) {
            List<LegacyProductImage> newImages =
                    commandFactory.createImagesFromFullUpdate(groupId, command.images());
            imageCoordinator.updateImages(groupId, newImages);
        }

        if (status.stockOptionStatus() && !command.options().isEmpty()) {
            List<LegacyProduct> newProducts =
                    commandFactory.createProductsFromFullUpdate(groupId, command.options());
            optionUpdateCoordinator.execute(groupId, newProducts);
        }
    }

    private LegacyProductGroupUpdateData toUpdateData(ProductGroupDetailsCommand details) {
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

    private LegacyProductNotice toNotice(NoticeCommand notice) {
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

    private LegacyProductDelivery toDelivery(DeliveryCommand delivery) {
        return new LegacyProductDelivery(
                delivery.deliveryArea(),
                delivery.deliveryFee(),
                delivery.deliveryPeriodAverage(),
                ReturnMethod.valueOf(delivery.returnMethodDomestic()),
                ShipmentCompanyCode.valueOf(delivery.returnCourierDomestic()),
                delivery.returnChargeDomestic(),
                delivery.returnExchangeAreaDomestic());
    }
}
