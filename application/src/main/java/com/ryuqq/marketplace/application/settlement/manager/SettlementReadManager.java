package com.ryuqq.marketplace.application.settlement.manager;

import com.ryuqq.marketplace.application.settlement.port.out.query.SettlementQueryPort;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import com.ryuqq.marketplace.domain.settlement.exception.SettlementErrorCode;
import com.ryuqq.marketplace.domain.settlement.exception.SettlementException;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 정산 Read Manager. */
@Component
public class SettlementReadManager {

    private final SettlementQueryPort queryPort;

    public SettlementReadManager(SettlementQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public Settlement getById(SettlementId id) {
        return queryPort
                .findById(id)
                .orElseThrow(
                        () -> new SettlementException(SettlementErrorCode.SETTLEMENT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Optional<Settlement> findBySellerIdAndPeriod(
            long sellerId, LocalDate startDate, LocalDate endDate) {
        return queryPort.findBySellerIdAndPeriod(sellerId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Settlement> findBySellerIdAndStatus(long sellerId, SettlementStatus status) {
        return queryPort.findBySellerIdAndStatus(sellerId, status);
    }

    @Transactional(readOnly = true)
    public List<Settlement> findByStatus(SettlementStatus status) {
        return queryPort.findByStatus(status);
    }
}
