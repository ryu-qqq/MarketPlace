package com.ryuqq.marketplace.integration.saleschannel;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.repository.SalesChannelJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.repository.SalesChannelBrandJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.repository.SalesChannelCategoryJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Tag("e2e")
@Tag("sales-channel")
@Tag("flow")
@Tag("P1")
@DisplayName("[E2E] SalesChannel 통합 플로우 테스트")
class SalesChannelFlowE2ETest extends E2ETestBase {

    @Autowired private SalesChannelJpaRepository salesChannelRepository;
    @Autowired private SalesChannelBrandJpaRepository salesChannelBrandRepository;
    @Autowired private SalesChannelCategoryJpaRepository salesChannelCategoryRepository;

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {
        salesChannelCategoryRepository.deleteAll();
        salesChannelBrandRepository.deleteAll();
        salesChannelRepository.deleteAll();
    }

    // ===== SalesChannel =====

    @Nested
    @DisplayName("POST /sales-channels -> PUT /{id} -> GET /sales-channels")
    class SalesChannelCrud {

        @Test
        @DisplayName("FLOW-1: 판매채널 등록 -> 수정 -> 조회 전체 플로우")
        void registerUpdateAndSearch() {
            // 1) 등록
            Map<String, Object> registerRequest = new HashMap<>();
            registerRequest.put("channelName", "쿠팡");

            Long salesChannelId =
                    given().spec(givenSuperAdmin())
                            .body(registerRequest)
                            .when()
                            .post("/sales-channels")
                            .then()
                            .statusCode(201)
                            .body("data.salesChannelId", notNullValue())
                            .extract()
                            .jsonPath()
                            .getLong("data.salesChannelId");

            // 2) 수정
            Map<String, Object> updateRequest = new HashMap<>();
            updateRequest.put("channelName", "쿠팡(수정)");
            updateRequest.put("status", "INACTIVE");

            given().spec(givenSuperAdmin())
                    .body(updateRequest)
                    .when()
                    .put("/sales-channels/{id}", salesChannelId)
                    .then()
                    .statusCode(204);

            // 3) 조회 - 수정된 데이터 확인
            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get("/sales-channels")
                    .then()
                    .statusCode(200)
                    .body("data.totalElements", greaterThanOrEqualTo(1))
                    .body("data.content.size()", greaterThanOrEqualTo(1));
        }

        @Test
        @DisplayName("FLOW-2: 판매채널 등록 후 목록 조회에서 확인")
        void registerAndSearchByName() {
            // 등록
            Map<String, Object> request = new HashMap<>();
            request.put("channelName", "네이버쇼핑");

            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .post("/sales-channels")
                    .then()
                    .statusCode(201);

            // 조회 - channelName 검색
            given().spec(givenSuperAdmin())
                    .queryParam("searchField", "CHANNEL_NAME")
                    .queryParam("searchWord", "네이버쇼핑")
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get("/sales-channels")
                    .then()
                    .statusCode(200)
                    .body("data.totalElements", greaterThanOrEqualTo(1));
        }

        @Test
        @DisplayName("FLOW-3: 판매채널 수정 시 ACTIVE/INACTIVE 상태 변경")
        void updateStatus() {
            // 등록
            Map<String, Object> registerRequest = new HashMap<>();
            registerRequest.put("channelName", "11번가");

            Long id =
                    given().spec(givenSuperAdmin())
                            .body(registerRequest)
                            .when()
                            .post("/sales-channels")
                            .then()
                            .statusCode(201)
                            .extract()
                            .jsonPath()
                            .getLong("data.salesChannelId");

            // INACTIVE로 변경
            Map<String, Object> inactiveRequest = new HashMap<>();
            inactiveRequest.put("channelName", "11번가");
            inactiveRequest.put("status", "INACTIVE");

            given().spec(givenSuperAdmin())
                    .body(inactiveRequest)
                    .when()
                    .put("/sales-channels/{id}", id)
                    .then()
                    .statusCode(204);

            // INACTIVE 상태로 필터링 조회
            given().spec(givenSuperAdmin())
                    .queryParam("statuses", "INACTIVE")
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get("/sales-channels")
                    .then()
                    .statusCode(200)
                    .body("data.totalElements", greaterThanOrEqualTo(1));
        }
    }

    @Nested
    @DisplayName("판매채널 등록 검증")
    class SalesChannelValidation {

