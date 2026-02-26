package com.ryuqq.marketplace.adapter.in.rest.shipment.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.shipment.exception.ShipmentErrorCode;
import com.ryuqq.marketplace.domain.shipment.exception.ShipmentException;
import com.ryuqq.marketplace.domain.shipment.exception.ShipmentNotFoundException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("ShipmentErrorMapper 단위 테스트")
class ShipmentErrorMapperTest {

    private final ShipmentErrorMapper sut = new ShipmentErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("ShipmentNotFoundException을 지원한다")
        void supports_ShipmentNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new ShipmentNotFoundException("SHIP-001");

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("ShipmentException을 지원한다")
        void supports_ShipmentException_ReturnsTrue() {
            // given
            DomainException ex = new ShipmentException(ShipmentErrorCode.INVALID_STATUS_TRANSITION);

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
        @DisplayName("ShipmentNotFoundException(SHP-001)을 404 MappedError로 변환하고 제목이 올바르다")
        void map_ShipmentNotFoundException_Returns404WithCorrectTitle() {
            // given
            ShipmentException ex = new ShipmentNotFoundException("SHIP-001");

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("배송 정보를 찾을 수 없음");
            assertThat(result.type().toString()).startsWith("/errors/shipment/");
            assertThat(result.type().toString()).contains("shp-001");
        }

        @Test
        @DisplayName("INVALID_STATUS_TRANSITION(SHP-002)을 400 MappedError로 변환하고 제목이 올바르다")
        void map_InvalidStatusTransition_Returns400WithCorrectTitle() {
            // given
            ShipmentException ex =
                    new ShipmentException(ShipmentErrorCode.INVALID_STATUS_TRANSITION);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("유효하지 않은 배송 상태 변경");
            assertThat(result.type().toString()).startsWith("/errors/shipment/");
        }

        @Test
        @DisplayName("TRACKING_NUMBER_REQUIRED(SHP-003)을 400 MappedError로 변환하고 제목이 올바르다")
        void map_TrackingNumberRequired_Returns400WithCorrectTitle() {
            // given
            ShipmentException ex =
                    new ShipmentException(ShipmentErrorCode.TRACKING_NUMBER_REQUIRED);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("송장번호 필수");
            assertThat(result.type().toString()).startsWith("/errors/shipment/");
        }

        @Test
        @DisplayName("에러 타입 URI에 소문자 에러 코드가 포함된다")
        void map_ShipmentNotFoundException_TypeUriContainsLowercaseCode() {
            // given
            ShipmentException ex = new ShipmentNotFoundException("SHIP-001");

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString()).isEqualTo("/errors/shipment/shp-001");
        }
    }
}
