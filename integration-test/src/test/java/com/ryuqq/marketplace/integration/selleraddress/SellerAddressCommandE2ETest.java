package com.ryuqq.marketplace.integration.selleraddress;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.SellerAddressJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.entity.SellerAddressJpaEntity;
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
 * SellerAddress Command API E2E 테스트.
 *
 * <p>POST, PUT, PATCH /api/v1/market/sellers/{sellerId}/addresses 엔드포인트 테스트
 */
@DisplayName("SellerAddress Command API E2E 테스트")
class SellerAddressCommandE2ETest extends E2ETestBase {

    private static final String BASE_PATH = "/sellers/{sellerId}/addresses";

    @Autowired private SellerJpaRepository sellerRepository;
    @Autowired private SellerAddressJpaRepository sellerAddressRepository;

    @BeforeEach
    void setUp() {
        sellerAddressRepository.deleteAll();
        sellerRepository.deleteAll();
    }

    @Nested
    @DisplayName("POST /sellers/{sellerId}/addresses - 주소 등록")
    class RegisterAddressTest {

        @Test
        @DisplayName("생성 성공 - 유효한 요청으로 주소 등록")
        void shouldRegisterAddressSuccessfully() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();
            Map<String, Object> request = createRegisterRequest("SHIPPING", "본사 창고", false);

            // when
            Response response =
                    given().spec(givenAdminJson()).body(request).when().post(BASE_PATH, sellerId);

            // then
            response.then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data.addressId", greaterThan(0));

            Long addressId = response.jsonPath().getLong("data.addressId");
            assertThat(sellerAddressRepository.findById(addressId)).isPresent();
        }

        @Test
        @DisplayName("생성 성공 - 기본 주소로 등록")
        void shouldRegisterAsDefaultAddress() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();
            Map<String, Object> request = createRegisterRequest("SHIPPING", "본사 창고", true);

            // when
            Response response =
                    given().spec(givenAdminJson()).body(request).when().post(BASE_PATH, sellerId);

            // then
            response.then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data.addressId", greaterThan(0));

