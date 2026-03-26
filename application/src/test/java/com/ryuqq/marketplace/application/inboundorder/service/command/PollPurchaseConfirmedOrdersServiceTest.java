package com.ryuqq.marketplace.application.inboundorder.service.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.ReceivePurchaseConfirmedWebhookUseCase;
import com.ryuqq.marketplace.application.inboundorder.port.out.client.SalesChannelPurchaseConfirmedClient;
import com.ryuqq.marketplace.application.saleschannel.manager.SalesChannelReadManager;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.saleschannel.SalesChannelFixtures;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.shop.ShopFixtures;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.vo.ShopCredentials;
import java.time.Instant;
import java.util.List;
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
@DisplayName("PollPurchaseConfirmedOrdersService 단위 테스트")
class PollPurchaseConfirmedOrdersServiceTest {

    @InjectMocks private PollPurchaseConfirmedOrdersService sut;

    @Mock private SalesChannelReadManager salesChannelReadManager;
    @Mock private ShopReadManager shopReadManager;
    @Mock private SalesChannelPurchaseConfirmedClient purchaseConfirmedClient;
    @Mock private ReceivePurchaseConfirmedWebhookUseCase receivePurchaseConfirmedWebhookUseCase;
    @Mock private TimeProvider timeProvider;

    private static final long DEFAULT_SALES_CHANNEL_ID = 1L;
    private static final Instant NOW = Instant.parse("2026-03-25T10:00:00Z");

    @Nested
    @DisplayName("execute() - 구매확정 폴링")
    class ExecuteTest {

        @Test
        @DisplayName("지원하지 않는 채널이면 fetchPurchaseConfirmedProductOrderIds를 호출하지 않는다")
        void execute_UnsupportedChannel_SkipsFetch() {
            // given
            SalesChannel salesChannel =
                    SalesChannelFixtures.activeSalesChannel(DEFAULT_SALES_CHANNEL_ID, "SELLIC");
            given(salesChannelReadManager.getById(SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID)))
                    .willReturn(salesChannel);
            given(timeProvider.now()).willReturn(NOW);
            given(purchaseConfirmedClient.supports("SELLIC")).willReturn(false);

            // when
            sut.execute(DEFAULT_SALES_CHANNEL_ID);

            // then
            then(purchaseConfirmedClient)
                    .should(never())
                    .fetchPurchaseConfirmedProductOrderIds(
                            anyLong(), anyLong(), any(ShopCredentials.class), any(), any());
            then(shopReadManager).shouldHaveNoInteractions();
            then(receivePurchaseConfirmedWebhookUseCase).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("활성 Shop이 없으면 fetch를 호출하지 않는다")
        void execute_NoActiveShops_SkipsFetch() {
            // given
            SalesChannel salesChannel =
                    SalesChannelFixtures.activeSalesChannel(DEFAULT_SALES_CHANNEL_ID, "NAVER");
            given(salesChannelReadManager.getById(SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID)))
                    .willReturn(salesChannel);
            given(timeProvider.now()).willReturn(NOW);
            given(purchaseConfirmedClient.supports("NAVER")).willReturn(true);
            given(shopReadManager.findActiveBySalesChannelId(DEFAULT_SALES_CHANNEL_ID))
                    .willReturn(List.of());

            // when
            sut.execute(DEFAULT_SALES_CHANNEL_ID);

            // then
            then(purchaseConfirmedClient)
                    .should(never())
                    .fetchPurchaseConfirmedProductOrderIds(
                            anyLong(), anyLong(), any(ShopCredentials.class), any(), any());
            then(receivePurchaseConfirmedWebhookUseCase).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("정상 폴링: productOrderIds 3건이면 webhook UseCase를 호출한다")
        void execute_ThreeProductOrderIds_CallsWebhookUseCase() {
            // given
            SalesChannel salesChannel =
                    SalesChannelFixtures.activeSalesChannel(DEFAULT_SALES_CHANNEL_ID, "NAVER");
            Shop shop = ShopFixtures.activeShopWithCredentials(100L);
            List<String> productOrderIds = List.of("PO-001", "PO-002", "PO-003");

            given(timeProvider.now()).willReturn(NOW);
            given(salesChannelReadManager.getById(SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID)))
                    .willReturn(salesChannel);
            given(purchaseConfirmedClient.supports("NAVER")).willReturn(true);
            given(shopReadManager.findActiveBySalesChannelId(DEFAULT_SALES_CHANNEL_ID))
                    .willReturn(List.of(shop));
            given(
                            purchaseConfirmedClient.fetchPurchaseConfirmedProductOrderIds(
                                    eq(DEFAULT_SALES_CHANNEL_ID),
                                    eq(shop.idValue()),
                                    any(ShopCredentials.class),
                                    any(Instant.class),
                                    eq(NOW)))
                    .willReturn(productOrderIds);

            // when
            sut.execute(DEFAULT_SALES_CHANNEL_ID);

            // then
            then(receivePurchaseConfirmedWebhookUseCase)
                    .should()
                    .execute(DEFAULT_SALES_CHANNEL_ID, productOrderIds);
        }

        @Test
        @DisplayName("폴링 결과가 빈 목록이면 webhook UseCase를 호출하지 않는다")
        void execute_EmptyPollingResult_SkipsWebhookUseCase() {
            // given
            SalesChannel salesChannel =
                    SalesChannelFixtures.activeSalesChannel(DEFAULT_SALES_CHANNEL_ID, "NAVER");
            Shop shop = ShopFixtures.activeShopWithCredentials(100L);

            given(timeProvider.now()).willReturn(NOW);
            given(salesChannelReadManager.getById(SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID)))
                    .willReturn(salesChannel);
            given(purchaseConfirmedClient.supports("NAVER")).willReturn(true);
            given(shopReadManager.findActiveBySalesChannelId(DEFAULT_SALES_CHANNEL_ID))
                    .willReturn(List.of(shop));
            given(
                            purchaseConfirmedClient.fetchPurchaseConfirmedProductOrderIds(
                                    eq(DEFAULT_SALES_CHANNEL_ID),
                                    eq(shop.idValue()),
                                    any(ShopCredentials.class),
                                    any(Instant.class),
                                    eq(NOW)))
                    .willReturn(List.of());

            // when
            sut.execute(DEFAULT_SALES_CHANNEL_ID);

            // then
            then(receivePurchaseConfirmedWebhookUseCase).shouldHaveNoInteractions();
        }
    }
}
