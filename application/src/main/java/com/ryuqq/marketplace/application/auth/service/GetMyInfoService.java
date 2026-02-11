package com.ryuqq.marketplace.application.auth.service;

import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import com.ryuqq.marketplace.application.auth.manager.AuthManager;
import com.ryuqq.marketplace.application.auth.port.in.GetMyInfoUseCase;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminReadManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 내 정보 조회 서비스.
 *
 * <p>GetMyInfoUseCase를 구현하며, AuthManager를 통해 사용자 정보를 조회한 뒤 SellerAdminReadManager를 통해 핸드폰 번호와 셀러
 * ID를 보강합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
@ConditionalOnProperty(prefix = "authhub", name = "base-url")
public class GetMyInfoService implements GetMyInfoUseCase {

    private final AuthManager authManager;
    private final SellerAdminReadManager sellerAdminReadManager;

    public GetMyInfoService(
            AuthManager authManager, SellerAdminReadManager sellerAdminReadManager) {
        this.authManager = authManager;
        this.sellerAdminReadManager = sellerAdminReadManager;
    }

    @Override
    public MyInfoResult execute(String accessToken) {
        MyInfoResult authInfo = authManager.getMyInfo(accessToken);

        return sellerAdminReadManager
                .findByAuthUserId(authInfo.userId())
                .map(
                        sellerAdmin ->
                                authInfo.withSellerInfo(
                                        sellerAdmin.idValue(),
                                        sellerAdmin.sellerIdValue(),
                                        sellerAdmin.phoneNumberValue()))
                .orElse(authInfo);
    }
}
