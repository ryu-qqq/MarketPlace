package com.ryuqq.marketplace.application.legacy.qna.port.out;

import com.ryuqq.marketplace.application.legacy.qna.dto.query.LegacyQnaSearchParams;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaDetailResult;
import java.util.List;
import java.util.Optional;

/** 레거시 QnA 조회 Port. luxurydb qna 테이블을 조회합니다. */
public interface LegacyQnaQueryPort {

    Optional<LegacyQnaDetailResult> fetchQnaDetail(long qnaId);

    List<LegacyQnaDetailResult> fetchQnaList(LegacyQnaSearchParams params);

    long countQnas(LegacyQnaSearchParams params);
}
