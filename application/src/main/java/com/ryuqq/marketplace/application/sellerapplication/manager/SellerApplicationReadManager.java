package com.ryuqq.marketplace.application.sellerapplication.manager;

import com.ryuqq.marketplace.application.sellerapplication.port.out.query.SellerApplicationQueryPort;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import com.ryuqq.marketplace.domain.sellerapplication.exception.SellerApplicationNotFoundException;
import com.ryuqq.marketplace.domain.sellerapplication.id.SellerApplicationId;
import com.ryuqq.marketplace.domain.sellerapplication.query.SellerApplicationSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SellerApplication Read Manager.
 *
 * <p>입점 신청 조회를 담당합니다.
 *
 * @author ryu-qqq
 */
@Component
public class SellerApplicationReadManager {

    private final SellerApplicationQueryPort queryPort;

    public SellerApplicationReadManager(SellerApplicationQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * ID로 입점 신청을 조회합니다.
     *
     * @param id 신청 ID
     * @return 입점 신청
     * @throws SellerApplicationNotFoundException 신청이 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public SellerApplication getById(SellerApplicationId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new SellerApplicationNotFoundException(id.value()));
    }

    /**
     * ID로 입점 신청 존재 여부를 확인합니다.
     *
     * @param id 신청 ID
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsById(SellerApplicationId id) {
        return queryPort.existsById(id);
    }

    /**
     * 사업자등록번호로 대기 중인 신청 존재 여부를 확인합니다.
     *
     * @param registrationNumber 사업자등록번호
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsPendingByRegistrationNumber(String registrationNumber) {
        return queryPort.existsPendingByRegistrationNumber(registrationNumber);
    }

    /**
     * 검색 조건으로 입점 신청 목록을 조회합니다.
     *
     * @param criteria 검색 조건
     * @return 입점 신청 목록
     */
    @Transactional(readOnly = true)
    public List<SellerApplication> findByCriteria(SellerApplicationSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    /**
     * 검색 조건에 해당하는 전체 개수를 조회합니다.
     *
     * @param criteria 검색 조건
     * @return 전체 개수
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SellerApplicationSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }
}
