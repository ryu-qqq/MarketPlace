package com.ryuqq.marketplace.application.settlement.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.settlement.SettlementCommandFixtures;
import com.ryuqq.marketplace.application.settlement.assembler.SettlementAssembler;
import com.ryuqq.marketplace.application.settlement.dto.command.AggregateSettlementCommand;
import com.ryuqq.marketplace.application.settlement.entry.manager.SettlementEntryReadManager;
import com.ryuqq.marketplace.application.settlement.factory.SettlementCommandFactory;
import com.ryuqq.marketplace.application.settlement.factory.SettlementCommandFactory.SettlementBundle;
import com.ryuqq.marketplace.application.settlement.internal.SettlementPersistenceFacade;
import com.ryuqq.marketplace.application.settlement.manager.SettlementReadManager;
import com.ryuqq.marketplace.domain.settlement.SettlementFixtures;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import com.ryuqq.marketplace.domain.settlement.entry.SettlementEntryFixtures;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryStatus;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementAmounts;
import java.util.List;
import java.util.Optional;
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
@DisplayName("AggregateSettlementService 단위 테스트")
class AggregateSettlementServiceTest {

    @InjectMocks private AggregateSettlementService sut;

    @Mock private SettlementEntryReadManager entryReadManager;
    @Mock private SettlementReadManager settlementReadManager;
    @Mock private SettlementCommandFactory commandFactory;
    @Mock private SettlementAssembler assembler;
    @Mock private SettlementPersistenceFacade persistenceFacade;

    @Nested
    @DisplayName("execute() - CONFIRMED Entry → Settlement 집계")
    class ExecuteTest {

        @Test
        @DisplayName("이미 동일 기간 Settlement가 존재하면 아무 처리도 하지 않는다")
        void execute_AlreadyExistingSettlement_DoesNothing() {
            // given
            AggregateSettlementCommand command = SettlementCommandFixtures.aggregateCommand();
            Settlement existing = SettlementFixtures.calculatingSettlement();

            given(
                            settlementReadManager.findBySellerIdAndPeriod(
                                    command.sellerId(),
                                    command.periodStartDate(),
                                    command.periodEndDate()))
                    .willReturn(Optional.of(existing));

            // when
            sut.execute(command);

            // then
            then(entryReadManager).shouldHaveNoInteractions();
            then(commandFactory).shouldHaveNoInteractions();
            then(persistenceFacade).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("CONFIRMED Entry가 없으면 Settlement를 생성하지 않는다")
        void execute_NoConfirmedEntries_DoesNothing() {
            // given
            AggregateSettlementCommand command = SettlementCommandFixtures.aggregateCommand();

            given(
                            settlementReadManager.findBySellerIdAndPeriod(
                                    command.sellerId(),
                                    command.periodStartDate(),
                                    command.periodEndDate()))
                    .willReturn(Optional.empty());
            given(
                            entryReadManager.findBySellerIdAndStatus(
                                    command.sellerId(), EntryStatus.CONFIRMED))
                    .willReturn(List.of());

            // when
            sut.execute(command);

            // then
            then(commandFactory).shouldHaveNoInteractions();
            then(persistenceFacade).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("CONFIRMED Entry가 존재하면 Settlement를 집계하고 저장한다")
        void execute_WithConfirmedEntries_AggregatesAndPersists() {
            // given
            AggregateSettlementCommand command = SettlementCommandFixtures.aggregateCommand();
            List<SettlementEntry> entries = List.of(SettlementEntryFixtures.confirmedSalesEntry());
            SettlementAmounts amounts = SettlementFixtures.defaultSettlementAmounts();
            Settlement settlement = SettlementFixtures.newSettlement();
            SettlementBundle bundle = Mockito.mock(SettlementBundle.class);

            given(
                            settlementReadManager.findBySellerIdAndPeriod(
                                    command.sellerId(),
                                    command.periodStartDate(),
                                    command.periodEndDate()))
                    .willReturn(Optional.empty());
            given(
                            entryReadManager.findBySellerIdAndStatus(
                                    command.sellerId(), EntryStatus.CONFIRMED))
                    .willReturn(entries);
            given(assembler.toSettlementAmounts(entries)).willReturn(amounts);
            given(commandFactory.createAggregateBundle(command, amounts, entries))
                    .willReturn(bundle);
            given(bundle.settlement()).willReturn(settlement);
            given(bundle.settledEntries()).willReturn(entries);

            // when
            sut.execute(command);

            // then
            then(assembler).should().toSettlementAmounts(entries);
            then(commandFactory).should().createAggregateBundle(command, amounts, entries);
            then(persistenceFacade).should().persistWithSettledEntries(settlement, entries);
        }
    }
}
