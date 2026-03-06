package com.ryuqq.marketplace.application.inboundorder.port.in.command;

import com.ryuqq.marketplace.application.inboundorder.dto.result.InboundOrderPollingResult;

/** 외부몰 주문 폴링 UseCase. */
public interface PollExternalOrdersUseCase {

    InboundOrderPollingResult execute(long salesChannelId, int batchSize);
}
