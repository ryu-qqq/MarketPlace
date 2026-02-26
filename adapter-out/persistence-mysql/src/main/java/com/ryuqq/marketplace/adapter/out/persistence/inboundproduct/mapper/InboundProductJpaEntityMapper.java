package com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.entity.InboundProductJpaEntity;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.inboundproduct.id.InboundProductId;
import com.ryuqq.marketplace.domain.inboundproduct.vo.ExternalProductCode;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductPayload;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductStatus;
import org.springframework.stereotype.Component;

@Component
public class InboundProductJpaEntityMapper {

    private final ObjectMapper objectMapper;

    public InboundProductJpaEntityMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public InboundProductJpaEntity toEntity(InboundProduct product) {
        return InboundProductJpaEntity.create(
                product.idValue(),
                product.inboundSourceId(),
                product.externalProductCodeValue(),
                product.productName(),
                product.externalBrandCode(),
                product.externalCategoryCode(),
                product.internalBrandId(),
                product.internalCategoryId(),
                product.internalProductGroupId(),
                product.sellerId(),
                product.regularPrice(),
                product.currentPrice(),
                product.optionType(),
                product.status().name(),
                product.descriptionHtml(),
                serializePayload(product.payload()),
                product.retryCount(),
                product.createdAt(),
                product.updatedAt());
    }

    public InboundProduct toDomain(InboundProductJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        return InboundProduct.reconstitute(
                InboundProductId.of(entity.getId()),
                entity.getInboundSourceId(),
                ExternalProductCode.of(entity.getExternalProductCode()),
                entity.getProductName(),
                entity.getExternalBrandCode(),
                entity.getExternalCategoryCode(),
                entity.getInternalBrandId(),
                entity.getInternalCategoryId(),
                entity.getInternalProductGroupId(),
                entity.getSellerId(),
                entity.getRegularPrice(),
                entity.getCurrentPrice(),
                entity.getOptionType(),
                InboundProductStatus.fromString(entity.getStatus()),
                entity.getDescriptionHtml(),
                deserializePayload(entity.getRawPayloadJson()),
                entity.getRetryCount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private String serializePayload(InboundProductPayload payload) {
        if (payload == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("InboundProductPayload 직렬화 실패", e);
        }
    }

    private InboundProductPayload deserializePayload(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, InboundProductPayload.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("InboundProductPayload 역직렬화 실패", e);
        }
    }
}
