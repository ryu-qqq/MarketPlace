package com.ryuqq.marketplace.adapter.in.rest.selleraddress.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.SellerAddressAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.query.SearchSellerAddressesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.response.SellerAddressApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.response.SellerOperationMetadataApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.mapper.SellerAddressQueryApiMapper;
import com.ryuqq.marketplace.application.selleraddress.dto.query.SellerAddressSearchParams;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressPageResult;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerOperationMetadataResult;
import com.ryuqq.marketplace.application.selleraddress.port.in.query.GetSellerOperationMetadataUseCase;
import com.ryuqq.marketplace.application.selleraddress.port.in.query.SearchSellerAddressUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * SellerAddressQueryController - 셀러 주소 조회 API.
 *
 * <p>셀러 주소 조회 엔드포인트를 제공합니다.
 *
 * <p>API-CTR-001: @RestController 어노테이션 필수.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-010: CQRS Controller 분리.
 *
 * <p>API-CTR-011: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
 *
 * <p>API-CTR-012: URL 경로 소문자 + 복수형 (/seller-addresses).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "셀러 주소 조회", description = "셀러 주소 조회 API")
@RestController
@RequestMapping(SellerAddressAdminEndpoints.SELLER_ADDRESSES)
public class SellerAddressQueryController {

    private final SearchSellerAddressUseCase searchUseCase;
    private final GetSellerOperationMetadataUseCase metadataUseCase;
    private final SellerAddressQueryApiMapper mapper;

    public SellerAddressQueryController(
            SearchSellerAddressUseCase searchUseCase,
            GetSellerOperationMetadataUseCase metadataUseCase,
            SellerAddressQueryApiMapper mapper) {
        this.searchUseCase = searchUseCase;
        this.metadataUseCase = metadataUseCase;
        this.mapper = mapper;
    }

    /**
     * 셀러 주소 복합 조회 API.
     *
     * <p>sellerIds를 query parameter로 전달하여 셀러 주소를 검색합니다.
     *
     * @param request 조회 요청 DTO (sellerIds 필수)
     * @return 셀러 주소 페이지 목록
     */
    @Operation(summary = "셀러 주소 목록 조회", description = "셀러 주소를 복합 조건으로 페이지 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.superAdmin()")
    @RequirePermission(value = "seller-address:read", description = "셀러 주소 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<SellerAddressApiResponse>>> search(
            @Valid SearchSellerAddressesApiRequest request) {

        SellerAddressSearchParams searchParams = mapper.toSearchParams(request);
        SellerAddressPageResult pageResult = searchUseCase.execute(searchParams);
        PageApiResponse<SellerAddressApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }

    /**
     * 셀러 운영 메타데이터 조회 API.
     *
     * <p>특정 셀러의 주소/배송정책/환불정책 메타데이터를 조회합니다.
     *
     * @param sellerId 셀러 ID
     * @return 셀러 운영 메타데이터
     */
    @Operation(summary = "셀러 운영 메타데이터 조회", description = "셀러의 주소/배송정책/환불정책 메타데이터를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#sellerId, 'seller-address:read')")
    @RequirePermission(value = "seller-address:read", description = "셀러 주소 조회")
    @GetMapping(SellerAddressAdminEndpoints.METADATA)
    public ResponseEntity<ApiResponse<SellerOperationMetadataApiResponse>> getMetadata(
            @Parameter(description = "셀러 ID", required = true) @RequestParam Long sellerId) {

        SellerOperationMetadataResult result = metadataUseCase.execute(sellerId);
        SellerOperationMetadataApiResponse response = mapper.toMetadataResponse(result);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
