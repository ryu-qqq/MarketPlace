package com.ryuqq.marketplace.application.inboundproduct.port.out.command;

import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.util.List;

public interface InboundProductCommandPort {
    Long persist(InboundProduct product);

    List<Long> persistAll(List<InboundProduct> products);
}
