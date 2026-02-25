package com.ryuqq.marketplace.adapter.in.rest.legacy.auth.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** 레거시 인증 도메인 예외를 HTTP 응답으로 변환하는 매퍼. */
@Component
public class LegacyAuthErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/legacy-auth";

    @Override
    public boolean supports(DomainException ex) {
        return ex.code().startsWith("AUTH_");
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "Legacy Authentication Error",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
