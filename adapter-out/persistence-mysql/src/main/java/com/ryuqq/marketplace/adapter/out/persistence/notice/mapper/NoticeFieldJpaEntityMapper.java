package com.ryuqq.marketplace.adapter.out.persistence.notice.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeFieldJpaEntity;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import com.ryuqq.marketplace.domain.notice.id.NoticeFieldId;
import com.ryuqq.marketplace.domain.notice.vo.NoticeFieldCode;
import com.ryuqq.marketplace.domain.notice.vo.NoticeFieldName;
import org.springframework.stereotype.Component;

/** NoticeField JPA Entity Mapper. */
@Component
public class NoticeFieldJpaEntityMapper {

    public NoticeField toDomain(NoticeFieldJpaEntity entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
        return NoticeField.reconstitute(
                NoticeFieldId.of(entity.getId()),
                NoticeFieldCode.of(entity.getFieldCode()),
                NoticeFieldName.of(entity.getFieldName()),
                entity.isRequired(),
                entity.getSortOrder());
    }
}
