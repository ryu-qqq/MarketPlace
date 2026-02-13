package com.ryuqq.marketplace.adapter.in.rest.product.mapper;

import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.ChangeProductStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductPriceApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductStockApiRequest;
import com.ryuqq.marketplace.application.product.dto.command.ChangeProductStatusCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductPriceCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
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
                productId, request.regularPrice(), request.currentPrice(), request.salePrice());
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
     * ChangeProductStatusApiRequest -> ChangeProductStatusCommand 변환.
     *
     * @param productId 상품 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public ChangeProductStatusCommand toCommand(
            Long productId, ChangeProductStatusApiRequest request) {
        return new ChangeProductStatusCommand(productId, request.targetStatus());
    }
}
