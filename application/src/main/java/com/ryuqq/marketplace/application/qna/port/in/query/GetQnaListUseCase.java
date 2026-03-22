package com.ryuqq.marketplace.application.qna.port.in.query;

import com.ryuqq.marketplace.application.qna.dto.query.QnaSearchCondition;
import com.ryuqq.marketplace.application.qna.dto.result.QnaListResult;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;

/** QnA 목록 조회 UseCase. */
public interface GetQnaListUseCase {
    QnaListResult execute(long sellerId, QnaStatus status, int offset, int limit);
    QnaListResult execute(QnaSearchCondition condition);
}
