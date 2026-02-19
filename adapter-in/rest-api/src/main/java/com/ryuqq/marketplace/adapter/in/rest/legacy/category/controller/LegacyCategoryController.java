package com.ryuqq.marketplace.adapter.in.rest.legacy.category.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.category.LegacyCategoryEndpoints.CATEGORY;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.category.LegacyCategoryEndpoints.CATEGORY_EXTERNAL_MAPPING;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.category.LegacyCategoryEndpoints.CATEGORY_ID;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.category.LegacyCategoryEndpoints.CATEGORY_PAGE;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.category.LegacyCategoryEndpoints.CATEGORY_PARENT;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.category.LegacyCategoryEndpoints.CATEGORY_PARENTS;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.category.dto.request.LegacyCategoryMappingInfoRequest;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 세토프 레거시 카테고리 API 호환 컨트롤러. */
@RestController
public class LegacyCategoryController {

    @GetMapping(CATEGORY_ID)
    public ResponseEntity<ApiResponse<Object>> fetchChildCategories(@PathVariable long categoryId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(CATEGORY_PARENT)
    public ResponseEntity<ApiResponse<Object>> fetchParentCategories(
            @PathVariable long categoryId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(CATEGORY_PARENTS)
    public ResponseEntity<ApiResponse<Object>> fetchCategoriesByIds(
            @RequestParam Set<Long> categoryIds) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(CATEGORY)
    public ResponseEntity<ApiResponse<Object>> fetchAllCategories() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(CATEGORY_PAGE)
    public ResponseEntity<ApiResponse<Object>> fetchCategoriesPage(Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping(CATEGORY_EXTERNAL_MAPPING)
    public ResponseEntity<ApiResponse<List<LegacyCategoryMappingInfoRequest>>>
            convertExternalCategoryToInternal(
                    @PathVariable long siteId,
                    @RequestBody List<LegacyCategoryMappingInfoRequest> categoryMappingInfos) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
