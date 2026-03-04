package com.ryuqq.marketplace.application.selleradmin.service.query;

import com.ryuqq.marketplace.application.selleradmin.dto.query.VerifySellerAdminQuery;
import com.ryuqq.marketplace.application.selleradmin.dto.response.VerifySellerAdminResult;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminReadManager;
import com.ryuqq.marketplace.application.selleradmin.port.in.query.VerifySellerAdminUseCase;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminNotFoundException;
import org.springframework.stereotype.Service;

/**
 * VerifySellerAdminService - 셀러 관리자 본인 확인 Service.
 *
 * <p>이름과 로그인 ID로 셀러 관리자를 확인하고 핸드폰 번호를 반환합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Service
public class VerifySellerAdminService implements VerifySellerAdminUseCase {

    private final SellerAdminReadManager readManager;

    public VerifySellerAdminService(SellerAdminReadManager readManager) {
        this.readManager = readManager;
    }

    @Override
    public VerifySellerAdminResult execute(VerifySellerAdminQuery query) {
        SellerAdmin sellerAdmin =
                readManager
                        .findByNameAndLoginId(query.name(), query.loginId())
                        .orElseThrow(
                                () ->
                                        SellerAdminNotFoundException.withMessage(
                                                "일치하는 셀러 관리자를 찾을 수 없습니다. name="
                                                        + query.name()
                                                        + ", loginId="
                                                        + query.loginId()));

        return VerifySellerAdminResult.of(
                sellerAdmin.status().name(),
                sellerAdmin.idValue(),
                sellerAdmin.phoneNumberValue());
    }
}
