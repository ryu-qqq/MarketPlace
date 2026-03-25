package com.ryuqq.marketplace.application.shipment.service.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.claimsync.ClaimSyncFixtures;
import com.ryuqq.marketplace.application.claimsync.manager.ExternalOrderItemMappingReadManager;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.shipment.dto.command.ExecuteShipmentOutboxCommand;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentSyncStrategyProvider;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxReadManager;
import com.ryuqq.marketplace.application.shipment.port.out.client.ShipmentSyncStrategy;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;
import com.ryuqq.marketplace.domain.shipment.outbox.ShipmentOutboxFixtures;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
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
@DisplayName("ExecuteShipmentOutboxService 단위 테스트")
class ExecuteShipmentOutboxServiceTest {

    @InjectMocks private ExecuteShipmentOutboxService sut;

    @Mock private ShipmentOutboxReadManager outboxReadManager;
    @Mock private ShipmentOutboxCommandManager outboxCommandManager;
    @Mock private ShipmentSyncStrategyProvider strategyProvider;
    @Mock private ExternalOrderItemMappingReadManager mappingReadManager;
    @Mock private ShipmentCommandFactory commandFactory;
    @Mock private ShopReadManager shopReadManager;

    private static final Long OUTBOX_ID = 1L;
    private static final String ORDER_ITEM_ID = "01940001-0000-7000-8000-000000000001";
    private static final String CHANNEL_CODE = "NAVER";

    private ExecuteShipmentOutboxCommand defaultCommand() {
        return ExecuteShipmentOutboxCommand.of(OUTBOX_ID, ORDER_ITEM_ID, "SHIP");
    }

    @Nested
    @DisplayName("execute() - 배송 Outbox 실행")
    class ExecuteTest {

        @Test
        @DisplayName("외부 동기화가 성공하면 Outbox를 COMPLETED 상태로 업데이트한다")
        void execute_SyncSuccess_CompletesOutbox() {
            // given
            ExecuteShipmentOutboxCommand command = defaultCommand();
            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            ShipmentOutbox freshOutbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            ExternalOrderItemMapping mapping = ClaimSyncFixtures.defaultMapping();
            ShipmentSyncStrategy strategy = org.mockito.Mockito.mock(ShipmentSyncStrategy.class);

            given(outboxReadManager.getById(OUTBOX_ID)).willReturn(outbox).willReturn(freshOutbox);
            given(mappingReadManager.findByOrderItemId(ORDER_ITEM_ID)).willReturn(mapping);
            given(strategyProvider.getStrategy(CHANNEL_CODE)).willReturn(strategy);
            given(shopReadManager.findActiveBySalesChannelId(anyLong())).willReturn(List.of());
            given(strategy.execute(any(ShipmentOutbox.class), any())).willReturn(OutboxSyncResult.success());

            // when
            sut.execute(command);

            // then
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("외부 동기화가 실패하면 Outbox를 FAILED 상태로 업데이트한다")
        void execute_SyncFailure_RecordsFailure() {
            // given
            ExecuteShipmentOutboxCommand command = defaultCommand();
            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            ShipmentOutbox freshOutbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            ExternalOrderItemMapping mapping = ClaimSyncFixtures.defaultMapping();
            ShipmentSyncStrategy strategy = org.mockito.Mockito.mock(ShipmentSyncStrategy.class);

            given(outboxReadManager.getById(OUTBOX_ID)).willReturn(outbox).willReturn(freshOutbox);
            given(mappingReadManager.findByOrderItemId(ORDER_ITEM_ID)).willReturn(mapping);
            given(strategyProvider.getStrategy(CHANNEL_CODE)).willReturn(strategy);
            given(shopReadManager.findActiveBySalesChannelId(anyLong())).willReturn(List.of());
            given(strategy.execute(any(ShipmentOutbox.class), any()))
                    .willReturn(OutboxSyncResult.failure(true, "외부 API 응답 오류"));

            // when
            sut.execute(command);

            // then
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("외부 서비스 일시 장애 시 Outbox를 PENDING으로 복구한다")
        void execute_ExternalServiceUnavailable_RecoversToPending() {
            // given
            ExecuteShipmentOutboxCommand command = defaultCommand();
            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            ShipmentOutbox freshOutbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            ExternalOrderItemMapping mapping = ClaimSyncFixtures.defaultMapping();
            ShipmentSyncStrategy strategy = org.mockito.Mockito.mock(ShipmentSyncStrategy.class);

            given(outboxReadManager.getById(OUTBOX_ID)).willReturn(outbox).willReturn(freshOutbox);
            given(mappingReadManager.findByOrderItemId(ORDER_ITEM_ID)).willReturn(mapping);
            given(strategyProvider.getStrategy(CHANNEL_CODE)).willReturn(strategy);
            given(shopReadManager.findActiveBySalesChannelId(anyLong())).willReturn(List.of());
            given(strategy.execute(any(ShipmentOutbox.class), any()))
                    .willThrow(new ExternalServiceUnavailableException("외부 서비스 일시 장애"));

            // when
            sut.execute(command);

            // then
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("예기치 않은 예외 발생 시 re-read 후 실패 상태로 업데이트한다")
        void execute_UnexpectedException_RecordsFailureWithReRead() {
            // given
            ExecuteShipmentOutboxCommand command = defaultCommand();
            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            ShipmentOutbox freshOutbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            ExternalOrderItemMapping mapping = ClaimSyncFixtures.defaultMapping();
            ShipmentSyncStrategy strategy = org.mockito.Mockito.mock(ShipmentSyncStrategy.class);

            given(outboxReadManager.getById(OUTBOX_ID)).willReturn(outbox).willReturn(freshOutbox);
            given(mappingReadManager.findByOrderItemId(ORDER_ITEM_ID)).willReturn(mapping);
            given(strategyProvider.getStrategy(CHANNEL_CODE)).willReturn(strategy);
            given(shopReadManager.findActiveBySalesChannelId(anyLong())).willReturn(List.of());
            given(strategy.execute(any(ShipmentOutbox.class), any()))
                    .willThrow(new RuntimeException("예기치 않은 오류"));

            // when
            sut.execute(command);

            // then
            then(outboxCommandManager).should().persist(any(ShipmentOutbox.class));
        }

        @Test
        @DisplayName("외부 주문 매핑이 없으면 실패 상태로 업데이트한다")
        void execute_MappingNotFound_RecordsFailure() {
            // given
            ExecuteShipmentOutboxCommand command = defaultCommand();
            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            ShipmentOutbox freshOutbox = ShipmentOutboxFixtures.processingShipmentOutbox();

            given(outboxReadManager.getById(OUTBOX_ID)).willReturn(outbox).willReturn(freshOutbox);
            given(mappingReadManager.findByOrderItemId(ORDER_ITEM_ID)).willReturn(null);

            // when
            sut.execute(command);

            // then
            then(outboxCommandManager).should().persist(any(ShipmentOutbox.class));
            then(strategyProvider).shouldHaveNoInteractions();
        }
    }
}
