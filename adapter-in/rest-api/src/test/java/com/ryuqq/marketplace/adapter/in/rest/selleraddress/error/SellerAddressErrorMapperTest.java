package com.ryuqq.marketplace.adapter.in.rest.selleraddress.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.selleraddress.exception.CannotDeleteDefaultAddressException;
import com.ryuqq.marketplace.domain.selleraddress.exception.DuplicateAddressNameException;
import com.ryuqq.marketplace.domain.selleraddress.exception.SellerAddressException;
import com.ryuqq.marketplace.domain.selleraddress.exception.SellerAddressNotFoundException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("SellerAddressErrorMapper 단위 테스트")
class SellerAddressErrorMapperTest {

    private final SellerAddressErrorMapper sut = new SellerAddressErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("SellerAddressNotFoundException을 지원한다")
        void supports_SellerAddressNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new SellerAddressNotFoundException();

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("CannotDeleteDefaultAddressException을 지원한다")
        void supports_CannotDeleteDefaultAddressException_ReturnsTrue() {
            // given
            DomainException ex = new CannotDeleteDefaultAddressException();

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("DuplicateAddressNameException을 지원한다")
        void supports_DuplicateAddressNameException_ReturnsTrue() {
            // given
            DomainException ex = new DuplicateAddressNameException();

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("다른 도메인 예외는 지원하지 않는다")
        void supports_OtherDomainException_ReturnsFalse() {
            // given
            DomainException ex =
                    new DomainException(
                            new com.ryuqq.marketplace.domain.common.exception.ErrorCode() {
                                @Override
                                public String getCode() {
                                    return "OTHER-001";
                                }

                                @Override
                                public int getHttpStatus() {
                                    return 400;
                                }

                                @Override
                                public String getMessage() {
                                    return "Other error";
                                }
                            }) {};

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("map() - 예외를 MappedError로 변환")
    class MapTest {

        @Test
        @DisplayName("SellerAddressNotFoundException을 404 MappedError로 변환한다")
        void map_SellerAddressNotFoundException_Returns404() {
            // given
            SellerAddressException ex = new SellerAddressNotFoundException();

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Seller Address Error");
            assertThat(result.type().toString()).startsWith("/errors/seller-address/");
            assertThat(result.type().toString()).contains("addr-001");
        }

        @Test
        @DisplayName("CannotDeleteDefaultAddressException을 400 MappedError로 변환한다")
        void map_CannotDeleteDefaultAddressException_Returns400() {
            // given
            SellerAddressException ex = new CannotDeleteDefaultAddressException();

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Seller Address Error");
            assertThat(result.type().toString()).startsWith("/errors/seller-address/");
        }

        @Test
        @DisplayName("DuplicateAddressNameException을 400 MappedError로 변환한다")
        void map_DuplicateAddressNameException_Returns400() {
            // given
            SellerAddressException ex = new DuplicateAddressNameException();

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Seller Address Error");
            assertThat(result.type().toString()).startsWith("/errors/seller-address/");
        }

        @Test
        @DisplayName("에러 타입 URI에 소문자 에러 코드가 포함된다")
        void map_SellerAddressNotFoundException_TypeUriContainsLowercaseCode() {
            // given
            SellerAddressException ex = new SellerAddressNotFoundException();

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString()).isEqualTo("/errors/seller-address/addr-001");
        }
    }
}
