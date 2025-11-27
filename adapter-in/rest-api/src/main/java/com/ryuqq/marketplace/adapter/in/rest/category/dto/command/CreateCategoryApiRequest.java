package com.ryuqq.marketplace.adapter.in.rest.category.dto.command;

import jakarta.validation.constraints.*;

/**
 * 카테고리 생성 요청 DTO
 *
 * <p>카테고리 생성 시 필요한 정보를 담는 Request DTO입니다.</p>
 *
 * <ul>
 *   <li>parentId: 부모 카테고리 ID (null이면 루트 카테고리)</li>
 *   <li>code: 카테고리 고유 코드 (대문자 시작, 대문자/숫자/언더스코어만 허용, 필수)</li>
 *   <li>nameKo: 한글 카테고리명 (필수)</li>
 *   <li>nameEn: 영문 카테고리명 (선택)</li>
 *   <li>sortOrder: 정렬 순서 (0 이상)</li>
 *   <li>isListable: 상품 등록 가능 여부</li>
 *   <li>isVisible: 표시 여부</li>
 *   <li>department: 부서 구분 (필수)</li>
 *   <li>productGroup: 상품 그룹 (필수)</li>
 *   <li>genderScope: 성별 구분</li>
 *   <li>ageGroup: 연령대 구분</li>
 *   <li>displayName: 표시용 이름</li>
 *   <li>seoSlug: SEO용 슬러그</li>
 *   <li>iconUrl: 아이콘 URL</li>
 * </ul>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
public record CreateCategoryApiRequest(
    Long parentId,

    @NotBlank(message = "카테고리 코드는 필수입니다")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]{2,99}$", message = "카테고리 코드는 대문자로 시작하고, 3자 이상이어야 하며, 대문자/숫자/언더스코어만 허용됩니다")
    String code,

    @NotBlank(message = "한글 카테고리명은 필수입니다")
    @Size(max = 255, message = "한글 카테고리명은 255자 이내여야 합니다")
    String nameKo,

    @Size(max = 255, message = "영문 카테고리명은 255자 이내여야 합니다")
    String nameEn,

    @Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다")
    Integer sortOrder,

    Boolean isListable,

    Boolean isVisible,

    @NotNull(message = "부서 구분은 필수입니다")
    String department,

    @NotNull(message = "상품 그룹은 필수입니다")
    String productGroup,

    String genderScope,

    String ageGroup,

    @Size(max = 255, message = "표시용 이름은 255자 이내여야 합니다")
    String displayName,

    @Size(max = 255, message = "SEO 슬러그는 255자 이내여야 합니다")
    String seoSlug,

    @Size(max = 500, message = "아이콘 URL은 500자 이내여야 합니다")
    String iconUrl
) {
    /**
     * 루트 카테고리 여부 확인
     *
     * @return 부모가 없으면 true
     */
    public boolean isRootCategory() {
        return parentId == null;
    }
}
