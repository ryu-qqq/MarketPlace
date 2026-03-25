package com.ryuqq.marketplace.application.legacy.sellicorder.service.command;

import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.manager.SalesChannelOrderClientManager;
import com.ryuqq.marketplace.application.inboundorder.port.out.client.SalesChannelOrderClient;
import com.ryuqq.marketplace.application.legacy.sellicorder.internal.SellicLegacyOrderCoordinator;
import com.ryuqq.marketplace.application.legacy.sellicorder.port.in.IssueSellicOrderUseCase;
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

/**
 * 셀릭 주문 발행 서비스.
 *
 * <p>셀릭 API에서 주문을 폴링하여 luxurydb에 레거시 형식으로 저장하고, LegacyOrderConversionOutbox를 생성합니다.
 */
@Service
public class IssueSellicOrderService implements IssueSellicOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(IssueSellicOrderService.class);
    private static final Duration DEFAULT_LOOKBACK = Duration.ofDays(2);

    private final SalesChannelReadManager salesChannelReadManager;
    private final ShopReadManager shopReadManager;
    private final SalesChannelOrderClientManager orderClientManager;
    private final SellicLegacyOrderCoordinator coordinator;

    public IssueSellicOrderService(
            SalesChannelReadManager salesChannelReadManager,
            ShopReadManager shopReadManager,
            SalesChannelOrderClientManager orderClientManager,
            SellicLegacyOrderCoordinator coordinator) {
        this.salesChannelReadManager = salesChannelReadManager;
        this.shopReadManager = shopReadManager;
        this.orderClientManager = orderClientManager;
        this.coordinator = coordinator;
    }

    @Override
    public void execute(long salesChannelId, int batchSize) {
        Instant now = Instant.now();

        SalesChannel salesChannel =
                salesChannelReadManager.getById(SalesChannelId.of(salesChannelId));
        String channelCode = salesChannel.channelName();

        if (!orderClientManager.supports(channelCode)) {
            log.warn("셀릭 주문 발행 — 지원하지 않는 채널코드: channelCode={}", channelCode);
            return;
        }

        SalesChannelOrderClient orderClient = orderClientManager.getClient(channelCode);

        List<Shop> shops = shopReadManager.findActiveBySalesChannelId(salesChannelId);
        if (shops.isEmpty()) {
            log.info("셀릭 주문 발행 — 활성 Shop 없음: salesChannelId={}", salesChannelId);
            return;
        }

        int totalIssued = 0;
        int totalSkipped = 0;
        int totalFailed = 0;

        for (Shop shop : shops) {
            Instant fromTime = now.minus(DEFAULT_LOOKBACK);

            List<ExternalOrderPayload> payloads =
                    orderClient.fetchNewOrders(
                            salesChannelId, shop.idValue(), shop.toCredentials(), fromTime, now);

            if (payloads.isEmpty()) {
                continue;
            }

            log.info(
                    "셀릭 주문 수신: salesChannelId={}, shopId={}, count={}",
                    salesChannelId,
                    shop.idValue(),
                    payloads.size());

            for (ExternalOrderPayload payload : payloads) {
                try {
                    boolean issued =
                            coordinator.issueIfNotDuplicate(payload, salesChannelId, now);
                    if (issued) {
                        totalIssued++;
                    } else {
                        totalSkipped++;
                    }
                } catch (Exception e) {
                    totalFailed++;
                    log.error(
                            "셀릭 주문 발행 실패: externalOrderNo={}, error={}",
                            payload.externalOrderNo(),
                            e.getMessage(),
                            e);
                }
            }
        }

        log.info(
                "셀릭 주문 발행 완료: issued={}, skipped={}, failed={}",
                totalIssued,
                totalSkipped,
                totalFailed);
    }
}
