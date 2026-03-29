package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyOptionDto;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductFetchResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductStatusResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacySearchProductGroupByOffsetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyBrandResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyClothesDetailResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyCrawlProductInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyDeliveryNoticeResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyPriceResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyProductGroupInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyProductImageResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyProductNoticeResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyRefundNoticeResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductGroupListApiResponse;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.query.LegacyProductGroupSearchParams;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.response.LegacyProductGroupPageResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyDeliveryResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyImageResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyNoticeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyOptionMappingResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyProductResult;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품 조회 결과 → API 응답 변환 매퍼.
 *
 * <p>레거시 목록 조회: LegacySearchProductGroupByOffsetApiRequest → LegacyProductGroupSearchParams,
 * LegacyProductGroupPageResult → LegacyProductGroupListApiResponse
 *
 * <p>레거시 단건 조회: LegacyProductGroupDetailResult → LegacyProductDetailApiResponse
 */
@Component
public class LegacyProductGroupQueryApiMapper {

    /**
     * 레거시 목록 조회 Request → 내부 LegacyProductGroupSearchParams 변환.
     *
     * @param request 레거시 목록 조회 요청 DTO
     * @return 내부 검색 파라미터
     */
    public LegacyProductGroupSearchParams toSearchParams(
            LegacySearchProductGroupByOffsetApiRequest request) {
        return LegacyProductGroupSearchParams.of(
                request.sellerId(),
                request.brandId(),
                request.categoryId(),
                request.managementType(),
                request.soldOutYn(),
                request.displayYn(),
                request.minSalePrice(),
                request.maxSalePrice(),
                request.minDiscountRate(),
                request.maxDiscountRate(),
                request.searchKeyword(),
                request.searchWord(),
                request.startDate(),
                request.endDate(),
                request.resolvedPage(),
                request.resolvedSize());
    }

    /**
     * 내부 LegacyProductGroupPageResult → 레거시 목록 응답 변환.
     *
     * @param pageResult 페이징 처리된 레거시 상품그룹 목록 결과
     * @return 레거시 목록 조회 응답 DTO
     */
    public LegacyProductGroupListApiResponse toListResponse(
            LegacyProductGroupPageResult pageResult) {
        List<LegacyProductGroupListApiResponse.LegacyProductGroupDetailItem> items =
                pageResult.items().stream().map(this::toDetailItem).toList();

        return LegacyProductGroupListApiResponse.of(
                items, pageResult.totalElements(), pageResult.page(), pageResult.size());
    }

    private LegacyProductGroupListApiResponse.LegacyProductGroupDetailItem toDetailItem(
            LegacyProductGroupDetailResult result) {
        var productGroupInfo =
                new LegacyProductGroupListApiResponse.LegacyProductGroupInfo(
                        result.productGroupId(),
                        result.productGroupName(),
                        result.sellerId(),
                        result.sellerName(),
                        result.categoryId(),
                        result.optionType(),
                        result.managementType(),
                        new LegacyProductGroupListApiResponse.LegacyBrandInfo(
                                result.brandId(), result.brandName()),
                        new LegacyProductGroupListApiResponse.LegacyPriceInfo(
                                BigDecimal.valueOf(result.regularPrice()),
                                BigDecimal.valueOf(result.salePrice()),
                                result.discountRate()),
                        extractMainImageUrl(result.images()),
                        result.categoryPath(),
                        LegacyProductGroupListApiResponse.LegacyProductStatusInfo.of(
                                result.soldOut(), result.displayed()),
                        result.createdAt(),
                        result.updatedAt());

        List<LegacyProductGroupListApiResponse.LegacyProductItem> products =
                toProductItems(result.products());

        return new LegacyProductGroupListApiResponse.LegacyProductGroupDetailItem(
                productGroupInfo, products);
    }

    private List<LegacyProductGroupListApiResponse.LegacyProductItem> toProductItems(
            List<LegacyProductResult> products) {
        if (products == null || products.isEmpty()) {
            return List.of();
        }
        return products.stream()
                .map(
                        p ->
                                new LegacyProductGroupListApiResponse.LegacyProductItem(
                                        p.productId(),
                                        p.stockQuantity(),
                                        buildOptionString(p.options())))
                .toList();
    }

    public LegacyProductDetailApiResponse toResponse(LegacyProductGroupDetailResult result) {
        return new LegacyProductDetailApiResponse(
                toProductGroupInfo(result),
                toProductFetchResponses(result.products()),
                toNoticeResponse(result.notice()),
                toImageResponses(result.images()),
                result.detailDescription(),
                List.of());
    }

