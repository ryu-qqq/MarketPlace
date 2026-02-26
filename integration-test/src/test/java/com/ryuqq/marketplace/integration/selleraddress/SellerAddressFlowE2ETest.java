package com.ryuqq.marketplace.integration.selleraddress;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.repository.SellerAddressJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * SellerAddress 통합 플로우 E2E 테스트.
 *
 * <p>여러 API를 조합한 전체 시나리오 테스트
 */
@DisplayName("SellerAddress 통합 플로우 E2E 테스트")
class SellerAddressFlowE2ETest extends E2ETestBase {

    private static final String COMMAND_PATH = "/seller-addresses/sellers/{sellerId}";
    private static final String QUERY_PATH = "/seller-addresses";

    @Autowired private SellerJpaRepository sellerRepository;
    @Autowired private SellerAddressJpaRepository sellerAddressRepository;

    @BeforeEach
    void setUp() {
        sellerAddressRepository.deleteAll();
        sellerRepository.deleteAll();
    }

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowTest {

        @Test
        @DisplayName("CRUD 전체 플로우")
        void shouldCompleteCrudFlow() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            // Step 1 - 등록
            Map<String, Object> registerRequest =
                    Map.of(
                            "addressType",
                            "SHIPPING",
                            "addressName",
                            "본사 창고",
                            "address",
                            Map.of(
                                    "zipCode", "06164",
                                    "line1", "서울 강남구 역삼로 123",
                                    "line2", "5층"),
                            "defaultAddress",
                            false);

            Response registerResponse =
                    given().spec(givenAdminJson())
                            .body(registerRequest)
                            .when()
                            .post(COMMAND_PATH, sellerId);

            registerResponse.then().statusCode(HttpStatus.CREATED.value());
            Long addressId = registerResponse.jsonPath().getLong("data.addressId");

            // Step 2 - 조회
            given().spec(givenAdmin())
                    .queryParam("sellerIds", sellerId)
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1))
                    .body("data.content[0].id", equalTo(addressId.intValue()))
                    .body("data.content[0].addressName", equalTo("본사 창고"));

            // Step 3 - 수정
            Map<String, Object> updateRequest =
                    Map.of(
                            "addressName",
                            "물류센터",
                            "address",
                            Map.of(
                                    "zipCode", "06165",
                                    "line1", "서울 강남구 역삼로 456",
                                    "line2", "6층"));

            given().spec(givenAdminJson())
                    .body(updateRequest)
                    .when()
                    .put(COMMAND_PATH + "/{addressId}", sellerId, addressId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // Step 4 - 수정 확인
            given().spec(givenAdmin())
                    .queryParam("sellerIds", sellerId)
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].addressName", equalTo("물류센터"))
                    .body("data.content[0].address.zipCode", equalTo("06165"));

            // Step 5 - 삭제
            given().spec(givenAdmin())
                    .when()
                    .patch(COMMAND_PATH + "/{addressId}/status", sellerId, addressId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // Step 6 - 삭제 확인
            given().spec(givenAdmin())
                    .queryParam("sellerIds", sellerId)
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(0));
        }

        @Test
        @DisplayName("기본 주소 전환 플로우")
        void shouldToggleDefaultAddress() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            // Step 1 - 첫 번째 주소 등록 (기본 주소)
            Map<String, Object> request1 =
                    Map.of(
                            "addressType",
                            "SHIPPING",
                            "addressName",
                            "본사 창고",
                            "address",
                            Map.of(
                                    "zipCode", "06164",
                                    "line1", "서울 강남구 역삼로 123",
                                    "line2", "5층"),
                            "defaultAddress",
                            true);

            Response response1 =
                    given().spec(givenAdminJson())
                            .body(request1)
                            .when()
                            .post(COMMAND_PATH, sellerId);

            Long address1Id = response1.jsonPath().getLong("data.addressId");

            // Step 2 - 두 번째 주소 등록 (비기본 주소)
            Map<String, Object> request2 =
                    Map.of(
                            "addressType",
                            "SHIPPING",
                            "addressName",
                            "물류센터",
                            "address",
                            Map.of(
                                    "zipCode", "06165",
                                    "line1", "서울 강남구 역삼로 456",
                                    "line2", "6층"),
                            "defaultAddress",
                            false);

            Response response2 =
                    given().spec(givenAdminJson())
                            .body(request2)
                            .when()
                            .post(COMMAND_PATH, sellerId);

            Long address2Id = response2.jsonPath().getLong("data.addressId");

            // Step 3 - 기본 주소 확인
            given().spec(givenAdmin())
                    .queryParam("sellerIds", sellerId)
                    .queryParam("defaultAddress", true)
                    .queryParam("addressTypes", "SHIPPING")
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1))
                    .body("data.content[0].id", equalTo(address1Id.intValue()))
                    .body("data.content[0].defaultAddress", equalTo(true));

            // Step 4 - 두 번째 주소를 기본 주소로 변경
            Map<String, Object> updateRequest =
                    Map.of(
                            "addressName",
                            "물류센터",
                            "address",
                            Map.of(
                                    "zipCode", "06165",
                                    "line1", "서울 강남구 역삼로 456",
                                    "line2", "6층"),
                            "defaultAddress",
                            true);

            given().spec(givenAdminJson())
                    .body(updateRequest)
                    .when()
                    .put(COMMAND_PATH + "/{addressId}", sellerId, address2Id)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // Step 5 - 기본 주소 전환 확인
            given().spec(givenAdmin())
                    .queryParam("sellerIds", sellerId)
                    .queryParam("defaultAddress", true)
                    .queryParam("addressTypes", "SHIPPING")
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1))
                    .body("data.content[0].id", equalTo(address2Id.intValue()))
                    .body("data.content[0].defaultAddress", equalTo(true));

            // Step 6 - 전체 주소 조회로 첫 번째 주소 비기본 확인
            given().spec(givenAdmin())
                    .queryParam("sellerIds", sellerId)
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2));

            var address1 = sellerAddressRepository.findById(address1Id).orElseThrow();
            assertThat(address1.isDefaultAddress()).isFalse();

            var address2 = sellerAddressRepository.findById(address2Id).orElseThrow();
            assertThat(address2.isDefaultAddress()).isTrue();
        }

        @Test
        @DisplayName("타입별 기본 주소 독립 관리 플로우")
        void shouldManageDefaultAddressIndependentlyPerType() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            // Step 1 - SHIPPING 기본 주소 생성
            Map<String, Object> shippingRequest =
                    Map.of(
                            "addressType",
                            "SHIPPING",
                            "addressName",
                            "출고 주소",
                            "address",
                            Map.of(
                                    "zipCode", "06164",
                                    "line1", "서울 강남구 역삼로 123",
                                    "line2", "5층"),
                            "defaultAddress",
                            true);

            Response shippingResponse =
                    given().spec(givenAdminJson())
                            .body(shippingRequest)
                            .when()
                            .post(COMMAND_PATH, sellerId);

            Long shippingId = shippingResponse.jsonPath().getLong("data.addressId");

            // Step 2 - RETURN 기본 주소 생성
            Map<String, Object> returnRequest =
                    Map.of(
                            "addressType",
                            "RETURN",
                            "addressName",
                            "반품 주소",
                            "address",
                            Map.of(
                                    "zipCode", "06165",
                                    "line1", "서울 강남구 역삼로 456",
                                    "line2", "6층"),
                            "defaultAddress",
                            true);

            Response returnResponse =
                    given().spec(givenAdminJson())
                            .body(returnRequest)
                            .when()
                            .post(COMMAND_PATH, sellerId);

            Long returnId = returnResponse.jsonPath().getLong("data.addressId");

            // Step 3 - 타입별 기본 주소 확인
            given().spec(givenAdmin())
                    .queryParam("sellerIds", sellerId)
                    .queryParam("defaultAddress", true)
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2));

            // Step 4 - SHIPPING 새 기본 주소 생성
            Map<String, Object> newShippingRequest =
                    Map.of(
                            "addressType",
                            "SHIPPING",
                            "addressName",
                            "새 출고 주소",
                            "address",
                            Map.of(
                                    "zipCode", "06166",
                                    "line1", "서울 강남구 역삼로 789",
                                    "line2", "7층"),
                            "defaultAddress",
                            true);

            Response newShippingResponse =
                    given().spec(givenAdminJson())
                            .body(newShippingRequest)
                            .when()
                            .post(COMMAND_PATH, sellerId);

            Long newShippingId = newShippingResponse.jsonPath().getLong("data.addressId");

            // Step 5 - RETURN 기본 주소 유지 확인
            given().spec(givenAdmin())
                    .queryParam("sellerIds", sellerId)
                    .queryParam("defaultAddress", true)
                    .when()
                    .get(QUERY_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2));

            // DB 검증: SHIPPING 기본 주소 변경됨
            var oldShipping = sellerAddressRepository.findById(shippingId).orElseThrow();
            assertThat(oldShipping.isDefaultAddress()).isFalse();

            var newShipping = sellerAddressRepository.findById(newShippingId).orElseThrow();
            assertThat(newShipping.isDefaultAddress()).isTrue();

            // DB 검증: RETURN 기본 주소 유지됨
            var returnAddress = sellerAddressRepository.findById(returnId).orElseThrow();
            assertThat(returnAddress.isDefaultAddress()).isTrue();
        }
    }
}
