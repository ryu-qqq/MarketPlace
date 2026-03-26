package com.ryuqq.marketplace.application.qna.assembler;

import com.ryuqq.marketplace.application.qna.dto.result.QnaReplyResult;
import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.aggregate.QnaReply;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class QnaAssembler {

    public QnaResult toResult(Qna qna) {
        List<QnaReplyResult> replyResults = qna.replies().stream()
                .map(this::toReplyResult)
                .toList();

        return new QnaResult(
                qna.idValue(),
                qna.sellerId(),
                qna.productGroupId(),
                qna.orderId(),
                qna.qnaType(),
                qna.source(),
                qna.questionTitle(),
                qna.questionContent(),
                qna.questionAuthor(),
                qna.status(),
                replyResults,
                qna.createdAt(),
                qna.updatedAt());
    }

    public List<QnaResult> toResults(List<Qna> qnas) {
        return qnas.stream().map(this::toResult).toList();
    }

    private QnaReplyResult toReplyResult(QnaReply reply) {
        return new QnaReplyResult(
                reply.idValue(),
                reply.parentReplyId(),
                reply.content(),
                reply.authorName(),
                reply.replyType(),
                reply.createdAt());
    }
}
