package com.ryuqq.marketplace.adapter.in.rest.selleradmin.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.seller.exception.SellerException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * SellerAdminErrorMapper - 셀러 관리자 도메인 예외를 HTTP 응답으로 변환.
 *
 * <p>OCP(Open-Closed Principle) 준수를 위해 도메인별로 구현체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Component
public class SellerAdminErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/seller-admin";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof SellerException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        return new MappedError(
                status,
                "Seller Admin Error",
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }
}
