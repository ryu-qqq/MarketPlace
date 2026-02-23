package com.ryuqq.marketplace.adapter.out.persistence.adminmenu.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.adminmenu.mapper.AdminMenuJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.adminmenu.repository.AdminMenuQueryDslRepository;
import com.ryuqq.marketplace.application.adminmenu.port.out.query.AdminMenuQueryPort;
import com.ryuqq.marketplace.domain.adminmenu.aggregate.AdminMenu;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * AdminMenuQueryAdapter - Admin 메뉴 조회 어댑터.
 *
 * <p>AdminMenuQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 */
@Component
public class AdminMenuQueryAdapter implements AdminMenuQueryPort {

    private final AdminMenuQueryDslRepository queryDslRepository;
    private final AdminMenuJpaEntityMapper mapper;

    public AdminMenuQueryAdapter(
            AdminMenuQueryDslRepository queryDslRepository, AdminMenuJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<AdminMenu> findActiveByMaxRoleLevel(int roleLevel) {
        return queryDslRepository.findActiveByMaxRoleLevel(roleLevel).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
