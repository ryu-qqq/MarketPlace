package com.ryuqq.marketplace.adapter.in.rest.qna;

/** QnA API 엔드포인트 상수. */
public final class QnaEndpoints {

    private QnaEndpoints() {
        throw new UnsupportedOperationException("상수 클래스는 인스턴스화할 수 없습니다");
    }

    public static final String BASE = "/api/v1/market";
    public static final String QNAS = BASE + "/qnas";
    public static final String QNA_ID = "/{qnaId}";
    public static final String QNA_BY_ID = QNAS + QNA_ID;
    public static final String ANSWER = QNA_ID + "/answers";
    public static final String CLOSE = QNA_ID + "/close";

    public static final String PATH_QNA_ID = "qnaId";
}
