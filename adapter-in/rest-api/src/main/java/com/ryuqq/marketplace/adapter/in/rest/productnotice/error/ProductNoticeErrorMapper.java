package com.ryuqq.marketplace.adapter.in.rest.productnotice.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** ProductNotice 에러 매퍼. */
@Component
public class ProductNoticeErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/product-notice";

    @Override
    public boolean supports(DomainException ex) {
        return ex.code().startsWith("PRDNTC-");
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "Product Notice Not Found",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
