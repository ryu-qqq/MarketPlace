package com.ryuqq.marketplace.domain.qna.vo;

/**
 * QnA 출처 정보.
 *
 * <p>어떤 외부 판매채널에서 유입된 QnA인지, 외부 QnA ID가 무엇인지 기록합니다.
 */
public record QnaSource(long salesChannelId, String externalQnaId) {

    public QnaSource {
        if (externalQnaId == null || externalQnaId.isBlank()) {
            throw new IllegalArgumentException("externalQnaId must not be blank");
        }
    }
}
