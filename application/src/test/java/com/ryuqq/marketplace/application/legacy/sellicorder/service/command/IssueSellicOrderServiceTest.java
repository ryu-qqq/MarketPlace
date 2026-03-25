package com.ryuqq.marketplace.application.legacy.sellicorder.service.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderItemPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.manager.SalesChannelOrderClientManager;
import com.ryuqq.marketplace.application.inboundorder.port.out.client.SalesChannelOrderClient;
import com.ryuqq.marketplace.application.legacy.sellicorder.internal.SellicLegacyOrderCoordinator;
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
@DisplayName("IssueSellicOrderService 단위 테스트")
class IssueSellicOrderServiceTest {

    private static final long SALES_CHANNEL_ID = 16L;
    private static final int BATCH_SIZE = 100;

    @InjectMocks private IssueSellicOrderService sut;

    @Mock private SalesChannelReadManager salesChannelReadManager;
    @Mock private ShopReadManager shopReadManager;
    @Mock private SalesChannelOrderClientManager orderClientManager;
    @Mock private SellicLegacyOrderCoordinator coordinator;

    // ===== 헬퍼 메서드 =====

    private static ExternalOrderItemPayload createItemPayload(String externalProductOrderId) {
        return new ExternalOrderItemPayload(
                externalProductOrderId,
                "OWN-001",
                "OPT-001",
                "테스트 상품",
                "옵션 A",
                "https://img.example.com/1.jpg",
                50000,
                1,
                50000,
                0,
                0,
                50000,
                "홍길동",
                "010-1234-5678",
                "12345",
                "서울특별시 강남구",
                "101동 201호",
                "부재시 경비실",
                "PAYED");
    }

    private static ExternalOrderPayload createOrderPayload(String externalOrderNo) {
        return new ExternalOrderPayload(
                externalOrderNo,
                Instant.parse("2026-03-25T10:00:00Z"),
                "구매자",
                "buyer@test.com",
                "010-0000-0000",
                "CARD",
                50000,
                Instant.parse("2026-03-25T10:00:00Z"),
                List.of(createItemPayload("100")));
    }

    @Nested
    @DisplayName("execute() - 셀릭 주문 폴링 및 발행")
    class ExecuteTest {

        @Test
        @DisplayName("Shop 1개에 주문 2건이면 coordinator를 2회 호출한다")
        void execute_OneShopTwoOrders_CallsCoordinatorTwice() {
            // given
            SalesChannel salesChannel =
                    SalesChannelFixtures.activeSalesChannel(SALES_CHANNEL_ID, "SELLIC");
            Shop shop = ShopFixtures.activeShopWithCredentials(10L);

            ExternalOrderPayload order1 = createOrderPayload("ORD-001");
            ExternalOrderPayload order2 = createOrderPayload("ORD-002");

            given(salesChannelReadManager.getById(SalesChannelId.of(SALES_CHANNEL_ID)))
                    .willReturn(salesChannel);
            given(orderClientManager.supports("SELLIC")).willReturn(true);

            SalesChannelOrderClient mockClient = givenMockClient("SELLIC");
            given(mockClient.fetchNewOrders(
                            eq(SALES_CHANNEL_ID),
                            eq(10L),
                            any(ShopCredentials.class),
                            any(Instant.class),
                            any(Instant.class)))
                    .willReturn(List.of(order1, order2));

            given(shopReadManager.findActiveBySalesChannelId(SALES_CHANNEL_ID))
                    .willReturn(List.of(shop));
            given(coordinator.issueIfNotDuplicate(
                            any(ExternalOrderPayload.class), eq(SALES_CHANNEL_ID), any(Instant.class)))
                    .willReturn(true);

            // when
            sut.execute(SALES_CHANNEL_ID, BATCH_SIZE);

            // then
            then(coordinator)
                    .should(times(2))
                    .issueIfNotDuplicate(
                            any(ExternalOrderPayload.class),
                            eq(SALES_CHANNEL_ID),
                            any(Instant.class));
        }

        @Test
        @DisplayName("활성 Shop이 없으면 coordinator를 호출하지 않는다")
        void execute_NoActiveShops_DoesNotCallCoordinator() {
            // given
            SalesChannel salesChannel =
                    SalesChannelFixtures.activeSalesChannel(SALES_CHANNEL_ID, "SELLIC");

            given(salesChannelReadManager.getById(SalesChannelId.of(SALES_CHANNEL_ID)))
                    .willReturn(salesChannel);
            given(orderClientManager.supports("SELLIC")).willReturn(true);
            givenMockClient("SELLIC");
            given(shopReadManager.findActiveBySalesChannelId(SALES_CHANNEL_ID))
                    .willReturn(List.of());

            // when
            sut.execute(SALES_CHANNEL_ID, BATCH_SIZE);

            // then
            then(coordinator)
                    .should(never())
                    .issueIfNotDuplicate(any(), anyLong(), any());
        }

        @Test
        @DisplayName("지원하지 않는 채널코드면 coordinator를 호출하지 않는다")
        void execute_UnsupportedChannelCode_DoesNotCallCoordinator() {
            // given
            SalesChannel salesChannel =
                    SalesChannelFixtures.activeSalesChannel(SALES_CHANNEL_ID, "UNKNOWN_CHANNEL");

            given(salesChannelReadManager.getById(SalesChannelId.of(SALES_CHANNEL_ID)))
                    .willReturn(salesChannel);
            given(orderClientManager.supports("UNKNOWN_CHANNEL")).willReturn(false);

            // when
            sut.execute(SALES_CHANNEL_ID, BATCH_SIZE);

            // then
            then(coordinator)
                    .should(never())
                    .issueIfNotDuplicate(any(), anyLong(), any());
            then(shopReadManager).should(never()).findActiveBySalesChannelId(anyLong());
        }

        // ===== private 헬퍼 =====

        private SalesChannelOrderClient givenMockClient(String channelCode) {
            SalesChannelOrderClient mockClient =
                    org.mockito.Mockito.mock(SalesChannelOrderClient.class);
            given(orderClientManager.getClient(channelCode)).willReturn(mockClient);
            return mockClient;
        }
    }
}
