package com.ryuqq.marketplace.integration.catalog.category.fixture;

import com.ryuqq.marketplace.adapter.in.rest.category.dto.command.ChangeCategoryStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.command.CreateCategoryApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.command.MoveCategoryApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.command.UpdateCategoryApiRequest;

/**
 * Category Integration Test Fixture
 *
 * <p>통합 테스트용 Category 요청 객체 생성 유틸리티
 *
 * <p>Kent Beck TDD + Tidy First 철학 기반 TestFixture 설계:
 * <ul>
 *   <li>Structural: 테스트 데이터 생성 로직 (동작 변경 없음)</li>
 *   <li>명확한 의도: 각 메서드는 특정 테스트 시나리오를 명확히 표현</li>
 *   <li>재사용성: 다양한 테스트 케이스에서 일관된 데이터 제공</li>
 * </ul>
 *
 * @author Claude Code (Quality Engineer)
 * @since 2025-11-27
 */
public final class CategoryIntegrationTestFixture {

    private CategoryIntegrationTestFixture() {
        // 인스턴스화 방지 (유틸리티 클래스)
    }

    // ========== CreateCategoryApiRequest ==========

    /**
     * 루트 카테고리 생성 요청 (FASHION 예시)
     *
     * @return CreateCategoryApiRequest 루트 카테고리 생성 요청 객체
     */
    public static CreateCategoryApiRequest createRootCategoryRequest() {
        return createRootCategoryRequest("FASHION", "패션", "Fashion");
    }

    /**
     * 커스텀 루트 카테고리 생성 요청
     *
     * @param code 카테고리 코드 (대문자)
     * @param nameKo 한국어명
     * @param nameEn 영어명
     * @return CreateCategoryApiRequest 루트 카테고리 생성 요청 객체
     */
    public static CreateCategoryApiRequest createRootCategoryRequest(
            String code, String nameKo, String nameEn) {
        return new CreateCategoryApiRequest(
                null,           // parentId (루트이므로 null)
                code,
                nameKo,
                nameEn,
                0,              // sortOrder
                true,           // isListable
                true,           // isVisible
                "FASHION",      // department
                "ETC",          // productGroup
                "UNISEX",       // genderScope
                "ALL",          // ageGroup
                nameKo,         // displayName
                code.toLowerCase(), // seoSlug
                null            // iconUrl
        );
    }

    /**
     * 자식 카테고리 생성 요청
     *
     * @param parentId 부모 카테고리 ID
     * @return CreateCategoryApiRequest 자식 카테고리 생성 요청 객체
     */
    public static CreateCategoryApiRequest createChildCategoryRequest(Long parentId) {
        return createChildCategoryRequest(parentId, "APPAREL", "의류", "Apparel");
    }

    /**
     * 커스텀 자식 카테고리 생성 요청
     *
     * @param parentId 부모 카테고리 ID
     * @param code 카테고리 코드 (대문자)
     * @param nameKo 한국어명
     * @param nameEn 영어명
     * @return CreateCategoryApiRequest 자식 카테고리 생성 요청 객체
     */
    public static CreateCategoryApiRequest createChildCategoryRequest(
            Long parentId, String code, String nameKo, String nameEn) {
        return new CreateCategoryApiRequest(
                parentId,
                code,
                nameKo,
                nameEn,
                0,              // sortOrder
                true,           // isListable
                true,           // isVisible
                "FASHION",      // department
                "APPAREL",      // productGroup
                "UNISEX",       // genderScope
                "ALL",          // ageGroup
                nameKo,         // displayName
                code.toLowerCase(), // seoSlug
                null            // iconUrl
        );
    }

    /**
     * Leaf 카테고리 생성 요청 (상품 등록 가능)
     *
     * @param parentId 부모 카테고리 ID
     * @param code 카테고리 코드
     * @param nameKo 한국어명
     * @return CreateCategoryApiRequest Leaf 카테고리 생성 요청 객체
     */
    public static CreateCategoryApiRequest createLeafCategoryRequest(
            Long parentId, String code, String nameKo) {
        return new CreateCategoryApiRequest(
                parentId,
                code,
                nameKo,
                code,           // nameEn (code와 동일)
                0,              // sortOrder
                true,           // isListable (상품 등록 가능)
                true,           // isVisible
                "FASHION",      // department
                "APPAREL",      // productGroup
                "UNISEX",       // genderScope
                "ALL",          // ageGroup
                nameKo,         // displayName
                code.toLowerCase(), // seoSlug
                null            // iconUrl
        );
    }

