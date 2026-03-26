package com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity.LegacyQnaImageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** 레거시 QnA 이미지 JPA Repository. */
public interface LegacyQnaImageJpaRepository extends JpaRepository<LegacyQnaImageEntity, Long> {

    List<LegacyQnaImageEntity> findByQnaAnswerIdAndDeleteYn(Long qnaAnswerId, String deleteYn);
}
