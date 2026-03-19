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
    @DisplayName("persistClaimsWithHistories() - Claim + History 저장")
    class PersistClaimsWithHistoriesTest {

        @Test
        @DisplayName("Claim 목록과 History 목록을 함께 저장한다")
        void persistClaimsWithHistories_SavesClaimsAndHistories() {
            // given
            List<ExchangeClaim> claims = List.of(ExchangeFixtures.requestedExchangeClaim());
            List<ClaimHistory> histories = List.of(Mockito.mock(ClaimHistory.class));

            // when
            sut.persistClaimsWithHistories(claims, histories);

            // then
            then(exchangeCommandManager).should().persistAll(claims);
            then(historyCommandManager).should().persistAll(histories);
            then(outboxCommandManager).shouldHaveNoInteractions();
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("persistClaimsWithOutboxesAndHistories() - Claim + Outbox + History 저장")
    class PersistClaimsWithOutboxesAndHistoriesTest {

        @Test
        @DisplayName("Claim, Outbox, History 목록을 함께 저장한다")
        void persistClaimsWithOutboxesAndHistories_SavesAll() {
            // given
            List<ExchangeClaim> claims = List.of(ExchangeFixtures.collectingExchangeClaim());
            List<ExchangeOutbox> outboxes = List.of(Mockito.mock(ExchangeOutbox.class));
            List<ClaimHistory> histories = List.of(Mockito.mock(ClaimHistory.class));

            // when
            sut.persistClaimsWithOutboxesAndHistories(claims, outboxes, histories);

            // then
            then(exchangeCommandManager).should().persistAll(claims);
            then(outboxCommandManager).should().persistAll(outboxes);
            then(historyCommandManager).should().persistAll(histories);
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("persistAllWithHistoriesAndOrderItems() - Claim + History + OrderItem 저장")
    class PersistAllWithHistoriesAndOrderItemsTest {

        @Test
        @DisplayName("Claim, History, OrderItem 목록을 함께 저장한다")
        void persistAllWithHistoriesAndOrderItems_SavesAll() {
            // given
            List<ExchangeClaim> claims = List.of(ExchangeFixtures.newExchangeClaim());
            List<ClaimHistory> histories = List.of(Mockito.mock(ClaimHistory.class));
            List<OrderItem> orderItems = List.of(Mockito.mock(OrderItem.class));

            // when
            sut.persistAllWithHistoriesAndOrderItems(claims, histories, orderItems);

            // then
            then(exchangeCommandManager).should().persistAll(claims);
            then(historyCommandManager).should().persistAll(histories);
            then(orderItemCommandManager).should().persistAll(orderItems);
            then(outboxCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("persistClaimsWithHistoriesAndOrderItems() - Claim + History + OrderItem 업데이트 저장")
    class PersistClaimsWithHistoriesAndOrderItemsTest {

        @Test
        @DisplayName("완료 처리 시 Claim, History, OrderItem을 함께 저장한다")
        void persistClaimsWithHistoriesAndOrderItems_SavesAll() {
            // given
            List<ExchangeClaim> claims = List.of(ExchangeFixtures.shippingExchangeClaim());
            List<ClaimHistory> histories = List.of(Mockito.mock(ClaimHistory.class));
            List<OrderItem> orderItems = List.of(Mockito.mock(OrderItem.class));

            // when
            sut.persistClaimsWithHistoriesAndOrderItems(claims, histories, orderItems);

            // then
            then(exchangeCommandManager).should().persistAll(claims);
            then(historyCommandManager).should().persistAll(histories);
            then(orderItemCommandManager).should().persistAll(orderItems);
            then(outboxCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("persistAllWithHistories() - 신규 Claim + History 저장")
    class PersistAllWithHistoriesTest {

        @Test
        @DisplayName("신규 Claim과 History를 함께 저장한다")
        void persistAllWithHistories_SavesClaimsAndHistories() {
            // given
            List<ExchangeClaim> claims = List.of(ExchangeFixtures.newExchangeClaim());
            List<ClaimHistory> histories = List.of(Mockito.mock(ClaimHistory.class));

            // when
            sut.persistAllWithHistories(claims, histories);

            // then
            then(exchangeCommandManager).should().persistAll(claims);
            then(historyCommandManager).should().persistAll(histories);
            then(outboxCommandManager).shouldHaveNoInteractions();
            then(orderItemCommandManager).shouldHaveNoInteractions();
        }
    }
}
