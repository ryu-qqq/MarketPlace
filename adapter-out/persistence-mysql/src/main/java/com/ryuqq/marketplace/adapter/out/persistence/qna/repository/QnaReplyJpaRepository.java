package com.ryuqq.marketplace.adapter.out.persistence.qna.repository;

import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaReplyJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** QnaReply JPA Repository. */
public interface QnaReplyJpaRepository extends JpaRepository<QnaReplyJpaEntity, Long> {
    List<QnaReplyJpaEntity> findByQnaId(long qnaId);
    List<QnaReplyJpaEntity> findByQnaIdIn(List<Long> qnaIds);
}
