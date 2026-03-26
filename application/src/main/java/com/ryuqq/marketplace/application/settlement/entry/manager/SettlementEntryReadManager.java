package com.ryuqq.marketplace.application.settlement.entry.manager;

import com.ryuqq.marketplace.application.settlement.dto.response.DailySettlementResult;
import com.ryuqq.marketplace.application.settlement.entry.dto.query.SettlementEntrySearchParams;
import com.ryuqq.marketplace.application.settlement.entry.port.out.query.SettlementEntryQueryPort;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import com.ryuqq.marketplace.domain.settlement.entry.exception.SettlementEntryNotFoundException;
import com.ryuqq.marketplace.domain.settlement.entry.id.SettlementEntryId;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 정산 원장 Read Manager. */
@Component
public class SettlementEntryReadManager {

    private final SettlementEntryQueryPort queryPort;

    public SettlementEntryReadManager(SettlementEntryQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public SettlementEntry getById(SettlementEntryId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new SettlementEntryNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<SettlementEntry> findConfirmableEntries(Instant cutoffTime, int limit) {
        return queryPort.findConfirmableEntries(cutoffTime, limit);
    }

    @Transactional(readOnly = true)
    public List<SettlementEntry> findBySellerIdAndStatus(long sellerId, EntryStatus status) {
        return queryPort.findBySellerIdAndStatus(sellerId, status);
    }

    @Transactional(readOnly = true)
    public List<SettlementEntry> findByOrderItemId(String orderItemId) {
        return queryPort.findByOrderItemId(orderItemId);
    }

    @Transactional(readOnly = true)
    public List<SettlementEntry> findByIdIn(List<String> entryIds) {
        return queryPort.findByIdIn(entryIds);
    }

    @Transactional(readOnly = true)
    public List<SettlementEntry> findByCriteria(SettlementEntrySearchParams params) {
        return queryPort.findByCriteria(params);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(SettlementEntrySearchParams params) {
        return queryPort.countByCriteria(params);
    }

    @Transactional(readOnly = true)
    public List<DailySettlementResult> aggregateByDate(
            LocalDate startDate, LocalDate endDate, List<Long> sellerIds) {
        return queryPort.aggregateByDate(startDate, endDate, sellerIds);
    }

    @Transactional(readOnly = true)
    public List<Long> findDistinctSellerIdsByStatus(EntryStatus status) {
        return queryPort.findDistinctSellerIdsByStatus(status);
    }
}
