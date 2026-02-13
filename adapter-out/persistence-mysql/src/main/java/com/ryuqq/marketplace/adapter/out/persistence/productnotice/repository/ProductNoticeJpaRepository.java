package com.ryuqq.marketplace.adapter.out.persistence.productnotice.repository;

import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ProductNotice JPA Repository.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용, 커스텀 쿼리 메서드 미정의.
 */
public interface ProductNoticeJpaRepository extends JpaRepository<ProductNoticeJpaEntity, Long> {}
