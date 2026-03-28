package com.ryuqq.marketplace.domain.order.aggregate;

import com.ryuqq.marketplace.domain.order.exception.OrderErrorCode;
import com.ryuqq.marketplace.domain.order.exception.OrderException;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.id.OrderItemNumber;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderItemPrice;
import com.ryuqq.marketplace.domain.order.vo.ExternalProductSnapshot;
import com.ryuqq.marketplace.domain.order.vo.InternalProductReference;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import com.ryuqq.marketplace.domain.order.vo.ReceiverInfo;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 주문 상품. Order Aggregate 내부 구성 요소. */
public class OrderItem {

    private final OrderItemId id;
    private final OrderItemNumber orderItemNumber;
    private final InternalProductReference internalProduct;
    private final ExternalProductSnapshot externalProduct;
    private final ExternalOrderItemPrice price;
    private final ReceiverInfo receiverInfo;
    private OrderItemStatus status;
    private String externalOrderStatus;
    private int cancelledQty;
    private int returnedQty;

    private final List<OrderItemHistory> histories = new ArrayList<>();

    private OrderItem(
            OrderItemId id,
            OrderItemNumber orderItemNumber,
            InternalProductReference internalProduct,
            ExternalProductSnapshot externalProduct,
            ExternalOrderItemPrice price,
            ReceiverInfo receiverInfo,
            OrderItemStatus status,
            String externalOrderStatus,
            int cancelledQty,
            int returnedQty) {
        this.id = id;
        this.orderItemNumber = orderItemNumber;
        this.internalProduct = internalProduct;
        this.externalProduct = externalProduct;
        this.price = price;
        this.receiverInfo = receiverInfo;
        this.status = status;
        this.externalOrderStatus = externalOrderStatus;
        this.cancelledQty = cancelledQty;
        this.returnedQty = returnedQty;
    }

    public static OrderItem forNew(
            OrderItemId id,
            OrderItemNumber orderItemNumber,
            InternalProductReference internalProduct,
            ExternalProductSnapshot externalProduct,
            ExternalOrderItemPrice price,
            ReceiverInfo receiverInfo,
            String externalOrderStatus) {
        return new OrderItem(
                id,
                orderItemNumber,
                internalProduct,
                externalProduct,
                price,
                receiverInfo,
                OrderItemStatus.READY,
                externalOrderStatus,
                0,
                0);
    }

    public static OrderItem reconstitute(
            OrderItemId id,
            OrderItemNumber orderItemNumber,
            InternalProductReference internalProduct,
            ExternalProductSnapshot externalProduct,
            ExternalOrderItemPrice price,
            ReceiverInfo receiverInfo,
            OrderItemStatus status,
            String externalOrderStatus,
            int cancelledQty,
            int returnedQty,
            List<OrderItemHistory> histories) {
        OrderItem item =
                new OrderItem(
                        id,
                        orderItemNumber,
                        internalProduct,
                        externalProduct,
                        price,
                        receiverInfo,
                        status,
                        externalOrderStatus,
                        cancelledQty,
                        returnedQty);
        if (histories != null) {
            item.histories.addAll(histories);
        }
        return item;
    }

    public void confirm(String changedBy, Instant now) {
        OrderItemStatus from = this.status;
        validateTransition(OrderItemStatus.CONFIRMED);
        this.status = OrderItemStatus.CONFIRMED;
        this.histories.add(
                OrderItemHistory.of(
                        this.id, from, OrderItemStatus.CONFIRMED, changedBy, null, now));
    }

    /** 전체 취소. 잔여 수량 전부를 취소 처리한다. 기존 호출부 호환용. 내부적으로 partialCancel(remainingCancelableQty)을 호출한다. */
    public void cancel(String changedBy, String reason, Instant now) {
        int remaining = remainingCancelableQty();
        if (remaining <= 0) {
            validateTransition(OrderItemStatus.CANCELLED);
        }
        partialCancel(remaining > 0 ? remaining : quantity(), changedBy, reason, now);
    }

    /**
     * 부분 취소. cancelQty만큼 취소하고, 전체 수량 소진 시 CANCELLED 상태로 전환한다.
     *
     * @param cancelQty 이번에 취소할 수량
     */
    public void partialCancel(int cancelQty, String changedBy, String reason, Instant now) {
        if (cancelQty <= 0) {
            throw new OrderException(
                    OrderErrorCode.INVALID_CANCEL_QUANTITY,
                    String.format("취소 수량은 1 이상이어야 합니다: %d", cancelQty));
        }
        if (cancelQty > remainingCancelableQty()) {
            throw new OrderException(
                    OrderErrorCode.INVALID_CANCEL_QUANTITY,
                    String.format(
                            "주문상품 %s: 취소 가능 수량(%d)을 초과합니다. 요청=%d",
                            id.value(), remainingCancelableQty(), cancelQty));
        }

        OrderItemStatus from = this.status;
        this.cancelledQty += cancelQty;

        if (isFullyCancelled()) {
            if (from != OrderItemStatus.CANCELLED) {
                validateTransition(OrderItemStatus.CANCELLED);
                this.status = OrderItemStatus.CANCELLED;
            }
        }

        this.histories.add(
                OrderItemHistory.of(this.id, from, this.status, changedBy, reason, cancelQty, now));
    }

