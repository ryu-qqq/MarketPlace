package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerCsJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerCsQueryDslRepository;
import com.ryuqq.marketplace.application.seller.port.out.query.SellerCsQueryPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import com.ryuqq.marketplace.domain.seller.id.SellerCsId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * SellerCsQueryAdapter - 셀러 CS 정보 조회 어댑터.
 *
 * <p>SellerCsQueryPort를 구현하여 영속성 계층과 연결합니다.
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
public class SellerCsQueryAdapter implements SellerCsQueryPort {

    private final SellerCsQueryDslRepository queryDslRepository;
    private final SellerCsJpaEntityMapper mapper;

    public SellerCsQueryAdapter(
            SellerCsQueryDslRepository queryDslRepository, SellerCsJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 CS 정보 조회.
     *
     * @param id CS ID
     * @return CS 정보 Optional
     */
    @Override
    public Optional<SellerCs> findById(SellerCsId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    /**
     * 셀러 ID로 CS 정보 조회.
     *
     * @param sellerId 셀러 ID
     * @return CS 정보 Optional
     */
    @Override
    public Optional<SellerCs> findBySellerId(SellerId sellerId) {
        return queryDslRepository.findBySellerId(sellerId.value()).map(mapper::toDomain);
    }

    /**
     * 셀러 ID로 CS 정보 존재 여부 확인.
     *
     * @param sellerId 셀러 ID
     * @return 존재하면 true
     */
    @Override
    public boolean existsBySellerId(SellerId sellerId) {
        return queryDslRepository.existsBySellerId(sellerId.value());
    }
}
