package com.ryuqq.marketplace.application.saleschannelbrand.dto.response;

/**
 * 외부 판매채널 API에서 조회된 브랜드 결과.
 *
 * @param externalBrandCode 외부 브랜드 코드 (판매채널 브랜드 ID)
 * @param externalBrandName 외부 브랜드 이름
 */
public record ExternalBrandResult(String externalBrandCode, String externalBrandName) {}
