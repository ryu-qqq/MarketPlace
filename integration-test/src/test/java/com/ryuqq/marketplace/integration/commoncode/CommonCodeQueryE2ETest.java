package com.ryuqq.marketplace.integration.commoncode;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.commoncode.CommonCodeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.entity.CommonCodeJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.repository.CommonCodeJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.CommonCodeTypeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.repository.CommonCodeTypeJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.time.Instant;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * CommonCode Query 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - GET /admin/common-codes - 공통 코드 목록 조회
 *
 * <p>우선순위: - P0: 6개 시나리오 (필수 기능) - P1: 4개 시나리오 (중요 기능) - P2: 2개 시나리오 (엣지 케이스)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("commoncode")
@Tag("query")
@DisplayName("CommonCode Query API E2E 테스트")
class CommonCodeQueryE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/common-codes";

    @Autowired private CommonCodeJpaRepository commonCodeRepository;
    @Autowired private CommonCodeTypeJpaRepository commonCodeTypeRepository;

    private Long defaultCommonCodeTypeId;

    @BeforeEach
    void setUp() {
        // 전체 데이터 초기화
        commonCodeRepository.deleteAll();
        commonCodeTypeRepository.deleteAll();

        // CommonCodeType 사전 데이터 생성 (대부분 시나리오 공통)
        var paymentMethodType =
                commonCodeTypeRepository.save(
                        CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                "PAYMENT_METHOD", "결제수단"));
        defaultCommonCodeTypeId = paymentMethodType.getId();
    }

    @AfterEach
    void tearDown() {
        commonCodeRepository.deleteAll();
        commonCodeTypeRepository.deleteAll();
    }

    // ===== GET /admin/common-codes - 공통 코드 목록 조회 =====

    // ===== 인증/인가 테스트 =====

    @Nested
    @DisplayName("인증/인가 테스트")
    class AuthorizationTest {

        @Test
        @Tag("P0")
        @Tag("auth")
        @DisplayName("[Q1-AUTH-1] 인증 없이 조회 시도 → 200 (public endpoint)")
        void searchCommonCodes_Unauthenticated_Returns200() {
            // given: CommonCodeType 1개 + CommonCode 3개
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "CREDIT_CARD", "신용카드"));
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "DEBIT_CARD", "체크카드"));
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "BANK_TRANSFER", "계좌이체"));

            // when & then: 공통 코드 조회는 public endpoint (permitAll)
            given().spec(givenUnauthenticated())
                    .queryParam("commonCodeTypeId", defaultCommonCodeTypeId)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(3))
                    .body("data.totalElements", equalTo(3));
        }

        @Test
        @Tag("P0")
        @Tag("auth")
        @DisplayName("[Q1-AUTH-2] 인증된 일반 사용자 조회 성공 → 200")
        void searchCommonCodes_AuthenticatedUser_Returns200() {
            // given: CommonCodeType 1개 + CommonCode 3개
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "CREDIT_CARD", "신용카드"));
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "DEBIT_CARD", "체크카드"));
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "BANK_TRANSFER", "계좌이체"));

            // when & then: 일반 사용자 토큰으로 요청 (조회는 인증만 필요)
            given().spec(givenAuthenticatedUser())
                    .queryParam("commonCodeTypeId", defaultCommonCodeTypeId)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(3))
                    .body("data.totalElements", equalTo(3));
        }
    }

    @Nested
    @DisplayName("GET /admin/common-codes - 공통 코드 목록 조회")
    class SearchCommonCodesTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1-1] 기본 조회 - 데이터 존재 시 정상 조회")
        void searchCommonCodes_WithData_Returns200() {
            // given: CommonCodeType 1개 + CommonCode 3개 (활성)
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "CREDIT_CARD", "신용카드"));
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "DEBIT_CARD", "체크카드"));
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "BANK_TRANSFER", "계좌이체"));

            // when & then
            given().spec(givenAdmin())
                    .queryParam("commonCodeTypeId", defaultCommonCodeTypeId)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(3))
                    .body("data.totalElements", equalTo(3))
                    .body("data.content[0].id", notNullValue())
                    .body("data.content[0].code", notNullValue())
                    .body("data.content[0].displayName", notNullValue())
                    .body("data.content[0].displayOrder", notNullValue())
                    .body("data.content[0].active", notNullValue())
                    .body("data.content[0].createdAt", notNullValue())
                    .body("data.content[0].updatedAt", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-2] 빈 결과 - 데이터 없을 때 빈 목록 반환")
        void searchCommonCodes_NoData_ReturnsEmptyList() {
            // given: CommonCodeType 1개 (코드 없음)

            // when & then
            given().spec(givenAdmin())
                    .queryParam("commonCodeTypeId", defaultCommonCodeTypeId)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(0))
                    .body("data.totalElements", equalTo(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-3] 필수 필드 누락 - commonCodeTypeId 없으면 400")
        void searchCommonCodes_MissingTypeId_Returns400() {
            // when & then: commonCodeTypeId 누락
            given().spec(givenAdmin())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-4] 존재하지 않는 타입 - 없는 commonCodeTypeId 조회 시 빈 목록")
        void searchCommonCodes_NonExistingTypeId_ReturnsEmptyList() {
            // given: 존재하지 않는 타입 ID
            Long nonExistingTypeId = 99999L;

            // when & then
            given().spec(givenAdmin())
                    .queryParam("commonCodeTypeId", nonExistingTypeId)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(0))
                    .body("data.totalElements", equalTo(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-5] 페이징 동작 확인")
        void searchCommonCodes_Paging_ReturnsCorrectPage() {
            // given: CommonCodeType 1개 + CommonCode 5개
            for (int i = 1; i <= 5; i++) {
                commonCodeRepository.save(
                        CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                                defaultCommonCodeTypeId, "CODE_" + i, "코드 " + i));
            }

            // when & then: page=0, size=2
            given().spec(givenAdmin())
                    .queryParam("commonCodeTypeId", defaultCommonCodeTypeId)
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.totalElements", equalTo(5))
                    .body("data.totalPages", equalTo(3));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-6] Soft Delete 필터링 - 삭제된 코드는 조회 안 됨")
        void searchCommonCodes_SoftDeleteFiltering_ExcludesDeleted() {
            // given: 활성 2개, 삭제 1개
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "CREDIT_CARD", "신용카드"));
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "DEBIT_CARD", "체크카드"));

            // 삭제된 코드 (deletedAt != null)
            Instant now = Instant.now();
            var deletedCode =
                    CommonCodeJpaEntity.create(
                            null,
                            defaultCommonCodeTypeId,
                            "DELETED_PAY",
                            "삭제된결제",
                            1,
                            false,
                            now,
                            now,
                            now); // deletedAt 설정
            commonCodeRepository.save(deletedCode);

            // when & then: 삭제된 코드는 조회되지 않음
            given().spec(givenAdmin())
                    .queryParam("commonCodeTypeId", defaultCommonCodeTypeId)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.totalElements", equalTo(2));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-7] 활성화 필터 - active=true 조건 검색")
        void searchCommonCodes_ActiveFilter_ReturnsActiveOnly() {
            // given: 활성 2개, 비활성 1개
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "CREDIT_CARD", "신용카드"));
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "DEBIT_CARD", "체크카드"));

            // 비활성 코드 생성
            Instant now = Instant.now();
            var inactiveCode =
                    CommonCodeJpaEntity.create(
                            null,
                            defaultCommonCodeTypeId,
                            "MOBILE_PAY",
                            "모바일페이",
                            1,
                            false, // 비활성
                            now,
                            now,
                            null);
            commonCodeRepository.save(inactiveCode);

            // when & then: active=true 필터
            given().spec(givenAdmin())
                    .queryParam("commonCodeTypeId", defaultCommonCodeTypeId)
                    .queryParam("active", true)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.content[0].active", equalTo(true))
                    .body("data.content[1].active", equalTo(true));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-8] 비활성화 필터 - active=false 조건 검색")
        void searchCommonCodes_InactiveFilter_ReturnsInactiveOnly() {
            // given: 활성 2개, 비활성 1개
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "CREDIT_CARD", "신용카드"));
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "DEBIT_CARD", "체크카드"));

            // 비활성 코드 생성
            Instant now = Instant.now();
            var inactiveCode =
                    CommonCodeJpaEntity.create(
                            null,
                            defaultCommonCodeTypeId,
                            "MOBILE_PAY",
                            "모바일페이",
                            1,
                            false, // 비활성
                            now,
                            now,
                            null);
            commonCodeRepository.save(inactiveCode);

            // when & then: active=false 필터
            given().spec(givenAdmin())
                    .queryParam("commonCodeTypeId", defaultCommonCodeTypeId)
                    .queryParam("active", false)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1))
                    .body("data.content[0].active", equalTo(false));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-9] 코드 검색 - code 부분 일치 검색")
        void searchCommonCodes_CodeFilter_ReturnsMatchingCodes() {
            // given: CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "CREDIT_CARD", "신용카드"));
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "DEBIT_CARD", "체크카드"));
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "BANK_TRANSFER", "계좌이체"));

            // when & then: code=CARD 검색
            given().spec(givenAdmin())
                    .queryParam("commonCodeTypeId", defaultCommonCodeTypeId)
                    .queryParam("code", "CARD")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body(
                            "data.content.code",
                            hasItems(containsString("CARD"), containsString("CARD")));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-10] 정렬 동작 - sortKey, sortDirection 확인")
        void searchCommonCodes_Sorting_ReturnsSortedResults() {
            // given: displayOrder: 3, 1, 2
            Instant now = Instant.now();
            commonCodeRepository.save(
                    CommonCodeJpaEntity.create(
                            null,
                            defaultCommonCodeTypeId,
                            "CODE_A",
                            "이름A",
                            3,
                            true,
                            now,
                            now,
                            null));
            commonCodeRepository.save(
                    CommonCodeJpaEntity.create(
                            null,
                            defaultCommonCodeTypeId,
                            "CODE_B",
                            "이름B",
                            1,
                            true,
                            now,
                            now,
                            null));
            commonCodeRepository.save(
                    CommonCodeJpaEntity.create(
                            null,
                            defaultCommonCodeTypeId,
                            "CODE_C",
                            "이름C",
                            2,
                            true,
                            now,
                            now,
                            null));

            // when & then: sortKey=DISPLAY_ORDER, sortDirection=ASC
            given().spec(givenAdmin())
                    .queryParam("commonCodeTypeId", defaultCommonCodeTypeId)
                    .queryParam("sortKey", "DISPLAY_ORDER")
                    .queryParam("sortDirection", "ASC")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(3))
                    .body("data.content[0].displayOrder", equalTo(1))
                    .body("data.content[1].displayOrder", equalTo(2))
                    .body("data.content[2].displayOrder", equalTo(3));
        }

        @Test
        @Tag("P2")
        @DisplayName("[Q1-11] 페이지 범위 초과")
        void searchCommonCodes_PageOutOfRange_ReturnsEmptyList() {
            // given: CommonCodeType 1개 + CommonCode 3개
            for (int i = 1; i <= 3; i++) {
                commonCodeRepository.save(
                        CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                                defaultCommonCodeTypeId, "CODE_" + i, "코드 " + i));
            }

            // when & then: page=10 (범위 초과)
            given().spec(givenAdmin())
                    .queryParam("commonCodeTypeId", defaultCommonCodeTypeId)
                    .queryParam("page", 10)
                    .queryParam("size", 20)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(0))
                    .body("data.totalElements", equalTo(3));
        }

        @Test
        @Tag("P2")
        @DisplayName("[Q1-12] 잘못된 size 값 - 101 이상")
        void searchCommonCodes_InvalidSize_Returns400() {
            // when & then: size=101 (최대값 초과)
            given().spec(givenAdmin())
                    .queryParam("commonCodeTypeId", defaultCommonCodeTypeId)
                    .queryParam("size", 101)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }
}
