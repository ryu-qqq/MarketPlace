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
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper.LegacyImageCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper.LegacyInboundApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper.LegacyNoticeCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper.LegacyOptionCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper.LegacyProductCommandApiMapper;
import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyRegisterProductGroupCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateNoticeCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateProductsCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.response.LegacyProductRegistrationResult;
import com.ryuqq.marketplace.application.legacyproduct.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductGroupFullRegisterUseCase;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductGroupFullUpdateUseCase;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductMarkOutOfStockUseCase;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateDescriptionUseCase;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateDisplayStatusUseCase;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateImagesUseCase;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateNoticeUseCase;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateOptionsUseCase;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdatePriceUseCase;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateStockUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
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
 * <p>등록은 InboundProduct 파이프라인을 통해, 수정은 개별 UseCase를 통해 PK 변환 후 기존 내부 Coordinator에 위임합니다.
 */
@Tag(
        name = "세토프 어드민용 레거시",
        description =
                "세토프 어드민용 레거시 엔드포인트. 기존 세토프 연동 호환을 위해 제공되며, 신규 개발 시에는 동일 기능의 일반 API 사용을 권장합니다.")
@RestController
public class LegacyProductCommandController {

    private static final long LEGACY_EXTERNAL_SOURCE_ID = 1L;

    private final LegacyProductGroupFullRegisterUseCase legacyProductGroupFullRegisterUseCase;
    private final LegacyProductGroupFullUpdateUseCase legacyProductGroupFullUpdateUseCase;
    private final LegacyProductUpdateImagesUseCase legacyProductUpdateImagesUseCase;
    private final LegacyProductUpdateDescriptionUseCase legacyProductUpdateDescriptionUseCase;
    private final LegacyProductUpdatePriceUseCase legacyProductUpdatePriceUseCase;
    private final LegacyProductUpdateDisplayStatusUseCase legacyProductUpdateDisplayStatusUseCase;
    private final LegacyProductUpdateNoticeUseCase legacyProductUpdateNoticeUseCase;
    private final LegacyProductUpdateOptionsUseCase legacyProductUpdateOptionsUseCase;
    private final LegacyProductMarkOutOfStockUseCase legacyProductMarkOutOfStockUseCase;
    private final LegacyProductUpdateStockUseCase legacyProductUpdateStockUseCase;
    private final LegacyInboundApiMapper legacyInboundApiMapper;
    private final LegacyProductCommandApiMapper legacyProductCommandApiMapper;
    private final LegacyNoticeCommandApiMapper legacyNoticeCommandApiMapper;
    private final LegacyImageCommandApiMapper legacyImageCommandApiMapper;
    private final LegacyOptionCommandApiMapper legacyOptionCommandApiMapper;

    public LegacyProductCommandController(
            LegacyProductGroupFullRegisterUseCase legacyProductGroupFullRegisterUseCase,
            LegacyProductGroupFullUpdateUseCase legacyProductGroupFullUpdateUseCase,
            LegacyProductUpdateImagesUseCase legacyProductUpdateImagesUseCase,
            LegacyProductUpdateDescriptionUseCase legacyProductUpdateDescriptionUseCase,
            LegacyProductUpdatePriceUseCase legacyProductUpdatePriceUseCase,
            LegacyProductUpdateDisplayStatusUseCase legacyProductUpdateDisplayStatusUseCase,
            LegacyProductUpdateNoticeUseCase legacyProductUpdateNoticeUseCase,
            LegacyProductUpdateOptionsUseCase legacyProductUpdateOptionsUseCase,
            LegacyProductMarkOutOfStockUseCase legacyProductMarkOutOfStockUseCase,
            LegacyProductUpdateStockUseCase legacyProductUpdateStockUseCase,
            LegacyInboundApiMapper legacyInboundApiMapper,
            LegacyProductCommandApiMapper legacyProductCommandApiMapper,
            LegacyNoticeCommandApiMapper legacyNoticeCommandApiMapper,
            LegacyImageCommandApiMapper legacyImageCommandApiMapper,
            LegacyOptionCommandApiMapper legacyOptionCommandApiMapper) {
        this.legacyProductGroupFullRegisterUseCase = legacyProductGroupFullRegisterUseCase;
        this.legacyProductGroupFullUpdateUseCase = legacyProductGroupFullUpdateUseCase;
        this.legacyProductUpdateImagesUseCase = legacyProductUpdateImagesUseCase;
        this.legacyProductUpdateDescriptionUseCase = legacyProductUpdateDescriptionUseCase;
        this.legacyProductUpdatePriceUseCase = legacyProductUpdatePriceUseCase;
        this.legacyProductUpdateDisplayStatusUseCase = legacyProductUpdateDisplayStatusUseCase;
        this.legacyProductUpdateNoticeUseCase = legacyProductUpdateNoticeUseCase;
        this.legacyProductUpdateOptionsUseCase = legacyProductUpdateOptionsUseCase;
        this.legacyProductMarkOutOfStockUseCase = legacyProductMarkOutOfStockUseCase;
        this.legacyProductUpdateStockUseCase = legacyProductUpdateStockUseCase;
        this.legacyInboundApiMapper = legacyInboundApiMapper;
        this.legacyProductCommandApiMapper = legacyProductCommandApiMapper;
        this.legacyNoticeCommandApiMapper = legacyNoticeCommandApiMapper;
        this.legacyImageCommandApiMapper = legacyImageCommandApiMapper;
        this.legacyOptionCommandApiMapper = legacyOptionCommandApiMapper;
    }

