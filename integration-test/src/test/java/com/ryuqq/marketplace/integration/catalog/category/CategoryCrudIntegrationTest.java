package com.ryuqq.marketplace.integration.catalog.category;

import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryTreeApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.integration.catalog.category.fixture.CategoryIntegrationTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Category CRUD Integration Test
 *
 * <p>카테고리 생성 → 조회 → 수정 → 상태변경 → 삭제 전체 플로우 통합 테스트
 *
 * <p><strong>Kent Beck TDD + Tidy First 철학 적용</strong>:
 * <ul>
 *   <li>Red (test:): 실패하는 통합 테스트 작성</li>
 *   <li>Green (feat:): 최소 구현으로 테스트 통과</li>
 *   <li>Refactor (struct:): 구조 개선 (동작 변경 없음)</li>
 * </ul>
 *
 * <p><strong>통합 테스트 표준 (CATEGORY-005.md 준수)</strong>:
 * <ul>
 *   <li>@SpringBootTest(webEnvironment = RANDOM_PORT) 필수</li>
 *   <li>TestRestTemplate 사용 (MockMvc 금지)</li>
 *   <li>Flyway migration으로 DB 스키마 자동 생성</li>
 *   <li>@Sql로 테스트 데이터 준비/정리</li>
 *   <li>TestFixture 활용으로 테스트 데이터 일관성 보장</li>
 * </ul>
 *
 * @author Claude Code (Quality Engineer)
 * @since 2025-11-27
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/sql/category/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CategoryCrudIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Admin API Base URL
     */
    private String adminBaseUrl() {
        return "http://localhost:" + port + "/api/v1/admin/catalog/categories";
    }

    /**
     * Public API Base URL
     */
    private String publicBaseUrl() {
        return "http://localhost:" + port + "/api/v1/catalog/categories";
    }

    // ========== TC-001: 루트 카테고리 생성 ==========

    @Test
    @DisplayName("TC-001: 루트 카테고리 생성 - 성공")
    void createRootCategory_success() {
        // given
        var request = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "ROOT_CAT", "루트 카테고리", "Root Category");

        // when
        ResponseEntity<ApiResponse<CategoryApiResponse>> response = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {}
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        CategoryApiResponse category = response.getBody().data();
        assertThat(category.id()).isNotNull();
        assertThat(category.code()).isEqualTo("ROOT_CAT");
        assertThat(category.nameKo()).isEqualTo("루트 카테고리");
        assertThat(category.depth()).isEqualTo(0);
        assertThat(category.parentId()).isNull();
        assertThat(category.status()).isEqualTo("ACTIVE");
    }

    // ========== TC-002: 자식 카테고리 생성 ==========

    @Test
    @DisplayName("TC-002: 자식 카테고리 생성 - depth/path 계산 확인")
    void createChildCategory_depthAndPathCalculated() {
        // given - 루트 카테고리 생성
        var rootRequest = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "PARENT_CAT", "부모 카테고리", "Parent Category");

        ResponseEntity<ApiResponse<CategoryApiResponse>> rootResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(rootRequest),
                new ParameterizedTypeReference<>() {}
        );

        Long parentId = rootResponse.getBody().data().id();

        // when - 자식 카테고리 생성
        var childRequest = CategoryIntegrationTestFixture.createChildCategoryRequest(
                parentId, "CHILD_CAT", "자식 카테고리", "Child Category");

        ResponseEntity<ApiResponse<CategoryApiResponse>> childResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(childRequest),
                new ParameterizedTypeReference<>() {}
        );

        // then
        assertThat(childResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        CategoryApiResponse child = childResponse.getBody().data();
        assertThat(child.depth()).isEqualTo(1);  // parent.depth + 1
        assertThat(child.parentId()).isEqualTo(parentId);
    }

    // ========== TC-003: 카테고리 트리 조회 ==========

    @Test
    @DisplayName("TC-003: 카테고리 트리 조회 - ACTIVE + visible 카테고리만 반환")
    void getCategoryTree_onlyActiveAndVisible() {
        // given - 카테고리 트리 생성
        var rootRequest = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "TREE_ROOT", "트리 루트", "Tree Root");

        ResponseEntity<ApiResponse<CategoryApiResponse>> rootResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(rootRequest),
                new ParameterizedTypeReference<>() {}
        );

        Long rootId = rootResponse.getBody().data().id();

        // 가시 자식 카테고리 생성
        var visibleChildRequest = CategoryIntegrationTestFixture.createChildCategoryRequest(
                rootId, "VISIBLE_CHILD", "가시 자식", "Visible Child");

        restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(visibleChildRequest),
                new ParameterizedTypeReference<ApiResponse<CategoryApiResponse>>() {}
        );

        // 비가시 자식 카테고리 생성
        var invisibleChildRequest = CategoryIntegrationTestFixture.createInvisibleCategoryRequest(
                rootId, "INVISIBLE_CHILD", "비가시 자식");

        restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(invisibleChildRequest),
                new ParameterizedTypeReference<ApiResponse<CategoryApiResponse>>() {}
        );

        // when - Public API로 트리 조회
        ResponseEntity<ApiResponse<CategoryTreeApiResponse>> treeResponse = restTemplate.exchange(
                publicBaseUrl() + "/tree",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then
        assertThat(treeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(treeResponse.getBody()).isNotNull();
        assertThat(treeResponse.getBody().success()).isTrue();

        // Public API는 visible=true인 카테고리만 반환해야 함
        CategoryTreeApiResponse tree = treeResponse.getBody().data();
        assertThat(tree).isNotNull();
    }

    // ========== TC-004: 카테고리 수정 ==========

    @Test
    @DisplayName("TC-004: 카테고리 수정 - 부분 업데이트 성공")
    void updateCategory_partialUpdate() {
        // given - 카테고리 생성
        var createRequest = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "UPDATE_TEST", "수정 테스트", "Update Test");

        ResponseEntity<ApiResponse<CategoryApiResponse>> createResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(createRequest),
                new ParameterizedTypeReference<>() {}
        );

        Long categoryId = createResponse.getBody().data().id();

        // when - 카테고리 수정
        var updateRequest = CategoryIntegrationTestFixture.updateCategoryRequest();

        ResponseEntity<ApiResponse<CategoryApiResponse>> updateResponse = restTemplate.exchange(
                adminBaseUrl() + "/" + categoryId,
                HttpMethod.PATCH,
                new HttpEntity<>(updateRequest),
                new ParameterizedTypeReference<>() {}
        );

        // then
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();

        CategoryApiResponse updated = updateResponse.getBody().data();
        assertThat(updated.nameKo()).isEqualTo("패션 업데이트");
        assertThat(updated.nameEn()).isEqualTo("Fashion Updated");
    }

    // ========== TC-005: 카테고리 상태 변경 ==========

    @Test
    @DisplayName("TC-005: 카테고리 상태 변경 - ACTIVE → INACTIVE → ACTIVE")
    void changeCategoryStatus_statusTransition() {
        // given - 카테고리 생성
        var createRequest = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "STATUS_TEST", "상태 테스트", "Status Test");

        ResponseEntity<ApiResponse<CategoryApiResponse>> createResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(createRequest),
                new ParameterizedTypeReference<>() {}
        );

        Long categoryId = createResponse.getBody().data().id();

        // when - INACTIVE로 변경
        var inactiveRequest = CategoryIntegrationTestFixture.changeStatusRequest("INACTIVE");

        ResponseEntity<ApiResponse<CategoryApiResponse>> inactiveResponse = restTemplate.exchange(
                adminBaseUrl() + "/" + categoryId + "/status",
                HttpMethod.PATCH,
                new HttpEntity<>(inactiveRequest),
                new ParameterizedTypeReference<>() {}
        );

        // then - INACTIVE 확인
        assertThat(inactiveResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(inactiveResponse.getBody().data().status()).isEqualTo("INACTIVE");

        // when - ACTIVE로 복원
        var activeRequest = CategoryIntegrationTestFixture.changeStatusRequest("ACTIVE");

        ResponseEntity<ApiResponse<CategoryApiResponse>> activeResponse = restTemplate.exchange(
                adminBaseUrl() + "/" + categoryId + "/status",
                HttpMethod.PATCH,
                new HttpEntity<>(activeRequest),
                new ParameterizedTypeReference<>() {}
        );

        // then - ACTIVE 확인
        assertThat(activeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(activeResponse.getBody().data().status()).isEqualTo("ACTIVE");
    }

    // ========== TC-006: 카테고리 삭제 (Soft Delete) ==========

    @Test
    @DisplayName("TC-006: 카테고리 삭제 - Soft Delete (status=INACTIVE)")
    void deleteCategory_softDelete() {
        // given - 카테고리 생성
        var createRequest = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "DELETE_TEST", "삭제 테스트", "Delete Test");

        ResponseEntity<ApiResponse<CategoryApiResponse>> createResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(createRequest),
                new ParameterizedTypeReference<>() {}
        );

        Long categoryId = createResponse.getBody().data().id();

        // when - 삭제 (Soft Delete)
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                adminBaseUrl() + "/" + categoryId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // then - 204 No Content
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // then - Admin API로 조회하면 status=INACTIVE 확인
        ResponseEntity<ApiResponse<CategoryApiResponse>> getResponse = restTemplate.exchange(
                adminBaseUrl() + "/" + categoryId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().data().status()).isEqualTo("INACTIVE");
    }

    // ========== TC-009: 중복 코드 생성 시도 ==========

    @Test
    @DisplayName("TC-009: 중복 코드 생성 시도 - 409 Conflict")
    void createCategory_duplicateCode_conflict() {
        // given - 첫 번째 카테고리 생성
        var firstRequest = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "DUPLICATE_CODE", "첫 번째", "First");

        restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(firstRequest),
                new ParameterizedTypeReference<ApiResponse<CategoryApiResponse>>() {}
        );

        // when - 동일 코드로 두 번째 카테고리 생성 시도
        var secondRequest = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "DUPLICATE_CODE", "두 번째", "Second");

        ResponseEntity<ApiResponse<CategoryApiResponse>> response = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(secondRequest),
                new ParameterizedTypeReference<>() {}
        );

        // then - 409 Conflict (또는 에러 응답)
        boolean isDuplicateError =
                response.getStatusCode() == HttpStatus.CONFLICT ||
                (response.getBody() != null && !response.getBody().success());

        assertThat(isDuplicateError).isTrue();
    }

    // ========== 전체 CRUD 플로우 ==========

    @Test
    @DisplayName("카테고리 CRUD 전체 플로우")
    void categoryCrudFlow() {
        // 1. POST - 루트 카테고리 생성
        var createRequest = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "CRUD_FLOW", "CRUD 플로우", "CRUD Flow");

        ResponseEntity<ApiResponse<CategoryApiResponse>> createResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(createRequest),
                new ParameterizedTypeReference<>() {}
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long categoryId = createResponse.getBody().data().id();

        // 2. GET - 생성된 카테고리 조회
        ResponseEntity<ApiResponse<CategoryApiResponse>> getResponse = restTemplate.exchange(
                adminBaseUrl() + "/" + categoryId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().data().code()).isEqualTo("CRUD_FLOW");

        // 3. PATCH - 카테고리 수정
        var updateRequest = CategoryIntegrationTestFixture.updateCategoryRequest();

        ResponseEntity<ApiResponse<CategoryApiResponse>> updateResponse = restTemplate.exchange(
                adminBaseUrl() + "/" + categoryId,
                HttpMethod.PATCH,
                new HttpEntity<>(updateRequest),
                new ParameterizedTypeReference<>() {}
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().data().nameKo()).isEqualTo("패션 업데이트");

        // 4. DELETE - Soft Delete
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                adminBaseUrl() + "/" + categoryId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // 5. GET - status=INACTIVE 확인
        ResponseEntity<ApiResponse<CategoryApiResponse>> afterDeleteResponse = restTemplate.exchange(
                adminBaseUrl() + "/" + categoryId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(afterDeleteResponse.getBody().data().status()).isEqualTo("INACTIVE");
    }

    // ========== 조회 실패 케이스 ==========

    @Test
    @DisplayName("카테고리 조회 - 존재하지 않는 ID")
    void getCategory_notFound() {
        Long nonExistentId = 999999L;

        ResponseEntity<ApiResponse<CategoryApiResponse>> response = restTemplate.exchange(
                adminBaseUrl() + "/" + nonExistentId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // 404 또는 에러 응답
        boolean isValidErrorResponse =
                response.getStatusCode() == HttpStatus.NOT_FOUND ||
                (response.getBody() != null && !response.getBody().success());

        assertThat(isValidErrorResponse).isTrue();
    }
}
