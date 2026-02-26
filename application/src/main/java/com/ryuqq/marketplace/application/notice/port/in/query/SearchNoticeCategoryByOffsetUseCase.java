package com.ryuqq.marketplace.application.notice.port.in.query;

import com.ryuqq.marketplace.application.notice.dto.query.NoticeCategorySearchParams;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryPageResult;

/** 고시정보 카테고리 목록 조회 UseCase (Offset 기반 페이징). */
public interface SearchNoticeCategoryByOffsetUseCase {
    NoticeCategoryPageResult execute(NoticeCategorySearchParams params);
}
