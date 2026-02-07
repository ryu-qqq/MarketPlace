package com.ryuqq.marketplace.adapter.in.rest.category.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.category.exception.CategoryException;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** Category 에러 매퍼. */
@Component
public class CategoryErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/category";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof CategoryException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "Category Error",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
