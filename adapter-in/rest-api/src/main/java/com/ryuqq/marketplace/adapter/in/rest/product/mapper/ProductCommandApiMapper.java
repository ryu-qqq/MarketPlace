package com.ryuqq.marketplace.adapter.in.rest.product.mapper;

import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.BatchChangeProductStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.BatchUpdateProductApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductPriceApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductStockApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductsApiRequest;
import com.ryuqq.marketplace.application.product.dto.command.BatchChangeProductStatusCommand;
import com.ryuqq.marketplace.application.product.dto.command.BatchUpdateProductCommand;
import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductPriceCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import org.springframework.stereotype.Component;

/**
 * ProductCommandApiMapper - 상품(SKU) Command API 변환 매퍼.
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-002: 양방향 변환 지원.
 *
 * <p>API-MAP-005: 순수 변환 로직만.
 *
 * <p>CQRS 분리: Command 전용 Mapper (QueryApiMapper와 분리).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ProductCommandApiMapper {

    /**
     * UpdateProductPriceApiRequest -> UpdateProductPriceCommand 변환.
     *
     * @param productId 상품 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateProductPriceCommand toCommand(
            Long productId, UpdateProductPriceApiRequest request) {
        return new UpdateProductPriceCommand(
                productId, request.regularPrice(), request.currentPrice());
    }

    /**
     * UpdateProductStockApiRequest -> UpdateProductStockCommand 변환.
     *
     * @param productId 상품 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateProductStockCommand toCommand(
            Long productId, UpdateProductStockApiRequest request) {
        return new UpdateProductStockCommand(productId, request.stockQuantity());
    }

    /**
     * BatchChangeProductStatusApiRequest -> BatchChangeProductStatusCommand 변환.
     *
     * @param sellerId 인증 컨텍스트에서 해석된 셀러 ID
     * @param productGroupId 상품 그룹 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public BatchChangeProductStatusCommand toCommand(
            Long sellerId, Long productGroupId, BatchChangeProductStatusApiRequest request) {
        return new BatchChangeProductStatusCommand(
                sellerId, productGroupId, request.productIds(), request.targetStatus());
    }

    /**
     * BatchUpdateProductApiRequest -> BatchUpdateProductCommand 변환.
     *
     * @param sellerId 인증 컨텍스트에서 해석된 셀러 ID
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public BatchUpdateProductCommand toCommand(
            Long sellerId, BatchUpdateProductApiRequest request) {
        return new BatchUpdateProductCommand(
                sellerId,
                request.items().stream()
                        .map(
                                e ->
                                        new BatchUpdateProductCommand.Entry(
                                                e.productId(),
                                                e.regularPrice(),
                                                e.currentPrice(),
                                                e.stockQuantity()))
                        .toList());
    }

    /**
     * UpdateProductsApiRequest -> UpdateProductsCommand 변환.
     *
     * @param productGroupId 상품 그룹 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateProductsCommand toCommand(Long productGroupId, UpdateProductsApiRequest request) {
        return new UpdateProductsCommand(
                productGroupId,
                request.optionGroups().stream()
                        .map(
                                g ->
                                        new UpdateProductsCommand.OptionGroupData(
                                                g.sellerOptionGroupId(),
                                                g.optionGroupName(),
                                                g.canonicalOptionGroupId(),
                                                g.inputType(),
                                                g.optionValues().stream()
                                                        .map(
                                                                v ->
                                                                        new UpdateProductsCommand
                                                                                .OptionValueData(
                                                                                v
                                                                                        .sellerOptionValueId(),
                                                                                v.optionValueName(),
                                                                                v
                                                                                        .canonicalOptionValueId(),
                                                                                v.sortOrder()))
                                                        .toList()))
                        .toList(),
                request.products().stream()
                        .map(
                                p ->
                                        new UpdateProductsCommand.ProductData(
                                                p.productId(),
                                                p.skuCode(),
                                                p.regularPrice(),
                                                p.currentPrice(),
                                                p.stockQuantity(),
                                                p.sortOrder(),
                                                p.selectedOptions().stream()
                                                        .map(
                                                                so ->
                                                                        new SelectedOption(
                                                                                so
                                                                                        .optionGroupName(),
                                                                                so
                                                                                        .optionValueName()))
                                                        .toList()))
                        .toList());
    }
}
