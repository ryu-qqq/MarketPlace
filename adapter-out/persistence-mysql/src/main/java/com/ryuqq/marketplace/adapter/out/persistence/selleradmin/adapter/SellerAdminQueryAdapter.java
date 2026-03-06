package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper.SellerAdminJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository.SellerAdminQueryDslRepository;
import com.ryuqq.marketplace.application.selleradmin.port.out.query.SellerAdminQueryPort;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import com.ryuqq.marketplace.domain.selleradmin.query.SellerAdminSearchCriteria;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * SellerAdminQueryAdapter - 셀러 관리자 조회 어댑터.
 *
 * <p>SellerAdminQueryPort를 구현하여 영속성 계층과 연결합니다.
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
public class SellerAdminQueryAdapter implements SellerAdminQueryPort {

    private final SellerAdminQueryDslRepository queryDslRepository;
    private final SellerAdminJpaEntityMapper mapper;

    public SellerAdminQueryAdapter(
            SellerAdminQueryDslRepository queryDslRepository, SellerAdminJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    /**
     * ID로 셀러 관리자 조회.
     *
     * @param sellerAdminId 셀러 관리자 ID
     * @return 셀러 관리자 Optional
     */
    @Override
    public Optional<SellerAdmin> findById(SellerAdminId sellerAdminId) {
        return queryDslRepository.findById(sellerAdminId.value()).map(mapper::toDomain);
    }

    /**
     * AuthHub 사용자 ID로 셀러 관리자 조회.
     *
     * @param authUserId AuthHub 사용자 ID
     * @return 셀러 관리자 Optional
     */
    @Override
    public Optional<SellerAdmin> findByAuthUserId(String authUserId) {
        return queryDslRepository.findByAuthUserId(authUserId).map(mapper::toDomain);
    }

    /**
     * 셀러 ID와 셀러 관리자 ID로 조회.
     *
     * @param sellerId 셀러 ID
     * @param sellerAdminId 셀러 관리자 ID
     * @return 셀러 관리자 Optional
     */
    @Override
    public Optional<SellerAdmin> findBySellerIdAndId(
            SellerId sellerId, SellerAdminId sellerAdminId) {
        return queryDslRepository
                .findBySellerIdAndId(sellerId.value(), sellerAdminId.value())
                .map(mapper::toDomain);
    }

    /**
     * 셀러 ID, 셀러 관리자 ID, 상태로 조회.
     *
     * @param sellerId 셀러 ID
     * @param sellerAdminId 셀러 관리자 ID
     * @param statuses 조회할 상태 목록
     * @return 셀러 관리자 Optional
     */
    @Override
    public Optional<SellerAdmin> findBySellerIdAndIdAndStatuses(
            SellerId sellerId, SellerAdminId sellerAdminId, List<SellerAdminStatus> statuses) {
        return queryDslRepository
                .findBySellerIdAndIdAndStatuses(sellerId.value(), sellerAdminId.value(), statuses)
                .map(mapper::toDomain);
    }

    /**
     * 셀러 관리자 ID와 상태로 조회.
     *
     * @param sellerAdminId 셀러 관리자 ID
     * @param statuses 조회할 상태 목록
     * @return 셀러 관리자 Optional
     */
    @Override
    public Optional<SellerAdmin> findByIdAndStatuses(
            SellerAdminId sellerAdminId, List<SellerAdminStatus> statuses) {
        return queryDslRepository
                .findByIdAndStatuses(sellerAdminId.value(), statuses)
                .map(mapper::toDomain);
    }

    /**
     * ID 목록으로 셀러 관리자 일괄 조회.
     *
     * @param sellerAdminIds 셀러 관리자 ID 목록
     * @return 셀러 관리자 목록
     */
    @Override
    public List<SellerAdmin> findAllByIds(List<SellerAdminId> sellerAdminIds) {
        List<String> ids = sellerAdminIds.stream().map(SellerAdminId::value).toList();
        List<SellerAdminJpaEntity> entities = queryDslRepository.findAllByIds(ids);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 검색 조건으로 셀러 관리자 목록 조회.
     *
     * @param criteria 검색 조건
     * @return 셀러 관리자 목록
     */
    @Override
    public List<SellerAdmin> findByCriteria(SellerAdminSearchCriteria criteria) {
        List<SellerAdminJpaEntity> entities = queryDslRepository.findByCriteria(criteria);
        return entities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 검색 조건으로 셀러 관리자 개수 조회.
     *
     * @param criteria 검색 조건
     * @return 셀러 관리자 개수
     */
    @Override
    public long countByCriteria(SellerAdminSearchCriteria criteria) {
        return queryDslRepository.countByCriteria(criteria);
    }

    /**
     * 로그인 ID 존재 여부 확인.
     *
     * @param loginId 로그인 ID
     * @return 존재하면 true
     */
    @Override
    public boolean existsByLoginId(String loginId) {
        return queryDslRepository.existsByLoginId(loginId);
    }

    /**
     * 이름과 로그인 ID로 셀러 관리자 조회.
     *
     * @param name 관리자 이름
     * @param loginId 로그인 ID
     * @return 셀러 관리자 Optional
     */
    @Override
    public Optional<SellerAdmin> findByNameAndLoginId(String name, String loginId) {
        return queryDslRepository.findByNameAndLoginId(name, loginId).map(mapper::toDomain);
    }
}
