package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.inboundproduct.exception.InboundProductException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** InboundProduct 에러 매퍼. */
@Component
public class InboundProductErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/inbound-product";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof InboundProductException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "Inbound Product Error",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
