package com.ryuqq.marketplace.integration.catalog.brand.fixture;

import com.ryuqq.marketplace.adapter.in.rest.brand.dto.command.AddBrandAliasApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.command.ChangeBrandStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.command.CreateBrandApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.command.UpdateBrandApiRequest;

import java.math.BigDecimal;

/**
 * Brand Integration Test Fixture
 *
 * <p>통합 테스트용 Brand 요청 객체 생성 유틸리티
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
public final class BrandIntegrationTestFixture {

    private BrandIntegrationTestFixture() {
        // 인스턴스화 방지 (유틸리티 클래스)
    }

    // ========== CreateBrandApiRequest ==========

    /**
     * 기본 브랜드 생성 요청 (NIKE 예시)
     *
     * @return CreateBrandApiRequest 기본 브랜드 생성 요청 객체
     */
    public static CreateBrandApiRequest createBrandRequest() {
        return createBrandRequest("NIKE", "Nike");
    }

    /**
     * 커스텀 코드/이름 브랜드 생성 요청
     *
     * @param code 브랜드 코드 (대문자)
     * @param canonicalName 표준 브랜드명
     * @return CreateBrandApiRequest 커스텀 브랜드 생성 요청 객체
     */
    public static CreateBrandApiRequest createBrandRequest(String code, String canonicalName) {
        return new CreateBrandApiRequest(
                code,
                canonicalName,
                "나이키",
                "Nike Inc.",
                "NIKE",
                "US",
                "FASHION",
                false,
                "https://nike.com",
                "https://nike.com/logo.png",
                "Nike is a global sportswear brand"
        );
    }

    /**
     * 럭셔리 브랜드 생성 요청 (GUCCI 예시)
     *
     * @return CreateBrandApiRequest 럭셔리 브랜드 생성 요청 객체
     */
    public static CreateBrandApiRequest createLuxuryBrandRequest() {
        return new CreateBrandApiRequest(
                "GUCCI",
                "Gucci",
                "구찌",
                "Gucci",
                "GUC",
                "IT",
                "FASHION",
                true,
                "https://gucci.com",
                "https://gucci.com/logo.png",
                "Gucci is a luxury fashion brand"
        );
    }

    // ========== UpdateBrandApiRequest ==========

    /**
     * 브랜드 정보 수정 요청 (Partial Update)
     *
     * @return UpdateBrandApiRequest 브랜드 수정 요청 객체
     */
    public static UpdateBrandApiRequest updateBrandRequest() {
        return new UpdateBrandApiRequest(
                null, // canonicalName 변경 안함
                "나이키 업데이트",
                "Nike Updated",
                "NIK",
                "US",
                null, // department 변경 안함
                null, // isLuxury 변경 안함
                "https://nike.com/new",
                "https://nike.com/new-logo.png",
                "Updated description"
        );
    }

    // ========== ChangeBrandStatusApiRequest ==========

    /**
     * 브랜드 상태 변경 요청
     *
     * @param status 변경할 상태 (ACTIVE, INACTIVE, BLOCKED)
     * @return ChangeBrandStatusApiRequest 상태 변경 요청 객체
     */
    public static ChangeBrandStatusApiRequest changeStatusRequest(String status) {
        return new ChangeBrandStatusApiRequest(status);
    }

    // ========== AddBrandAliasApiRequest ==========

    /**
     * 기본 브랜드 별칭 추가 요청
     *
     * @return AddBrandAliasApiRequest 기본 별칭 추가 요청 객체
     */
    public static AddBrandAliasApiRequest addAliasRequest() {
        return addAliasRequest("나이키");
    }

    /**
     * 커스텀 별칭 추가 요청 (MANUAL 소스)
     *
     * @param aliasName 별칭명
     * @return AddBrandAliasApiRequest 커스텀 별칭 추가 요청 객체
     */
    public static AddBrandAliasApiRequest addAliasRequest(String aliasName) {
        return new AddBrandAliasApiRequest(
                aliasName,
                "MANUAL",
                null,
                "GLOBAL",
                BigDecimal.ONE
        );
    }

    /**
     * 소스 타입 지정 별칭 추가 요청 (SELLER, MALL 등)
     *
     * @param aliasName 별칭명
     * @param sourceType 소스 타입 (SELLER, MALL, MANUAL)
     * @param sellerId 셀러 ID (SELLER 타입일 경우)
     * @param mallCode 몰 코드 (MALL 타입일 경우)
     * @return AddBrandAliasApiRequest 소스 타입 지정 별칭 추가 요청 객체
     */
    public static AddBrandAliasApiRequest addAliasRequestWithSource(
            String aliasName, String sourceType, Long sellerId, String mallCode) {
        return new AddBrandAliasApiRequest(
                aliasName,
                sourceType,
                sellerId,
                mallCode,
                new BigDecimal("0.85")
        );
    }
}
