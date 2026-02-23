package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.controller;

import static com.ryuqq.marketplace.adapter.in.rest.inboundproduct.InboundProductAdminEndpoints.DESCRIPTION;
import static com.ryuqq.marketplace.adapter.in.rest.inboundproduct.InboundProductAdminEndpoints.IMAGES;
import static com.ryuqq.marketplace.adapter.in.rest.inboundproduct.InboundProductAdminEndpoints.INBOUND_PRODUCTS;
import static com.ryuqq.marketplace.adapter.in.rest.inboundproduct.InboundProductAdminEndpoints.PATH_EXTERNAL_PRODUCT_CODE;
import static com.ryuqq.marketplace.adapter.in.rest.inboundproduct.InboundProductAdminEndpoints.PATH_EXTERNAL_SOURCE_ID;
import static com.ryuqq.marketplace.adapter.in.rest.inboundproduct.InboundProductAdminEndpoints.PRICE;
import static com.ryuqq.marketplace.adapter.in.rest.inboundproduct.InboundProductAdminEndpoints.PRODUCTS;
import static com.ryuqq.marketplace.adapter.in.rest.inboundproduct.InboundProductAdminEndpoints.STOCK;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.ReceiveInboundProductApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductDescriptionApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductImagesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductPriceApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductStockApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.response.InboundProductConversionApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.mapper.InboundProductCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.mapper.ProductCommandApiMapper;
import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult;
import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductIdResolver;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.ReceiveInboundProductUseCase;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.UpdateInboundProductDescriptionUseCase;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.UpdateInboundProductImagesUseCase;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.UpdateInboundProductPriceUseCase;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.UpdateInboundProductStockUseCase;
import com.ryuqq.marketplace.application.product.port.in.command.UpdateProductsUseCase;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** 인바운드 상품 수신 및 부분 수정 컨트롤러 (크롤링 등 외부 소스용). */
@RestController
@Tag(name = "InboundProduct Command", description = "인바운드 상품 수신/수정 API")
public class InboundProductCommandController {

    private final ReceiveInboundProductUseCase receiveUseCase;
    private final UpdateInboundProductPriceUseCase updatePriceUseCase;
    private final UpdateInboundProductStockUseCase updateStockUseCase;
    private final UpdateInboundProductImagesUseCase updateImagesUseCase;
    private final UpdateInboundProductDescriptionUseCase updateDescriptionUseCase;
    private final UpdateProductsUseCase updateProductsUseCase;
    private final InboundProductIdResolver idResolver;
    private final InboundProductCommandApiMapper apiMapper;
    private final ProductCommandApiMapper productCommandApiMapper;

    public InboundProductCommandController(
            ReceiveInboundProductUseCase receiveUseCase,
            UpdateInboundProductPriceUseCase updatePriceUseCase,
            UpdateInboundProductStockUseCase updateStockUseCase,
            UpdateInboundProductImagesUseCase updateImagesUseCase,
            UpdateInboundProductDescriptionUseCase updateDescriptionUseCase,
            UpdateProductsUseCase updateProductsUseCase,
            InboundProductIdResolver idResolver,
            InboundProductCommandApiMapper apiMapper,
            ProductCommandApiMapper productCommandApiMapper) {
        this.receiveUseCase = receiveUseCase;
        this.updatePriceUseCase = updatePriceUseCase;
        this.updateStockUseCase = updateStockUseCase;
        this.updateImagesUseCase = updateImagesUseCase;
        this.updateDescriptionUseCase = updateDescriptionUseCase;
        this.updateProductsUseCase = updateProductsUseCase;
        this.idResolver = idResolver;
        this.apiMapper = apiMapper;
        this.productCommandApiMapper = productCommandApiMapper;
    }

    @PostMapping(INBOUND_PRODUCTS)
    public ResponseEntity<ApiResponse<InboundProductConversionApiResponse>> receiveInboundProduct(
            @RequestBody ReceiveInboundProductApiRequest request) {
        ReceiveInboundProductCommand command = apiMapper.toCommand(request);
        InboundProductConversionResult result = receiveUseCase.execute(command);
        return ResponseEntity.ok(ApiResponse.of(apiMapper.toResponse(result)));
    }

    @PatchMapping(PRICE)
    public ResponseEntity<Void> updatePrice(
            @PathVariable(PATH_EXTERNAL_SOURCE_ID) long inboundSourceId,
            @PathVariable(PATH_EXTERNAL_PRODUCT_CODE) String externalProductCode,
            @Valid @RequestBody UpdateInboundProductPriceApiRequest request) {
        updatePriceUseCase.execute(
                inboundSourceId,
                externalProductCode,
                request.regularPrice(),
                request.currentPrice());
        return ResponseEntity.ok().build();
    }

    @PatchMapping(STOCK)
    public ResponseEntity<Void> updateStock(
            @PathVariable(PATH_EXTERNAL_SOURCE_ID) long inboundSourceId,
            @PathVariable(PATH_EXTERNAL_PRODUCT_CODE) String externalProductCode,
            @Valid @RequestBody UpdateInboundProductStockApiRequest request) {
        updateStockUseCase.execute(
                inboundSourceId, externalProductCode, apiMapper.toStockCommands(request));
        return ResponseEntity.ok().build();
    }

    @PatchMapping(IMAGES)
    public ResponseEntity<Void> updateImages(
            @PathVariable(PATH_EXTERNAL_SOURCE_ID) long inboundSourceId,
            @PathVariable(PATH_EXTERNAL_PRODUCT_CODE) String externalProductCode,
            @Valid @RequestBody UpdateInboundProductImagesApiRequest request) {
        updateImagesUseCase.execute(
                inboundSourceId, externalProductCode, apiMapper.toImagesCommand(request));
        return ResponseEntity.ok().build();
    }

    @PatchMapping(DESCRIPTION)
    public ResponseEntity<Void> updateDescription(
            @PathVariable(PATH_EXTERNAL_SOURCE_ID) long inboundSourceId,
            @PathVariable(PATH_EXTERNAL_PRODUCT_CODE) String externalProductCode,
            @Valid @RequestBody UpdateInboundProductDescriptionApiRequest request) {
        updateDescriptionUseCase.execute(inboundSourceId, externalProductCode, request.content());
        return ResponseEntity.ok().build();
    }

    @PatchMapping(PRODUCTS)
    @Operation(
            summary = "상품 + 옵션 일괄 수정",
            description = "인바운드 상품의 내부 상품 그룹 하위 상품들의 옵션/가격/재고/SKU/정렬을 일괄 수정합니다.")
    public ResponseEntity<Void> updateProducts(
            @Parameter(description = "인바운드 소스 ID") @PathVariable(PATH_EXTERNAL_SOURCE_ID)
                    long inboundSourceId,
            @Parameter(description = "외부 상품 코드") @PathVariable(PATH_EXTERNAL_PRODUCT_CODE)
                    String externalProductCode,
            @Valid @RequestBody UpdateProductsApiRequest request) {
        ProductGroupId productGroupId = idResolver.resolve(inboundSourceId, externalProductCode);
        updateProductsUseCase.execute(
                productCommandApiMapper.toCommand(productGroupId.value(), request));
        return ResponseEntity.noContent().build();
    }
}
