package com.ryuqq.marketplace.application.notice.assembler;

import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryPageResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeFieldResult;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import java.util.List;
import org.springframework.stereotype.Component;

/** 고시정보 카테고리 Assembler. */
@Component
public class NoticeCategoryAssembler {

    public NoticeCategoryResult toResult(NoticeCategory category, List<NoticeField> fields) {
        List<NoticeFieldResult> fieldResults = fields.stream().map(this::toFieldResult).toList();

        return new NoticeCategoryResult(
                category.idValue(),
                category.codeValue(),
                category.nameKo(),
                category.nameEn(),
                category.targetCategoryGroup().name(),
                category.isActive(),
                fieldResults,
                category.createdAt());
    }

    public NoticeCategoryPageResult toPageResult(
            List<NoticeCategoryResult> results, int page, int size, long totalElements) {
        return NoticeCategoryPageResult.of(results, page, size, totalElements);
    }

    private NoticeFieldResult toFieldResult(NoticeField field) {
        return new NoticeFieldResult(
                field.idValue(),
                field.fieldCodeValue(),
                field.fieldNameValue(),
                field.isRequired(),
                field.sortOrder());
    }
}
