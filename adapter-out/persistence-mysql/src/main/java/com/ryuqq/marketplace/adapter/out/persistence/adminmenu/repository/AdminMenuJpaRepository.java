package com.ryuqq.marketplace.adapter.out.persistence.adminmenu.repository;

import com.ryuqq.marketplace.adapter.out.persistence.adminmenu.entity.AdminMenuJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** Admin 메뉴 JPA Repository (save 용). */
public interface AdminMenuJpaRepository extends JpaRepository<AdminMenuJpaEntity, Long> {}
