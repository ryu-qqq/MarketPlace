package com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.entity.OutboundProductImageJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
import com.ryuqq.marketplace.domain.outboundproductimage.id.OutboundProductImageId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * OutboundProductImage Entity-Domain 매퍼.
 */
@Component
public class OutboundProductImageJpaEntityMapper {

    public OutboundProductImageJpaEntity toEntity(OutboundProductImage domain) {
        Instant now = Instant.now();
        return OutboundProductImageJpaEntity.of(
                domain.idValue(),
                domain.outboundProductIdValue(),
                domain.productGroupImageIdValue(),
                domain.originUrl(),
                domain.externalUrl(),
                domain.imageType().name(),
                domain.sortOrder(),
                domain.deletionStatus().deleted(),
                domain.deletionStatus().deletedAt(),
                now,
                now);
    }

    public OutboundProductImage toDomain(OutboundProductImageJpaEntity entity) {
        OutboundProductImageId id = entity.getId() != null
                ? OutboundProductImageId.of(entity.getId())
                : OutboundProductImageId.forNew();

        DeletionStatus deletionStatus =
                DeletionStatus.reconstitute(entity.isDeleted(), entity.getDeletedAt());

        return OutboundProductImage.reconstitute(
                id,
                entity.getOutboundProductId(),
                entity.getProductGroupImageId(),
                entity.getOriginUrl(),
                entity.getExternalUrl(),
                ImageType.valueOf(entity.getImageType()),
                entity.getSortOrder(),
                deletionStatus);
    }
}
