package com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreatePriceRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductStockRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyOptionDto;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductFetchResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductStatusResponse;
import com.ryuqq.marketplace.application.legacy.product.dto.command.LegacyUpdatePriceCommand;
import com.ryuqq.marketplace.application.legacy.product.dto.command.LegacyUpdateStockCommand;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyOptionMappingResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyProductResult;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품(SKU) 수정 요청 DTO → 내부 Command 변환 매퍼.
 *
 * <p>가격, 재고, 품절 관련 변환과 상품 응답 변환을 담당합니다.
 */
@Component
public class LegacyProductCommandApiMapper {

    /** LegacyCreatePriceRequest → LegacyUpdatePriceCommand. */
    public LegacyUpdatePriceCommand toPriceCommand(
            long productGroupId, LegacyCreatePriceRequest request) {
        return new LegacyUpdatePriceCommand(
                productGroupId, request.regularPrice(), request.currentPrice());
    }

    /** setofProductGroupId + Request → LegacyUpdateStockCommand. */
    public LegacyUpdateStockCommand toLegacyUpdateStockCommand(
            long productGroupId, List<LegacyUpdateProductStockRequest> request) {
        List<LegacyUpdateStockCommand.StockEntry> entries =
                request == null || request.isEmpty()
                        ? List.of()
                        : request.stream()
                                .map(
                                        r ->
                                                new LegacyUpdateStockCommand.StockEntry(
                                                        r.productId(), r.productStockQuantity()))
                                .toList();
        return new LegacyUpdateStockCommand(productGroupId, entries);
    }

    /**
     * LegacyProductGroupDetailResult → Set<LegacyProductFetchResponse> (productId 역매핑 포함).
     *
     * <p>레거시 상세 조회 결과를 세토프 호환 응답으로 변환합니다. 내부 productId를 외부 productId로 역변환합니다.
     *
     * @param result 레거시 상품그룹 상세 결과
     * @param internalToExternalMap internal → external productId 매핑
     */
    public Set<LegacyProductFetchResponse> toProductFetchResponses(
            LegacyProductGroupDetailResult result, Map<Long, Long> internalToExternalMap) {
        if (result.products() == null || result.products().isEmpty()) {
            return Set.of();
        }
        return result.products().stream()
                .map(p -> toLegacyProductFetchResponse(p, internalToExternalMap))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LegacyProductFetchResponse toLegacyProductFetchResponse(
            LegacyProductResult product, Map<Long, Long> internalToExternalMap) {
        LegacyProductStatusResponse productStatus =
                LegacyProductStatusResponse.of(product.soldOut(), !product.soldOut());

        String optionString = buildLegacyOptionString(product.options());
        Set<LegacyOptionDto> options = buildLegacyOptionDtos(product.options());

        long responseProductId =
                internalToExternalMap.getOrDefault(product.productId(), product.productId());

        return new LegacyProductFetchResponse(
                responseProductId,
                product.stockQuantity(),
                productStatus,
                optionString,
                options,
                BigDecimal.ZERO);
    }

    private String buildLegacyOptionString(List<LegacyOptionMappingResult> mappings) {
        if (mappings == null || mappings.isEmpty()) {
            return "";
        }
        return mappings.stream()
                .map(m -> m.optionGroupName() + m.optionValue())
                .collect(Collectors.joining(" "));
    }

    private Set<LegacyOptionDto> buildLegacyOptionDtos(List<LegacyOptionMappingResult> mappings) {
        if (mappings == null || mappings.isEmpty()) {
            return Set.of();
        }
        return mappings.stream()
                .map(
                        m ->
                                new LegacyOptionDto(
                                        m.optionGroupId(),
                                        m.optionDetailId(),
                                        m.optionGroupName(),
                                        m.optionValue()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
