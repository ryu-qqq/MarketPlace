package com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.inboundbrandmapping.exception.InboundBrandMappingException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** InboundBrandMapping 에러 매퍼. */
@Component
public class InboundBrandMappingErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/inbound-brand-mapping";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof InboundBrandMappingException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "Inbound Brand Mapping Error",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
