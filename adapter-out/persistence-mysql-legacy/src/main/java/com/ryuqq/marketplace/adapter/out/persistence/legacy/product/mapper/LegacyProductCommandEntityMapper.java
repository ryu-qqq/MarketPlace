package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductDeliveryEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupDetailDescriptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductNoticeEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductOptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductStockEntity;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import com.ryuqq.marketplace.domain.legacy.product.id.LegacyProductId;
import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductOption;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductDelivery;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductDescription;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductNotice;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ManagementType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.Origin;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ProductCondition;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import com.ryuqq.marketplace.domain.legacy.productimage.vo.ProductGroupImageType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Component;

/** 세토프 도메인 객체 ↔ JPA Entity 변환 Mapper. */
@Component
public class LegacyProductCommandEntityMapper {

    private static final ZoneId ZONE_KST = ZoneId.of("Asia/Seoul");

    public LegacyProductGroupEntity toEntity(LegacyProductGroup data) {
        return LegacyProductGroupEntity.create(
                data.idValue(),
                data.productGroupName(),
                data.sellerId(),
                data.brandId(),
                data.categoryId(),
                data.optionType().name(),
                data.managementType().name(),
                data.regularPrice(),
                data.currentPrice(),
                data.soldOutYn(),
                data.displayYn(),
                data.productCondition().name(),
                data.origin().name(),
                data.styleCode());
    }

    public LegacyProductGroup toDomain(LegacyProductGroupEntity entity) {
        return LegacyProductGroup.reconstitute(
                entity.getId(),
                entity.getProductGroupName(),
                entity.getSellerId(),
                entity.getBrandId(),
                entity.getCategoryId(),
                OptionType.valueOf(entity.getOptionType()),
                ManagementType.valueOf(entity.getManagementType()),
                entity.getRegularPrice(),
                entity.getCurrentPrice(),
                entity.getSoldOutYn(),
                entity.getDisplayYn(),
                ProductCondition.valueOf(entity.getProductCondition()),
                Origin.valueOf(entity.getOrigin()),
                entity.getStyleCode(),
                null,
                null,
                null,
                toInstant(entity.getInsertDate()),
                toInstant(entity.getUpdateDate()));
    }

    private Instant toInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZONE_KST).toInstant();
    }

    public LegacyProductEntity toEntity(LegacyProduct data) {
        return LegacyProductEntity.create(
                data.productGroupIdValue(), data.soldOutYn(), data.displayYn());
    }

    public LegacyProductStockEntity toEntity(LegacyProductId productId, int stockQuantity) {
        return LegacyProductStockEntity.create(productId.value(), stockQuantity);
    }

    public LegacyProductOptionEntity toEntity(LegacyProductOption data) {
        return LegacyProductOptionEntity.create(
                data.productId().value(),
                data.optionGroupId().value(),
                data.optionDetailId().value(),
                data.additionalPrice());
    }

    public LegacyProductNoticeEntity toEntity(
            LegacyProductGroupId productGroupId, LegacyProductNotice data) {
        return LegacyProductNoticeEntity.create(
                productGroupId.value(),
                data.material(),
                data.color(),
                data.size(),
                data.maker(),
                data.origin(),
                data.washingMethod(),
                data.yearMonthDay(),
                data.assuranceStandard(),
                data.asPhone());
    }

    public LegacyProductDeliveryEntity toEntity(
            LegacyProductGroupId productGroupId, LegacyProductDelivery data) {
        return LegacyProductDeliveryEntity.create(
                productGroupId.value(),
                data.deliveryArea(),
                data.deliveryFee(),
                data.deliveryPeriodAverage(),
                data.returnMethodDomestic().name(),
                data.returnCourierDomestic().name(),
                data.returnChargeDomestic(),
                data.returnExchangeAreaDomestic());
    }

    public LegacyProductGroupImageEntity toEntity(LegacyProductImage data) {
        return LegacyProductGroupImageEntity.create(
                data.idValue(),
                data.productGroupIdValue(),
                data.imageType().name(),
                data.imageUrl(),
                data.originUrl(),
                data.displayOrder(),
                data.isDeleted() ? "Y" : "N");
    }

    public LegacyProductImage toImageDomain(LegacyProductGroupImageEntity entity) {
        return LegacyProductImage.reconstitute(
                entity.getId(),
                entity.getProductGroupId(),
                ProductGroupImageType.valueOf(entity.getProductGroupImageType()),
                entity.getImageUrl(),
                entity.getOriginUrl(),
                entity.getDisplayOrder() != null ? entity.getDisplayOrder().intValue() : 0,
                DeletionStatus.active());
    }

    public LegacyProductGroupDetailDescriptionEntity toEntity(
            LegacyProductGroupId productGroupId, LegacyProductDescription data) {
        return LegacyProductGroupDetailDescriptionEntity.create(
                productGroupId.value(), data.detailDescription());
    }
}
