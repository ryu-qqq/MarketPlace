package com.ryuqq.marketplace.integration.seller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerBusinessInfoJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerContractJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerCsJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerSettlementJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerBusinessInfoJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerBusinessInfoQueryDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerContractJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerCsJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerCsQueryDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerQueryDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerSettlementJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Seller Query 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - GET /admin/sellers/{sellerId} - 셀러 상세 조회 - GET /admin/sellers - 셀러 목록 검색
 *
 * <p>우선순위: - P0: 15개 시나리오 (필수 기능) - P1: 8개 시나리오 (중요 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("seller")
@Tag("query")
@DisplayName("Seller Query API E2E 테스트")
class SellerQueryE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/sellers";

    @Autowired private SellerJpaRepository sellerRepository;
    @Autowired private SellerQueryDslRepository sellerQueryRepository;
    @Autowired private SellerBusinessInfoJpaRepository businessInfoRepository;
    @Autowired private SellerBusinessInfoQueryDslRepository businessInfoQueryRepository;
    @Autowired private SellerCsJpaRepository sellerCsRepository;
    @Autowired private SellerCsQueryDslRepository sellerCsQueryRepository;
    @Autowired private SellerContractJpaRepository sellerContractRepository;
    @Autowired private SellerSettlementJpaRepository sellerSettlementRepository;

    @BeforeEach
    void setUp() {
        sellerSettlementRepository.deleteAll();
        sellerContractRepository.deleteAll();
        sellerCsRepository.deleteAll();
        businessInfoRepository.deleteAll();
        sellerRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        sellerSettlementRepository.deleteAll();
        sellerContractRepository.deleteAll();
        sellerCsRepository.deleteAll();
        businessInfoRepository.deleteAll();
        sellerRepository.deleteAll();
    }

    // ===== GET /sellers/{sellerId} - 셀러 상세 조회 =====

    @Nested
    @DisplayName("GET /admin/sellers/{sellerId} - 셀러 상세 조회")
    class GetSellerDetailTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q1-S01] 존재하는 셀러 ID로 상세 조회 성공")
        void getSeller_ExistingId_Returns200() {
            // given
            var seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            businessInfoRepository.save(
                    SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(seller.getId()));
            sellerCsRepository.save(
                    SellerCsJpaEntityFixtures.activeEntityWithSellerId(seller.getId()));
            sellerContractRepository.save(
                    SellerContractJpaEntityFixtures.activeEntityWithSellerId(seller.getId()));
            sellerSettlementRepository.save(
                    SellerSettlementJpaEntityFixtures.verifiedEntityWithSellerId(seller.getId()));

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL + "/{sellerId}", seller.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.seller.id", equalTo(seller.getId().intValue()))
                    .body("data.seller.sellerName", notNullValue())
                    .body("data.seller.active", equalTo(true))
                    .body("data.businessInfo.registrationNumber", notNullValue())
                    .body("data.seller.createdAt", notNullValue())
                    .body("data.seller.updatedAt", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q1-S02] 모든 관련 정보가 있는 셀러 조회")
        void getSeller_WithAllRelatedInfo_Returns200() {
            // given
            var seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            businessInfoRepository.save(
                    SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(seller.getId()));
            sellerCsRepository.save(
                    SellerCsJpaEntityFixtures.activeEntityWithSellerId(seller.getId()));
            sellerContractRepository.save(
                    SellerContractJpaEntityFixtures.activeEntityWithSellerId(seller.getId()));
            sellerSettlementRepository.save(
                    SellerSettlementJpaEntityFixtures.verifiedEntityWithSellerId(seller.getId()));

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL + "/{sellerId}", seller.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.seller", notNullValue())
                    .body("data.businessInfo", notNullValue())
                    .body("data.csInfo", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q1-F01] 존재하지 않는 셀러 ID 조회 시 404")
        void getSeller_NonExistingId_Returns404() {
            // given
            Long nonExistingId = 99999L;

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL + "/{sellerId}", nonExistingId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-F02] 잘못된 타입의 sellerId 전달 시 400")
        void getSeller_InvalidTypeId_Returns400() {
            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL + "/{sellerId}", "invalid")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-E02] 연관 정보 일부만 있는 셀러 조회")
        void getSeller_WithPartialInfo_Returns200() {
            // given
            var seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            businessInfoRepository.save(
                    SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(seller.getId()));
            sellerCsRepository.save(
                    SellerCsJpaEntityFixtures.activeEntityWithSellerId(seller.getId()));
            sellerContractRepository.save(
                    SellerContractJpaEntityFixtures.activeEntityWithSellerId(seller.getId()));
            // 정산 정보는 저장하지 않음 (LEFT JOIN이므로 null 가능)

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL + "/{sellerId}", seller.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.seller", notNullValue())
                    .body("data.businessInfo", notNullValue())
                    .body("data.csInfo", notNullValue());
        }
    }

    // ===== GET /sellers - 셀러 목록 검색 =====

    @Nested
    @DisplayName("GET /admin/sellers - 셀러 목록 검색")
    class SearchSellersTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q2-S01] 기본 조회 (파라미터 없음)")
        void searchSellers_NoParams_Returns200() {
            // given
            for (int i = 0; i < 5; i++) {
                sellerRepository.save(
                        SellerJpaEntityFixtures.activeEntityWithName("셀러" + i, "디스플레이" + i));
            }

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.page", equalTo(0))
                    .body("data.size", equalTo(20)) // 기본값
                    .body("data.totalElements", equalTo(5))
                    .body("data.content.size()", equalTo(5));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q2-S02] 페이징 동작 확인 (page=0, size=2)")
        void searchSellers_Paging_Returns200() {
            // given
            for (int i = 0; i < 5; i++) {
                sellerRepository.save(
                        SellerJpaEntityFixtures.activeEntityWithName("셀러" + i, "디스플레이" + i));
            }

            // when & then
            given().spec(givenAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.page", equalTo(0))
                    .body("data.size", equalTo(2))
                    .body("data.totalElements", equalTo(5))
                    .body("data.totalPages", greaterThanOrEqualTo(3))
                    .body("data.content.size()", equalTo(2));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q2-S03] active 필터 (active=true)")
        void searchSellers_ActiveFilter_Returns200() {
            // given
            sellerRepository.save(SellerJpaEntityFixtures.activeEntityWithName("활성셀러1", "활성1"));
            sellerRepository.save(SellerJpaEntityFixtures.activeEntityWithName("활성셀러2", "활성2"));
            sellerRepository.save(SellerJpaEntityFixtures.activeEntityWithName("활성셀러3", "활성3"));
            sellerRepository.save(SellerJpaEntityFixtures.inactiveEntityWithName("비활성1", "비활성1"));
            sellerRepository.save(SellerJpaEntityFixtures.inactiveEntityWithName("비활성2", "비활성2"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("active", true)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3))
                    .body("data.content[0].active", equalTo(true))
                    .body("data.content[1].active", equalTo(true))
                    .body("data.content[2].active", equalTo(true));
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q2-S04] sellerName 검색 (searchField=sellerName, searchWord=테스트)")
        void searchSellers_BySellerName_Returns200() {
            // given
            sellerRepository.save(SellerJpaEntityFixtures.activeEntityWithName("테스트셀러1", "디스플레이1"));
            sellerRepository.save(SellerJpaEntityFixtures.activeEntityWithName("테스트셀러2", "디스플레이2"));
            sellerRepository.save(SellerJpaEntityFixtures.activeEntityWithName("다른셀러", "다른"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("searchField", "sellerName")
                    .queryParam("searchWord", "테스트")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(2));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q2-F01] 잘못된 page 값 (page=-1)")
        void searchSellers_InvalidPageNegative_Returns400() {
            // when & then
            given().spec(givenAdmin())
                    .queryParam("page", -1)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q2-F02] 잘못된 size 값 (size=0)")
        void searchSellers_InvalidSizeZero_Returns400() {
            // when & then
            given().spec(givenAdmin())
                    .queryParam("size", 0)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q2-F03] 잘못된 size 값 (size=101)")
        void searchSellers_InvalidSizeExceeded_Returns400() {
            // when & then
            given().spec(givenAdmin())
                    .queryParam("size", 101)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q2-E01] 데이터 없을 때 빈 목록 반환")
        void searchSellers_NoData_ReturnsEmptyList() {
            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(0))
                    .body("data.content", empty());
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q2-E02] 존재하지 않는 페이지 요청 (page=100)")
        void searchSellers_NonExistingPage_ReturnsEmptyContent() {
            // given
            for (int i = 0; i < 5; i++) {
                sellerRepository.save(
                        SellerJpaEntityFixtures.activeEntityWithName("셀러" + i, "디스플레이" + i));
            }

            // when & then
            given().spec(givenAdmin())
                    .queryParam("page", 100)
                    .queryParam("size", 20)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(5))
                    .body("data.content", empty());
        }
    }

    // ===== 인증/인가 테스트 =====

    @Nested
    @DisplayName("GET /admin/sellers/{sellerId} - 인증/인가 테스트")
    class GetSellerDetailAuthorizationTest {

        @Test
        @Tag("P0")
        @Tag("auth")
        @DisplayName("[TC-Q1-A01] 토큰 없이 요청 시 401")
        void getSeller_Unauthenticated_Returns401() {
            // given
            var seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());

            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(BASE_URL + "/{sellerId}", seller.getId())
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P0")
        @Tag("auth")
        @DisplayName("[TC-Q1-A02] 다른 셀러가 권한 없이 접근 시 403")
        void getSeller_OtherSellerWithoutPermission_Returns403() {
            // given
            var seller1 =
                    sellerRepository.save(
                            SellerJpaEntityFixtures.activeEntityWithOrganization("org-seller-1"));
            var seller2 =
                    sellerRepository.save(
                            SellerJpaEntityFixtures.activeEntityWithOrganization("org-seller-2"));

            // when & then - seller2가 seller1 조회 시도 (권한 없음)
            given().spec(givenSellerUser("org-seller-2"))
                    .when()
                    .get(BASE_URL + "/{sellerId}", seller1.getId())
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @Tag("P0")
        @Tag("auth")
        @DisplayName("[TC-Q1-S02] 셀러 본인이 자기 리소스 조회 성공")
        void getSeller_SellerOwner_Returns200() {
            // given
            var seller =
                    sellerRepository.save(
                            SellerJpaEntityFixtures.activeEntityWithOrganization("org-seller-1"));
            businessInfoRepository.save(
                    SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(seller.getId()));
            sellerCsRepository.save(
                    SellerCsJpaEntityFixtures.activeEntityWithSellerId(seller.getId()));
            sellerContractRepository.save(
                    SellerContractJpaEntityFixtures.activeEntityWithSellerId(seller.getId()));
            sellerSettlementRepository.save(
                    SellerSettlementJpaEntityFixtures.verifiedEntityWithSellerId(seller.getId()));

            // when & then - 본인 셀러 조회
            given().spec(givenSellerUser("org-seller-1"))
                    .when()
                    .get(BASE_URL + "/{sellerId}", seller.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.seller.id", equalTo(seller.getId().intValue()));
        }

        @Test
        @Tag("P0")
        @Tag("auth")
        @DisplayName("[TC-Q1-S03] seller:read 권한으로 다른 셀러 조회 성공")
        void getSeller_WithSellerReadPermission_Returns200() {
            // given
            var seller1 =
                    sellerRepository.save(
                            SellerJpaEntityFixtures.activeEntityWithOrganization("org-seller-1"));
            var seller2 =
                    sellerRepository.save(
                            SellerJpaEntityFixtures.activeEntityWithOrganization("org-seller-2"));
            businessInfoRepository.save(
                    SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(seller1.getId()));
            sellerCsRepository.save(
                    SellerCsJpaEntityFixtures.activeEntityWithSellerId(seller1.getId()));
            sellerContractRepository.save(
                    SellerContractJpaEntityFixtures.activeEntityWithSellerId(seller1.getId()));
            sellerSettlementRepository.save(
                    SellerSettlementJpaEntityFixtures.verifiedEntityWithSellerId(seller1.getId()));

            // when & then - seller2가 seller:read 권한으로 seller1 조회
            given().spec(givenSellerUser("org-seller-2", "seller:read"))
                    .when()
                    .get(BASE_URL + "/{sellerId}", seller1.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.seller.id", equalTo(seller1.getId().intValue()));
        }

        @Test
        @Tag("P0")
        @Tag("auth")
        @DisplayName("[TC-Q1-A03] SUPER_ADMIN은 모든 셀러 조회 가능")
        void getSeller_SuperAdmin_Returns200() {
            // given
            var seller1 =
                    sellerRepository.save(
                            SellerJpaEntityFixtures.activeEntityWithOrganization("org-seller-1"));
            var seller2 =
                    sellerRepository.save(
                            SellerJpaEntityFixtures.activeEntityWithOrganization("org-seller-2"));
            businessInfoRepository.save(
                    SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(seller1.getId()));
            sellerCsRepository.save(
                    SellerCsJpaEntityFixtures.activeEntityWithSellerId(seller1.getId()));
            sellerContractRepository.save(
                    SellerContractJpaEntityFixtures.activeEntityWithSellerId(seller1.getId()));
            sellerSettlementRepository.save(
                    SellerSettlementJpaEntityFixtures.verifiedEntityWithSellerId(seller1.getId()));
            businessInfoRepository.save(
                    SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(seller2.getId()));
            sellerCsRepository.save(
                    SellerCsJpaEntityFixtures.activeEntityWithSellerId(seller2.getId()));
            sellerContractRepository.save(
                    SellerContractJpaEntityFixtures.activeEntityWithSellerId(seller2.getId()));
            sellerSettlementRepository.save(
                    SellerSettlementJpaEntityFixtures.verifiedEntityWithSellerId(seller2.getId()));

            // when & then - SUPER_ADMIN이 모든 셀러 조회
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL + "/{sellerId}", seller1.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value());

            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL + "/{sellerId}", seller2.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }
    }

    @Nested
    @DisplayName("GET /admin/sellers - 인증/인가 테스트")
    class SearchSellersAuthorizationTest {

        @Test
        @Tag("P0")
        @Tag("auth")
        @DisplayName("[TC-Q2-A01] 토큰 없이 요청 시 401")
        void searchSellers_Unauthenticated_Returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P0")
        @Tag("auth")
        @DisplayName("[TC-Q2-A02] SUPER_ADMIN 아닌 사용자 접근 시 403")
        void searchSellers_NonSuperAdmin_Returns403() {
            // given
            sellerRepository.save(
                    SellerJpaEntityFixtures.activeEntityWithOrganization("org-seller-1"));

            // when & then - 셀러 본인도 목록 조회 불가
            given().spec(givenSellerUser("org-seller-1"))
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @Tag("P0")
        @Tag("auth")
        @DisplayName("[TC-Q2-A03] 일반 사용자 접근 시 403 (seller:read 권한 있어도)")
        void searchSellers_AuthenticatedUserWithPermission_Returns403() {
            // given
            sellerRepository.save(SellerJpaEntityFixtures.activeEntity());

            // when & then - seller:read 권한으로도 목록 조회 불가
            given().spec(givenSellerUser("org-user-1", "seller:read"))
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @Tag("P0")
        @Tag("auth")
        @DisplayName("[TC-Q2-S01] SUPER_ADMIN은 목록 조회 가능")
        void searchSellers_SuperAdmin_Returns200() {
            // given
            for (int i = 0; i < 3; i++) {
                sellerRepository.save(
                        SellerJpaEntityFixtures.activeEntityWithName("셀러" + i, "디스플레이" + i));
            }

            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3));
        }
    }
}
