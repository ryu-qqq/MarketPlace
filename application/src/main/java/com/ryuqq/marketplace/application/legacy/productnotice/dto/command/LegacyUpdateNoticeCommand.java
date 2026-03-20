package com.ryuqq.marketplace.application.legacy.productnotice.dto.command;

/**
 * 레거시 고시정보 수정 Command.
 *
 * @param productGroupId 레거시 상품그룹 PK
 * @param material 소재
 * @param color 색상
 * @param size 사이즈
 * @param maker 제조사
 * @param origin 원산지
 * @param washingMethod 세탁 방법
 * @param yearMonthDay 제조 연월
 * @param assuranceStandard 품질 보증 기준
 * @param asPhone AS 전화번호
 */
public record LegacyUpdateNoticeCommand(
        long productGroupId,
        String material,
        String color,
        String size,
        String maker,
        String origin,
        String washingMethod,
        String yearMonthDay,
        String assuranceStandard,
        String asPhone) {}
