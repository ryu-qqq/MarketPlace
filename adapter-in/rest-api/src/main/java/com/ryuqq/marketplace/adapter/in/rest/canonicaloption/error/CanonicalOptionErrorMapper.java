package com.ryuqq.marketplace.adapter.in.rest.canonicaloption.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.canonicaloption.exception.CanonicalOptionException;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** CanonicalOption 에러 매퍼. */
@Component
public class CanonicalOptionErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/canonical-option";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof CanonicalOptionException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "Canonical Option Error",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
