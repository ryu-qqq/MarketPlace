package com.ryuqq.marketplace.application.legacyproduct.dto.command;

import java.util.Map;

/**
 * 레거시 고시정보 수정 Command.
 *
 * <p>세토프 PK와 필드코드→값 매핑을 보유합니다. noticeCategoryId 및 noticeFieldId 해석은 Coordinator에서 수행합니다.
 *
 * @param setofProductGroupId 세토프 상품그룹 PK
 * @param noticeFields 필드코드 → 필드값 매핑 (e.g. "material" → "면 100%")
 */
public record LegacyUpdateNoticeCommand(
        long setofProductGroupId, Map<String, String> noticeFields) {}
