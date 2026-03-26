package com.ryuqq.marketplace.application.qna.dto.result;

import com.ryuqq.marketplace.domain.qna.vo.QnaReplyType;
import java.time.Instant;

/** QnA 답변 조회 결과. */
public record QnaReplyResult(
        Long replyId,
        Long parentReplyId,
        String content,
        String authorName,
        QnaReplyType replyType,
        Instant createdAt
) {}
