package com.ryuqq.marketplace.adapter.in.rest.brand.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.brand.exception.BrandErrorCode;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Brand API Error Mapper
 *
 * <p>Brand 도메인 예외를 HTTP 응답으로 매핑합니다.
 */
@Component
public class BrandApiErrorMapper implements ErrorMapper {

    private static final Set<String> SUPPORTED_CODES = Set.of(
        BrandErrorCode.BRAND_NOT_FOUND.getCode(),
        BrandErrorCode.BRAND_CODE_DUPLICATE.getCode(),
        BrandErrorCode.CANONICAL_NAME_DUPLICATE.getCode(),
        BrandErrorCode.BRAND_BLOCKED.getCode(),
        BrandErrorCode.BRAND_ALIAS_NOT_FOUND.getCode(),
        BrandErrorCode.BRAND_ALIAS_DUPLICATE.getCode()
    );

    private static final Map<String, HttpStatus> STATUS_MAP = Map.of(
        BrandErrorCode.BRAND_NOT_FOUND.getCode(), HttpStatus.NOT_FOUND,
        BrandErrorCode.BRAND_CODE_DUPLICATE.getCode(), HttpStatus.CONFLICT,
        BrandErrorCode.CANONICAL_NAME_DUPLICATE.getCode(), HttpStatus.CONFLICT,
        BrandErrorCode.BRAND_BLOCKED.getCode(), HttpStatus.FORBIDDEN,
        BrandErrorCode.BRAND_ALIAS_NOT_FOUND.getCode(), HttpStatus.NOT_FOUND,
        BrandErrorCode.BRAND_ALIAS_DUPLICATE.getCode(), HttpStatus.CONFLICT
    );

    private static final Map<String, String> TITLE_MAP = Map.of(
        BrandErrorCode.BRAND_NOT_FOUND.getCode(), "브랜드를 찾을 수 없습니다",
        BrandErrorCode.BRAND_CODE_DUPLICATE.getCode(), "브랜드 코드가 중복됩니다",
        BrandErrorCode.CANONICAL_NAME_DUPLICATE.getCode(), "표준 브랜드명이 중복됩니다",
        BrandErrorCode.BRAND_BLOCKED.getCode(), "차단된 브랜드입니다",
        BrandErrorCode.BRAND_ALIAS_NOT_FOUND.getCode(), "브랜드 별칭을 찾을 수 없습니다",
        BrandErrorCode.BRAND_ALIAS_DUPLICATE.getCode(), "브랜드 별칭이 중복됩니다"
    );

    @Override
    public boolean supports(String code) {
        return code != null && SUPPORTED_CODES.contains(code);
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        String code = ex.code();

        HttpStatus status = STATUS_MAP.getOrDefault(code, HttpStatus.BAD_REQUEST);
        String title = TITLE_MAP.getOrDefault(code, "브랜드 처리 오류");
        String detail = buildDetailMessage(ex);
        URI type = URI.create("about:blank");

        return new MappedError(status, title, detail, type);
    }

    private String buildDetailMessage(DomainException ex) {
        String message = ex.getMessage();
        if (message != null && !message.isBlank()) {
            return message;
        }

        Map<String, Object> args = ex.args();
        String code = ex.code();

        if (BrandErrorCode.BRAND_NOT_FOUND.getCode().equals(code)) {
            Object brandId = args.get("brandId");
            return brandId != null
                ? "Brand not found: " + brandId
                : "브랜드를 찾을 수 없습니다";
        }

        if (BrandErrorCode.BRAND_CODE_DUPLICATE.getCode().equals(code)) {
            Object brandCode = args.get("code");
            return brandCode != null
                ? "Brand code already exists: " + brandCode
                : "브랜드 코드가 이미 존재합니다";
        }

        if (BrandErrorCode.CANONICAL_NAME_DUPLICATE.getCode().equals(code)) {
            Object canonicalName = args.get("canonicalName");
            return canonicalName != null
                ? "Canonical name already exists: " + canonicalName
                : "표준 브랜드명이 이미 존재합니다";
        }

        if (BrandErrorCode.BRAND_BLOCKED.getCode().equals(code)) {
            Object brandId = args.get("brandId");
            return brandId != null
                ? "Brand is blocked and cannot be used for product mapping: " + brandId
                : "차단된 브랜드는 상품 매핑에 사용할 수 없습니다";
        }

        if (BrandErrorCode.BRAND_ALIAS_NOT_FOUND.getCode().equals(code)) {
            Object aliasId = args.get("aliasId");
            return aliasId != null
                ? "Brand alias not found: " + aliasId
                : "브랜드 별칭을 찾을 수 없습니다";
        }

        if (BrandErrorCode.BRAND_ALIAS_DUPLICATE.getCode().equals(code)) {
            Object aliasName = args.get("aliasName");
            return aliasName != null
                ? "Brand alias already exists: " + aliasName
                : "브랜드 별칭이 이미 존재합니다";
        }

        return "브랜드 처리 중 오류가 발생했습니다";
    }
}
