package com.ryuqq.marketplace.adapter.in.rest.product.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.product.exception.ProductNotFoundException;
import com.ryuqq.marketplace.domain.product.exception.ProductOwnershipViolationException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** Product 에러 매퍼. */
@Component
public class ProductErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/product";

    @Override
    public boolean supports(DomainException ex) {
        return ex.code().startsWith("PRD-");
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        String title;
        if (ex instanceof ProductNotFoundException) {
            title = "Product Not Found";
        } else if (ex instanceof ProductOwnershipViolationException) {
            title = "Product Ownership Violation";
        } else {
            title = "Product Error";
        }
        return new MappedError(
                status,
                title,
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
