package com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.entity.CommonCodeTypeJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.mapper.CommonCodeTypeJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.repository.CommonCodeTypeJpaRepository;
import com.ryuqq.marketplace.application.commoncodetype.port.out.command.CommonCodeTypeCommandPort;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * CommonCodeTypeCommandAdapter - 공통 코드 타입 명령 어댑터.
 *
 * <p>CommonCodeTypeCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class CommonCodeTypeCommandAdapter implements CommonCodeTypeCommandPort {

    private final CommonCodeTypeJpaRepository repository;
    private final CommonCodeTypeJpaEntityMapper mapper;

    /**
     * 생성자 주입.
     *
     * @param repository JPA 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public CommonCodeTypeCommandAdapter(
            CommonCodeTypeJpaRepository repository, CommonCodeTypeJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * CommonCodeType 영속화 (생성/수정).
     *
     * @param commonCodeType 영속화할 CommonCodeType
     * @return 영속화된 CommonCodeType ID
     */
    @Override
    public Long persist(CommonCodeType commonCodeType) {
        CommonCodeTypeJpaEntity entity = mapper.toEntity(commonCodeType);
        CommonCodeTypeJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    /**
     * CommonCodeType 목록 일괄 영속화.
     *
     * @param commonCodeTypes 영속화할 CommonCodeType 목록
     */
    @Override
    public void persistAll(List<CommonCodeType> commonCodeTypes) {
        List<CommonCodeTypeJpaEntity> entities =
                commonCodeTypes.stream().map(mapper::toEntity).toList();
        repository.saveAll(entities);
    }
}
