package com.ryuqq.marketplace.application.legacy.qna.port.in;

import com.ryuqq.marketplace.application.legacy.qna.dto.query.LegacyQnaSearchParams;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaPageResult;

/** 레거시 QnA 목록 조회 UseCase. */
public interface LegacyQnaListQueryUseCase {

    LegacyQnaPageResult execute(LegacyQnaSearchParams params);
}
