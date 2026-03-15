package com.ryuqq.marketplace.application.cancel.port.out.query;

import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchCriteria;
import java.util.List;
import java.util.Optional;

/** 취소 Query Port. */
public interface CancelQueryPort {

    /**
     * ID로 취소를 조회합니다.
     *
     * @param id 취소 ID
     * @return 취소 (없으면 empty)
     */
    Optional<Cancel> findById(CancelId id);

    /**
     * 주문 ID로 취소를 조회합니다.
     *
     * @param orderId 주문 ID
     * @return 취소 (없으면 empty)
     */
    Optional<Cancel> findByOrderId(String orderId);

    /**
     * 주문 ID 목록으로 취소 목록을 조회합니다.
     *
     * @param orderIds 주문 ID 목록
     * @return 취소 목록
     */
    List<Cancel> findByOrderIds(List<String> orderIds);

    /**
     * 검색 조건으로 취소 목록을 조회합니다.
     *
     * @param criteria 검색 조건
     * @return 취소 목록
     */
    List<Cancel> findByCriteria(CancelSearchCriteria criteria);

    /**
     * 검색 조건에 해당하는 취소 건수를 반환합니다.
     *
     * @param criteria 검색 조건
     * @return 건수
     */
    long countByCriteria(CancelSearchCriteria criteria);
}
