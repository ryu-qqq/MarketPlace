package com.ryuqq.marketplace.domain.qna.event;

import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.qna.id.QnaId;
import java.time.Instant;

/** QnA 답변 완료 이벤트. 외부몰 답변 동기화 트리거로 사용됩니다. */
public record QnaAnsweredEvent(
        QnaId qnaId,
        long sellerId,
        long salesChannelId,
        String externalQnaId,
        Instant occurredAt
) implements DomainEvent {}
