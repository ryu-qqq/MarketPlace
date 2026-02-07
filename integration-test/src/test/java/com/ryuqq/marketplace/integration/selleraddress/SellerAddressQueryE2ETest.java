package com.ryuqq.marketplace.integration.selleraddress;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.SellerAddressJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.entity.SellerAddressJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.repository.SellerAddressJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * SellerAddress Query API E2E 테스트.
 *
 * <p>GET /api/v1/market/seller-addresses 엔드포인트 테스트
 *
 * <p>GET /api/v1/market/seller-addresses/metadata 엔드포인트 테스트
 */
@DisplayName("SellerAddress Query API E2E 테스트")
class SellerAddressQueryE2ETest extends E2ETestBase {

    private static final String QUERY_PATH = "/seller-addresses";
    private static final String METADATA_PATH = "/seller-addresses/metadata";

    @Autowired private SellerJpaRepository sellerRepository;
    @Autowired private SellerAddressJpaRepository sellerAddressRepository;

    @BeforeEach
    void setUp() {
        sellerAddressRepository.deleteAll();
        sellerRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /admin/seller-addresses - 주소 목록 조회")
    class SearchAddressesTest {

        @Test
        @DisplayName("정상 조회 - 데이터 존재 시 목록 반환")
        void shouldReturnAddressesWhenDataExists() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            List<SellerAddressJpaEntity> addresses =
                    List.of(
                            SellerAddressJpaEntityFixtures.defaultShippingEntity(sellerId),
                            SellerAddressJpaEntityFixtures.nonDefaultShippingEntity(
                                    sellerId, "물류센터"),
                            SellerAddressJpaEntityFixtures.nonDefaultShippingEntity(
                                    sellerId, "강남센터"),
                            SellerAddressJpaEntityFixtures.defaultReturnEntity(sellerId),
                            SellerAddressJpaEntityFixtures.nonDefaultReturnEntity(
                                    sellerId, "반품센터2"));
            sellerAddressRepository.saveAll(addresses);

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sellerIds", sellerId)
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(greaterThanOrEqualTo(5)))
                    .body("data.totalElements", equalTo(5))
                    .body("data.content[0].sellerId", equalTo(sellerId.intValue()))
                    .body("data.content[0].addressType", notNullValue())
                    .body("data.content[0].addressName", notNullValue());
        }

