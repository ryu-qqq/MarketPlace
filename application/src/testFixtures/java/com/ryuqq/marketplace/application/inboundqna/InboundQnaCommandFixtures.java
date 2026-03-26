package com.ryuqq.marketplace.application.inboundqna;

import com.ryuqq.marketplace.application.inboundqna.dto.external.ExternalQnaPayload;

/**
 * InboundQna Application Command 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class InboundQnaCommandFixtures {

    private InboundQnaCommandFixtures() {}

    // ===== 기본 상수 =====
    public static final long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final long DEFAULT_INBOUND_QNA_ID = 1L;
    public static final String DEFAULT_EXTERNAL_QNA_ID = "EXT-QNA-001";
    public static final String DEFAULT_QUESTION_CONTENT = "이 상품 사이즈가 어떻게 되나요?";
    public static final String DEFAULT_QUESTION_AUTHOR = "구매자A";
    public static final String DEFAULT_QNA_TYPE = "PRODUCT";
    public static final String DEFAULT_QUESTION_TITLE = "기본 문의 제목";
    public static final String DEFAULT_RAW_PAYLOAD = "{\"externalQnaId\":\"EXT-QNA-001\"}";

    // ===== ExternalQnaPayload =====

    public static ExternalQnaPayload externalQnaPayload() {
        return new ExternalQnaPayload(
                DEFAULT_EXTERNAL_QNA_ID,
                null,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                null,
                null,
                DEFAULT_RAW_PAYLOAD);
    }

    public static ExternalQnaPayload externalQnaPayload(String externalQnaId) {
        return new ExternalQnaPayload(
                externalQnaId,
                null,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                null,
                null,
                "{\"externalQnaId\":\"" + externalQnaId + "\"}");
    }

    public static ExternalQnaPayload externalQnaPayloadWithParent(
            String externalQnaId, String parentExternalQnaId) {
        return new ExternalQnaPayload(
                externalQnaId,
                parentExternalQnaId,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                null,
                null,
                "{\"externalQnaId\":\"" + externalQnaId + "\"}");
    }

    public static ExternalQnaPayload externalQnaPayloadWithProduct(
            String externalQnaId, String externalProductId) {
        return new ExternalQnaPayload(
                externalQnaId,
                null,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                externalProductId,
                null,
                "{\"externalQnaId\":\"" + externalQnaId + "\"}");
    }

    public static ExternalQnaPayload externalQnaPayloadWithOrder(
            String externalQnaId, String externalOrderId) {
        return new ExternalQnaPayload(
                externalQnaId,
                null,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                null,
                externalOrderId,
                "{\"externalQnaId\":\"" + externalQnaId + "\"}");
    }

    public static ExternalQnaPayload externalQnaPayloadWithUnknownType(String externalQnaId) {
        return new ExternalQnaPayload(
                externalQnaId,
                null,
                "UNKNOWN_TYPE",
                DEFAULT_QUESTION_TITLE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                null,
                null,
                "{\"externalQnaId\":\"" + externalQnaId + "\"}");
    }
}
