package com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductGroupRequest;
import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** 레거시 세토프 요청을 InboundProduct 수신 커맨드로 변환하는 매퍼. */
@Component
public class LegacyInboundApiMapper {

    private static final Logger log = LoggerFactory.getLogger(LegacyInboundApiMapper.class);

    private final ObjectMapper objectMapper;

    public LegacyInboundApiMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ReceiveInboundProductCommand toCommand(
            LegacyCreateProductGroupRequest request, long inboundSourceId) {

        int regularPrice = (int) request.price().regularPrice();
        int currentPrice = (int) request.price().currentPrice();

        if (regularPrice < currentPrice) {
            throw new IllegalArgumentException("정상가는 판매가보다 작을 수 없습니다.");
        }

        String rawPayloadJson = serializeToJson(request, "레거시 상품 등록 요청");
        OptionType internalOptionType = mapLegacyOptionTypeToInternal(request.optionType());

        return new ReceiveInboundProductCommand(
                inboundSourceId,
                UUID.randomUUID().toString(),
                request.productGroupName(),
                String.valueOf(request.brandId()),
                String.valueOf(request.categoryId()),
                request.sellerId(),
                regularPrice,
                currentPrice,
                internalOptionType.name(),
                request.detailDescription(),
                rawPayloadJson);
    }

    /**
     * 레거시 상품 수정 요청을 InboundProduct 업데이트 커맨드로 변환합니다.
     *
     * <p>수정 요청에 없는 필수 필드는 서비스에서 기존 InboundProduct 값으로 병합합니다.
     */
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

    /** 레거시(SINGLE, OPTION_ONE, OPTION_TWO) → 내부(NONE, SINGLE, COMBINATION) 매핑. */
    private OptionType mapLegacyOptionTypeToInternal(String legacyOptionType) {
        return switch (legacyOptionType.trim().toUpperCase()) {
            case "SINGLE" -> OptionType.NONE;
            case "OPTION_ONE" -> OptionType.SINGLE;
            case "OPTION_TWO" -> OptionType.COMBINATION;
            default -> OptionType.NONE;
        };
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
