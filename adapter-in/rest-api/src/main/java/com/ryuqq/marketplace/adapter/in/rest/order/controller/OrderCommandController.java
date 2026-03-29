package com.ryuqq.marketplace.adapter.in.rest.order.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.response.ClaimHistoryMemoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker.ActorInfo;
import com.ryuqq.marketplace.adapter.in.rest.order.OrderAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.order.mapper.OrderCommandApiMapper;
import com.ryuqq.marketplace.application.claimhistory.port.in.command.AddClaimHistoryMemoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 주문 커맨드 API 컨트롤러. */
@Tag(name = "주문 명령", description = "주문 명령 API")
@RestController
@RequestMapping(OrderAdminEndpoints.ORDERS)
public class OrderCommandController {

    private final AddClaimHistoryMemoUseCase addClaimHistoryMemoUseCase;
    private final OrderCommandApiMapper mapper;
    private final MarketAccessChecker accessChecker;

    public OrderCommandController(
            AddClaimHistoryMemoUseCase addClaimHistoryMemoUseCase,
            OrderCommandApiMapper mapper,
            MarketAccessChecker accessChecker) {
        this.addClaimHistoryMemoUseCase = addClaimHistoryMemoUseCase;
        this.mapper = mapper;
        this.accessChecker = accessChecker;
    }

    @Operation(summary = "주문 수기 메모 등록", description = "주문 건에 수기 메모를 등록합니다.")
    @PreAuthorize("@access.hasPermission('order:write')")
    @RequirePermission(value = "order:write", description = "주문 수기 메모 등록")
    @PostMapping(OrderAdminEndpoints.HISTORIES)
    public ResponseEntity<ApiResponse<ClaimHistoryMemoApiResponse>> addMemo(
            @PathVariable(OrderAdminEndpoints.PATH_ORDER_ITEM_ID) String orderId,
            @RequestBody @Valid AddClaimHistoryMemoApiRequest request) {
        ActorInfo actor = accessChecker.resolveActorInfo();
        String historyId =
                addClaimHistoryMemoUseCase.execute(
                        mapper.toAddMemoCommand(orderId, request, actor));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(new ClaimHistoryMemoApiResponse(historyId)));
    }
}
