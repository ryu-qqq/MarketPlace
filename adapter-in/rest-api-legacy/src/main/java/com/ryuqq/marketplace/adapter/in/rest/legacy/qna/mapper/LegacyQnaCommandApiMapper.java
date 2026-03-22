package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyCreateQnaAnswerRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyUpdateQnaAnswerRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyCreateQnaAnswerResponse;
import com.ryuqq.marketplace.application.qna.dto.command.AnswerQnaCommand;
import com.ryuqq.marketplace.application.qna.dto.command.UpdateQnaReplyCommand;
import com.ryuqq.marketplace.application.qna.dto.result.QnaReplyResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 레거시 QnA 커맨드 API Mapper.
 *
 * <p>Legacy Request → 표준 Command, 표준 Result → Legacy Response 변환.
 */
@Component
public class LegacyQnaCommandApiMapper {

    public AnswerQnaCommand toAnswerCommand(LegacyCreateQnaAnswerRequest request) {
        String title = request.qnaContents() != null ? request.qnaContents().title() : "";
        String content = request.qnaContents() != null ? request.qnaContents().content() : "";

        return new AnswerQnaCommand(request.qnaId(), title, content, "SELLER", null);
    }

    public UpdateQnaReplyCommand toUpdateCommand(LegacyUpdateQnaAnswerRequest request) {
        String content = request.qnaContents() != null ? request.qnaContents().content() : "";

        return new UpdateQnaReplyCommand(request.qnaId(), request.qnaAnswerId(), content);
    }

    public LegacyCreateQnaAnswerResponse toCreateAnswerResponse(
            long qnaId, QnaReplyResult replyResult) {
        return new LegacyCreateQnaAnswerResponse(
                qnaId,
                replyResult.replyId() != null ? replyResult.replyId() : 0L,
                replyResult.replyType() != null ? replyResult.replyType().name() : "",
                "CLOSED",
                List.of());
    }
}
