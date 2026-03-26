package com.ryuqq.marketplace.integration.shippingpolicy;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.ShippingPolicyJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.repository.ShippingPolicyJpaRepository;
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
 * ShippingPolicy Query 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - GET /sellers/{sellerId}/shipping-policies - 배송정책 목록 조회
 *
 * <p>우선순위: - P0: 5개 시나리오 (필수 기능) - P1: 7개 시나리오 (중요 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("shippingpolicy")
@Tag("query")
@DisplayName("ShippingPolicy Query API E2E 테스트")
class ShippingPolicyQueryE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/sellers/{sellerId}/shipping-policies";
    private static final Long DEFAULT_SELLER_ID = 1L;

    @Autowired private ShippingPolicyJpaRepository shippingPolicyRepository;

    @BeforeEach
    void setUp() {
        shippingPolicyRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        shippingPolicyRepository.deleteAll();
    }

    // ===== GET /sellers/{sellerId}/shipping-policies - 배송정책 목록 조회 =====

    @Nested
    @DisplayName("GET /sellers/{sellerId}/shipping-policies - 배송정책 목록 조회")
    class GetShippingPoliciesTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q1-01] 정상 조회 - 데이터 존재 시")
        void getShippingPolicies_ExistingData_Returns200() {
            // given
            List.of(
                            ShippingPolicyJpaEntityFixtures.newDefaultEntity(DEFAULT_SELLER_ID),
                            ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                                    DEFAULT_SELLER_ID, "정책2"),
                            ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                                    DEFAULT_SELLER_ID, "정책3"))
                    .forEach(shippingPolicyRepository::save);

            // when & then
            given().spec(givenAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.totalElements", equalTo(3));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q1-02] 빈 결과 - 데이터 없을 때")
        void getShippingPolicies_NoData_Returns200() {
            // given
            Long nonExistingSellerId = 999L;

            // when & then
            given().spec(givenAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(BASE_URL, nonExistingSellerId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(0))
                    .body("data.totalElements", equalTo(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q1-03] 기본 정책 존재 확인")
        void getShippingPolicies_DefaultPolicyExists_Returns200() {
            // given
            List.of(
                            ShippingPolicyJpaEntityFixtures.newDefaultEntity(
                                    DEFAULT_SELLER_ID), // defaultPolicy=true
                            ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                                    DEFAULT_SELLER_ID, "정책2"),
                            ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                                    DEFAULT_SELLER_ID, "정책3"))
                    .forEach(shippingPolicyRepository::save);

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body(
                            "data.content.findAll { it.defaultPolicy == true }.size()",
                            equalTo(1)); // 기본 정책 1개만 존재
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q1-04] 활성/비활성 정책 모두 조회")
        void getShippingPolicies_ActiveAndInactive_Returns200() {
            // given
            shippingPolicyRepository.save(
                    ShippingPolicyJpaEntityFixtures.newDefaultEntity(
                            DEFAULT_SELLER_ID)); // active=true
            shippingPolicyRepository.save(
                    ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                            DEFAULT_SELLER_ID, "정책2")); // active=true
            shippingPolicyRepository.save(
                    ShippingPolicyJpaEntityFixtures.newInactiveEntity(
                            DEFAULT_SELLER_ID)); // active=false

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.content.findAll { it.active == true }.size()", equalTo(2))
                    .body("data.content.findAll { it.active == false }.size()", equalTo(1));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q1-05] 삭제된 정책 제외")
        void getShippingPolicies_ExcludeDeleted_Returns200() {
            // given
            shippingPolicyRepository.save(
                    ShippingPolicyJpaEntityFixtures.newDefaultEntity(DEFAULT_SELLER_ID));
            shippingPolicyRepository.save(
                    ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                            DEFAULT_SELLER_ID, "정책2"));
            shippingPolicyRepository.save(
                    ShippingPolicyJpaEntityFixtures.newDeletedEntity(
                            DEFAULT_SELLER_ID)); // deletedAt != null

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2)); // 삭제된 정책 제외
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-06] 페이징 동작 확인")
        void getShippingPolicies_Paging_Returns200() {
            // given
            IntStream.rangeClosed(1, 5)
                    .forEach(
                            i ->
                                    shippingPolicyRepository.save(
                                            ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                                                    DEFAULT_SELLER_ID, "정책" + i)));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2))
                    .body("data.totalElements", equalTo(5))
                    .body("data.totalPages", equalTo(3));
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-07] 정렬 - 생성일 내림차순 (기본)")
        void getShippingPolicies_SortByCreatedAtDesc_Returns200() {
            // given
            IntStream.rangeClosed(1, 3)
                    .forEach(
                            i ->
                                    shippingPolicyRepository.save(
                                            ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                                                    DEFAULT_SELLER_ID, "정책" + i)));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sortKey", "CREATED_AT")
                    .queryParam("sortDirection", "DESC")
                    .when()
                    .get(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.content[0].createdAt", notNullValue());
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-08] 정렬 - 정책명 오름차순")
        void getShippingPolicies_SortByPolicyNameAsc_Returns200() {
            // given
            List.of("C정책", "A정책", "B정책")
                    .forEach(
                            name ->
                                    shippingPolicyRepository.save(
                                            ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                                                    DEFAULT_SELLER_ID, name)));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sortKey", "POLICY_NAME")
                    .queryParam("sortDirection", "ASC")
                    .when()
                    .get(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].policyName", equalTo("A정책"))
                    .body("data.content[1].policyName", equalTo("B정책"))
                    .body("data.content[2].policyName", equalTo("C정책"));
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-10] 응답 필드 검증 - CONDITIONAL_FREE")
        void getShippingPolicies_ConditionalFreeResponse_Returns200() {
            // given
            shippingPolicyRepository.save(
                    ShippingPolicyJpaEntityFixtures.activeConditionalFreeEntity());

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].shippingFeeType", equalTo("CONDITIONAL_FREE"))
                    .body("data.content[0].shippingFeeTypeDisplayName", equalTo("조건부 무료배송"))
                    .body("data.content[0].freeThreshold", notNullValue());
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-11] 응답 필드 검증 - FREE")
        void getShippingPolicies_FreeResponse_Returns200() {
            // given
            shippingPolicyRepository.save(ShippingPolicyJpaEntityFixtures.freeShippingEntity());

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].shippingFeeType", equalTo("FREE"))
                    .body("data.content[0].baseFee", equalTo(0))
                    .body("data.content[0].freeThreshold", anyOf(nullValue(), equalTo(0)));
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-Q1-12] ISO-8601 날짜 형식 검증")
        void getShippingPolicies_Iso8601DateFormat_Returns200() {
            // given
            shippingPolicyRepository.save(
                    ShippingPolicyJpaEntityFixtures.newDefaultEntity(DEFAULT_SELLER_ID));

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL, DEFAULT_SELLER_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(
                            "data.content[0].createdAt",
                            matchesRegex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
        }
    }
}
