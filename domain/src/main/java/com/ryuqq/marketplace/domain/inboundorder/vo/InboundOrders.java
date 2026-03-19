package com.ryuqq.marketplace.domain.inboundorder.vo;

import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrderItem;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** InboundOrder 목록을 감싸는 도메인 VO. */
@SuppressWarnings("PMD.DomainTooManyMethods")
public class InboundOrders {

    private final List<InboundOrder> orders;

    private InboundOrders(List<InboundOrder> orders) {
        this.orders = orders;
    }

    public static InboundOrders of(List<InboundOrder> orders) {
        return new InboundOrders(List.copyOf(orders));
    }

    public static InboundOrders empty() {
        return new InboundOrders(List.of());
    }

    public Set<String> externalOrderNos() {
        return orders.stream()
                .map(InboundOrder::externalOrderNo)
                .collect(Collectors.toUnmodifiableSet());
    }

    public InboundOrders excludeDuplicates(Set<String> existingOrderNos) {
        List<InboundOrder> filtered =
                orders.stream()
                        .filter(o -> !existingOrderNos.contains(o.externalOrderNo()))
                        .toList();
        return new InboundOrders(filtered);
    }

    public Set<String> unmappedExternalProductIds() {
        return orders.stream()
                .flatMap(o -> o.items().stream())
                .filter(item -> !item.isMapped())
                .map(InboundOrderItem::externalProductId)
                .collect(Collectors.toUnmodifiableSet());
    }

    public List<InboundOrder> fullyMapped() {
        return orders.stream()
                .filter(o -> o.items().stream().allMatch(InboundOrderItem::isMapped))
                .toList();
    }

    public List<InboundOrder> pendingMapping() {
        return orders.stream()
                .filter(o -> o.items().stream().anyMatch(item -> !item.isMapped()))
                .toList();
    }

    public List<InboundOrder> notConverted() {
        return orders.stream().filter(o -> o.status() != InboundOrderStatus.CONVERTED).toList();
    }

    public List<InboundOrder> all() {
        return Collections.unmodifiableList(orders);
    }

    public int size() {
        return orders.size();
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }
}
