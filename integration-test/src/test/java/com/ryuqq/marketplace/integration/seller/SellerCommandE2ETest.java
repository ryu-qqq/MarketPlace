package com.ryuqq.marketplace.integration.seller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerBusinessInfoJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerContractJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerCsJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerSettlementJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerBusinessInfoJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerBusinessInfoQueryDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerContractJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerCsJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerSettlementJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Seller Command 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - POST /admin/sellers - 셀러 등록 - PUT /admin/sellers/{sellerId} - 셀러 전체정보 수정 - PATCH
 * /admin/sellers/{sellerId} - 셀러 기본정보 수정
 *
 * <p>우선순위: - P0: 20개 시나리오 (필수 기능) - P1: 7개 시나리오 (중요 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("seller")
@Tag("command")
@DisplayName("Seller Command API E2E 테스트")
class SellerCommandE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/sellers";

    @Autowired private SellerJpaRepository sellerRepository;
    @Autowired private SellerBusinessInfoJpaRepository businessInfoRepository;
    @Autowired private SellerBusinessInfoQueryDslRepository businessInfoQueryRepository;
    @Autowired private SellerCsJpaRepository sellerCsRepository;
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

    // ===== POST /sellers - 셀러 등록 =====

    @Nested
    @DisplayName("POST /admin/sellers - 셀러 등록")
    class RegisterSellerTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-S01] 유효한 요청으로 셀러 등록 성공")
        void registerSeller_ValidRequest_Returns201() {
            // given
            Map<String, Object> request = createRegisterRequest();

            // when
            Response response = given().spec(givenAdminJson()).body(request).when().post(BASE_URL);

            // then
            response.then().statusCode(HttpStatus.CREATED.value());

            Long sellerId = response.jsonPath().getLong("data.sellerId");
            assertThat(sellerId).isNotNull();

            // DB 검증
            assertThat(sellerRepository.findById(sellerId)).isPresent();
            assertThat(businessInfoQueryRepository.findBySellerId(sellerId)).isPresent();
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-F01] seller 필드 누락 시 400")
        void registerSeller_MissingSeller_Returns400() {
            // given
            Map<String, Object> request = createRegisterRequest();
            request.remove("seller");

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-F02] sellerName 필드 누락 시 400")
        void registerSeller_MissingSellerName_Returns400() {
            // given
            Map<String, Object> request = createRegisterRequest();
            @SuppressWarnings("unchecked")
            Map<String, Object> seller = (Map<String, Object>) request.get("seller");
            seller.put("sellerName", "");

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-F03] businessInfo 필드 누락 시 400")
        void registerSeller_MissingBusinessInfo_Returns400() {
            // given
            Map<String, Object> request = createRegisterRequest();
            request.remove("businessInfo");

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-F04] registrationNumber 필드 누락 시 400")
        void registerSeller_MissingRegistrationNumber_Returns400() {
            // given
            Map<String, Object> request = createRegisterRequest();
            @SuppressWarnings("unchecked")
            Map<String, Object> businessInfo = (Map<String, Object>) request.get("businessInfo");
            businessInfo.put("registrationNumber", "");

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-F07] 셀러명 중복 시 409")
        void registerSeller_DuplicateSellerName_Returns409() {
            // given
            sellerRepository.save(SellerJpaEntityFixtures.activeEntityWithName("중복셀러", "중복디스플레이"));
            Map<String, Object> request = createRegisterRequest();
            @SuppressWarnings("unchecked")
            Map<String, Object> seller = (Map<String, Object>) request.get("seller");
            seller.put("sellerName", "중복셀러");

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-F08] 사업자등록번호 중복 시 409")
        void registerSeller_DuplicateRegistrationNumber_Returns409() {
            // given - 첫 번째 셀러를 API를 통해 등록 (registrationNumber: "123-45-67890")
            Map<String, Object> firstRequest = createRegisterRequest();
            given().spec(givenAdminJson())
                    .body(firstRequest)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            // 두 번째 셀러 등록 시도 (같은 registrationNumber)
            Map<String, Object> secondRequest = createRegisterRequest();
            @SuppressWarnings("unchecked")
            Map<String, Object> seller2 = (Map<String, Object>) secondRequest.get("seller");
            seller2.put("sellerName", "다른셀러명"); // 셀러명은 다르게
            @SuppressWarnings("unchecked")
            Map<String, Object> businessInfo2 =
                    (Map<String, Object>) secondRequest.get("businessInfo");
            businessInfo2.put("registrationNumber", "123-45-67890"); // 동일 사업자번호

            // when & then
            given().spec(givenAdminJson())
                    .body(secondRequest)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value());
        }
    }

    // ===== PUT /sellers/{sellerId} - 셀러 전체정보 수정 =====

    @Nested
    @DisplayName("PUT /admin/sellers/{sellerId} - 셀러 전체정보 수정")
    class UpdateSellerFullTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-C2-S01] 모든 정보 수정 성공")
        void updateSellerFull_AllInfo_Returns204() {
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

            Map<String, Object> request = createUpdateFullRequest();

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{sellerId}", seller.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // DB 검증
            var updated = sellerRepository.findById(seller.getId()).orElseThrow();
            assertThat(updated.getSellerName()).isEqualTo("수정된셀러");
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C2-F01] 존재하지 않는 셀러 ID 수정 시 404")
        void updateSellerFull_NonExistingId_Returns404() {
            // given
            Long nonExistingId = 99999L;
            Map<String, Object> request = createUpdateFullRequest();

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{sellerId}", nonExistingId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C2-F02] 필수 필드 누락 (seller=null) 시 400")
        void updateSellerFull_MissingSeller_Returns400() {
            // given
            var seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Map<String, Object> request = createUpdateFullRequest();
            request.remove("seller");

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{sellerId}", seller.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== PATCH /sellers/{sellerId} - 셀러 기본정보 수정 =====

    @Nested
    @DisplayName("PATCH /admin/sellers/{sellerId} - 셀러 기본정보 수정")
    class UpdateSellerTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-S01] 기본정보만 수정 (선택 필드 없음)")
        void updateSeller_BasicInfoOnly_Returns204() {
            // given
            var seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Map<String, Object> request = createUpdateBasicRequest();

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/{sellerId}", seller.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // DB 검증
            var updated = sellerRepository.findById(seller.getId()).orElseThrow();
            assertThat(updated.getSellerName()).isEqualTo("수정된셀러명");
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-F01] 존재하지 않는 셀러 ID 수정 시 404")
        void updateSeller_NonExistingId_Returns404() {
            // given
            Long nonExistingId = 99999L;
            Map<String, Object> request = createUpdateBasicRequest();

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/{sellerId}", nonExistingId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-F02] 필수 필드 누락 (sellerName=\"\") 시 400")
        void updateSeller_MissingSellerName_Returns400() {
            // given
            var seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Map<String, Object> request = createUpdateBasicRequest();
            request.put("sellerName", "");

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/{sellerId}", seller.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== Helper Methods =====

    private Map<String, Object> createRegisterRequest() {
        Map<String, Object> seller =
                new HashMap<>(
                        Map.of(
                                "sellerName", "테스트셀러",
                                "displayName", "테스트 브랜드",
                                "logoUrl", "https://example.com/logo.png",
                                "description", "테스트 셀러 설명"));

        Map<String, Object> businessAddress =
                new HashMap<>(
                        Map.of(
                                "zipCode", "12345",
                                "line1", "서울시 강남구",
                                "line2", "테헤란로 123"));

        Map<String, Object> csContact =
                new HashMap<>(
                        Map.of(
                                "phone", "02-1234-5678",
                                "email", "cs@example.com",
                                "mobile", "010-1234-5678"));

        Map<String, Object> businessInfo =
                new HashMap<>(
                        Map.of(
                                "registrationNumber", "123-45-67890",
                                "companyName", "테스트컴퍼니",
                                "representative", "홍길동",
                                "saleReportNumber", "제2025-서울강남-1234호",
                                "businessAddress", businessAddress,
                                "csContact", csContact));

        return new HashMap<>(Map.of("seller", seller, "businessInfo", businessInfo));
    }

    private Map<String, Object> createUpdateFullRequest() {
        Map<String, Object> seller =
                new HashMap<>(
                        Map.of(
                                "sellerName", "수정된셀러",
                                "displayName", "수정된 브랜드",
                                "logoUrl", "https://example.com/new-logo.png",
                                "description", "수정된 설명"));

        Map<String, Object> businessAddress =
                new HashMap<>(
                        Map.of(
                                "zipCode", "54321",
                                "line1", "서울시 서초구",
                                "line2", "강남대로 456"));

        Map<String, Object> businessInfo =
                new HashMap<>(
                        Map.of(
                                "registrationNumber", "999-99-99999",
                                "companyName", "수정컴퍼니",
                                "representative", "김철수",
                                "saleReportNumber", "제2026-서울강남-5678호",
                                "businessAddress", businessAddress));

        Map<String, Object> csInfo =
                new HashMap<>(
                        Map.of(
                                "phone", "02-9999-8888",
                                "email", "updated-cs@example.com",
                                "mobile", "010-9999-8888",
                                "operatingStartTime", "09:00",
                                "operatingEndTime", "18:00",
                                "operatingDays", "MON,TUE,WED,THU,FRI",
                                "kakaoChannelUrl", "https://pf.kakao.com/_updated"));

        Map<String, Object> contractInfo =
                new HashMap<>(
                        Map.of(
                                "commissionRate", 10.5,
                                "contractStartDate", "2025-01-01",
                                "contractEndDate", "2025-12-31",
                                "specialTerms", "수정된 특약사항"));

        Map<String, Object> bankAccount =
                new HashMap<>(
                        Map.of(
                                "bankCode", "004",
                                "bankName", "KB국민은행",
                                "accountNumber", "12345678901234",
                                "accountHolderName", "김철수"));

        Map<String, Object> settlementInfo =
                new HashMap<>(
                        Map.of(
                                "bankAccount",
                                bankAccount,
                                "settlementCycle",
                                "MONTHLY",
                                "settlementDay",
                                25));

        Map<String, Object> request = new HashMap<>();
        request.put("seller", seller);
        request.put("businessInfo", businessInfo);
        request.put("csInfo", csInfo);
        request.put("contractInfo", contractInfo);
        request.put("settlementInfo", settlementInfo);
        return request;
    }

    private Map<String, Object> createUpdateBasicRequest() {
        return new HashMap<>(
                Map.of(
                        "sellerName", "수정된셀러명",
                        "displayName", "수정된 디스플레이명",
                        "logoUrl", "https://example.com/updated-logo.png",
                        "description", "수정된 셀러 설명"));
    }
}
