package com.ryuqq.marketplace.integration.oms;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerJpaRepository;
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
 * OMS 파트너(셀러) 조회 API E2E 테스트.
 *
 * <p>테스트 대상: - GET /oms/partners - 파트너(셀러) 목록 조회
 *
 * <p>SearchOmsPartnersByOffsetService → SellerQueryPort → DB 전체 경로 검증.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("oms")
@Tag("query")
@DisplayName("OMS 파트너 조회 API E2E 테스트")
class OmsPartnerQueryE2ETest extends E2ETestBase {

    private static final String PARTNERS_URL = "/oms/partners";

    @Autowired private SellerJpaRepository sellerRepository;

    @BeforeEach
    void setUp() {
        sellerRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        sellerRepository.deleteAll();
    }

    // ========================================================================
    // 1. GET /oms/partners - 파트너 목록 조회
    // ========================================================================

    @Nested
    @DisplayName("GET /oms/partners - 파트너 목록 조회")
    class SearchOmsPartnersTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1-S01] 데이터 존재 시 파트너 목록 정상 조회")
        void searchOmsPartners_withData_returnsOk() {
            // given: 셀러 3건 저장
            sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            sellerRepository.save(SellerJpaEntityFixtures.activeEntity());

            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(PARTNERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3))
                    .body("data.content", hasSize(3));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-S02] 데이터 없을 때 빈 목록 반환")
        void searchOmsPartners_noData_returnsEmptyList() {
            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(PARTNERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(0))
                    .body("data.content", empty());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-S03] 페이징 파라미터 적용 시 지정한 크기만큼 반환")
        void searchOmsPartners_withPaging_returnsCorrectSize() {
            // given: 5건 저장
            for (int i = 0; i < 5; i++) {
                sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            }

            // when & then
            given().spec(givenAuthenticatedUser())
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(PARTNERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.page", equalTo(0))
                    .body("data.size", equalTo(2))
                    .body("data.totalElements", equalTo(5));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-S04] keyword 필터로 셀러명 검색")
        void searchOmsPartners_byKeyword_returnsFilteredResults() {
            // given: 검색 대상 셀러 2건, 일반 셀러 3건
            sellerRepository.save(
                    SellerJpaEntityFixtures.activeEntityWithName("나이키코리아", "나이키코리아 스토어"));
            sellerRepository.save(
                    SellerJpaEntityFixtures.activeEntityWithName("나이키글로벌", "나이키글로벌 스토어"));
            sellerRepository.save(
                    SellerJpaEntityFixtures.activeEntityWithName("아디다스코리아", "아디다스 스토어"));

            // when & then
            given().spec(givenAuthenticatedUser())
                    .queryParam("keyword", "나이키")
                    .when()
                    .get(PARTNERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(2))
                    .body("data.content.size()", equalTo(2));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-S05] keyword 검색 결과 없을 때 빈 목록 반환")
        void searchOmsPartners_byKeywordNoMatch_returnsEmptyList() {
            // given: 셀러 저장
            sellerRepository.save(SellerJpaEntityFixtures.activeEntityWithName("나이키", "나이키 스토어"));

            // when & then
            given().spec(givenAuthenticatedUser())
                    .queryParam("keyword", "존재하지않는셀러명")
                    .when()
                    .get(PARTNERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(0))
                    .body("data.content", empty());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-S06] 응답에 파트너 기본 정보 필드 포함")
        void searchOmsPartners_responseContainsRequiredFields() {
            // given
            sellerRepository.save(SellerJpaEntityFixtures.activeEntityWithName("테스트셀러", "테스트 스토어"));

            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(PARTNERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].id", notNullValue())
                    .body("data.content[0].partnerName", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-A01] 비인증 요청 시 401 반환")
        void searchOmsPartners_unauthenticated_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(PARTNERS_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-S07] 삭제된 셀러는 조회되지 않는다")
        void searchOmsPartners_deletedSeller_notReturned() {
            // given: 활성 셀러 2건, 삭제 셀러 1건
            sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            sellerRepository.save(SellerJpaEntityFixtures.deletedEntity());

            // when & then: 삭제된 셀러 제외
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(PARTNERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(2));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-S08] 두 번째 페이지 조회 시 올바른 데이터 반환")
        void searchOmsPartners_secondPage_returnsCorrectData() {
            // given: 5건 저장
            for (int i = 0; i < 5; i++) {
                sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            }

            // when & then: 두 번째 페이지 (page=1, size=2) → 2건 반환
            given().spec(givenAuthenticatedUser())
                    .queryParam("page", 1)
                    .queryParam("size", 2)
                    .when()
                    .get(PARTNERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.page", equalTo(1))
                    .body("data.totalElements", equalTo(5));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-S09] 마지막 페이지 초과 시 빈 목록 반환")
        void searchOmsPartners_beyondLastPage_returnsEmptyContent() {
            // given: 3건 저장
            for (int i = 0; i < 3; i++) {
                sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            }

            // when & then: page=100은 존재하지 않으므로 빈 content 반환
            given().spec(givenAuthenticatedUser())
                    .queryParam("page", 100)
                    .queryParam("size", 10)
                    .when()
                    .get(PARTNERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3))
                    .body("data.content", empty());
        }
    }

    // ========================================================================
    // 2. 전체 플로우 시나리오
    // ========================================================================

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F1] 파트너 목록 조회 → 키워드 검색 플로우")
        void fullFlow_listThenFilterByKeyword() {
            // Step 1: 다양한 셀러 저장
            sellerRepository.save(SellerJpaEntityFixtures.activeEntityWithName("삼성전자", "삼성 공식스토어"));
            sellerRepository.save(SellerJpaEntityFixtures.activeEntityWithName("LG전자", "LG 공식스토어"));
            sellerRepository.save(SellerJpaEntityFixtures.activeEntityWithName("삼성물산", "삼성물산 패션"));

            // Step 2: 전체 목록 조회 (3건)
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(PARTNERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3));

            // Step 3: "삼성" 키워드 검색 (2건)
            given().spec(givenAuthenticatedUser())
                    .queryParam("keyword", "삼성")
                    .when()
                    .get(PARTNERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(2));

            // Step 4: "LG" 키워드 검색 (1건)
            // partnerName 필드는 displayName("LG 공식스토어")에 매핑됨
            given().spec(givenAuthenticatedUser())
                    .queryParam("keyword", "LG")
                    .when()
                    .get(PARTNERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(1))
                    .body("data.content[0].partnerCode", equalTo("LG전자"));
        }
    }
}
