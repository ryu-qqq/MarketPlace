package com.ryuqq.marketplace.adapter.in.rest.categorypreset.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetException;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** CategoryPreset 에러 매퍼. */
@Component
public class CategoryPresetErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/category-preset";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof CategoryPresetException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "Category Preset Error",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
