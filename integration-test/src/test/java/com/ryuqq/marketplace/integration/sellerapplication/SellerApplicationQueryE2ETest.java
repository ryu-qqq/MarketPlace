package com.ryuqq.marketplace.integration.sellerapplication;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.SellerApplicationJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.repository.SellerApplicationJpaRepository;
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
 * Seller Application Query 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - GET /seller-applications - 입점 신청 목록 조회 - GET /seller-applications/{id} - 입점 신청 상세 조회
 *
 * <p>특징: - 상태 관리 리소스 (PENDING, APPROVED, REJECTED) - SuperAdmin 권한 필요
 *
 * <p>시나리오: - P0: 8개 시나리오 (필수 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("sellerapplication")
@Tag("query")
@DisplayName("Seller Application Query API E2E 테스트")
class SellerApplicationQueryE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/seller-applications";

    @Autowired private SellerApplicationJpaRepository sellerApplicationRepository;

    @BeforeEach
    void setUp() {
        sellerApplicationRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        sellerApplicationRepository.deleteAll();
    }

    // ===== GET /seller-applications - 입점 신청 목록 조회 =====

    @Nested
    @DisplayName("GET /seller-applications - 입점 신청 목록 조회")
    class SearchSellerApplicationsTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1-1] 데이터 존재 시 정상 조회")
        void searchSellerApplications_withData_returnsOk() {
            // given: 5건 저장 (PENDING 3건, APPROVED 2건)
            sellerApplicationRepository.save(SellerApplicationJpaEntityFixtures.pendingEntity());
            sellerApplicationRepository.save(SellerApplicationJpaEntityFixtures.pendingEntity());
            sellerApplicationRepository.save(SellerApplicationJpaEntityFixtures.pendingEntity());
            sellerApplicationRepository.save(
                    SellerApplicationJpaEntityFixtures.approvedEntity(100L));
            sellerApplicationRepository.save(
                    SellerApplicationJpaEntityFixtures.approvedEntity(101L));

            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(5))
                    .body("data.totalElements", equalTo(5));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-2] 데이터 없을 때 빈 목록")
        void searchSellerApplications_noData_returnsEmptyList() {
            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", empty())
                    .body("data.totalElements", equalTo(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-6] 권한 검증 - 일반 사용자")
        void searchSellerApplications_authenticatedUser_returns403() {
            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    // ===== GET /seller-applications/{id} - 입점 신청 상세 조회 =====

    @Nested
    @DisplayName("GET /seller-applications/{id} - 입점 신청 상세 조회")
    class GetSellerApplicationTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q2-1] 존재하는 ID로 상세 조회")
        void getSellerApplication_existingId_returns200() {
            // given
            var application =
                    sellerApplicationRepository.save(
                            SellerApplicationJpaEntityFixtures.pendingEntity());

            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL + "/{id}", application.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.id", equalTo(application.getId().intValue()))
                    .body("data.status", equalTo("PENDING"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q2-2] 존재하지 않는 ID → 404")
        void getSellerApplication_nonExistingId_returns404() {
            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL + "/{id}", 99999)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }
}
