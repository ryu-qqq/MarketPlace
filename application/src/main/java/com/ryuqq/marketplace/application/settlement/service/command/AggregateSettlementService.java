package com.ryuqq.marketplace.application.settlement.service.command;

import com.ryuqq.marketplace.application.settlement.assembler.SettlementAssembler;
import com.ryuqq.marketplace.application.settlement.dto.command.AggregateSettlementCommand;
import com.ryuqq.marketplace.application.settlement.entry.manager.SettlementEntryReadManager;
import com.ryuqq.marketplace.application.settlement.factory.SettlementCommandFactory;
import com.ryuqq.marketplace.application.settlement.factory.SettlementCommandFactory.SettlementBundle;
import com.ryuqq.marketplace.application.settlement.internal.SettlementPersistenceFacade;
import com.ryuqq.marketplace.application.settlement.manager.SettlementReadManager;
import com.ryuqq.marketplace.application.settlement.port.in.command.AggregateSettlementUseCase;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryStatus;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementAmounts;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** CONFIRMED Entry → Settlement 집계 서비스. */
@Service
public class AggregateSettlementService implements AggregateSettlementUseCase {

    private static final Logger log = LoggerFactory.getLogger(AggregateSettlementService.class);

    private final SettlementEntryReadManager entryReadManager;
    private final SettlementReadManager settlementReadManager;
    private final SettlementCommandFactory commandFactory;
    private final SettlementAssembler assembler;
    private final SettlementPersistenceFacade persistenceFacade;

    public AggregateSettlementService(
            SettlementEntryReadManager entryReadManager,
            SettlementReadManager settlementReadManager,
            SettlementCommandFactory commandFactory,
            SettlementAssembler assembler,
            SettlementPersistenceFacade persistenceFacade) {
        this.entryReadManager = entryReadManager;
        this.settlementReadManager = settlementReadManager;
        this.commandFactory = commandFactory;
        this.assembler = assembler;
        this.persistenceFacade = persistenceFacade;
    }

    @Override
    public void execute(AggregateSettlementCommand command) {
        if (settlementReadManager
                .findBySellerIdAndPeriod(
                        command.sellerId(), command.periodStartDate(), command.periodEndDate())
                .isPresent()) {
            return;
        }

        List<SettlementEntry> entries =
                entryReadManager.findBySellerIdAndStatus(command.sellerId(), EntryStatus.CONFIRMED);
        if (entries.isEmpty()) {
            return;
        }

        SettlementAmounts amounts = assembler.toSettlementAmounts(entries);
        SettlementBundle bundle = commandFactory.createAggregateBundle(command, amounts, entries);

        persistenceFacade.persistWithSettledEntries(bundle.settlement(), bundle.settledEntries());
    }

    @Override
    public void executeAll() {
        LocalDate today = LocalDate.now();
        LocalDate periodStart = today.minusDays(7);
        LocalDate periodEnd = today.minusDays(1);

        List<Long> sellerIds =
                entryReadManager.findDistinctSellerIdsByStatus(EntryStatus.CONFIRMED);
        log.info("[Settlement-Aggregate] 대상 셀러 수: {}, 기간: {} ~ {}", sellerIds.size(), periodStart, periodEnd);

        for (Long sellerId : sellerIds) {
            try {
                execute(new AggregateSettlementCommand(sellerId, periodStart, periodEnd, "WEEKLY"));
            } catch (Exception e) {
                log.warn("셀러 {} 정산 집계 실패: {}", sellerId, e.getMessage());
            }
        }
    }
}
