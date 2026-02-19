package com.ryuqq.marketplace.adapter.in.rest.productgroupimage.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.productgroup.exception.DescriptionImageNotFoundException;
import com.ryuqq.marketplace.domain.productgroupimage.exception.ProductGroupImageNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** ProductGroupImage 에러 매퍼. */
@Component
public class ProductGroupImageErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/product-group-image";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof ProductGroupImageNotFoundException
                || ex instanceof DescriptionImageNotFoundException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        String title =
                ex instanceof ProductGroupImageNotFoundException
                        ? "Product Group Image Not Found"
                        : "Description Image Not Found";
        return new MappedError(
                status,
                title,
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
