package com.ryuqq.marketplace.application.notice.internal;

import com.ryuqq.marketplace.application.notice.assembler.NoticeCategoryAssembler;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.application.notice.manager.NoticeCategoryReadManager;
import com.ryuqq.marketplace.application.notice.manager.NoticeFieldReadManager;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySearchCriteria;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 고시정보 카테고리 + 필드 조합 Read Facade. */
@Component
public class NoticeCategoryReadFacade {

    private final NoticeCategoryReadManager categoryReadManager;
    private final NoticeFieldReadManager fieldReadManager;
    private final NoticeCategoryAssembler assembler;

    public NoticeCategoryReadFacade(
            NoticeCategoryReadManager categoryReadManager,
            NoticeFieldReadManager fieldReadManager,
            NoticeCategoryAssembler assembler) {
        this.categoryReadManager = categoryReadManager;
        this.fieldReadManager = fieldReadManager;
        this.assembler = assembler;
    }

    @Transactional(readOnly = true)
    public NoticeCategoryResult getByCategoryGroup(CategoryGroup categoryGroup) {
        NoticeCategory category = categoryReadManager.getByCategoryGroup(categoryGroup);
        List<NoticeField> fields = fieldReadManager.getByNoticeCategoryId(category.idValue());
        return assembler.toResult(category, fields);
    }

    @Transactional(readOnly = true)
    public List<NoticeCategoryResult> findByCriteria(NoticeCategorySearchCriteria criteria) {
        List<NoticeCategory> categories = categoryReadManager.findByCriteria(criteria);
        if (categories.isEmpty()) {
            return List.of();
        }

        List<Long> categoryIds = categories.stream()
                .map(NoticeCategory::idValue)
                .toList();

        Map<Long, List<NoticeField>> fieldsMap =
                fieldReadManager.getGroupedByNoticeCategoryIds(categoryIds);

        return categories.stream()
                .map(category -> assembler.toResult(
                        category,
                        fieldsMap.getOrDefault(category.idValue(), List.of())))
                .toList();
    }

    @Transactional(readOnly = true)
    public long countByCriteria(NoticeCategorySearchCriteria criteria) {
        return categoryReadManager.countByCriteria(criteria);
    }
}
