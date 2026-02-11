package com.ryuqq.marketplace.adapter.out.persistence.notice.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeCategoryJpaEntity;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.vo.NoticeCategoryCode;
import com.ryuqq.marketplace.domain.notice.vo.NoticeCategoryName;
import java.util.List;
import org.springframework.stereotype.Component;

/** NoticeCategory JPA Entity Mapper. */
@Component
public class NoticeCategoryJpaEntityMapper {

    public NoticeCategory toDomain(NoticeCategoryJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        var id = NoticeCategoryId.of(entity.getId());
        var code = NoticeCategoryCode.of(entity.getCode());
        var categoryName = NoticeCategoryName.of(entity.getNameKo(), entity.getNameEn());
        var categoryGroup = CategoryGroup.valueOf(entity.getTargetCategoryGroup());

        return NoticeCategory.reconstitute(
                id,
                code,
                categoryName,
                categoryGroup,
                entity.isActive(),
                List.of(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
