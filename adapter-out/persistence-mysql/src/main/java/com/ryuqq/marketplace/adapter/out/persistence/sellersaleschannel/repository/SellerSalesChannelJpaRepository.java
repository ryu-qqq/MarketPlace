package com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.repository;

import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.entity.SellerSalesChannelJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * SellerSalesChannelJpaRepository - 셀러 판매채널 JPA 레포지토리.
 *
 * <p>PER-REP-001: JpaRepository는 save/saveAll만 사용.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository에서 처리.
 */
public interface SellerSalesChannelJpaRepository
        extends JpaRepository<SellerSalesChannelJpaEntity, Long> {}