    /** 취소 가능한 잔여 수량. */
    public int remainingCancelableQty() {
        return quantity() - cancelledQty;
    }

    /** 전체 수량이 취소되었는지 여부. */
    public boolean isFullyCancelled() {
        return cancelledQty >= quantity();
    }

    public void requestReturn(String changedBy, String reason, Instant now) {
        OrderItemStatus from = this.status;
        validateTransition(OrderItemStatus.RETURN_REQUESTED);
        this.status = OrderItemStatus.RETURN_REQUESTED;
        this.histories.add(
                OrderItemHistory.of(
                        this.id, from, OrderItemStatus.RETURN_REQUESTED, changedBy, reason, now));
    }

    public void completeReturn(String changedBy, Instant now) {
        OrderItemStatus from = this.status;
        validateTransition(OrderItemStatus.RETURNED);
        this.status = OrderItemStatus.RETURNED;
        this.histories.add(
                OrderItemHistory.of(this.id, from, OrderItemStatus.RETURNED, changedBy, null, now));
    }

    /**
     * 부분 반품. returnQty만큼 반품하고, 전체 수량 소진 시 RETURNED 상태로 전환한다.
     *
     * @param returnQty 이번에 반품할 수량
     */
    public void partialReturn(int returnQty, String changedBy, String reason, Instant now) {
        if (returnQty <= 0) {
            throw new OrderException(
                    OrderErrorCode.INVALID_RETURN_QUANTITY,
                    String.format("반품 수량은 1 이상이어야 합니다: %d", returnQty));
        }
        if (returnQty > remainingReturnableQty()) {
            throw new OrderException(
                    OrderErrorCode.INVALID_RETURN_QUANTITY,
                    String.format(
                            "주문상품 %s: 반품 가능 수량(%d)을 초과합니다. 요청=%d",
                            id.value(), remainingReturnableQty(), returnQty));
        }

        OrderItemStatus from = this.status;
        this.returnedQty += returnQty;

        if (isFullyReturned()) {
            if (from != OrderItemStatus.RETURNED) {
                this.status = OrderItemStatus.RETURNED;
            }
        }

        this.histories.add(
                OrderItemHistory.of(this.id, from, this.status, changedBy, reason, returnQty, now));
    }

    /** 반품 가능한 잔여 수량. */
    public int remainingReturnableQty() {
        return quantity() - returnedQty;
    }

    /** 전체 수량이 반품되었는지 여부. */
    public boolean isFullyReturned() {
        return returnedQty >= quantity();
    }

    public boolean isConfirmable() {
        return status.canTransitionTo(OrderItemStatus.CONFIRMED);
    }

    private void validateTransition(OrderItemStatus target) {
        if (!this.status.canTransitionTo(target)) {
            throw new OrderException(
                    OrderErrorCode.INVALID_STATUS_TRANSITION,
                    String.format(
                            "주문상품 %s: %s 상태에서 %s 상태로 변경할 수 없습니다", id.value(), this.status, target));
        }
    }

    public OrderItemId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public OrderItemNumber orderItemNumber() {
        return orderItemNumber;
    }

    public String orderItemNumberValue() {
        return orderItemNumber.value();
    }

    public InternalProductReference internalProduct() {
        return internalProduct;
    }

    public ExternalProductSnapshot externalProduct() {
        return externalProduct;
    }

    public ExternalOrderItemPrice price() {
        return price;
    }

    public int quantity() {
        return price.quantity();
    }

    public long sellerId() {
        return internalProduct.sellerId();
    }

    public ReceiverInfo receiverInfo() {
        return receiverInfo;
    }

    public OrderItemStatus status() {
        return status;
    }

    public String externalOrderStatus() {
        return externalOrderStatus;
    }

    public void updateExternalOrderStatus(String externalOrderStatus) {
        this.externalOrderStatus = externalOrderStatus;
    }

    public int cancelledQty() {
        return cancelledQty;
    }

    public int returnedQty() {
        return returnedQty;
    }

    public List<OrderItemHistory> histories() {
        return Collections.unmodifiableList(histories);
    }
}
