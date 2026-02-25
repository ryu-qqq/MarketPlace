package com.ryuqq.marketplace.integration.seller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerBusinessInfoJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerBusinessInfoJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Seller Public Profile 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - GET /sellers/{sellerId}/profile - 셀러 공개 프로필 조회 (인증 불필요)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("seller")
@Tag("query")
@DisplayName("Seller Public Profile API E2E 테스트")
class SellerPublicQueryE2ETest extends E2ETestBase {

    private static final String PROFILE_URL = "/sellers/{sellerId}/profile";

    @Autowired private SellerJpaRepository sellerRepository;
    @Autowired private SellerBusinessInfoJpaRepository businessInfoRepository;

    @BeforeEach
    void setUp() {
        businessInfoRepository.deleteAll();
        sellerRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        businessInfoRepository.deleteAll();
        sellerRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /sellers/{sellerId}/profile - 셀러 공개 프로필 조회")
    class GetSellerPublicProfileTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-PQ1-S01] 존재하는 셀러의 공개 프로필 조회 성공")
        void getSellerPublicProfile_ExistingId_Returns200() {
            // given
            var seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            businessInfoRepository.save(
                    SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(seller.getId()));

            // when & then - 인증 없이 요청
            given().contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .when()
                    .get(PROFILE_URL, seller.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.sellerName", notNullValue())
                    .body("data.displayName", notNullValue())
                    .body("data.companyName", notNullValue())
                    .body("data.representative", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-PQ1-S02] 공개 프로필에 4개 필드만 포함된다")
        void getSellerPublicProfile_ReturnsOnly4Fields() {
            // given
            var seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            businessInfoRepository.save(
                    SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(seller.getId()));

            // when & then
            given().contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .when()
                    .get(PROFILE_URL, seller.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.sellerName", notNullValue())
                    .body("data.displayName", notNullValue())
                    .body("data.companyName", notNullValue())
                    .body("data.representative", notNullValue())
                    // 민감 정보가 포함되지 않음을 검증
                    .body("data.logoUrl", nullValue())
                    .body("data.registrationNumber", nullValue())
                    .body("data.csPhone", nullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-PQ1-S03] 인증 없이도 접근 가능하다")
        void getSellerPublicProfile_Unauthenticated_Returns200() {
            // given
            var seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            businessInfoRepository.save(
                    SellerBusinessInfoJpaEntityFixtures.activeEntityWithSellerId(seller.getId()));

            // when & then - 인증 헤더 없이 요청
            given().contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .when()
                    .get(PROFILE_URL, seller.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-PQ1-F01] 존재하지 않는 셀러 ID 조회 시 404")
        void getSellerPublicProfile_NonExistingId_Returns404() {
            // given
            Long nonExistingId = 99999L;

            // when & then
            given().contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .when()
                    .get(PROFILE_URL, nonExistingId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-PQ1-F02] 잘못된 타입의 sellerId 전달 시 400")
        void getSellerPublicProfile_InvalidTypeId_Returns400() {
            // when & then
            given().contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .when()
                    .get(PROFILE_URL, "invalid")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }
}
