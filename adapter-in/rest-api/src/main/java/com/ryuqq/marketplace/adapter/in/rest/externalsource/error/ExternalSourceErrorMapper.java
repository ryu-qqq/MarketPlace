package com.ryuqq.marketplace.adapter.in.rest.externalsource.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.externalsource.exception.ExternalSourceException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** ExternalSource 에러 매퍼. */
@Component
public class ExternalSourceErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/external-source";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof ExternalSourceException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "External Source Error",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
