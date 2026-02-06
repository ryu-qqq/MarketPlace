package com.ryuqq.marketplace.integration.seller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerContractJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerCsJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerSettlementJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerBusinessInfoJpaRepository;
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
 * Seller 통합 플로우 E2E 테스트.
 *
 * <p>여러 API를 순차적으로 호출하여 전체 시나리오를 검증합니다.
 *
 * <p>테스트 시나리오: - CRUD 전체 플로우 - 목록 조회 → 상세 조회 플로우 - 검색 → 수정 → 재검색 플로우
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("seller")
@Tag("flow")
@DisplayName("Seller Flow API E2E 테스트")
class SellerFlowE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/sellers";

    @Autowired private SellerJpaRepository sellerRepository;
    @Autowired private SellerBusinessInfoJpaRepository businessInfoRepository;
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

    @Nested
    @DisplayName("CRUD 전체 플로우")
    class CrudFullFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-FLOW-01] 생성 → 조회 → 수정 → 삭제 전체 플로우")
        void fullCrudFlow_Success() {
            // 1. POST /sellers - 셀러 생성
            Map<String, Object> createRequest = createRegisterRequest();
            Response createResponse =
                    given().spec(givenAdminJson()).body(createRequest).when().post(BASE_URL);

            createResponse.then().statusCode(HttpStatus.CREATED.value());
            Long sellerId = createResponse.jsonPath().getLong("data.sellerId");
            assertThat(sellerId).isNotNull();

            // 상세 조회에 필요한 CS/Contract/Settlement 데이터 생성
            sellerCsRepository.save(SellerCsJpaEntityFixtures.activeEntityWithSellerId(sellerId));
            sellerContractRepository.save(
                    SellerContractJpaEntityFixtures.activeEntityWithSellerId(sellerId));
            sellerSettlementRepository.save(
                    SellerSettlementJpaEntityFixtures.verifiedEntityWithSellerId(sellerId));

            // 2. GET /sellers/{sellerId} - 상세 조회
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL + "/{sellerId}", sellerId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.seller.id", equalTo(sellerId.intValue()))
                    .body("data.seller.sellerName", equalTo("테스트셀러"));

            // 3. GET /sellers - 목록 조회 (totalElements=1)
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(1));

            // 4. PATCH /sellers/{sellerId} - 기본정보 수정
            Map<String, Object> patchRequest =
                    Map.of(
                            "sellerName", "수정된셀러",
                            "displayName", "수정된디스플레이",
                            "logoUrl", "https://example.com/updated.png",
                            "description", "수정된 설명");

            given().spec(givenAdminJson())
                    .body(patchRequest)
                    .when()
                    .patch(BASE_URL + "/{sellerId}", sellerId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // 5. GET /sellers/{sellerId} - 수정 확인
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL + "/{sellerId}", sellerId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.seller.sellerName", equalTo("수정된셀러"));

            // 6. PUT /sellers/{sellerId} - 전체정보 수정
            Map<String, Object> putRequest = createUpdateFullRequest();
            given().spec(givenAdminJson())
                    .body(putRequest)
                    .when()
                    .put(BASE_URL + "/{sellerId}", sellerId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // 7. GET /sellers/{sellerId} - 전체 수정 확인
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL + "/{sellerId}", sellerId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.seller.sellerName", equalTo("최종수정셀러"));

            // DB 검증
            assertThat(sellerRepository.findById(sellerId)).isPresent();
            var seller = sellerRepository.findById(sellerId).orElseThrow();
            assertThat(seller.getSellerName()).isEqualTo("최종수정셀러");
        }
    }

    @Nested
    @DisplayName("목록 조회 → 상세 조회 플로우")
    class ListToDetailFlowTest {

        @Test
        @Tag("P1")
        @DisplayName("[TC-FLOW-02] 목록에서 ID 추출 → 상세 조회")
        void listToDetail_Success() {
            // given
            Map<String, Object> request1 = createRegisterRequest();
            Response response1 =
                    given().spec(givenAdminJson()).body(request1).when().post(BASE_URL);
            Long sellerId1 = response1.jsonPath().getLong("data.sellerId");

            Map<String, Object> request2 = createRegisterRequest();
            @SuppressWarnings("unchecked")
            Map<String, Object> seller2 = (Map<String, Object>) request2.get("seller");
            seller2 =
                    Map.of(
                            "sellerName", "테스트셀러2",
                            "displayName", "테스트 브랜드2",
                            "logoUrl", "https://example.com/logo2.png",
                            "description", "테스트 셀러2 설명");
            request2.put("seller", seller2);
            @SuppressWarnings("unchecked")
            Map<String, Object> bizInfo2 = (Map<String, Object>) request2.get("businessInfo");
            bizInfo2.put("registrationNumber", "999-88-77777");
            Response response2 =
                    given().spec(givenAdminJson()).body(request2).when().post(BASE_URL);
            Long sellerId2 = response2.jsonPath().getLong("data.sellerId");

            // 상세 조회에 필요한 CS/Contract/Settlement 데이터 생성
            sellerCsRepository.save(SellerCsJpaEntityFixtures.activeEntityWithSellerId(sellerId1));
            sellerContractRepository.save(
                    SellerContractJpaEntityFixtures.activeEntityWithSellerId(sellerId1));
            sellerSettlementRepository.save(
                    SellerSettlementJpaEntityFixtures.verifiedEntityWithSellerId(sellerId1));
            sellerCsRepository.save(SellerCsJpaEntityFixtures.activeEntityWithSellerId(sellerId2));
            sellerContractRepository.save(
                    SellerContractJpaEntityFixtures.activeEntityWithSellerId(sellerId2));
            sellerSettlementRepository.save(
                    SellerSettlementJpaEntityFixtures.verifiedEntityWithSellerId(sellerId2));

            // 1. GET /sellers - 목록 조회
            Response listResponse = given().spec(givenAdmin()).when().get(BASE_URL);

            listResponse
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", greaterThanOrEqualTo(2));

            // 2. 첫 번째 ID 추출
            Long firstId = listResponse.jsonPath().getLong("data.content[0].id");

            // 3. GET /sellers/{id} - 상세 조회
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL + "/{sellerId}", firstId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.seller.id", equalTo(firstId.intValue()));
        }
    }

    @Nested
    @DisplayName("검색 → 수정 → 재검색 플로우")
    class SearchUpdateRearchFlowTest {

        @Test
        @Tag("P1")
        @DisplayName("[TC-FLOW-03] 검색 → 수정 → 검색 결과 변경 확인")
        void searchUpdateSearch_Success() {
            // given
            for (int i = 1; i <= 3; i++) {
                Map<String, Object> request = createRegisterRequest();
                @SuppressWarnings("unchecked")
                Map<String, Object> seller = (Map<String, Object>) request.get("seller");
                seller =
                        Map.of(
                                "sellerName", "테스트셀러" + i,
                                "displayName", "테스트" + i,
                                "logoUrl", "https://example.com/logo" + i + ".png",
                                "description", "설명" + i);
                request.put("seller", seller);
                @SuppressWarnings("unchecked")
                Map<String, Object> bizInfo = (Map<String, Object>) request.get("businessInfo");
                bizInfo.put("registrationNumber", "100-00-0000" + i);
                given().spec(givenAdminJson()).body(request).when().post(BASE_URL);
            }

            // 1. GET /sellers?searchField=sellerName&searchWord=테스트 - 3건 조회
            Response searchResponse1 =
                    given().spec(givenAdmin())
                            .queryParam("searchField", "sellerName")
                            .queryParam("searchWord", "테스트")
                            .when()
                            .get(BASE_URL);

            searchResponse1
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3));

            Long firstId = searchResponse1.jsonPath().getLong("data.content[0].id");

            // 2. PATCH /sellers/{id} - 첫 번째 셀러명 수정
            Map<String, Object> updateRequest =
                    Map.of(
                            "sellerName", "변경된셀러",
                            "displayName", "변경된디스플레이",
                            "logoUrl", "https://example.com/changed.png",
                            "description", "변경된 설명");

            given().spec(givenAdminJson())
                    .body(updateRequest)
                    .when()
                    .patch(BASE_URL + "/{sellerId}", firstId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // 3. GET /sellers?searchField=sellerName&searchWord=테스트 - 2건 조회
            given().spec(givenAdmin())
                    .queryParam("searchField", "sellerName")
                    .queryParam("searchWord", "테스트")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(2));

            // 4. GET /sellers?searchField=sellerName&searchWord=변경 - 1건 조회
            given().spec(givenAdmin())
                    .queryParam("searchField", "sellerName")
                    .queryParam("searchWord", "변경")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(1));
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
                                "sellerName", "최종수정셀러",
                                "displayName", "최종수정 브랜드",
                                "logoUrl", "https://example.com/final-logo.png",
                                "description", "최종수정 설명"));

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
                                "companyName", "최종컴퍼니",
                                "representative", "김철수",
                                "saleReportNumber", "제2026-서울강남-9999호",
                                "businessAddress", businessAddress));

        Map<String, Object> csInfo =
                new HashMap<>(
                        Map.of(
                                "phone", "02-9999-8888",
                                "email", "final-cs@example.com",
                                "mobile", "010-9999-8888",
                                "operatingStartTime", "09:00",
                                "operatingEndTime", "18:00",
                                "operatingDays", "MON,TUE,WED,THU,FRI",
                                "kakaoChannelUrl", "https://pf.kakao.com/_final"));

        Map<String, Object> contractInfo =
                new HashMap<>(
                        Map.of(
                                "commissionRate", 10.5,
                                "contractStartDate", "2025-01-01",
                                "contractEndDate", "2025-12-31",
                                "specialTerms", "최종 특약사항"));

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
}
