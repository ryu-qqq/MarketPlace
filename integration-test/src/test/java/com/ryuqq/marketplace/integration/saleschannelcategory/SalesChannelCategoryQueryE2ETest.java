package com.ryuqq.marketplace.integration.saleschannelcategory;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.SalesChannelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.repository.SalesChannelJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.SalesChannelCategoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.repository.SalesChannelCategoryJpaRepository;
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
 * Sales Channel Category Query 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - GET /sales-channels/{salesChannelId}/categories - 판매채널 카테고리 목록 조회
 *
 * <p>시나리오: - P0: 6개 시나리오 (필수 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("saleschannelcategory")
@Tag("query")
@DisplayName("Sales Channel Category Query API E2E 테스트")
class SalesChannelCategoryQueryE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/sales-channels/{salesChannelId}/categories";

    @Autowired private SalesChannelJpaRepository salesChannelRepository;

    @Autowired private SalesChannelCategoryJpaRepository salesChannelCategoryRepository;

    @BeforeEach
    void setUp() {
        salesChannelCategoryRepository.deleteAll();
        salesChannelRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        salesChannelCategoryRepository.deleteAll();
        salesChannelRepository.deleteAll();
    }

    // ===== GET /sales-channels/{salesChannelId}/categories - 판매채널 카테고리 목록 조회 =====

    @Nested
    @DisplayName("GET /sales-channels/{salesChannelId}/categories - 판매채널 카테고리 목록 조회")
    class SearchCategoriesTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1-1] 데이터 존재 시 정상 조회")
        void searchCategories_withData_returnsOk() {
            // given: 판매채널 생성 후 카테고리 3건 등록
            var channel =
                    salesChannelRepository.save(
                            SalesChannelJpaEntityFixtures.activeEntityWithName("쿠팡"));
            salesChannelCategoryRepository.save(
                    SalesChannelCategoryJpaEntityFixtures.activeEntity(channel.getId()));
            salesChannelCategoryRepository.save(
                    SalesChannelCategoryJpaEntityFixtures.activeEntity(channel.getId()));
            salesChannelCategoryRepository.save(
                    SalesChannelCategoryJpaEntityFixtures.activeEntity(channel.getId()));

            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL, channel.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.totalElements", equalTo(3));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-2] 데이터 없을 때 빈 목록")
        void searchCategories_noData_returnsEmptyList() {
            // given: 판매채널만 생성 (카테고리 없음)
            var channel =
                    salesChannelRepository.save(
                            SalesChannelJpaEntityFixtures.activeEntityWithName("쿠팡"));

            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL, channel.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", empty())
                    .body("data.totalElements", equalTo(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-5] 존재하지 않는 채널 ID")
        void searchCategories_nonExistingChannel_returns404OrEmpty() {
            // when & then: 존재하지 않는 채널 ID로 조회
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL, 99999)
                    .then()
                    .statusCode(
                            anyOf(
                                    equalTo(HttpStatus.NOT_FOUND.value()),
                                    equalTo(HttpStatus.OK.value())));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-6] 권한 검증 - 일반 사용자")
        void searchCategories_authenticatedUser_returns403() {
            // given
            var channel =
                    salesChannelRepository.save(
                            SalesChannelJpaEntityFixtures.activeEntityWithName("쿠팡"));

            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(BASE_URL, channel.getId())
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    // ===== 전체 플로우 시나리오 =====

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F1] 판매채널 생성 → 카테고리 등록 → 조회 플로우 (Query Only)")
        void fullFlow_channelToListCategories() {
            // Step 1: 판매채널 생성
            var channel =
                    salesChannelRepository.save(
                            SalesChannelJpaEntityFixtures.activeEntityWithName("쿠팡"));

            // Step 2: 카테고리 2건 등록
            var category1 =
                    salesChannelCategoryRepository.save(
                            SalesChannelCategoryJpaEntityFixtures.activeEntity(channel.getId()));
            var category2 =
                    salesChannelCategoryRepository.save(
                            SalesChannelCategoryJpaEntityFixtures.activeEntity(channel.getId()));

            // Step 3: 카테고리 목록 조회
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL, channel.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(2));

            // DB 검증
            assertThat(salesChannelCategoryRepository.count()).isEqualTo(2);
        }
    }
}
