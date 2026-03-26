package com.ryuqq.marketplace.application.qna.manager;

import com.ryuqq.marketplace.application.qna.dto.query.QnaSearchCondition;
import com.ryuqq.marketplace.application.qna.port.out.query.QnaQueryPort;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.exception.QnaErrorCode;
import com.ryuqq.marketplace.domain.qna.exception.QnaException;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class QnaReadManager {

    private final QnaQueryPort queryPort;

    public QnaReadManager(QnaQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public Qna getById(long id) {
        return queryPort.findById(id)
                .orElseThrow(() -> new QnaException(QnaErrorCode.QNA_NOT_FOUND,
                        "Qna not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Qna> findBySellerId(long sellerId, QnaStatus status, int offset, int limit) {
        return queryPort.findBySellerId(sellerId, status, offset, limit);
    }

    @Transactional(readOnly = true)
    public long countBySellerId(long sellerId, QnaStatus status) {
        return queryPort.countBySellerId(sellerId, status);
    }

    @Transactional(readOnly = true)
    public Qna getBySalesChannelIdAndExternalQnaId(long salesChannelId, String externalQnaId) {
        return queryPort
                .findBySalesChannelIdAndExternalQnaId(salesChannelId, externalQnaId)
                .orElseThrow(
                        () ->
                                new QnaException(
                                        QnaErrorCode.QNA_NOT_FOUND,
                                        "Qna not found: salesChannelId="
                                                + salesChannelId
                                                + ", externalQnaId="
                                                + externalQnaId));
    }

    @Transactional(readOnly = true)
    public List<Qna> search(QnaSearchCondition condition) {
        return queryPort.search(condition);
    }

    @Transactional(readOnly = true)
    public long countByCondition(QnaSearchCondition condition) {
        return queryPort.countByCondition(condition);
    }
}
