package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.controller;

import static com.ryuqq.marketplace.adapter.in.rest.outboundproduct.OmsEndpoints.PARTNERS;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query.SearchOmsPartnersApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsPartnerApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.mapper.OmsPartnerQueryApiMapper;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsPartnerSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.port.in.query.SearchOmsPartnersByOffsetUseCase;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** OMS 파트너(셀러) 조회 컨트롤러 (API 6). */
@RestController
@Tag(name = "OMS Partner Query", description = "OMS 파트너 조회 API")
public class OmsPartnerQueryController {

    private final SearchOmsPartnersByOffsetUseCase searchOmsPartnersByOffsetUseCase;
    private final OmsPartnerQueryApiMapper mapper;

    public OmsPartnerQueryController(
            SearchOmsPartnersByOffsetUseCase searchOmsPartnersByOffsetUseCase,
            OmsPartnerQueryApiMapper mapper) {
        this.searchOmsPartnersByOffsetUseCase = searchOmsPartnersByOffsetUseCase;
        this.mapper = mapper;
    }

    @GetMapping(PARTNERS)
    @Operation(summary = "파트너(셀러) 목록 조회")
    @PreAuthorize("@access.authenticated()")
    public ResponseEntity<ApiResponse<PageApiResponse<OmsPartnerApiResponse>>> searchPartners(
            @ParameterObject @Valid SearchOmsPartnersApiRequest request) {
        OmsPartnerSearchParams params = mapper.toSearchParams(request);
        SellerPageResult pageResult = searchOmsPartnersByOffsetUseCase.execute(params);
        PageApiResponse<OmsPartnerApiResponse> response = mapper.toPageResponse(pageResult);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
