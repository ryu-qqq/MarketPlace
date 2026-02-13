package com.ryuqq.marketplace.adapter.in.rest.productgroup.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupNotFoundException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** ProductGroup 에러 매퍼. */
@Component
public class ProductGroupErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/product-group";

    @Override
    public boolean supports(DomainException ex) {
        return ex.code().startsWith("PRDGRP-");
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        String title =
                ex instanceof ProductGroupNotFoundException
                        ? "Product Group Not Found"
                        : "Product Group Error";
        return new MappedError(
                status,
                title,
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
