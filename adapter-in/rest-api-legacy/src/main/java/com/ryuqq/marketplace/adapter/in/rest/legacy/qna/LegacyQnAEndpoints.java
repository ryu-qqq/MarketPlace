package com.ryuqq.marketplace.adapter.in.rest.legacy.qna;

/** 레거시 세토프 호환 QnA(문의) API 엔드포인트. */
public final class LegacyQnAEndpoints {

    private LegacyQnAEndpoints() {}

    private static final String BASE = "/api/v1/legacy";

    /** GET - QnA 단건 상세 조회 */
    public static final String QNA_ID = BASE + "/qna/{qnaId}";

    /** GET - QnA 목록 조회 (페이징) */
    public static final String QNAS = BASE + "/qnas";

    /** POST - QnA 답변 등록 */
    public static final String QNA_REPLY = BASE + "/qna/reply";
}
