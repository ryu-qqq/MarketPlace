package com.ryuqq.marketplace.adapter.out.persistence.commoncode.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.commoncode.entity.CommonCodeJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.mapper.CommonCodeJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.repository.CommonCodeQueryDslRepository;
import com.ryuqq.marketplace.application.commoncode.port.out.query.CommonCodeQueryPort;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
import com.ryuqq.marketplace.domain.commoncode.query.CommonCodeSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * CommonCodeQueryAdapter - 공통 코드 Query 어댑터.
 *
 * <p>CommonCodeQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * <p>활성화 조건: persistence.legacy.common-code.enabled=false
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(
        name = "persistence.legacy.common-code.enabled",
        havingValue = "false",
        matchIfMissing = true)
public class CommonCodeQueryAdapter implements CommonCodeQueryPort {

    private final CommonCodeQueryDslRepository queryDslRepository;
    private final CommonCodeJpaEntityMapper mapper;

    /**
     * 생성자 주입.
     *
     * <p>PER-ADP-006: Mapper + QueryDslRepository 의존.
     *
     * @param queryDslRepository QueryDSL 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public CommonCodeQueryAdapter(
            CommonCodeQueryDslRepository queryDslRepository, CommonCodeJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 공통 코드 조회.
     *
     * @param id 공통 코드 ID
     * @return 공통 코드 Optional
     */
    @Override
    public Optional<CommonCode> findById(CommonCodeId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    /**
     * ID 목록으로 공통 코드 목록 조회.
     *
     * @param ids 공통 코드 ID 목록
     * @return 공통 코드 목록
     */
    @Override
    public List<CommonCode> findByIds(List<CommonCodeId> ids) {
        List<Long> idValues = ids.stream().map(CommonCodeId::value).toList();
        List<CommonCodeJpaEntity> entities = queryDslRepository.findByIds(idValues);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 타입 ID + 코드 존재 여부 확인.
     *
     * @param commonCodeTypeId 공통 코드 타입 ID
     * @param code 코드값
     * @return 존재하면 true
     */
    @Override
    public boolean existsByCommonCodeTypeIdAndCode(Long commonCodeTypeId, String code) {
        return queryDslRepository.existsByCommonCodeTypeIdAndCode(commonCodeTypeId, code);
    }

    /**
     * 특정 타입 ID의 활성화된 공통 코드 존재 여부 확인.
     *
     * @param commonCodeTypeId 공통 코드 타입 ID
     * @return 활성화된 공통 코드 존재 여부
     */
    @Override
    public boolean existsActiveByCommonCodeTypeId(Long commonCodeTypeId) {
        return queryDslRepository.existsActiveByCommonCodeTypeId(commonCodeTypeId);
    }

    /**
     * 검색 조건으로 공통 코드 목록 조회.
     *
     * @param criteria 검색 조건
     * @return 공통 코드 목록
     */
    @Override
    public List<CommonCode> findByCriteria(CommonCodeSearchCriteria criteria) {
        List<CommonCodeJpaEntity> entities = queryDslRepository.findByCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 검색 조건으로 공통 코드 개수 조회.
     *
     * @param criteria 검색 조건
     * @return 공통 코드 개수
     */
    @Override
    public long countByCriteria(CommonCodeSearchCriteria criteria) {
        return queryDslRepository.countByCriteria(criteria);
    }
}
