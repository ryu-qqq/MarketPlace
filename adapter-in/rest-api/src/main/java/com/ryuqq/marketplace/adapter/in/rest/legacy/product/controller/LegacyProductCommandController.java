package com.ryuqq.marketplace.adapter.in.rest.legacy.product.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.DETAIL_DESCRIPTION;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.GROUP_DISPLAY_YN;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.GROUP_STOCK;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.IMAGES;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.NOTICE;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.OPTION;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.OUT_STOCK;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.PRICE;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.PRODUCT_GROUP;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.PRODUCT_GROUP_ID;

import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateOptionRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreatePriceRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateProductImageRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateDisplayYnRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductDescriptionRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductStockRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyCreateProductGroupResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductFetchResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper.LegacyInboundApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper.LegacyProductCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper.LegacyProductCommandApiMapper.LegacyOptionConversionResult;
import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.ReceiveInboundProductUseCase;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductCommandUseCase;
import com.ryuqq.marketplace.application.legacyproduct.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 상품 등록/수정 API 컨트롤러.
 *
 * <p>기존 세토프 연동 호환을 위해 제공되는 레거시 엔드포인트입니다.
 *
 * <p>API-CTR-010: CQRS Controller 분리 (Command 전용).
 */
@Tag(
        name = "세토프 어드민용 레거시",
        description =
                "세토프 어드민용 레거시 엔드포인트. 기존 세토프 연동 호환을 위해 제공되며, 신규 개발 시에는 동일 기능의 일반 API 사용을 권장합니다.")
@RestController
public class LegacyProductCommandController {

    private static final long LEGACY_EXTERNAL_SOURCE_ID = 1L;

    private final ReceiveInboundProductUseCase receiveInboundProductUseCase;
    private final LegacyInboundApiMapper legacyInboundApiMapper;
    private final LegacyProductCommandUseCase legacyProductCommandUseCase;
    private final LegacyProductQueryUseCase legacyProductQueryUseCase;
    private final LegacyProductCommandApiMapper legacyProductCommandApiMapper;

    public LegacyProductCommandController(
            ReceiveInboundProductUseCase receiveInboundProductUseCase,
            LegacyInboundApiMapper legacyInboundApiMapper,
            LegacyProductCommandUseCase legacyProductCommandUseCase,
            LegacyProductQueryUseCase legacyProductQueryUseCase,
            LegacyProductCommandApiMapper legacyProductCommandApiMapper) {
        this.receiveInboundProductUseCase = receiveInboundProductUseCase;
        this.legacyInboundApiMapper = legacyInboundApiMapper;
        this.legacyProductCommandUseCase = legacyProductCommandUseCase;
        this.legacyProductQueryUseCase = legacyProductQueryUseCase;
        this.legacyProductCommandApiMapper = legacyProductCommandApiMapper;
    }

    // ===== 등록 =====

    @PostMapping(PRODUCT_GROUP)
    public ResponseEntity<LegacyApiResponse<LegacyCreateProductGroupResponse>> registerProductGroup(
            @Valid @RequestBody LegacyCreateProductGroupRequest request) {
        ReceiveInboundProductCommand command =
                legacyInboundApiMapper.toCommand(request, LEGACY_EXTERNAL_SOURCE_ID);
        InboundProductConversionResult result = receiveInboundProductUseCase.execute(command);

        long productGroupId =
                result.internalProductGroupId() != null
                        ? result.internalProductGroupId()
                        : request.productGroupId();

        ProductGroupDetailCompositeResult detail =
                legacyProductQueryUseCase.execute(productGroupId);
        LegacyCreateProductGroupResponse response =
                legacyProductCommandApiMapper.toCreateResponse(detail, request.sellerId());

        return ResponseEntity.ok(LegacyApiResponse.of(response));
    }

    // ===== 수정 =====

    @PutMapping(PRODUCT_GROUP_ID)
    public ResponseEntity<LegacyApiResponse<Long>> updateProductGroup(
            @PathVariable long productGroupId,
            @RequestBody LegacyUpdateProductGroupRequest request) {
        ProductGroupUpdateBundle bundle = legacyProductCommandApiMapper.toUpdateBundle(request);
        legacyProductCommandUseCase.updateFull(productGroupId, bundle);
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }

