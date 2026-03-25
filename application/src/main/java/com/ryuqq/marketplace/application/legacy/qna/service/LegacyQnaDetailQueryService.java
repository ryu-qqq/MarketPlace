package com.ryuqq.marketplace.application.legacy.qna.service;

import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaDetailResult;
import com.ryuqq.marketplace.application.legacy.qna.manager.LegacyQnaReadManager;
import com.ryuqq.marketplace.application.legacy.qna.port.in.LegacyQnaDetailQueryUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 QnA 단건 조회 서비스. */
@Service
public class LegacyQnaDetailQueryService implements LegacyQnaDetailQueryUseCase {

    private final LegacyQnaReadManager readManager;

    public LegacyQnaDetailQueryService(LegacyQnaReadManager readManager) {
        this.readManager = readManager;
    }

    @Override
    @Transactional(readOnly = true)
    public LegacyQnaDetailResult execute(long qnaId) {
        return readManager.fetchQnaDetail(qnaId);
    }
}
