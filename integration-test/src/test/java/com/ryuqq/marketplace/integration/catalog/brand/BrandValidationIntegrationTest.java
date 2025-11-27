package com.ryuqq.marketplace.integration.catalog.brand;

import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandAliasApiResponse;
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
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Brand Validation Integration Test
 *
 * <p>브랜드 유효성 검증 관련 E2E 테스트
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/sql/brand/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BrandValidationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String brandUrl() {
        return "http://localhost:" + port + "/api/v1/admin/catalog/brands";
    }

    @Test
    @DisplayName("브랜드 코드 중복 시 409 CONFLICT 응답")
    void createBrand_duplicateCode_returns409() {
        // Given: 브랜드 생성
        var firstRequest = BrandIntegrationTestFixture.createBrandRequest("DUPLICATE_CODE", "First Brand");
        restTemplate.exchange(
                brandUrl(),
                HttpMethod.POST,
                new HttpEntity<>(firstRequest),
                new ParameterizedTypeReference<ApiResponse<BrandApiResponse>>() {}
        );

        // When: 동일 코드로 재생성 시도
        var secondRequest = BrandIntegrationTestFixture.createBrandRequest("DUPLICATE_CODE", "Second Brand");
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                brandUrl(),
                HttpMethod.POST,
                new HttpEntity<>(secondRequest),
                ProblemDetail.class
        );

        // Then: 409 CONFLICT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
    }

    @Test
    @DisplayName("표준 브랜드명 중복 시 409 CONFLICT 응답")
    void createBrand_duplicateCanonicalName_returns409() {
        // Given: 브랜드 생성
        var firstRequest = BrandIntegrationTestFixture.createBrandRequest("FIRST_CODE", "Duplicate Name");
        restTemplate.exchange(
                brandUrl(),
                HttpMethod.POST,
                new HttpEntity<>(firstRequest),
                new ParameterizedTypeReference<ApiResponse<BrandApiResponse>>() {}
        );

        // When: 동일 canonicalName으로 재생성 시도
        var secondRequest = BrandIntegrationTestFixture.createBrandRequest("SECOND_CODE", "Duplicate Name");
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                brandUrl(),
                HttpMethod.POST,
                new HttpEntity<>(secondRequest),
                ProblemDetail.class
        );

        // Then: 409 CONFLICT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
    }

    @Test
    @DisplayName("별칭 중복 (동일 scope) 시 409 CONFLICT 응답")
    void addAlias_duplicateScope_returns409() {
        // Given: 브랜드 생성
        var createRequest = BrandIntegrationTestFixture.createBrandRequest("ALIAS_TEST", "Alias Test Brand");
        ResponseEntity<ApiResponse<BrandApiResponse>> createResponse = restTemplate.exchange(
                brandUrl(),
                HttpMethod.POST,
                new HttpEntity<>(createRequest),
                new ParameterizedTypeReference<>() {}
        );
        Long brandId = createResponse.getBody().data().id();

        // 첫 번째 별칭 추가
        var firstAliasRequest = BrandIntegrationTestFixture.addAliasRequest("나이키");
        restTemplate.exchange(
                brandUrl() + "/" + brandId + "/aliases",
                HttpMethod.POST,
                new HttpEntity<>(firstAliasRequest),
                new ParameterizedTypeReference<ApiResponse<BrandAliasApiResponse>>() {}
        );

        // When: 동일 scope로 재추가 시도
        var secondAliasRequest = BrandIntegrationTestFixture.addAliasRequest("나이키");
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                brandUrl() + "/" + brandId + "/aliases",
                HttpMethod.POST,
                new HttpEntity<>(secondAliasRequest),
                ProblemDetail.class
        );

        // Then: 409 CONFLICT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
    }

    @Test
    @DisplayName("다른 scope의 동일 별칭은 추가 가능")
    void addAlias_differentScope_success() {
        // Given: 브랜드 생성
        var createRequest = BrandIntegrationTestFixture.createBrandRequest("SCOPE_TEST", "Scope Test Brand");
        ResponseEntity<ApiResponse<BrandApiResponse>> createResponse = restTemplate.exchange(
                brandUrl(),
                HttpMethod.POST,
                new HttpEntity<>(createRequest),
                new ParameterizedTypeReference<>() {}
        );
        Long brandId = createResponse.getBody().data().id();

        // 첫 번째 별칭 추가 (GLOBAL)
        var firstAliasRequest = BrandIntegrationTestFixture.addAliasRequestWithSource(
                "나이키", "SELLER", 100L, "GLOBAL");
        restTemplate.exchange(
                brandUrl() + "/" + brandId + "/aliases",
                HttpMethod.POST,
                new HttpEntity<>(firstAliasRequest),
                new ParameterizedTypeReference<ApiResponse<BrandAliasApiResponse>>() {}
        );

        // When: 다른 scope로 추가 (다른 sellerId)
        var secondAliasRequest = BrandIntegrationTestFixture.addAliasRequestWithSource(
                "나이키", "SELLER", 200L, "GLOBAL");
        ResponseEntity<ApiResponse<BrandAliasApiResponse>> response = restTemplate.exchange(
                brandUrl() + "/" + brandId + "/aliases",
                HttpMethod.POST,
                new HttpEntity<>(secondAliasRequest),
                new ParameterizedTypeReference<>() {}
        );

        // Then: 성공 (201 Created)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("존재하지 않는 브랜드 조회 시 404 NOT_FOUND 응답")
    void getBrand_notFound_returns404() {
        // When: 존재하지 않는 ID로 조회
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                brandUrl() + "/999999",
                HttpMethod.GET,
                null,
                ProblemDetail.class
        );

        // Then: 404 NOT_FOUND
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
    }

    @Test
    @DisplayName("잘못된 브랜드 코드 형식 시 400 BAD_REQUEST 응답")
    void createBrand_invalidCodeFormat_returns400() {
        // When: 잘못된 코드 형식으로 생성 시도 (소문자 시작)
        var request = BrandIntegrationTestFixture.createBrandRequest("invalid_code", "Invalid Brand");
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                brandUrl(),
                HttpMethod.POST,
                new HttpEntity<>(request),
                ProblemDetail.class
        );

        // Then: 400 BAD_REQUEST
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("필수 필드 누락 시 400 BAD_REQUEST 응답")
    void createBrand_missingRequiredField_returns400() {
        // When: code 없이 생성 시도
        var request = BrandIntegrationTestFixture.createBrandRequest(null, "Missing Code Brand");
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                brandUrl(),
                HttpMethod.POST,
                new HttpEntity<>(request),
                ProblemDetail.class
        );

        // Then: 400 BAD_REQUEST
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
