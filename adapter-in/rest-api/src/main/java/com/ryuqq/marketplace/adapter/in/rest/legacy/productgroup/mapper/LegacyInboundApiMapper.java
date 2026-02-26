package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.notice.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateOptionRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyCreateDeliveryNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyCreateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyCreateRefundNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyProductGroupDetailsRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyUpdateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyUpdateStatusRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.dto.request.LegacyCreateProductImageRequest;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyRegisterProductGroupCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyRegisterProductGroupCommand.DeliveryCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyRegisterProductGroupCommand.ImageCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyRegisterProductGroupCommand.NoticeCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyRegisterProductGroupCommand.OptionCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyRegisterProductGroupCommand.OptionDetailCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateProductGroupCommand;
import java.util.List;
import org.springframework.stereotype.Component;

/** 레거시 세토프 등록/수정 요청을 Command로 변환하는 매퍼. */
@Component
public class LegacyInboundApiMapper {

    public LegacyRegisterProductGroupCommand toCommand(LegacyCreateProductGroupRequest request) {
        String optionType = request.optionType().trim().toUpperCase();
        validateOptionDetails(optionType, request.productOptions());

        return new LegacyRegisterProductGroupCommand(
                request.sellerId(),
                request.brandId(),
                request.categoryId(),
                request.productGroupName(),
                optionType,
                request.managementType(),
                request.price().regularPrice(),
                request.price().currentPrice(),
                request.productStatus().soldOutYn(),
                request.productStatus().displayYn(),
                request.clothesDetailInfo().productCondition(),
                request.clothesDetailInfo().origin(),
                request.clothesDetailInfo().styleCode(),
                toNoticeCommand(request.productNotice()),
                toDeliveryCommand(request.deliveryNotice(), request.refundNotice()),
                toImageCommands(request.productImageList()),
                request.detailDescription(),
                toOptionCommands(request.productOptions()));
    }

    private void validateOptionDetails(
            String optionType, List<LegacyCreateOptionRequest> productOptions) {
        int expectedSize =
                switch (optionType) {
                    case "SINGLE" -> 0;
                    case "OPTION_ONE" -> 1;
                    case "OPTION_TWO" -> 2;
                    default ->
                            throw new IllegalArgumentException("지원하지 않는 옵션 타입입니다: " + optionType);
                };

        for (LegacyCreateOptionRequest option : productOptions) {
            if (option.options().size() != expectedSize) {
                throw new IllegalArgumentException(
                        "옵션 타입 %s에 대한 옵션 항목 수가 올바르지 않습니다. 기대: %d, 실제: %d"
                                .formatted(optionType, expectedSize, option.options().size()));
            }
        }
    }

    private NoticeCommand toNoticeCommand(LegacyCreateProductNoticeRequest notice) {
        return new NoticeCommand(
                notice.material(),
                notice.color(),
                notice.size(),
                notice.maker(),
                notice.origin(),
                notice.washingMethod(),
                notice.yearMonth(),
                notice.assuranceStandard(),
                notice.asPhone());
    }

    private DeliveryCommand toDeliveryCommand(
            LegacyCreateDeliveryNoticeRequest delivery, LegacyCreateRefundNoticeRequest refund) {
        return new DeliveryCommand(
                delivery.deliveryArea(),
                delivery.deliveryFee(),
                delivery.deliveryPeriodAverage(),
                refund.returnMethodDomestic(),
                refund.returnCourierDomestic(),
                refund.returnChargeDomestic(),
                refund.returnExchangeAreaDomestic());
    }

    private List<ImageCommand> toImageCommands(List<LegacyCreateProductImageRequest> images) {
        return images.stream()
                .map(img -> new ImageCommand(img.type(), img.productImageUrl(), img.originUrl()))
                .toList();
    }

    private List<OptionCommand> toOptionCommands(List<LegacyCreateOptionRequest> options) {
        return options.stream()
                .map(
                        opt ->
                                new OptionCommand(
                                        opt.quantity(),
                                        opt.additionalPrice() != null
                                                ? opt.additionalPrice().longValue()
                                                : 0L,
                                        opt.options().stream()
                                                .map(
                                                        d ->
                                                                new OptionDetailCommand(
                                                                        d.optionName(),
                                                                        d.optionValue()))
                                                .toList()))
                .toList();
    }

    // ===== 수정 =====

