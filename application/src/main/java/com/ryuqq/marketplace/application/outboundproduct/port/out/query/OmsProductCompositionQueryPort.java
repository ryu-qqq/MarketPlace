package com.ryuqq.marketplace.application.outboundproduct.port.out.query;

import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductListResult;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSearchCriteria;
import java.util.List;

/** OMS 상품 목록 Composition 조회 포트 (크로스 도메인 JOIN). */
public interface OmsProductCompositionQueryPort {

    /** 검색 조건에 맞는 OMS 상품 목록 조회. */
    List<OmsProductListResult> findByCriteria(OmsProductSearchCriteria criteria);

    /** 검색 조건에 맞는 전체 건수 조회. */
    long countByCriteria(OmsProductSearchCriteria criteria);
}
