package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.outboundproduct.exception.OutboundProductErrorCode;
import com.ryuqq.marketplace.domain.outboundproduct.exception.OutboundProductException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("OutboundProductErrorMapper 단위 테스트")
class OutboundProductErrorMapperTest {

    private final OutboundProductErrorMapper sut = new OutboundProductErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("OutboundProductException을 지원한다")
        void supports_OutboundProductException_ReturnsTrue() {
            // given
            DomainException ex =
                    new OutboundProductException(
                            OutboundProductErrorCode.OUTBOUND_PRODUCT_NOT_FOUND);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("OutboundProductException의 하위 예외를 지원한다")
        void supports_OutboundProductExceptionSubclass_ReturnsTrue() {
            // given
            DomainException ex =
                    new OutboundProductException(
                            OutboundProductErrorCode.OUTBOUND_PRODUCT_ALREADY_REGISTERED);

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
        @DisplayName("OUTBOUND_PRODUCT_NOT_FOUND를 404 MappedError로 변환한다")
        void map_OutboundProductNotFound_Returns404() {
            // given
            OutboundProductException ex =
                    new OutboundProductException(
                            OutboundProductErrorCode.OUTBOUND_PRODUCT_NOT_FOUND);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Outbound Product Error");
            assertThat(result.type().toString()).startsWith("/errors/outbound-product/");
            assertThat(result.type().toString()).contains("obp-001");
        }

        @Test
        @DisplayName("OUTBOUND_PRODUCT_ALREADY_REGISTERED를 409 MappedError로 변환한다")
        void map_OutboundProductAlreadyRegistered_Returns409() {
            // given
            OutboundProductException ex =
                    new OutboundProductException(
                            OutboundProductErrorCode.OUTBOUND_PRODUCT_ALREADY_REGISTERED);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("Outbound Product Error");
            assertThat(result.type().toString()).startsWith("/errors/outbound-product/");
        }

        @Test
        @DisplayName("OUTBOUND_PRODUCT_INVALID_STATUS를 400 MappedError로 변환한다")
        void map_OutboundProductInvalidStatus_Returns400() {
            // given
            OutboundProductException ex =
                    new OutboundProductException(
                            OutboundProductErrorCode.OUTBOUND_PRODUCT_INVALID_STATUS);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Outbound Product Error");
            assertThat(result.type().toString()).startsWith("/errors/outbound-product/");
        }

        @Test
        @DisplayName("에러 타입 URI에 소문자 에러 코드가 포함된다")
        void map_OutboundProductException_TypeUriContainsLowercaseCode() {
            // given
            OutboundProductException ex =
                    new OutboundProductException(
                            OutboundProductErrorCode.OUTBOUND_PRODUCT_NOT_FOUND);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString()).isEqualTo("/errors/outbound-product/obp-001");
        }
    }
}
