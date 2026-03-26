package com.ryuqq.marketplace.application.settlement.entry.service.query;

import com.ryuqq.marketplace.application.settlement.dto.response.SettlementEntryPageResult;
import com.ryuqq.marketplace.application.settlement.entry.assembler.SettlementEntryAssembler;
import com.ryuqq.marketplace.application.settlement.entry.dto.query.SettlementEntrySearchParams;
import com.ryuqq.marketplace.application.settlement.entry.manager.SettlementEntryReadManager;
import com.ryuqq.marketplace.application.settlement.entry.port.in.query.GetSettlementEntryListUseCase;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import java.util.List;
import org.springframework.stereotype.Service;

/** 정산 원장 목록 조회 서비스. */
@Service
public class GetSettlementEntryListService implements GetSettlementEntryListUseCase {

    private final SettlementEntryReadManager readManager;
    private final SettlementEntryAssembler assembler;

    public GetSettlementEntryListService(
            SettlementEntryReadManager readManager, SettlementEntryAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public SettlementEntryPageResult execute(SettlementEntrySearchParams params) {
        List<SettlementEntry> entries = readManager.findByCriteria(params);
        long totalElements = readManager.countByCriteria(params);
        PageMeta pageMeta = PageMeta.of(params.page(), params.size(), totalElements);
        return assembler.toPageResult(entries, pageMeta);
    }
}
