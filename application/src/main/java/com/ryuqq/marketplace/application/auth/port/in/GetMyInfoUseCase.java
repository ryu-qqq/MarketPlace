package com.ryuqq.marketplace.application.auth.port.in;

import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;

/**
 * 내 정보 조회 UseCase.
 *
 * <p>현재 로그인한 사용자의 정보를 조회합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface GetMyInfoUseCase {

    /**
     * 내 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    MyInfoResult execute(String userId);
}
