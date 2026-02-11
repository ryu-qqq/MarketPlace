package com.ryuqq.marketplace.adapter.out.persistence.notice.repository;

import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeFieldJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** NoticeField JPA Repository. */
public interface NoticeFieldJpaRepository extends JpaRepository<NoticeFieldJpaEntity, Long> {
    List<NoticeFieldJpaEntity> findByNoticeCategoryIdOrderBySortOrder(Long noticeCategoryId);

    List<NoticeFieldJpaEntity> findByNoticeCategoryIdInOrderBySortOrder(
            List<Long> noticeCategoryIds);
}
