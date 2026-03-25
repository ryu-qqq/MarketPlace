package com.ryuqq.marketplace.application.legacy.qna.manager;

import com.ryuqq.marketplace.application.legacy.qna.dto.query.LegacyQnaSearchParams;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaDetailResult;
import com.ryuqq.marketplace.application.legacy.qna.port.out.LegacyQnaQueryPort;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 QnA 조회 매니저.
 *
 * <p>LegacyQnaQueryPort 호출 래퍼.
 */
@Component
@Transactional(readOnly = true)
public class LegacyQnaReadManager {

    private final LegacyQnaQueryPort queryPort;

    public LegacyQnaReadManager(LegacyQnaQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    public LegacyQnaDetailResult fetchQnaDetail(long qnaId) {
        return queryPort
                .fetchQnaDetail(qnaId)
                .orElseThrow(
                        () -> new IllegalStateException("레거시 QnA를 찾을 수 없습니다. qnaId=" + qnaId));
    }

    public List<LegacyQnaDetailResult> fetchQnaList(LegacyQnaSearchParams params) {
        return queryPort.fetchQnaList(params);
    }

    public long countQnas(LegacyQnaSearchParams params) {
        return queryPort.countQnas(params);
    }
}
