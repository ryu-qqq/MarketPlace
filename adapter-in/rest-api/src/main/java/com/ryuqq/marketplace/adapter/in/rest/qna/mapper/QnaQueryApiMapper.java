package com.ryuqq.marketplace.adapter.in.rest.qna.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.qna.dto.response.QnaApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.qna.dto.response.QnaReplyApiResponse;
import com.ryuqq.marketplace.application.qna.dto.result.QnaListResult;
import com.ryuqq.marketplace.application.qna.dto.result.QnaReplyResult;
import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** QnA Query API Mapper. */
@Component
public class QnaQueryApiMapper {

    public QnaApiResponse toResponse(QnaResult result) {
        List<QnaReplyApiResponse> replies =
                result.replies().stream().map(this::toReplyResponse).toList();

        return new QnaApiResponse(
                result.qnaId(),
                result.sellerId(),
                result.productGroupId(),
                result.orderId(),
                result.qnaType().name(),
                result.source().salesChannelId(),
                result.source().externalQnaId(),
                nullToEmpty(result.questionTitle()),
                nullToEmpty(result.questionContent()),
                nullToEmpty(result.questionAuthor()),
                result.status().name(),
                replies,
                DateTimeFormatUtils.formatDisplay(result.createdAt()),
                DateTimeFormatUtils.formatDisplay(result.updatedAt()));
    }

    public PageApiResponse<QnaApiResponse> toPageResponse(QnaListResult listResult) {
        List<QnaApiResponse> responses =
                listResult.items().stream().map(this::toResponse).toList();

        int page = listResult.limit() > 0 ? listResult.offset() / listResult.limit() : 0;
        return PageApiResponse.of(responses, page, listResult.limit(), listResult.totalCount());
    }

    private QnaReplyApiResponse toReplyResponse(QnaReplyResult result) {
        return new QnaReplyApiResponse(
                result.replyId(),
                result.parentReplyId(),
                nullToEmpty(result.content()),
                nullToEmpty(result.authorName()),
                result.replyType().name(),
                DateTimeFormatUtils.formatDisplay(result.createdAt()));
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
}
