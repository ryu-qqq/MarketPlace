package com.ryuqq.marketplace.application.outboundproduct.internal;

import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductCommandManager;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxCommandManager;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 수동 전송 시 OutboundProduct + OutboundSyncOutbox 쓰기를 트랜잭션으로 묶는 Write Facade. */
@Component
public class ManualSyncCommandFacade {

    private final OutboundProductCommandManager outboundProductCommandManager;
    private final OutboundSyncOutboxCommandManager outboxCommandManager;

    public ManualSyncCommandFacade(
            OutboundProductCommandManager outboundProductCommandManager,
            OutboundSyncOutboxCommandManager outboxCommandManager) {
        this.outboundProductCommandManager = outboundProductCommandManager;
        this.outboxCommandManager = outboxCommandManager;
    }

    /** CREATE: OutboundProduct 신규 생성 + OutboundSyncOutbox 생성을 하나의 트랜잭션으로 처리. */
    @Transactional
    public void createProductAndOutbox(
            ProductGroupId productGroupId,
            SalesChannelId salesChannelId,
            SellerId sellerId,
            Instant now) {
        outboundProductCommandManager.persist(
                OutboundProduct.forNew(productGroupId, salesChannelId, now));
        outboxCommandManager.persist(
                OutboundSyncOutbox.forNew(
                        productGroupId, salesChannelId, sellerId, SyncType.CREATE, "{}", now));
    }

    /** UPDATE: OutboundSyncOutbox만 생성. */
    @Transactional
    public void createUpdateOutbox(
            ProductGroupId productGroupId,
            SalesChannelId salesChannelId,
            SellerId sellerId,
            Instant now) {
        outboxCommandManager.persist(
                OutboundSyncOutbox.forNew(
                        productGroupId, salesChannelId, sellerId, SyncType.UPDATE, "{}", now));
    }
}
