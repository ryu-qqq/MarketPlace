package com.ryuqq.marketplace.application.notice.port.in.query;

import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;

/** 고시정보 카테고리 단건 조회 UseCase (CategoryGroup 기반). */
public interface GetNoticeCategoryUseCase {
    NoticeCategoryResult execute(CategoryGroup categoryGroup);
}
