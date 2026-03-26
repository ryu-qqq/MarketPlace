package com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("LegacyShipmentErrorMapper 단위 테스트")
class LegacyShipmentErrorMapperTest {

    private final LegacyShipmentErrorMapper sut = new LegacyShipmentErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("SHIPMENT_ 코드를 가진 예외를 지원한다")
        void supports_ShipmentCodeException_ReturnsTrue() {
            // given
            DomainException ex = createDomainException("SHIPMENT_NOT_FOUND", 404);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("SHIPMENT_INVALID 코드를 가진 예외를 지원한다")
        void supports_ShipmentInvalidCodeException_ReturnsTrue() {
            // given
            DomainException ex = createDomainException("SHIPMENT_INVALID_STATUS", 400);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("SELLER_ 코드를 가진 예외는 지원하지 않는다")
        void supports_SellerCodeException_ReturnsFalse() {
            // given
            DomainException ex = createDomainException("SELLER_NOT_FOUND", 404);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("다른 도메인 예외는 지원하지 않는다")
        void supports_OtherDomainException_ReturnsFalse() {
            // given
            DomainException ex = createDomainException("AUTH_001", 401);

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
        @DisplayName("404 예외는 NOT_FOUND로 변환된다")
        void map_ShipmentNotFoundException_ReturnsNotFound() {
            // given
            DomainException ex = createDomainException("SHIPMENT_NOT_FOUND", 404);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("변환된 title이 'Legacy Shipment Error'이다")
        void map_ShipmentException_ReturnsCorrectTitle() {
            // given
            DomainException ex = createDomainException("SHIPMENT_NOT_FOUND", 404);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.title()).isEqualTo("Legacy Shipment Error");
        }

        @Test
        @DisplayName("에러 타입 URI에 '/errors/legacy-shipment/' 접두사가 포함된다")
        void map_ShipmentException_TypeUriContainsPrefix() {
            // given
            DomainException ex = createDomainException("SHIPMENT_NOT_FOUND", 404);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString()).startsWith("/errors/legacy-shipment/");
        }

        @Test
        @DisplayName("에러 타입 URI에 소문자 에러 코드가 포함된다")
        void map_ShipmentException_TypeUriContainsLowercaseCode() {
            // given
            DomainException ex = createDomainException("SHIPMENT_NOT_FOUND", 404);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString())
                    .isEqualTo("/errors/legacy-shipment/shipment_not_found");
        }

        @Test
        @DisplayName("400 상태 예외는 BAD_REQUEST로 변환된다")
        void map_ShipmentException400_ReturnsBadRequest() {
            // given
            DomainException ex = createDomainException("SHIPMENT_INVALID_STATUS", 400);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    private static DomainException createDomainException(String code, int httpStatus) {
        return new DomainException(
                new ErrorCode() {
                    @Override
                    public String getCode() {
                        return code;
                    }

                    @Override
                    public int getHttpStatus() {
                        return httpStatus;
                    }

                    @Override
                    public String getMessage() {
                        return "Test error message";
                    }
                }) {};
    }
}
