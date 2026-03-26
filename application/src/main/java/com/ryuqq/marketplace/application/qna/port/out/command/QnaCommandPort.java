package com.ryuqq.marketplace.application.qna.port.out.command;

import com.ryuqq.marketplace.domain.qna.aggregate.Qna;

/** Qna 저장 포트. */
public interface QnaCommandPort {
    long persist(Qna qna);
}
