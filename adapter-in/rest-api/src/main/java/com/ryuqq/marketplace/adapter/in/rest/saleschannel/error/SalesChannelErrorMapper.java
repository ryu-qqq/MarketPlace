package com.ryuqq.marketplace.adapter.in.rest.saleschannel.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.saleschannel.exception.SalesChannelException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** SalesChannel 에러 매퍼. */
@Component
public class SalesChannelErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/sales-channel";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof SalesChannelException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "Sales Channel Error",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
