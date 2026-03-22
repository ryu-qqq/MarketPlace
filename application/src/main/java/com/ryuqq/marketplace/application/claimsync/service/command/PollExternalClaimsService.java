package com.ryuqq.marketplace.application.claimsync.service.command;

import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.claimsync.dto.result.ClaimSyncResult;
import com.ryuqq.marketplace.application.claimsync.internal.ClaimSyncCoordinator;
import com.ryuqq.marketplace.application.claimsync.port.in.PollExternalClaimsUseCase;
import com.ryuqq.marketplace.application.claimsync.port.out.client.SalesChannelClaimClient;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
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

/** 외부몰 클레임 폴링 서비스 — Shop 기반. */
@Service
@ConditionalOnBean(SalesChannelClaimClient.class)
public class PollExternalClaimsService implements PollExternalClaimsUseCase {

    private static final Logger log = LoggerFactory.getLogger(PollExternalClaimsService.class);
    private static final Duration DEFAULT_LOOKBACK = Duration.ofDays(1);

    private final SalesChannelReadManager salesChannelReadManager;
    private final ShopReadManager shopReadManager;
    private final SalesChannelClaimClient claimClient;
    private final ClaimSyncCoordinator coordinator;
    private final TimeProvider timeProvider;

    public PollExternalClaimsService(
            SalesChannelReadManager salesChannelReadManager,
            ShopReadManager shopReadManager,
            SalesChannelClaimClient claimClient,
            ClaimSyncCoordinator coordinator,
            TimeProvider timeProvider) {
        this.salesChannelReadManager = salesChannelReadManager;
        this.shopReadManager = shopReadManager;
        this.claimClient = claimClient;
        this.coordinator = coordinator;
        this.timeProvider = timeProvider;
    }

    @Override
    public ClaimSyncResult execute(long salesChannelId) {
        Instant now = timeProvider.now();
        ClaimSyncResult totalResult = ClaimSyncResult.empty();

        SalesChannel salesChannel =
                salesChannelReadManager.getById(SalesChannelId.of(salesChannelId));

        String channelCode = salesChannel.channelName();

        if (!claimClient.supports(channelCode)) {
            log.warn("지원하지 않는 채널코드: channelCode={}", channelCode);
            return totalResult;
        }

        List<Shop> shops = shopReadManager.findActiveBySalesChannelId(salesChannelId);
        if (shops.isEmpty()) {
            log.info("활성 Shop 없음: salesChannelId={}", salesChannelId);
            return totalResult;
        }

        for (Shop shop : shops) {
            Instant fromTime = now.minus(DEFAULT_LOOKBACK);

            List<ExternalClaimPayload> payloads =
                    claimClient.fetchClaimChanges(
                            salesChannelId, shop.idValue(), shop.toCredentials(), fromTime, now);

            if (payloads.isEmpty()) {
                continue;
            }

            log.info(
                    "외부 클레임 수신: channelCode={}, salesChannelId={}, shopId={}, count={}",
                    channelCode,
                    salesChannelId,
                    shop.idValue(),
                    payloads.size());

            ClaimSyncResult result = coordinator.syncAll(payloads, salesChannelId);
            totalResult = totalResult.merge(result);
        }

        return totalResult;
    }
}