        @Test
        @DisplayName("CMD-1: 채널명 없이 등록 시 400 반환")
        void registerWithoutChannelName_returns400() {
            Map<String, Object> request = new HashMap<>();
            request.put("channelName", "");

            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .post("/sales-channels")
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CMD-2: 수정 시 잘못된 status 값이면 400 반환")
        void updateWithInvalidStatus_returns400() {
            Long id = createSalesChannel("테스트채널");

            Map<String, Object> updateRequest = new HashMap<>();
            updateRequest.put("channelName", "테스트채널");
            updateRequest.put("status", "INVALID_STATUS");

            given().spec(givenSuperAdmin())
                    .body(updateRequest)
                    .when()
                    .put("/sales-channels/{id}", id)
                    .then()
                    .statusCode(400);
        }
    }

    @Nested
    @DisplayName("판매채널 권한 검증")
    class SalesChannelAuth {

        @Test
        @DisplayName("AUTH-1: 인증 없이 판매채널 등록 시 401 반환")
        void registerWithoutAuth_returns401() {
            Map<String, Object> request = new HashMap<>();
            request.put("channelName", "무인증채널");

            given().spec(givenUnauthenticated())
                    .body(request)
                    .when()
                    .post("/sales-channels")
                    .then()
                    .statusCode(401);
        }

        @Test
        @DisplayName("AUTH-2: 셀러 권한으로 판매채널 등록 시 403 반환")
        void registerAsSeller_returns403() {
            Map<String, Object> request = new HashMap<>();
            request.put("channelName", "셀러채널");

            given().spec(givenSellerUser("org-100"))
                    .body(request)
                    .when()
                    .post("/sales-channels")
                    .then()
                    .statusCode(403);
        }
    }

    // ===== SalesChannelBrand =====

    @Nested
    @DisplayName("POST /sales-channels/{id}/brands -> GET /sales-channels/{id}/brands")
    class SalesChannelBrandCrud {

        @Test
        @DisplayName("BRAND-FLOW-1: 판매채널 생성 -> 브랜드 등록 -> 조회")
        void registerBrandAndSearch() {
            Long salesChannelId = createSalesChannel("브랜드테스트채널");

            // 브랜드 등록
            Map<String, Object> brandRequest = new HashMap<>();
            brandRequest.put("externalBrandCode", "NIKE-001");
            brandRequest.put("externalBrandName", "나이키");

            given().spec(givenSuperAdmin())
                    .body(brandRequest)
                    .when()
                    .post("/sales-channels/{id}/brands", salesChannelId)
                    .then()
                    .statusCode(201)
                    .body("data.brandIds.size()", equalTo(1))
                    .body("data.brandIds[0]", greaterThan(0));

            // 브랜드 목록 조회
            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get("/sales-channels/{id}/brands", salesChannelId)
                    .then()
                    .statusCode(200)
                    .body("data.totalElements", greaterThanOrEqualTo(1))
                    .body("data.content[0].externalBrandCode", equalTo("NIKE-001"))
                    .body("data.content[0].externalBrandName", equalTo("나이키"));
        }

        @Test
        @DisplayName("BRAND-FLOW-2: 여러 브랜드 등록 후 조회")
        void registerMultipleBrandsAndSearch() {
            Long salesChannelId = createSalesChannel("멀티브랜드채널");

            Map<String, Object> brand1 = new HashMap<>();
            brand1.put("externalBrandCode", "ADIDAS-001");
            brand1.put("externalBrandName", "아디다스");

            given().spec(givenSuperAdmin())
                    .body(brand1)
                    .when()
                    .post("/sales-channels/{id}/brands", salesChannelId)
                    .then()
                    .statusCode(201);

            Map<String, Object> brand2 = new HashMap<>();
            brand2.put("externalBrandCode", "PUMA-001");
            brand2.put("externalBrandName", "푸마");

            given().spec(givenSuperAdmin())
                    .body(brand2)
                    .when()
                    .post("/sales-channels/{id}/brands", salesChannelId)
                    .then()
                    .statusCode(201);

            // 전체 조회
            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get("/sales-channels/{id}/brands", salesChannelId)
                    .then()
                    .statusCode(200)
                    .body("data.totalElements", greaterThanOrEqualTo(2));
        }

        @Test
        @DisplayName("BRAND-VALID-1: 브랜드코드 없이 등록 시 400 반환")
        void registerBrandWithoutCode_returns400() {
            Long salesChannelId = createSalesChannel("검증채널");

            Map<String, Object> request = new HashMap<>();
            request.put("externalBrandCode", "");
            request.put("externalBrandName", "테스트");

            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .post("/sales-channels/{id}/brands", salesChannelId)
                    .then()
                    .statusCode(400);
        }
    }

    // ===== SalesChannelCategory =====

    @Nested
    @DisplayName("POST /sales-channels/{id}/categories -> GET /sales-channels/{id}/categories")
    class SalesChannelCategoryCrud {

