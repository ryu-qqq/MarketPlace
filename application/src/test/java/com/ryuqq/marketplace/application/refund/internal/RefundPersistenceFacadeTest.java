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
    @DisplayName("persistAll() - RefundPersistenceBundle 일괄 저장")
    class PersistAllTest {

        @Test
        @DisplayName("claims, outboxes, histories만 있는 번들을 저장한다")
        void persistAll_WithoutOrderItems_SavesClaimsOutboxesHistories() {
            // given
            List<RefundClaim> claims = List.of(RefundFixtures.requestedRefundClaim());
            RefundOutbox outbox = Mockito.mock(RefundOutbox.class);
            List<RefundOutbox> outboxes = List.of(outbox);
            List<ClaimHistory> histories =
                    List.of(ClaimHistoryFixtures.refundStatusChangeHistory());

            RefundPersistenceBundle bundle = RefundPersistenceBundle.of(claims, outboxes, histories);

            // when
            sut.persistAll(bundle);

            // then
            then(refundCommandManager).should().persistAll(claims);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(historyCommandManager).should().persistAll(histories);
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("histories가 비어있으면 historyCommandManager를 호출하지 않는다")
        void persistAll_EmptyHistories_SkipsHistoryManager() {
            // given
            List<RefundClaim> claims = List.of(RefundFixtures.requestedRefundClaim());
            RefundOutbox outbox = Mockito.mock(RefundOutbox.class);
            List<RefundOutbox> outboxes = List.of(outbox);

            RefundPersistenceBundle bundle =
                    RefundPersistenceBundle.of(claims, outboxes, List.of());

            // when
            sut.persistAll(bundle);

            // then
            then(refundCommandManager).should().persistAll(claims);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(historyCommandManager).shouldHaveNoInteractions();
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("OrderItem이 포함된 번들을 저장한다")
        void persistAll_WithOrderItems_SavesAll() {
            // given
            List<RefundClaim> claims = List.of(RefundFixtures.requestedRefundClaim());
            RefundOutbox outbox = Mockito.mock(RefundOutbox.class);
            List<RefundOutbox> outboxes = List.of(outbox);
            List<ClaimHistory> histories =
                    List.of(ClaimHistoryFixtures.refundStatusChangeHistory());
            OrderItem orderItem = Mockito.mock(OrderItem.class);
            List<OrderItem> orderItems = List.of(orderItem);

            RefundPersistenceBundle bundle =
                    RefundPersistenceBundle.withOrderItems(claims, outboxes, histories, orderItems);

            // when
            sut.persistAll(bundle);

            // then
            then(refundCommandManager).should().persistAll(claims);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(historyCommandManager).should().persistAll(histories);
            then(orderItemCommandManager).should().persistAll(orderItems);
        }
    }
}
