package com.ryuqq.marketplace.application.exchange.internal;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryCommandManager;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeCommandManager;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeOutboxCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
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
@DisplayName("ExchangePersistenceFacade 단위 테스트")
class ExchangePersistenceFacadeTest {

    @InjectMocks private ExchangePersistenceFacade sut;

    @Mock private ExchangeCommandManager exchangeCommandManager;
    @Mock private ExchangeOutboxCommandManager outboxCommandManager;
    @Mock private ClaimHistoryCommandManager historyCommandManager;
    @Mock private OrderItemCommandManager orderItemCommandManager;

    @Nested
    @DisplayName("persistAll() - Bundle 기반 일괄 저장")
    class PersistAllTest {

        @Test
        @DisplayName("Claim + History만 있는 Bundle을 저장한다")
        void persistAll_WithoutOutboxes_SavesClaimsAndHistories() {
            // given
            List<ExchangeClaim> claims = List.of(ExchangeFixtures.requestedExchangeClaim());
            List<ClaimHistory> histories = List.of(Mockito.mock(ClaimHistory.class));
            ExchangePersistenceBundle bundle = ExchangePersistenceBundle.withoutOutboxes(claims, histories);

            // when
            sut.persistAll(bundle);

            // then
            then(exchangeCommandManager).should().persistAll(claims);
            then(historyCommandManager).should().persistAll(histories);
            then(outboxCommandManager).shouldHaveNoInteractions();
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Claim + Outbox + History Bundle을 저장한다")
        void persistAll_WithOutboxes_SavesAll() {
            // given
            List<ExchangeClaim> claims = List.of(ExchangeFixtures.collectingExchangeClaim());
            List<ExchangeOutbox> outboxes = List.of(Mockito.mock(ExchangeOutbox.class));
            List<ClaimHistory> histories = List.of(Mockito.mock(ClaimHistory.class));
            ExchangePersistenceBundle bundle = ExchangePersistenceBundle.of(claims, outboxes, histories);

            // when
            sut.persistAll(bundle);

            // then
            then(exchangeCommandManager).should().persistAll(claims);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(historyCommandManager).should().persistAll(histories);
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Claim + History + OrderItem Bundle을 저장한다")
        void persistAll_WithOrderItems_SavesAll() {
            // given
            List<ExchangeClaim> claims = List.of(ExchangeFixtures.newExchangeClaim());
            List<ClaimHistory> histories = List.of(Mockito.mock(ClaimHistory.class));
            List<OrderItem> orderItems = List.of(Mockito.mock(OrderItem.class));
            ExchangePersistenceBundle bundle = ExchangePersistenceBundle.withOrderItems(claims, histories, orderItems);

            // when
            sut.persistAll(bundle);

            // then
            then(exchangeCommandManager).should().persistAll(claims);
            then(historyCommandManager).should().persistAll(histories);
            then(orderItemCommandManager).should().persistAll(orderItems);
            then(outboxCommandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("빈 Outbox/History/OrderItem은 Manager 호출을 건너뛴다")
        void persistAll_EmptyCollections_SkipsEmptyManagers() {
            // given
            List<ExchangeClaim> claims = List.of(ExchangeFixtures.requestedExchangeClaim());
            ExchangePersistenceBundle bundle = new ExchangePersistenceBundle(claims, List.of(), List.of(), List.of());

            // when
            sut.persistAll(bundle);

            // then
            then(exchangeCommandManager).should().persistAll(claims);
            then(outboxCommandManager).shouldHaveNoInteractions();
            then(historyCommandManager).shouldHaveNoInteractions();
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }
    }
}
