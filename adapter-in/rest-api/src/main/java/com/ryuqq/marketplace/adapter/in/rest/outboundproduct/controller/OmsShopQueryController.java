package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.controller;

import static com.ryuqq.marketplace.adapter.in.rest.outboundproduct.OmsEndpoints.SHOPS;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query.SearchOmsShopsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsShopApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.mapper.OmsShopQueryApiMapper;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsShopSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.port.in.query.SearchOmsShopsByOffsetUseCase;
import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** OMS 쇼핑몰 조회 컨트롤러 (API 7). */
@RestController
@Tag(name = "OMS Shop Query", description = "OMS 쇼핑몰 조회 API")
public class OmsShopQueryController {

    private final SearchOmsShopsByOffsetUseCase searchOmsShopsByOffsetUseCase;
    private final OmsShopQueryApiMapper mapper;

    public OmsShopQueryController(
            SearchOmsShopsByOffsetUseCase searchOmsShopsByOffsetUseCase,
            OmsShopQueryApiMapper mapper) {
        this.searchOmsShopsByOffsetUseCase = searchOmsShopsByOffsetUseCase;
        this.mapper = mapper;
    }

    @GetMapping(SHOPS)
    @Operation(summary = "쇼핑몰 목록 조회")
    @PreAuthorize("@access.authenticated()")
    public ResponseEntity<ApiResponse<PageApiResponse<OmsShopApiResponse>>> searchShops(
            @ParameterObject @Valid SearchOmsShopsApiRequest request) {
        OmsShopSearchParams params = mapper.toSearchParams(request);
        ShopPageResult pageResult = searchOmsShopsByOffsetUseCase.execute(params);
        PageApiResponse<OmsShopApiResponse> response = mapper.toPageResponse(pageResult);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
