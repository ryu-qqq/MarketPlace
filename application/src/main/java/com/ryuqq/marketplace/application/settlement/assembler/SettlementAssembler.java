package com.ryuqq.marketplace.application.settlement.assembler;

import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementAmounts;
import java.util.List;
import org.springframework.stereotype.Component;

/** Settlement Assembler. Entry 목록 → 집계 금액 변환을 담당합니다. */
@Component
public class SettlementAssembler {

    /** CONFIRMED Entry 목록을 집계하여 SettlementAmounts를 생성합니다. */
    public SettlementAmounts toSettlementAmounts(List<SettlementEntry> entries) {
        int totalSales = 0;
        int totalCommission = 0;
        int totalReversal = 0;

        for (SettlementEntry entry : entries) {
            if (entry.entryType().isReversal()) {
                totalReversal += entry.amounts().settlementAmount().value();
            } else {
                totalSales += entry.amounts().salesAmount().value();
                totalCommission += entry.amounts().commissionAmount().value();
            }
        }

        int net = totalSales - totalCommission - totalReversal;
        return SettlementAmounts.of(
                Money.of(totalSales),
                Money.of(totalCommission),
                Money.of(totalReversal),
                Money.of(Math.max(net, 0)));
    }
}
