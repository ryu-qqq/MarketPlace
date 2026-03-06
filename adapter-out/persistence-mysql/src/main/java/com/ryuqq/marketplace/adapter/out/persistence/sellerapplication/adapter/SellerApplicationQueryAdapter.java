package com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.entity.SellerApplicationJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.mapper.SellerApplicationJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.repository.SellerApplicationQueryDslRepository;
import com.ryuqq.marketplace.application.sellerapplication.port.out.query.SellerApplicationQueryPort;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import com.ryuqq.marketplace.domain.sellerapplication.id.SellerApplicationId;
import com.ryuqq.marketplace.domain.sellerapplication.query.SellerApplicationSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * SellerApplicationQueryAdapter - 입점 신청 조회 어댑터.
 *
 * <p>SellerApplicationQueryPort를 구현하여 영속성 계층과 연결합니다.
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
public class SellerApplicationQueryAdapter implements SellerApplicationQueryPort {

    private final SellerApplicationQueryDslRepository queryDslRepository;
    private final SellerApplicationJpaEntityMapper mapper;

    public SellerApplicationQueryAdapter(
            SellerApplicationQueryDslRepository queryDslRepository,
            SellerApplicationJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 입점 신청 조회.
     *
     * @param id 신청 ID
     * @return 입점 신청 Optional
     */
    @Override
    public Optional<SellerApplication> findById(SellerApplicationId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    /**
     * ID로 입점 신청 존재 여부 확인.
     *
     * @param id 신청 ID
     * @return 존재 여부
     */
    @Override
    public boolean existsById(SellerApplicationId id) {
        return queryDslRepository.existsById(id.value());
    }

    /**
     * 사업자등록번호로 대기 중인 신청 존재 여부 확인.
     *
     * @param registrationNumber 사업자등록번호
     * @return 존재 여부
     */
    @Override
    public boolean existsPendingByRegistrationNumber(String registrationNumber) {
        return queryDslRepository.existsPendingByRegistrationNumber(registrationNumber);
    }

    /**
     * 검색 조건으로 입점 신청 목록 조회.
     *
     * @param criteria 검색 조건
     * @return 입점 신청 목록
     */
    @Override
    public List<SellerApplication> findByCriteria(SellerApplicationSearchCriteria criteria) {
        List<SellerApplicationJpaEntity> entities = queryDslRepository.findByCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 검색 조건에 해당하는 전체 개수 조회.
     *
     * @param criteria 검색 조건
     * @return 전체 개수
     */
    @Override
    public long countByCriteria(SellerApplicationSearchCriteria criteria) {
        return queryDslRepository.countByCriteria(criteria);
    }

    /**
     * 승인된 셀러 ID로 입점 신청 조회.
     *
     * @param sellerId 승인된 셀러 ID
     * @return 입점 신청 Optional
     */
    @Override
    public Optional<SellerApplication> findByApprovedSellerId(SellerId sellerId) {
        return queryDslRepository.findByApprovedSellerId(sellerId.value()).map(mapper::toDomain);
    }
}
