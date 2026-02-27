package com.ryuqq.marketplace.adapter.in.rest.canonicaloption.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.canonicaloption.CanonicalOptionAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.canonicaloption.dto.query.SearchCanonicalOptionGroupsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.canonicaloption.dto.response.CanonicalOptionGroupApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.canonicaloption.mapper.CanonicalOptionGroupQueryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupPageResult;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupResult;
import com.ryuqq.marketplace.application.canonicaloption.port.in.query.GetCanonicalOptionGroupUseCase;
import com.ryuqq.marketplace.application.canonicaloption.port.in.query.SearchCanonicalOptionGroupByOffsetUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 캐노니컬 옵션 그룹 조회 API 컨트롤러. */
@Tag(name = "정규 옵션그룹 조회", description = "정규 옵션그룹 조회 API")
@RestController
@RequestMapping(CanonicalOptionAdminEndpoints.CANONICAL_OPTION_GROUPS)
public class CanonicalOptionGroupQueryController {

    private final GetCanonicalOptionGroupUseCase getCanonicalOptionGroupUseCase;
    private final SearchCanonicalOptionGroupByOffsetUseCase
            searchCanonicalOptionGroupByOffsetUseCase;
    private final CanonicalOptionGroupQueryApiMapper mapper;

    public CanonicalOptionGroupQueryController(
            GetCanonicalOptionGroupUseCase getCanonicalOptionGroupUseCase,
            SearchCanonicalOptionGroupByOffsetUseCase searchCanonicalOptionGroupByOffsetUseCase,
            CanonicalOptionGroupQueryApiMapper mapper) {
        this.getCanonicalOptionGroupUseCase = getCanonicalOptionGroupUseCase;
        this.searchCanonicalOptionGroupByOffsetUseCase = searchCanonicalOptionGroupByOffsetUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "정규 옵션그룹 목록 조회", description = "정규 옵션그룹 목록을 조회합니다.")
    @PreAuthorize("@access.hasPermission('canonical-option-group:read')")
    @RequirePermission(value = "canonical-option-group:read", description = "캐노니컬 옵션 그룹 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<CanonicalOptionGroupApiResponse>>>
            searchCanonicalOptionGroupsByOffset(
                    @ParameterObject @Valid SearchCanonicalOptionGroupsApiRequest request) {
        CanonicalOptionGroupPageResult pageResult =
                searchCanonicalOptionGroupByOffsetUseCase.execute(mapper.toSearchParams(request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toPageResponse(pageResult)));
    }

    @Operation(summary = "정규 옵션그룹 상세 조회", description = "정규 옵션그룹 상세 정보를 조회합니다.")
    @PreAuthorize("@access.hasPermission('canonical-option-group:read')")
    @RequirePermission(value = "canonical-option-group:read", description = "캐노니컬 옵션 그룹 단건 조회")
    @GetMapping(CanonicalOptionAdminEndpoints.CANONICAL_OPTION_GROUP_ID)
    public ResponseEntity<ApiResponse<CanonicalOptionGroupApiResponse>> getCanonicalOptionGroup(
            @PathVariable(CanonicalOptionAdminEndpoints.PATH_CANONICAL_OPTION_GROUP_ID)
                    Long canonicalOptionGroupId) {
        CanonicalOptionGroupResult result =
                getCanonicalOptionGroupUseCase.execute(canonicalOptionGroupId);
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }
}
