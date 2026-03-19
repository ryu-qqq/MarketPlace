package com.ryuqq.marketplace.application.cancel.internal;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.cancel.manager.CancelCommandManager;
import com.ryuqq.marketplace.application.cancel.manager.CancelOutboxCommandManager;
import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CancelPersistenceFacade 단위 테스트")
class CancelPersistenceFacadeTest {

    @InjectMocks private CancelPersistenceFacade sut;

    @Mock private CancelCommandManager cancelCommandManager;
    @Mock private CancelOutboxCommandManager outboxCommandManager;
    @Mock private ClaimHistoryCommandManager historyCommandManager;
    @Mock private OrderItemCommandManager orderItemCommandManager;
    @Mock private ShipmentCommandManager shipmentCommandManager;

    @Nested
    @DisplayName("persistAll() - CancelPersistenceBundle 일괄 저장")
    class PersistAllTest {

        @Test
        @DisplayName("Cancel + Outbox + History만 포함된 Bundle을 저장한다")
        void persistAll_WithCancelsOutboxesHistories_SavesAll() {
            // given
            List<Cancel> cancels = List.of(CancelFixtures.requestedCancel());
            CancelOutbox outbox = Mockito.mock(CancelOutbox.class);
            List<CancelOutbox> outboxes = List.of(outbox);
            List<ClaimHistory> histories =
                    List.of(ClaimHistoryFixtures.cancelStatusChangeHistory());
            CancelPersistenceBundle bundle =
                    CancelPersistenceBundle.of(cancels, outboxes, histories);

            // when
            sut.persistAll(bundle);

            // then
            then(cancelCommandManager).should().persistAll(cancels);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(historyCommandManager).should().persistAll(histories);
            then(orderItemCommandManager).shouldHaveNoInteractions();
            then(shipmentCommandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("OrderItem + Shipment까지 포함된 Bundle을 저장한다")
        void persistAll_WithOrderItemsAndShipments_SavesAll() {
            // given
            List<Cancel> cancels = List.of(CancelFixtures.approvedCancel());
            CancelOutbox outbox = Mockito.mock(CancelOutbox.class);
            List<CancelOutbox> outboxes = List.of(outbox);
            List<ClaimHistory> histories =
                    List.of(ClaimHistoryFixtures.cancelStatusChangeHistory());
            OrderItem orderItem = Mockito.mock(OrderItem.class);
            List<OrderItem> orderItems = List.of(orderItem);
            Shipment shipment = Mockito.mock(Shipment.class);
            List<Shipment> shipments = List.of(shipment);

            CancelPersistenceBundle bundle =
                    CancelPersistenceBundle.withOrderItemsAndShipments(
                            cancels, outboxes, histories, orderItems, shipments);

            // when
            sut.persistAll(bundle);

            // then
            then(cancelCommandManager).should().persistAll(cancels);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(historyCommandManager).should().persistAll(histories);
            then(orderItemCommandManager).should().persistAll(orderItems);
            then(shipmentCommandManager).should().persistAll(shipments);
        }

        @Test
        @DisplayName("빈 OrderItem/Shipment 목록이면 해당 Manager를 호출하지 않는다")
        void persistAll_EmptyOptionalLists_SkipsEmptyManagers() {
            // given
            List<Cancel> cancels = List.of(CancelFixtures.requestedCancel());
            CancelOutbox outbox = Mockito.mock(CancelOutbox.class);
            List<CancelOutbox> outboxes = List.of(outbox);

            CancelPersistenceBundle bundle =
                    CancelPersistenceBundle.of(cancels, outboxes, List.of());

            // when
            sut.persistAll(bundle);

            // then
            then(cancelCommandManager).should().persistAll(cancels);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(historyCommandManager).shouldHaveNoInteractions();
            then(orderItemCommandManager).shouldHaveNoInteractions();
            then(shipmentCommandManager).shouldHaveNoInteractions();
        }
    }
}
