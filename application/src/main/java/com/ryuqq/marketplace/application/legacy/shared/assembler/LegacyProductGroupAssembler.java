package com.ryuqq.marketplace.application.legacy.shared.assembler;

import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductCompositeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult.DeliveryInfo;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult.ImageInfo;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult.NoticeInfo;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.response.LegacyProductGroupPageResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupDetailBundle;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyDeliveryResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyImageResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyNoticeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyOptionMappingResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyProductResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB 상품그룹 Assembler.
 *
 * <p>LegacyProductGroupDetailBundle → LegacyProductGroupDetailResult 조립을 담당합니다.
 */
@Component
public class LegacyProductGroupAssembler {

    /**
     * 번들 목록 → 레거시 페이징 결과 조립.
     *
     * @param bundles 세토프 DB 목록 조회 번들 목록
     * @param totalElements 전체 건수
     * @param page 현재 페이지 번호
     * @param size 페이지 크기
     * @return 레거시 상품그룹 페이징 결과
     */
    public LegacyProductGroupPageResult toPageResult(
            List<LegacyProductGroupDetailBundle> bundles,
            long totalElements,
            int page,
            int size) {
        if (bundles == null || bundles.isEmpty()) {
            return LegacyProductGroupPageResult.empty(page, size);
        }
        List<LegacyProductGroupDetailResult> items =
                bundles.stream().map(this::toDetailResult).toList();
        return LegacyProductGroupPageResult.of(items, totalElements, page, size);
    }

    /**
     * 번들 → 레거시 상세 결과 조립.
     *
     * @param bundle 세토프 DB 상세 조회 번들
     * @return 레거시 상품그룹 상세 결과
     */
    public LegacyProductGroupDetailResult toDetailResult(LegacyProductGroupDetailBundle bundle) {
        LegacyProductGroupCompositeResult composite = bundle.composite();

        return new LegacyProductGroupDetailResult(
                composite.productGroupId(),
                composite.productGroupName(),
                composite.sellerId(),
                composite.sellerName(),
                composite.brandId(),
                composite.brandName(),
                composite.categoryId(),
                composite.categoryPath(),
                composite.optionType(),
                composite.managementType(),
                composite.regularPrice(),
                composite.currentPrice(),
                composite.salePrice(),
                composite.directDiscountPrice(),
                composite.directDiscountRate(),
                composite.discountRate(),
                composite.soldOut(),
                composite.displayed(),
                composite.productCondition(),
                composite.origin(),
                composite.styleCode(),
                composite.insertOperator(),
                composite.updateOperator(),
                composite.createdAt(),
                composite.updatedAt(),
                toNoticeResult(composite.notice()),
                toImageResults(composite.images()),
                composite.detailDescription(),
                toDeliveryResult(composite.delivery()),
                toProductResults(bundle.products()));
    }

    private LegacyNoticeResult toNoticeResult(NoticeInfo notice) {
        if (notice == null) {
            return null;
        }
        return new LegacyNoticeResult(
                notice.material(),
                notice.color(),
                notice.size(),
                notice.maker(),
                notice.origin(),
                notice.washingMethod(),
                notice.yearMonthDay(),
                notice.assuranceStandard(),
                notice.asPhone());
    }

    private List<LegacyImageResult> toImageResults(List<ImageInfo> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }
        return images.stream()
                .map(img -> new LegacyImageResult(img.imageType(), img.imageUrl()))
                .toList();
    }

    private LegacyDeliveryResult toDeliveryResult(DeliveryInfo delivery) {
        if (delivery == null) {
            return null;
        }
        return new LegacyDeliveryResult(
                delivery.deliveryArea(),
                delivery.deliveryFee(),
                delivery.deliveryPeriodAverage(),
                delivery.returnMethodDomestic(),
                delivery.returnCourierDomestic(),
                delivery.returnChargeDomestic(),
                delivery.returnExchangeAreaDomestic());
    }

    private List<LegacyProductResult> toProductResults(
            List<LegacyProductCompositeResult> products) {
        if (products == null || products.isEmpty()) {
            return List.of();
        }
        return products.stream().map(this::toProductResult).toList();
    }

    private LegacyProductResult toProductResult(LegacyProductCompositeResult product) {
        List<LegacyOptionMappingResult> options =
                product.optionMappings().stream()
                        .map(
                                m ->
                                        new LegacyOptionMappingResult(
                                                m.optionGroupId(),
                                                m.optionDetailId(),
                                                m.optionGroupName(),
                                                m.optionValue()))
                        .toList();

        return new LegacyProductResult(
                product.productId(), product.stockQuantity(), product.soldOut(), options);
    }
}
