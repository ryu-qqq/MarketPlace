package com.ryuqq.marketplace.adapter.in.rest.qna;

import com.ryuqq.marketplace.adapter.in.rest.qna.dto.request.AnswerQnaApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.qna.dto.request.SearchQnaApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.qna.dto.response.QnaApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.qna.dto.response.QnaReplyApiResponse;
import com.ryuqq.marketplace.application.qna.dto.command.AnswerQnaCommand;
import com.ryuqq.marketplace.application.qna.dto.command.CloseQnaCommand;
import com.ryuqq.marketplace.application.qna.dto.result.QnaListResult;
import com.ryuqq.marketplace.application.qna.dto.result.QnaReplyResult;
import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;
import com.ryuqq.marketplace.domain.qna.vo.QnaReplyType;
import com.ryuqq.marketplace.domain.qna.vo.QnaSource;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/**
 * QnA API 테스트 Fixtures.
 *
 * <p>QnA REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class QnaApiFixtures {

    private QnaApiFixtures() {}

    // ===== 상수 =====
    public static final long DEFAULT_QNA_ID = 1L;
    public static final long DEFAULT_SELLER_ID = 10L;
    public static final long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final String DEFAULT_EXTERNAL_QNA_ID = "EXT-QNA-001";
    public static final String DEFAULT_QUESTION_TITLE = "상품 사이즈 문의";
    public static final String DEFAULT_QUESTION_CONTENT = "이 상품의 사이즈가 어떻게 되나요?";
    public static final String DEFAULT_QUESTION_AUTHOR = "구매자A";
    public static final String DEFAULT_ANSWER_CONTENT = "해당 상품은 Free 사이즈입니다.";
    public static final String DEFAULT_AUTHOR_NAME = "판매자A";
    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-02-10T01:30:00Z");
    public static final String DEFAULT_FORMATTED_TIME = "2025-02-10T01:30:00Z";

    // ===== SearchQnaApiRequest =====

    public static SearchQnaApiRequest searchRequest() {
        return new SearchQnaApiRequest(DEFAULT_SELLER_ID, null, null, null);
    }

    public static SearchQnaApiRequest searchRequest(long sellerId, QnaStatus status) {
        return new SearchQnaApiRequest(sellerId, status, 0, 20);
    }

    public static SearchQnaApiRequest searchRequest(int page, int size) {
        return new SearchQnaApiRequest(DEFAULT_SELLER_ID, null, page, size);
    }

    public static SearchQnaApiRequest searchRequestWithStatus(QnaStatus status) {
        return new SearchQnaApiRequest(DEFAULT_SELLER_ID, status, 0, 20);
    }

    // ===== AnswerQnaApiRequest =====

    public static AnswerQnaApiRequest answerRequest() {
        return new AnswerQnaApiRequest(DEFAULT_ANSWER_CONTENT, DEFAULT_AUTHOR_NAME, null);
    }

    public static AnswerQnaApiRequest answerRequest(String content, String authorName) {
        return new AnswerQnaApiRequest(content, authorName, null);
    }

    public static AnswerQnaApiRequest answerRequestWithParent(Long parentReplyId) {
        return new AnswerQnaApiRequest(DEFAULT_ANSWER_CONTENT, DEFAULT_AUTHOR_NAME, parentReplyId);
    }

    // ===== QnaReplyResult (Application) =====

    public static QnaReplyResult replyResult(long replyId) {
        return new QnaReplyResult(
                replyId,
                null,
                DEFAULT_ANSWER_CONTENT,
                DEFAULT_AUTHOR_NAME,
                QnaReplyType.SELLER_ANSWER,
                DEFAULT_INSTANT);
    }

    public static QnaReplyResult replyResultWithParent(long replyId, long parentReplyId) {
        return new QnaReplyResult(
                replyId,
                parentReplyId,
                "추가 답변입니다.",
                DEFAULT_AUTHOR_NAME,
                QnaReplyType.SELLER_ANSWER,
                DEFAULT_INSTANT);
    }

    // ===== QnaResult (Application) =====

    public static QnaResult qnaResult(long qnaId) {
        return new QnaResult(
                qnaId,
                DEFAULT_SELLER_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                null,
                QnaType.PRODUCT,
                new QnaSource(DEFAULT_SALES_CHANNEL_ID, DEFAULT_EXTERNAL_QNA_ID),
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                QnaStatus.PENDING,
                List.of(),
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static QnaResult qnaResultWithReplies(long qnaId) {
        return new QnaResult(
                qnaId,
                DEFAULT_SELLER_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                null,
                QnaType.PRODUCT,
                new QnaSource(DEFAULT_SALES_CHANNEL_ID, DEFAULT_EXTERNAL_QNA_ID),
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                QnaStatus.ANSWERED,
                List.of(replyResult(1L), replyResultWithParent(2L, 1L)),
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static QnaResult qnaResult(long qnaId, QnaStatus status, QnaType qnaType) {
        return new QnaResult(
                qnaId,
                DEFAULT_SELLER_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                null,
                qnaType,
                new QnaSource(DEFAULT_SALES_CHANNEL_ID, "EXT-QNA-" + String.format("%03d", qnaId)),
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                status,
                List.of(),
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static List<QnaResult> qnaResults(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> qnaResult((long) i, QnaStatus.PENDING, QnaType.PRODUCT))
                .toList();
    }

    // ===== QnaListResult (Application) =====

    public static QnaListResult listResult(int count, int offset, int limit) {
        List<QnaResult> results = qnaResults(count);
        return new QnaListResult(results, count, offset, limit);
    }

    public static QnaListResult emptyListResult() {
        return new QnaListResult(List.of(), 0, 0, 20);
    }

    // ===== AnswerQnaCommand (Application) =====

    public static AnswerQnaCommand answerCommand(long qnaId) {
        return new AnswerQnaCommand(qnaId, "", DEFAULT_ANSWER_CONTENT, DEFAULT_AUTHOR_NAME, null);
    }

    public static CloseQnaCommand closeCommand(long qnaId) {
        return new CloseQnaCommand(qnaId);
    }

    // ===== QnaReplyApiResponse =====

    public static QnaReplyApiResponse replyApiResponse(long replyId) {
        return new QnaReplyApiResponse(
                replyId,
                null,
                DEFAULT_ANSWER_CONTENT,
                DEFAULT_AUTHOR_NAME,
                QnaReplyType.SELLER_ANSWER.name(),
                DEFAULT_FORMATTED_TIME);
    }

    public static QnaReplyApiResponse replyApiResponseWithParent(long replyId, long parentReplyId) {
        return new QnaReplyApiResponse(
                replyId,
                parentReplyId,
                "추가 답변입니다.",
                DEFAULT_AUTHOR_NAME,
                QnaReplyType.SELLER_ANSWER.name(),
                DEFAULT_FORMATTED_TIME);
    }

    // ===== QnaApiResponse =====

    public static QnaApiResponse qnaApiResponse(long qnaId) {
        return new QnaApiResponse(
                qnaId,
                DEFAULT_SELLER_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                QnaType.PRODUCT.name(),
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                QnaStatus.PENDING.name(),
                List.of(),
                DEFAULT_FORMATTED_TIME,
                DEFAULT_FORMATTED_TIME);
    }

    public static QnaApiResponse qnaApiResponseWithReplies(long qnaId) {
        return new QnaApiResponse(
                qnaId,
                DEFAULT_SELLER_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                QnaType.PRODUCT.name(),
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                QnaStatus.ANSWERED.name(),
                List.of(replyApiResponse(1L), replyApiResponseWithParent(2L, 1L)),
                DEFAULT_FORMATTED_TIME,
                DEFAULT_FORMATTED_TIME);
    }

    public static List<QnaApiResponse> qnaApiResponses(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> qnaApiResponse((long) i))
                .toList();
    }
}
