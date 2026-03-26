package com.ryuqq.marketplace.application.refund.internal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
@DisplayName("RefundSettlementProcessor 단위 테스트")
class RefundSettlementProcessorTest {

    @InjectMocks private RefundSettlementProcessor sut;

    @Mock private SettlementEntryCommandFactory factory;
    @Mock private SettlementEntryPersistenceFacade persistenceFacade;

    @Nested
    @DisplayName("createReversalEntry() - 환불 역분개 Entry 생성")
    class CreateReversalEntryTest {

        @Test
        @DisplayName("정상 호출 시 claimType=REFUND로 factory + persist가 호출된다")
        void createReversalEntry_Normal_CallsFactoryWithRefundType() {
            // given
            String orderItemId = "oi-test-001";
            long sellerId = 100L;
            String refundClaimId = "refund-001";
            int refundAmount = 50000;
            SettlementEntry entry = SettlementEntryFixtures.cancelReversalEntry();

            given(factory.createReversalEntry(argThat(cmd -> "REFUND".equals(cmd.claimType()))))
                    .willReturn(entry);

            // when
            sut.createReversalEntry(orderItemId, sellerId, refundClaimId, refundAmount);

            // then
            then(factory)
                    .should()
                    .createReversalEntry(argThat(cmd -> "REFUND".equals(cmd.claimType())));
            then(persistenceFacade).should().persist(entry);
        }

        @Test
        @DisplayName("factory에서 예외 발생 시 전파하지 않는다")
        void createReversalEntry_FactoryThrows_DoesNotPropagate() {
            // given
            String orderItemId = "oi-test-001";
            long sellerId = 100L;
            String refundClaimId = "refund-001";
            int refundAmount = 50000;

            willThrow(new RuntimeException("팩토리 오류")).given(factory).createReversalEntry(any());

            // when - 예외가 전파되지 않음
            sut.createReversalEntry(orderItemId, sellerId, refundClaimId, refundAmount);

            // then
            then(persistenceFacade).shouldHaveNoInteractions();
        }
    }
}
