package com.ryuqq.marketplace.integration.brand;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brand.repository.BrandJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.time.Instant;
import java.util.ArrayList;
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
 * Brand Query 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - GET /admin/brands - 브랜드 목록 조회 (Offset 기반 페이징)
 *
 * <p>시나리오: - P0: 8개 시나리오 (필수 기능) - P1: 6개 시나리오 (중요 기능) - 전체 플로우: 1개 시나리오
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("brand")
@Tag("query")
@DisplayName("Brand Query API E2E 테스트")
class BrandQueryE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/brands";

    @Autowired private BrandJpaRepository brandRepository;

    @BeforeEach
    void setUp() {
        brandRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        brandRepository.deleteAll();
    }

    // ===== GET /admin/brands - 브랜드 목록 조회 =====

    @Nested
    @DisplayName("GET /admin/brands - 브랜드 목록 조회")
    class SearchBrandsByOffsetTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1.1] 데이터 존재 시 정상 조회 (기본 페이징)")
        void searchBrands_withData_returnsDefaultPage() {
            // given: ACTIVE 5건 + INACTIVE 3건 + DELETED 2건
            saveBrands(5, "ACTIVE", "ACTIVE");
            saveBrands(3, "INACTIVE", "INACTIVE");
            saveBrands(2, "DELETED", "ACTIVE"); // 삭제된 브랜드

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(8)) // ACTIVE 5 + INACTIVE 3
                    .body("data.page", equalTo(0))
                    .body("data.size", equalTo(20)) // 기본값
                    .body("data.totalElements", equalTo(8));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1.2] 데이터 없을 때 빈 목록 반환")
        void searchBrands_noData_returnsEmptyList() {
            // when & then
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", empty())
                    .body("data.page", equalTo(0))
                    .body("data.size", equalTo(20))
                    .body("data.totalElements", equalTo(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1.3] 페이징 동작 확인 (page, size)")
        void searchBrands_paging_worksCorrectly() {
            // given: 25건 저장
            saveBrands(25, "BRAND", "ACTIVE");

            // when & then: page=0, size=10
            given().spec(givenAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(10))
                    .body("data.page", equalTo(0))
                    .body("data.size", equalTo(10))
                    .body("data.totalElements", equalTo(25));

            // when & then: page=1, size=10
            given().spec(givenAdmin())
                    .queryParam("page", 1)
                    .queryParam("size", 10)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(10))
                    .body("data.page", equalTo(1))
                    .body("data.totalElements", equalTo(25));

            // when & then: page=2, size=10 (마지막 페이지)
            given().spec(givenAdmin())
                    .queryParam("page", 2)
                    .queryParam("size", 10)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(5)) // 25 - 20
                    .body("data.page", equalTo(2))
                    .body("data.totalElements", equalTo(25));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1.4] 상태 필터 - ACTIVE만 조회")
        void searchBrands_statusFilter_ACTIVE() {
            // given: ACTIVE 7건 + INACTIVE 3건
            saveBrands(7, "ACTIVE", "ACTIVE");
            saveBrands(3, "INACTIVE", "INACTIVE");

            // when & then
            given().spec(givenAdmin())
                    .queryParam("statuses", "ACTIVE")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(7))
                    .body("data.totalElements", equalTo(7))
                    .body("data.content[0].status", equalTo("ACTIVE"))
                    .body("data.content[6].status", equalTo("ACTIVE"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1.5] 상태 필터 - INACTIVE만 조회")
        void searchBrands_statusFilter_INACTIVE() {
            // given: ACTIVE 7건 + INACTIVE 3건
            saveBrands(7, "ACTIVE", "ACTIVE");
            saveBrands(3, "INACTIVE", "INACTIVE");

            // when & then
            given().spec(givenAdmin())
                    .queryParam("statuses", "INACTIVE")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.totalElements", equalTo(3))
                    .body("data.content[0].status", equalTo("INACTIVE"))
                    .body("data.content[2].status", equalTo("INACTIVE"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1.6] 상태 필터 - 다중 상태 (ACTIVE, INACTIVE)")
        void searchBrands_statusFilter_multiple() {
            // given: ACTIVE 7건 + INACTIVE 3건
            saveBrands(7, "ACTIVE", "ACTIVE");
            saveBrands(3, "INACTIVE", "INACTIVE");

            // when & then
            given().spec(givenAdmin())
                    .queryParam("statuses", "ACTIVE")
                    .queryParam("statuses", "INACTIVE")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(10))
                    .body("data.totalElements", equalTo(10));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1.7] 검색 - searchWord만 (전체 필드 검색)")
        void searchBrands_searchWordOnly_allFields() {
            // given: Nike, Adidas, Puma
            brandRepository.save(createBrand("NIKE001", "나이키", "Nike", "NK"));
            brandRepository.save(createBrand("ADIDAS001", "아디다스", "Adidas", "AD"));
            brandRepository.save(createBrand("PUMA001", "퓨마", "Puma", "PM"));

            // when & then: 한글명 검색
            given().spec(givenAdmin())
                    .queryParam("searchWord", "나이키")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(1))
                    .body("data.content[0].nameKo", equalTo("나이키"))
                    .body("data.totalElements", equalTo(1));

            // when & then: 영문명 검색
            given().spec(givenAdmin())
                    .queryParam("searchWord", "Nike")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(1))
                    .body("data.content[0].nameEn", equalTo("Nike"));

            // when & then: 코드 검색
            given().spec(givenAdmin())
                    .queryParam("searchWord", "NIKE001")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(1))
                    .body("data.content[0].code", equalTo("NIKE001"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1.8] 검색 - searchField + searchWord (특정 필드 검색)")
        void searchBrands_searchFieldAndWord_specificField() {
            // given: Nike, NikeLab
            brandRepository.save(createBrand("NIKE001", "나이키", "Nike", "NK"));
            brandRepository.save(createBrand("NIKELAB", "나이키랩", "NikeLab", "NL"));

            // when & then: nameKo 필드만
            given().spec(givenAdmin())
                    .queryParam("searchField", "nameKo")
                    .queryParam("searchWord", "나이키")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2))
                    .body("data.totalElements", equalTo(2));

            // when & then: code 필드만
            given().spec(givenAdmin())
                    .queryParam("searchField", "code")
                    .queryParam("searchWord", "NIKE")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2));

            // when & then: nameEn 필드만
            given().spec(givenAdmin())
                    .queryParam("searchField", "nameEn")
                    .queryParam("searchWord", "Nike")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(2));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1.9] 정렬 - createdAt DESC (기본 정렬)")
        void searchBrands_sort_createdAtDesc() {
            // given: 시간차를 두고 3건 저장
            Instant time1 = Instant.parse("2025-01-01T00:00:00Z");
            Instant time2 = Instant.parse("2025-01-02T00:00:00Z");
            Instant time3 = Instant.parse("2025-01-03T00:00:00Z");

            brandRepository.save(createBrandWithTime("BRAND1", "브랜드1", "Brand1", "B1", time1));
            brandRepository.save(createBrandWithTime("BRAND2", "브랜드2", "Brand2", "B2", time2));
            brandRepository.save(createBrandWithTime("BRAND3", "브랜드3", "Brand3", "B3", time3));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sortKey", "createdAt")
                    .queryParam("sortDirection", "DESC")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].createdAt", containsString("2025-01-03")) // 최신
                    .body("data.content[1].createdAt", containsString("2025-01-02"))
                    .body("data.content[2].createdAt", containsString("2025-01-01")); // 가장 오래됨
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1.10] 정렬 - nameKo ASC (가나다순)")
        void searchBrands_sort_nameKoAsc() {
            // given
            brandRepository.save(createBrand("CODE1", "퓨마", "Puma", "PM"));
            brandRepository.save(createBrand("CODE2", "나이키", "Nike", "NK"));
            brandRepository.save(createBrand("CODE3", "아디다스", "Adidas", "AD"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sortKey", "nameKo")
                    .queryParam("sortDirection", "ASC")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].nameKo", equalTo("나이키"))
                    .body("data.content[1].nameKo", equalTo("아디다스"))
                    .body("data.content[2].nameKo", equalTo("퓨마"));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1.11] 정렬 - updatedAt DESC (최근 수정순)")
        void searchBrands_sort_updatedAtDesc() {
            // given: updatedAt 시간차를 두고 3건 저장
            Instant time1 = Instant.parse("2025-01-01T00:00:00Z");
            Instant time2 = Instant.parse("2025-01-03T00:00:00Z");
            Instant time3 = Instant.parse("2025-01-02T00:00:00Z");

            brandRepository.save(createBrandWithUpdatedAt("BRAND1", "브랜드1", "Brand1", "B1", time1));
            brandRepository.save(createBrandWithUpdatedAt("BRAND2", "브랜드2", "Brand2", "B2", time2));
            brandRepository.save(createBrandWithUpdatedAt("BRAND3", "브랜드3", "Brand3", "B3", time3));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sortKey", "updatedAt")
                    .queryParam("sortDirection", "DESC")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].updatedAt", containsString("2025-01-03"))
                    .body("data.content[1].updatedAt", containsString("2025-01-02"))
                    .body("data.content[2].updatedAt", containsString("2025-01-01"));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1.12] 복합 필터 - 상태 + 검색 + 정렬 + 페이징")
        void searchBrands_complexFilters() {
            // given: ACTIVE + "나이키" 포함 15건
            for (int i = 1; i <= 15; i++) {
                brandRepository.save(createBrand("NIKE" + i, "나이키" + i, "Nike" + i, "NK" + i));
            }
            // ACTIVE + "아디다스" 포함 10건
            for (int i = 1; i <= 10; i++) {
                brandRepository.save(createBrand("ADIDAS" + i, "아디다스" + i, "Adidas" + i, "AD" + i));
            }
            // INACTIVE + "나이키" 포함 5건
            for (int i = 1; i <= 5; i++) {
                brandRepository.save(
                        createBrand(
                                "NIKEINACTIVE" + i,
                                "나이키비활성" + i,
                                "NikeInactive" + i,
                                "NI" + i,
                                "INACTIVE"));
            }

            // when & then
            given().spec(givenAdmin())
                    .queryParam("statuses", "ACTIVE")
                    .queryParam("searchWord", "나이키")
                    .queryParam("sortKey", "nameKo")
                    .queryParam("sortDirection", "ASC")
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(10)) // 첫 페이지
                    .body("data.totalElements", equalTo(15))
                    .body("data.content[0].status", equalTo("ACTIVE"));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1.13] 엣지 케이스 - 대량 데이터 조회")
        void searchBrands_largeDataset() {
            // given: 1000건 저장
            List<BrandJpaEntity> brands = new ArrayList<>();
            for (int i = 1; i <= 1000; i++) {
                brands.add(createBrand("BRAND" + i, "브랜드" + i, "Brand" + i, "B" + i));
            }
            brandRepository.saveAll(brands);

            // when & then
            given().spec(givenAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 100)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(100))
                    .body("data.totalElements", equalTo(1000));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1.14] 엣지 케이스 - 마지막 페이지 조회")
        void searchBrands_lastPage() {
            // given: 23건 저장
            saveBrands(23, "BRAND", "ACTIVE");

            // when & then
            given().spec(givenAdmin())
                    .queryParam("page", 2)
                    .queryParam("size", 10)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3)) // 23 - 20
                    .body("data.page", equalTo(2))
                    .body("data.totalElements", equalTo(23));
        }
    }

    // ===== 전체 플로우 시나리오 =====

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F1] 목록 조회 → Response 검증 플로우")
        void fullFlow_listAndVerifyResponse() {
            // Step 1: 사전 데이터 저장
            var brand1 = brandRepository.save(createBrand("BRAND001", "브랜드1", "Brand1", "B1"));
            var brand2 = brandRepository.save(createBrand("BRAND002", "브랜드2", "Brand2", "B2"));
            var brand3 = brandRepository.save(createBrand("BRAND003", "브랜드3", "Brand3", "B3"));

            assertThat(brandRepository.count()).isEqualTo(3);

            // Step 2: 목록 조회
            var response =
                    given().spec(givenAdmin())
                            .when()
                            .get(BASE_URL)
                            .then()
                            .statusCode(HttpStatus.OK.value())
                            .body("data.content.size()", equalTo(3))
                            .extract()
                            .response();

            // Step 3: Response 구조 검증
            assertThat(response.jsonPath().getList("data.content")).hasSize(3);
            assertThat(response.jsonPath().getInt("data.page")).isEqualTo(0);
            assertThat(response.jsonPath().getInt("data.size")).isEqualTo(20);
            assertThat(response.jsonPath().getInt("data.totalElements")).isEqualTo(3);

            // Step 4: BrandApiResponse 필드 검증
            assertThat(response.jsonPath().getLong("data.content[0].id")).isNotNull();
            assertThat(response.jsonPath().getString("data.content[0].code")).isNotNull();
            assertThat(response.jsonPath().getString("data.content[0].nameKo")).isNotNull();
            assertThat(response.jsonPath().getString("data.content[0].nameEn")).isNotNull();
            assertThat(response.jsonPath().getString("data.content[0].status")).isNotNull();
            assertThat(response.jsonPath().getString("data.content[0].createdAt")).isNotNull();
            assertThat(response.jsonPath().getString("data.content[0].updatedAt")).isNotNull();

            // Step 5: 날짜 포맷 검증 (yyyy-MM-dd HH:mm:ss)
            String createdAt = response.jsonPath().getString("data.content[0].createdAt");
            assertThat(createdAt)
                    .matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");

            String updatedAt = response.jsonPath().getString("data.content[0].updatedAt");
            assertThat(updatedAt)
                    .matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");

            // Step 6: DB 일관성 검증
            Long responseId = response.jsonPath().getLong("data.content[0].id");
            var dbBrand = brandRepository.findById(responseId);
            assertThat(dbBrand).isPresent();
            assertThat(dbBrand.get().getCode())
                    .isEqualTo(response.jsonPath().getString("data.content[0].code"));
            assertThat(dbBrand.get().getNameKo())
                    .isEqualTo(response.jsonPath().getString("data.content[0].nameKo"));
        }
    }

    // ===== Helper Methods =====

    /**
     * 지정한 개수만큼 브랜드 Entity 생성 및 저장.
     *
     * @param count 생성 개수
     * @param codePrefix 코드 prefix
     * @param status 상태 (ACTIVE, INACTIVE, DELETED)
     */
    private void saveBrands(int count, String codePrefix, String status) {
        List<BrandJpaEntity> brands = new ArrayList<>();
        boolean isDeleted = "DELETED".equals(codePrefix);

        for (int i = 1; i <= count; i++) {
            String code = codePrefix + String.format("%03d", i);
            Instant now = Instant.now();
            Instant deletedAt = isDeleted ? now : null;

            brands.add(
                    BrandJpaEntity.create(
                            null,
                            code,
                            codePrefix + "브랜드" + i,
                            codePrefix + "Brand" + i,
                            codePrefix.substring(0, 2) + i,
                            status,
                            "https://example.com/logo.png",
                            now,
                            now,
                            deletedAt));
        }

        brandRepository.saveAll(brands);
    }

    /**
     * 기본 브랜드 Entity 생성 (ACTIVE 상태).
     *
     * @param code 브랜드 코드
     * @param nameKo 한글명
     * @param nameEn 영문명
     * @param shortName 약칭
     */
    private BrandJpaEntity createBrand(
            String code, String nameKo, String nameEn, String shortName) {
        return createBrand(code, nameKo, nameEn, shortName, "ACTIVE");
    }

    /**
     * 상태를 지정한 브랜드 Entity 생성.
     *
     * @param code 브랜드 코드
     * @param nameKo 한글명
     * @param nameEn 영문명
     * @param shortName 약칭
     * @param status 상태
     */
    private BrandJpaEntity createBrand(
            String code, String nameKo, String nameEn, String shortName, String status) {
        Instant now = Instant.now();
        return BrandJpaEntity.create(
                null,
                code,
                nameKo,
                nameEn,
                shortName,
                status,
                "https://example.com/logo.png",
                now,
                now,
                null);
    }

    /**
     * createdAt을 지정한 브랜드 Entity 생성.
     *
     * @param code 브랜드 코드
     * @param nameKo 한글명
     * @param nameEn 영문명
     * @param shortName 약칭
     * @param createdAt 생성일시
     */
    private BrandJpaEntity createBrandWithTime(
            String code, String nameKo, String nameEn, String shortName, Instant createdAt) {
        return BrandJpaEntity.create(
                null,
                code,
                nameKo,
                nameEn,
                shortName,
                "ACTIVE",
                "https://example.com/logo.png",
                createdAt,
                createdAt,
                null);
    }

    /**
     * updatedAt을 지정한 브랜드 Entity 생성.
     *
     * @param code 브랜드 코드
     * @param nameKo 한글명
     * @param nameEn 영문명
     * @param shortName 약칭
     * @param updatedAt 수정일시
     */
    private BrandJpaEntity createBrandWithUpdatedAt(
            String code, String nameKo, String nameEn, String shortName, Instant updatedAt) {
        Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");
        return BrandJpaEntity.create(
                null,
                code,
                nameKo,
                nameEn,
                shortName,
                "ACTIVE",
                "https://example.com/logo.png",
                createdAt,
                updatedAt,
                null);
    }
}
