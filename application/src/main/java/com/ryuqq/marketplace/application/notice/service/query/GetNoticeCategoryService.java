package com.ryuqq.marketplace.application.notice.service.query;

import com.ryuqq.marketplace.application.notice.assembler.NoticeCategoryAssembler;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.application.notice.manager.NoticeCategoryReadManager;
import com.ryuqq.marketplace.application.notice.port.in.query.GetNoticeCategoryUseCase;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import org.springframework.stereotype.Service;

/** 고시정보 카테고리 단건 조회 Service (CategoryGroup 기반). */
@Service
public class GetNoticeCategoryService implements GetNoticeCategoryUseCase {

    private final NoticeCategoryReadManager readManager;
    private final NoticeCategoryAssembler assembler;

    public GetNoticeCategoryService(
            NoticeCategoryReadManager readManager, NoticeCategoryAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public NoticeCategoryResult execute(CategoryGroup categoryGroup) {
        NoticeCategory category = readManager.getByCategoryGroup(categoryGroup);
        return assembler.toResult(category);
    }
}
