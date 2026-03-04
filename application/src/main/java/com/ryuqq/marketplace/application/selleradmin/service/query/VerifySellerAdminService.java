package com.ryuqq.marketplace.application.selleradmin.service.query;

import com.ryuqq.marketplace.application.selleradmin.dto.query.VerifySellerAdminQuery;
import com.ryuqq.marketplace.application.selleradmin.dto.response.VerifySellerAdminResult;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminReadManager;
import com.ryuqq.marketplace.application.selleradmin.port.in.query.VerifySellerAdminUseCase;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * VerifySellerAdminService - 셀러 관리자 본인 확인 Service.
 *
 * <p>이름과 핸드폰 번호로 셀러 관리자 존재 여부 및 상태를 확인합니다.
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
        Optional<SellerAdmin> sellerAdmin =
                readManager.findByNameAndPhoneNumber(query.name(), query.phoneNumber());

        return sellerAdmin
                .map(admin -> VerifySellerAdminResult.found(admin.status().name()))
                .orElseGet(VerifySellerAdminResult::notFound);
    }
}
