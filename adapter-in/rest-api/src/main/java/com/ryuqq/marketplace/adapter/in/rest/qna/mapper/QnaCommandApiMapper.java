package com.ryuqq.marketplace.adapter.in.rest.qna.mapper;

import com.ryuqq.marketplace.adapter.in.rest.qna.dto.request.AnswerQnaApiRequest;
import com.ryuqq.marketplace.application.qna.dto.command.AnswerQnaCommand;
import com.ryuqq.marketplace.application.qna.dto.command.CloseQnaCommand;
import org.springframework.stereotype.Component;

/** QnA Command API Mapper. */
@Component
public class QnaCommandApiMapper {

    public AnswerQnaCommand toCommand(long qnaId, AnswerQnaApiRequest request) {
        return new AnswerQnaCommand(
                qnaId,
                "",
                request.content(),
                request.authorName(),
                request.parentReplyId());
    }

    public CloseQnaCommand toCloseCommand(long qnaId) {
        return new CloseQnaCommand(qnaId);
    }
}
