package com.ryuqq.marketplace.integration.shop;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.shop.ShopJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shop.repository.ShopJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.shop.repository.ShopQueryDslRepository;
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
 * Shop Query 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - GET /admin/shops - 외부몰 목록 검색
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("e2e")
@Tag("shop")
@Tag("query")
@DisplayName("Shop Query API E2E 테스트")
class ShopQueryE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/shops";

    @Autowired private ShopJpaRepository shopRepository;
    @Autowired private ShopQueryDslRepository shopQueryRepository;

    @BeforeEach
    void setUp() {
        shopRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        shopRepository.deleteAll();
    }

    // ===== GET /shops - 외부몰 목록 조회 =====

    @Nested
    @DisplayName("GET /shops - 외부몰 목록 조회")
    class SearchShopsTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q1-S01] 외부몰 목록 조회 성공")
        void searchShops_WithDefaultParams_Returns200() {
            // given
            shopRepository.save(ShopJpaEntityFixtures.newEntity());
            shopRepository.save(ShopJpaEntityFixtures.newEntity());

            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(greaterThanOrEqualTo(2)))
                    .body("data.content[0].shopName", notNullValue())
                    .body("data.content[0].accountId", notNullValue())
                    .body("data.content[0].status", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q1-S02] Shop명으로 검색 시 필터링 성공")
        void searchShops_WithShopNameFilter_Returns200() {
            // given
            var shop1 =
                    shopRepository.save(
                            ShopJpaEntityFixtures.activeEntityWithName("테스트몰A", "account-a"));
            shopRepository.save(ShopJpaEntityFixtures.activeEntityWithName("테스트몰B", "account-b"));

            // when & then
            given().spec(givenSuperAdmin())
                    .queryParam("searchField", "SHOP_NAME")
                    .queryParam("searchWord", "테스트몰A")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1))
                    .body("data.content[0].shopName", equalTo(shop1.getShopName()));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q1-S03] 활성/비활성 상태 필터링 성공")
        void searchShops_WithStatusFilter_Returns200() {
            // given
            shopRepository.save(ShopJpaEntityFixtures.newEntity());
            shopRepository.save(ShopJpaEntityFixtures.newInactiveEntity());

            // when & then - 활성만 조회
            given().spec(givenSuperAdmin())
                    .queryParam("statuses", "ACTIVE")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(greaterThanOrEqualTo(1)))
                    .body("data.content[0].status", equalTo("ACTIVE"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q1-S04] 페이징 파라미터 적용 성공")
        void searchShops_WithPagingParams_Returns200() {
            // given
            for (int i = 0; i < 5; i++) {
                shopRepository.save(ShopJpaEntityFixtures.newEntity());
            }

            // when & then
            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.page", equalTo(0))
                    .body("data.size", equalTo(2))
                    .body("data.totalElements", greaterThanOrEqualTo(5));
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-S05] 삭제된 외부몰은 조회되지 않음")
        void searchShops_ExcludesDeletedShops_Returns200() {
            // given
            shopRepository.save(ShopJpaEntityFixtures.newEntity());
            shopRepository.save(ShopJpaEntityFixtures.newDeletedEntity());

            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(greaterThanOrEqualTo(1)))
                    .body("data.content[0].deletedAt", nullValue());
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-S06] 결과가 없으면 빈 목록 반환")
        void searchShops_NoResults_ReturnsEmptyList() {
            // when & then
            given().spec(givenSuperAdmin())
                    .queryParam("searchWord", "존재하지않는외부몰")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(0))
                    .body("data.totalElements", equalTo(0));
        }
    }
}
