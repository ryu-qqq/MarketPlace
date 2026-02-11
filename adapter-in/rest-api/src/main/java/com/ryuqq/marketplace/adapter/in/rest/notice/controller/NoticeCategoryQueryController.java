package com.ryuqq.marketplace.adapter.in.rest.notice.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.notice.NoticeAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.notice.dto.query.SearchNoticeCategoriesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.notice.dto.response.NoticeCategoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.notice.mapper.NoticeCategoryQueryApiMapper;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryPageResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.application.notice.port.in.query.GetNoticeCategoryUseCase;
import com.ryuqq.marketplace.application.notice.port.in.query.SearchNoticeCategoryByOffsetUseCase;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 고시정보 카테고리 조회 API 컨트롤러. */
@RestController
@RequestMapping(NoticeAdminEndpoints.NOTICE_CATEGORIES)
public class NoticeCategoryQueryController {

    private final GetNoticeCategoryUseCase getNoticeCategoryUseCase;
    private final SearchNoticeCategoryByOffsetUseCase searchNoticeCategoryByOffsetUseCase;
    private final NoticeCategoryQueryApiMapper mapper;

    public NoticeCategoryQueryController(
            GetNoticeCategoryUseCase getNoticeCategoryUseCase,
            SearchNoticeCategoryByOffsetUseCase searchNoticeCategoryByOffsetUseCase,
            NoticeCategoryQueryApiMapper mapper) {
        this.getNoticeCategoryUseCase = getNoticeCategoryUseCase;
        this.searchNoticeCategoryByOffsetUseCase = searchNoticeCategoryByOffsetUseCase;
        this.mapper = mapper;
    }

    @RequirePermission(value = "notice-category:read", description = "고시정보 카테고리 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<NoticeCategoryApiResponse>>>
            searchNoticeCategoriesByOffset(
                    @ParameterObject @Valid SearchNoticeCategoriesApiRequest request) {
        NoticeCategoryPageResult pageResult =
                searchNoticeCategoryByOffsetUseCase.execute(mapper.toSearchParams(request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toPageResponse(pageResult)));
    }

    @RequirePermission(value = "notice-category:read", description = "카테고리 그룹별 고시정보 조회")
    @GetMapping(NoticeAdminEndpoints.CATEGORY_GROUP)
    public ResponseEntity<ApiResponse<NoticeCategoryApiResponse>> getNoticeCategoryByCategoryGroup(
            @PathVariable(NoticeAdminEndpoints.PATH_CATEGORY_GROUP) CategoryGroup categoryGroup) {
        NoticeCategoryResult result = getNoticeCategoryUseCase.execute(categoryGroup);
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }
}
