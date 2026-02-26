package com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.entity.RefundPolicyJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.mapper.RefundPolicyJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.repository.RefundPolicyQueryDslRepository;
import com.ryuqq.marketplace.application.refundpolicy.port.out.query.RefundPolicyQueryPort;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.refundpolicy.query.RefundPolicySearchCriteria;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * RefundPolicyQueryAdapter - 환불 정책 Query 어댑터.
 *
 * <p>RefundPolicyQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class RefundPolicyQueryAdapter implements RefundPolicyQueryPort {

    private final RefundPolicyQueryDslRepository queryDslRepository;
    private final RefundPolicyJpaEntityMapper mapper;

    /**
     * 생성자 주입.
     *
     * <p>PER-ADP-006: Mapper + QueryDslRepository 의존.
     *
     * @param queryDslRepository QueryDSL 레포지토리
     * @param mapper Entity-Domain 매퍼
     */
    public RefundPolicyQueryAdapter(
            RefundPolicyQueryDslRepository queryDslRepository, RefundPolicyJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 환불 정책 조회.
     *
     * @param id 환불 정책 ID
     * @return 환불 정책 Optional
     */
    @Override
    public Optional<RefundPolicy> findById(RefundPolicyId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    /**
     * ID 목록으로 환불 정책 목록 조회.
     *
     * @param ids 환불 정책 ID 목록
     * @return 환불 정책 목록
     */
    @Override
    public List<RefundPolicy> findByIds(List<RefundPolicyId> ids) {
        List<Long> idValues = ids.stream().map(RefundPolicyId::value).toList();
        List<RefundPolicyJpaEntity> entities = queryDslRepository.findByIds(idValues);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 셀러의 기본 정책 조회.
     *
     * @param sellerId 셀러 ID
     * @return 기본 환불 정책 Optional
     */
    @Override
    public Optional<RefundPolicy> findDefaultBySellerId(SellerId sellerId) {
        return queryDslRepository.findDefaultBySellerId(sellerId.value()).map(mapper::toDomain);
    }

    /**
     * 셀러 ID와 정책 ID로 조회.
     *
     * @param sellerId 셀러 ID
     * @param policyId 정책 ID
     * @return 환불 정책 Optional
     */
    @Override
    public Optional<RefundPolicy> findBySellerIdAndId(SellerId sellerId, RefundPolicyId policyId) {
        return queryDslRepository
                .findBySellerIdAndId(sellerId.value(), policyId.value())
                .map(mapper::toDomain);
    }

    /**
     * 검색 조건으로 환불 정책 목록 조회.
     *
     * @param criteria 검색 조건
     * @return 환불 정책 목록
     */
    @Override
    public List<RefundPolicy> findByCriteria(RefundPolicySearchCriteria criteria) {
        List<RefundPolicyJpaEntity> entities = queryDslRepository.findByCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 검색 조건으로 환불 정책 개수 조회.
     *
     * @param criteria 검색 조건
     * @return 환불 정책 개수
     */
    @Override
    public long countByCriteria(RefundPolicySearchCriteria criteria) {
        return queryDslRepository.countByCriteria(criteria);
    }

    /**
     * 셀러의 활성 정책 개수 조회.
     *
     * <p>POL-DEACT-002: 마지막 활성 정책 비활성화 검증에 사용
     *
     * @param sellerId 셀러 ID
     * @return 활성 정책 개수
     */
    @Override
    public long countActiveBySellerId(SellerId sellerId) {
        return queryDslRepository.countActiveBySellerId(sellerId.value());
    }
}
