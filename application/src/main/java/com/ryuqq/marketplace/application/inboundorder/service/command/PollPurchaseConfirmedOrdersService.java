package com.ryuqq.marketplace.application.inboundorder.service.command;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.PollPurchaseConfirmedOrdersUseCase;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.ReceivePurchaseConfirmedWebhookUseCase;
import com.ryuqq.marketplace.application.inboundorder.port.out.client.SalesChannelPurchaseConfirmedClient;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * 외부몰 구매확정 폴링 서비스.
 *
 * <p>활성 Shop 목록을 순회하며 구매확정된 상품주문을 조회하고, ReceivePurchaseConfirmedWebhookUseCase를 통해 내부 상태를 갱신합니다.
 */
@Service
@ConditionalOnBean(SalesChannelPurchaseConfirmedClient.class)
public class PollPurchaseConfirmedOrdersService implements PollPurchaseConfirmedOrdersUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(PollPurchaseConfirmedOrdersService.class);
    private static final Duration DEFAULT_LOOKBACK = Duration.ofDays(1);

    private final SalesChannelReadManager salesChannelReadManager;
    private final ShopReadManager shopReadManager;
    private final SalesChannelPurchaseConfirmedClient purchaseConfirmedClient;
    private final ReceivePurchaseConfirmedWebhookUseCase receivePurchaseConfirmedWebhookUseCase;
    private final TimeProvider timeProvider;

    public PollPurchaseConfirmedOrdersService(
            SalesChannelReadManager salesChannelReadManager,
            ShopReadManager shopReadManager,
            SalesChannelPurchaseConfirmedClient purchaseConfirmedClient,
            ReceivePurchaseConfirmedWebhookUseCase receivePurchaseConfirmedWebhookUseCase,
            TimeProvider timeProvider) {
        this.salesChannelReadManager = salesChannelReadManager;
        this.shopReadManager = shopReadManager;
        this.purchaseConfirmedClient = purchaseConfirmedClient;
        this.receivePurchaseConfirmedWebhookUseCase = receivePurchaseConfirmedWebhookUseCase;
        this.timeProvider = timeProvider;
    }

    @Override
    public void execute(long salesChannelId) {
        Instant now = timeProvider.now();

        SalesChannel salesChannel =
                salesChannelReadManager.getById(SalesChannelId.of(salesChannelId));

        String channelCode = salesChannel.channelName();

        if (!purchaseConfirmedClient.supports(channelCode)) {
            log.warn("지원하지 않는 채널코드: channelCode={}", channelCode);
            return;
        }

        List<Shop> shops = shopReadManager.findActiveBySalesChannelId(salesChannelId);
        if (shops.isEmpty()) {
            log.info("활성 Shop 없음: salesChannelId={}", salesChannelId);
            return;
        }

        for (Shop shop : shops) {
            Instant fromTime = now.minus(DEFAULT_LOOKBACK);

            List<String> productOrderIds =
                    purchaseConfirmedClient.fetchPurchaseConfirmedProductOrderIds(
                            salesChannelId, shop.idValue(), shop.toCredentials(), fromTime, now);

            if (productOrderIds.isEmpty()) {
                continue;
            }

            log.info(
                    "구매확정 수신: channelCode={}, salesChannelId={}, shopId={}, count={}",
                    channelCode,
                    salesChannelId,
                    shop.idValue(),
                    productOrderIds.size());

            receivePurchaseConfirmedWebhookUseCase.execute(salesChannelId, productOrderIds);
        }
    }
}
