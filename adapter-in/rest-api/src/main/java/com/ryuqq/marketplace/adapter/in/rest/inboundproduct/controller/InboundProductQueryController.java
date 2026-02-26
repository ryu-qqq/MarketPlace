package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.controller;

import static com.ryuqq.marketplace.adapter.in.rest.inboundproduct.InboundProductAdminEndpoints.INBOUND_PRODUCT_ID;
import static com.ryuqq.marketplace.adapter.in.rest.inboundproduct.InboundProductAdminEndpoints.PATH_EXTERNAL_PRODUCT_CODE;
import static com.ryuqq.marketplace.adapter.in.rest.inboundproduct.InboundProductAdminEndpoints.PATH_EXTERNAL_SOURCE_ID;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.response.InboundProductDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.mapper.InboundProductQueryApiMapper;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductDetailResult;
import com.ryuqq.marketplace.application.inboundproduct.port.in.query.GetInboundProductDetailUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인바운드 상품 조회 컨트롤러.
 *
 * <p>외부 식별자로 인바운드 상품 상세 정보 및 내부 상품 목록을 조회합니다.
 */
@RestController
@Tag(name = "InboundProduct Query", description = "인바운드 상품 조회 API")
public class InboundProductQueryController {

    private final GetInboundProductDetailUseCase getDetailUseCase;
    private final InboundProductQueryApiMapper apiMapper;

    public InboundProductQueryController(
            GetInboundProductDetailUseCase getDetailUseCase,
            InboundProductQueryApiMapper apiMapper) {
        this.getDetailUseCase = getDetailUseCase;
        this.apiMapper = apiMapper;
    }

    @GetMapping(INBOUND_PRODUCT_ID)
    @Operation(
            summary = "인바운드 상품 상세 조회",
            description = "외부 소스 ID와 상품 코드로 인바운드 상품 상태 및 내부 상품 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<InboundProductDetailApiResponse>> getDetail(
            @Parameter(description = "인바운드 소스 ID") @PathVariable(PATH_EXTERNAL_SOURCE_ID)
                    long inboundSourceId,
            @Parameter(description = "외부 상품 코드") @PathVariable(PATH_EXTERNAL_PRODUCT_CODE)
                    String externalProductCode) {
        InboundProductDetailResult result =
                getDetailUseCase.execute(inboundSourceId, externalProductCode);
        InboundProductDetailApiResponse response = apiMapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
