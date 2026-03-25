package com.ryuqq.marketplace.application.legacy.qna.port.in;

import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaDetailResult;

/** 레거시 QnA 단건 상세 조회 UseCase. */
public interface LegacyQnaDetailQueryUseCase {

    LegacyQnaDetailResult execute(long qnaId);
}
