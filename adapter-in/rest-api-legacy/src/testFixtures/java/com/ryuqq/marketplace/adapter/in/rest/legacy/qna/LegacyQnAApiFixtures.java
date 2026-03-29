package com.ryuqq.marketplace.adapter.in.rest.legacy.qna;

import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyCreateQnaAnswerRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyQnaContentsRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyQnaImageRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyQnaSearchRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyUpdateQnaAnswerRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyAnswerQnaResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyCreateQnaAnswerResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyDetailQnaResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyFetchQnaResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyQnaContentsResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyQnaImageResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyQnaTargetResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyUserInfoQnaResponse;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaAnswerResult;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaDetailResult;
import com.ryuqq.marketplace.application.qna.dto.result.QnaReplyResult;
import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;
import com.ryuqq.marketplace.domain.qna.vo.QnaReplyType;
import com.ryuqq.marketplace.domain.qna.vo.QnaSource;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Legacy QnA API 테스트 Fixtures.
 *
 * <p>표준 QnaResult 기반으로 레거시 응답을 생성합니다.
 */
public final class LegacyQnAApiFixtures {

    private LegacyQnAApiFixtures() {}

    public static final long DEFAULT_QNA_ID = 501L;
    public static final long DEFAULT_QNA_ANSWER_ID = 601L;
    public static final String DEFAULT_QNA_TYPE = "PRODUCT";
    public static final String DEFAULT_QNA_STATUS = "PENDING";
    public static final String DEFAULT_TITLE = "상품 문의드립니다";
    public static final String DEFAULT_CONTENT = "상품 재고가 있나요?";
    public static final String DEFAULT_IMAGE_URL = "https://cdn.example.com/qna/image1.jpg";
    public static final String DEFAULT_AUTHOR = "구매자A";
    public static final Instant DEFAULT_CREATED_AT = Instant.parse("2025-02-10T01:30:00Z");
    public static final Instant DEFAULT_UPDATED_AT = Instant.parse("2025-02-10T02:00:00Z");

    // ===== Request Fixtures =====

    public static LegacyQnaSearchRequest searchRequest() {
        return new LegacyQnaSearchRequest(
                DEFAULT_QNA_STATUS, DEFAULT_QNA_TYPE, "GENERAL", "N", null, 1L, null, null, null);
    }

    public static LegacyCreateQnaAnswerRequest createAnswerRequest() {
        return new LegacyCreateQnaAnswerRequest(
                DEFAULT_QNA_ID,
                new LegacyQnaContentsRequest("답변 제목", "답변 내용입니다."),
                List.of(
                        new LegacyQnaImageRequest(
                                null, DEFAULT_QNA_ID, null, DEFAULT_IMAGE_URL, 1)));
    }

    public static LegacyUpdateQnaAnswerRequest updateAnswerRequest() {
        return new LegacyUpdateQnaAnswerRequest(
                DEFAULT_QNA_ANSWER_ID,
                DEFAULT_QNA_ID,
                new LegacyQnaContentsRequest("수정된 답변 제목", "수정된 답변 내용입니다."),
                List.of(
                        new LegacyQnaImageRequest(
                                101L,
                                DEFAULT_QNA_ID,
                                DEFAULT_QNA_ANSWER_ID,
                                DEFAULT_IMAGE_URL,
                                1)));
    }

    // ===== Application Result Fixtures =====

    public static QnaResult qnaResult() {
        return qnaResult(DEFAULT_QNA_ID);
    }

    public static QnaResult qnaResult(long qnaId) {
        return new QnaResult(
                qnaId,
                1L,
                100L,
                null,
                QnaType.PRODUCT,
                new QnaSource(1L, "EXT-QNA-001"),
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                DEFAULT_AUTHOR,
                QnaStatus.PENDING,
                List.of(),
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT);
    }

