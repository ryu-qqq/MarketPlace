package com.ryuqq.marketplace.application.outboundproduct.internal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductCommandManager;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxCommandManager;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ManualSyncCommandFacade 단위 테스트")
class ManualSyncCommandFacadeTest {

    @InjectMocks private ManualSyncCommandFacade sut;
    @Mock private OutboundProductCommandManager outboundProductCommandManager;
    @Mock private OutboundSyncOutboxCommandManager outboxCommandManager;

    private static final ProductGroupId PG_ID = ProductGroupId.of(100L);
    private static final SalesChannelId CHANNEL_ID = SalesChannelId.of(10L);
    private static final SellerId SELLER_ID = SellerId.of(1L);
    private static final Instant NOW = Instant.parse("2026-03-04T00:00:00Z");

    @Nested
    @DisplayName("createProductAndOutbox() - CREATE 처리")
    class CreateProductAndOutboxTest {

        @Test
        @DisplayName("OutboundProduct와 OutboundSyncOutbox를 모두 영속화한다")
        void createProductAndOutbox_PersistsBoth() {
            // when
            sut.createProductAndOutbox(PG_ID, CHANNEL_ID, SELLER_ID, NOW);

            // then
            then(outboundProductCommandManager).should().persist(any(OutboundProduct.class));
            then(outboxCommandManager).should().persist(any(OutboundSyncOutbox.class));
        }
    }

    @Nested
    @DisplayName("createUpdateOutbox() - UPDATE 처리")
    class CreateUpdateOutboxTest {

        @Test
        @DisplayName("OutboundSyncOutbox만 영속화하고 OutboundProduct는 생성하지 않는다")
        void createUpdateOutbox_PersistsOnlyOutbox() {
            // when
            sut.createUpdateOutbox(PG_ID, CHANNEL_ID, SELLER_ID, NOW);

            // then
            then(outboxCommandManager).should().persist(any(OutboundSyncOutbox.class));
            then(outboundProductCommandManager).shouldHaveNoInteractions();
        }
    }
}
