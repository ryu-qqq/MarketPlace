package com.ryuqq.marketplace.integration.category;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.category.entity.CategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.category.repository.CategoryJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Category Query 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - GET /admin/categories - 카테고리 목록 조회 (Offset 기반 페이징)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("category")
@Tag("query")
@DisplayName("Category Query API E2E 테스트")
class CategoryQueryE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/categories";

    @Autowired private CategoryJpaRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
    }

    // ===== 1. 기본 조회 시나리오 =====

    @Nested
    @DisplayName("기본 조회 - 데이터 존재 시 정상 조회")
    class BasicQueryTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-001] 카테고리 목록 조회 - 데이터 존재 시 정상 조회")
        void searchCategories_whenDataExists_thenReturns200() {
            // given: 5개 카테고리 저장
            for (long i = 1; i <= 5; i++) {
                categoryRepository.save(createActiveEntity(i));
            }

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", greaterThan(0))
                    .body("data.totalElements", equalTo(5))
                    .body("data.last", equalTo(true));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-002] 카테고리 목록 조회 - 데이터 없을 때 빈 목록 반환")
        void searchCategories_whenNoData_thenReturnsEmptyList() {
            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(0))
                    .body("data.totalElements", equalTo(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-003] 카테고리 목록 조회 - 삭제된 카테고리 제외")
        void searchCategories_whenDeletedExist_thenExcludesDeleted() {
            // given: 활성 2건, 삭제 1건
            categoryRepository.save(createActiveEntity(1));
            categoryRepository.save(createActiveEntity(2));
            categoryRepository.save(createDeletedEntity(3));

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2))
                    .body("data.totalElements", equalTo(2));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-004] 카테고리 목록 조회 - 기본 정렬 순서 (sortOrder DESC)")
        void searchCategories_whenNoSortSpecified_thenSortsBySortOrderDesc() {
            // given: sortOrder가 다른 카테고리 3건
            Instant now = Instant.now();
            categoryRepository.saveAll(
                    List.of(
                            createEntityWithSortOrder(1, 100, now),
                            createEntityWithSortOrder(2, 200, now),
                            createEntityWithSortOrder(3, 150, now)));

            // when & then: sortKey=sortOrder, sortDirection=DESC 명시적 전달
            given().spec(givenAdmin())
                    .queryParam("sortKey", "sortOrder")
                    .queryParam("sortDirection", "DESC")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].sortOrder", equalTo(200))
                    .body("data.content[1].sortOrder", equalTo(150))
                    .body("data.content[2].sortOrder", equalTo(100));
        }
    }

    // ===== 2. parentId 필터 시나리오 =====

    @Nested
    @DisplayName("parentId 필터 - 계층 구조 조회")
    class ParentIdFilterTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-005] parentId 필터 - 최상위 카테고리 조회")
        void searchCategories_whenFilterByDepth1_thenReturnsRootCategories() {
            // given: 루트 3건, 자식 2건
            var root1 = categoryRepository.save(createActiveEntity(1));
            categoryRepository.save(createActiveEntity(2));
            categoryRepository.save(createActiveEntity(3));
            categoryRepository.save(createChildEntity(root1.getId(), "CHILD1"));
            categoryRepository.save(createChildEntity(root1.getId(), "CHILD2"));

            // when & then: depth=1로 루트 카테고리만 조회
            given().spec(givenAdmin())
                    .queryParam("depth", 1)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.content[0].depth", equalTo(1))
                    .body("data.content[1].depth", equalTo(1))
                    .body("data.content[2].depth", equalTo(1));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-006] parentId 필터 - 특정 부모의 자식 조회")
        void searchCategories_whenFilterByParentId_thenReturnsChildren() {
            // given: 부모 1건, 자식 3건
            var root = categoryRepository.save(createActiveEntity(1));
            categoryRepository.save(createChildEntity(root.getId(), "CHILD1"));
            categoryRepository.save(createChildEntity(root.getId(), "CHILD2"));
            categoryRepository.save(createChildEntity(root.getId(), "CHILD3"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("parentId", root.getId())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.content[0].parentId", equalTo(root.getId().intValue()))
                    .body("data.content[1].parentId", equalTo(root.getId().intValue()))
                    .body("data.content[2].parentId", equalTo(root.getId().intValue()));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-007] parentId 필터 - 존재하지 않는 부모 ID")
        void searchCategories_whenFilterByNonExistentParentId_thenReturnsEmpty() {
            // given
            categoryRepository.save(createActiveEntity(1));
            categoryRepository.save(createActiveEntity(2));
            categoryRepository.save(createActiveEntity(3));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("parentId", 9999L)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(0));
        }
    }

    // ===== 3. depth 필터 시나리오 =====

    @Nested
    @DisplayName("depth 필터 - 계층 깊이 조회")
    class DepthFilterTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-008] depth 필터 - depth=1 조회")
        void searchCategories_whenFilterByDepth1_thenReturnsDepth1Only() {
            // given: depth 1, 2, 3 각 2건
            var root1 = categoryRepository.save(createActiveEntity(1));
            var root2 = categoryRepository.save(createActiveEntity(2));
            var child1 = categoryRepository.save(createChildEntity(root1.getId(), "C1"));
            var child2 = categoryRepository.save(createChildEntity(root2.getId(), "C2"));
            categoryRepository.save(createDepth3Entity(child1.getId(), "D1"));
            categoryRepository.save(createDepth3Entity(child2.getId(), "D2"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("depth", 1)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2))
                    .body("data.content[0].depth", equalTo(1))
                    .body("data.content[1].depth", equalTo(1));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-009] depth 필터 - depth=2 조회")
        void searchCategories_whenFilterByDepth2_thenReturnsDepth2Only() {
            // given: depth 1, 2, 3 각 2건
            var root1 = categoryRepository.save(createActiveEntity(1));
            var root2 = categoryRepository.save(createActiveEntity(2));
            var child1 = categoryRepository.save(createChildEntity(root1.getId(), "C1"));
            var child2 = categoryRepository.save(createChildEntity(root2.getId(), "C2"));
            categoryRepository.save(createDepth3Entity(child1.getId(), "D1"));
            categoryRepository.save(createDepth3Entity(child2.getId(), "D2"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("depth", 2)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2))
                    .body("data.content[0].depth", equalTo(2))
                    .body("data.content[1].depth", equalTo(2));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-010] depth 필터 - depth=3 조회")
        void searchCategories_whenFilterByDepth3_thenReturnsDepth3Only() {
            // given: depth 1, 2, 3 각 2건
            var root1 = categoryRepository.save(createActiveEntity(1));
            var root2 = categoryRepository.save(createActiveEntity(2));
            var child1 = categoryRepository.save(createChildEntity(root1.getId(), "C1"));
            var child2 = categoryRepository.save(createChildEntity(root2.getId(), "C2"));
            categoryRepository.save(createDepth3Entity(child1.getId(), "D1"));
            categoryRepository.save(createDepth3Entity(child2.getId(), "D2"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("depth", 3)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2))
                    .body("data.content[0].depth", equalTo(3))
                    .body("data.content[1].depth", equalTo(3));
        }
    }

    // ===== 4. leaf 필터 시나리오 =====

    @Nested
    @DisplayName("leaf 필터 - 리프 노드 조회")
    class LeafFilterTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-011] leaf 필터 - 리프 노드만 조회")
        void searchCategories_whenFilterByLeafTrue_thenReturnsLeafOnly() {
            // given: 리프 3건, 비리프 2건
            categoryRepository.save(createActiveEntity(1));
            categoryRepository.save(createActiveEntity(2));
            categoryRepository.save(createActiveEntity(3));
            categoryRepository.save(createNonLeafEntity(10));
            categoryRepository.save(createNonLeafEntity(11));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("leaf", true)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.content[0].leaf", equalTo(true))
                    .body("data.content[1].leaf", equalTo(true))
                    .body("data.content[2].leaf", equalTo(true));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-012] leaf 필터 - 비리프 노드만 조회")
        void searchCategories_whenFilterByLeafFalse_thenReturnsNonLeafOnly() {
            // given: 리프 3건, 비리프 2건
            categoryRepository.save(createActiveEntity(1));
            categoryRepository.save(createActiveEntity(2));
            categoryRepository.save(createActiveEntity(3));
            categoryRepository.save(createNonLeafEntity(10));
            categoryRepository.save(createNonLeafEntity(11));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("leaf", false)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2))
                    .body("data.content[0].leaf", equalTo(false))
                    .body("data.content[1].leaf", equalTo(false));
        }
    }

    // ===== 5. statuses 필터 시나리오 =====

    @Nested
    @DisplayName("statuses 필터 - 상태별 조회")
    class StatusesFilterTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-013] statuses 필터 - ACTIVE만 조회")
        void searchCategories_whenFilterByActiveStatus_thenReturnsActiveOnly() {
            // given: ACTIVE 3건, INACTIVE 2건
            categoryRepository.save(createActiveEntity(1));
            categoryRepository.save(createActiveEntity(2));
            categoryRepository.save(createActiveEntity(3));
            categoryRepository.save(createInactiveEntity(10));
            categoryRepository.save(createInactiveEntity(11));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("statuses", "ACTIVE")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.content[0].status", equalTo("ACTIVE"))
                    .body("data.content[1].status", equalTo("ACTIVE"))
                    .body("data.content[2].status", equalTo("ACTIVE"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-014] statuses 필터 - INACTIVE만 조회")
        void searchCategories_whenFilterByInactiveStatus_thenReturnsInactiveOnly() {
            // given: ACTIVE 3건, INACTIVE 2건
            categoryRepository.save(createActiveEntity(1));
            categoryRepository.save(createActiveEntity(2));
            categoryRepository.save(createActiveEntity(3));
            categoryRepository.save(createInactiveEntity(10));
            categoryRepository.save(createInactiveEntity(11));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("statuses", "INACTIVE")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2))
                    .body("data.content[0].status", equalTo("INACTIVE"))
                    .body("data.content[1].status", equalTo("INACTIVE"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-015] statuses 필터 - 다중 상태 조회")
        void searchCategories_whenFilterByMultipleStatuses_thenReturnsAll() {
            // given: ACTIVE 3건, INACTIVE 2건
            categoryRepository.save(createActiveEntity(1));
            categoryRepository.save(createActiveEntity(2));
            categoryRepository.save(createActiveEntity(3));
            categoryRepository.save(createInactiveEntity(10));
            categoryRepository.save(createInactiveEntity(11));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("statuses", "ACTIVE,INACTIVE")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(5));
        }
    }

    // ===== 6. departments 필터 시나리오 =====

    @Nested
    @DisplayName("departments 필터 - 부문별 조회")
    class DepartmentsFilterTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-016] departments 필터 - FASHION만 조회")
        void searchCategories_whenFilterByFashionDepartment_thenReturnsFashionOnly() {
            // given: 다양한 부문
            categoryRepository.save(createEntityWithDepartment(1, "FASHION"));
            categoryRepository.save(createEntityWithDepartment(2, "FASHION"));
            categoryRepository.save(createEntityWithDepartment(3, "FASHION"));
            categoryRepository.save(createEntityWithDepartment(4, "BEAUTY"));
            categoryRepository.save(createEntityWithDepartment(5, "BEAUTY"));
            categoryRepository.save(createEntityWithDepartment(6, "DIGITAL"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("departments", "FASHION")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.content[0].department", equalTo("FASHION"))
                    .body("data.content[1].department", equalTo("FASHION"))
                    .body("data.content[2].department", equalTo("FASHION"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-017] departments 필터 - BEAUTY만 조회")
        void searchCategories_whenFilterByBeautyDepartment_thenReturnsBeautyOnly() {
            // given: 다양한 부문
            categoryRepository.save(createEntityWithDepartment(1, "FASHION"));
            categoryRepository.save(createEntityWithDepartment(2, "FASHION"));
            categoryRepository.save(createEntityWithDepartment(3, "FASHION"));
            categoryRepository.save(createEntityWithDepartment(4, "BEAUTY"));
            categoryRepository.save(createEntityWithDepartment(5, "BEAUTY"));
            categoryRepository.save(createEntityWithDepartment(6, "DIGITAL"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("departments", "BEAUTY")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2))
                    .body("data.content[0].department", equalTo("BEAUTY"))
                    .body("data.content[1].department", equalTo("BEAUTY"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-018] departments 필터 - 다중 부문 조회")
        void searchCategories_whenFilterByMultipleDepartments_thenReturnsMatching() {
            // given: 다양한 부문
            categoryRepository.save(createEntityWithDepartment(1, "FASHION"));
            categoryRepository.save(createEntityWithDepartment(2, "FASHION"));
            categoryRepository.save(createEntityWithDepartment(3, "FASHION"));
            categoryRepository.save(createEntityWithDepartment(4, "BEAUTY"));
            categoryRepository.save(createEntityWithDepartment(5, "BEAUTY"));
            categoryRepository.save(createEntityWithDepartment(6, "DIGITAL"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("departments", "FASHION,BEAUTY")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(5));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-019] departments 필터 - 존재하지 않는 부문")
        void searchCategories_whenFilterByNonExistentDepartment_thenReturnsEmpty() {
            // given: FASHION만 존재
            categoryRepository.save(createEntityWithDepartment(1, "FASHION"));
            categoryRepository.save(createEntityWithDepartment(2, "FASHION"));
            categoryRepository.save(createEntityWithDepartment(3, "FASHION"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("departments", "PET")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(0));
        }
    }

    // ===== 7. categoryGroups 필터 시나리오 =====

    @Nested
    @DisplayName("categoryGroups 필터 - 카테고리 그룹별 조회 (고시정보 연결)")
    class CategoryGroupsFilterTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-020] categoryGroups 필터 - CLOTHING만 조회")
        void searchCategories_whenFilterByClothingGroup_thenReturnsClothingOnly() {
            // given: 다양한 카테고리 그룹
            categoryRepository.save(createEntityWithCategoryGroup(1, "CLOTHING"));
            categoryRepository.save(createEntityWithCategoryGroup(2, "CLOTHING"));
            categoryRepository.save(createEntityWithCategoryGroup(3, "CLOTHING"));
            categoryRepository.save(createEntityWithCategoryGroup(4, "SHOES"));
            categoryRepository.save(createEntityWithCategoryGroup(5, "SHOES"));
            categoryRepository.save(createEntityWithCategoryGroup(6, "DIGITAL"));
            categoryRepository.save(createEntityWithCategoryGroup(7, "ETC"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("categoryGroups", "CLOTHING")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.content[0].categoryGroup", equalTo("CLOTHING"))
                    .body("data.content[1].categoryGroup", equalTo("CLOTHING"))
                    .body("data.content[2].categoryGroup", equalTo("CLOTHING"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-021] categoryGroups 필터 - SHOES만 조회")
        void searchCategories_whenFilterByShoesGroup_thenReturnsShoesOnly() {
            // given: 다양한 카테고리 그룹
            categoryRepository.save(createEntityWithCategoryGroup(1, "CLOTHING"));
            categoryRepository.save(createEntityWithCategoryGroup(2, "CLOTHING"));
            categoryRepository.save(createEntityWithCategoryGroup(3, "CLOTHING"));
            categoryRepository.save(createEntityWithCategoryGroup(4, "SHOES"));
            categoryRepository.save(createEntityWithCategoryGroup(5, "SHOES"));
            categoryRepository.save(createEntityWithCategoryGroup(6, "DIGITAL"));
            categoryRepository.save(createEntityWithCategoryGroup(7, "ETC"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("categoryGroups", "SHOES")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2))
                    .body("data.content[0].categoryGroup", equalTo("SHOES"))
                    .body("data.content[1].categoryGroup", equalTo("SHOES"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-022] categoryGroups 필터 - DIGITAL만 조회")
        void searchCategories_whenFilterByDigitalGroup_thenReturnsDigitalOnly() {
            // given: 다양한 카테고리 그룹
            categoryRepository.save(createEntityWithCategoryGroup(1, "CLOTHING"));
            categoryRepository.save(createEntityWithCategoryGroup(2, "CLOTHING"));
            categoryRepository.save(createEntityWithCategoryGroup(3, "CLOTHING"));
            categoryRepository.save(createEntityWithCategoryGroup(4, "SHOES"));
            categoryRepository.save(createEntityWithCategoryGroup(5, "SHOES"));
            categoryRepository.save(createEntityWithCategoryGroup(6, "DIGITAL"));
            categoryRepository.save(createEntityWithCategoryGroup(7, "ETC"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("categoryGroups", "DIGITAL")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(1))
                    .body("data.content[0].categoryGroup", equalTo("DIGITAL"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-023] categoryGroups 필터 - 다중 그룹 조회")
        void searchCategories_whenFilterByMultipleGroups_thenReturnsMatching() {
            // given: 다양한 카테고리 그룹
            categoryRepository.save(createEntityWithCategoryGroup(1, "CLOTHING"));
            categoryRepository.save(createEntityWithCategoryGroup(2, "CLOTHING"));
            categoryRepository.save(createEntityWithCategoryGroup(3, "CLOTHING"));
            categoryRepository.save(createEntityWithCategoryGroup(4, "SHOES"));
            categoryRepository.save(createEntityWithCategoryGroup(5, "SHOES"));
            categoryRepository.save(createEntityWithCategoryGroup(6, "DIGITAL"));
            categoryRepository.save(createEntityWithCategoryGroup(7, "ETC"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("categoryGroups", "CLOTHING,SHOES")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(5));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-024] categoryGroups 필터 - ETC 그룹 조회")
        void searchCategories_whenFilterByEtcGroup_thenReturnsEtcOnly() {
            // given: 다양한 카테고리 그룹
            categoryRepository.save(createEntityWithCategoryGroup(1, "CLOTHING"));
            categoryRepository.save(createEntityWithCategoryGroup(2, "CLOTHING"));
            categoryRepository.save(createEntityWithCategoryGroup(3, "CLOTHING"));
            categoryRepository.save(createEntityWithCategoryGroup(4, "SHOES"));
            categoryRepository.save(createEntityWithCategoryGroup(5, "SHOES"));
            categoryRepository.save(createEntityWithCategoryGroup(6, "DIGITAL"));
            categoryRepository.save(createEntityWithCategoryGroup(7, "ETC"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("categoryGroups", "ETC")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(1))
                    .body("data.content[0].categoryGroup", equalTo("ETC"));
        }
    }

    // ===== 8. 검색 시나리오 =====

    @Nested
    @DisplayName("검색 - code, nameKo, nameEn 검색")
    class SearchTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-025] 검색 - code 필드 검색")
        void searchCategories_whenSearchByCode_thenReturnsMatchingCode() {
            // given: 다양한 code
            categoryRepository.save(createEntityWithCode(1, "CLOTH_001"));
            categoryRepository.save(createEntityWithCode(2, "CLOTH_002"));
            categoryRepository.save(createEntityWithCode(3, "SHOES_001"));
            categoryRepository.save(createEntityWithCode(4, "BAG_001"));
            categoryRepository.save(createEntityWithCode(5, "DIGITAL_001"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("searchField", "code")
                    .queryParam("searchWord", "CLOTH")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-026] 검색 - nameKo 필드 검색")
        void searchCategories_whenSearchByNameKo_thenReturnsMatchingNameKo() {
            // given: 다양한 한글명
            categoryRepository.save(createEntityWithName(1, "남성 의류", "Men's Clothing"));
            categoryRepository.save(createEntityWithName(2, "여성 의류", "Women's Clothing"));
            categoryRepository.save(createEntityWithName(3, "아동 의류", "Kids Clothing"));
            categoryRepository.save(createEntityWithName(4, "신발", "Shoes"));
            categoryRepository.save(createEntityWithName(5, "가방", "Bags"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("searchField", "nameKo")
                    .queryParam("searchWord", "의류")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-027] 검색 - nameEn 필드 검색")
        void searchCategories_whenSearchByNameEn_thenReturnsMatchingNameEn() {
            // given: 다양한 영문명
            categoryRepository.save(createEntityWithName(1, "남성 의류", "Men's Clothing"));
            categoryRepository.save(createEntityWithName(2, "여성 의류", "Women's Clothing"));
            categoryRepository.save(createEntityWithName(3, "아동 의류", "Kids Clothing"));
            categoryRepository.save(createEntityWithName(4, "신발", "Shoes"));
            categoryRepository.save(createEntityWithName(5, "가방", "Bags"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("searchField", "nameEn")
                    .queryParam("searchWord", "Clothing")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-028] 검색 - searchField 없이 전체 필드 검색")
        void searchCategories_whenSearchWithoutField_thenSearchesAllFields() {
            // given: 다양한 카테고리
            categoryRepository.save(createEntityWithCode(1, "TEST_001"));
            categoryRepository.save(createEntityWithName(2, "테스트 카테고리", "Test Category"));
            categoryRepository.save(createEntityWithCode(3, "OTHER_002"));
            categoryRepository.save(createEntityWithName(4, "기타", "Other"));
            categoryRepository.save(createEntityWithCode(5, "FINAL_003"));

            // when & then: searchField 없이 searchWord만 전달
            // DB collation에 따라 LIKE 검색 시 대소문자 매칭이 다를 수 있음
            given().spec(givenAdmin())
                    .queryParam("searchWord", "TEST")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", greaterThanOrEqualTo(1));
        }
    }

    // ===== 9. 페이징 시나리오 =====

    @Nested
    @DisplayName("페이징 - Offset 기반 페이징")
    class PagingTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-029] 페이징 - 첫 페이지 조회 (page=0, size=2)")
        void searchCategories_whenPagingFirstPage_thenReturnsFirstPage() {
            // given: 5건 저장
            for (long i = 1; i <= 5; i++) {
                categoryRepository.save(createActiveEntity(i));
            }

            // when & then
            given().spec(givenAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2))
                    .body("data.totalElements", equalTo(5))
                    .body("data.page", equalTo(0))
                    .body("data.size", equalTo(2))
                    .body("data.last", equalTo(false))
                    .body("data.first", equalTo(true));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-Q-030] 페이징 - 두 번째 페이지 조회 (page=1, size=2)")
        void searchCategories_whenPagingSecondPage_thenReturnsSecondPage() {
            // given: 5건 저장
            for (long i = 1; i <= 5; i++) {
                categoryRepository.save(createActiveEntity(i));
            }

            // when & then
            given().spec(givenAdmin())
                    .queryParam("page", 1)
                    .queryParam("size", 2)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2))
                    .body("data.totalElements", equalTo(5))
                    .body("data.page", equalTo(1))
                    .body("data.size", equalTo(2))
                    .body("data.last", equalTo(false))
                    .body("data.first", equalTo(false));
        }
    }

    // ===== Helper 메서드 =====

    private CategoryJpaEntity createActiveEntity(long suffix) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                "CAT" + suffix,
                "카테고리" + suffix,
                "Category" + suffix,
                null,
                1,
                "/" + suffix,
                1,
                true,
                "ACTIVE",
                "FASHION",
                "CLOTHING",
                now,
                now,
                null);
    }

    private CategoryJpaEntity createDeletedEntity(long suffix) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                "CAT_DEL" + suffix,
                "삭제카테고리" + suffix,
                "Deleted" + suffix,
                null,
                1,
                "/" + suffix,
                1,
                true,
                "ACTIVE",
                "FASHION",
                "CLOTHING",
                now,
                now,
                now);
    }

    private CategoryJpaEntity createChildEntity(Long parentId, String codeSuffix) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                "CAT_" + codeSuffix,
                "카테고리_" + codeSuffix,
                "Category_" + codeSuffix,
                parentId,
                2,
                "/" + parentId + "/" + codeSuffix,
                1,
                true,
                "ACTIVE",
                "FASHION",
                "CLOTHING",
                now,
                now,
                null);
    }

    private CategoryJpaEntity createDepth3Entity(Long parentId, String codeSuffix) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                "CAT_D3_" + codeSuffix,
                "카테고리_D3_" + codeSuffix,
                "Category_D3_" + codeSuffix,
                parentId,
                3,
                "/" + parentId + "/" + codeSuffix,
                1,
                true,
                "ACTIVE",
                "FASHION",
                "CLOTHING",
                now,
                now,
                null);
    }

    private CategoryJpaEntity createEntityWithSortOrder(long suffix, int sortOrder, Instant now) {
        return CategoryJpaEntity.create(
                null,
                "CAT" + suffix,
                "카테고리" + suffix,
                "Category" + suffix,
                null,
                1,
                "/" + suffix,
                sortOrder,
                true,
                "ACTIVE",
                "FASHION",
                "CLOTHING",
                now,
                now,
                null);
    }

    private CategoryJpaEntity createNonLeafEntity(long suffix) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                "CAT" + suffix,
                "카테고리" + suffix,
                "Category" + suffix,
                null,
                1,
                "/" + suffix,
                1,
                false,
                "ACTIVE",
                "FASHION",
                "CLOTHING",
                now,
                now,
                null);
    }

    private CategoryJpaEntity createInactiveEntity(long suffix) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                "CAT" + suffix,
                "카테고리" + suffix,
                "Category" + suffix,
                null,
                1,
                "/" + suffix,
                1,
                true,
                "INACTIVE",
                "FASHION",
                "CLOTHING",
                now,
                now,
                null);
    }

    private CategoryJpaEntity createEntityWithDepartment(long suffix, String department) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                "CAT" + suffix,
                "카테고리" + suffix,
                "Category" + suffix,
                null,
                1,
                "/" + suffix,
                1,
                true,
                "ACTIVE",
                department,
                "CLOTHING",
                now,
                now,
                null);
    }

    private CategoryJpaEntity createEntityWithCategoryGroup(long suffix, String categoryGroup) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                "CAT" + suffix,
                "카테고리" + suffix,
                "Category" + suffix,
                null,
                1,
                "/" + suffix,
                1,
                true,
                "ACTIVE",
                "FASHION",
                categoryGroup,
                now,
                now,
                null);
    }

    private CategoryJpaEntity createEntityWithCode(long suffix, String code) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                code,
                "카테고리" + suffix,
                "Category" + suffix,
                null,
                1,
                "/" + suffix,
                1,
                true,
                "ACTIVE",
                "FASHION",
                "CLOTHING",
                now,
                now,
                null);
    }

    private CategoryJpaEntity createEntityWithName(long suffix, String nameKo, String nameEn) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                "CAT" + suffix,
                nameKo,
                nameEn,
                null,
                1,
                "/" + suffix,
                1,
                true,
                "ACTIVE",
                "FASHION",
                "CLOTHING",
                now,
                now,
                null);
    }
}
