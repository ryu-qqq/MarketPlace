package com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.externalbrandmapping.exception.ExternalBrandMappingException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** ExternalBrandMapping 에러 매퍼. */
@Component
public class ExternalBrandMappingErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/external-brand-mapping";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof ExternalBrandMappingException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "External Brand Mapping Error",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
