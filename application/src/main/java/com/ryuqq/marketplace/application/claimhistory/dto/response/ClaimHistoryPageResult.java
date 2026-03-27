package com.ryuqq.marketplace.application.claimhistory.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 클레임 이력 페이지 조회 결과. */
public record ClaimHistoryPageResult(List<ClaimHistoryResult> results, PageMeta pageMeta) {

    public static ClaimHistoryPageResult of(List<ClaimHistoryResult> results, PageMeta pageMeta) {
        return new ClaimHistoryPageResult(results, pageMeta);
    }
}
