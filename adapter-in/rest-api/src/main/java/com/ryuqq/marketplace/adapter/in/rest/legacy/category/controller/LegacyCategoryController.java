package com.ryuqq.marketplace.adapter.in.rest.legacy.category.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.category.LegacyCategoryEndpoints.CATEGORY;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.category.LegacyCategoryEndpoints.CATEGORY_EXTERNAL_MAPPING;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.category.LegacyCategoryEndpoints.CATEGORY_ID;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.category.LegacyCategoryEndpoints.CATEGORY_PAGE;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.category.LegacyCategoryEndpoints.CATEGORY_PARENT;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.category.LegacyCategoryEndpoints.CATEGORY_PARENTS;

import com.ryuqq.marketplace.adapter.in.rest.legacy.category.dto.request.LegacyCategoryMappingInfoRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.category.dto.response.LegacyProductCategoryContext;
import com.ryuqq.marketplace.adapter.in.rest.legacy.category.dto.response.LegacyTreeCategoryContext;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyCustomPageable;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * 세토프 어드민용 레거시 카테고리 API 호환 컨트롤러.
 *
 * <p>기존 세토프 연동 호환을 위해 제공되는 레거시 엔드포인트입니다.
 */
@Tag(
        name = "세토프 어드민용 레거시",
        description =
                "세토프 어드민용 레거시 엔드포인트. 기존 세토프 연동 호환을 위해 제공되며, 신규 개발 시에는 동일 기능의 일반 API 사용을 권장합니다.")
@RestController
public class LegacyCategoryController {

    @GetMapping(CATEGORY_ID)
    public ResponseEntity<LegacyApiResponse<List<LegacyTreeCategoryContext>>> fetchChildCategories(
            @PathVariable long categoryId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(CATEGORY_PARENT)
    public ResponseEntity<LegacyApiResponse<List<LegacyTreeCategoryContext>>> fetchParentCategories(
            @PathVariable long categoryId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(CATEGORY_PARENTS)
    public ResponseEntity<LegacyApiResponse<List<LegacyTreeCategoryContext>>> fetchCategoriesByIds(
            @RequestParam Set<Long> categoryIds) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(CATEGORY)
    public ResponseEntity<LegacyApiResponse<List<LegacyTreeCategoryContext>>> fetchAllCategories() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(CATEGORY_PAGE)
    public ResponseEntity<LegacyApiResponse<LegacyCustomPageable<LegacyProductCategoryContext>>>
            fetchCategoriesPage(Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping(CATEGORY_EXTERNAL_MAPPING)
    public ResponseEntity<LegacyApiResponse<List<LegacyCategoryMappingInfoRequest>>>
            convertExternalCategoryToInternal(
                    @PathVariable long siteId,
                    @RequestBody List<LegacyCategoryMappingInfoRequest> categoryMappingInfos) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
