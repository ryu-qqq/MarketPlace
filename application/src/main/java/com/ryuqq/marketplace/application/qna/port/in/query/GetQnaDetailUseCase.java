package com.ryuqq.marketplace.application.qna.port.in.query;

import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;

/** QnA 상세 조회 UseCase. */
public interface GetQnaDetailUseCase {
    QnaResult execute(long qnaId);
}
