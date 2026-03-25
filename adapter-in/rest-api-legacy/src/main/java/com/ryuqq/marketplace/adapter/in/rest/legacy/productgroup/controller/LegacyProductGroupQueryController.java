package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.LegacyProductGroupEndpoints.PRODUCTS_GROUP;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.LegacyProductGroupEndpoints.PRODUCT_GROUP_ID;

import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyCustomPageable;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.security.LegacyAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.mapper.LegacyProductGroupQueryApiMapper;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.query.LegacyProductGroupSearchParams;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.response.LegacyProductGroupPageResult;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.LegacySearchProductGroupByOffsetUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 상품그룹 조회 API 컨트롤러.
 *
 * <p>상세 조회 + 목록 조회 (세토프 레거시 호환).
 */
@Tag(name = "세토프 어드민용 레거시 - 상품그룹", description = "세토프 어드민용 레거시 상품그룹 엔드포인트.")
@RestController
public class LegacyProductGroupQueryController {

    private final LegacyProductQueryUseCase legacyProductQueryUseCase;
    private final LegacySearchProductGroupByOffsetUseCase legacySearchProductGroupByOffsetUseCase;
    private final LegacyProductGroupQueryApiMapper legacyProductGroupQueryApiMapper;
    private final LegacyAccessChecker legacyAccessChecker;

    public LegacyProductGroupQueryController(
            LegacyProductQueryUseCase legacyProductQueryUseCase,
            LegacySearchProductGroupByOffsetUseCase legacySearchProductGroupByOffsetUseCase,
            LegacyProductGroupQueryApiMapper legacyProductGroupQueryApiMapper,
            LegacyAccessChecker legacyAccessChecker) {
        this.legacyProductQueryUseCase = legacyProductQueryUseCase;
        this.legacySearchProductGroupByOffsetUseCase = legacySearchProductGroupByOffsetUseCase;
        this.legacyProductGroupQueryApiMapper = legacyProductGroupQueryApiMapper;
        this.legacyAccessChecker = legacyAccessChecker;
    }

    @Operation(summary = "레거시 상품그룹 상세 조회")
    @PreAuthorize("@legacyAccess.isProductOwnerOrMaster(#productGroupId)")
    @GetMapping(PRODUCT_GROUP_ID)
    public ResponseEntity<LegacyApiResponse<LegacyProductDetailApiResponse>> getProductGroup(
            @Parameter(description = "조회할 상품그룹 ID") @PathVariable long productGroupId) {
        LegacyProductGroupDetailResult result = legacyProductQueryUseCase.execute(productGroupId);
        LegacyProductDetailApiResponse response =
                legacyProductGroupQueryApiMapper.toResponse(result);
        return ResponseEntity.ok(LegacyApiResponse.of(response));
    }

    @Operation(summary = "레거시 상품그룹 목록 조회")
    @PreAuthorize("@legacyAccess.authenticated()")
    @GetMapping(PRODUCTS_GROUP)
    public ResponseEntity<LegacyApiResponse<LegacyCustomPageable<LegacyProductDetailApiResponse>>>
            getProductGroups(
                    @RequestParam(required = false) Long brandId,
                    @RequestParam(required = false) Long categoryId,
                    @RequestParam(required = false) String managementType,
                    @RequestParam(required = false) String soldOutYn,
                    @RequestParam(required = false) String displayYn,
                    @RequestParam(required = false) Long minSalePrice,
                    @RequestParam(required = false) Long maxSalePrice,
                    @RequestParam(required = false) Long minDiscountRate,
                    @RequestParam(required = false) Long maxDiscountRate,
                    @RequestParam(required = false) String searchKeyword,
                    @RequestParam(required = false) String searchWord,
                    @RequestParam(required = false) LocalDateTime startDate,
                    @RequestParam(required = false) LocalDateTime endDate,
                    @RequestParam(defaultValue = "0") int page,
                    @RequestParam(defaultValue = "20") int size) {

        Long effectiveSellerId = legacyAccessChecker.resolveSellerIdOrNull();

        LegacyProductGroupSearchParams params =
                LegacyProductGroupSearchParams.of(
                        effectiveSellerId,
                        brandId,
                        categoryId,
                        managementType,
                        soldOutYn,
                        displayYn,
                        minSalePrice,
                        maxSalePrice,
                        minDiscountRate,
                        maxDiscountRate,
                        searchKeyword,
                        searchWord,
                        startDate,
                        endDate,
                        page,
                        size);

        LegacyProductGroupPageResult pageResult =
                legacySearchProductGroupByOffsetUseCase.execute(params);

        List<LegacyProductDetailApiResponse> responses =
                pageResult.items().stream()
                        .map(legacyProductGroupQueryApiMapper::toResponse)
                        .toList();

        Pageable pageable = PageRequest.of(page, size);
        LegacyCustomPageable<LegacyProductDetailApiResponse> customPage =
                new LegacyCustomPageable<>(responses, pageable, pageResult.totalElements(), null);

        return ResponseEntity.ok(LegacyApiResponse.success(customPage));
    }
}
