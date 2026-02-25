package com.ryuqq.marketplace.adapter.in.rest.commoncode.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.CommonCodeAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.query.SearchCommonCodesPageApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.response.CommonCodeApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.mapper.CommonCodeQueryApiMapper;
import com.ryuqq.marketplace.application.commoncode.dto.query.CommonCodeSearchParams;
import com.ryuqq.marketplace.application.commoncode.dto.response.CommonCodePageResult;
import com.ryuqq.marketplace.application.commoncode.port.in.query.SearchCommonCodeUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CommonCodeQueryController - 공통 코드 조회 API.
 *
 * <p>공통 코드 조회 엔드포인트를 제공합니다.
 *
 * <p>API-CTR-001: @RestController 어노테이션 필수.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-010: CQRS Controller 분리.
 *
 * <p>API-CTR-012: URL 경로 소문자 + 복수형 (/common-codes).
 *
 * <p>API-CTR-011: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "공통 코드 조회", description = "공통 코드 조회 API")
@RestController
@RequestMapping(CommonCodeAdminEndpoints.BASE)
public class CommonCodeQueryController {

    private final SearchCommonCodeUseCase searchCommonCodeUseCase;
    private final CommonCodeQueryApiMapper mapper;

    /**
     * CommonCodeQueryController 생성자.
     *
     * @param searchCommonCodeUseCase 공통 코드 검색 UseCase
     * @param mapper Query API 매퍼
     */
    public CommonCodeQueryController(
            SearchCommonCodeUseCase searchCommonCodeUseCase, CommonCodeQueryApiMapper mapper) {
        this.searchCommonCodeUseCase = searchCommonCodeUseCase;
        this.mapper = mapper;
    }

    /**
     * 공통 코드 페이지 조회 API.
     *
     * <p>공통 코드 목록을 페이지 기반으로 조회합니다. 타입별 필터링, 활성화 여부, 코드 검색을 지원합니다.
     *
     * <p>API-CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * @param request 조회 요청 DTO (페이지 기반, 필터 포함)
     * @return 공통 코드 페이지 목록
     */
    @Operation(
            summary = "공통 코드 목록 조회",
            description = "타입별 공통 코드 목록을 페이지 기반으로 조회합니다. 활성화 여부, 코드 검색을 지원합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<CommonCodeApiResponse>>> search(
            @Valid SearchCommonCodesPageApiRequest request) {

        CommonCodeSearchParams searchParams = mapper.toSearchParams(request);
        CommonCodePageResult pageResult = searchCommonCodeUseCase.execute(searchParams);
        PageApiResponse<CommonCodeApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
