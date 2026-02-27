package com.ryuqq.marketplace.adapter.in.rest.sellerapplication.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.SellerApplicationAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.query.SearchSellerApplicationsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.response.SellerApplicationApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.mapper.SellerApplicationQueryApiMapper;
import com.ryuqq.marketplace.application.sellerapplication.dto.query.SellerApplicationSearchParams;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationPageResult;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationResult;
import com.ryuqq.marketplace.application.sellerapplication.port.in.query.GetSellerApplicationUseCase;
import com.ryuqq.marketplace.application.sellerapplication.port.in.query.SearchSellerApplicationByOffsetUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SellerApplicationQueryController - 셀러 입점 신청 조회 API.
 *
 * <p>셀러 입점 신청 목록 조회 엔드포인트를 제공합니다.
 *
 * <p>API-CTR-001: @RestController 어노테이션 필수.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-010: CQRS Controller 분리.
 *
 * <p>API-CTR-012: URL 경로 소문자 + 복수형 (/seller-applications).
 *
 * <p>API-CTR-011: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "셀러 입점 신청 조회", description = "셀러 입점 신청 목록 조회 API")
@RestController
@RequestMapping(SellerApplicationAdminEndpoints.SELLER_APPLICATIONS)
public class SellerApplicationQueryController {

    private final SearchSellerApplicationByOffsetUseCase searchUseCase;
    private final GetSellerApplicationUseCase getUseCase;
    private final SellerApplicationQueryApiMapper mapper;

    /**
     * SellerApplicationQueryController 생성자.
     *
     * @param searchUseCase 입점 신청 검색 UseCase
     * @param getUseCase 입점 신청 상세 조회 UseCase
     * @param mapper Query API 매퍼
     */
    public SellerApplicationQueryController(
            SearchSellerApplicationByOffsetUseCase searchUseCase,
            GetSellerApplicationUseCase getUseCase,
            SellerApplicationQueryApiMapper mapper) {
        this.searchUseCase = searchUseCase;
        this.getUseCase = getUseCase;
        this.mapper = mapper;
    }

    /**
     * 셀러 입점 신청 목록 조회 API.
     *
     * <p>입점 신청 목록을 페이지 기반으로 조회합니다.
     *
     * @param request 조회 요청 DTO (페이지 기반)
     * @return 입점 신청 페이지 목록
     */
    @Operation(summary = "셀러 입점 신청 목록 조회", description = "입점 신청 목록을 페이지 기반으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.hasPermission('seller-application:read')")
    @RequirePermission(value = "seller-application:read", description = "셀러 입점 신청 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<SellerApplicationApiResponse>>> search(
            @ParameterObject @Valid SearchSellerApplicationsApiRequest request) {

        SellerApplicationSearchParams searchParams = mapper.toSearchParams(request);
        SellerApplicationPageResult pageResult = searchUseCase.execute(searchParams);
        PageApiResponse<SellerApplicationApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }

    /**
     * 셀러 입점 신청 상세 조회 API.
     *
     * <p>특정 입점 신청의 상세 정보를 조회합니다.
     *
     * @param applicationId 신청 ID
     * @return 입점 신청 상세 정보
     */
    @Operation(summary = "셀러 입점 신청 상세 조회", description = "셀러 입점 신청의 상세 정보를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "신청을 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('seller-application:read')")
    @RequirePermission(value = "seller-application:read", description = "셀러 입점 신청 조회")
    @GetMapping(SellerApplicationAdminEndpoints.ID)
    public ResponseEntity<ApiResponse<SellerApplicationApiResponse>> get(
            @Parameter(description = "신청 ID", required = true)
                    @PathVariable(SellerApplicationAdminEndpoints.PATH_APPLICATION_ID)
                    Long applicationId) {

        SellerApplicationResult result = getUseCase.execute(applicationId);
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }
}
