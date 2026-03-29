package com.ryuqq.marketplace.application.legacy.qna.service;

import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaAnswerResult;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaDetailResult;
import com.ryuqq.marketplace.application.qna.dto.result.QnaReplyResult;
import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;
import com.ryuqq.marketplace.domain.qna.vo.QnaReplyType;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * market QnaResult → 레거시 LegacyQnaDetailResult 변환기.
 *
 * <p>market 스키마의 QnaResult를 레거시 어드민 프론트가 기대하는 형태로 변환합니다.
 */
final class LegacyQnaFromMarketAssembler {

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    private LegacyQnaFromMarketAssembler() {}

    /** 상품 정보가 포함된 QnA 상세 결과 생성. */
    static LegacyQnaDetailResult toDetailResult(
            QnaResult result,
            String sellerName,
            String productGroupName,
            String productGroupMainImageUrl,
            Long brandId,
            String brandName) {
        List<LegacyQnaAnswerResult> answers =
                result.replies().stream()
                        .map(LegacyQnaFromMarketAssembler::toAnswerResult)
                        .toList();

        return new LegacyQnaDetailResult(
                result.qnaId(),
                nullToEmpty(result.questionTitle()),
                nullToEmpty(result.questionContent()),
                "N",
                mapStatus(result.status()),
                mapQnaType(result.qnaType()),
                mapQnaDetailType(result.qnaType()),
                null,
                result.sellerId(),
                nullToEmpty(sellerName),
                "MEMBERS",
                nullToEmpty(result.questionAuthor()),
                toLocalDateTime(result.createdAt()),
                toLocalDateTime(result.updatedAt()),
                result.productGroupId() != 0 ? result.productGroupId() : null,
                result.orderId(),
                answers,
                List.of(),
                nullToEmpty(productGroupName),
                nullToEmpty(productGroupMainImageUrl),
                brandId != null ? brandId : 0L,
                nullToEmpty(brandName),
                "",
                "",
                "",
                "M");
    }

    private static LegacyQnaAnswerResult toAnswerResult(QnaReplyResult reply) {
        String writerType = reply.replyType() == QnaReplyType.SELLER_ANSWER ? "SELLER" : "CUSTOMER";
        LocalDateTime createdAt = toLocalDateTime(reply.createdAt());

        return new LegacyQnaAnswerResult(
                reply.replyId(),
                reply.parentReplyId(),
                writerType,
                "",
                nullToEmpty(reply.content()),
                nullToEmpty(reply.authorName()),
                nullToEmpty(reply.authorName()),
                createdAt,
                createdAt,
                List.of());
    }

    /** market QnaStatus → 레거시 상태 문자열. */
    private static String mapStatus(QnaStatus status) {
        if (status == null) {
            return "OPEN";
        }
        return switch (status) {
            case PENDING -> "OPEN";
            case ANSWERED -> "CLOSED";
            case CLOSED -> "CLOSED";
        };
    }

    /** market QnaType → 레거시 qnaType 문자열. */
    private static String mapQnaType(com.ryuqq.marketplace.domain.qna.vo.QnaType qnaType) {
        if (qnaType == null) {
            return "PRODUCT";
        }
        return switch (qnaType) {
            case ORDER, EXCHANGE, REFUND -> "ORDER";
            default -> "PRODUCT";
        };
    }

    /** market QnaType → 레거시 qnaDetailType 문자열. */
    private static String mapQnaDetailType(com.ryuqq.marketplace.domain.qna.vo.QnaType qnaType) {
        if (qnaType == null) {
            return "ETC";
        }
        return qnaType.name();
    }

    private static LocalDateTime toLocalDateTime(java.time.Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZONE_ID);
    }

    private static String nullToEmpty(String value) {
        return value != null ? value : "";
    }
}
