package com.ryuqq.marketplace.application.auth.service;

import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import com.ryuqq.marketplace.application.auth.manager.AuthManager;
import com.ryuqq.marketplace.application.auth.port.in.GetMyInfoUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * 내 정보 조회 서비스.
 *
 * <p>GetMyInfoUseCase를 구현하며, AuthManager를 통해 사용자 정보를 조회합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
@ConditionalOnBean(AuthManager.class)
public class GetMyInfoService implements GetMyInfoUseCase {

    private final AuthManager authManager;

    public GetMyInfoService(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public MyInfoResult execute(String accessToken) {
        return authManager.getMyInfo(accessToken);
    }
}
