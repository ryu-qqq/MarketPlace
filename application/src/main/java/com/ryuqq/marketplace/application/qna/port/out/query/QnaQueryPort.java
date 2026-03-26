package com.ryuqq.marketplace.application.qna.port.out.query;

import com.ryuqq.marketplace.application.qna.dto.query.QnaSearchCondition;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import java.util.List;
import java.util.Optional;

/** Qna 조회 포트. */
public interface QnaQueryPort {
    Optional<Qna> findById(long id);
    List<Qna> findBySellerId(long sellerId, QnaStatus status, int offset, int limit);
    long countBySellerId(long sellerId, QnaStatus status);
    Optional<Qna> findBySalesChannelIdAndExternalQnaId(long salesChannelId, String externalQnaId);

    List<Qna> search(QnaSearchCondition condition);
    long countByCondition(QnaSearchCondition condition);
}
