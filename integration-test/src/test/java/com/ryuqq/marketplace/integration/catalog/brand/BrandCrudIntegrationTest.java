package com.ryuqq.marketplace.integration.catalog.brand;

import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.integration.catalog.brand.fixture.BrandIntegrationTestFixture;
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
 * Brand CRUD Integration Test
 *
 * <p>브랜드 생성 → 조회 → 수정 → 삭제 전체 플로우 통합 테스트
 *
 * <p><strong>Kent Beck TDD + Tidy First 철학 적용</strong>:
 * <ul>
 *   <li>Red (test:): 실패하는 통합 테스트 작성</li>
 *   <li>Green (feat:): 최소 구현으로 테스트 통과</li>
 *   <li>Refactor (struct:): 구조 개선 (동작 변경 없음)</li>
 * </ul>
 *
 * <p><strong>통합 테스트 표준 (BRAND-005.md 준수)</strong>:
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
@Sql(scripts = "/sql/brand/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BrandCrudIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * 동적 포트 기반 Base URL 생성
     *
     * @return 브랜드 API Base URL
     */
    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/admin/catalog/brands";
    }

    @Test
    @DisplayName("브랜드 생성 → 조회 → 수정 → 삭제 전체 플로우")
    void brandCrudFlow() {
        // 1. POST - 브랜드 생성
        var createRequest = BrandIntegrationTestFixture.createBrandRequest("TEST_BRAND", "Test Brand");

        ResponseEntity<ApiResponse<BrandApiResponse>> createResponse = restTemplate.exchange(
                baseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(createRequest),
                new ParameterizedTypeReference<>() {}
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().success()).isTrue();

        Long brandId = createResponse.getBody().data().id();
        assertThat(brandId).isNotNull();

        // 2. GET - 생성된 브랜드 조회
        ResponseEntity<ApiResponse<BrandDetailApiResponse>> getResponse = restTemplate.exchange(
                baseUrl() + "/" + brandId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().data().code()).isEqualTo("TEST_BRAND");
        assertThat(getResponse.getBody().data().canonicalName()).isEqualTo("Test Brand");
        assertThat(getResponse.getBody().data().status()).isEqualTo("ACTIVE");

        // 3. PATCH - 브랜드 수정
        var updateRequest = BrandIntegrationTestFixture.updateBrandRequest();

        ResponseEntity<ApiResponse<BrandApiResponse>> updateResponse = restTemplate.exchange(
                baseUrl() + "/" + brandId,
                HttpMethod.PATCH,
                new HttpEntity<>(updateRequest),
                new ParameterizedTypeReference<>() {}
        );

        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        assertThat(updateResponse.getBody().data().nameKo()).isEqualTo("나이키 업데이트");

        // 4. GET - 수정 확인
        ResponseEntity<ApiResponse<BrandDetailApiResponse>> verifyResponse = restTemplate.exchange(
                baseUrl() + "/" + brandId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(verifyResponse.getBody()).isNotNull();
        assertThat(verifyResponse.getBody().data().nameKo()).isEqualTo("나이키 업데이트");
        assertThat(verifyResponse.getBody().data().nameEn()).isEqualTo("Nike Updated");

        // 5. DELETE - 브랜드 삭제 (Soft Delete)
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl() + "/" + brandId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // 6. GET - status=INACTIVE 확인
        ResponseEntity<ApiResponse<BrandDetailApiResponse>> afterDeleteResponse = restTemplate.exchange(
                baseUrl() + "/" + brandId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(afterDeleteResponse.getBody()).isNotNull();
        assertThat(afterDeleteResponse.getBody().data().status()).isEqualTo("INACTIVE");
    }

    @Test
    @DisplayName("브랜드 생성 - 성공")
    void createBrand_success() {
        var request = BrandIntegrationTestFixture.createBrandRequest("NEW_BRAND", "New Brand");

        ResponseEntity<ApiResponse<BrandApiResponse>> response = restTemplate.exchange(
                baseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
        assertThat(response.getBody().data().code()).isEqualTo("NEW_BRAND");
        assertThat(response.getBody().data().canonicalName()).isEqualTo("New Brand");
    }

    @Test
    @DisplayName("브랜드 상태 변경 - ACTIVE → BLOCKED → ACTIVE")
    void changeBrandStatus() {
        // 1. 브랜드 생성
        var createRequest = BrandIntegrationTestFixture.createBrandRequest("STATUS_TEST", "Status Test");

        ResponseEntity<ApiResponse<BrandApiResponse>> createResponse = restTemplate.exchange(
                baseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(createRequest),
                new ParameterizedTypeReference<>() {}
        );

        Long brandId = createResponse.getBody().data().id();

        // 2. BLOCKED로 변경
        var blockRequest = BrandIntegrationTestFixture.changeStatusRequest("BLOCKED");

        ResponseEntity<ApiResponse<BrandApiResponse>> blockResponse = restTemplate.exchange(
                baseUrl() + "/" + brandId + "/status",
                HttpMethod.PATCH,
                new HttpEntity<>(blockRequest),
                new ParameterizedTypeReference<>() {}
        );

        assertThat(blockResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(blockResponse.getBody()).isNotNull();
        assertThat(blockResponse.getBody().data().status()).isEqualTo("BLOCKED");

        // 3. ACTIVE로 복원
        var activateRequest = BrandIntegrationTestFixture.changeStatusRequest("ACTIVE");

        ResponseEntity<ApiResponse<BrandApiResponse>> activateResponse = restTemplate.exchange(
                baseUrl() + "/" + brandId + "/status",
                HttpMethod.PATCH,
                new HttpEntity<>(activateRequest),
                new ParameterizedTypeReference<>() {}
        );

        assertThat(activateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(activateResponse.getBody()).isNotNull();
        assertThat(activateResponse.getBody().data().status()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("럭셔리 브랜드 생성 - isLuxury=true")
    void createLuxuryBrand() {
        var request = BrandIntegrationTestFixture.createLuxuryBrandRequest();

        ResponseEntity<ApiResponse<BrandApiResponse>> response = restTemplate.exchange(
                baseUrl(),
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().data().code()).isEqualTo("GUCCI");
        assertThat(response.getBody().data().isLuxury()).isTrue();
    }

    @Test
    @DisplayName("브랜드 조회 - 존재하지 않는 ID")
    void getBrand_notFound() {
        Long nonExistentId = 999999L;

        ResponseEntity<ApiResponse<BrandDetailApiResponse>> response = restTemplate.exchange(
                baseUrl() + "/" + nonExistentId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // 404 또는 200+error 둘 다 허용 (에러 처리 전략에 따라)
        boolean isValidErrorResponse =
                response.getStatusCode() == HttpStatus.NOT_FOUND ||
                (response.getStatusCode() == HttpStatus.OK &&
                 response.getBody() != null &&
                 !response.getBody().success());

        assertThat(isValidErrorResponse).isTrue();
    }
}
