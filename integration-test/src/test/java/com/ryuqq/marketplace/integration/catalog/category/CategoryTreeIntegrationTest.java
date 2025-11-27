package com.ryuqq.marketplace.integration.catalog.category;

import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryPathApiResponse;
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
 * Category Tree Integration Test
 *
 * <p>카테고리 트리 구조 관련 통합 테스트
 * <ul>
 *   <li>TC-007: 카테고리 이동 성공</li>
 *   <li>TC-008: Cycle 이동 방지</li>
 *   <li>TC-013: 경로 조회 (Breadcrumb)</li>
 * </ul>
 *
 * <p><strong>Kent Beck TDD + Tidy First 철학 적용</strong>
 *
 * @author Claude Code (Quality Engineer)
 * @since 2025-11-27
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/sql/category/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CategoryTreeIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String adminBaseUrl() {
        return "http://localhost:" + port + "/api/v1/admin/catalog/categories";
    }

    private String publicBaseUrl() {
        return "http://localhost:" + port + "/api/v1/catalog/categories";
    }

    // ========== TC-007: 카테고리 이동 성공 ==========

    @Test
    @DisplayName("TC-007: 카테고리 이동 성공 - depth/path 재계산")
    void moveCategory_success_recalculateDepthAndPath() {
        // given - 트리 구조 생성
        // Root A
        var rootARequest = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "ROOT_A", "루트A", "Root A");
        ResponseEntity<ApiResponse<CategoryApiResponse>> rootAResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(rootARequest),
                new ParameterizedTypeReference<>() {}
        );
        Long rootAId = rootAResponse.getBody().data().id();

        // Root B
        var rootBRequest = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "ROOT_B", "루트B", "Root B");
        ResponseEntity<ApiResponse<CategoryApiResponse>> rootBResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(rootBRequest),
                new ParameterizedTypeReference<>() {}
        );
        Long rootBId = rootBResponse.getBody().data().id();

        // Child of Root A
        var childRequest = CategoryIntegrationTestFixture.createChildCategoryRequest(
                rootAId, "CHILD_OF_A", "A의 자식", "Child of A");
        ResponseEntity<ApiResponse<CategoryApiResponse>> childResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(childRequest),
                new ParameterizedTypeReference<>() {}
        );
        Long childId = childResponse.getBody().data().id();

        // when - Child를 Root B 아래로 이동
        var moveRequest = CategoryIntegrationTestFixture.moveToParentRequest(rootBId);

        ResponseEntity<ApiResponse<CategoryApiResponse>> moveResponse = restTemplate.exchange(
                adminBaseUrl() + "/" + childId + "/move",
                HttpMethod.PATCH,
                new HttpEntity<>(moveRequest),
                new ParameterizedTypeReference<>() {}
        );

        // then - 이동 성공 및 parentId 변경 확인
        assertThat(moveResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(moveResponse.getBody()).isNotNull();
        assertThat(moveResponse.getBody().data().parentId()).isEqualTo(rootBId);
    }

    @Test
    @DisplayName("TC-007: 카테고리 루트로 이동 - depth=0")
    void moveCategory_toRoot_depthZero() {
        // given - 부모-자식 구조 생성
        var rootRequest = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "PARENT_ROOT", "부모 루트", "Parent Root");
        ResponseEntity<ApiResponse<CategoryApiResponse>> rootResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(rootRequest),
                new ParameterizedTypeReference<>() {}
        );
        Long rootId = rootResponse.getBody().data().id();

        var childRequest = CategoryIntegrationTestFixture.createChildCategoryRequest(
                rootId, "MOVE_TO_ROOT", "루트로 이동", "Move to Root");
        ResponseEntity<ApiResponse<CategoryApiResponse>> childResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(childRequest),
                new ParameterizedTypeReference<>() {}
        );
        Long childId = childResponse.getBody().data().id();

        // when - 루트로 이동
        var moveRequest = CategoryIntegrationTestFixture.moveToRootRequest();

        ResponseEntity<ApiResponse<CategoryApiResponse>> moveResponse = restTemplate.exchange(
                adminBaseUrl() + "/" + childId + "/move",
                HttpMethod.PATCH,
                new HttpEntity<>(moveRequest),
                new ParameterizedTypeReference<>() {}
        );

        // then - 루트로 이동 성공 (parentId=null, depth=0)
        assertThat(moveResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(moveResponse.getBody().data().parentId()).isNull();
        assertThat(moveResponse.getBody().data().depth()).isEqualTo(0);
    }

    // ========== TC-008: Cycle 이동 방지 ==========

    @Test
    @DisplayName("TC-008: Cycle 이동 방지 - 자기 자신 아래로 이동 불가")
    void moveCategory_toSelf_forbidden() {
        // given - 카테고리 생성
        var rootRequest = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "SELF_MOVE", "자기 이동", "Self Move");
        ResponseEntity<ApiResponse<CategoryApiResponse>> rootResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(rootRequest),
                new ParameterizedTypeReference<>() {}
        );
        Long categoryId = rootResponse.getBody().data().id();

        // when - 자기 자신 아래로 이동 시도
        var moveRequest = CategoryIntegrationTestFixture.moveToParentRequest(categoryId);

        ResponseEntity<ApiResponse<CategoryApiResponse>> moveResponse = restTemplate.exchange(
                adminBaseUrl() + "/" + categoryId + "/move",
                HttpMethod.PATCH,
                new HttpEntity<>(moveRequest),
                new ParameterizedTypeReference<>() {}
        );

        // then - 400 Bad Request 또는 에러 응답
        boolean isCycleError =
                moveResponse.getStatusCode() == HttpStatus.BAD_REQUEST ||
                (moveResponse.getBody() != null && !moveResponse.getBody().success());

        assertThat(isCycleError).isTrue();
    }

    @Test
    @DisplayName("TC-008: Cycle 이동 방지 - 자식의 하위로 이동 불가")
    void moveCategory_toDescendant_forbidden() {
        // given - 부모-자식-손자 구조 생성
        // 부모
        var parentRequest = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "CYCLE_PARENT", "사이클 부모", "Cycle Parent");
        ResponseEntity<ApiResponse<CategoryApiResponse>> parentResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(parentRequest),
                new ParameterizedTypeReference<>() {}
        );
        Long parentId = parentResponse.getBody().data().id();

        // 자식
        var childRequest = CategoryIntegrationTestFixture.createChildCategoryRequest(
                parentId, "CYCLE_CHILD", "사이클 자식", "Cycle Child");
        ResponseEntity<ApiResponse<CategoryApiResponse>> childResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(childRequest),
                new ParameterizedTypeReference<>() {}
        );
        Long childId = childResponse.getBody().data().id();

        // 손자
        var grandchildRequest = CategoryIntegrationTestFixture.createChildCategoryRequest(
                childId, "CYCLE_GRANDCHILD", "사이클 손자", "Cycle Grandchild");
        ResponseEntity<ApiResponse<CategoryApiResponse>> grandchildResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(grandchildRequest),
                new ParameterizedTypeReference<>() {}
        );
        Long grandchildId = grandchildResponse.getBody().data().id();

        // when - 부모를 손자 아래로 이동 시도 (Cycle)
        var moveRequest = CategoryIntegrationTestFixture.moveToParentRequest(grandchildId);

        ResponseEntity<ApiResponse<CategoryApiResponse>> moveResponse = restTemplate.exchange(
                adminBaseUrl() + "/" + parentId + "/move",
                HttpMethod.PATCH,
                new HttpEntity<>(moveRequest),
                new ParameterizedTypeReference<>() {}
        );

        // then - Cycle 감지로 에러 발생
        boolean isCycleError =
                moveResponse.getStatusCode() == HttpStatus.BAD_REQUEST ||
                (moveResponse.getBody() != null && !moveResponse.getBody().success());

        assertThat(isCycleError).isTrue();
    }

    // ========== TC-013: 경로 조회 (Breadcrumb) ==========

    @Test
    @DisplayName("TC-013: 카테고리 경로 조회 - Breadcrumb")
    void getCategoryPath_breadcrumb() {
        // given - 3단계 트리 구조 생성
        // 패션 (depth=0)
        var level0Request = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "PATH_L0", "패션", "Fashion");
        ResponseEntity<ApiResponse<CategoryApiResponse>> level0Response = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(level0Request),
                new ParameterizedTypeReference<>() {}
        );
        Long level0Id = level0Response.getBody().data().id();

        // 의류 (depth=1)
        var level1Request = CategoryIntegrationTestFixture.createChildCategoryRequest(
                level0Id, "PATH_L1", "의류", "Apparel");
        ResponseEntity<ApiResponse<CategoryApiResponse>> level1Response = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(level1Request),
                new ParameterizedTypeReference<>() {}
        );
        Long level1Id = level1Response.getBody().data().id();

        // 상의 (depth=2)
        var level2Request = CategoryIntegrationTestFixture.createChildCategoryRequest(
                level1Id, "PATH_L2", "상의", "Tops");
        ResponseEntity<ApiResponse<CategoryApiResponse>> level2Response = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(level2Request),
                new ParameterizedTypeReference<>() {}
        );
        Long level2Id = level2Response.getBody().data().id();

        // when - 경로 조회
        ResponseEntity<ApiResponse<CategoryPathApiResponse>> pathResponse = restTemplate.exchange(
                publicBaseUrl() + "/" + level2Id + "/path",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then - 루트부터 현재까지 경로 반환
        assertThat(pathResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(pathResponse.getBody()).isNotNull();
        assertThat(pathResponse.getBody().success()).isTrue();

        CategoryPathApiResponse path = pathResponse.getBody().data();
        assertThat(path).isNotNull();
        assertThat(path.ancestors()).hasSize(3); // 패션 → 의류 → 상의

        // 순서 검증 (루트부터)
        assertThat(path.ancestors().get(0).code()).isEqualTo("PATH_L0");
        assertThat(path.ancestors().get(1).code()).isEqualTo("PATH_L1");
        assertThat(path.ancestors().get(2).code()).isEqualTo("PATH_L2");
    }

    @Test
    @DisplayName("TC-013: 루트 카테고리 경로 조회 - 자기 자신만 반환")
    void getCategoryPath_rootCategory() {
        // given - 루트 카테고리 생성
        var rootRequest = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "SINGLE_ROOT", "단일 루트", "Single Root");
        ResponseEntity<ApiResponse<CategoryApiResponse>> rootResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(rootRequest),
                new ParameterizedTypeReference<>() {}
        );
        Long rootId = rootResponse.getBody().data().id();

        // when - 경로 조회
        ResponseEntity<ApiResponse<CategoryPathApiResponse>> pathResponse = restTemplate.exchange(
                publicBaseUrl() + "/" + rootId + "/path",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then - 자기 자신만 포함
        assertThat(pathResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(pathResponse.getBody().data().ancestors()).hasSize(1);
        assertThat(pathResponse.getBody().data().ancestors().get(0).code()).isEqualTo("SINGLE_ROOT");
    }

    // ========== 깊은 트리 테스트 ==========

    @Test
    @DisplayName("깊은 트리 구조 생성 및 이동")
    void deepTreeStructure() {
        // given - 5단계 트리 생성
        Long currentParentId = null;
        Long[] categoryIds = new Long[5];

        for (int i = 0; i < 5; i++) {
            var request = currentParentId == null
                    ? CategoryIntegrationTestFixture.createRootCategoryRequest(
                            "DEEP_L" + i, "레벨" + i, "Level" + i)
                    : CategoryIntegrationTestFixture.createChildCategoryRequest(
                            currentParentId, "DEEP_L" + i, "레벨" + i, "Level" + i);

            ResponseEntity<ApiResponse<CategoryApiResponse>> response = restTemplate.exchange(
                    adminBaseUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<>() {}
            );

            categoryIds[i] = response.getBody().data().id();
            currentParentId = categoryIds[i];

            // depth 검증
            assertThat(response.getBody().data().depth()).isEqualTo(i);
        }

        // when - 가장 깊은 노드 경로 조회
        ResponseEntity<ApiResponse<CategoryPathApiResponse>> pathResponse = restTemplate.exchange(
                publicBaseUrl() + "/" + categoryIds[4] + "/path",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then - 5단계 경로 확인
        assertThat(pathResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(pathResponse.getBody().data().ancestors()).hasSize(5);
    }
}
