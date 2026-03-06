package com.ryuqq.marketplace.integration.oms;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.shop.ShopJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shop.repository.ShopJpaRepository;
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
 * OMS 쇼핑몰 조회 API E2E 테스트.
 *
 * <p>테스트 대상: - GET /oms/shops - 쇼핑몰 목록 조회
 *
 * <p>SearchOmsShopsByOffsetService → ShopQueryPort → DB 전체 경로 검증.
 *
 * <p>기존 ShopQueryE2ETest(/shops)와 달리 /oms/shops 엔드포인트를 테스트합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("oms")
@Tag("query")
@DisplayName("OMS 쇼핑몰 조회 API E2E 테스트")
class OmsShopQueryE2ETest extends E2ETestBase {

    private static final String OMS_SHOPS_URL = "/oms/shops";

    @Autowired private ShopJpaRepository shopRepository;

    @BeforeEach
    void setUp() {
        shopRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        shopRepository.deleteAll();
    }

    // ========================================================================
    // 1. GET /oms/shops - 쇼핑몰 목록 조회
    // ========================================================================

    @Nested
    @DisplayName("GET /oms/shops - 쇼핑몰 목록 조회")
    class SearchOmsShopsTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1-S01] 데이터 존재 시 쇼핑몰 목록 정상 조회")
        void searchOmsShops_withData_returnsOk() {
            // given: 쇼핑몰 3건 저장
            shopRepository.save(ShopJpaEntityFixtures.newEntity());
            shopRepository.save(ShopJpaEntityFixtures.newEntity());
            shopRepository.save(ShopJpaEntityFixtures.newEntity());

            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(OMS_SHOPS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3))
                    .body("data.content", hasSize(3));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-S02] 데이터 없을 때 빈 목록 반환")
        void searchOmsShops_noData_returnsEmptyList() {
            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(OMS_SHOPS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(0))
                    .body("data.content", empty());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-S03] 페이징 파라미터 적용 시 지정한 크기만큼 반환")
        void searchOmsShops_withPaging_returnsCorrectSize() {
            // given: 5건 저장
            for (int i = 0; i < 5; i++) {
                shopRepository.save(ShopJpaEntityFixtures.newEntity());
            }

            // when & then
            given().spec(givenAuthenticatedUser())
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(OMS_SHOPS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.page", equalTo(0))
                    .body("data.size", equalTo(2))
                    .body("data.totalElements", equalTo(5));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-S04] keyword 필터로 쇼핑몰명 검색")
        void searchOmsShops_byKeyword_returnsFilteredResults() {
            // given: 검색 대상 쇼핑몰 2건, 일반 쇼핑몰 1건
            shopRepository.save(ShopJpaEntityFixtures.activeEntityWithName("스마트스토어A", "account-a"));
            shopRepository.save(ShopJpaEntityFixtures.activeEntityWithName("스마트스토어B", "account-b"));
            shopRepository.save(ShopJpaEntityFixtures.activeEntityWithName("쿠팡마켓", "account-c"));

            // when & then: 스마트스토어 검색
            given().spec(givenAuthenticatedUser())
                    .queryParam("keyword", "스마트스토어")
                    .when()
                    .get(OMS_SHOPS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(2))
                    .body("data.content.size()", equalTo(2));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-S05] keyword 검색 결과 없을 때 빈 목록 반환")
        void searchOmsShops_byKeywordNoMatch_returnsEmptyList() {
            // given
            shopRepository.save(ShopJpaEntityFixtures.activeEntityWithName("스마트스토어", "account-a"));

            // when & then
            given().spec(givenAuthenticatedUser())
                    .queryParam("keyword", "존재하지않는쇼핑몰")
                    .when()
                    .get(OMS_SHOPS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(0))
                    .body("data.content", empty());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-S06] 응답에 쇼핑몰 기본 정보 필드 포함")
        void searchOmsShops_responseContainsRequiredFields() {
            // given
            shopRepository.save(ShopJpaEntityFixtures.activeEntityWithName("테스트몰", "account-test"));

            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(OMS_SHOPS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].id", notNullValue())
                    .body("data.content[0].shopName", notNullValue())
                    .body("data.content[0].accountId", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-A01] 비인증 요청 시 401 반환")
        void searchOmsShops_unauthenticated_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(OMS_SHOPS_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-S07] 소프트 삭제된 쇼핑몰은 조회되지 않는다")
        void searchOmsShops_deletedShop_notReturned() {
            // given: 활성 쇼핑몰 2건, 삭제 쇼핑몰 1건
            shopRepository.save(ShopJpaEntityFixtures.newEntity());
            shopRepository.save(ShopJpaEntityFixtures.newEntity());
            shopRepository.save(ShopJpaEntityFixtures.newDeletedEntity());

            // when & then: 삭제된 쇼핑몰 제외
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(OMS_SHOPS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(2));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-S08] 두 번째 페이지 조회 시 올바른 데이터 반환")
        void searchOmsShops_secondPage_returnsCorrectData() {
            // given: 5건 저장
            for (int i = 0; i < 5; i++) {
                shopRepository.save(ShopJpaEntityFixtures.newEntity());
            }

            // when & then: 두 번째 페이지 (page=1, size=2) → 2건 반환
            given().spec(givenAuthenticatedUser())
                    .queryParam("page", 1)
                    .queryParam("size", 2)
                    .when()
                    .get(OMS_SHOPS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.page", equalTo(1))
                    .body("data.totalElements", equalTo(5));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-S09] 마지막 페이지 초과 시 빈 목록 반환")
        void searchOmsShops_beyondLastPage_returnsEmptyContent() {
            // given: 3건 저장
            for (int i = 0; i < 3; i++) {
                shopRepository.save(ShopJpaEntityFixtures.newEntity());
            }

            // when & then
            given().spec(givenAuthenticatedUser())
                    .queryParam("page", 100)
                    .queryParam("size", 10)
                    .when()
                    .get(OMS_SHOPS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3))
                    .body("data.content", empty());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-S10] INACTIVE 쇼핑몰도 삭제되지 않으면 조회됨")
        void searchOmsShops_inactiveShop_returned() {
            // given: 활성 1건, 비활성 1건 (삭제는 아님)
            shopRepository.save(ShopJpaEntityFixtures.newEntity());
            shopRepository.save(ShopJpaEntityFixtures.newInactiveEntity());

            // when & then: 비활성 쇼핑몰도 조회 포함 (삭제된 것만 제외)
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(OMS_SHOPS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", greaterThanOrEqualTo(1));
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
        @DisplayName("[F1] 쇼핑몰 목록 조회 → 키워드 검색 플로우")
        void fullFlow_listThenFilterByKeyword() {
            // Step 1: 다양한 쇼핑몰 저장
            shopRepository.save(
                    ShopJpaEntityFixtures.activeEntityWithName("스마트스토어 공식", "naver-official"));
            shopRepository.save(
                    ShopJpaEntityFixtures.activeEntityWithName("쿠팡마켓플레이스", "coupang-mp"));
            shopRepository.save(
                    ShopJpaEntityFixtures.activeEntityWithName("스마트스토어 파트너", "naver-partner"));

            // Step 2: 전체 목록 조회 (3건)
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(OMS_SHOPS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3));

            // Step 3: "스마트스토어" 키워드 검색 (2건)
            given().spec(givenAuthenticatedUser())
                    .queryParam("keyword", "스마트스토어")
                    .when()
                    .get(OMS_SHOPS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(2));

            // Step 4: "쿠팡" 키워드 검색 (1건)
            given().spec(givenAuthenticatedUser())
                    .queryParam("keyword", "쿠팡")
                    .when()
                    .get(OMS_SHOPS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(1))
                    .body("data.content[0].shopName", equalTo("쿠팡마켓플레이스"));
        }

        @Test
        @Tag("P1")
        @DisplayName("[F2] 삭제 쇼핑몰 혼재 시 활성 쇼핑몰만 조회되는 플로우")
        void fullFlow_mixedStatus_onlyActiveReturned() {
            // Step 1: 활성 3건, 삭제 2건 저장
            shopRepository.save(ShopJpaEntityFixtures.newEntity());
            shopRepository.save(ShopJpaEntityFixtures.newEntity());
            shopRepository.save(ShopJpaEntityFixtures.newEntity());
            shopRepository.save(ShopJpaEntityFixtures.newDeletedEntity());
            shopRepository.save(ShopJpaEntityFixtures.newDeletedEntity());

            // Step 2: 전체 조회 - 활성 3건만 반환
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(OMS_SHOPS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3));

            // Step 3: 페이징 적용 시에도 동일한 총 건수
            given().spec(givenAuthenticatedUser())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(OMS_SHOPS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3))
                    .body("data.content", hasSize(3));
        }
    }
}
