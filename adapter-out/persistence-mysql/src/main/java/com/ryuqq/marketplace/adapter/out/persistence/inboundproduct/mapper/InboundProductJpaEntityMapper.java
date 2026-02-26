package com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.entity.InboundProductJpaEntity;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.inboundproduct.id.InboundProductId;
import com.ryuqq.marketplace.domain.inboundproduct.vo.ExternalProductCode;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductStatus;
import org.springframework.stereotype.Component;

@Component
public class InboundProductJpaEntityMapper {

    public InboundProductJpaEntity toEntity(InboundProduct product) {
        return InboundProductJpaEntity.create(
                product.idValue(),
                product.inboundSourceId(),
                product.externalProductCodeValue(),
                product.externalBrandCode(),
                product.externalCategoryCode(),
                product.internalBrandId(),
                product.internalCategoryId(),
                product.internalProductGroupId(),
                product.sellerId(),
                product.status().name(),
                product.resolvedShippingPolicyId(),
                product.resolvedRefundPolicyId(),
                product.resolvedNoticeCategoryId(),
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
                entity.getExternalBrandCode(),
                entity.getExternalCategoryCode(),
                entity.getInternalBrandId(),
                entity.getInternalCategoryId(),
                entity.getInternalProductGroupId(),
                entity.getSellerId(),
                InboundProductStatus.fromString(entity.getStatus()),
                entity.getResolvedShippingPolicyId(),
                entity.getResolvedRefundPolicyId(),
                entity.getResolvedNoticeCategoryId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
