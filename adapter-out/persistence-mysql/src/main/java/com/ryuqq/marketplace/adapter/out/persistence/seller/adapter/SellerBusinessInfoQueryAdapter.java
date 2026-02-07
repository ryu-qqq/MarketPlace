package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerBusinessInfoJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerBusinessInfoQueryDslRepository;
import com.ryuqq.marketplace.application.seller.port.out.query.SellerBusinessInfoQueryPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.id.SellerBusinessInfoId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * SellerBusinessInfoQueryAdapter - 셀러 사업자 정보 조회 어댑터.
 *
 * <p>SellerBusinessInfoQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * <p>활성화 조건: persistence.legacy.seller-business-info.enabled=false
 */
@Component
@ConditionalOnProperty(
        name = "persistence.legacy.seller-business-info.enabled",
        havingValue = "false")
public class SellerBusinessInfoQueryAdapter implements SellerBusinessInfoQueryPort {

    private final SellerBusinessInfoQueryDslRepository queryDslRepository;
    private final SellerBusinessInfoJpaEntityMapper mapper;

    public SellerBusinessInfoQueryAdapter(
            SellerBusinessInfoQueryDslRepository queryDslRepository,
            SellerBusinessInfoJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 사업자 정보 조회.
     *
     * @param id 사업자 정보 ID
     * @return 사업자 정보 Optional
     */
    @Override
    public Optional<SellerBusinessInfo> findById(SellerBusinessInfoId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    /**
     * 셀러 ID로 사업자 정보 조회.
     *
     * @param sellerId 셀러 ID
     * @return 사업자 정보 Optional
     */
    @Override
    public Optional<SellerBusinessInfo> findBySellerId(SellerId sellerId) {
        return queryDslRepository.findBySellerId(sellerId.value()).map(mapper::toDomain);
    }

    /**
     * 셀러 ID 존재 여부 확인.
     *
     * @param sellerId 셀러 ID
     * @return 존재하면 true
     */
    @Override
    public boolean existsBySellerId(SellerId sellerId) {
        return queryDslRepository.existsBySellerId(sellerId.value());
    }

    /**
     * 사업자등록번호 존재 여부 확인.
     *
     * @param registrationNumber 사업자등록번호
     * @return 존재하면 true
     */
    @Override
    public boolean existsByRegistrationNumber(String registrationNumber) {
        return queryDslRepository.existsByRegistrationNumber(registrationNumber);
    }

    /**
     * 사업자등록번호 존재 여부 확인 (특정 셀러 ID 제외).
     *
     * @param registrationNumber 사업자등록번호
     * @param excludeId 제외할 셀러 ID
     * @return 존재하면 true
     */
    @Override
    public boolean existsByRegistrationNumberExcluding(
            String registrationNumber, SellerId excludeId) {
        return queryDslRepository.existsByRegistrationNumberExcluding(
                registrationNumber, excludeId.value());
    }
}
