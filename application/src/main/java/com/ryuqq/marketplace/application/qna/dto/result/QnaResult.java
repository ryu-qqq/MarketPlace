package com.ryuqq.marketplace.application.qna.dto.result;

import com.ryuqq.marketplace.domain.qna.vo.QnaSource;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import java.time.Instant;
import java.util.List;

/** QnA 조회 결과. */
public record QnaResult(
        long qnaId,
        long sellerId,
        long productGroupId,
        Long orderId,
        QnaType qnaType,
        QnaSource source,
        String questionTitle,
        String questionContent,
        String questionAuthor,
        QnaStatus status,
        List<QnaReplyResult> replies,
        Instant createdAt,
        Instant updatedAt
) {}
