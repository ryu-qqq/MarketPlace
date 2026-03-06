package com.ryuqq.marketplace.adapter.in.rest.order.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.order.exception.OrderException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** Order 에러 매퍼. */
@Component
public class OrderErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/order";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof OrderException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "Order Error",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