        @Test
        @DisplayName("CAT-FLOW-1: 판매채널 생성 -> 카테고리 등록 -> 조회")
        void registerCategoryAndSearch() {
            Long salesChannelId = createSalesChannel("카테고리테스트채널");

            Map<String, Object> categoryRequest = new HashMap<>();
            categoryRequest.put("externalCategoryCode", "FASHION-001");
            categoryRequest.put("externalCategoryName", "패션");
            categoryRequest.put("parentId", 0L);
            categoryRequest.put("depth", 0);
            categoryRequest.put("path", "1");
            categoryRequest.put("sortOrder", 1);
            categoryRequest.put("leaf", false);
            categoryRequest.put("displayPath", "패션");

            given().spec(givenSuperAdmin())
                    .body(categoryRequest)
                    .when()
                    .post("/sales-channels/{id}/categories", salesChannelId)
                    .then()
                    .statusCode(201)
                    .body("data.categoryIds.size()", equalTo(1))
                    .body("data.categoryIds[0]", greaterThan(0));

            // 카테고리 목록 조회
            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get("/sales-channels/{id}/categories", salesChannelId)
                    .then()
                    .statusCode(200)
                    .body("data.totalElements", greaterThanOrEqualTo(1))
                    .body("data.content[0].externalCategoryCode", equalTo("FASHION-001"))
                    .body("data.content[0].externalCategoryName", equalTo("패션"));
        }

        @Test
        @DisplayName("CAT-FLOW-2: 계층 구조 카테고리 등록 후 depth 필터 조회")
        void registerHierarchicalCategoriesAndFilter() {
            Long salesChannelId = createSalesChannel("계층카테고리채널");

            // 상위 카테고리 등록
            Map<String, Object> parentCat = new HashMap<>();
            parentCat.put("externalCategoryCode", "TOP-001");
            parentCat.put("externalCategoryName", "의류");
            parentCat.put("parentId", 0L);
            parentCat.put("depth", 0);
            parentCat.put("path", "1");
            parentCat.put("sortOrder", 1);
            parentCat.put("leaf", false);
            parentCat.put("displayPath", "의류");

            Long parentCategoryId =
                    given().spec(givenSuperAdmin())
                            .body(parentCat)
                            .when()
                            .post("/sales-channels/{id}/categories", salesChannelId)
                            .then()
                            .statusCode(201)
                            .extract()
                            .jsonPath()
                            .getLong("data.categoryIds[0]");

            // 하위 카테고리 등록
            Map<String, Object> childCat = new HashMap<>();
            childCat.put("externalCategoryCode", "SUB-001");
            childCat.put("externalCategoryName", "남성의류");
            childCat.put("parentId", parentCategoryId);
            childCat.put("depth", 1);
            childCat.put("path", "1/" + parentCategoryId);
            childCat.put("sortOrder", 1);
            childCat.put("leaf", true);
            childCat.put("displayPath", "의류 > 남성의류");

            given().spec(givenSuperAdmin())
                    .body(childCat)
                    .when()
                    .post("/sales-channels/{id}/categories", salesChannelId)
                    .then()
                    .statusCode(201);

            // depth=0 필터 조회
            given().spec(givenSuperAdmin())
                    .queryParam("depth", 0)
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get("/sales-channels/{id}/categories", salesChannelId)
                    .then()
                    .statusCode(200)
                    .body("data.totalElements", greaterThanOrEqualTo(1));
        }

        @Test
        @DisplayName("CAT-VALID-1: 카테고리코드 없이 등록 시 400 반환")
        void registerCategoryWithoutCode_returns400() {
            Long salesChannelId = createSalesChannel("검증채널2");

            Map<String, Object> request = new HashMap<>();
            request.put("externalCategoryCode", "");
            request.put("externalCategoryName", "테스트");
            request.put("parentId", 0L);
            request.put("depth", 0);
            request.put("path", "1");
            request.put("sortOrder", 1);
            request.put("leaf", false);

            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .post("/sales-channels/{id}/categories", salesChannelId)
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("CAT-VALID-2: depth가 음수이면 400 반환")
        void registerCategoryWithNegativeDepth_returns400() {
            Long salesChannelId = createSalesChannel("검증채널3");

            Map<String, Object> request = new HashMap<>();
            request.put("externalCategoryCode", "NEG-001");
            request.put("externalCategoryName", "음수뎁스");
            request.put("parentId", 0L);
            request.put("depth", -1);
            request.put("path", "1");
            request.put("sortOrder", 1);
            request.put("leaf", false);

            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .post("/sales-channels/{id}/categories", salesChannelId)
                    .then()
                    .statusCode(400);
        }
    }

    // ===== Helper =====

    private Long createSalesChannel(String channelName) {
        Map<String, Object> request = new HashMap<>();
        request.put("channelName", channelName);

        return given().spec(givenSuperAdmin())
                .body(request)
                .when()
                .post("/sales-channels")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("data.salesChannelId");
    }
}
