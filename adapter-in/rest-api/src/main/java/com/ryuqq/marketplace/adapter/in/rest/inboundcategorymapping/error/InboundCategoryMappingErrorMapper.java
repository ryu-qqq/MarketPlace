package com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.inboundcategorymapping.exception.InboundCategoryMappingException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** InboundCategoryMapping 에러 매퍼. */
@Component
public class InboundCategoryMappingErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/inbound-category-mapping";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof InboundCategoryMappingException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "Inbound Category Mapping Error",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
