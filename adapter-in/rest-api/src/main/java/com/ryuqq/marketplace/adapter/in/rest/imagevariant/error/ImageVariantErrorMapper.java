package com.ryuqq.marketplace.adapter.in.rest.imagevariant.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.imagevariant.exception.ImageVariantNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** ImageVariant 에러 매퍼. */
@Component
public class ImageVariantErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/image-variant";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof ImageVariantNotFoundException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "Image Variant Not Found",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
