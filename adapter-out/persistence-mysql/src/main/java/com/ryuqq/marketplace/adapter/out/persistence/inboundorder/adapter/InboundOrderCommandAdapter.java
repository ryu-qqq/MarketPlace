package com.ryuqq.marketplace.adapter.out.persistence.inboundorder.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.entity.InboundOrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.entity.InboundOrderJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.mapper.InboundOrderJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.repository.InboundOrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.repository.InboundOrderJpaRepository;
import com.ryuqq.marketplace.application.inboundorder.port.out.command.InboundOrderCommandPort;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import java.util.List;
import org.springframework.stereotype.Component;

/** InboundOrder 저장 어댑터. */
@Component
public class InboundOrderCommandAdapter implements InboundOrderCommandPort {

    private final InboundOrderJpaRepository orderRepository;
    private final InboundOrderItemJpaRepository itemRepository;
    private final InboundOrderJpaEntityMapper mapper;

    public InboundOrderCommandAdapter(
            InboundOrderJpaRepository orderRepository,
            InboundOrderItemJpaRepository itemRepository,
            InboundOrderJpaEntityMapper mapper) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(InboundOrder inboundOrder) {
        InboundOrderJpaEntity orderEntity = mapper.toEntity(inboundOrder);
        InboundOrderJpaEntity saved = orderRepository.save(orderEntity);

        List<InboundOrderItemJpaEntity> itemEntities =
                mapper.toItemEntities(inboundOrder.items(), saved.getId());
        itemRepository.saveAll(itemEntities);
    }

    @Override
    public void saveAll(List<InboundOrder> inboundOrders) {
        for (InboundOrder inboundOrder : inboundOrders) {
            save(inboundOrder);
        }
    }
}
