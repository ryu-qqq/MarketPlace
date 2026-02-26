package com.ryuqq.marketplace.domain.seller.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerErrorCode 테스트")
class SellerErrorCodeTest {

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현 테스트")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void implementsErrorCode() {
            // then
            assertThat(SellerErrorCode.SELLER_NOT_FOUND).isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("셀러 관련 에러 코드 테스트")
    class SellerErrorCodesTest {

        @Test
        @DisplayName("SELLER_NOT_FOUND 에러 코드를 검증한다")
        void sellerNotFound() {
            // then
            assertThat(SellerErrorCode.SELLER_NOT_FOUND.getCode()).isEqualTo("SEL-001");
            assertThat(SellerErrorCode.SELLER_NOT_FOUND.getHttpStatus()).isEqualTo(404);
            assertThat(SellerErrorCode.SELLER_NOT_FOUND.getMessage()).isEqualTo("셀러를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("SELLER_ALREADY_EXISTS 에러 코드를 검증한다")
        void sellerAlreadyExists() {
            // then
            assertThat(SellerErrorCode.SELLER_ALREADY_EXISTS.getCode()).isEqualTo("SEL-002");
            assertThat(SellerErrorCode.SELLER_ALREADY_EXISTS.getHttpStatus()).isEqualTo(409);
            assertThat(SellerErrorCode.SELLER_ALREADY_EXISTS.getMessage())
                    .isEqualTo("이미 존재하는 셀러입니다");
        }

        @Test
        @DisplayName("SELLER_INACTIVE 에러 코드를 검증한다")
        void sellerInactive() {
            // then
            assertThat(SellerErrorCode.SELLER_INACTIVE.getCode()).isEqualTo("SEL-003");
            assertThat(SellerErrorCode.SELLER_INACTIVE.getHttpStatus()).isEqualTo(400);
            assertThat(SellerErrorCode.SELLER_INACTIVE.getMessage()).isEqualTo("비활성화된 셀러입니다");
        }

        @Test
        @DisplayName("SELLER_NAME_DUPLICATE 에러 코드를 검증한다")
        void sellerNameDuplicate() {
            // then
            assertThat(SellerErrorCode.SELLER_NAME_DUPLICATE.getCode()).isEqualTo("SEL-006");
            assertThat(SellerErrorCode.SELLER_NAME_DUPLICATE.getHttpStatus()).isEqualTo(409);
            assertThat(SellerErrorCode.SELLER_NAME_DUPLICATE.getMessage())
                    .isEqualTo("이미 존재하는 셀러명입니다");
        }
    }

    @Nested
    @DisplayName("사업자 정보 관련 에러 코드 테스트")
    class BusinessInfoErrorCodesTest {

        @Test
        @DisplayName("BUSINESS_INFO_NOT_FOUND 에러 코드를 검증한다")
        void businessInfoNotFound() {
            // then
            assertThat(SellerErrorCode.BUSINESS_INFO_NOT_FOUND.getCode()).isEqualTo("SEL-100");
            assertThat(SellerErrorCode.BUSINESS_INFO_NOT_FOUND.getHttpStatus()).isEqualTo(404);
            assertThat(SellerErrorCode.BUSINESS_INFO_NOT_FOUND.getMessage())
                    .isEqualTo("사업자 정보를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("INVALID_REGISTRATION_NUMBER 에러 코드를 검증한다")
        void invalidRegistrationNumber() {
            // then
            assertThat(SellerErrorCode.INVALID_REGISTRATION_NUMBER.getCode()).isEqualTo("SEL-102");
            assertThat(SellerErrorCode.INVALID_REGISTRATION_NUMBER.getHttpStatus()).isEqualTo(400);
            assertThat(SellerErrorCode.INVALID_REGISTRATION_NUMBER.getMessage())
                    .isEqualTo("유효하지 않은 사업자등록번호입니다");
        }

        @Test
        @DisplayName("REGISTRATION_NUMBER_DUPLICATE 에러 코드를 검증한다")
        void registrationNumberDuplicate() {
            // then
            assertThat(SellerErrorCode.REGISTRATION_NUMBER_DUPLICATE.getCode())
                    .isEqualTo("SEL-103");
            assertThat(SellerErrorCode.REGISTRATION_NUMBER_DUPLICATE.getHttpStatus())
                    .isEqualTo(409);
            assertThat(SellerErrorCode.REGISTRATION_NUMBER_DUPLICATE.getMessage())
                    .isEqualTo("이미 등록된 사업자등록번호입니다");
        }
    }

    @Nested
    @DisplayName("주소 관련 에러 코드 테스트")
    class AddressErrorCodesTest {

        @Test
        @DisplayName("ADDRESS_NOT_FOUND 에러 코드를 검증한다")
        void addressNotFound() {
            // then
            assertThat(SellerErrorCode.ADDRESS_NOT_FOUND.getCode()).isEqualTo("SEL-200");
            assertThat(SellerErrorCode.ADDRESS_NOT_FOUND.getHttpStatus()).isEqualTo(404);
            assertThat(SellerErrorCode.ADDRESS_NOT_FOUND.getMessage()).isEqualTo("주소를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("DEFAULT_ADDRESS_REQUIRED 에러 코드를 검증한다")
        void defaultAddressRequired() {
            // then
            assertThat(SellerErrorCode.DEFAULT_ADDRESS_REQUIRED.getCode()).isEqualTo("SEL-202");
            assertThat(SellerErrorCode.DEFAULT_ADDRESS_REQUIRED.getHttpStatus()).isEqualTo(400);
            assertThat(SellerErrorCode.DEFAULT_ADDRESS_REQUIRED.getMessage())
                    .isEqualTo("기본 주소는 삭제할 수 없습니다");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 에러 코드 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(SellerErrorCode.values())
                    .containsExactly(
                            SellerErrorCode.SELLER_NOT_FOUND,
                            SellerErrorCode.SELLER_ALREADY_EXISTS,
                            SellerErrorCode.SELLER_INACTIVE,
                            SellerErrorCode.SELLER_ALREADY_ACTIVE,
                            SellerErrorCode.SELLER_ALREADY_INACTIVE,
                            SellerErrorCode.SELLER_NAME_DUPLICATE,
                            SellerErrorCode.BUSINESS_INFO_NOT_FOUND,
                            SellerErrorCode.BUSINESS_INFO_ALREADY_EXISTS,
                            SellerErrorCode.INVALID_REGISTRATION_NUMBER,
                            SellerErrorCode.REGISTRATION_NUMBER_DUPLICATE,
                            SellerErrorCode.ADDRESS_NOT_FOUND,
                            SellerErrorCode.ADDRESS_TYPE_MISMATCH,
                            SellerErrorCode.DEFAULT_ADDRESS_REQUIRED,
                            SellerErrorCode.DEFAULT_ADDRESS_ALREADY_EXISTS,
                            SellerErrorCode.CS_NOT_FOUND,
                            SellerErrorCode.CONTRACT_NOT_FOUND,
                            SellerErrorCode.SETTLEMENT_NOT_FOUND);
        }
    }
}
