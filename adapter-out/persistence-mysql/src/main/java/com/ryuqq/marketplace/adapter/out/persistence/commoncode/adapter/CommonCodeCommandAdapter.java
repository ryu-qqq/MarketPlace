package com.ryuqq.marketplace.adapter.out.persistence.commoncode.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.commoncode.entity.CommonCodeJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.mapper.CommonCodeJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.repository.CommonCodeJpaRepository;
import com.ryuqq.marketplace.application.commoncode.port.out.command.CommonCodeCommandPort;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * CommonCodeCommandAdapter - 공통 코드 Command 어댑터.
 *
 * <p>CommonCodeCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 *
 * <p>PER-ADP-005: Domain -> Entity 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class CommonCodeCommandAdapter implements CommonCodeCommandPort {

    private final CommonCodeJpaRepository jpaRepository;
    private final CommonCodeJpaEntityMapper mapper;

    /**
     * 생성자 주입.
     *
     * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 의존.
     *
     * @param jpaRepository JPA 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public CommonCodeCommandAdapter(
            CommonCodeJpaRepository jpaRepository, CommonCodeJpaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * 공통 코드 저장.
     *
     * @param commonCode CommonCode 도메인 객체
     * @return 저장된 공통 코드 ID
     */
    @Override
    public Long persist(CommonCode commonCode) {
        CommonCodeJpaEntity entity = mapper.toEntity(commonCode);
        CommonCodeJpaEntity saved = jpaRepository.save(entity);
        return saved.getId();
    }

    /**
     * 공통 코드 일괄 저장.
     *
     * @param commonCodes CommonCode 도메인 객체 목록
     */
    @Override
    public void persistAll(List<CommonCode> commonCodes) {
        List<CommonCodeJpaEntity> entities = commonCodes.stream().map(mapper::toEntity).toList();
        jpaRepository.saveAll(entities);
    }
}
