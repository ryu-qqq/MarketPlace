package com.ryuqq.marketplace.integration.canonicaloption;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.CanonicalOptionGroupJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.repository.CanonicalOptionGroupJpaRepository;
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
 * Canonical Option Group Query 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - GET /canonical-option-groups - 정규 옵션그룹 목록 조회 - GET /canonical-option-groups/{id} -
 * 정규 옵션그룹 상세 조회
 *
 * <p>특징: - 읽기 전용 리소스 (Command 없음) - SuperAdmin 권한 불필요 (권한 체크만)
 *
 * <p>시나리오: - P0: 7개 시나리오 (필수 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("canonicaloption")
@Tag("query")
@DisplayName("Canonical Option Group Query API E2E 테스트")
class CanonicalOptionGroupQueryE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/canonical-option-groups";

    @Autowired private CanonicalOptionGroupJpaRepository canonicalOptionGroupRepository;

    @BeforeEach
    void setUp() {
        canonicalOptionGroupRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        canonicalOptionGroupRepository.deleteAll();
    }

    // ===== GET /canonical-option-groups - 정규 옵션그룹 목록 조회 =====

    @Nested
    @DisplayName("GET /canonical-option-groups - 정규 옵션그룹 목록 조회")
    class SearchCanonicalOptionGroupsTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1-1] 데이터 존재 시 정상 조회")
        void searchCanonicalOptionGroups_withData_returnsOk() {
            // given: 5건 저장
            for (int i = 0; i < 5; i++) {
                canonicalOptionGroupRepository.save(
                        CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("CODE_Q1_" + i));
            }

            // when & then
            given().spec(
                            givenAuthenticatedUser()
                                    .header("X-User-Permissions", "canonical-option-group:read"))
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
        void searchCanonicalOptionGroups_noData_returnsEmptyList() {
            // when & then
            given().spec(
                            givenAuthenticatedUser()
                                    .header("X-User-Permissions", "canonical-option-group:read"))
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", empty())
                    .body("data.totalElements", equalTo(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-5] 권한 없는 사용자 접근")
        void searchCanonicalOptionGroups_unauthenticated_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ===== GET /canonical-option-groups/{id} - 정규 옵션그룹 상세 조회 =====

    @Nested
    @DisplayName("GET /canonical-option-groups/{id} - 정규 옵션그룹 상세 조회")
    class GetCanonicalOptionGroupTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q2-1] 존재하는 ID로 상세 조회")
        void getCanonicalOptionGroup_existingId_returns200() {
            // given
            var optionGroup =
                    canonicalOptionGroupRepository.save(
                            CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("CODE_Q2"));

            // when & then
            given().spec(
                            givenAuthenticatedUser()
                                    .header("X-User-Permissions", "canonical-option-group:read"))
                    .when()
                    .get(BASE_URL + "/{id}", optionGroup.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.id", equalTo(optionGroup.getId().intValue()));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q2-2] 존재하지 않는 ID → 404")
        void getCanonicalOptionGroup_nonExistingId_returns404() {
            // when & then
            given().spec(
                            givenAuthenticatedUser()
                                    .header("X-User-Permissions", "canonical-option-group:read"))
                    .when()
                    .get(BASE_URL + "/{id}", 99999)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    // ===== 전체 플로우 시나리오 =====

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F1] 목록 조회 → 상세 조회 플로우")
        void fullFlow_listToDetail() {
            // Step 1: 사전 데이터 저장 (3건)
            var optionGroup1 =
                    canonicalOptionGroupRepository.save(
                            CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode(
                                    "CODE_F1_1"));
            var optionGroup2 =
                    canonicalOptionGroupRepository.save(
                            CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode(
                                    "CODE_F1_2"));
            var optionGroup3 =
                    canonicalOptionGroupRepository.save(
                            CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode(
                                    "CODE_F1_3"));

            // Step 2: 목록 조회
            var listResponse =
                    given().spec(
                                    givenAuthenticatedUser()
                                            .header(
                                                    "X-User-Permissions",
                                                    "canonical-option-group:read"))
                            .when()
                            .get(BASE_URL)
                            .then()
                            .statusCode(HttpStatus.OK.value())
                            .body("data.totalElements", equalTo(3))
                            .extract()
                            .response();

            // Step 3: 첫 번째 ID 추출
            Long extractedId = listResponse.jsonPath().getLong("data.content[0].id");

            // Step 4: 상세 조회
            given().spec(
                            givenAuthenticatedUser()
                                    .header("X-User-Permissions", "canonical-option-group:read"))
                    .when()
                    .get(BASE_URL + "/{id}", extractedId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.id", equalTo(extractedId.intValue()));

            // DB 일관성 검증
            assertThat(canonicalOptionGroupRepository.findById(extractedId)).isPresent();
        }
    }
}
