package com.ryuqq.marketplace.adapter.in.rest.legacy.order.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDERS;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints.ORDER_ID;

import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyCustomPageable;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.security.LegacyAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request.LegacyOrderSearchRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderListResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.mapper.LegacyOrderQueryApiMapper;
import com.ryuqq.marketplace.application.legacy.order.dto.query.LegacyOrderSearchParams;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderPageResult;
import com.ryuqq.marketplace.application.legacy.order.port.in.query.LegacyOrderListQueryUseCase;
import com.ryuqq.marketplace.application.legacy.order.port.in.query.LegacyOrderQueryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 주문 조회 API 컨트롤러.
 *
 * <p>MASTER는 전체 주문 조회, SELLER는 본인 주문만 조회 가능.
 */
@Tag(name = "세토프 어드민용 레거시 - 주문", description = "세토프 어드민용 레거시 주문 엔드포인트.")
@RestController
public class LegacyOrderQueryController {

    private final LegacyOrderQueryUseCase orderQueryUseCase;
    private final LegacyOrderListQueryUseCase orderListQueryUseCase;
    private final LegacyOrderQueryApiMapper queryApiMapper;
    private final LegacyAccessChecker legacyAccessChecker;

    public LegacyOrderQueryController(
            LegacyOrderQueryUseCase orderQueryUseCase,
            LegacyOrderListQueryUseCase orderListQueryUseCase,
            LegacyOrderQueryApiMapper queryApiMapper,
            LegacyAccessChecker legacyAccessChecker) {
        this.orderQueryUseCase = orderQueryUseCase;
        this.orderListQueryUseCase = orderListQueryUseCase;
        this.queryApiMapper = queryApiMapper;
        this.legacyAccessChecker = legacyAccessChecker;
    }

    @Operation(summary = "주문 단건 조회", description = "주문 ID로 주문 상세 정보를 조회합니다.")
    @PreAuthorize("@legacyAccess.authenticated()")
    @GetMapping(ORDER_ID)
    public ResponseEntity<LegacyApiResponse<LegacyOrderResponse>> fetchOrder(
            @PathVariable long orderId) {
        LegacyOrderDetailResult result = orderQueryUseCase.execute(orderId);

        if (!legacyAccessChecker.isMaster()
                && result.sellerId() != legacyAccessChecker.getCurrentSellerId()) {
            throw new AccessDeniedException("해당 주문에 접근 권한이 없습니다");
        }

        LegacyOrderResponse response = queryApiMapper.toOrderResponse(result);
        return ResponseEntity.ok(LegacyApiResponse.success(response));
    }

    @Operation(summary = "주문 목록 조회", description = "커서 기반 페이징으로 주문 목록을 조회합니다.")
    @PreAuthorize("@legacyAccess.authenticated()")
    @GetMapping(ORDERS)
    public ResponseEntity<LegacyApiResponse<LegacyCustomPageable<LegacyOrderListResponse>>>
            getOrders(@ModelAttribute LegacyOrderSearchRequest request) {

        Long effectiveSellerId = legacyAccessChecker.resolveSellerIdOrNull();
        LegacyOrderSearchParams params = queryApiMapper.toSearchParams(request, effectiveSellerId);

        LegacyOrderPageResult pageResult = orderListQueryUseCase.execute(params);

        List<LegacyOrderListResponse> responses =
                queryApiMapper.toOrderListResponses(pageResult.items());

        Pageable pageable = PageRequest.of(0, params.size());
        LegacyCustomPageable<LegacyOrderListResponse> page =
                new LegacyCustomPageable<>(
                        responses, pageable, pageResult.totalElements(), pageResult.lastDomainId());

        return ResponseEntity.ok(LegacyApiResponse.success(page));
    }
}
