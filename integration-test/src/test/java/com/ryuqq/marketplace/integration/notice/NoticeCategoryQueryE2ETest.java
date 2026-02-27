package com.ryuqq.marketplace.integration.notice;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.persistence.notice.NoticeFieldJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.notice.repository.NoticeCategoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.notice.repository.NoticeFieldJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Notice Category Query 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - GET /notice-categories - 고시정보 카테고리 목록 조회 (페이징) - GET
 * /notice-categories/category-group/{categoryGroup} - 카테고리 그룹별 조회
 *
 * <p>Phase 4 (LOW): 읽기 전용 리소스, SuperAdmin 불필요 (notice-category:read 권한)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("notice")
@Tag("query")
@DisplayName("[E2E] Notice Category Query API 테스트")
class NoticeCategoryQueryE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/notice-categories";

    @Autowired private NoticeCategoryJpaRepository noticeCategoryRepository;
    @Autowired private NoticeFieldJpaRepository noticeFieldRepository;

    @BeforeEach
    void setUp() {
        noticeFieldRepository.deleteAll();
        noticeCategoryRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        noticeFieldRepository.deleteAll();
        noticeCategoryRepository.deleteAll();
    }

    /** ID가 null인 새 NoticeCategory 엔티티 생성 (JPA INSERT 보장). */
    private static NoticeCategoryJpaEntity newCategory(
            String code, String targetCategoryGroup, boolean active) {
        Instant now = Instant.now();
        return NoticeCategoryJpaEntity.create(
                null, code, "테스트카테고리", "TestCategory", targetCategoryGroup, active, now, now);
    }

    // ===== GET /notice-categories - 목록 조회 =====

    @Nested
    @DisplayName("GET /notice-categories - 고시정보 카테고리 목록 조회")
    class SearchNoticeCategoriesTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1-1] 데이터 존재 시 정상 페이징 조회")
        void searchNoticeCategories_withData_returnsPagedResult() {
            // given: 3건 저장 (targetCategoryGroup 유니크 제약 대응)
            noticeCategoryRepository.save(newCategory("NC_Q1_0", "CLOTHING", true));
            noticeCategoryRepository.save(newCategory("NC_Q1_1", "SHOES", true));
            noticeCategoryRepository.save(newCategory("NC_Q1_2", "BAGS", true));

            // when & then
            given().spec(givenWithPermission("notice-category:read"))
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3))
                    .body("data.content.size()", equalTo(3))
                    .body("data.content[0].code", notNullValue())
                    .body("data.content[0].active", equalTo(true));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-2] 데이터 없을 때 빈 목록")
        void searchNoticeCategories_noData_returnsEmptyList() {
            // when & then
            given().spec(givenWithPermission("notice-category:read"))
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", empty())
                    .body("data.totalElements", equalTo(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-3] 활성 상태 필터링 조회")
        void searchNoticeCategories_activeFilter_returnsOnlyActive() {
            // given: 활성 2건, 비활성 1건
            noticeCategoryRepository.save(newCategory("NC_ACTIVE_1", "CLOTHING", true));
            noticeCategoryRepository.save(newCategory("NC_ACTIVE_2", "SHOES", true));
            noticeCategoryRepository.save(newCategory("NC_INACTIVE_1", "BAGS", false));

            // when & then: 활성만 필터
            given().spec(givenWithPermission("notice-category:read"))
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .queryParam("active", true)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(2));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-4] 필드 포함 조회 - 카테고리에 필드가 있으면 fields 배열 반환")
        void searchNoticeCategories_withFields_returnsFieldsInResponse() {
            // given: 카테고리 1건 + 필드 2건
            var category =
                    noticeCategoryRepository.save(newCategory("NC_FIELD_TEST", "BAGS", true));
            noticeFieldRepository.save(
                    NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(category.getId()));
            noticeFieldRepository.save(
                    NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(category.getId()));

            // when & then
            given().spec(givenWithPermission("notice-category:read"))
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(1))
                    .body("data.content[0].fields.size()", equalTo(2))
                    .body("data.content[0].fields[0].fieldCode", notNullValue())
                    .body("data.content[0].fields[0].fieldName", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-5] 비인증 사용자 접근 → 401")
        void searchNoticeCategories_unauthenticated_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ===== GET /notice-categories/category-group/{categoryGroup} =====

    @Nested
    @DisplayName("GET /notice-categories/category-group/{categoryGroup} - 카테고리 그룹별 조회")
    class GetNoticeCategoryByCategoryGroupTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q2-1] 존재하는 카테고리 그룹 조회 → 200")
        void getNoticeCategoryByCategoryGroup_existing_returns200() {
            // given: CLOTHING 그룹 카테고리 저장
            noticeCategoryRepository.save(newCategory("NC_GROUP_TEST", "CLOTHING", true));

            // when & then
            given().spec(givenWithPermission("notice-category:read"))
                    .when()
                    .get(BASE_URL + "/category-group/{categoryGroup}", "CLOTHING")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.id", notNullValue())
                    .body("data.targetCategoryGroup", equalTo("CLOTHING"))
                    .body("data.active", equalTo(true));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q2-2] 필드 포함 카테고리 그룹 조회")
        void getNoticeCategoryByCategoryGroup_withFields_returnsFieldsIncluded() {
            // given: SHOES 그룹 카테고리 + 필드 2건
            var category =
                    noticeCategoryRepository.save(newCategory("NC_SHOES_FIELD", "SHOES", true));
            noticeFieldRepository.save(
                    NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(category.getId()));
            noticeFieldRepository.save(
                    NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(category.getId()));

            // when & then
            given().spec(givenWithPermission("notice-category:read"))
                    .when()
                    .get(BASE_URL + "/category-group/{categoryGroup}", "SHOES")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.targetCategoryGroup", equalTo("SHOES"))
                    .body("data.fields.size()", equalTo(2));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q2-3] 존재하지 않는 카테고리 그룹 → 404")
        void getNoticeCategoryByCategoryGroup_nonExisting_returns404() {
            // when & then: JEWELRY 그룹에 데이터 없음
            given().spec(givenWithPermission("notice-category:read"))
                    .when()
                    .get(BASE_URL + "/category-group/{categoryGroup}", "JEWELRY")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q2-4] 비인증 사용자 접근 → 401")
        void getNoticeCategoryByCategoryGroup_unauthenticated_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(BASE_URL + "/category-group/{categoryGroup}", "CLOTHING")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ===== 전체 플로우 =====

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F1] 목록 조회 → 카테고리 그룹 상세 조회 플로우")
        void fullFlow_listToCategoryGroupDetail() {
            // Step 1: 사전 데이터 저장
            noticeCategoryRepository.save(newCategory("NC_FLOW_1", "CLOTHING", true));
            noticeCategoryRepository.save(newCategory("NC_FLOW_2", "SHOES", true));

            // Step 2: 목록 조회 → 2건 확인
            given().spec(givenWithPermission("notice-category:read"))
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", greaterThanOrEqualTo(2));

            // Step 3: 카테고리 그룹별 상세 조회
            given().spec(givenWithPermission("notice-category:read"))
                    .when()
                    .get(BASE_URL + "/category-group/{categoryGroup}", "CLOTHING")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.targetCategoryGroup", equalTo("CLOTHING"))
                    .body("data.code", equalTo("NC_FLOW_1"));
        }
    }
}
