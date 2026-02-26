package com.ryuqq.marketplace.application.sellerapplication.port.in.query;

import com.ryuqq.marketplace.application.sellerapplication.dto.query.SellerApplicationSearchParams;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationPageResult;

/**
 * 셀러 입점 신청 목록 조회 UseCase.
 *
 * <p>검색 조건과 페이징을 기반으로 입점 신청 목록을 조회합니다.
 *
 * @author ryu-qqq
 */
public interface SearchSellerApplicationByOffsetUseCase {

    /**
     * 입점 신청 목록을 조회합니다.
     *
     * @param params 검색 파라미터
     * @return 페이징된 신청 목록
     */
    SellerApplicationPageResult execute(SellerApplicationSearchParams params);
}
