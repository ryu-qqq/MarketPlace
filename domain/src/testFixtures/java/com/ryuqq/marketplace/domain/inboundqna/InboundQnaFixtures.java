package com.ryuqq.marketplace.domain.inboundqna;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import com.ryuqq.marketplace.domain.inboundqna.id.InboundQnaId;
import com.ryuqq.marketplace.domain.inboundqna.vo.InboundQnaStatus;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;

/** InboundQna 테스트 Fixtures. */
public final class InboundQnaFixtures {

    private InboundQnaFixtures() {}

    public static final long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final String DEFAULT_EXTERNAL_QNA_ID = "EXT-QNA-001";
    public static final QnaType DEFAULT_QNA_TYPE = QnaType.PRODUCT;
    public static final String DEFAULT_QUESTION_CONTENT = "이 상품 사이즈가 어떻게 되나요?";
    public static final String DEFAULT_QUESTION_AUTHOR = "구매자A";
    public static final String DEFAULT_RAW_PAYLOAD = "{\"externalQnaId\":\"EXT-QNA-001\"}";

    public static InboundQna newInboundQna() {
        return InboundQna.forNew(
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                DEFAULT_RAW_PAYLOAD,
                CommonVoFixtures.now());
    }

    public static InboundQna newInboundQna(long salesChannelId, String externalQnaId) {
        return InboundQna.forNew(
                salesChannelId,
                externalQnaId,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                DEFAULT_RAW_PAYLOAD,
                CommonVoFixtures.now());
    }

    public static InboundQna receivedInboundQna() {
        return receivedInboundQna(1L);
    }

    public static InboundQna receivedInboundQna(long id) {
        return InboundQna.reconstitute(
                InboundQnaId.of(id),
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                DEFAULT_RAW_PAYLOAD,
                InboundQnaStatus.RECEIVED,
                null,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static InboundQna convertedInboundQna() {
        return convertedInboundQna(1L);
    }

    public static InboundQna convertedInboundQna(long id) {
        return InboundQna.reconstitute(
                InboundQnaId.of(id),
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                DEFAULT_RAW_PAYLOAD,
                InboundQnaStatus.CONVERTED,
                100L,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now());
    }

    public static InboundQna failedInboundQna() {
        return InboundQna.reconstitute(
                InboundQnaId.of(1L),
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                DEFAULT_QNA_TYPE,
                DEFAULT_QUESTION_CONTENT,
                DEFAULT_QUESTION_AUTHOR,
                DEFAULT_RAW_PAYLOAD,
                InboundQnaStatus.FAILED,
                null,
                "매핑 실패: 상품을 찾을 수 없습니다",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now());
    }
}
