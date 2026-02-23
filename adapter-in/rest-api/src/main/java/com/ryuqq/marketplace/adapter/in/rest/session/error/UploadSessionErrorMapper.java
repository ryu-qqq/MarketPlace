package com.ryuqq.marketplace.adapter.in.rest.session.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * UploadSessionErrorMapper - 업로드 세션 관련 도메인 예외를 HTTP 응답으로 변환.
 *
 * <p>API-ERR-001: 도메인별 ErrorMapper를 구현하여 DomainException을 HTTP 응답으로 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class UploadSessionErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/upload-session";
    private static final String CODE_PREFIX = "UPLOAD_SESSION-";

    @Override
    public boolean supports(DomainException ex) {
        return ex.code().startsWith(CODE_PREFIX);
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "Upload Session Error",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
