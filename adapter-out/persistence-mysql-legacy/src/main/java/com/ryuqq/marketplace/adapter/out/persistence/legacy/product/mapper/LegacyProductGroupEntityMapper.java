package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionDetailEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductDeliveryEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupDetailDescriptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductNoticeEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductOptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductStockEntity;
import com.ryuqq.marketplace.application.legacyproduct.dto.setof.SetofProductGroupComposite;
import com.ryuqq.marketplace.application.legacyproduct.dto.setof.SetofProductGroupComposite.DeliveryData;
import com.ryuqq.marketplace.application.legacyproduct.dto.setof.SetofProductGroupComposite.DetailDescriptionData;
import com.ryuqq.marketplace.application.legacyproduct.dto.setof.SetofProductGroupComposite.ImageData;
import com.ryuqq.marketplace.application.legacyproduct.dto.setof.SetofProductGroupComposite.NoticeData;
import com.ryuqq.marketplace.application.legacyproduct.dto.setof.SetofProductGroupComposite.OptionDetailData;
import com.ryuqq.marketplace.application.legacyproduct.dto.setof.SetofProductGroupComposite.OptionGroupData;
import com.ryuqq.marketplace.application.legacyproduct.dto.setof.SetofProductGroupComposite.ProductData;
import com.ryuqq.marketplace.application.legacyproduct.dto.setof.SetofProductGroupComposite.ProductGroupData;
import com.ryuqq.marketplace.application.legacyproduct.dto.setof.SetofProductGroupComposite.ProductOptionData;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 레거시 엔티티 → SetofProductGroupComposite 변환 Mapper.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class LegacyProductGroupEntityMapper {

    public SetofProductGroupComposite toComposite(
            LegacyProductGroupEntity productGroup,
            List<LegacyProductEntity> products,
            List<LegacyProductOptionEntity> productOptions,
            List<LegacyProductStockEntity> stocks,
            List<LegacyProductGroupImageEntity> images,
            LegacyProductGroupDetailDescriptionEntity detailDescription,
            LegacyProductNoticeEntity notice,
            LegacyProductDeliveryEntity delivery,
            List<LegacyOptionGroupEntity> optionGroups,
            List<LegacyOptionDetailEntity> optionDetails) {

        return new SetofProductGroupComposite(
                toProductGroupData(productGroup),
                products.stream().map(this::toProductData).toList(),
                productOptions.stream().map(this::toProductOptionData).toList(),
                toStockMap(stocks),
                images.stream().map(this::toImageData).toList(),
                toDetailDescriptionData(detailDescription),
                toNoticeData(notice),
                toDeliveryData(delivery),
                optionGroups.stream().map(this::toOptionGroupData).toList(),
                optionDetails.stream().map(this::toOptionDetailData).toList());
    }

    private ProductGroupData toProductGroupData(LegacyProductGroupEntity e) {
        return new ProductGroupData(
                e.getId(),
                e.getExternalProductUuid(),
                e.getProductGroupName(),
                e.getSellerId(),
                e.getBrandId(),
                e.getCategoryId(),
                e.getOptionType(),
                e.getRegularPrice(),
                e.getCurrentPrice(),
                e.getSalePrice(),
                e.getDirectDiscountRate() != null ? e.getDirectDiscountRate() : 0,
                e.getDirectDiscountPrice() != null ? e.getDirectDiscountPrice() : 0L,
                e.getDiscountRate() != null ? e.getDiscountRate() : 0,
                e.getSoldOutYn(),
                e.getDisplayYn(),
                e.getProductCondition(),
                e.getOrigin(),
                e.getStyleCode(),
                e.getManagementType(),
                e.getDeleteYn(),
                e.getInsertDate(),
                e.getUpdateDate());
    }

    private ProductData toProductData(LegacyProductEntity e) {
        return new ProductData(
                e.getId(),
                e.getProductGroupId(),
                e.getSoldOutYn(),
                e.getDisplayYn(),
                e.getDeleteYn(),
                e.getInsertDate(),
                e.getUpdateDate());
    }

    private ProductOptionData toProductOptionData(LegacyProductOptionEntity e) {
        return new ProductOptionData(
                e.getId(),
                e.getProductId(),
                e.getOptionGroupId(),
                e.getOptionDetailId(),
                e.getAdditionalPrice());
    }

    private Map<Long, Integer> toStockMap(List<LegacyProductStockEntity> stocks) {
        Map<Long, Integer> map = new LinkedHashMap<>();
        for (LegacyProductStockEntity stock : stocks) {
            map.put(stock.getProductId(), stock.getStockQuantity());
        }
        return map;
    }

    private ImageData toImageData(LegacyProductGroupImageEntity e) {
        return new ImageData(
                e.getId(),
                e.getProductGroupId(),
                e.getProductGroupImageType(),
                e.getImageUrl(),
                e.getOriginUrl(),
                e.getDisplayOrder());
    }

    private DetailDescriptionData toDetailDescriptionData(
            LegacyProductGroupDetailDescriptionEntity e) {
        if (e == null) {
            return null;
        }
        return new DetailDescriptionData(
                e.getProductGroupId(),
                e.getProductGroupImageType(),
                e.getImageUrl(),
                e.getImageUrls());
    }

    private NoticeData toNoticeData(LegacyProductNoticeEntity e) {
        if (e == null) {
            return null;
        }
        return new NoticeData(
                e.getProductGroupId(),
                e.getMaterial(),
                e.getColor(),
                e.getSize(),
                e.getMaker(),
                e.getOrigin(),
                e.getWashingMethod(),
                e.getYearMonthDay(),
                e.getAssuranceStandard(),
                e.getAsPhone());
    }

    private DeliveryData toDeliveryData(LegacyProductDeliveryEntity e) {
        if (e == null) {
            return null;
        }
        return new DeliveryData(
                e.getProductGroupId(),
                e.getDeliveryArea(),
                e.getDeliveryFee(),
                e.getDeliveryPeriodAverage(),
                e.getReturnMethodDomestic(),
                e.getReturnCourierDomestic(),
                e.getReturnChargeDomestic(),
                e.getReturnExchangeAreaDomestic());
    }

    private OptionGroupData toOptionGroupData(LegacyOptionGroupEntity e) {
        return new OptionGroupData(e.getId(), e.getOptionName());
    }

    private OptionDetailData toOptionDetailData(LegacyOptionDetailEntity e) {
        return new OptionDetailData(e.getId(), e.getOptionGroupId(), e.getOptionValue());
    }
}
