package com.ryuqq.marketplace.application.settlement.entry.port.out.query;

import com.ryuqq.marketplace.application.settlement.dto.response.DailySettlementResult;
import com.ryuqq.marketplace.application.settlement.entry.dto.query.SettlementEntrySearchParams;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import com.ryuqq.marketplace.domain.settlement.entry.id.SettlementEntryId;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** 정산 원장 Query Port. */
public interface SettlementEntryQueryPort {

    Optional<SettlementEntry> findById(SettlementEntryId id);

    /** eligibleAt <= cutoffTime 이고 PENDING 상태인 Entry 조회. */
    List<SettlementEntry> findConfirmableEntries(Instant cutoffTime, int limit);

    /** 특정 셀러의 CONFIRMED 상태 Entry 목록. */
    List<SettlementEntry> findBySellerIdAndStatus(long sellerId, EntryStatus status);

    /** 특정 orderItemId의 Entry 목록. */
    List<SettlementEntry> findByOrderItemId(String orderItemId);

    /** ID 목록으로 Entry 일괄 조회. */
    List<SettlementEntry> findByIdIn(List<String> entryIds);

    /** 검색 조건으로 Entry 목록 조회 (페이징). */
    List<SettlementEntry> findByCriteria(SettlementEntrySearchParams params);

    /** 검색 조건에 해당하는 Entry 전체 건수. */
    long countByCriteria(SettlementEntrySearchParams params);

    /** 날짜별 Entry 집계 (eligible_at 기준). */
    List<DailySettlementResult> aggregateByDate(
            LocalDate startDate, LocalDate endDate, List<Long> sellerIds);

    /** 지정 상태의 Entry가 존재하는 셀러 ID 목록 (중복 제거). */
    List<Long> findDistinctSellerIdsByStatus(EntryStatus status);
}