    // ===== 등록 =====

    @PostMapping(PRODUCT_GROUP)
    public ResponseEntity<LegacyApiResponse<LegacyCreateProductGroupResponse>>
            registerProductGroupFull(@Valid @RequestBody LegacyCreateProductGroupRequest request) {
        LegacyRegisterProductGroupCommand command = legacyInboundApiMapper.toCommand(request);
        LegacyProductRegistrationResult result =
                legacyProductGroupFullRegisterUseCase.execute(command);
        LegacyCreateProductGroupResponse response =
                legacyProductCommandApiMapper.toCreateResponse(result);

        return ResponseEntity.ok(LegacyApiResponse.of(response));
    }

    // ===== 수정 =====

    @PutMapping(PRODUCT_GROUP_ID)
    public ResponseEntity<LegacyApiResponse<Long>> updateProductGroupFull(
            @PathVariable long productGroupId,
            @Valid @RequestBody LegacyUpdateProductGroupRequest request) {
        ReceiveInboundProductCommand command =
                legacyInboundApiMapper.toUpdateCommand(
                        request, LEGACY_EXTERNAL_SOURCE_ID, productGroupId);
        legacyProductGroupFullUpdateUseCase.execute(command);
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }

    @PutMapping(NOTICE)
    public ResponseEntity<LegacyApiResponse<Long>> updateProductNotice(
            @PathVariable long productGroupId,
            @Valid @RequestBody LegacyCreateProductNoticeRequest request) {
        LegacyUpdateNoticeCommand command =
                legacyNoticeCommandApiMapper.toLegacyNoticeCommand(productGroupId, request);
        legacyProductUpdateNoticeUseCase.execute(command);
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }

    @PutMapping(IMAGES)
    public ResponseEntity<LegacyApiResponse<Long>> updateProductImages(
            @PathVariable long productGroupId,
            @Valid @RequestBody List<LegacyCreateProductImageRequest> request) {
        legacyProductUpdateImagesUseCase.execute(
                legacyImageCommandApiMapper.toLegacyUpdateImagesCommand(productGroupId, request));
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }

    @PutMapping(DETAIL_DESCRIPTION)
    public ResponseEntity<LegacyApiResponse<Long>> updateDetailDescription(
            @PathVariable long productGroupId,
            @Valid @RequestBody LegacyUpdateProductDescriptionRequest request) {
        legacyProductUpdateDescriptionUseCase.execute(
                legacyImageCommandApiMapper.toLegacyUpdateDescriptionCommand(
                        productGroupId, request));
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }

    @PutMapping(OPTION)
    public ResponseEntity<LegacyApiResponse<Set<LegacyProductFetchResponse>>> updateProductOption(
            @PathVariable long productGroupId,
            @Valid @RequestBody List<LegacyCreateOptionRequest> request) {
        LegacyUpdateProductsCommand command =
                legacyOptionCommandApiMapper.toLegacyUpdateProductsCommand(productGroupId, request);
        LegacyProductGroupDetailResult result = legacyProductUpdateOptionsUseCase.execute(command);

        Set<LegacyProductFetchResponse> products =
                legacyProductCommandApiMapper.toProductFetchResponses(result, Map.of());
        return ResponseEntity.ok(LegacyApiResponse.of(products));
    }

    @PatchMapping(PRICE)
    public ResponseEntity<LegacyApiResponse<Long>> updatePrice(
            @PathVariable long productGroupId,
            @Valid @RequestBody LegacyCreatePriceRequest request) {
        legacyProductUpdatePriceUseCase.execute(
                legacyProductCommandApiMapper.toPriceCommand(productGroupId, request));
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }

    @PatchMapping(GROUP_DISPLAY_YN)
    public ResponseEntity<LegacyApiResponse<Long>> updateGroupDisplayYn(
            @PathVariable long productGroupId,
            @Valid @RequestBody LegacyUpdateDisplayYnRequest request) {
        legacyProductUpdateDisplayStatusUseCase.execute(
                legacyProductCommandApiMapper.toDisplayStatusCommand(productGroupId, request));
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }

    @PatchMapping(OUT_STOCK)
    public ResponseEntity<LegacyApiResponse<Set<LegacyProductFetchResponse>>> outOfStock(
            @PathVariable long productGroupId) {
        LegacyProductGroupDetailResult result =
                legacyProductMarkOutOfStockUseCase.execute(
                        legacyProductCommandApiMapper.toLegacyMarkOutOfStockCommand(
                                productGroupId));
        Set<LegacyProductFetchResponse> products =
                legacyProductCommandApiMapper.toProductFetchResponses(result, Map.of());
        return ResponseEntity.ok(LegacyApiResponse.of(products));
    }

    @PatchMapping(GROUP_STOCK)
    public ResponseEntity<LegacyApiResponse<Set<LegacyProductFetchResponse>>> updateGroupStock(
            @PathVariable long productGroupId,
            @RequestBody List<LegacyUpdateProductStockRequest> request) {
        LegacyProductGroupDetailResult result =
                legacyProductUpdateStockUseCase.execute(
                        legacyProductCommandApiMapper.toLegacyUpdateStockCommand(
                                productGroupId, request));
        Set<LegacyProductFetchResponse> products =
                legacyProductCommandApiMapper.toProductFetchResponses(result, Map.of());
        return ResponseEntity.ok(LegacyApiResponse.of(products));
    }
}
