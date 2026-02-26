package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.inboundproduct.exception.InboundProductErrorCode;
import com.ryuqq.marketplace.domain.inboundproduct.exception.InboundProductException;
import com.ryuqq.marketplace.domain.inboundproduct.exception.InboundProductNotFoundException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("InboundProductErrorMapper 단위 테스트")
class InboundProductErrorMapperTest {

    private final InboundProductErrorMapper sut = new InboundProductErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("InboundProductNotFoundException을 지원한다")
        void supports_InboundProductNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new InboundProductNotFoundException(1L, "EXT-001");

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("InboundProductException을 지원한다")
        void supports_InboundProductException_ReturnsTrue() {
            // given
            DomainException ex =
                    new InboundProductException(
                            InboundProductErrorCode.INBOUND_PRODUCT_INVALID_STATUS);

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
        @DisplayName("InboundProductNotFoundException을 404 MappedError로 변환한다")
        void map_InboundProductNotFoundException_Returns404() {
            // given
            InboundProductNotFoundException ex = new InboundProductNotFoundException(1L, "EXT-001");

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Inbound Product Error");
            assertThat(result.type().toString()).startsWith("/errors/inbound-product");
            assertThat(result.type().toString())
                    .contains(
                            InboundProductErrorCode.INBOUND_PRODUCT_NOT_FOUND
                                    .getCode()
                                    .toLowerCase(Locale.ROOT));
        }

        @Test
        @DisplayName("INBOUND_PRODUCT_INVALID_STATUS 예외를 400 MappedError로 변환한다")
        void map_InboundProductInvalidStatusException_Returns400() {
            // given
            InboundProductException ex =
                    new InboundProductException(
                            InboundProductErrorCode.INBOUND_PRODUCT_INVALID_STATUS);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Inbound Product Error");
            assertThat(result.type().toString()).startsWith("/errors/inbound-product");
        }

        @Test
        @DisplayName("INBOUND_PRODUCT_CONVERSION_FAILED 예외를 500 MappedError로 변환한다")
        void map_InboundProductConversionFailedException_Returns500() {
            // given
            InboundProductException ex =
                    new InboundProductException(
                            InboundProductErrorCode.INBOUND_PRODUCT_CONVERSION_FAILED);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(result.title()).isEqualTo("Inbound Product Error");
        }

        @Test
        @DisplayName("에러 타입 URI에 소문자 에러 코드가 포함된다")
        void map_InboundProductNotFoundException_TypeUriContainsLowercaseCode() {
            // given
            InboundProductNotFoundException ex =
                    new InboundProductNotFoundException(42L, "EXT-042");

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString())
                    .isEqualTo(
                            "/errors/inbound-product/"
                                    + InboundProductErrorCode.INBOUND_PRODUCT_NOT_FOUND
                                            .getCode()
                                            .toLowerCase(Locale.ROOT));
        }

        @Test
        @DisplayName("INBOUND_PRODUCT_PAYLOAD_INVALID 예외를 400 MappedError로 변환한다")
        void map_InboundPayloadInvalidException_Returns400() {
            // given
            InboundProductException ex =
                    new InboundProductException(
                            InboundProductErrorCode.INBOUND_PRODUCT_PAYLOAD_INVALID);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.type().toString())
                    .contains(
                            InboundProductErrorCode.INBOUND_PRODUCT_PAYLOAD_INVALID
                                    .getCode()
                                    .toLowerCase(Locale.ROOT));
        }
    }
}
