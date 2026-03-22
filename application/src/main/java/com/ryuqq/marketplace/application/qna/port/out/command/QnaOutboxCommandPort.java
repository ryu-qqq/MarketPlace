package com.ryuqq.marketplace.application.qna.port.out.command;

import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;

/** QnA 아웃박스 저장 포트. */
public interface QnaOutboxCommandPort {
    void persist(QnaOutbox outbox);
}
