package com.ryuqq.marketplace.application.inboundorder.service.command;

import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.result.InboundOrderPollingResult;
import com.ryuqq.marketplace.application.inboundorder.internal.InboundOrderReceiveCoordinator;
import com.ryuqq.marketplace.application.inboundorder.manager.InboundOrderReadManager;
import com.ryuqq.marketplace.application.inboundorder.manager.SalesChannelOrderClientManager;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.PollExternalOrdersUseCase;
import com.ryuqq.marketplace.application.inboundorder.port.out.client.SalesChannelOrderClient;
import com.ryuqq.marketplace.application.saleschannel.manager.SalesChannelReadManager;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 외부몰 주문 폴링 서비스 — Shop 기반. */
@Service
public class PollExternalOrdersService implements PollExternalOrdersUseCase {

    private static final Logger log = LoggerFactory.getLogger(PollExternalOrdersService.class);
    private static final Duration DEFAULT_LOOKBACK = Duration.ofDays(7);

    private final SalesChannelReadManager salesChannelReadManager;
    private final ShopReadManager shopReadManager;
    private final InboundOrderReadManager inboundOrderReadManager;
    private final SalesChannelOrderClientManager orderClientManager;
    private final InboundOrderReceiveCoordinator coordinator;

    public PollExternalOrdersService(
            SalesChannelReadManager salesChannelReadManager,
            ShopReadManager shopReadManager,
            InboundOrderReadManager inboundOrderReadManager,
            SalesChannelOrderClientManager orderClientManager,
            InboundOrderReceiveCoordinator coordinator) {
        this.salesChannelReadManager = salesChannelReadManager;
        this.shopReadManager = shopReadManager;
        this.inboundOrderReadManager = inboundOrderReadManager;
        this.orderClientManager = orderClientManager;
        this.coordinator = coordinator;
    }

    @Override
    public InboundOrderPollingResult execute(long salesChannelId, int batchSize) {
        Instant now = Instant.now();
        InboundOrderPollingResult totalResult = InboundOrderPollingResult.empty();

        SalesChannel salesChannel =
                salesChannelReadManager.getById(SalesChannelId.of(salesChannelId));

        String channelCode = salesChannel.channelName();

        if (!orderClientManager.supports(channelCode)) {
            log.warn("지원하지 않는 채널코드: channelCode={}", channelCode);
            return totalResult;
        }

        SalesChannelOrderClient orderClient = orderClientManager.getClient(channelCode);

        List<Shop> shops = shopReadManager.findActiveBySalesChannelId(salesChannelId);
        if (shops.isEmpty()) {
            log.info("활성 Shop 없음: salesChannelId={}", salesChannelId);
            return totalResult;
        }

        for (Shop shop : shops) {
            Instant lastSyncedAt =
                    inboundOrderReadManager
                            .findLastExternalOrderedAt(salesChannelId)
                            .orElse(now.minus(DEFAULT_LOOKBACK));

            List<ExternalOrderPayload> payloads =
                    orderClient.fetchNewOrders(
                            salesChannelId,
                            shop.idValue(),
                            shop.toCredentials(),
                            lastSyncedAt,
                            now);

            if (payloads.isEmpty()) {
                continue;
            }

            log.info(
                    "외부 주문 수신: channelCode={}, salesChannelId={}, shopId={}, count={}",
                    channelCode,
                    salesChannelId,
                    shop.idValue(),
                    payloads.size());

            InboundOrderPollingResult result =
                    coordinator.receiveAll(payloads, salesChannelId, shop.idValue(), now);

            totalResult = totalResult.merge(result);
        }

        return totalResult;
    }
}
