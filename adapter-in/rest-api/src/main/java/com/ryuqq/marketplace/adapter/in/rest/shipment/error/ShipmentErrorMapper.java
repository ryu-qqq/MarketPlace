package com.ryuqq.marketplace.adapter.in.rest.shipment.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.shipment.exception.ShipmentException;
import java.net.URI;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * ShipmentErrorMapper - 배송 도메인 예외를 HTTP 응답으로 변환.
 *
 * <p>API-ERR-001: 도메인별 ErrorMapper를 구현하여 DomainException을 HTTP 응답으로 매핑.
 */
@Component
public class ShipmentErrorMapper implements ErrorMapper {

    private static final String ERROR_TYPE_PREFIX = "/errors/shipment";

    @Override
    public boolean supports(DomainException ex) {
        return ex instanceof ShipmentException;
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
            case "SHP-001" -> "배송 정보를 찾을 수 없음";
            case "SHP-002" -> "유효하지 않은 배송 상태 변경";
            case "SHP-003" -> "송장번호 필수";
            default -> "배송 오류";
        };
    }
}
