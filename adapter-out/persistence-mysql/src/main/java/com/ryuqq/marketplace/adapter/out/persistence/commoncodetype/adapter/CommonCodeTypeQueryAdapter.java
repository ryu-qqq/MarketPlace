package com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.entity.CommonCodeTypeJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.mapper.CommonCodeTypeJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.repository.CommonCodeTypeQueryDslRepository;
import com.ryuqq.marketplace.application.commoncodetype.port.out.query.CommonCodeTypeQueryPort;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * CommonCodeTypeQueryAdapter - 공통 코드 타입 조회 어댑터.
 *
 * <p>CommonCodeTypeQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * <p>활성화 조건: persistence.legacy.common-code-type.enabled=false
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(name = "persistence.legacy.common-code-type.enabled", havingValue = "false", matchIfMissing = true)
public class CommonCodeTypeQueryAdapter implements CommonCodeTypeQueryPort {

    private final CommonCodeTypeQueryDslRepository queryDslRepository;
    private final CommonCodeTypeJpaEntityMapper mapper;

    /**
     * 생성자 주입.
     *
     * <p>PER-ADP-006: Mapper + QueryDslRepository 의존.
     *
     * @param queryDslRepository QueryDSL 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public CommonCodeTypeQueryAdapter(
            CommonCodeTypeQueryDslRepository queryDslRepository,
            CommonCodeTypeJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 공통 코드 타입 조회.
     *
     * @param id 공통 코드 타입 ID
     * @return 공통 코드 타입 Optional
     */
    @Override
    public Optional<CommonCodeType> findById(CommonCodeTypeId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    /**
     * ID 목록으로 공통 코드 타입 목록 조회.
     *
     * @param ids 공통 코드 타입 ID 목록
     * @return 공통 코드 타입 목록
     */
    @Override
    public List<CommonCodeType> findByIds(List<CommonCodeTypeId> ids) {
        List<Long> idValues = ids.stream().map(CommonCodeTypeId::value).toList();
        List<CommonCodeTypeJpaEntity> entities = queryDslRepository.findByIds(idValues);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 코드 존재 여부 확인.
     *
     * @param code 코드
     * @return 존재하면 true
     */
    @Override
    public boolean existsByCode(String code) {
        return queryDslRepository.existsByCode(code);
    }

    /**
     * 표시 순서 존재 여부 확인 (특정 ID 제외).
     *
     * @param displayOrder 표시 순서
     * @param excludeId 제외할 ID
     * @return 존재하면 true
     */
    @Override
    public boolean existsByDisplayOrderExcludingId(int displayOrder, Long excludeId) {
        return queryDslRepository.existsByDisplayOrderExcludingId(displayOrder, excludeId);
    }

    /**
     * 검색 조건으로 공통 코드 타입 목록 조회.
     *
     * @param criteria 검색 조건
     * @return 공통 코드 타입 목록
     */
    @Override
    public List<CommonCodeType> findByCriteria(CommonCodeTypeSearchCriteria criteria) {
        List<CommonCodeTypeJpaEntity> entities = queryDslRepository.findByCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 검색 조건으로 공통 코드 타입 개수 조회.
     *
     * @param criteria 검색 조건
     * @return 공통 코드 타입 개수
     */
    @Override
    public long countByCriteria(CommonCodeTypeSearchCriteria criteria) {
        return queryDslRepository.countByCriteria(criteria);
    }
}