    private LegacyProductGroupInfoResponse toProductGroupInfo(
            LegacyProductGroupDetailResult result) {
        return new LegacyProductGroupInfoResponse(
                result.productGroupId(),
                result.productGroupName(),
                result.sellerId(),
                result.sellerName(),
                result.categoryId(),
                result.optionType(),
                result.managementType(),
                new LegacyBrandResponse(result.brandId(), result.brandName()),
                toPriceResponse(result),
                new LegacyClothesDetailResponse(
                        result.productCondition(), result.origin(), result.styleCode()),
                toDeliveryNotice(result.delivery()),
                toRefundNotice(result.delivery()),
                extractMainImageUrl(result.images()),
                result.categoryPath(),
                LegacyProductStatusResponse.of(result.soldOut(), result.displayed()),
                result.createdAt(),
                result.updatedAt(),
                result.insertOperator(),
                result.updateOperator(),
                LegacyCrawlProductInfoResponse.defaultValue(),
                0L,
                java.util.List.of(),
                "");
    }

    private LegacyPriceResponse toPriceResponse(LegacyProductGroupDetailResult result) {
        return new LegacyPriceResponse(
                BigDecimal.valueOf(result.regularPrice()),
                BigDecimal.valueOf(result.currentPrice()),
                BigDecimal.valueOf(result.salePrice()),
                BigDecimal.valueOf(result.directDiscountPrice()),
                result.directDiscountRate(),
                result.discountRate());
    }

    private LegacyDeliveryNoticeResponse toDeliveryNotice(LegacyDeliveryResult delivery) {
        if (delivery == null) {
            return null;
        }
        return new LegacyDeliveryNoticeResponse(
                delivery.deliveryArea(),
                delivery.deliveryFee() != null ? delivery.deliveryFee() : 0L,
                delivery.deliveryPeriodAverage() != null ? delivery.deliveryPeriodAverage() : 0);
    }

    private LegacyRefundNoticeResponse toRefundNotice(LegacyDeliveryResult delivery) {
        if (delivery == null) {
            return null;
        }
        return new LegacyRefundNoticeResponse(
                delivery.returnMethodDomestic(),
                delivery.returnCourierDomestic(),
                delivery.returnChargeDomestic() != null ? delivery.returnChargeDomestic() : 0,
                delivery.returnExchangeAreaDomestic());
    }

    private String extractMainImageUrl(List<LegacyImageResult> images) {
        if (images == null || images.isEmpty()) {
            return "";
        }
        return images.stream()
                .filter(img -> "MAIN".equals(img.imageType()))
                .map(LegacyImageResult::imageUrl)
                .findFirst()
                .orElse(images.getFirst().imageUrl());
    }

    private Set<LegacyProductFetchResponse> toProductFetchResponses(
            List<LegacyProductResult> products) {
        if (products == null || products.isEmpty()) {
            return Set.of();
        }
        return products.stream()
                .map(this::toProductFetchResponse)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LegacyProductFetchResponse toProductFetchResponse(LegacyProductResult product) {
        LegacyProductStatusResponse productStatus =
                LegacyProductStatusResponse.of(product.soldOut(), !product.soldOut());

        String optionString = buildOptionString(product.options());
        Set<LegacyOptionDto> options = buildOptionDtos(product.options());

        return new LegacyProductFetchResponse(
                product.productId(),
                product.stockQuantity(),
                productStatus,
                optionString,
                options,
                BigDecimal.ZERO);
    }

    private LegacyProductNoticeResponse toNoticeResponse(LegacyNoticeResult notice) {
        if (notice == null) {
            return null;
        }
        return new LegacyProductNoticeResponse(
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

    private List<LegacyProductImageResponse> toImageResponses(List<LegacyImageResult> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }
        return images.stream()
                .map(img -> new LegacyProductImageResponse(img.imageType(), img.imageUrl()))
                .toList();
    }

    private String buildOptionString(List<LegacyOptionMappingResult> mappings) {
        if (mappings == null || mappings.isEmpty()) {
            return "";
        }
        return mappings.stream()
                .map(m -> m.optionGroupName() + m.optionValue())
                .collect(Collectors.joining(" "));
    }

    private Set<LegacyOptionDto> buildOptionDtos(List<LegacyOptionMappingResult> mappings) {
        if (mappings == null || mappings.isEmpty()) {
            return Set.of();
        }
        return mappings.stream()
                .map(
                        m ->
                                new LegacyOptionDto(
                                        m.optionGroupId(),
                                        m.optionDetailId(),
                                        toLegacyOptionName(m.optionGroupName()),
                                        m.optionValue()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /** 한국어 옵션 그룹명 → 레거시 enum (SIZE, COLOR, DEFAULT_ONE, DEFAULT_TWO). */
    private String toLegacyOptionName(String optionGroupName) {
        if (optionGroupName == null) {
            return "DEFAULT_ONE";
        }
        return switch (optionGroupName) {
            case "사이즈", "SIZE", "size" -> "SIZE";
            case "컬러", "색상", "COLOR", "color" -> "COLOR";
            case "DEFAULT_ONE" -> "DEFAULT_ONE";
            case "DEFAULT_TWO" -> "DEFAULT_TWO";
            default -> "DEFAULT_ONE";
        };
    }
}
