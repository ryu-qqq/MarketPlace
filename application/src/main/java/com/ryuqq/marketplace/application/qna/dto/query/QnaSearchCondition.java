package com.ryuqq.marketplace.application.qna.dto.query;

import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import java.time.Instant;

/** QnA 검색 조건. */
public record QnaSearchCondition(
        Long sellerId,
        QnaStatus status,
        QnaType qnaType,
        String keyword,
        Instant fromDate,
        Instant toDate,
        Long cursorId,
        int size) {}
