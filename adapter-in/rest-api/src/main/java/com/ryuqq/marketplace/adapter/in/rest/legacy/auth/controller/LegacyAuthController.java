package com.ryuqq.marketplace.adapter.in.rest.legacy.auth.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.auth.LegacyAuthEndpoints.AUTH_AUTHENTICATION;

import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.request.LegacyCreateAuthTokenRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.response.LegacyAuthTokenResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 인증 API 호환 컨트롤러.
 *
 * <p>기존 세토프 연동 호환을 위해 제공되는 레거시 엔드포인트입니다. OMS(사방넷, 셀릭)가 호출하는 POST /auth/authentication만 제공합니다.
 */
@Tag(
        name = "세토프 어드민용 레거시",
        description =
                "세토프 어드민용 레거시 엔드포인트. 기존 세토프 연동 호환을 위해 제공되며, 신규 개발 시에는 동일 기능의 일반 API 사용을 권장합니다.")
@RestController
public class LegacyAuthController {

    @PostMapping(AUTH_AUTHENTICATION)
    public ResponseEntity<LegacyApiResponse<LegacyAuthTokenResponse>> getAccessToken(
            @RequestBody LegacyCreateAuthTokenRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