    public static QnaResult qnaResultWithReply(long qnaId) {
        return new QnaResult(
                qnaId,
                1L,
                100L,
                null,
                QnaType.PRODUCT,
                new QnaSource(1L, "EXT-QNA-001"),
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                DEFAULT_AUTHOR,
                QnaStatus.ANSWERED,
                List.of(replyResult()),
                DEFAULT_CREATED_AT,
                DEFAULT_UPDATED_AT);
    }

    public static QnaReplyResult replyResult() {
        return new QnaReplyResult(
                DEFAULT_QNA_ANSWER_ID,
                null,
                "답변 내용입니다.",
                "판매자A",
                QnaReplyType.SELLER_ANSWER,
                DEFAULT_CREATED_AT);
    }

    // ===== Legacy Application Result Fixtures =====

    public static LegacyQnaDetailResult legacyQnaDetailResult() {
        return new LegacyQnaDetailResult(
                DEFAULT_QNA_ID,
                DEFAULT_TITLE,
                DEFAULT_CONTENT,
                "N",
                DEFAULT_QNA_STATUS,
                DEFAULT_QNA_TYPE,
                "GENERAL",
                2L,
                1L,
                "테스트셀러",
                "MEMBERS",
                DEFAULT_AUTHOR,
                LocalDateTime.of(2025, 2, 10, 10, 30, 0),
                LocalDateTime.of(2025, 2, 10, 11, 0, 0),
                100L,
                null,
                List.of(
                        new LegacyQnaAnswerResult(
                                DEFAULT_QNA_ANSWER_ID,
                                null,
                                "SELLER",
                                "",
                                "답변 내용입니다.",
                                "판매자A",
                                "판매자A",
                                LocalDateTime.of(2025, 2, 10, 10, 30, 0),
                                LocalDateTime.of(2025, 2, 10, 10, 30, 0),
                                List.of())),
                List.of());
    }

    // ===== API Response Fixtures =====

    public static LegacyQnaImageResponse qnaImageResponse() {
        return new LegacyQnaImageResponse(null, 201L, DEFAULT_QNA_ID, null, DEFAULT_IMAGE_URL, 1);
    }

    public static LegacyFetchQnaResponse fetchQnaResponse() {
        return fetchQnaResponse(DEFAULT_QNA_ID);
    }

    public static LegacyFetchQnaResponse fetchQnaResponse(long qnaId) {
        return new LegacyFetchQnaResponse(
                qnaId,
                new LegacyQnaContentsResponse(DEFAULT_TITLE, DEFAULT_CONTENT),
                "N",
                DEFAULT_QNA_STATUS,
                DEFAULT_QNA_TYPE,
                "GENERAL",
                "",
                new LegacyUserInfoQnaResponse("", null, DEFAULT_AUTHOR, "", "", null),
                LegacyQnaTargetResponse.product(100L, "", "", ""),
                List.of(qnaImageResponse()),
                LocalDateTime.of(2025, 2, 10, 10, 30, 0),
                LocalDateTime.of(2025, 2, 10, 11, 0, 0));
    }

    public static LegacyDetailQnaResponse detailQnaResponse() {
        return new LegacyDetailQnaResponse(fetchQnaResponse(), Set.of(answerQnaResponse()));
    }

    public static LegacyCreateQnaAnswerResponse createQnaAnswerResponse() {
        return new LegacyCreateQnaAnswerResponse(
                DEFAULT_QNA_ID,
                DEFAULT_QNA_ANSWER_ID,
                "SELLER_ANSWER",
                "CLOSED",
                List.of(qnaImageResponse()));
    }

    public static LegacyAnswerQnaResponse answerQnaResponse() {
        return new LegacyAnswerQnaResponse(
                DEFAULT_QNA_ANSWER_ID,
                null,
                "SELLER",
                new LegacyQnaContentsResponse("", "답변 내용입니다."),
                List.of(qnaImageResponse()),
                "판매자A",
                "판매자A",
                LocalDateTime.of(2025, 2, 10, 10, 30, 0),
                LocalDateTime.of(2025, 2, 10, 10, 30, 0));
    }
}
