package com.ryuqq.marketplace.application.exchange.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.ExchangeCommandFixtures;
import com.ryuqq.marketplace.application.exchange.dto.command.ShipExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory.OutboxWithHistory;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceBundle;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceFacade;
import com.ryuqq.marketplace.application.exchange.validator.ExchangeBatchValidator;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
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
@DisplayName("ShipExchangeBatchService 단위 테스트")
class ShipExchangeBatchServiceTest {

    @InjectMocks private ShipExchangeBatchService sut;

    @Mock private ExchangeBatchValidator validator;
    @Mock private ExchangeCommandFactory commandFactory;
    @Mock private ExchangePersistenceFacade persistenceFacade;

    @Nested
    @DisplayName("execute() - 교환 재배송 일괄 처리")
    class ExecuteTest {

        @Test
        @DisplayName("준비 완료 상태 클레임을 재배송 처리하고 Outbox와 History를 저장한다")
        void execute_PreparingClaims_PersistsWithOutboxAndHistory() {
            // given
            ShipExchangeBatchCommand command = ExchangeCommandFixtures.shipCommand();
            ExchangeClaim claim = ExchangeFixtures.preparingExchangeClaim();
            ExchangeOutbox outbox = Mockito.mock(ExchangeOutbox.class);
            ClaimHistory history = Mockito.mock(ClaimHistory.class);
            OutboxWithHistory bundle = new OutboxWithHistory(outbox, history);

            ShipExchangeBatchCommand.ShipItem item = command.items().get(0);
            List<String> claimIds = List.of(item.exchangeClaimId());

            given(validator.validateAndGet(claimIds, command.sellerId()))
                    .willReturn(List.of(claim));
            given(
                            commandFactory.createShipBundle(
                                    claim,
                                    item.linkedOrderId(),
                                    item.deliveryCompany(),
                                    item.trackingNumber(),
                                    command.processedBy()))
                    .willReturn(bundle);

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.successCount()).isEqualTo(1);
            assertThat(result.failureCount()).isEqualTo(0);
            then(persistenceFacade).should().persistAll(any(ExchangePersistenceBundle.class));
        }

        @Test
        @DisplayName("클레임이 없으면 저장이 호출되지 않는다")
        void execute_NoClaims_PersistenceNotCalled() {
            // given
            ShipExchangeBatchCommand command = ExchangeCommandFixtures.shipCommand();
            List<String> claimIds = List.of(command.items().get(0).exchangeClaimId());

            given(validator.validateAndGet(claimIds, command.sellerId())).willReturn(List.of());

            // when
            BatchProcessingResult<String> result = sut.execute(command);

            // then
            assertThat(result.successCount()).isEqualTo(0);
            then(persistenceFacade).shouldHaveNoInteractions();
        }
    }
}
