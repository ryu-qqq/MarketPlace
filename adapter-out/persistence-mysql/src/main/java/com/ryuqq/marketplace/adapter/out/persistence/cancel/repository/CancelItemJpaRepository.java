package com.ryuqq.marketplace.adapter.out.persistence.cancel.repository;

import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.CancelItemJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** 취소 상품 JPA Repository (save 용). */
public interface CancelItemJpaRepository extends JpaRepository<CancelItemJpaEntity, Long> {

    /**
     * cancel_id 목록으로 취소 상품 목록을 조회합니다.
     *
     * @param cancelIds 취소 ID 목록
     * @return 취소 상품 목록
     */
    List<CancelItemJpaEntity> findAllByCancelIdIn(List<String> cancelIds);

    /**
     * cancel_id로 취소 상품 목록을 조회합니다.
     *
     * @param cancelId 취소 ID
     * @return 취소 상품 목록
     */
    List<CancelItemJpaEntity> findAllByCancelId(String cancelId);

    /**
     * cancel_id로 취소 상품을 삭제합니다.
     *
     * @param cancelId 취소 ID
     */
    void deleteAllByCancelId(String cancelId);
}
