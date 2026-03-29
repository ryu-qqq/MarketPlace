package com.ryuqq.marketplace.application.legacy.order.internal;

import com.ryuqq.marketplace.application.cancel.dto.command.ApproveCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.RejectCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand.SellerCancelItem;
import com.ryuqq.marketplace.application.cancel.manager.CancelReadManager;
import com.ryuqq.marketplace.application.cancel.port.in.command.ApproveCancelBatchUseCase;
import com.ryuqq.marketplace.application.cancel.port.in.command.RejectCancelBatchUseCase;
import com.ryuqq.marketplace.application.cancel.port.in.command.SellerCancelBatchUseCase;
import com.ryuqq.marketplace.application.legacy.order.dto.command.LegacyOrderUpdateCommand;
import com.ryuqq.marketplace.application.order.dto.command.OrderItemStatusCommand;
import com.ryuqq.marketplace.application.order.port.in.command.DeliverOrderUseCase;
import com.ryuqq.marketplace.application.refund.dto.command.ApproveRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.dto.command.RejectRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.manager.RefundReadManager;
import com.ryuqq.marketplace.application.refund.port.in.command.ApproveRefundBatchUseCase;
import com.ryuqq.marketplace.application.refund.port.in.command.RejectRefundBatchUseCase;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipSingleCommand;
import com.ryuqq.marketplace.application.shipment.port.in.command.ShipSingleUseCase;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReasonType;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 상태 변경 → market UseCase 라우터.
 *
 * <p>레거시 주문 상태별 커맨드를 표준 market UseCase 호출로 변환합니다. 매핑이 있는 주문에 대해서만 호출됩니다.
 */
@Component
public class LegacyOrderMarketRouter {

    private static final String LEGACY_SYSTEM = "legacy-admin";

    private final DeliverOrderUseCase deliverOrderUseCase;
    private final ShipSingleUseCase shipSingleUseCase;
    private final ApproveCancelBatchUseCase approveCancelUseCase;
    private final RejectCancelBatchUseCase rejectCancelUseCase;
    private final ApproveRefundBatchUseCase approveRefundUseCase;
    private final RejectRefundBatchUseCase rejectRefundUseCase;
    private final SellerCancelBatchUseCase sellerCancelUseCase;
    private final CancelReadManager cancelReadManager;
    private final RefundReadManager refundReadManager;

    public LegacyOrderMarketRouter(
            DeliverOrderUseCase deliverOrderUseCase,
            ShipSingleUseCase shipSingleUseCase,
            ApproveCancelBatchUseCase approveCancelUseCase,
            RejectCancelBatchUseCase rejectCancelUseCase,
            ApproveRefundBatchUseCase approveRefundUseCase,
            RejectRefundBatchUseCase rejectRefundUseCase,
            SellerCancelBatchUseCase sellerCancelUseCase,
            CancelReadManager cancelReadManager,
            RefundReadManager refundReadManager) {
        this.deliverOrderUseCase = deliverOrderUseCase;
        this.shipSingleUseCase = shipSingleUseCase;
        this.approveCancelUseCase = approveCancelUseCase;
        this.rejectCancelUseCase = rejectCancelUseCase;
        this.approveRefundUseCase = approveRefundUseCase;
        this.rejectRefundUseCase = rejectRefundUseCase;
        this.sellerCancelUseCase = sellerCancelUseCase;
        this.cancelReadManager = cancelReadManager;
        this.refundReadManager = refundReadManager;
    }

    /**
     * 레거시 커맨드를 market UseCase로 라우팅.
     *
     * @param command 레거시 주문 상태 변경 커맨드
     * @param mapping 레거시-market ID 매핑
     */
    public void route(LegacyOrderUpdateCommand command, LegacyOrderIdMapping mapping) {
        Long orderItemId = mapping.internalOrderItemId();
        String orderItemIdStr = String.valueOf(orderItemId);

        switch (command.orderStatus()) {
            case "DELIVERY_COMPLETED" ->
                    deliverOrderUseCase.execute(
                            new OrderItemStatusCommand(List.of(orderItemIdStr), LEGACY_SYSTEM));

            case "DELIVERY_PROCESSING" ->
                    shipSingleUseCase.execute(
                            new ShipSingleCommand(
                                    orderItemId,
                                    safe(command.invoiceNo()),
                                    safe(command.courierCode()),
                                    "",
                                    safe(command.shipmentType())));

            case "CANCEL_REQUEST_CONFIRMED" -> routeApproveCancel(orderItemId);

            case "CANCEL_REQUEST_REJECTED" -> routeRejectCancel(orderItemId);

            case "RETURN_REQUEST_CONFIRMED" -> routeApproveRefund(orderItemId);

            case "RETURN_REQUEST_REJECTED" -> routeRejectRefund(orderItemId);

            case "SALE_CANCELLED" -> routeSellerCancel(command, orderItemId);

            default ->
                    throw new IllegalArgumentException(
                            "market 라우팅 불가능한 레거시 상태: " + command.orderStatus());
        }
    }

    private void routeApproveCancel(Long orderItemId) {
        Cancel cancel =
                cancelReadManager
                        .findByOrderItemId(OrderItemId.of(orderItemId))
                        .orElseThrow(
                                () ->
                                        new com.ryuqq.marketplace.domain.cancel.exception
                                                .CancelNotFoundException(
                                                String.valueOf(orderItemId)));
        approveCancelUseCase.execute(
                new ApproveCancelBatchCommand(List.of(cancel.idValue()), LEGACY_SYSTEM, null));
    }

    private void routeRejectCancel(Long orderItemId) {
        Cancel cancel =
                cancelReadManager
                        .findByOrderItemId(OrderItemId.of(orderItemId))
                        .orElseThrow(
                                () ->
                                        new com.ryuqq.marketplace.domain.cancel.exception
                                                .CancelNotFoundException(
                                                String.valueOf(orderItemId)));
        rejectCancelUseCase.execute(
                new RejectCancelBatchCommand(List.of(cancel.idValue()), LEGACY_SYSTEM, null));
    }

    private void routeApproveRefund(Long orderItemId) {
        RefundClaim refund =
                refundReadManager
                        .findByOrderItemId(orderItemId)
                        .orElseThrow(
                                () ->
                                        new com.ryuqq.marketplace.domain.refund.exception
                                                .RefundNotFoundException(
                                                String.valueOf(orderItemId)));
        approveRefundUseCase.execute(
                new ApproveRefundBatchCommand(List.of(refund.idValue()), LEGACY_SYSTEM, null));
    }

    private void routeRejectRefund(Long orderItemId) {
        RefundClaim refund =
                refundReadManager
                        .findByOrderItemId(orderItemId)
                        .orElseThrow(
                                () ->
                                        new com.ryuqq.marketplace.domain.refund.exception
                                                .RefundNotFoundException(
                                                String.valueOf(orderItemId)));
        rejectRefundUseCase.execute(
                new RejectRefundBatchCommand(List.of(refund.idValue()), LEGACY_SYSTEM, null));
    }

    private void routeSellerCancel(LegacyOrderUpdateCommand command, Long orderItemId) {
        SellerCancelItem item =
                new SellerCancelItem(
                        orderItemId,
                        1,
                        CancelReasonType.OUT_OF_STOCK,
                        safe(command.changeReason()));
        sellerCancelUseCase.execute(new SellerCancelBatchCommand(List.of(item), LEGACY_SYSTEM, 0L));
    }

    private String safe(String value) {
        return value != null ? value : "";
    }
}
