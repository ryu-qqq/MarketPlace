package com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity.LegacyQnaAnswerEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** 레거시 QnA 답변 JPA Repository. */
public interface LegacyQnaAnswerJpaRepository extends JpaRepository<LegacyQnaAnswerEntity, Long> {

    List<LegacyQnaAnswerEntity> findByQnaIdAndDeleteYn(Long qnaId, String deleteYn);
}