            Long addressId = response.jsonPath().getLong("data.addressId");
            var address = sellerAddressRepository.findById(addressId).orElseThrow();
            assertThat(address.isDefaultAddress()).isTrue();
        }

        @Test
        @DisplayName("기본 주소 자동 전환 - 기존 기본 주소가 있을 때")
        void shouldUnmarkExistingDefaultWhenRegisteringNewDefault() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            SellerAddressJpaEntity existingDefault =
                    sellerAddressRepository.save(
                            SellerAddressJpaEntityFixtures.defaultShippingEntity(sellerId));
            Long existingId = existingDefault.getId();

            Map<String, Object> request = createRegisterRequest("SHIPPING", "새 본사 창고", true);

            // when
            Response response =
                    given().spec(givenAdminJson()).body(request).when().post(BASE_PATH, sellerId);

            // then
            response.then().statusCode(HttpStatus.CREATED.value());

            Long newId = response.jsonPath().getLong("data.addressId");

            var existingAddress = sellerAddressRepository.findById(existingId).orElseThrow();
            assertThat(existingAddress.isDefaultAddress()).isFalse();

            var newAddress = sellerAddressRepository.findById(newId).orElseThrow();
            assertThat(newAddress.isDefaultAddress()).isTrue();
        }

        @Test
        @DisplayName("Validation 실패 - addressType 누락")
        void shouldFailWhenAddressTypeIsMissing() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            Map<String, Object> request =
                    Map.of(
                            "addressName",
                            "본사 창고",
                            "address",
                            createAddressDto(),
                            "defaultAddress",
                            false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_PATH, sellerId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("Validation 실패 - address 누락")
        void shouldFailWhenAddressIsMissing() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            Map<String, Object> request =
                    Map.of(
                            "addressType", "SHIPPING",
                            "addressName", "본사 창고",
                            "defaultAddress", false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_PATH, sellerId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("Validation 실패 - address.zipCode 누락")
        void shouldFailWhenZipCodeIsMissing() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            Map<String, Object> address = Map.of("line1", "서울 강남구 역삼로 123");
            Map<String, Object> request =
                    Map.of(
                            "addressType",
                            "SHIPPING",
                            "addressName",
                            "본사 창고",
                            "address",
                            address,
                            "defaultAddress",
                            false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_PATH, sellerId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("Validation 실패 - address.line1 누락")
        void shouldFailWhenLine1IsMissing() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            Map<String, Object> address = Map.of("zipCode", "06164");
            Map<String, Object> request =
                    Map.of(
                            "addressType",
                            "SHIPPING",
                            "addressName",
                            "본사 창고",
                            "address",
                            address,
                            "defaultAddress",
                            false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_PATH, sellerId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("성공 - 다른 타입에서는 동일 주소명 허용")
        void shouldAllowSameAddressNameInDifferentType() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            sellerAddressRepository.save(
                    SellerAddressJpaEntityFixtures.shippingEntity(sellerId, "본사 창고", true));

            Map<String, Object> request = createRegisterRequest("RETURN", "본사 창고", false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_PATH, sellerId)
                    .then()
                    .statusCode(HttpStatus.CREATED.value());
        }
    }

    @Nested
    @DisplayName("PUT /sellers/{sellerId}/addresses/{addressId} - 주소 수정")
    class UpdateAddressTest {

        @Test
        @DisplayName("수정 성공 - 주소 정보 수정")
        void shouldUpdateAddressSuccessfully() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            SellerAddressJpaEntity address =
                    sellerAddressRepository.save(
                            SellerAddressJpaEntityFixtures.shippingEntity(
                                    sellerId, "본사 창고", false));
            Long addressId = address.getId();

            Map<String, Object> request = createUpdateRequest("물류센터", "06165", null);

            // when
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_PATH + "/{addressId}", sellerId, addressId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // then
            var updated = sellerAddressRepository.findById(addressId).orElseThrow();
            assertThat(updated.getAddressName()).isEqualTo("물류센터");
            assertThat(updated.getZipcode()).isEqualTo("06165");
        }

        @Test
        @DisplayName("수정 성공 - 기본 주소로 변경")
        void shouldUpdateToDefaultAddress() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            SellerAddressJpaEntity existingDefault =
                    sellerAddressRepository.save(
                            SellerAddressJpaEntityFixtures.defaultShippingEntity(sellerId));
            Long existingDefaultId = existingDefault.getId();

            SellerAddressJpaEntity address =
                    sellerAddressRepository.save(
                            SellerAddressJpaEntityFixtures.nonDefaultShippingEntity(
                                    sellerId, "물류센터"));
            Long addressId = address.getId();

            Map<String, Object> request = createUpdateRequest("새 기본 주소", null, true);

            // when
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_PATH + "/{addressId}", sellerId, addressId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // then
            var existingDefaultAfter =
                    sellerAddressRepository.findById(existingDefaultId).orElseThrow();
            assertThat(existingDefaultAfter.isDefaultAddress()).isFalse();

            var updated = sellerAddressRepository.findById(addressId).orElseThrow();
            assertThat(updated.isDefaultAddress()).isTrue();
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 주소 ID")
        void shouldFailWhenAddressNotFound() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();
            Long nonExistentId = 99999L;

            Map<String, Object> request = createUpdateRequest("물류센터", null, null);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_PATH + "/{addressId}", sellerId, nonExistentId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("Validation 실패 - address 누락")
        void shouldFailWhenAddressIsMissing() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            SellerAddressJpaEntity address =
                    sellerAddressRepository.save(
                            SellerAddressJpaEntityFixtures.shippingEntity(
                                    sellerId, "본사 창고", false));
            Long addressId = address.getId();

            Map<String, Object> request = Map.of("addressName", "물류센터");

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_PATH + "/{addressId}", sellerId, addressId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @DisplayName("PATCH /sellers/{sellerId}/addresses/{addressId}/status - 주소 삭제")
    class DeleteAddressTest {

        @Test
        @DisplayName("삭제 성공 - 비기본 주소 소프트 삭제")
        void shouldDeleteNonDefaultAddressSuccessfully() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            SellerAddressJpaEntity address =
                    sellerAddressRepository.save(
                            SellerAddressJpaEntityFixtures.nonDefaultShippingEntity(
                                    sellerId, "물류센터"));
            Long addressId = address.getId();

            // when
            given().spec(givenAdmin())
                    .when()
                    .patch(BASE_PATH + "/{addressId}/status", sellerId, addressId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // then
            var deleted = sellerAddressRepository.findById(addressId).orElseThrow();
            assertThat(deleted.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("실패 - 기본 주소 삭제 불가 (동일 타입에 다른 주소 존재 시)")
        void shouldFailWhenDeletingDefaultAddress() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();

            // 기본 주소와 동일 타입의 다른 주소가 있으면 기본 주소 삭제 불가
            SellerAddressJpaEntity address =
                    sellerAddressRepository.save(
                            SellerAddressJpaEntityFixtures.defaultShippingEntity(sellerId));
            sellerAddressRepository.save(
                    SellerAddressJpaEntityFixtures.nonDefaultShippingEntity(sellerId, "물류센터"));
            Long addressId = address.getId();

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .patch(BASE_PATH + "/{addressId}/status", sellerId, addressId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());

            var addressAfter = sellerAddressRepository.findById(addressId).orElseThrow();
            assertThat(addressAfter.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 주소 ID")
        void shouldFailWhenAddressNotFound() {
            // given
            SellerJpaEntity seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntity());
            Long sellerId = seller.getId();
            Long nonExistentId = 99999L;

            // when & then
            given().spec(givenAdmin())
                    .when()
                    .patch(BASE_PATH + "/{addressId}/status", sellerId, nonExistentId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    // ===== Helper Methods =====

    private Map<String, Object> createRegisterRequest(
            String addressType, String addressName, boolean defaultAddress) {
        return Map.of(
                "addressType", addressType,
                "addressName", addressName,
                "address", createAddressDto(),
                "defaultAddress", defaultAddress);
    }

    private Map<String, Object> createUpdateRequest(
            String addressName, String zipCode, Boolean defaultAddress) {
        Map<String, Object> address =
                zipCode != null
                        ? Map.of("zipCode", zipCode, "line1", "서울 강남구 역삼로 456", "line2", "6층")
                        : createAddressDto();

        if (addressName != null && defaultAddress != null) {
            return Map.of(
                    "addressName", addressName,
                    "address", address,
                    "defaultAddress", defaultAddress);
        } else if (addressName != null) {
            return Map.of("addressName", addressName, "address", address);
        } else {
            return Map.of("address", address);
        }
    }

    private Map<String, Object> createAddressDto() {
        return Map.of("zipCode", "06164", "line1", "서울 강남구 역삼로 123", "line2", "5층");
    }
}
