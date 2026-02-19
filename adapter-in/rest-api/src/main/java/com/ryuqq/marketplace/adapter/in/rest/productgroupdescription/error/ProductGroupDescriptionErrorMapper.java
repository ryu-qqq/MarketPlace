package com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupDescriptionNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** ProductGroupDescription 에러 매퍼. */
@Component
public class ProductGroupDescriptionErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/product-group-description";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof ProductGroupDescriptionNotFoundException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "Product Group Description Not Found",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
