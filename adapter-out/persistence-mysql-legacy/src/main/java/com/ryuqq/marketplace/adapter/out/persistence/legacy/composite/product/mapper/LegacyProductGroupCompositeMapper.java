package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductGroupBasicQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductGroupImageQueryDto;
import com.ryuqq.marketplace.application.legacyproduct.dto.composite.LegacyProductGroupCompositeResult;
import com.ryuqq.marketplace.application.legacyproduct.dto.composite.LegacyProductGroupCompositeResult.DeliveryInfo;
import com.ryuqq.marketplace.application.legacyproduct.dto.composite.LegacyProductGroupCompositeResult.ImageInfo;
import com.ryuqq.marketplace.application.legacyproduct.dto.composite.LegacyProductGroupCompositeResult.NoticeInfo;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB 상품그룹 Composite Mapper.
 *
 * <p>JOIN 쿼리 결과(flat DTO + 이미지 목록)를 {@link LegacyProductGroupCompositeResult}로 변환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class LegacyProductGroupCompositeMapper {

    /**
     * flat DTO + 이미지 목록 → 상품그룹 Composite 결과 변환.
     *
     * @param dto 7개 테이블 JOIN flat projection
     * @param images 이미지 projection 목록
     * @return 상품그룹 Composite 결과
     */
    public LegacyProductGroupCompositeResult toCompositeResult(
            LegacyProductGroupBasicQueryDto dto, List<LegacyProductGroupImageQueryDto> images) {

        return new LegacyProductGroupCompositeResult(
                dto.productGroupId(),
                dto.productGroupName(),
                dto.sellerId(),
                dto.sellerName(),
                dto.brandId(),
                dto.brandName(),
                dto.categoryId(),
                dto.categoryPath(),
                dto.optionType(),
                dto.managementType(),
                nullSafe(dto.regularPrice()),
                nullSafe(dto.currentPrice()),
                nullSafe(dto.salePrice()),
                nullSafe(dto.directDiscountPrice()),
                nullSafeInt(dto.directDiscountRate()),
                nullSafeInt(dto.discountRate()),
                "Y".equals(dto.soldOutYn()),
                "Y".equals(dto.displayYn()),
                dto.productCondition(),
                dto.origin(),
                dto.styleCode(),
                dto.insertOperator(),
                dto.updateOperator(),
                dto.insertDate(),
                dto.updateDate(),
                toImageInfos(images),
                dto.detailDescription(),
                toNoticeInfo(dto),
                toDeliveryInfo(dto));
    }

    private List<ImageInfo> toImageInfos(List<LegacyProductGroupImageQueryDto> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }
        return images.stream().map(img -> new ImageInfo(img.imageType(), img.imageUrl())).toList();
    }

    private NoticeInfo toNoticeInfo(LegacyProductGroupBasicQueryDto dto) {
        if (dto.material() == null
                && dto.color() == null
                && dto.noticeSize() == null
                && dto.maker() == null) {
            return null;
        }
        return new NoticeInfo(
                dto.material(),
                dto.color(),
                dto.noticeSize(),
                dto.maker(),
                dto.noticeOrigin(),
                dto.washingMethod(),
                dto.yearMonthDay(),
                dto.assuranceStandard(),
                dto.asPhone());
    }

    private DeliveryInfo toDeliveryInfo(LegacyProductGroupBasicQueryDto dto) {
        if (dto.deliveryArea() == null && dto.deliveryFee() == null) {
            return null;
        }
        return new DeliveryInfo(
                dto.deliveryArea(),
                dto.deliveryFee(),
                dto.deliveryPeriodAverage(),
                dto.returnMethodDomestic(),
                dto.returnCourierDomestic(),
                dto.returnChargeDomestic(),
                dto.returnExchangeAreaDomestic());
    }

    private long nullSafe(long value) {
        return value;
    }

    private int nullSafeInt(int value) {
        return value;
    }
}