        @Test
        @DisplayName("빈 결과 - 주소 데이터가 없을 때")
        void shouldReturnEmptyWhenNoAddresses() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sellerIds", sellerId)
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(0))
                    .body("data.totalElements", equalTo(0));
        }

        @Test
        @DisplayName("페이징 - page, size 동작 확인")
        void shouldReturnPagedResults() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            List<SellerAddressJpaEntity> addresses =
                    List.of(
                            SellerAddressJpaEntityFixtures.nonDefaultShippingEntity(
                                    sellerId, "주소1"),
                            SellerAddressJpaEntityFixtures.nonDefaultShippingEntity(
                                    sellerId, "주소2"),
                            SellerAddressJpaEntityFixtures.nonDefaultShippingEntity(
                                    sellerId, "주소3"),
                            SellerAddressJpaEntityFixtures.nonDefaultShippingEntity(
                                    sellerId, "주소4"),
                            SellerAddressJpaEntityFixtures.nonDefaultShippingEntity(
                                    sellerId, "주소5"),
                            SellerAddressJpaEntityFixtures.nonDefaultShippingEntity(
                                    sellerId, "주소6"),
                            SellerAddressJpaEntityFixtures.nonDefaultShippingEntity(
                                    sellerId, "주소7"));
            sellerAddressRepository.saveAll(addresses);

            // when & then - page 0
            given().spec(givenAdmin())
                    .queryParam("sellerIds", sellerId)
                    .queryParam("page", 0)
                    .queryParam("size", 3)
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(3))
                    .body("data.totalElements", equalTo(7))
                    .body("data.totalPages", equalTo(3));

            // when & then - page 1
            given().spec(givenAdmin())
                    .queryParam("sellerIds", sellerId)
                    .queryParam("page", 1)
                    .queryParam("size", 3)
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(3))
                    .body("data.totalElements", equalTo(7));
        }
    }

    @Nested
    @DisplayName("GET /admin/seller-addresses - 필터 조건 검색")
    class FilterSearchTest {

        @Test
        @DisplayName("addressType 조건 검색 (SHIPPING)")
        void shouldFilterByAddressType() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            List<SellerAddressJpaEntity> addresses =
                    List.of(
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "출고주소1", true),
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "출고주소2", false),
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "출고주소3", false),
                            SellerAddressJpaEntityFixtures.returnEntity(sellerId, "반품주소1", true),
                            SellerAddressJpaEntityFixtures.returnEntity(sellerId, "반품주소2", false));
            sellerAddressRepository.saveAll(addresses);

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sellerIds", sellerId)
                    .queryParam("addressTypes", "SHIPPING")
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(3))
                    .body("data.content[0].addressType", equalTo("SHIPPING"))
                    .body("data.content[1].addressType", equalTo("SHIPPING"))
                    .body("data.content[2].addressType", equalTo("SHIPPING"));
        }

        @Test
        @DisplayName("addressType 복수 조건 검색")
        void shouldFilterByMultipleAddressTypes() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            List<SellerAddressJpaEntity> addresses =
                    List.of(
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "출고주소1", true),
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "출고주소2", false),
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "출고주소3", false),
                            SellerAddressJpaEntityFixtures.returnEntity(sellerId, "반품주소1", true),
                            SellerAddressJpaEntityFixtures.returnEntity(sellerId, "반품주소2", false));
            sellerAddressRepository.saveAll(addresses);

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sellerIds", sellerId)
                    .queryParam("addressTypes", "SHIPPING,RETURN")
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(5));
        }

        @Test
        @DisplayName("defaultAddress 조건 검색")
        void shouldFilterByDefaultAddress() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            List<SellerAddressJpaEntity> addresses =
                    List.of(
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "출고기본", true),
                            SellerAddressJpaEntityFixtures.shippingEntity(
                                    sellerId, "출고비기본1", false),
                            SellerAddressJpaEntityFixtures.shippingEntity(
                                    sellerId, "출고비기본2", false),
                            SellerAddressJpaEntityFixtures.returnEntity(sellerId, "반품기본", true),
                            SellerAddressJpaEntityFixtures.returnEntity(sellerId, "반품비기본", false));
            sellerAddressRepository.saveAll(addresses);

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sellerIds", sellerId)
                    .queryParam("defaultAddress", true)
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.content[0].defaultAddress", equalTo(true))
                    .body("data.content[1].defaultAddress", equalTo(true));
        }

        @Test
        @DisplayName("검색어 조건 (주소명)")
        void shouldFilterBySearchWord() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            List<SellerAddressJpaEntity> addresses =
                    List.of(
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "본사 창고", true),
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "물류센터", false),
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "강남센터", false),
                            SellerAddressJpaEntityFixtures.returnEntity(sellerId, "반품센터", true),
                            SellerAddressJpaEntityFixtures.returnEntity(sellerId, "기타", false));
            sellerAddressRepository.saveAll(addresses);

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sellerIds", sellerId)
                    .queryParam("searchField", "addressName")
                    .queryParam("searchWord", "센터")
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(3));
        }

        @Test
        @DisplayName("복합 필터 - addressType + defaultAddress + 검색어")
        void shouldFilterByMultipleConditions() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            List<SellerAddressJpaEntity> addresses =
                    List.of(
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "본사 창고", true),
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "물류센터", false),
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "강남센터", false),
                            SellerAddressJpaEntityFixtures.returnEntity(sellerId, "반품센터", true),
                            SellerAddressJpaEntityFixtures.returnEntity(sellerId, "기타", false));
            sellerAddressRepository.saveAll(addresses);

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sellerIds", sellerId)
                    .queryParam("addressTypes", "SHIPPING")
                    .queryParam("defaultAddress", true)
                    .queryParam("searchWord", "창고")
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1))
                    .body("data.content[0].addressName", equalTo("본사 창고"))
                    .body("data.content[0].addressType", equalTo("SHIPPING"))
                    .body("data.content[0].defaultAddress", equalTo(true));
        }
    }

    @Nested
    @DisplayName("GET /admin/seller-addresses/metadata - 주소 메타데이터 조회")
    class MetadataTest {

        @Test
        @DisplayName("정상 조회 - 출고지/반품지 건수와 기본 주소 설정 여부 반환")
        void shouldReturnMetadataWithCorrectCounts() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            List<SellerAddressJpaEntity> addresses =
                    List.of(
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "출고기본", true),
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "출고2", false),
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "출고3", false),
                            SellerAddressJpaEntityFixtures.returnEntity(sellerId, "반품기본", true),
                            SellerAddressJpaEntityFixtures.returnEntity(sellerId, "반품2", false));
            sellerAddressRepository.saveAll(addresses);

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sellerId", sellerId)
                    .when()
                    .get(METADATA_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(5))
                    .body("data.shippingCount", equalTo(3))
                    .body("data.returnCount", equalTo(2))
                    .body("data.hasDefaultShipping", equalTo(true))
                    .body("data.hasDefaultReturn", equalTo(true));
        }

        @Test
        @DisplayName("기본 출고지 미설정 - hasDefaultShipping false")
        void shouldReturnFalseWhenNoDefaultShipping() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            List<SellerAddressJpaEntity> addresses =
                    List.of(
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "출고1", false),
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "출고2", false),
                            SellerAddressJpaEntityFixtures.returnEntity(sellerId, "반품기본", true));
            sellerAddressRepository.saveAll(addresses);

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sellerId", sellerId)
                    .when()
                    .get(METADATA_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(3))
                    .body("data.shippingCount", equalTo(2))
                    .body("data.returnCount", equalTo(1))
                    .body("data.hasDefaultShipping", equalTo(false))
                    .body("data.hasDefaultReturn", equalTo(true));
        }

        @Test
        @DisplayName("기본 반품지 미설정 - hasDefaultReturn false")
        void shouldReturnFalseWhenNoDefaultReturn() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            List<SellerAddressJpaEntity> addresses =
                    List.of(
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "출고기본", true),
                            SellerAddressJpaEntityFixtures.returnEntity(sellerId, "반품1", false));
            sellerAddressRepository.saveAll(addresses);

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sellerId", sellerId)
                    .when()
                    .get(METADATA_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(2))
                    .body("data.shippingCount", equalTo(1))
                    .body("data.returnCount", equalTo(1))
                    .body("data.hasDefaultShipping", equalTo(true))
                    .body("data.hasDefaultReturn", equalTo(false));
        }

        @Test
        @DisplayName("주소 없음 - 전부 0/false 반환")
        void shouldReturnZeroWhenNoAddresses() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sellerId", sellerId)
                    .when()
                    .get(METADATA_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(0))
                    .body("data.shippingCount", equalTo(0))
                    .body("data.returnCount", equalTo(0))
                    .body("data.hasDefaultShipping", equalTo(false))
                    .body("data.hasDefaultReturn", equalTo(false));
        }

        @Test
        @DisplayName("출고지만 있는 경우 - returnCount 0")
        void shouldReturnZeroReturnWhenOnlyShipping() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            List<SellerAddressJpaEntity> addresses =
                    List.of(
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "출고기본", true),
                            SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "출고2", false));
            sellerAddressRepository.saveAll(addresses);

            // when & then
            given().spec(givenAdmin())
                    .queryParam("sellerId", sellerId)
                    .when()
                    .get(METADATA_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(2))
                    .body("data.shippingCount", equalTo(2))
                    .body("data.returnCount", equalTo(0))
                    .body("data.hasDefaultShipping", equalTo(true))
                    .body("data.hasDefaultReturn", equalTo(false));
        }
    }

    @Nested
    @DisplayName("인증/인가 시나리오")
    class AuthorizationTest {

        @Test
        @DisplayName("토큰 없이 요청 - 401 Unauthorized")
        void shouldReturn401WhenUnauthenticated() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            // when & then
            given().spec(givenUnauthenticated())
                    .queryParam("sellerIds", sellerId)
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @DisplayName("다른 셀러의 주소 조회 시도 - 403 Forbidden")
        void shouldReturn403WhenAccessingOtherSeller() {
            // given
            SellerJpaEntity sellerA =
                    sellerRepository.save(
                            SellerJpaEntityFixtures.activeEntityWithOrganization("org-100"));
            Long sellerIdA = sellerA.getId();

            SellerJpaEntity sellerB =
                    sellerRepository.save(
                            SellerJpaEntityFixtures.activeEntityWithOrganization("org-200"));
            Long sellerIdB = sellerB.getId();

            // Seller A의 주소 생성
            sellerAddressRepository.save(
                    SellerAddressJpaEntityFixtures.defaultShippingEntity(sellerIdA));

            // when & then - Seller B 인증으로 Seller A 주소 조회 시도
            given().spec(givenSellerUser("org-200", "seller-address:read"))
                    .queryParam("sellerIds", sellerIdA)
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @DisplayName("본인 셀러 주소 조회 - 403 Forbidden (superAdmin 전용 엔드포인트)")
        void shouldReturn403WhenSellerUserAccessesSearchEndpoint() {
            // given
            SellerJpaEntity seller =
                    sellerRepository.save(
                            SellerJpaEntityFixtures.activeEntityWithOrganization("org-100"));
            Long sellerId = seller.getId();

            sellerAddressRepository.save(
                    SellerAddressJpaEntityFixtures.defaultShippingEntity(sellerId));

            // when & then - search endpoint는 @access.superAdmin() 전용
            given().spec(givenSellerUser("org-100", "seller-address:read"))
                    .queryParam("sellerIds", sellerId)
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @DisplayName("superAdmin 바이패스 - 다른 셀러 주소 조회 가능")
        void shouldReturn200WhenSuperAdminAccessesAnySeller() {
            // given
            SellerJpaEntity seller =
                    sellerRepository.save(
                            SellerJpaEntityFixtures.activeEntityWithOrganization("org-100"));
            Long sellerId = seller.getId();

            sellerAddressRepository.save(
                    SellerAddressJpaEntityFixtures.defaultShippingEntity(sellerId));

            // when & then
            given().spec(givenSuperAdmin())
                    .queryParam("sellerIds", sellerId)
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(greaterThanOrEqualTo(1)));
        }

        @Test
        @DisplayName("metadata 조회 - 권한 없는 사용자 403 Forbidden (본인 아닌 경우)")
        void shouldReturn403WhenNonOwnerWithoutPermissionAccessesMetadata() {
            // given
            SellerJpaEntity sellerA =
                    sellerRepository.save(
                            SellerJpaEntityFixtures.activeEntityWithOrganization("org-100"));
            Long sellerIdA = sellerA.getId();

            SellerJpaEntity sellerB =
                    sellerRepository.save(
                            SellerJpaEntityFixtures.activeEntityWithOrganization("org-200"));

            // when & then - 권한 없이 요청 (isSellerOwnerOr fallback에서 hasPermission도 false)
            given().spec(givenSellerUser("org-200"))
                    .queryParam("sellerId", sellerIdA)
                    .when()
                    .get(METADATA_PATH)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @DisplayName("metadata 조회 - 본인 셀러 200 OK")
        void shouldReturn200WhenOwnerAccessesMetadata() {
            // given
            SellerJpaEntity seller =
                    sellerRepository.save(
                            SellerJpaEntityFixtures.activeEntityWithOrganization("org-100"));
            Long sellerId = seller.getId();

            sellerAddressRepository.save(
                    SellerAddressJpaEntityFixtures.defaultShippingEntity(sellerId));

            // when & then
            given().spec(givenSellerUser("org-100", "seller-address:read"))
                    .queryParam("sellerId", sellerId)
                    .when()
                    .get(METADATA_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", greaterThanOrEqualTo(1));
        }

        @Test
        @DisplayName("metadata 조회 - superAdmin 전체 조회 가능")
        void shouldReturn200WhenSuperAdminAccessesMetadata() {
            // given
            SellerJpaEntity seller =
                    sellerRepository.save(
                            SellerJpaEntityFixtures.activeEntityWithOrganization("org-100"));
            Long sellerId = seller.getId();

            // when & then
            given().spec(givenSuperAdmin())
                    .queryParam("sellerId", sellerId)
                    .when()
                    .get(METADATA_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }
    }
}
