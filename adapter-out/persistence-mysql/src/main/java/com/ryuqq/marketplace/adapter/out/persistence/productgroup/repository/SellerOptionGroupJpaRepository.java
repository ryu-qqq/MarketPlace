package com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionGroupJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** SellerOptionGroup JPA Repository. */
public interface SellerOptionGroupJpaRepository
        extends JpaRepository<SellerOptionGroupJpaEntity, Long> {

    List<SellerOptionGroupJpaEntity> findByProductGroupId(Long productGroupId);

    void deleteByProductGroupId(Long productGroupId);
}
