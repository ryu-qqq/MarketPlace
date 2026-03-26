package com.ryuqq.marketplace.application.legacy.qna.service;

import com.ryuqq.marketplace.application.legacy.qna.dto.query.LegacyQnaSearchParams;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaDetailResult;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaPageResult;
import com.ryuqq.marketplace.application.legacy.qna.manager.LegacyQnaReadManager;
import com.ryuqq.marketplace.application.legacy.qna.port.in.LegacyQnaListQueryUseCase;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 QnA 목록 조회 서비스. */
@Service
public class LegacyQnaListQueryService implements LegacyQnaListQueryUseCase {

    private final LegacyQnaReadManager readManager;

    public LegacyQnaListQueryService(LegacyQnaReadManager readManager) {
        this.readManager = readManager;
    }

    @Override
    @Transactional(readOnly = true)
    public LegacyQnaPageResult execute(LegacyQnaSearchParams params) {
        List<LegacyQnaDetailResult> items = readManager.fetchQnaList(params);
        long totalElements = readManager.countQnas(params);

        Long lastDomainId = items.isEmpty() ? null : items.getLast().qnaId();

        return new LegacyQnaPageResult(items, totalElements, lastDomainId);
    }
}
