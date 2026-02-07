package com.ryuqq.marketplace.application.sellerapplication.port.out.query;

import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import com.ryuqq.marketplace.domain.sellerapplication.id.SellerApplicationId;
import com.ryuqq.marketplace.domain.sellerapplication.query.SellerApplicationSearchCriteria;
import java.util.List;
import java.util.Optional;

/**
 * SellerApplication Query Port.
 *
 * <p>입점 신청 조회 포트입니다.
 *
 * @author ryu-qqq
 */
public interface SellerApplicationQueryPort {

    /**
     * ID로 입점 신청을 조회합니다.
     *
     * @param id 신청 ID
     * @return 입점 신청 Optional
     */
    Optional<SellerApplication> findById(SellerApplicationId id);

    /**
     * ID로 입점 신청 존재 여부를 확인합니다.
     *
     * @param id 신청 ID
     * @return 존재 여부
     */
    boolean existsById(SellerApplicationId id);

    /**
     * 사업자등록번호로 대기 중인 신청 존재 여부를 확인합니다.
     *
     * @param registrationNumber 사업자등록번호
     * @return 존재 여부
     */
    boolean existsPendingByRegistrationNumber(String registrationNumber);

    /**
     * 검색 조건으로 입점 신청 목록을 조회합니다.
     *
     * @param criteria 검색 조건
     * @return 입점 신청 목록
     */
    List<SellerApplication> findByCriteria(SellerApplicationSearchCriteria criteria);

    /**
     * 검색 조건에 해당하는 전체 개수를 조회합니다.
     *
     * @param criteria 검색 조건
     * @return 전체 개수
     */
    long countByCriteria(SellerApplicationSearchCriteria criteria);
}
