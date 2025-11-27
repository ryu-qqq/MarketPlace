package com.ryuqq.marketplace.integration.catalog.brand;

import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.AliasMatchApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandAliasApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandApiResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Brand Alias Integration Test
 *
 * <p>별칭 추가 → 확정 → 삭제 플로우 및 resolve-by-alias 테스트
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/sql/brand/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BrandAliasIntegrationTest {

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

    private Long brandId;

    @BeforeEach
    void setUp() {
        // 브랜드 생성
        var createRequest = BrandIntegrationTestFixture.createBrandRequest("ALIAS_TEST", "Alias Test Brand");
        ResponseEntity<ApiResponse<BrandApiResponse>> response = restTemplate.exchange(
                adminUrl(),
                HttpMethod.POST,
                new HttpEntity<>(createRequest),
                new ParameterizedTypeReference<>() {}
        );
        brandId = response.getBody().data().id();
    }

    @Test
    @DisplayName("별칭 추가 → 확정 → 삭제 전체 플로우")
    void aliasLifecycle() {
        // 1. POST - 별칭 추가
        var addAliasRequest = BrandIntegrationTestFixture.addAliasRequest("나이키 테스트");

        ResponseEntity<ApiResponse<BrandAliasApiResponse>> addResponse = restTemplate.exchange(
                adminUrl() + "/" + brandId + "/aliases",
                HttpMethod.POST,
                new HttpEntity<>(addAliasRequest),
                new ParameterizedTypeReference<>() {}
        );

        assertThat(addResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long aliasId = addResponse.getBody().data().id();
        assertThat(aliasId).isNotNull();

        // 2. GET - 별칭 목록 확인
        ResponseEntity<ApiResponse<List<BrandAliasApiResponse>>> listResponse = restTemplate.exchange(
                adminUrl() + "/" + brandId + "/aliases",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResponse.getBody().data()).hasSize(1);

        // 3. PATCH - 별칭 확정
        ResponseEntity<ApiResponse<BrandAliasApiResponse>> confirmResponse = restTemplate.exchange(
                adminUrl() + "/" + brandId + "/aliases/" + aliasId + "/confirm",
                HttpMethod.PATCH,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(confirmResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(confirmResponse.getBody().data().status()).isEqualTo("CONFIRMED");

        // 4. DELETE - 별칭 삭제
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                adminUrl() + "/" + brandId + "/aliases/" + aliasId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("resolve-by-alias - 정규화 후 매칭")
    void resolveByAlias() {
        // Given: 다양한 형태의 별칭 추가
        String[] aliases = {"나이키", "NIKE", "N I K E", "나 이 키"};
        for (String alias : aliases) {
            var request = BrandIntegrationTestFixture.addAliasRequest(alias);
            restTemplate.exchange(
                    adminUrl() + "/" + brandId + "/aliases",
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<ApiResponse<BrandAliasApiResponse>>() {}
            );
        }

        // When: 별칭으로 조회 (정규화됨)
        ResponseEntity<ApiResponse<AliasMatchApiResponse>> response = restTemplate.exchange(
                publicUrl() + "/resolve-by-alias?aliasName=나이키",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // Then: 매칭 결과 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().data()).isNotNull();
        assertThat(response.getBody().data().candidates()).isNotEmpty();
        assertThat(response.getBody().data().candidates().get(0).brandId()).isEqualTo(brandId);
    }

    @Test
    @DisplayName("resolve-by-alias - 공백 제거 후 매칭")
    void resolveByAlias_withSpaces() {
        // Given: 별칭 추가
        var request = BrandIntegrationTestFixture.addAliasRequest("나이키");
        restTemplate.exchange(
                adminUrl() + "/" + brandId + "/aliases",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<ApiResponse<BrandAliasApiResponse>>() {}
        );

        // When: 공백이 포함된 별칭으로 조회
        ResponseEntity<ApiResponse<AliasMatchApiResponse>> response = restTemplate.exchange(
                publicUrl() + "/resolve-by-alias?aliasName=나 이 키",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // Then: 정규화되어 매칭됨
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // 정규화된 별칭이 일치하면 매칭됨
        assertThat(response.getBody().data().normalizedAlias()).isEqualTo("나이키");
    }

    @Test
    @DisplayName("별칭 전역 검색")
    void searchAliases() {
        // Given: 여러 브랜드에 별칭 추가
        var request1 = BrandIntegrationTestFixture.addAliasRequest("검색테스트별칭");
        restTemplate.exchange(
                adminUrl() + "/" + brandId + "/aliases",
                HttpMethod.POST,
                new HttpEntity<>(request1),
                new ParameterizedTypeReference<ApiResponse<BrandAliasApiResponse>>() {}
        );

        // When: 별칭 검색
        ResponseEntity<ApiResponse<List<BrandAliasApiResponse>>> response = restTemplate.exchange(
                adminUrl() + "/aliases/search?keyword=검색테스트",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        // Then: 검색 결과 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().data()).isNotEmpty();
    }
}
