package com.ryuqq.marketplace.adapter.in.rest.shop.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shop.ShopAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.shop.dto.query.SearchShopsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shop.dto.response.ShopApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shop.mapper.ShopQueryApiMapper;
import com.ryuqq.marketplace.application.shop.dto.query.ShopSearchParams;
import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;
import com.ryuqq.marketplace.application.shop.port.in.query.SearchShopByOffsetUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Shop 조회 Controller. */
@Tag(name = "외부몰 조회", description = "외부몰 조회 API")
@RestController
@RequestMapping(ShopAdminEndpoints.SHOPS)
public class ShopQueryController {

    private final SearchShopByOffsetUseCase searchShopByOffsetUseCase;
    private final ShopQueryApiMapper mapper;

    public ShopQueryController(
            SearchShopByOffsetUseCase searchShopByOffsetUseCase, ShopQueryApiMapper mapper) {
        this.searchShopByOffsetUseCase = searchShopByOffsetUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "외부몰 목록 조회", description = "외부몰 목록을 복합 조건으로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<ShopApiResponse>>> searchShops(
            @ParameterObject @Valid SearchShopsApiRequest request) {

        ShopSearchParams params = mapper.toSearchParams(request);
        ShopPageResult pageResult = searchShopByOffsetUseCase.execute(params);
        PageApiResponse<ShopApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
