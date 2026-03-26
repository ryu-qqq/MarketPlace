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
import org.mockito.ArgumentMatchers;
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

    @Nested
    @DisplayName("executeAll() - 전체 셀러 일괄 집계")
    class ExecuteAllTest {

        @Test
        @DisplayName("CONFIRMED 셀러가 없으면 execute를 호출하지 않는다")
        void executeAll_NoConfirmedSellers_DoesNotCallExecute() {
            // given
            given(entryReadManager.findDistinctSellerIdsByStatus(EntryStatus.CONFIRMED))
                    .willReturn(List.of());

            // when
            sut.executeAll();

            // then
            then(settlementReadManager).shouldHaveNoInteractions();
            then(commandFactory).shouldHaveNoInteractions();
            then(persistenceFacade).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("셀러 2명이면 각각 execute가 호출된다")
        void executeAll_TwoSellers_ExecutesForEach() {
            // given
            long sellerId1 = 1L;
            long sellerId2 = 2L;

            given(entryReadManager.findDistinctSellerIdsByStatus(EntryStatus.CONFIRMED))
                    .willReturn(List.of(sellerId1, sellerId2));

            // 각 셀러에 대해 findBySellerIdAndPeriod → empty, findBySellerIdAndStatus → empty 로 조기 종료
            given(
                            settlementReadManager.findBySellerIdAndPeriod(
                                    ArgumentMatchers.anyLong(),
                                    ArgumentMatchers.any(),
                                    ArgumentMatchers.any()))
                    .willReturn(Optional.empty());
            given(
                            entryReadManager.findBySellerIdAndStatus(
                                    ArgumentMatchers.anyLong(),
                                    ArgumentMatchers.eq(EntryStatus.CONFIRMED)))
                    .willReturn(List.of());

            // when
            sut.executeAll();

            // then - 셀러 2명에 대해 각각 조회 호출 확인
            then(settlementReadManager)
                    .should(Mockito.times(2))
                    .findBySellerIdAndPeriod(
                            ArgumentMatchers.anyLong(),
                            ArgumentMatchers.any(),
                            ArgumentMatchers.any());
        }

        @Test
        @DisplayName("한 셀러 실패해도 다른 셀러는 계속 진행한다")
        void executeAll_OneSellerFails_OthersContinue() {
            // given
            long failSellerId = 1L;
            long successSellerId = 2L;

            given(entryReadManager.findDistinctSellerIdsByStatus(EntryStatus.CONFIRMED))
                    .willReturn(List.of(failSellerId, successSellerId));

            // 첫 번째 셀러: findBySellerIdAndPeriod에서 예외
            given(
                            settlementReadManager.findBySellerIdAndPeriod(
                                    ArgumentMatchers.eq(failSellerId),
                                    ArgumentMatchers.any(),
                                    ArgumentMatchers.any()))
                    .willThrow(new RuntimeException("DB 오류"));

            // 두 번째 셀러: 정상 (조기 종료 — entry 없음)
            given(
                            settlementReadManager.findBySellerIdAndPeriod(
                                    ArgumentMatchers.eq(successSellerId),
                                    ArgumentMatchers.any(),
                                    ArgumentMatchers.any()))
                    .willReturn(Optional.empty());
            given(entryReadManager.findBySellerIdAndStatus(successSellerId, EntryStatus.CONFIRMED))
                    .willReturn(List.of());

            // when
            sut.executeAll();

            // then - 두 번째 셀러도 정상적으로 조회 호출됨
            then(settlementReadManager)
                    .should()
                    .findBySellerIdAndPeriod(
                            ArgumentMatchers.eq(successSellerId),
                            ArgumentMatchers.any(),
                            ArgumentMatchers.any());
        }
    }
}
