package com.ryuqq.marketplace.adapter.out.persistence.inboundorder.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.entity.InboundOrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.entity.InboundOrderJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.mapper.InboundOrderJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.repository.InboundOrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.repository.InboundOrderQueryDslRepository;
import com.ryuqq.marketplace.application.inboundorder.port.out.query.InboundOrderQueryPort;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.vo.InboundOrderStatus;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** InboundOrder 조회 어댑터. */
@Component
public class InboundOrderQueryAdapter implements InboundOrderQueryPort {

    private final InboundOrderQueryDslRepository queryDslRepository;
    private final InboundOrderItemJpaRepository itemRepository;
    private final InboundOrderJpaEntityMapper mapper;

    public InboundOrderQueryAdapter(
            InboundOrderQueryDslRepository queryDslRepository,
            InboundOrderItemJpaRepository itemRepository,
            InboundOrderJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    @Override
    public boolean existsBySalesChannelIdAndExternalOrderNo(
            long salesChannelId, String externalOrderNo) {
        return queryDslRepository.existsBySalesChannelIdAndExternalOrderNo(
                salesChannelId, externalOrderNo);
    }

    @Override
    public Set<String> findExistingExternalOrderNos(
            long salesChannelId, Set<String> externalOrderNos) {
        if (externalOrderNos.isEmpty()) {
            return Set.of();
        }
        List<String> existing =
                queryDslRepository.findExternalOrderNosBySalesChannelIdAndExternalOrderNoIn(
                        salesChannelId, externalOrderNos);
        return new HashSet<>(existing);
    }

    @Override
    public Optional<Instant> findLastExternalOrderedAt(long salesChannelId) {
        return queryDslRepository.findMaxExternalOrderedAtBySalesChannelId(salesChannelId);
    }

    @Override
    public List<InboundOrder> findByStatus(InboundOrderStatus status, int limit) {
        InboundOrderJpaEntity.Status entityStatus =
                InboundOrderJpaEntity.Status.valueOf(status.name());
        List<InboundOrderJpaEntity> orderEntities =
                queryDslRepository.findByStatusOrderByIdAsc(entityStatus, limit);

        if (orderEntities.isEmpty()) {
            return List.of();
        }

        List<Long> orderIds = orderEntities.stream().map(InboundOrderJpaEntity::getId).toList();
        List<InboundOrderItemJpaEntity> allItems = itemRepository.findByInboundOrderIdIn(orderIds);

        Map<Long, List<InboundOrderItemJpaEntity>> itemsByOrderId =
                allItems.stream()
                        .collect(
                                Collectors.groupingBy(
                                        InboundOrderItemJpaEntity::getInboundOrderId));

        return orderEntities.stream()
                .map(
                        order ->
                                mapper.toDomain(
                                        order,
                                        itemsByOrderId.getOrDefault(order.getId(), List.of())))
                .toList();
    }
}
