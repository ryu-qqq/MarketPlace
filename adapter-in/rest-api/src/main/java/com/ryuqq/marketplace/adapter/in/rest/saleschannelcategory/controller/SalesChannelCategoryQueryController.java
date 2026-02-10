package com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.SalesChannelCategoryAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.query.SearchSalesChannelCategoriesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.response.SalesChannelCategoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.mapper.SalesChannelCategoryQueryApiMapper;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.query.SalesChannelCategorySearchParams;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.SalesChannelCategoryPageResult;
import com.ryuqq.marketplace.application.saleschannelcategory.port.in.query.SearchSalesChannelCategoryByOffsetUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 외부채널 카테고리 조회 Controller. */
@Tag(name = "외부채널 카테고리 조회", description = "외부채널 카테고리 조회 API")
@RestController
@RequestMapping(SalesChannelCategoryAdminEndpoints.CATEGORIES)
public class SalesChannelCategoryQueryController {

    private final SearchSalesChannelCategoryByOffsetUseCase searchUseCase;
    private final SalesChannelCategoryQueryApiMapper mapper;

    public SalesChannelCategoryQueryController(
            SearchSalesChannelCategoryByOffsetUseCase searchUseCase,
            SalesChannelCategoryQueryApiMapper mapper) {
        this.searchUseCase = searchUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "외부채널 카테고리 목록 조회", description = "외부채널 카테고리 목록을 복합 조건으로 조회합니다.")
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "sales-channel-category:read", description = "외부채널 카테고리 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<SalesChannelCategoryApiResponse>>>
            searchCategories(
                    @Parameter(description = "판매채널 ID", required = true)
                            @PathVariable(SalesChannelCategoryAdminEndpoints.PATH_SALES_CHANNEL_ID)
                            Long salesChannelId,
                    @ParameterObject @Valid SearchSalesChannelCategoriesApiRequest request) {

        SalesChannelCategorySearchParams params =
                mapper.toSearchParams(List.of(salesChannelId), request);
        SalesChannelCategoryPageResult pageResult = searchUseCase.execute(params);
        PageApiResponse<SalesChannelCategoryApiResponse> response =
                mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
