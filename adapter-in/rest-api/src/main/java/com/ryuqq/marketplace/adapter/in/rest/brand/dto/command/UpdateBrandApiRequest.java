package com.ryuqq.marketplace.adapter.in.rest.brand.dto.command;

import jakarta.validation.constraints.*;

/**
 * 브랜드 수정 요청 DTO (Partial Update)
 *
 * <p>브랜드 정보를 부분 수정할 때 사용하는 Command DTO입니다.</p>
 * <p>모든 필드는 Optional이며, 제공된 필드만 업데이트됩니다.</p>
 *
 * <ul>
 *   <li>canonicalName: 표준 브랜드명 (선택)</li>
 *   <li>nameKo: 한글 브랜드명 (선택)</li>
 *   <li>nameEn: 영문 브랜드명 (선택)</li>
 *   <li>shortName: 단축 브랜드명 (선택)</li>
 *   <li>country: 브랜드 국가 코드 (선택)</li>
 *   <li>department: 브랜드 부문 (선택)</li>
 *   <li>isLuxury: 럭셔리 브랜드 여부 (선택)</li>
 *   <li>officialWebsite: 공식 웹사이트 URL (선택)</li>
 *   <li>logoUrl: 브랜드 로고 URL (선택)</li>
 *   <li>description: 브랜드 설명 (선택)</li>
 * </ul>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
public record UpdateBrandApiRequest(
    @Size(max = 255, message = "표준 브랜드명은 255자 이내여야 합니다")
    String canonicalName,

    @Size(max = 255, message = "한글명은 255자 이내여야 합니다")
    String nameKo,

    @Size(max = 255, message = "영문명은 255자 이내여야 합니다")
    String nameEn,

    @Size(max = 100, message = "단축명은 100자 이내여야 합니다")
    String shortName,

    @Size(max = 10, message = "국가 코드는 10자 이내여야 합니다")
    String country,

    String department,

    Boolean isLuxury,

    @Size(max = 500, message = "공식 웹사이트는 500자 이내여야 합니다")
    String officialWebsite,

    @Size(max = 500, message = "로고 URL은 500자 이내여야 합니다")
    String logoUrl,

    @Size(max = 2000, message = "설명은 2000자 이내여야 합니다")
    String description
) {}
