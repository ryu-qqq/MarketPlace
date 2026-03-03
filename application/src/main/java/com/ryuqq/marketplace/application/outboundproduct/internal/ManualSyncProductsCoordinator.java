package com.ryuqq.marketplace.application.outboundproduct.internal;

import com.ryuqq.marketplace.application.outboundproduct.dto.command.ManualSyncProductsCommand;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.ManualSyncResult;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import java.time.Instant;
import java.util.Set;
import org.springframework.stereotype.Component;

/** 수동 상품 외부몰 전송 Coordinator. */
@Component
public class ManualSyncProductsCoordinator {

    private final ManualSyncReadFacade readFacade;
    private final ManualSyncCommandFacade manualSyncCommandFacade;

    public ManualSyncProductsCoordinator(
            ManualSyncReadFacade readFacade, ManualSyncCommandFacade manualSyncCommandFacade) {
        this.readFacade = readFacade;
        this.manualSyncCommandFacade = manualSyncCommandFacade;
    }

    /** 수동 전송 실행. */
    public ManualSyncResult execute(ManualSyncProductsCommand command) {
        ManualSyncContext ctx = readFacade.resolve(command);
        Instant now = Instant.now();

        int createCount = 0;
        int updateCount = 0;
        int skippedCount = 0;

        for (ProductGroup pg : ctx.productGroups()) {
            Set<Long> connectedIds =
                    ctx.connectedChannelIdsBySellerId()
                            .getOrDefault(pg.sellerId().value(), Set.of());

            for (Long channelIdValue : ctx.salesChannelIds()) {
                String key = pg.id().value() + ":" + channelIdValue;

                if (!connectedIds.contains(channelIdValue) || ctx.pendingKeys().contains(key)) {
                    skippedCount++;
                    continue;
                }

                SalesChannelId channelId = SalesChannelId.of(channelIdValue);

                if (ctx.existingProductKeys().contains(key)) {
                    manualSyncCommandFacade.createUpdateOutbox(
                            pg.id(), channelId, pg.sellerId(), now);
                    updateCount++;
                } else {
                    manualSyncCommandFacade.createProductAndOutbox(
                            pg.id(), channelId, pg.sellerId(), now);
                    createCount++;
                }
            }
        }

        return ManualSyncResult.of(createCount, updateCount, skippedCount);
    }
}
