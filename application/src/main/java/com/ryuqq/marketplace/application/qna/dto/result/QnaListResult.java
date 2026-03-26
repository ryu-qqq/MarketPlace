package com.ryuqq.marketplace.application.qna.dto.result;

import java.util.List;

/** QnA 목록 조회 결과. */
public record QnaListResult(
        List<QnaResult> items,
        long totalCount,
        int offset,
        int limit
) {}
