package com.ryuqq.marketplace.application.outboundproduct.port.out.command;

import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import java.util.List;

public interface OutboundProductCommandPort {
    Long persist(OutboundProduct product);

    List<Long> persistAll(List<OutboundProduct> products);
}
