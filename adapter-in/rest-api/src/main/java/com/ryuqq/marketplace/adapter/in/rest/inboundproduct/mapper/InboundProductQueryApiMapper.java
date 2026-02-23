package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.mapper;

import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.response.InboundProductDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.response.InboundProductDetailApiResponse.OptionItemApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.response.InboundProductDetailApiResponse.ProductItemApiResponse;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductDetailResult;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductDetailResult.OptionItem;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductDetailResult.ProductItem;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 인바운드 상품 조회 API 매퍼.
 *
 * <p>Application 레이어 Result DTO를 Adapter-In API 응답 DTO로 변환합니다.
 */
@Component
public class InboundProductQueryApiMapper {

    public InboundProductDetailApiResponse toResponse(InboundProductDetailResult result) {
        List<ProductItemApiResponse> products =
                result.products() != null
                        ? result.products().stream().map(this::toProductItem).toList()
                        : List.of();
        return new InboundProductDetailApiResponse(
                result.status(),
                result.externalProductCode(),
                result.internalProductGroupId(),
                products);
    }

    private ProductItemApiResponse toProductItem(ProductItem item) {
        List<OptionItemApiResponse> options =
                item.options() != null
                        ? item.options().stream().map(this::toOptionItem).toList()
                        : List.of();
        return new ProductItemApiResponse(
                item.productId(),
                item.skuCode(),
                item.regularPrice(),
                item.currentPrice(),
                item.stockQuantity(),
                item.sortOrder(),
                options);
    }

    private OptionItemApiResponse toOptionItem(OptionItem option) {
        return new OptionItemApiResponse(option.optionGroupName(), option.optionValueName());
    }
}
