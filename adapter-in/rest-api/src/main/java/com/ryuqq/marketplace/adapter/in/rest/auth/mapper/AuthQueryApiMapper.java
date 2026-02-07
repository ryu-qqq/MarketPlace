package com.ryuqq.marketplace.adapter.in.rest.auth.mapper;

import com.ryuqq.marketplace.adapter.in.rest.auth.dto.response.MyInfoApiResponse;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import org.springframework.stereotype.Component;

/**
 * AuthQueryApiMapper - 인증 Query Mapper.
 *
 * <p>Application Result → API Response 변환을 담당합니다.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class AuthQueryApiMapper {

    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Authorization 헤더에서 액세스 토큰 추출.
     *
     * @param authorization Authorization 헤더 값
     * @return 액세스 토큰
     * @throws IllegalArgumentException 유효하지 않은 헤더 형식
     */
    public String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }

    /**
     * MyInfoResult → MyInfoApiResponse 변환.
     *
     * @param result 사용자 정보 결과
     * @return MyInfoApiResponse
     */
    public MyInfoApiResponse toResponse(MyInfoResult result) {
        return MyInfoApiResponse.from(result);
    }
}
