package com.ryuqq.marketplace.adapter.in.rest.selleradmin.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.SellerAdminApplicationEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.query.SearchSellerAdminApplicationsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response.SellerAdminApplicationApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response.SellerAdminApplicationPageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.mapper.SellerAdminApplicationQueryApiMapper;
import com.ryuqq.marketplace.application.selleradmin.dto.query.GetSellerAdminApplicationQuery;
import com.ryuqq.marketplace.application.selleradmin.dto.query.SellerAdminApplicationSearchParams;
import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminApplicationPageResult;
import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminApplicationResult;
import com.ryuqq.marketplace.application.selleradmin.port.in.query.GetSellerAdminApplicationUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.query.SearchSellerAdminApplicationsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

/**
 * SellerAdminApplicationQueryController - 셀러 관리자 가입 신청 Query API.
 *
 * <p>셀러 관리자 가입 신청 목록/상세 조회 엔드포인트를 제공합니다.
 *
 * <p>API-CTR-001: Controller는 @RestController로 정의.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-004: ResponseEntity&lt;ApiResponse&lt;T&gt;&gt; 래핑 필수.
 *
 * <p>API-CTR-005: Controller에서 @Transactional 금지.
 *
 * <p>API-CTR-007: Controller에 비즈니스 로직 포함 금지.
 *
 * <p>API-CTR-010: CQRS Controller 분리.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag(name = "셀러 관리자 가입 신청", description = "셀러 관리자 가입 신청 조회 API")
@RestController
@RequestMapping(SellerAdminApplicationEndpoints.BASE)
public class SellerAdminApplicationQueryController {

    private final SearchSellerAdminApplicationsUseCase searchUseCase;
    private final GetSellerAdminApplicationUseCase getUseCase;
    private final SellerAdminApplicationQueryApiMapper mapper;
    private final MarketAccessChecker accessChecker;

    /**
     * SellerAdminApplicationQueryController 생성자.
     *
     * @param searchUseCase 목록 조회 UseCase
     * @param getUseCase 상세 조회 UseCase
     * @param mapper Query API 매퍼
     * @param accessChecker 접근 권한 검사기
     */
    public SellerAdminApplicationQueryController(
            SearchSellerAdminApplicationsUseCase searchUseCase,
            GetSellerAdminApplicationUseCase getUseCase,
            SellerAdminApplicationQueryApiMapper mapper,
            MarketAccessChecker accessChecker) {
        this.searchUseCase = searchUseCase;
        this.getUseCase = getUseCase;
        this.mapper = mapper;
        this.accessChecker = accessChecker;
    }

    /**
     * 셀러 관리자 가입 신청 목록 조회 API.
     *
     * <p>셀러 관리자 가입 신청 목록을 조회합니다. sellerIds를 지정하면 해당 셀러들의 신청만 조회합니다.
     *
     * @param request 검색 조건 (sellerIds 포함)
     * @return 신청 목록 (페이징)
     */
    @Operation(
            summary = "셀러 관리자 가입 신청 목록 조회",
            description = "셀러 관리자 가입 신청 목록을 조회합니다. sellerIds 생략 시 전체 조회 (슈퍼관리자).")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.hasPermission('seller-admin:read')")
    @RequirePermission(value = "seller-admin:read", description = "셀러 관리자 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<SellerAdminApplicationPageApiResponse>> search(
            @Valid @ParameterObject SearchSellerAdminApplicationsApiRequest request) {

        List<Long> effectiveSellerIds =
                accessChecker.resolveEffectiveSellerIds(request.sellerIds());
        SellerAdminApplicationSearchParams params =
                mapper.toSearchParams(request, effectiveSellerIds);
        SellerAdminApplicationPageResult result = searchUseCase.execute(params);

        return ResponseEntity.ok(
                ApiResponse.of(SellerAdminApplicationPageApiResponse.from(result)));
    }

    /**
     * 셀러 관리자 가입 신청 상세 조회 API.
     *
     * <p>특정 가입 신청의 상세 정보를 조회합니다.
     *
     * @param sellerAdminId 셀러 관리자 ID
     * @return 신청 상세 정보
     */
    @Operation(summary = "셀러 관리자 가입 신청 상세 조회", description = "특정 가입 신청의 상세 정보를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "신청을 찾을 수 없음")
    })
    @PreAuthorize("@access.myselfOr(#sellerAdminId, 'seller-admin:read')")
    @RequirePermission(value = "seller-admin:read", description = "셀러 관리자 목록 조회")
    @GetMapping(SellerAdminApplicationEndpoints.DETAIL)
    public ResponseEntity<ApiResponse<SellerAdminApplicationApiResponse>> get(
            @Parameter(description = "셀러 관리자 ID", required = true)
                    @PathVariable(SellerAdminApplicationEndpoints.PATH_SELLER_ADMIN_ID)
                    String sellerAdminId) {

        GetSellerAdminApplicationQuery query = mapper.toGetQuery(sellerAdminId);
        SellerAdminApplicationResult result = getUseCase.execute(query);

        return ResponseEntity.ok(ApiResponse.of(SellerAdminApplicationApiResponse.from(result)));
    }
}
