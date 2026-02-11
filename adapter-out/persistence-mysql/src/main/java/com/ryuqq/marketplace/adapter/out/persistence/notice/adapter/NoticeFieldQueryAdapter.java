package com.ryuqq.marketplace.adapter.out.persistence.notice.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeFieldJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.notice.mapper.NoticeFieldJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.notice.repository.NoticeFieldJpaRepository;
import com.ryuqq.marketplace.application.notice.port.out.query.NoticeFieldQueryPort;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** NoticeField Query Adapter. */
@Component
public class NoticeFieldQueryAdapter implements NoticeFieldQueryPort {

    private final NoticeFieldJpaRepository fieldJpaRepository;
    private final NoticeFieldJpaEntityMapper mapper;

    public NoticeFieldQueryAdapter(
            NoticeFieldJpaRepository fieldJpaRepository,
            NoticeFieldJpaEntityMapper mapper) {
        this.fieldJpaRepository = fieldJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<NoticeField> findByNoticeCategoryId(Long noticeCategoryId) {
        return fieldJpaRepository
                .findByNoticeCategoryIdOrderBySortOrder(noticeCategoryId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Map<Long, List<NoticeField>> findGroupedByNoticeCategoryIds(
            List<Long> noticeCategoryIds) {
        return fieldJpaRepository
                .findByNoticeCategoryIdInOrderBySortOrder(noticeCategoryIds)
                .stream()
                .collect(Collectors.groupingBy(
                        NoticeFieldJpaEntity::getNoticeCategoryId,
                        Collectors.mapping(mapper::toDomain, Collectors.toList())));
    }
}
