package com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.saleschannelbrand.exception.SalesChannelBrandCodeDuplicateException;
import com.ryuqq.marketplace.domain.saleschannelbrand.exception.SalesChannelBrandException;
import com.ryuqq.marketplace.domain.saleschannelbrand.exception.SalesChannelBrandNotFoundException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("SalesChannelBrandErrorMapper 단위 테스트")
class SalesChannelBrandErrorMapperTest {

    private final SalesChannelBrandErrorMapper sut = new SalesChannelBrandErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("SalesChannelBrandNotFoundException을 지원한다")
        void supports_SalesChannelBrandNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new SalesChannelBrandNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("SalesChannelBrandCodeDuplicateException을 지원한다")
        void supports_SalesChannelBrandCodeDuplicateException_ReturnsTrue() {
            // given
            DomainException ex = new SalesChannelBrandCodeDuplicateException("BRAND-CODE");

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
        @DisplayName("SalesChannelBrandNotFoundException을 404 MappedError로 변환한다")
        void map_SalesChannelBrandNotFoundException_Returns404() {
            // given
            SalesChannelBrandException ex = new SalesChannelBrandNotFoundException(1L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Sales Channel Brand Error");
            assertThat(result.type().toString()).startsWith("/errors/sales-channel-brand/");
            assertThat(result.type().toString()).contains("scbrd-001");
        }

        @Test
        @DisplayName("SalesChannelBrandCodeDuplicateException을 409 MappedError로 변환한다")
        void map_SalesChannelBrandCodeDuplicateException_Returns409() {
            // given
            SalesChannelBrandException ex =
                    new SalesChannelBrandCodeDuplicateException("BRAND-CODE");

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("Sales Channel Brand Error");
            assertThat(result.type().toString()).startsWith("/errors/sales-channel-brand/");
        }

        @Test
        @DisplayName("에러 타입 URI에 소문자 에러 코드가 포함된다")
        void map_SalesChannelBrandNotFoundException_TypeUriContainsLowercaseCode() {
            // given
            SalesChannelBrandException ex = new SalesChannelBrandNotFoundException(42L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString()).isEqualTo("/errors/sales-channel-brand/scbrd-001");
        }
    }
}
