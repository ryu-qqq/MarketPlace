package com.ryuqq.marketplace.application.selleradmin.manager;

import com.ryuqq.marketplace.application.selleradmin.port.out.query.SellerAdminQueryPort;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminNotFoundException;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import com.ryuqq.marketplace.domain.selleradmin.query.SellerAdminSearchCriteria;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SellerAdmin Read Manager.
 *
 * <p>셀러 관리자 조회를 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Component
public class SellerAdminReadManager {

    private final SellerAdminQueryPort queryPort;

    public SellerAdminReadManager(SellerAdminQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * AuthHub 사용자 ID로 셀러 관리자를 조회합니다.
     *
     * @param authUserId AuthHub 사용자 ID
     * @return 셀러 관리자 (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<SellerAdmin> findByAuthUserId(String authUserId) {
        return queryPort.findByAuthUserId(authUserId);
    }

    /**
     * ID로 셀러 관리자를 조회합니다.
     *
     * @param sellerAdminId 셀러 관리자 ID
     * @return 셀러 관리자
     * @throws SellerAdminNotFoundException 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public SellerAdmin getById(SellerAdminId sellerAdminId) {
        return queryPort
                .findById(sellerAdminId)
                .orElseThrow(() -> new SellerAdminNotFoundException(sellerAdminId.value()));
    }

    /**
     * 셀러 ID와 셀러 관리자 ID로 조회합니다.
     *
     * @param sellerId 셀러 ID
     * @param sellerAdminId 셀러 관리자 ID
     * @return 셀러 관리자
     * @throws SellerAdminNotFoundException 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public SellerAdmin getBySellerIdAndId(SellerId sellerId, SellerAdminId sellerAdminId) {
        return queryPort
                .findBySellerIdAndId(sellerId, sellerAdminId)
                .orElseThrow(() -> new SellerAdminNotFoundException(sellerAdminId.value()));
    }

    /**
     * 셀러 ID, 셀러 관리자 ID, 상태로 조회합니다.
     *
     * @param sellerId 셀러 ID
     * @param sellerAdminId 셀러 관리자 ID
     * @param statuses 조회할 상태 목록
     * @return 셀러 관리자
     * @throws SellerAdminNotFoundException 조건에 맞는 셀러 관리자가 없는 경우
     */
    @Transactional(readOnly = true)
    public SellerAdmin getBySellerIdAndIdAndStatuses(
            SellerId sellerId, SellerAdminId sellerAdminId, List<SellerAdminStatus> statuses) {
        return queryPort
                .findBySellerIdAndIdAndStatuses(sellerId, sellerAdminId, statuses)
                .orElseThrow(
                        () ->
                                SellerAdminNotFoundException.withMessage(
                                        "조건에 맞는 셀러 관리자를 찾을 수 없습니다. sellerAdminId="
                                                + sellerAdminId.value()));
    }

    /**
     * 셀러 관리자 ID와 상태로 조회합니다.
     *
     * @param sellerAdminId 셀러 관리자 ID
     * @param statuses 조회할 상태 목록
     * @return 셀러 관리자
     * @throws SellerAdminNotFoundException 조건에 맞는 셀러 관리자가 없는 경우
     */
    @Transactional(readOnly = true)
    public SellerAdmin getByIdAndStatuses(
            SellerAdminId sellerAdminId, List<SellerAdminStatus> statuses) {
        return queryPort
                .findByIdAndStatuses(sellerAdminId, statuses)
                .orElseThrow(
                        () ->
                                SellerAdminNotFoundException.withMessage(
                                        "조건에 맞는 셀러 관리자를 찾을 수 없습니다. sellerAdminId="
                                                + sellerAdminId.value()));
    }

    /**
     * ID 목록으로 셀러 관리자를 일괄 조회합니다.
     *
     * @param sellerAdminIds 셀러 관리자 ID 목록
     * @return 셀러 관리자 목록
     */
    @Transactional(readOnly = true)
    public List<SellerAdmin> getAllByIds(List<SellerAdminId> sellerAdminIds) {
        return queryPort.findAllByIds(sellerAdminIds);
    }

    /**
     * 검색 조건으로 셀러 관리자 목록을 조회합니다.
     *
     * @param criteria 검색 조건
     * @return 셀러 관리자 목록
     */
    @Transactional(readOnly = true)
    public List<SellerAdmin> findByCriteria(SellerAdminSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    /**
     * 검색 조건에 해당하는 전체 개수를 조회합니다.
     *
     * @param criteria 검색 조건
     * @return 전체 개수
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SellerAdminSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    /**
     * 로그인 ID 존재 여부를 확인합니다.
     *
     * @param loginId 로그인 ID
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByLoginId(String loginId) {
        return queryPort.existsByLoginId(loginId);
    }

    /**
     * 이름과 로그인 ID로 셀러 관리자를 조회합니다.
     *
     * @param name 관리자 이름
     * @param loginId 로그인 ID
     * @return 셀러 관리자 (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<SellerAdmin> findByNameAndLoginId(String name, String loginId) {
        return queryPort.findByNameAndLoginId(name, loginId);
    }
}
