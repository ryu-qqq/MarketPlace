package com.ryuqq.marketplace.application.settlement.port.out.query;

import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** 정산 Query Port. */
public interface SettlementQueryPort {

    Optional<Settlement> findById(SettlementId id);

    /** 셀러+기간으로 기존 Settlement 조회 (중복 방지). */
    Optional<Settlement> findBySellerIdAndPeriod(
            long sellerId, LocalDate startDate, LocalDate endDate);

    List<Settlement> findBySellerIdAndStatus(long sellerId, SettlementStatus status);

    List<Settlement> findByStatus(SettlementStatus status);
}
