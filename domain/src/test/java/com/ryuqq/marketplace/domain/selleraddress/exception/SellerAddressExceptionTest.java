package com.ryuqq.marketplace.domain.selleraddress.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAddress 예외 테스트")
class SellerAddressExceptionTest {

    @Nested
    @DisplayName("SellerAddressErrorCode 테스트")
    class ErrorCodeTest {

        @Test
        @DisplayName("SELLER_ADDRESS_NOT_FOUND 에러 코드가 올바르다")
        void sellerAddressNotFound() {
            SellerAddressErrorCode code = SellerAddressErrorCode.SELLER_ADDRESS_NOT_FOUND;
            assertThat(code.getCode()).isEqualTo("ADDR-001");
            assertThat(code.getHttpStatus()).isEqualTo(404);
            assertThat(code.getMessage()).contains("찾을 수 없습니다");
        }

        @Test
        @DisplayName("CANNOT_DELETE_DEFAULT_ADDRESS 에러 코드가 올바르다")
        void cannotDeleteDefaultAddress() {
            SellerAddressErrorCode code = SellerAddressErrorCode.CANNOT_DELETE_DEFAULT_ADDRESS;
            assertThat(code.getCode()).isEqualTo("ADDR-002");
            assertThat(code.getHttpStatus()).isEqualTo(400);
            assertThat(code.getMessage()).contains("기본 주소");
        }

        @Test
        @DisplayName("DUPLICATE_ADDRESS_NAME 에러 코드가 올바르다")
        void duplicateAddressName() {
            SellerAddressErrorCode code = SellerAddressErrorCode.DUPLICATE_ADDRESS_NAME;
            assertThat(code.getCode()).isEqualTo("ADDR-004");
            assertThat(code.getHttpStatus()).isEqualTo(400);
        }
    }

    @Nested
    @DisplayName("예외 클래스 테스트")
    class ExceptionClassTest {

        @Test
        @DisplayName("SellerAddressNotFoundException 생성 시 올바른 에러 코드를 가진다")
        void sellerAddressNotFoundException() {
            SellerAddressNotFoundException exception = new SellerAddressNotFoundException();
            assertThat(exception.code()).isEqualTo("ADDR-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("CannotDeleteDefaultAddressException 생성 시 올바른 에러 코드를 가진다")
        void cannotDeleteDefaultAddressException() {
            CannotDeleteDefaultAddressException exception =
                    new CannotDeleteDefaultAddressException();
            assertThat(exception.code()).isEqualTo("ADDR-002");
            assertThat(exception.httpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("DuplicateAddressNameException 생성 시 올바른 에러 코드를 가진다")
        void duplicateAddressNameException() {
            DuplicateAddressNameException exception = new DuplicateAddressNameException();
            assertThat(exception.code()).isEqualTo("ADDR-004");
            assertThat(exception.httpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("SellerAddressException은 커스텀 메시지를 지원한다")
        void sellerAddressExceptionWithCustomMessage() {
            SellerAddressException exception =
                    new SellerAddressException(
                            SellerAddressErrorCode.SELLER_ADDRESS_NOT_FOUND, "커스텀 메시지");
            assertThat(exception.getMessage()).isEqualTo("커스텀 메시지");
            assertThat(exception.code()).isEqualTo("ADDR-001");
        }
    }
}
