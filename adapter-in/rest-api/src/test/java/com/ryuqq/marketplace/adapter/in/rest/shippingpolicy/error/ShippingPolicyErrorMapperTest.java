package com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.DefaultShippingPolicyNotFoundException;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.ShippingPolicyException;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.ShippingPolicyNotFoundForSellerException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("ShippingPolicyErrorMapper 단위 테스트")
class ShippingPolicyErrorMapperTest {

    private final ShippingPolicyErrorMapper sut = new ShippingPolicyErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("ShippingPolicyException을 지원한다")
        void supports_ShippingPolicyException_ReturnsTrue() {
            // given
            DomainException ex = ShippingPolicyException.policyNotFound();

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("ShippingPolicyNotFoundForSellerException을 지원한다")
        void supports_ShippingPolicyNotFoundForSellerException_ReturnsTrue() {
            // given
            DomainException ex = new ShippingPolicyNotFoundForSellerException(1L, 2L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("DefaultShippingPolicyNotFoundException을 지원한다")
        void supports_DefaultShippingPolicyNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new DefaultShippingPolicyNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("ShippingPolicyException.policyInactive()를 지원한다")
        void supports_ShippingPolicyInactive_ReturnsTrue() {
            // given
            DomainException ex = ShippingPolicyException.policyInactive();

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
        @DisplayName("policyNotFound를 404 MappedError로 변환한다")
        void map_PolicyNotFound_Returns404() {
            // given
            ShippingPolicyException ex = ShippingPolicyException.policyNotFound();

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Shipping Policy Error");
            assertThat(result.type().toString()).startsWith("/errors/shipping-policy/");
            assertThat(result.type().toString()).contains("shp-001");
        }

        @Test
        @DisplayName("policyInactive를 400 MappedError로 변환한다")
        void map_PolicyInactive_Returns400() {
            // given
            ShippingPolicyException ex = ShippingPolicyException.policyInactive();

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Shipping Policy Error");
            assertThat(result.type().toString()).startsWith("/errors/shipping-policy/");
        }

        @Test
        @DisplayName("invalidFreeThreshold를 400 MappedError로 변환한다")
        void map_InvalidFreeThreshold_Returns400() {
            // given
            ShippingPolicyException ex = ShippingPolicyException.invalidFreeThreshold();

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Shipping Policy Error");
            assertThat(result.type().toString()).startsWith("/errors/shipping-policy/");
        }

        @Test
        @DisplayName("DefaultShippingPolicyNotFoundException을 400 MappedError로 변환한다")
        void map_DefaultShippingPolicyNotFoundException_Returns400() {
            // given
            ShippingPolicyException ex = new DefaultShippingPolicyNotFoundException(1L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Shipping Policy Error");
            assertThat(result.detail()).contains("기본 배송 정책이 없습니다");
            assertThat(result.type().toString()).contains("shp-015");
        }

        @Test
        @DisplayName("에러 타입 URI에 소문자 에러 코드가 포함된다")
        void map_ShippingPolicyException_TypeUriContainsLowercaseCode() {
            // given
            ShippingPolicyException ex = ShippingPolicyException.policyNotFound();

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString()).isEqualTo("/errors/shipping-policy/shp-001");
        }
    }
}
