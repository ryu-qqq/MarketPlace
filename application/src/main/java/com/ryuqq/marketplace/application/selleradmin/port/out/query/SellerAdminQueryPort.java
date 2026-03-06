package com.ryuqq.marketplace.application.selleradmin.port.out.query;

import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import com.ryuqq.marketplace.domain.selleradmin.query.SellerAdminSearchCriteria;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import java.util.List;
import java.util.Optional;

/**
 * SellerAdmin Query Port.
 *
 * <p>셀러 관리자 조회를 위한 Port-Out 인터페이스입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public interface SellerAdminQueryPort {

    /**
     * ID로 셀러 관리자를 조회합니다.
     *
     * @param sellerAdminId 셀러 관리자 ID
     * @return 셀러 관리자 (Optional)
     */
    Optional<SellerAdmin> findById(SellerAdminId sellerAdminId);

    /**
     * AuthHub 사용자 ID로 셀러 관리자를 조회합니다.
     *
     * @param authUserId AuthHub 사용자 ID
     * @return 셀러 관리자 (Optional)
     */
    Optional<SellerAdmin> findByAuthUserId(String authUserId);

    /**
     * 셀러 ID와 셀러 관리자 ID로 조회합니다.
     *
     * @param sellerId 셀러 ID
     * @param sellerAdminId 셀러 관리자 ID
     * @return 셀러 관리자 (Optional)
     */
    Optional<SellerAdmin> findBySellerIdAndId(SellerId sellerId, SellerAdminId sellerAdminId);

    /**
     * 셀러 ID, 셀러 관리자 ID, 상태로 조회합니다.
     *
     * @param sellerId 셀러 ID
     * @param sellerAdminId 셀러 관리자 ID
     * @param statuses 조회할 상태 목록
     * @return 셀러 관리자 (Optional)
     */
    Optional<SellerAdmin> findBySellerIdAndIdAndStatuses(
            SellerId sellerId, SellerAdminId sellerAdminId, List<SellerAdminStatus> statuses);

    /**
     * 셀러 관리자 ID와 상태로 조회합니다.
     *
     * @param sellerAdminId 셀러 관리자 ID
     * @param statuses 조회할 상태 목록
     * @return 셀러 관리자 (Optional)
     */
    Optional<SellerAdmin> findByIdAndStatuses(
            SellerAdminId sellerAdminId, List<SellerAdminStatus> statuses);

    /**
     * ID 목록으로 셀러 관리자를 일괄 조회합니다.
     *
     * @param sellerAdminIds 셀러 관리자 ID 목록
     * @return 셀러 관리자 목록
     */
    List<SellerAdmin> findAllByIds(List<SellerAdminId> sellerAdminIds);

    /**
     * 검색 조건으로 셀러 관리자 목록을 조회합니다.
     *
     * @param criteria 검색 조건
     * @return 셀러 관리자 목록
     */
    List<SellerAdmin> findByCriteria(SellerAdminSearchCriteria criteria);

    /**
     * 검색 조건에 해당하는 전체 개수를 조회합니다.
     *
     * @param criteria 검색 조건
     * @return 전체 개수
     */
    long countByCriteria(SellerAdminSearchCriteria criteria);

    /**
     * 로그인 ID 존재 여부를 확인합니다.
     *
     * @param loginId 로그인 ID
     * @return 존재 여부
     */
    boolean existsByLoginId(String loginId);

    /**
     * 이름과 로그인 ID로 셀러 관리자를 조회합니다.
     *
     * @param name 관리자 이름
     * @param loginId 로그인 ID
     * @return 셀러 관리자 (Optional)
     */
    Optional<SellerAdmin> findByNameAndLoginId(String name, String loginId);
}
