package com.ryuqq.marketplace.adapter.in.rest.legacy.qna;

import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyQnAListResponse;
import java.util.List;

/**
 * Legacy QnA API 테스트 Fixtures.
 *
 * <p>Legacy QnA(문의) REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacyQnAApiFixtures {

    private LegacyQnAApiFixtures() {}

    // ===== 상수 =====
    public static final long DEFAULT_QNA_ID = 501L;
    public static final String DEFAULT_QUESTION_TYPE = "PRODUCT";
    public static final String DEFAULT_STATUS = "PENDING";
    public static final String DEFAULT_CREATED_AT = "2025-02-10T10:30:00+09:00";

    // ===== Response Fixtures =====

    public static LegacyQnAListResponse qnaListResponse() {
        return new LegacyQnAListResponse(
                DEFAULT_QNA_ID, DEFAULT_QUESTION_TYPE, DEFAULT_STATUS, DEFAULT_CREATED_AT);
    }

    public static LegacyQnAListResponse qnaListResponse(long qnaId) {
        return new LegacyQnAListResponse(
                qnaId, DEFAULT_QUESTION_TYPE, DEFAULT_STATUS, DEFAULT_CREATED_AT);
    }

    public static List<LegacyQnAListResponse> qnaListResponses(int count) {
        return java.util.stream.LongStream.rangeClosed(1, count)
                .mapToObj(
                        i ->
                                new LegacyQnAListResponse(
                                        i,
                                        DEFAULT_QUESTION_TYPE,
                                        DEFAULT_STATUS,
                                        DEFAULT_CREATED_AT))
                .toList();
    }
}