    /**
     * 비노출 카테고리 생성 요청 (테스트용)
     *
     * @param parentId 부모 카테고리 ID (null이면 루트)
     * @param code 카테고리 코드
     * @param nameKo 한국어명
     * @return CreateCategoryApiRequest 비노출 카테고리 생성 요청 객체
     */
    public static CreateCategoryApiRequest createInvisibleCategoryRequest(
            Long parentId, String code, String nameKo) {
        return new CreateCategoryApiRequest(
                parentId,
                code,
                nameKo,
                code,
                99,             // sortOrder (높은 순서)
                false,          // isListable (상품 등록 불가)
                false,          // isVisible (비노출)
                "FASHION",
                "ETC",
                "UNISEX",
                "ALL",
                nameKo,
                code.toLowerCase(),
                null
        );
    }

    // ========== UpdateCategoryApiRequest ==========

    /**
     * 카테고리 수정 요청 (Partial Update)
     *
     * @return UpdateCategoryApiRequest 카테고리 수정 요청 객체
     */
    public static UpdateCategoryApiRequest updateCategoryRequest() {
        return new UpdateCategoryApiRequest(
                "패션 업데이트",  // nameKo
                "Fashion Updated",  // nameEn
                null,           // isListable (변경 안함)
                null,           // isVisible (변경 안함)
                1,              // sortOrder
                null,           // genderScope (변경 안함)
                null,           // ageGroup (변경 안함)
                "패션 업데이트", // displayName
                null,           // seoSlug (변경 안함)
                "https://cdn.example.com/icon.png"  // iconUrl
        );
    }

    /**
     * 카테고리 노출 설정 변경 요청
     *
     * @param isVisible 노출 여부
     * @param isListable 상품 등록 가능 여부
     * @return UpdateCategoryApiRequest 노출 설정 변경 요청 객체
     */
    public static UpdateCategoryApiRequest updateVisibilityRequest(
            Boolean isVisible, Boolean isListable) {
        return new UpdateCategoryApiRequest(
                null,           // nameKo
                null,           // nameEn
                isListable,     // isListable
                isVisible,      // isVisible
                null,           // sortOrder
                null,           // genderScope
                null,           // ageGroup
                null,           // displayName
                null,           // seoSlug
                null            // iconUrl
        );
    }

    // ========== ChangeCategoryStatusApiRequest ==========

    /**
     * 카테고리 상태 변경 요청
     *
     * @param status 변경할 상태 (ACTIVE, INACTIVE, DEPRECATED)
     * @return ChangeCategoryStatusApiRequest 상태 변경 요청 객체
     */
    public static ChangeCategoryStatusApiRequest changeStatusRequest(String status) {
        return new ChangeCategoryStatusApiRequest(status, null);
    }

    /**
     * 카테고리 상태 변경 요청 (대체 카테고리 지정)
     *
     * @param status 변경할 상태
     * @param replacementCategoryId 대체 카테고리 ID (DEPRECATED 시 사용)
     * @return ChangeCategoryStatusApiRequest 상태 변경 요청 객체
     */
    public static ChangeCategoryStatusApiRequest changeStatusRequest(String status, Long replacementCategoryId) {
        return new ChangeCategoryStatusApiRequest(status, replacementCategoryId);
    }

    // ========== MoveCategoryApiRequest ==========

    /**
     * 카테고리 이동 요청 (루트로 이동)
     *
     * @return MoveCategoryApiRequest 루트 이동 요청 객체
     */
    public static MoveCategoryApiRequest moveToRootRequest() {
        return new MoveCategoryApiRequest(null, 0);
    }

    /**
     * 카테고리 이동 요청 (다른 부모 아래로 이동)
     *
     * @param newParentId 새 부모 카테고리 ID
     * @return MoveCategoryApiRequest 이동 요청 객체
     */
    public static MoveCategoryApiRequest moveToParentRequest(Long newParentId) {
        return new MoveCategoryApiRequest(newParentId, 0);
    }

    /**
     * 카테고리 이동 요청 (다른 부모 + 순서 지정)
     *
     * @param newParentId 새 부모 카테고리 ID
     * @param newSortOrder 새 정렬 순서
     * @return MoveCategoryApiRequest 이동 요청 객체
     */
    public static MoveCategoryApiRequest moveCategoryRequest(Long newParentId, Integer newSortOrder) {
        return new MoveCategoryApiRequest(newParentId, newSortOrder);
    }
}
