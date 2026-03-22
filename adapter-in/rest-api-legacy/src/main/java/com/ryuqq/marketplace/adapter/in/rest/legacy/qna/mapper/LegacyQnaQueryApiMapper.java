package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyQnaSearchRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyAnswerQnaResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyDetailQnaResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyFetchQnaResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyQnaContentsResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyQnaTargetResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyUserInfoQnaResponse;
import com.ryuqq.marketplace.application.qna.dto.query.QnaSearchCondition;
import com.ryuqq.marketplace.application.qna.dto.result.QnaReplyResult;
import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;
import com.ryuqq.marketplace.domain.qna.vo.QnaReplyType;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 레거시 QnA 조회 API Mapper.
 *
 * <p>표준 QnaResult → 레거시 Response 변환.
 * 부족한 필드는 하드코딩/빈값으로 채움.
 */
@Component
public class LegacyQnaQueryApiMapper {

    private static final String DEFAULT_PRIVATE_YN = "N";
    private static final String DEFAULT_QNA_DETAIL_TYPE = "GENERAL";
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public QnaSearchCondition toSearchCondition(LegacyQnaSearchRequest request, int size) {
        QnaStatus status = parseStatus(request.qnaStatus());
        QnaType qnaType = parseQnaType(request.qnaType());
        Instant fromDate = request.startDate() != null
                ? request.startDate().atZone(KST).toInstant() : null;
        Instant toDate = request.endDate() != null
                ? request.endDate().atZone(KST).toInstant() : null;

        return new QnaSearchCondition(
                request.sellerId(),
                status,
                qnaType,
                request.searchKeyword(),
                fromDate,
                toDate,
                request.lastDomainId(),
                size);
    }

    public LegacyDetailQnaResponse toDetailResponse(QnaResult result) {
        LegacyFetchQnaResponse qna = toFetchResponse(result);
        Set<LegacyAnswerQnaResponse> answers = result.replies().stream()
                .filter(r -> r.replyType() == QnaReplyType.SELLER_ANSWER)
                .map(this::toAnswerResponse)
                .collect(Collectors.toSet());
        return new LegacyDetailQnaResponse(qna, answers);
    }

    public LegacyFetchQnaResponse toFetchResponse(QnaResult result) {
        LegacyQnaContentsResponse contents = new LegacyQnaContentsResponse(
                nullToEmpty(result.questionTitle()),
                nullToEmpty(result.questionContent()));

        LegacyUserInfoQnaResponse userInfo = new LegacyUserInfoQnaResponse(
                "", null, nullToEmpty(result.questionAuthor()), "", "", null);

        LegacyQnaTargetResponse target;
        if (result.orderId() != null) {
            target = LegacyQnaTargetResponse.order(
                    result.productGroupId(), "", "", "",
                    0L, result.orderId(), "");
        } else {
            target = LegacyQnaTargetResponse.product(
                    result.productGroupId(), "", "", "");
        }

        return new LegacyFetchQnaResponse(
                result.qnaId(),
                contents,
                DEFAULT_PRIVATE_YN,
                result.status() != null ? result.status().name() : "",
                result.qnaType() != null ? result.qnaType().name() : "",
                DEFAULT_QNA_DETAIL_TYPE,
                "",
                userInfo,
                target,
                List.of(),
                toLocalDateTime(result.createdAt()),
                toLocalDateTime(result.updatedAt()));
    }

    public List<LegacyFetchQnaResponse> toFetchResponses(List<QnaResult> results) {
        return results.stream().map(this::toFetchResponse).toList();
    }

    private LegacyAnswerQnaResponse toAnswerResponse(QnaReplyResult reply) {
        LegacyQnaContentsResponse contents = new LegacyQnaContentsResponse(
                "", nullToEmpty(reply.content()));

        return new LegacyAnswerQnaResponse(
                reply.replyId(),
                reply.parentReplyId(),
                "SELLER",
                contents,
                List.of(),
                nullToEmpty(reply.authorName()),
                nullToEmpty(reply.authorName()),
                toLocalDateTime(reply.createdAt()),
                toLocalDateTime(reply.createdAt()));
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, KST) : null;
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    private QnaStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return QnaStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private QnaType parseQnaType(String qnaType) {
        if (qnaType == null || qnaType.isBlank()) {
            return null;
        }
        try {
            return QnaType.valueOf(qnaType);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
