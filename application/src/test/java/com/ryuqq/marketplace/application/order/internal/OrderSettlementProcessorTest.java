package com.ryuqq.marketplace.application.order.internal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.marketplace.application.settlement.entry.factory.SettlementEntryCommandFactory;
import com.ryuqq.marketplace.application.settlement.entry.internal.SettlementEntryPersistenceFacade;
import com.ryuqq.marketplace.domain.settlement.entry.SettlementEntryFixtures;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
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
@DisplayName("OrderSettlementProcessor 단위 테스트")
class OrderSettlementProcessorTest {

    @InjectMocks private OrderSettlementProcessor sut;

    @Mock private SettlementEntryCommandFactory factory;
    @Mock private SettlementEntryPersistenceFacade persistenceFacade;

    @Nested
    @DisplayName("createSalesEntry() - 판매 Entry 생성")
    class CreateSalesEntryTest {

        @Test
        @DisplayName("정상 호출 시 factory.createSalesEntry + persistenceFacade.persist가 호출된다")
        void createSalesEntry_Normal_CallsFactoryAndPersist() {
            // given
            String orderItemId = "oi-test-001";
            long sellerId = 100L;
            int salesAmount = 50000;
            SettlementEntry entry = SettlementEntryFixtures.salesEntry();

            given(factory.createSalesEntry(any())).willReturn(entry);

            // when
            sut.createSalesEntry(orderItemId, sellerId, salesAmount);

            // then
            then(factory).should().createSalesEntry(any());
            then(persistenceFacade).should().persist(entry);
        }

        @Test
        @DisplayName("factory에서 예외 발생 시 전파하지 않는다")
        void createSalesEntry_FactoryThrows_DoesNotPropagate() {
            // given
            String orderItemId = "oi-test-001";
            long sellerId = 100L;
            int salesAmount = 50000;

            willThrow(new RuntimeException("팩토리 오류")).given(factory).createSalesEntry(any());

            // when - 예외가 전파되지 않음
            sut.createSalesEntry(orderItemId, sellerId, salesAmount);

            // then
            then(persistenceFacade).shouldHaveNoInteractions();
        }
    }
}
