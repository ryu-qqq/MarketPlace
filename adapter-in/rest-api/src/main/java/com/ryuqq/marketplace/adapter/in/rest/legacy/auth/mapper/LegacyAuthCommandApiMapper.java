package com.ryuqq.marketplace.adapter.in.rest.legacy.auth.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.request.LegacyCreateAuthTokenRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.response.LegacyAuthTokenResponse;
import com.ryuqq.marketplace.application.legacyauth.dto.command.LegacyLoginCommand;
import org.springframework.stereotype.Component;

/** 레거시 인증 요청 DTO → 내부 Command 변환 매퍼. */
@Component
public class LegacyAuthCommandApiMapper {

    /** LegacyCreateAuthTokenRequest → LegacyLoginCommand. */
    public LegacyLoginCommand toLoginCommand(LegacyCreateAuthTokenRequest request) {
        return new LegacyLoginCommand(request.userId(), request.password());
    }

    /** 토큰 문자열 → LegacyAuthTokenResponse. */
    public LegacyAuthTokenResponse toAuthTokenResponse(String token) {
        return new LegacyAuthTokenResponse(token);
    }
}
