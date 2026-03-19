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
    @DisplayName("persistWithOutbox() - 단건 Cancel + Outbox 저장")
    class PersistWithOutboxTest {

        @Test
        @DisplayName("Cancel과 CancelOutbox를 함께 저장한다")
        void persistWithOutbox_SavesCancelAndOutbox() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();
            CancelOutbox outbox = Mockito.mock(CancelOutbox.class);

            // when
            sut.persistWithOutbox(cancel, outbox);

            // then
            then(cancelCommandManager).should().persist(cancel);
            then(outboxCommandManager).should().persist(outbox);
            then(historyCommandManager).shouldHaveNoInteractions();
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("persistAllWithOutboxes() - Cancel 목록 + Outbox 목록 저장")
    class PersistAllWithOutboxesTest {

        @Test
        @DisplayName("Cancel 목록과 CancelOutbox 목록을 일괄 저장한다")
        void persistAllWithOutboxes_SavesAllCancelsAndOutboxes() {
            // given
            List<Cancel> cancels = List.of(CancelFixtures.requestedCancel());
            CancelOutbox outbox = Mockito.mock(CancelOutbox.class);
            List<CancelOutbox> outboxes = List.of(outbox);

            // when
            sut.persistAllWithOutboxes(cancels, outboxes);

            // then
            then(cancelCommandManager).should().persistAll(cancels);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(historyCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("persistAllWithOutboxesAndHistories() - Cancel + Outbox + History 저장")
    class PersistAllWithOutboxesAndHistoriesTest {

        @Test
        @DisplayName("Cancel, Outbox, History를 일괄 저장한다")
        void persistAllWithOutboxesAndHistories_SavesAll() {
            // given
            List<Cancel> cancels = List.of(CancelFixtures.requestedCancel());
            CancelOutbox outbox = Mockito.mock(CancelOutbox.class);
            List<CancelOutbox> outboxes = List.of(outbox);
            List<ClaimHistory> histories =
                    List.of(ClaimHistoryFixtures.cancelStatusChangeHistory());

            // when
            sut.persistAllWithOutboxesAndHistories(cancels, outboxes, histories);

            // then
            then(cancelCommandManager).should().persistAll(cancels);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(historyCommandManager).should().persistAll(histories);
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName(
            "persistAllWithOutboxesAndHistoriesAndOrderItems() - Cancel + Outbox + History +"
                    + " OrderItem 저장")
    class PersistAllWithOutboxesAndHistoriesAndOrderItemsTest {

        @Test
        @DisplayName("Cancel, Outbox, History, OrderItem을 모두 일괄 저장한다")
        void persistAllWithOutboxesAndHistoriesAndOrderItems_SavesAll() {
            // given
            List<Cancel> cancels = List.of(CancelFixtures.requestedCancel());
            CancelOutbox outbox = Mockito.mock(CancelOutbox.class);
            List<CancelOutbox> outboxes = List.of(outbox);
            List<ClaimHistory> histories =
                    List.of(ClaimHistoryFixtures.cancelStatusChangeHistory());
            OrderItem orderItem = Mockito.mock(OrderItem.class);
            List<OrderItem> orderItems = List.of(orderItem);

            // when
            sut.persistAllWithOutboxesAndHistoriesAndOrderItems(
                    cancels, outboxes, histories, orderItems);

            // then
            then(cancelCommandManager).should().persistAll(cancels);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(historyCommandManager).should().persistAll(histories);
            then(orderItemCommandManager).should().persistAll(orderItems);
        }
    }

    @Nested
    @DisplayName("persistCancelsWithOutboxesAndHistories() - 승인/거절 시 Cancel + Outbox + History 저장")
    class PersistCancelsWithOutboxesAndHistoriesTest {

        @Test
        @DisplayName("승인/거절용 Cancel, Outbox, History를 저장한다")
        void persistCancelsWithOutboxesAndHistories_SavesAll() {
            // given
            List<Cancel> cancels = List.of(CancelFixtures.approvedCancel());
            CancelOutbox outbox = Mockito.mock(CancelOutbox.class);
            List<CancelOutbox> outboxes = List.of(outbox);
            List<ClaimHistory> histories =
                    List.of(ClaimHistoryFixtures.cancelStatusChangeHistory());

            // when
            sut.persistCancelsWithOutboxesAndHistories(cancels, outboxes, histories);

            // then
            then(cancelCommandManager).should().persistAll(cancels);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(historyCommandManager).should().persistAll(histories);
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("persistCancelsWithOutboxesAndHistoriesAndOrderItems() - 승인 시 OrderItem 포함 저장")
    class PersistCancelsWithOutboxesAndHistoriesAndOrderItemsTest {

        @Test
        @DisplayName("승인 시 Cancel, Outbox, History, OrderItem을 모두 저장한다")
        void persistCancelsWithOutboxesAndHistoriesAndOrderItems_SavesAll() {
            // given
            List<Cancel> cancels = List.of(CancelFixtures.approvedCancel());
            CancelOutbox outbox = Mockito.mock(CancelOutbox.class);
            List<CancelOutbox> outboxes = List.of(outbox);
            List<ClaimHistory> histories =
                    List.of(ClaimHistoryFixtures.cancelStatusChangeHistory());
            OrderItem orderItem = Mockito.mock(OrderItem.class);
            List<OrderItem> orderItems = List.of(orderItem);

            // when
            sut.persistCancelsWithOutboxesAndHistoriesAndOrderItems(
                    cancels, outboxes, histories, orderItems);

            // then
            then(cancelCommandManager).should().persistAll(cancels);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(historyCommandManager).should().persistAll(histories);
            then(orderItemCommandManager).should().persistAll(orderItems);
        }
    }
}
