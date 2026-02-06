package com.ryuqq.marketplace.application.selleradmin.port.in.query;

import com.ryuqq.marketplace.application.selleradmin.dto.query.SellerAdminApplicationSearchParams;
import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminApplicationPageResult;

/**
 * 셀러 관리자 가입 신청 목록 조회 UseCase.
 *
 * <p>운영자 또는 셀러 관리자가 신청 목록을 조회합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public interface SearchSellerAdminApplicationsUseCase {

    /**
     * 가입 신청 목록을 조회합니다.
     *
     * @param params 검색 파라미터
     * @return 신청 목록 (페이징)
     */
    SellerAdminApplicationPageResult execute(SellerAdminApplicationSearchParams params);
}
