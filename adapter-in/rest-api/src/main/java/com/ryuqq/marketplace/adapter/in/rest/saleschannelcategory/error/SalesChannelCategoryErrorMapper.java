package com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.saleschannelcategory.exception.SalesChannelCategoryException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** SalesChannelCategory 에러 매퍼. */
@Component
public class SalesChannelCategoryErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/sales-channel-category";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof SalesChannelCategoryException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "Sales Channel Category Error",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
