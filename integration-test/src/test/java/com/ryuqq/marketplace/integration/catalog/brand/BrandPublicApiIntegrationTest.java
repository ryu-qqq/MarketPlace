package com.ryuqq.marketplace.integration.catalog.brand;

import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandSimpleApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.integration.catalog.brand.fixture.BrandIntegrationTestFixture;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Brand Public API Integration Test
 *
 * <p>Public API는 ACTIVE 브랜드만 노출되어야 합니다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/sql/brand/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BrandPublicApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String adminUrl() {
        return "http://localhost:" + port + "/api/v1/admin/catalog/brands";
    }

    private String publicUrl() {
        return "http://localhost:" + port + "/api/v1/catalog/brands";
    }

    private Long activeBrandId;
    private Long inactiveBrandId;
    private Long blockedBrandId;

    @BeforeEach
    void setUp() {
        // ACTIVE 브랜드 생성
        var activeRequest = BrandIntegrationTestFixture.createBrandRequest("ACTIVE_BRAND", "Active Brand");
        ResponseEntity<ApiResponse<BrandApiResponse>> activeResponse = restTemplate.exchange(
                adminUrl(),
                HttpMethod.POST,
                new HttpEntity<>(activeRequest),
                new ParameterizedTypeReference<>() {}
        );
        activeBrandId = activeResponse.getBody().data().id();

        // INACTIVE 브랜드 생성 후 상태 변경
        var inactiveRequest = BrandIntegrationTestFixture.createBrandRequest("INACTIVE_BRAND", "Inactive Brand");
        ResponseEntity<ApiResponse<BrandApiResponse>> inactiveResponse = restTemplate.exchange(
                adminUrl(),
                HttpMethod.POST,
                new HttpEntity<>(inactiveRequest),
                new ParameterizedTypeReference<>() {}
        );
        inactiveBrandId = inactiveResponse.getBody().data().id();

        restTemplate.exchange(
                adminUrl() + "/" + inactiveBrandId + "/status",
                HttpMethod.PATCH,
                new HttpEntity<>(BrandIntegrationTestFixture.changeStatusRequest("INACTIVE")),
                new ParameterizedTypeReference<ApiResponse<BrandApiResponse>>() {}
        );

        // BLOCKED 브랜드 생성 후 상태 변경
        var blockedRequest = BrandIntegrationTestFixture.createBrandRequest("BLOCKED_BRAND", "Blocked Brand");
        ResponseEntity<ApiResponse<BrandApiResponse>> blockedResponse = restTemplate.exchange(
                adminUrl(),
                HttpMethod.POST,
                new HttpEntity<>(blockedRequest),
                new ParameterizedTypeReference<>() {}
        );
        blockedBrandId = blockedResponse.getBody().data().id();

        restTemplate.exchange(
                adminUrl() + "/" + blockedBrandId + "/status",
                HttpMethod.PATCH,
                new HttpEntity<>(BrandIntegrationTestFixture.changeStatusRequest("BLOCKED")),
                new ParameterizedTypeReference<ApiResponse<BrandApiResponse>>() {}
        );
    }

    @Test
    @DisplayName("Public API - ACTIVE 브랜드만 조회 가능")
    void publicApi_onlyActiveVisible() {
        // When: ACTIVE 브랜드 조회
        ResponseEntity<ApiResponse<BrandDetailApiResponse>> activeResponse = restTemplate.exchange(
                publicUrl() + "/" + activeBrandId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // Then: 성공
        assertThat(activeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(activeResponse.getBody().data().code()).isEqualTo("ACTIVE_BRAND");
    }

    @Test
    @DisplayName("Public API - INACTIVE 브랜드 조회 시 404")
    void publicApi_inactiveNotFound() {
        // When: INACTIVE 브랜드 조회
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                publicUrl() + "/" + inactiveBrandId,
                HttpMethod.GET,
                null,
                ProblemDetail.class
        );

        // Then: 404 NOT_FOUND
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Public API - BLOCKED 브랜드 조회 시 404")
    void publicApi_blockedNotFound() {
        // When: BLOCKED 브랜드 조회
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                publicUrl() + "/" + blockedBrandId,
                HttpMethod.GET,
                null,
                ProblemDetail.class
        );

        // Then: 404 NOT_FOUND
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Public API - 검색 결과에 ACTIVE만 포함")
    void publicApi_searchOnlyActive() {
        // When: 검색
        ResponseEntity<ApiResponse<List<BrandApiResponse>>> response = restTemplate.exchange(
                publicUrl() + "/search",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // Then: ACTIVE만 포함
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().data()).isNotEmpty();
        assertThat(response.getBody().data())
                .allMatch(brand -> "ACTIVE".equals(brand.status()));
    }

    @Test
    @DisplayName("Public API - simple-list에 ACTIVE만 포함")
    void publicApi_simpleListOnlyActive() {
        // When: 간단 목록 조회
        ResponseEntity<ApiResponse<List<BrandSimpleApiResponse>>> response = restTemplate.exchange(
                publicUrl() + "/simple-list",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // Then: ACTIVE만 포함 (INACTIVE, BLOCKED 제외)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().data()).isNotEmpty();
        // Simple response에는 status가 없으므로 ACTIVE 브랜드만 있는지 확인
        assertThat(response.getBody().data())
                .noneMatch(brand -> "INACTIVE_BRAND".equals(brand.code()));
        assertThat(response.getBody().data())
                .noneMatch(brand -> "BLOCKED_BRAND".equals(brand.code()));
    }

    @Test
    @DisplayName("Public API - 코드로 조회 (ACTIVE만)")
    void publicApi_getByCode_onlyActive() {
        // When: ACTIVE 브랜드 코드로 조회
        ResponseEntity<ApiResponse<BrandDetailApiResponse>> response = restTemplate.exchange(
                publicUrl() + "/by-code/ACTIVE_BRAND",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // Then: 성공
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().data().code()).isEqualTo("ACTIVE_BRAND");
    }

    @Test
    @DisplayName("Public API - INACTIVE 브랜드 코드로 조회 시 404")
    void publicApi_getByCode_inactiveNotFound() {
        // When: INACTIVE 브랜드 코드로 조회
        ResponseEntity<ProblemDetail> response = restTemplate.exchange(
                publicUrl() + "/by-code/INACTIVE_BRAND",
                HttpMethod.GET,
                null,
                ProblemDetail.class
        );

        // Then: 404 NOT_FOUND
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
