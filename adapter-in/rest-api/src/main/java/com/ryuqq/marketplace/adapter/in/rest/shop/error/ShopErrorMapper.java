package com.ryuqq.marketplace.adapter.in.rest.shop.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.shop.exception.ShopException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * ShopErrorMapper - 외부몰(Shop) 도메인 예외를 HTTP 응답으로 변환.
 *
 * <p>API-ERR-001: 도메인별 ErrorMapper를 구현하여 DomainException을 HTTP 응답으로 매핑.
 */
@Component
public class ShopErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/shop";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof ShopException;
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        String title = titleFor(ex.code());
        return new MappedError(
                status,
                title,
                ex.getMessage(),
                URI.create(ERROR_TYPE_PREFIX + "/" + ex.code().toLowerCase(Locale.ROOT)));
    }

    private static String titleFor(String code) {
        return switch (code) {
            case "SHP-001" -> "외부몰을 찾을 수 없음";
            case "SHP-003" -> "계정 ID 중복";
            default -> "외부몰 오류";
        };
    }
}
