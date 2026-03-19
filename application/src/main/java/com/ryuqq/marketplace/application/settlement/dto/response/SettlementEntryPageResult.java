package com.ryuqq.marketplace.application.settlement.dto.response;

import com.ryuqq.marketplace.application.settlement.entry.dto.response.SettlementEntryListResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/**
 * 정산 원장 목록 페이징 결과.
 *
 * @param entries 정산 원장 목록
 * @param pageMeta 페이지 메타 정보
 */
public record SettlementEntryPageResult(
        List<SettlementEntryListResult> entries, PageMeta pageMeta) {}
