package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyPaymentSnapshotShippingAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 레거시 결제 스냅샷 배송지 JPA Repository. */
public interface LegacyPaymentSnapshotShippingAddressJpaRepository
        extends JpaRepository<LegacyPaymentSnapshotShippingAddressEntity, Long> {}
