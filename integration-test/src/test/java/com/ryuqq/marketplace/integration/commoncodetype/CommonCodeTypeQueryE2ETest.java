package com.ryuqq.marketplace.integration.commoncodetype;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.CommonCodeTypeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.entity.CommonCodeTypeJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.repository.CommonCodeTypeJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * CommonCodeType Query 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - GET /api/v1/market/common-code-types - 공통 코드 타입 목록 조회
 *
 * <p>우선순위: - P0: 2개 시나리오 (필수 기능) - P1: 10개 시나리오 (중요 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("commoncodetype")
@Tag("query")
@DisplayName("CommonCodeType Query API E2E 테스트")
class CommonCodeTypeQueryE2ETest extends E2ETestBase {

    private static final String BASE_PATH = "/public/common-code-types";

    @Autowired private CommonCodeTypeJpaRepository commonCodeTypeRepository;

    @BeforeEach
    void setUp() {
        commonCodeTypeRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        commonCodeTypeRepository.deleteAll();
    }

    // ===== GET /api/v1/market/common-code-types - 목록 조회 =====

    @Nested
    @DisplayName("GET /api/v1/market/common-code-types - 목록 조회")
    class SearchCommonCodeTypesTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q1-01] 기본 조회 - 데이터 존재 시 정상 조회")
        void searchCommonCodeTypes_ExistingData_Returns200() {
            // given
            List.of(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"),
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "DELIVERY_COMPANY", "배송사"),
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode("BANK", "은행"),
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "CARD_COMPANY", "카드사"),
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "REFUND_REASON", "환불사유"))
                    .forEach(commonCodeTypeRepository::save);

            // when & then
            given().spec(givenAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(5))
                    .body("data.totalElements", equalTo(5))
                    .body("data.totalPages", equalTo(1))
                    .body("data.page", equalTo(0))
                    .body("data.last", equalTo(true))
                    .body("data.first", equalTo(true))
                    .body("data.content[0].id", notNullValue())
                    .body("data.content[0].code", notNullValue())
                    .body("data.content[0].name", notNullValue())
                    .body("data.content[0].displayOrder", notNullValue())
                    .body("data.content[0].active", notNullValue())
                    .body("data.content[0].createdAt", notNullValue())
                    .body("data.content[0].updatedAt", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q1-02] 빈 결과 - 데이터 없을 때 빈 목록 반환")
        void searchCommonCodeTypes_NoData_Returns200() {
            // given: 데이터 없음

            // when & then
            given().spec(givenAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(0))
                    .body("data.totalElements", equalTo(0))
                    .body("data.totalPages", equalTo(0))
                    .body("data.last", equalTo(true))
                    .body("data.first", equalTo(true));
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-03] 페이징 - page, size 파라미터 동작")
        void searchCommonCodeTypes_Paging_Returns200() {
            // given: 10건 생성
            IntStream.rangeClosed(1, 10)
                    .forEach(
                            i ->
                                    commonCodeTypeRepository.save(
                                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                                    "TYPE_" + i, "타입" + i)));

            // when & then: 첫 페이지 (0)
            given().spec(givenAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 3)
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.totalElements", equalTo(10))
                    .body("data.totalPages", equalTo(4))
                    .body("data.page", equalTo(0))
                    .body("data.last", equalTo(false))
                    .body("data.first", equalTo(true));

            // when & then: 두 번째 페이지 (1)
            given().spec(givenAdmin())
                    .queryParam("page", 1)
                    .queryParam("size", 3)
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.page", equalTo(1))
                    .body("data.last", equalTo(false))
                    .body("data.first", equalTo(false));
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-04] 활성화 필터 - active=true로 필터링")
        void searchCommonCodeTypes_ActiveFilter_True_Returns200() {
            // given
            IntStream.rangeClosed(1, 3)
                    .forEach(
                            i ->
                                    commonCodeTypeRepository.save(
                                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                                    "ACTIVE_" + i, "활성" + i)));

            IntStream.rangeClosed(1, 2)
                    .forEach(
                            i ->
                                    commonCodeTypeRepository.save(
                                            CommonCodeTypeJpaEntityFixtures.newInactiveEntity()));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("active", true)
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.totalElements", equalTo(3))
                    .body("data.content.findAll { it.active == true }.size()", equalTo(3));
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-05] 활성화 필터 - active=false로 필터링")
        void searchCommonCodeTypes_ActiveFilter_False_Returns200() {
            // given
            IntStream.rangeClosed(1, 3)
                    .forEach(
                            i ->
                                    commonCodeTypeRepository.save(
                                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                                    "ACTIVE_" + i, "활성" + i)));

            IntStream.rangeClosed(1, 2)
                    .forEach(
                            i ->
                                    commonCodeTypeRepository.save(
                                            CommonCodeTypeJpaEntityFixtures.newInactiveEntity()));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("active", false)
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2))
                    .body("data.totalElements", equalTo(2))
                    .body("data.content.findAll { it.active == false }.size()", equalTo(2));
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-06] 검색 - searchField=CODE & searchWord")
        void searchCommonCodeTypes_SearchByCode_Returns200() {
            // given
            List.of(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"),
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "DELIVERY_COMPANY", "배송사"),
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode("BANK", "은행"))
                    .forEach(commonCodeTypeRepository::save);

            // when & then
            given().spec(givenAdmin())
                    .queryParam("searchField", "CODE")
                    .queryParam("searchWord", "PAYMENT")
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(1))
                    .body("data.content[0].code", containsString("PAYMENT"));
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-07] 검색 - searchField=NAME & searchWord")
        void searchCommonCodeTypes_SearchByName_Returns200() {
            // given
            List.of(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"),
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "DELIVERY_COMPANY", "배송사"))
                    .forEach(commonCodeTypeRepository::save);

            // when & then
            given().spec(givenAdmin())
                    .queryParam("searchField", "NAME")
                    .queryParam("searchWord", "결제")
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(1))
                    .body("data.content[0].name", containsString("결제"));
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-08] 검색 - searchField 없이 searchWord만 (전체 필드 검색)")
        void searchCommonCodeTypes_SearchAllFields_Returns200() {
            // given
            List.of(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"),
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "DELIVERY_COMPANY", "배송사"))
                    .forEach(commonCodeTypeRepository::save);

            // when & then
            given().spec(givenAdmin())
                    .queryParam("searchWord", "결제")
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", greaterThanOrEqualTo(1));
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-09] 정렬 - sortKey=CREATED_AT, sortDirection=DESC")
        void searchCommonCodeTypes_SortByCreatedAtDesc_Returns200() {
            // given
            IntStream.rangeClosed(1, 3)
                    .forEach(
                            i -> {
                                commonCodeTypeRepository.save(
                                        CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                                "TYPE_" + i, "타입" + i));
                                try {
                                    Thread.sleep(100); // 생성 시간 차이를 위해
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            });

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sortKey", "CREATED_AT")
                    .queryParam("sortDirection", "DESC")
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.content[0].createdAt", notNullValue());
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-10] 정렬 - sortKey=DISPLAY_ORDER, sortDirection=ASC")
        void searchCommonCodeTypes_SortByDisplayOrderAsc_Returns200() {
            // given: displayOrder는 자동 증가 (1, 2, 3)
            List<CommonCodeTypeJpaEntity> entities =
                    List.of(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "TYPE_5", "타입5"), // displayOrder=1
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "TYPE_1", "타입1"), // displayOrder=2
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "TYPE_3", "타입3")); // displayOrder=3

            entities.forEach(commonCodeTypeRepository::save);

            // when & then: displayOrder ASC 정렬이 올바르게 적용되는지 검증
            // DISPLAY_ORDER_COUNTER가 static이므로 절대값 대신 상대 순서를 검증
            var response =
                    given().spec(givenAdmin())
                            .queryParam("sortKey", "DISPLAY_ORDER")
                            .queryParam("sortDirection", "ASC")
                            .when()
                            .get(BASE_PATH)
                            .then()
                            .statusCode(HttpStatus.OK.value())
                            .body("data.content.size()", equalTo(3))
                            .extract()
                            .response();

            int first = response.jsonPath().getInt("data.content[0].displayOrder");
            int second = response.jsonPath().getInt("data.content[1].displayOrder");
            int third = response.jsonPath().getInt("data.content[2].displayOrder");
            assertThat(first).isLessThanOrEqualTo(second);
            assertThat(second).isLessThanOrEqualTo(third);
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-11] 정렬 - sortKey=CODE, sortDirection=ASC")
        void searchCommonCodeTypes_SortByCodeAsc_Returns200() {
            // given
            List.of(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "CARD_COMPANY", "카드사"),
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode("BANK", "은행"),
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "DELIVERY_COMPANY", "배송사"))
                    .forEach(commonCodeTypeRepository::save);

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sortKey", "CODE")
                    .queryParam("sortDirection", "ASC")
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.content[0].code", equalTo("BANK"))
                    .body("data.content[1].code", equalTo("CARD_COMPANY"))
                    .body("data.content[2].code", equalTo("DELIVERY_COMPANY"));
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-12] 복합 필터 - active + searchWord + 정렬")
        void searchCommonCodeTypes_ComplexFilter_Returns200() {
            // given
            List.of(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"), // 활성화
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_GATEWAY", "결제게이트웨이")) // 활성화
                    .forEach(commonCodeTypeRepository::save);

            commonCodeTypeRepository.save(CommonCodeTypeJpaEntityFixtures.newInactiveEntity());

            // when & then
            given().spec(givenAdmin())
                    .queryParam("active", true)
                    .queryParam("searchWord", "PAYMENT")
                    .queryParam("sortKey", "DISPLAY_ORDER")
                    .queryParam("sortDirection", "ASC")
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2))
                    .body("data.content.findAll { it.active == true }.size()", equalTo(2))
                    .body(
                            "data.content.findAll { it.code.contains('PAYMENT') }.size()",
                            equalTo(2));
        }
    }

    // ===== 인증/인가 테스트 =====

    @Nested
    @DisplayName("인증/인가 테스트")
    @Tag("auth")
    class AuthorizationTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-A1-01] 비인증 요청 - 200 OK (public endpoint)")
        void searchCommonCodeTypes_Unauthenticated_Returns200() {
            // given: 데이터 1건 생성
            commonCodeTypeRepository.save(
                    CommonCodeTypeJpaEntityFixtures.newEntityWithCode("PAYMENT_METHOD", "결제수단"));

            // when & then: 공통 코드 타입 조회는 public endpoint (permitAll)
            given().spec(givenUnauthenticated())
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(1));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-A1-02] 인증된 사용자 요청 - 200 OK (Query 엔드포인트는 인증만 필요)")
        void searchCommonCodeTypes_AuthenticatedUser_Returns200() {
            // given: 데이터 1건 생성
            commonCodeTypeRepository.save(
                    CommonCodeTypeJpaEntityFixtures.newEntityWithCode("PAYMENT_METHOD", "결제수단"));

            // when & then
            given().spec(givenAuthenticatedUser())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(1));
        }
    }
}
