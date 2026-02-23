package com.ryuqq.marketplace.adapter.in.rest.adminmenu.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.adminmenu.exception.AdminMenuException;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/** Admin 메뉴 에러 매퍼. */
@Component
public class AdminMenuErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/admin-menu";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof AdminMenuException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "Admin Menu Error",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
