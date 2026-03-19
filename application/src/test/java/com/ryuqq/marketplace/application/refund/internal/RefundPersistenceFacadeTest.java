package com.ryuqq.marketplace.application.refund.internal;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundOutboxCommandManager;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
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
@DisplayName("RefundPersistenceFacade 단위 테스트")
class RefundPersistenceFacadeTest {

    @InjectMocks private RefundPersistenceFacade sut;

    @Mock private RefundCommandManager refundCommandManager;
    @Mock private RefundOutboxCommandManager outboxCommandManager;
    @Mock private ClaimHistoryCommandManager historyCommandManager;
    @Mock private OrderItemCommandManager orderItemCommandManager;

    @Nested
    @DisplayName("persistWithOutbox() - 단건 RefundClaim + Outbox 저장")
    class PersistWithOutboxTest {

        @Test
        @DisplayName("RefundClaim과 RefundOutbox를 함께 저장한다")
        void persistWithOutbox_SavesClaimAndOutbox() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();
            RefundOutbox outbox = Mockito.mock(RefundOutbox.class);

            // when
            sut.persistWithOutbox(claim, outbox);

            // then
            then(refundCommandManager).should().persist(claim);
            then(outboxCommandManager).should().persist(outbox);
            then(historyCommandManager).shouldHaveNoInteractions();
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("persistAllWithOutboxes() - RefundClaim 목록 + Outbox 목록 저장")
    class PersistAllWithOutboxesTest {

        @Test
        @DisplayName("RefundClaim 목록과 RefundOutbox 목록을 일괄 저장한다")
        void persistAllWithOutboxes_SavesAllClaimsAndOutboxes() {
            // given
            List<RefundClaim> claims = List.of(RefundFixtures.requestedRefundClaim());
            RefundOutbox outbox = Mockito.mock(RefundOutbox.class);
            List<RefundOutbox> outboxes = List.of(outbox);

            // when
            sut.persistAllWithOutboxes(claims, outboxes);

            // then
            then(refundCommandManager).should().persistAll(claims);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(historyCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("persistAllWithOutboxesAndHistories() - RefundClaim + Outbox + History 저장")
    class PersistAllWithOutboxesAndHistoriesTest {

        @Test
        @DisplayName("RefundClaim, Outbox, History를 일괄 저장한다")
        void persistAllWithOutboxesAndHistories_SavesAll() {
            // given
            List<RefundClaim> claims = List.of(RefundFixtures.requestedRefundClaim());
            RefundOutbox outbox = Mockito.mock(RefundOutbox.class);
            List<RefundOutbox> outboxes = List.of(outbox);
            List<ClaimHistory> histories =
                    List.of(ClaimHistoryFixtures.refundStatusChangeHistory());

            // when
            sut.persistAllWithOutboxesAndHistories(claims, outboxes, histories);

            // then
            then(refundCommandManager).should().persistAll(claims);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(historyCommandManager).should().persistAll(histories);
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName(
            "persistClaimsWithOutboxesAndHistories() - 승인/거절 시 RefundClaim + Outbox + History 저장")
    class PersistClaimsWithOutboxesAndHistoriesTest {

        @Test
        @DisplayName("승인/거절용 RefundClaim, Outbox, History를 저장한다")
        void persistClaimsWithOutboxesAndHistories_SavesAll() {
            // given
            List<RefundClaim> claims = List.of(RefundFixtures.collectingRefundClaim());
            RefundOutbox outbox = Mockito.mock(RefundOutbox.class);
            List<RefundOutbox> outboxes = List.of(outbox);
            List<ClaimHistory> histories =
                    List.of(ClaimHistoryFixtures.refundStatusChangeHistory());

            // when
            sut.persistClaimsWithOutboxesAndHistories(claims, outboxes, histories);

            // then
            then(refundCommandManager).should().persistAll(claims);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(historyCommandManager).should().persistAll(histories);
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName(
            "persistAllWithOutboxesAndHistoriesAndOrderItems() - RefundClaim + Outbox + History +"
                    + " OrderItem 저장")
    class PersistAllWithOutboxesAndHistoriesAndOrderItemsTest {

        @Test
        @DisplayName("RefundClaim, Outbox, History, OrderItem을 모두 일괄 저장한다")
        void persistAllWithOutboxesAndHistoriesAndOrderItems_SavesAll() {
            // given
            List<RefundClaim> claims = List.of(RefundFixtures.requestedRefundClaim());
            RefundOutbox outbox = Mockito.mock(RefundOutbox.class);
            List<RefundOutbox> outboxes = List.of(outbox);
            List<ClaimHistory> histories =
                    List.of(ClaimHistoryFixtures.refundStatusChangeHistory());
            OrderItem orderItem = Mockito.mock(OrderItem.class);
            List<OrderItem> orderItems = List.of(orderItem);

            // when
            sut.persistAllWithOutboxesAndHistoriesAndOrderItems(
                    claims, outboxes, histories, orderItems);

            // then
            then(refundCommandManager).should().persistAll(claims);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(historyCommandManager).should().persistAll(histories);
            then(orderItemCommandManager).should().persistAll(orderItems);
        }
    }
}
