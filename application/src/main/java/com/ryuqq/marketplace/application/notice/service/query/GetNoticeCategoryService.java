package com.ryuqq.marketplace.application.notice.service.query;

import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.application.notice.internal.NoticeCategoryReadFacade;
import com.ryuqq.marketplace.application.notice.port.in.query.GetNoticeCategoryUseCase;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import org.springframework.stereotype.Service;

/** 고시정보 카테고리 단건 조회 Service (CategoryGroup 기반). */
@Service
public class GetNoticeCategoryService implements GetNoticeCategoryUseCase {

    private final NoticeCategoryReadFacade readFacade;

    public GetNoticeCategoryService(NoticeCategoryReadFacade readFacade) {
        this.readFacade = readFacade;
    }

    @Override
    public NoticeCategoryResult execute(CategoryGroup categoryGroup) {
        return readFacade.getByCategoryGroup(categoryGroup);
    }
}
