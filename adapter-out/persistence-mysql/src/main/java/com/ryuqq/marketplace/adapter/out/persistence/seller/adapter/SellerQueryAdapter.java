package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerQueryDslRepository;
import com.ryuqq.marketplace.application.seller.port.out.query.SellerQueryPort;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * SellerQueryAdapter - 셀러 조회 어댑터.
 *
 * <p>SellerQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * <p>활성화 조건: persistence.legacy.seller.enabled=false
 */
@Component
@ConditionalOnProperty(
        name = "persistence.legacy.seller.enabled",
        havingValue = "false",
        matchIfMissing = true)
public class SellerQueryAdapter implements SellerQueryPort {

    private final SellerQueryDslRepository queryDslRepository;
    private final SellerJpaEntityMapper mapper;

    public SellerQueryAdapter(
            SellerQueryDslRepository queryDslRepository, SellerJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 셀러 조회.
     *
     * @param id 셀러 ID
     * @return 셀러 Optional
     */
    @Override
    public Optional<Seller> findById(SellerId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    /**
     * ID 목록으로 셀러 목록 조회.
     *
     * @param ids 셀러 ID 목록
     * @return 셀러 목록
     */
    @Override
    public List<Seller> findByIds(List<SellerId> ids) {
        List<Long> idValues = ids.stream().map(SellerId::value).toList();
        List<SellerJpaEntity> entities = queryDslRepository.findByIds(idValues);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * ID 존재 여부 확인.
     *
     * @param id 셀러 ID
     * @return 존재하면 true
     */
    @Override
    public boolean existsById(SellerId id) {
        return queryDslRepository.existsById(id.value());
    }

    /**
     * 셀러명 존재 여부 확인.
     *
     * @param sellerName 셀러명
     * @return 존재하면 true
     */
    @Override
    public boolean existsBySellerName(String sellerName) {
        return queryDslRepository.existsBySellerName(sellerName);
    }

    /**
     * 셀러명 존재 여부 확인 (특정 ID 제외).
     *
     * @param sellerName 셀러명
     * @param excludeId 제외할 셀러 ID
     * @return 존재하면 true
     */
    @Override
    public boolean existsBySellerNameExcluding(String sellerName, SellerId excludeId) {
        return queryDslRepository.existsBySellerNameExcluding(sellerName, excludeId.value());
    }

    /**
     * 검색 조건으로 셀러 목록 조회.
     *
     * @param criteria 검색 조건
     * @return 셀러 목록
     */
    @Override
    public List<Seller> findByCriteria(SellerSearchCriteria criteria) {
        List<SellerJpaEntity> entities = queryDslRepository.findByCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 검색 조건으로 셀러 개수 조회.
     *
     * @param criteria 검색 조건
     * @return 셀러 개수
     */
    @Override
    public long countByCriteria(SellerSearchCriteria criteria) {
        return queryDslRepository.countByCriteria(criteria);
    }

    /**
     * organizationId로 셀러 ID 조회.
     *
     * @param organizationId 인증 조직 ID
     * @return 셀러 ID Optional
     */
    @Override
    public Optional<Long> findSellerIdByOrganizationId(String organizationId) {
        return queryDslRepository.findIdByOrganizationId(organizationId);
    }
}
