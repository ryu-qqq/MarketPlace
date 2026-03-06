package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.controller;

import static com.ryuqq.marketplace.adapter.in.rest.outboundproduct.OmsEndpoints.PATH_PRODUCT_GROUP_ID;
import static com.ryuqq.marketplace.adapter.in.rest.outboundproduct.OmsEndpoints.PRODUCTS;
import static com.ryuqq.marketplace.adapter.in.rest.outboundproduct.OmsEndpoints.PRODUCT_GROUP_ID;
import static com.ryuqq.marketplace.adapter.in.rest.outboundproduct.OmsEndpoints.SYNC_HISTORY;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query.SearchOmsProductsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query.SearchSyncHistoryApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.SyncHistoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.mapper.OmsProductQueryApiMapper;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsProductSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.SyncHistorySearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductDetailResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductPageResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryPageResult;
import com.ryuqq.marketplace.application.outboundproduct.port.in.query.GetOmsProductDetailUseCase;
import com.ryuqq.marketplace.application.outboundproduct.port.in.query.SearchOmsProductUseCase;
import com.ryuqq.marketplace.application.outboundproduct.port.in.query.SearchSyncHistoryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** OMS 상품 조회 컨트롤러 (API 1, 2, 3). */
@RestController
@Tag(name = "OMS Product Query", description = "OMS 상품 조회 API")
public class OmsProductQueryController {

    private final SearchOmsProductUseCase searchOmsProductUseCase;
    private final GetOmsProductDetailUseCase getOmsProductDetailUseCase;
    private final SearchSyncHistoryUseCase searchSyncHistoryUseCase;
    private final OmsProductQueryApiMapper mapper;

    public OmsProductQueryController(
            SearchOmsProductUseCase searchOmsProductUseCase,
            GetOmsProductDetailUseCase getOmsProductDetailUseCase,
            SearchSyncHistoryUseCase searchSyncHistoryUseCase,
            OmsProductQueryApiMapper mapper) {
        this.searchOmsProductUseCase = searchOmsProductUseCase;
        this.getOmsProductDetailUseCase = getOmsProductDetailUseCase;
        this.searchSyncHistoryUseCase = searchSyncHistoryUseCase;
        this.mapper = mapper;
    }

    @GetMapping(PRODUCTS)
    @Operation(summary = "OMS 상품 목록 조회")
    @PreAuthorize("@access.authenticated()")
    public ResponseEntity<ApiResponse<PageApiResponse<OmsProductApiResponse>>> searchProducts(
            @ParameterObject @Valid SearchOmsProductsApiRequest request) {
        OmsProductSearchParams params = mapper.toSearchParams(request);
        OmsProductPageResult pageResult = searchOmsProductUseCase.execute(params);
        PageApiResponse<OmsProductApiResponse> response = mapper.toProductPageResponse(pageResult);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping(PRODUCTS + PRODUCT_GROUP_ID)
    @Operation(summary = "OMS 상품 상세 조회")
    @PreAuthorize("@access.authenticated()")
    public ResponseEntity<ApiResponse<OmsProductDetailApiResponse>> getProductDetail(
            @Parameter(description = "상품그룹 ID") @PathVariable(PATH_PRODUCT_GROUP_ID)
                    long productGroupId) {
        OmsProductDetailResult result = getOmsProductDetailUseCase.execute(productGroupId);
        OmsProductDetailApiResponse response = mapper.toDetailResponse(result);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @GetMapping(SYNC_HISTORY)
    @Operation(summary = "상품별 연동 이력 조회")
    @PreAuthorize("@access.authenticated()")
    public ResponseEntity<ApiResponse<PageApiResponse<SyncHistoryApiResponse>>> searchSyncHistory(
            @Parameter(description = "상품그룹 ID") @PathVariable(PATH_PRODUCT_GROUP_ID)
                    long productGroupId,
            @ParameterObject @Valid SearchSyncHistoryApiRequest request) {
        SyncHistorySearchParams params = mapper.toSyncHistoryParams(productGroupId, request);
        SyncHistoryPageResult pageResult = searchSyncHistoryUseCase.execute(params);
        return ResponseEntity.ok(ApiResponse.of(mapper.toSyncHistoryPageResponse(pageResult)));
    }
}
