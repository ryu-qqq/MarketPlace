package com.ryuqq.marketplace.application.selleradmin.port.in.query;

import com.ryuqq.marketplace.application.selleradmin.dto.query.VerifySellerAdminQuery;
import com.ryuqq.marketplace.application.selleradmin.dto.response.VerifySellerAdminResult;

/**
 * 셀러 관리자 본인 확인 UseCase.
 *
 * <p>이름과 핸드폰 번호로 셀러 관리자 존재 여부 및 상태를 확인합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public interface VerifySellerAdminUseCase {

    /**
     * 셀러 관리자 본인 확인을 수행합니다.
     *
     * @param query 확인 Query (이름, 핸드폰 번호)
     * @return 존재 여부 및 상태
     */
    VerifySellerAdminResult execute(VerifySellerAdminQuery query);
}
