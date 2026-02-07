package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerContractJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerContractQueryDslRepository;
import com.ryuqq.marketplace.application.seller.port.out.query.SellerContractQueryPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerContract;
import com.ryuqq.marketplace.domain.seller.id.SellerContractId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * SellerContractQueryAdapter - 셀러 계약 정보 조회 어댑터.
 *
 * <p>SellerContractQueryPort를 구현하여 영속성 계층과 연결합니다.
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
public class SellerContractQueryAdapter implements SellerContractQueryPort {

    private final SellerContractQueryDslRepository queryDslRepository;
    private final SellerContractJpaEntityMapper mapper;

    public SellerContractQueryAdapter(
            SellerContractQueryDslRepository queryDslRepository,
            SellerContractJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 계약 정보 조회.
     *
     * @param id 계약 ID
     * @return 계약 정보 Optional
     */
    @Override
    public Optional<SellerContract> findById(SellerContractId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    /**
     * 셀러 ID로 계약 정보 조회.
     *
     * @param sellerId 셀러 ID
     * @return 계약 정보 Optional
     */
    @Override
    public Optional<SellerContract> findBySellerId(SellerId sellerId) {
        return queryDslRepository.findBySellerId(sellerId.value()).map(mapper::toDomain);
    }

    /**
     * 셀러 ID로 계약 정보 존재 여부 확인.
     *
     * @param sellerId 셀러 ID
     * @return 존재하면 true
     */
    @Override
    public boolean existsBySellerId(SellerId sellerId) {
        return queryDslRepository.existsBySellerId(sellerId.value());
    }
}
