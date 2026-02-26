package com.ryuqq.marketplace.application.saleschannel.port.out.command;

import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;

/** SalesChannel Command Port. */
public interface SalesChannelCommandPort {
    Long persist(SalesChannel salesChannel);
}
