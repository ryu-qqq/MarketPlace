package com.ryuqq.marketplace.application.outboundproduct.port.out.query;

import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryListResult;
import com.ryuqq.marketplace.domain.outboundproduct.query.SyncHistorySearchCriteria;
import java.util.List;

/** 연동 이력 Composition 조회 포트 (크로스 도메인 JOIN). */
public interface OmsSyncHistoryCompositionQueryPort {

    /** 검색 조건에 맞는 연동 이력 조회. */
    List<SyncHistoryListResult> findByCriteria(SyncHistorySearchCriteria criteria);

    /** 검색 조건에 맞는 전체 건수 조회. */
    long countByCriteria(SyncHistorySearchCriteria criteria);
}
