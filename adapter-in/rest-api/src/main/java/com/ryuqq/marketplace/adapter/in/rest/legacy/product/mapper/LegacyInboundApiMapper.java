package com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateDeliveryNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateOptionRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateProductImageRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateRefundNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductGroupRequest;
import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyRegisterProductGroupCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyRegisterProductGroupCommand.DeliveryCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyRegisterProductGroupCommand.ImageCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyRegisterProductGroupCommand.NoticeCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyRegisterProductGroupCommand.OptionCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyRegisterProductGroupCommand.OptionDetailCommand;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** 레거시 세토프 등록/수정 요청을 Command로 변환하는 매퍼. */
@Component
public class LegacyInboundApiMapper {

    private static final Logger log = LoggerFactory.getLogger(LegacyInboundApiMapper.class);

    private final ObjectMapper objectMapper;

    public LegacyInboundApiMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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

    // ===== 수정(기존 InboundProduct 파이프라인 유지) =====

    /** 레거시 상품 수정 요청을 ReceiveInboundProductCommand로 변환합니다. */
    public ReceiveInboundProductCommand toUpdateCommand(
            LegacyUpdateProductGroupRequest request,
            long inboundSourceId,
            long setofProductGroupId) {
        String descriptionHtml =
                request.detailDescription() != null
                        ? request.detailDescription().detailDescription()
                        : null;

        return new ReceiveInboundProductCommand(
                inboundSourceId,
                String.valueOf(setofProductGroupId),
                null,
                null,
                null,
                -1L,
                -1,
                -1,
                null,
                descriptionHtml,
                serializeToJson(request, "레거시 상품 수정 요청"));
    }

    private String serializeToJson(Object request, String requestLabel) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            log.error("{} 직렬화 실패", requestLabel, e);
            throw new IllegalStateException("레거시 요청 직렬화에 실패했습니다.", e);
        }
    }
}
