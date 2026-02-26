package com.ryuqq.marketplace.application.notice.service.query;

import com.ryuqq.marketplace.application.notice.assembler.NoticeCategoryAssembler;
import com.ryuqq.marketplace.application.notice.dto.query.NoticeCategorySearchParams;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryPageResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.application.notice.factory.NoticeCategoryQueryFactory;
import com.ryuqq.marketplace.application.notice.manager.NoticeCategoryReadManager;
import com.ryuqq.marketplace.application.notice.port.in.query.SearchNoticeCategoryByOffsetUseCase;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 고시정보 카테고리 검색 Service (Offset 기반 페이징). */
@Service
public class SearchNoticeCategoryByOffsetService implements SearchNoticeCategoryByOffsetUseCase {

    private final NoticeCategoryReadManager readManager;
    private final NoticeCategoryQueryFactory queryFactory;
    private final NoticeCategoryAssembler assembler;

    public SearchNoticeCategoryByOffsetService(
            NoticeCategoryReadManager readManager,
            NoticeCategoryQueryFactory queryFactory,
            NoticeCategoryAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public NoticeCategoryPageResult execute(NoticeCategorySearchParams params) {
        NoticeCategorySearchCriteria criteria = queryFactory.createCriteria(params);
        List<NoticeCategory> categories = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        List<NoticeCategoryResult> results = categories.stream().map(assembler::toResult).toList();
        return assembler.toPageResult(results, criteria.page(), criteria.size(), totalElements);
    }
}
