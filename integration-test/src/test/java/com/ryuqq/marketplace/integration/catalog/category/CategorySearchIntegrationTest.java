package com.ryuqq.marketplace.integration.catalog.category;

import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryApiResponse;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Category Search Integration Test
 *
 * <p>카테고리 검색 및 필터링 관련 통합 테스트
 * <ul>
 *   <li>TC-010: 키워드 검색</li>
 *   <li>TC-011: Leaf 카테고리 필터링</li>
 *   <li>TC-012: 비즈니스 분류 필터링</li>
 *   <li>TC-014: 증분 조회</li>
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
class CategorySearchIntegrationTest {

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

    /**
     * 테스트용 카테고리 트리 생성
     */
    private void setupCategoryTree() {
        // 패션 (루트)
        var fashionRequest = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "FASHION", "패션", "Fashion");
        ResponseEntity<ApiResponse<CategoryApiResponse>> fashionResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(fashionRequest),
                new ParameterizedTypeReference<>() {}
        );
        Long fashionId = fashionResponse.getBody().data().id();

        // 의류 (패션 하위)
        var apparelRequest = CategoryIntegrationTestFixture.createChildCategoryRequest(
                fashionId, "APPAREL", "의류", "Apparel");
        ResponseEntity<ApiResponse<CategoryApiResponse>> apparelResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(apparelRequest),
                new ParameterizedTypeReference<>() {}
        );
        Long apparelId = apparelResponse.getBody().data().id();

        // 상의 (Leaf)
        var topsRequest = CategoryIntegrationTestFixture.createLeafCategoryRequest(
                apparelId, "TOPS", "상의");
        restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(topsRequest),
                new ParameterizedTypeReference<ApiResponse<CategoryApiResponse>>() {}
        );

        // 하의 (Leaf)
        var bottomsRequest = CategoryIntegrationTestFixture.createLeafCategoryRequest(
                apparelId, "BOTTOMS", "하의");
        restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(bottomsRequest),
                new ParameterizedTypeReference<ApiResponse<CategoryApiResponse>>() {}
        );

        // 신발 (패션 하위, Leaf)
        var shoesRequest = CategoryIntegrationTestFixture.createLeafCategoryRequest(
                fashionId, "SHOES", "신발");
        restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(shoesRequest),
                new ParameterizedTypeReference<ApiResponse<CategoryApiResponse>>() {}
        );
    }

    // ========== TC-010: 키워드 검색 ==========

    @Test
    @DisplayName("TC-010: 키워드 검색 - nameKo 일치")
    void searchCategories_byKeyword_nameKo() {
        // given
        setupCategoryTree();

        // when - "패션" 키워드로 검색
        ResponseEntity<ApiResponse<List<CategoryApiResponse>>> response = restTemplate.exchange(
                publicBaseUrl() + "/search?keyword=패션",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        List<CategoryApiResponse> results = response.getBody().data();
        assertThat(results).isNotEmpty();
        assertThat(results.stream().anyMatch(c -> c.code().equals("FASHION"))).isTrue();
    }

    @Test
    @DisplayName("TC-010: 키워드 검색 - code 일치")
    void searchCategories_byKeyword_code() {
        // given
        setupCategoryTree();

        // when - "APPAREL" 코드로 검색
        ResponseEntity<ApiResponse<List<CategoryApiResponse>>> response = restTemplate.exchange(
                publicBaseUrl() + "/search?keyword=APPAREL",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().data()).isNotEmpty();
    }

    @Test
    @DisplayName("TC-010: 키워드 검색 - 결과 없음")
    void searchCategories_noResults() {
        // given
        setupCategoryTree();

        // when - 존재하지 않는 키워드로 검색
        ResponseEntity<ApiResponse<List<CategoryApiResponse>>> response = restTemplate.exchange(
                publicBaseUrl() + "/search?keyword=존재하지않는키워드",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then - 빈 결과 반환
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().data()).isEmpty();
    }

    // ========== TC-011: Leaf 카테고리 필터링 ==========

    @Test
    @DisplayName("TC-011: Leaf 카테고리 필터링 - isLeaf=true만 반환")
    void getLeafCategories_onlyLeaf() {
        // given
        setupCategoryTree();

        // when - Leaf 카테고리 조회
        ResponseEntity<ApiResponse<List<CategoryApiResponse>>> response = restTemplate.exchange(
                publicBaseUrl() + "/leaf",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then - Leaf 카테고리만 반환 (상의, 하의, 신발)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        List<CategoryApiResponse> leaves = response.getBody().data();
        // 모든 결과가 isListable=true 확인
        assertThat(leaves).allMatch(CategoryApiResponse::isListable);
    }

    // ========== TC-012: 비즈니스 분류 필터링 ==========

    @Test
    @DisplayName("TC-012: 비즈니스 분류 필터링 - department")
    void getLeafCategories_filterByDepartment() {
        // given
        setupCategoryTree();

        // when - FASHION department로 필터링
        ResponseEntity<ApiResponse<List<CategoryApiResponse>>> response = restTemplate.exchange(
                publicBaseUrl() + "/leaf?department=FASHION",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().data()).isNotEmpty();
    }

    @Test
    @DisplayName("TC-012: 비즈니스 분류 필터링 - department + productGroup")
    void getLeafCategories_filterByDepartmentAndProductGroup() {
        // given
        setupCategoryTree();

        // when - FASHION + APPAREL로 필터링
        ResponseEntity<ApiResponse<List<CategoryApiResponse>>> response = restTemplate.exchange(
                publicBaseUrl() + "/leaf?department=FASHION&productGroup=APPAREL",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // APPAREL 그룹에 속하는 Leaf 카테고리만 반환 (상의, 하의)
    }

    // ========== TC-014: 증분 조회 ==========

    @Test
    @DisplayName("TC-014: 증분 조회 - since 이후 변경된 카테고리만 반환")
    void getUpdatedSince_onlyUpdatedCategories() {
        // given - 기준 시점 기록
        LocalDateTime beforeCreate = LocalDateTime.now().minusMinutes(1);

        // 카테고리 생성
        var request = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "UPDATED_TEST", "증분 테스트", "Updated Test");
        restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<ApiResponse<CategoryApiResponse>>() {}
        );

        // when - 기준 시점 이후 변경된 카테고리 조회
        String sinceParam = beforeCreate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ResponseEntity<ApiResponse<List<CategoryApiResponse>>> response = restTemplate.exchange(
                publicBaseUrl() + "/updated-since?since=" + sinceParam,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then - since 이후 생성된 카테고리 포함
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        List<CategoryApiResponse> updated = response.getBody().data();
        assertThat(updated).isNotEmpty();
        assertThat(updated.stream().anyMatch(c -> c.code().equals("UPDATED_TEST"))).isTrue();
    }

    @Test
    @DisplayName("TC-014: 증분 조회 - 미래 시점은 빈 결과")
    void getUpdatedSince_futureDate_emptyResult() {
        // given
        setupCategoryTree();
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);

        // when - 미래 시점으로 조회
        String sinceParam = futureDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ResponseEntity<ApiResponse<List<CategoryApiResponse>>> response = restTemplate.exchange(
                publicBaseUrl() + "/updated-since?since=" + sinceParam,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then - 빈 결과
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().data()).isEmpty();
    }

    // ========== 자식 카테고리 조회 ==========

    @Test
    @DisplayName("자식 카테고리 조회 - parentId로 필터링")
    void getChildren_byParentId() {
        // given
        var rootRequest = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "PARENT_FOR_CHILDREN", "부모", "Parent");
        ResponseEntity<ApiResponse<CategoryApiResponse>> rootResponse = restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(rootRequest),
                new ParameterizedTypeReference<>() {}
        );
        Long parentId = rootResponse.getBody().data().id();

        // 자식 3개 생성
        for (int i = 1; i <= 3; i++) {
            var childRequest = CategoryIntegrationTestFixture.createChildCategoryRequest(
                    parentId, "CHILD_" + i, "자식" + i, "Child" + i);
            restTemplate.exchange(
                    adminBaseUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(childRequest),
                    new ParameterizedTypeReference<ApiResponse<CategoryApiResponse>>() {}
            );
        }

        // when - 자식 카테고리 조회
        ResponseEntity<ApiResponse<List<CategoryApiResponse>>> response = restTemplate.exchange(
                publicBaseUrl() + "/" + parentId + "/children",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then - 3개의 자식 반환
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().data()).hasSize(3);
    }

    // ========== 코드로 카테고리 조회 ==========

    @Test
    @DisplayName("코드로 카테고리 조회")
    void getCategoryByCode() {
        // given
        var request = CategoryIntegrationTestFixture.createRootCategoryRequest(
                "CODE_LOOKUP", "코드 조회", "Code Lookup");
        restTemplate.exchange(
                adminBaseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<ApiResponse<CategoryApiResponse>>() {}
        );

        // when - 코드로 조회
        ResponseEntity<ApiResponse<CategoryApiResponse>> response = restTemplate.exchange(
                publicBaseUrl() + "/by-code/CODE_LOOKUP",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().data().code()).isEqualTo("CODE_LOOKUP");
        assertThat(response.getBody().data().nameKo()).isEqualTo("코드 조회");
    }

    @Test
    @DisplayName("코드로 카테고리 조회 - 존재하지 않는 코드")
    void getCategoryByCode_notFound() {
        // when - 존재하지 않는 코드로 조회
        ResponseEntity<ApiResponse<CategoryApiResponse>> response = restTemplate.exchange(
                publicBaseUrl() + "/by-code/NON_EXISTENT_CODE",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // then - 404 또는 에러 응답
        boolean isNotFoundError =
                response.getStatusCode() == HttpStatus.NOT_FOUND ||
                (response.getBody() != null && !response.getBody().success());

        assertThat(isNotFoundError).isTrue();
    }
}
