package com.ryuqq.marketplace.application.outboundproduct.dto.result;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 연동 이력 페이징 결과 DTO. */
public record SyncHistoryPageResult(List<SyncHistoryListResult> results, PageMeta pageMeta) {

    public static SyncHistoryPageResult of(
            List<SyncHistoryListResult> results, int page, int size, long totalElements) {
        return new SyncHistoryPageResult(results, PageMeta.of(page, size, totalElements));
    }

    public static SyncHistoryPageResult empty(int size) {
        return new SyncHistoryPageResult(List.of(), PageMeta.empty(size));
    }
}