    /** 레거시 상품 수정 요청을 LegacyUpdateProductGroupCommand로 변환합니다. */
    public LegacyUpdateProductGroupCommand toUpdateCommand(
            LegacyUpdateProductGroupRequest request, long productGroupId) {
        return new LegacyUpdateProductGroupCommand(
                productGroupId,
                toProductGroupDetailsCommand(request.productGroupDetails()),
                request.productNotice() != null
                        ? toUpdateNoticeCommand(request.productNotice())
                        : null,
                request.deliveryNotice() != null && request.refundNotice() != null
                        ? toUpdateDeliveryCommand(request.deliveryNotice(), request.refundNotice())
                        : null,
                request.detailDescription() != null
                        ? request.detailDescription().detailDescription()
                        : null,
                toUpdateImageCommands(request.productImageList()),
                toUpdateOptionCommands(request.productOptions()),
                toUpdateStatusCommand(request.updateStatus()));
    }

    private LegacyUpdateProductGroupCommand.ProductGroupDetailsCommand toProductGroupDetailsCommand(
            LegacyProductGroupDetailsRequest details) {
        if (details == null) {
            return null;
        }
        return new LegacyUpdateProductGroupCommand.ProductGroupDetailsCommand(
                details.productGroupName(),
                details.optionType(),
                details.managementType(),
                details.price().regularPrice(),
                details.price().currentPrice(),
                details.productStatus().soldOutYn(),
                details.productStatus().displayYn(),
                details.clothesDetailInfo().productCondition(),
                details.clothesDetailInfo().origin(),
                details.clothesDetailInfo().styleCode(),
                details.sellerId(),
                details.categoryId(),
                details.brandId());
    }

    private LegacyUpdateProductGroupCommand.NoticeCommand toUpdateNoticeCommand(
            LegacyCreateProductNoticeRequest notice) {
        return new LegacyUpdateProductGroupCommand.NoticeCommand(
                notice.material(),
                notice.color(),
                notice.size(),
                notice.maker(),
                notice.origin(),
                notice.washingMethod(),
                notice.yearMonth(),
                notice.assuranceStandard(),
                notice.asPhone());
    }

    private LegacyUpdateProductGroupCommand.DeliveryCommand toUpdateDeliveryCommand(
            LegacyCreateDeliveryNoticeRequest delivery, LegacyCreateRefundNoticeRequest refund) {
        return new LegacyUpdateProductGroupCommand.DeliveryCommand(
                delivery.deliveryArea(),
                delivery.deliveryFee(),
                delivery.deliveryPeriodAverage(),
                refund.returnMethodDomestic(),
                refund.returnCourierDomestic(),
                refund.returnChargeDomestic(),
                refund.returnExchangeAreaDomestic());
    }

    private List<LegacyUpdateProductGroupCommand.ImageCommand> toUpdateImageCommands(
            List<LegacyCreateProductImageRequest> images) {
        return images.stream()
                .map(
                        img ->
                                new LegacyUpdateProductGroupCommand.ImageCommand(
                                        img.type(), img.productImageUrl(), img.originUrl()))
                .toList();
    }

    private List<LegacyUpdateProductGroupCommand.OptionCommand> toUpdateOptionCommands(
            List<LegacyCreateOptionRequest> options) {
        return options.stream()
                .map(
                        opt ->
                                new LegacyUpdateProductGroupCommand.OptionCommand(
                                        opt.productId(),
                                        opt.quantity(),
                                        opt.additionalPrice() != null
                                                ? opt.additionalPrice().longValue()
                                                : 0L,
                                        opt.options().stream()
                                                .map(
                                                        d ->
                                                                new LegacyUpdateProductGroupCommand
                                                                        .OptionDetailCommand(
                                                                        d.optionGroupId(),
                                                                        d.optionDetailId(),
                                                                        d.optionName(),
                                                                        d.optionValue()))
                                                .toList()))
                .toList();
    }

    private LegacyUpdateProductGroupCommand.UpdateStatusCommand toUpdateStatusCommand(
            LegacyUpdateStatusRequest status) {
        return new LegacyUpdateProductGroupCommand.UpdateStatusCommand(
                status.productStatus(),
                status.noticeStatus(),
                status.imageStatus(),
                status.descriptionStatus(),
                status.stockOptionStatus(),
                status.deliveryStatus(),
                status.refundStatus());
    }
}
