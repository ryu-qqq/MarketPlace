package com.ryuqq.marketplace.application.legacy.order.internal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.marketplace.application.cancel.dto.command.ApproveCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.RejectCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand;
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
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LegacyOrderMarketRouterTest {

    @Mock private DeliverOrderUseCase deliverOrderUseCase;
    @Mock private ShipSingleUseCase shipSingleUseCase;
    @Mock private ApproveCancelBatchUseCase approveCancelUseCase;
    @Mock private RejectCancelBatchUseCase rejectCancelUseCase;
    @Mock private ApproveRefundBatchUseCase approveRefundUseCase;
    @Mock private RejectRefundBatchUseCase rejectRefundUseCase;
    @Mock private SellerCancelBatchUseCase sellerCancelUseCase;
    @Mock private CancelReadManager cancelReadManager;
    @Mock private RefundReadManager refundReadManager;

    @InjectMocks private LegacyOrderMarketRouter router;

    private final LegacyOrderIdMapping mapping =
            LegacyOrderIdMapping.forNew(
                    5001L, 9001L, "order-uuid", 1001L, 1L, "SETOF", Instant.now());

    @Test
    @DisplayName("DELIVERY_COMPLETED → DeliverOrderUseCase 호출")
    void routeDeliveryCompleted() {
        LegacyOrderUpdateCommand command = command("DELIVERY_COMPLETED");

        router.route(command, mapping);

        verify(deliverOrderUseCase)
                .execute(
                        argThat(
                                (OrderItemStatusCommand cmd) ->
                                        cmd.orderItemIds().contains("1001")
                                                && "legacy-admin".equals(cmd.changedBy())));
    }

    @Test
    @DisplayName("DELIVERY_PROCESSING → ShipSingleUseCase 호출 (송장번호 포함)")
    void routeShip() {
        LegacyOrderUpdateCommand command =
                new LegacyOrderUpdateCommand(
                        "shipOrder",
                        5001L,
                        "DELIVERY_PROCESSING",
                        null,
                        "배송 시작",
                        "",
                        "1234567890",
                        "04",
                        "PARCEL");

        router.route(command, mapping);

        verify(shipSingleUseCase)
                .execute(
                        argThat(
                                (ShipSingleCommand cmd) ->
                                        cmd.orderItemId() == 1001L
                                                && "1234567890".equals(cmd.trackingNumber())
                                                && "04".equals(cmd.courierCode())));
    }

    @Test
    @DisplayName("CANCEL_REQUEST_CONFIRMED → ApproveCancelBatchUseCase 호출")
    void routeApproveCancel() {
        LegacyOrderUpdateCommand command = command("CANCEL_REQUEST_CONFIRMED");
        Cancel cancel = CancelFixtures.requestedCancel();
        given(cancelReadManager.findByOrderItemId(any(OrderItemId.class)))
                .willReturn(Optional.of(cancel));

        router.route(command, mapping);

        verify(approveCancelUseCase).execute(any(ApproveCancelBatchCommand.class));
    }

    @Test
    @DisplayName("CANCEL_REQUEST_REJECTED → RejectCancelBatchUseCase 호출")
    void routeRejectCancel() {
        LegacyOrderUpdateCommand command = command("CANCEL_REQUEST_REJECTED");
        Cancel cancel = CancelFixtures.requestedCancel();
        given(cancelReadManager.findByOrderItemId(any(OrderItemId.class)))
                .willReturn(Optional.of(cancel));

        router.route(command, mapping);

        verify(rejectCancelUseCase).execute(any(RejectCancelBatchCommand.class));
    }

    @Test
    @DisplayName("RETURN_REQUEST_CONFIRMED → ApproveRefundBatchUseCase 호출")
    void routeApproveRefund() {
        LegacyOrderUpdateCommand command = command("RETURN_REQUEST_CONFIRMED");
        RefundClaim refund = RefundFixtures.requestedRefundClaim();
        given(refundReadManager.findByOrderItemId(1001L)).willReturn(Optional.of(refund));

        router.route(command, mapping);

        verify(approveRefundUseCase).execute(any(ApproveRefundBatchCommand.class));
    }

    @Test
    @DisplayName("RETURN_REQUEST_REJECTED → RejectRefundBatchUseCase 호출")
    void routeRejectRefund() {
        LegacyOrderUpdateCommand command = command("RETURN_REQUEST_REJECTED");
        RefundClaim refund = RefundFixtures.requestedRefundClaim();
        given(refundReadManager.findByOrderItemId(1001L)).willReturn(Optional.of(refund));

        router.route(command, mapping);

        verify(rejectRefundUseCase).execute(any(RejectRefundBatchCommand.class));
    }

    @Test
    @DisplayName("SALE_CANCELLED → SellerCancelBatchUseCase 호출")
    void routeSellerCancel() {
        LegacyOrderUpdateCommand command = command("SALE_CANCELLED");

        router.route(command, mapping);

        verify(sellerCancelUseCase).execute(any(SellerCancelBatchCommand.class));
    }

    @Test
    @DisplayName("취소가 없으면 예외 발생")
    void throwsWhenNoCancelFound() {
        LegacyOrderUpdateCommand command = command("CANCEL_REQUEST_CONFIRMED");
        given(cancelReadManager.findByOrderItemId(any(OrderItemId.class)))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> router.route(command, mapping))
                .isInstanceOf(
                        com.ryuqq.marketplace.domain.cancel.exception.CancelNotFoundException
                                .class);
    }

    @Test
    @DisplayName("지원하지 않는 상태면 예외 발생")
    void throwsForUnsupportedStatus() {
        LegacyOrderUpdateCommand command = command("UNKNOWN_STATUS");

        assertThatThrownBy(() -> router.route(command, mapping))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("market 라우팅 불가능한 레거시 상태");
    }

    private LegacyOrderUpdateCommand command(String orderStatus) {
        return new LegacyOrderUpdateCommand(
                "normalOrder", 5001L, orderStatus, null, "사유", "상세 사유", null, null, null);
    }
}
