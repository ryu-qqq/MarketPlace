package com.ryuqq.marketplace.application.inboundorder.service.command;

import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.result.InboundOrderPollingResult;
import com.ryuqq.marketplace.application.inboundorder.internal.InboundOrderReceiveCoordinator;
import com.ryuqq.marketplace.application.inboundorder.manager.InboundOrderReadManager;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.PollExternalOrdersUseCase;
import com.ryuqq.marketplace.application.inboundorder.port.out.client.SalesChannelOrderClient;
import com.ryuqq.marketplace.application.sellersaleschannel.manager.SellerSalesChannelReadManager;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 외부몰 주문 폴링 서비스. */
@Service
public class PollExternalOrdersService implements PollExternalOrdersUseCase {

    private static final Logger log = LoggerFactory.getLogger(PollExternalOrdersService.class);
    private static final Duration DEFAULT_LOOKBACK = Duration.ofDays(7);

    private final SellerSalesChannelReadManager channelReadManager;
    private final InboundOrderReadManager inboundOrderReadManager;
    private final SalesChannelOrderClient orderClient;
    private final InboundOrderReceiveCoordinator coordinator;

    public PollExternalOrdersService(
            SellerSalesChannelReadManager channelReadManager,
            InboundOrderReadManager inboundOrderReadManager,
            SalesChannelOrderClient orderClient,
            InboundOrderReceiveCoordinator coordinator) {
        this.channelReadManager = channelReadManager;
        this.inboundOrderReadManager = inboundOrderReadManager;
        this.orderClient = orderClient;
        this.coordinator = coordinator;
    }

    @Override
    public InboundOrderPollingResult execute(String channelCode, int batchSize) {
        Instant now = Instant.now();
        InboundOrderPollingResult totalResult = InboundOrderPollingResult.empty();

        List<SellerSalesChannel> channels =
                channelReadManager.findConnectedByChannelCode(channelCode);

        for (SellerSalesChannel channel : channels) {
            Instant lastSyncedAt =
                    inboundOrderReadManager
                            .findLastExternalOrderedAt(channel.salesChannelIdValue())
                            .orElse(now.minus(DEFAULT_LOOKBACK));

            List<ExternalOrderPayload> payloads =
                    orderClient.fetchNewOrders(
                            channel.salesChannelIdValue(),
                            0L,
                            channel.sellerIdValue(),
                            channel.channelCode(),
                            channel.apiKey(),
                            channel.apiSecret(),
                            lastSyncedAt,
                            now);

            if (payloads.isEmpty()) {
                continue;
            }

            log.info(
                    "외부 주문 수신: channelCode={}, salesChannelId={}, count={}",
                    channelCode,
                    channel.salesChannelIdValue(),
                    payloads.size());

            InboundOrderPollingResult result =
                    coordinator.receiveAll(
                            payloads,
                            channel.salesChannelIdValue(),
                            0L,
                            channel.sellerIdValue(),
                            now);

            totalResult = totalResult.merge(result);
        }

        return totalResult;
    }
}
