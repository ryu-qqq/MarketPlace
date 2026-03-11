package com.ryuqq.marketplace.application.inboundorder.port.out.command;

import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import java.util.List;

/** InboundOrder 저장 포트. */
public interface InboundOrderCommandPort {

    void save(InboundOrder inboundOrder);

    void saveAll(List<InboundOrder> inboundOrders);
}
