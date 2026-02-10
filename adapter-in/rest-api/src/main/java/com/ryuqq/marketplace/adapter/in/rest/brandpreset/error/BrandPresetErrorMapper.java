package com.ryuqq.marketplace.adapter.in.rest.brandpreset.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetException;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** BrandPreset 에러 매퍼. */
@Component
public class BrandPresetErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/brand-preset";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof BrandPresetException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "Brand Preset Error",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
