package com.ryuqq.marketplace.application.settlement.entry.assembler;

import com.ryuqq.marketplace.application.settlement.dto.response.SettlementEntryPageResult;
import com.ryuqq.marketplace.application.settlement.entry.dto.response.SettlementEntryListResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import java.util.List;
import org.springframework.stereotype.Component;

/** 정산 원장 Assembler. SettlementEntry 도메인 객체를 Result DTO로 변환합니다. */
@Component
public class SettlementEntryAssembler {

    /** SettlementEntry 단건을 SettlementEntryListResult로 변환합니다. */
    public SettlementEntryListResult toListResult(SettlementEntry entry) {
        return new SettlementEntryListResult(
                entry.idValue(),
                entry.status().name(),
                entry.sellerId(),
                entry.entryType().name(),
                entry.source().orderItemId(),
                entry.amounts().salesAmount().value(),
                entry.amounts().commissionRate(),
                entry.amounts().commissionAmount().value(),
                entry.amounts().settlementAmount().value(),
                entry.source().claimId(),
                entry.source().claimType(),
                entry.eligibleAt(),
                null,
                null,
                entry.createdAt());
    }

    /** SettlementEntry 목록과 PageMeta를 SettlementEntryPageResult로 변환합니다. */
    public SettlementEntryPageResult toPageResult(
            List<SettlementEntry> entries, PageMeta pageMeta) {
        List<SettlementEntryListResult> results = entries.stream().map(this::toListResult).toList();
        return new SettlementEntryPageResult(results, pageMeta);
    }
}