    @PutMapping(NOTICE)
    public ResponseEntity<LegacyApiResponse<Long>> updateProductNotice(
            @PathVariable long productGroupId,
            @RequestBody LegacyCreateProductNoticeRequest request) {
        UpdateProductNoticeCommand command =
                legacyProductCommandApiMapper.toNoticeCommand(0L, request);
        legacyProductCommandUseCase.updateNotice(productGroupId, command);
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }

    @PutMapping(IMAGES)
    public ResponseEntity<LegacyApiResponse<Long>> updateProductImages(
            @PathVariable long productGroupId,
            @RequestBody List<LegacyCreateProductImageRequest> request) {
        UpdateProductGroupImagesCommand command =
                legacyProductCommandApiMapper.toImagesCommand(0L, request);
        legacyProductCommandUseCase.updateImages(productGroupId, command);
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }

    @PutMapping(DETAIL_DESCRIPTION)
    public ResponseEntity<LegacyApiResponse<Long>> updateDetailDescription(
            @PathVariable long productGroupId,
            @RequestBody LegacyUpdateProductDescriptionRequest request) {
        UpdateProductGroupDescriptionCommand command =
                legacyProductCommandApiMapper.toDescriptionCommand(0L, request);
        legacyProductCommandUseCase.updateDescription(productGroupId, command);
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }

    @PutMapping(OPTION)
    public ResponseEntity<LegacyApiResponse<Set<LegacyProductFetchResponse>>> updateProductOption(
            @PathVariable long productGroupId,
            @RequestBody List<LegacyCreateOptionRequest> request) {
        LegacyOptionConversionResult conversionResult =
                legacyProductCommandApiMapper.toOptionCommands(0L, request);
        legacyProductCommandUseCase.updateOptions(
                productGroupId,
                conversionResult.optionGroupCommand(),
                conversionResult.productEntries(),
                conversionResult.optionGroupData());

        ProductGroupDetailCompositeResult detail =
                legacyProductQueryUseCase.execute(productGroupId);
        Set<LegacyProductFetchResponse> products =
                legacyProductCommandApiMapper.toProductFetchResponses(detail);
        return ResponseEntity.ok(LegacyApiResponse.of(products));
    }

    @PatchMapping(PRICE)
    public ResponseEntity<LegacyApiResponse<Long>> updatePrice(
            @PathVariable long productGroupId, @RequestBody LegacyCreatePriceRequest request) {
        legacyProductCommandUseCase.updatePrice(
                productGroupId, (int) request.regularPrice(), (int) request.currentPrice());
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }

    @PatchMapping(GROUP_DISPLAY_YN)
    public ResponseEntity<LegacyApiResponse<Long>> updateGroupDisplayYn(
            @PathVariable long productGroupId, @RequestBody LegacyUpdateDisplayYnRequest request) {
        legacyProductCommandUseCase.updateDisplayStatus(productGroupId, request.displayYn());
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }

    @PatchMapping(OUT_STOCK)
    public ResponseEntity<LegacyApiResponse<Set<LegacyProductFetchResponse>>> outOfStock(
            @PathVariable long productGroupId) {
        legacyProductCommandUseCase.markOutOfStock(productGroupId);

        ProductGroupDetailCompositeResult detail =
                legacyProductQueryUseCase.execute(productGroupId);
        Set<LegacyProductFetchResponse> products =
                legacyProductCommandApiMapper.toProductFetchResponses(detail);
        return ResponseEntity.ok(LegacyApiResponse.of(products));
    }

    @PatchMapping(GROUP_STOCK)
    public ResponseEntity<LegacyApiResponse<Set<LegacyProductFetchResponse>>> updateGroupStock(
            @PathVariable long productGroupId,
            @RequestBody List<LegacyUpdateProductStockRequest> request) {
        List<UpdateProductStockCommand> commands =
                legacyProductCommandApiMapper.toStockCommands(request);
        legacyProductCommandUseCase.updateStock(commands);

        ProductGroupDetailCompositeResult detail =
                legacyProductQueryUseCase.execute(productGroupId);
        Set<LegacyProductFetchResponse> products =
                legacyProductCommandApiMapper.toProductFetchResponses(detail);
        return ResponseEntity.ok(LegacyApiResponse.of(products));
    }
}
