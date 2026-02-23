package com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateProductGroupRequest;
import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
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

        String rawPayloadJson = serializeToJson(request);

        return new ReceiveInboundProductCommand(
                inboundSourceId,
                String.valueOf(request.productGroupId()),
                request.productGroupName(),
                String.valueOf(request.brandId()),
                String.valueOf(request.categoryId()),
                request.sellerId(),
                regularPrice,
                currentPrice,
                request.optionType(),
                request.detailDescription(),
                rawPayloadJson);
    }

    private String serializeToJson(LegacyCreateProductGroupRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            log.error(
                    "레거시 상품 등록 요청 직렬화 실패: productGroupId={}, sellerId={}",
                    request.productGroupId(),
                    request.sellerId(),
                    e);
            throw new IllegalStateException("레거시 요청 직렬화에 실패했습니다.", e);
        }
    }
}
