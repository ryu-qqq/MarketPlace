package com.ryuqq.marketplace.adapter.in.rest.brandpreset.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetChannelMismatchException;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetException;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetInternalBrandNotFoundException;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetNotFoundException;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetSalesChannelBrandNotFoundException;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("BrandPresetErrorMapper 단위 테스트")
class BrandPresetErrorMapperTest {

    private final BrandPresetErrorMapper sut = new BrandPresetErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("BrandPresetNotFoundException을 지원한다")
        void supports_BrandPresetNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new BrandPresetNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("BrandPresetChannelMismatchException을 지원한다")
        void supports_BrandPresetChannelMismatchException_ReturnsTrue() {
            // given
            DomainException ex = new BrandPresetChannelMismatchException(1L, 2L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("BrandPresetInternalBrandNotFoundException을 지원한다")
        void supports_BrandPresetInternalBrandNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new BrandPresetInternalBrandNotFoundException(List.of(1L, 2L));

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("BrandPresetSalesChannelBrandNotFoundException을 지원한다")
        void supports_BrandPresetSalesChannelBrandNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new BrandPresetSalesChannelBrandNotFoundException(1L);

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
        @DisplayName("BrandPresetNotFoundException을 404 MappedError로 변환한다")
        void map_BrandPresetNotFoundException_Returns404() {
            // given
            BrandPresetException ex = new BrandPresetNotFoundException(1L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Brand Preset Error");
            assertThat(result.type().toString()).startsWith("/errors/brand-preset/");
            assertThat(result.type().toString()).contains("brdpre-001");
        }

        @Test
        @DisplayName("BrandPresetChannelMismatchException을 400 MappedError로 변환한다")
        void map_BrandPresetChannelMismatchException_Returns400() {
            // given
            BrandPresetException ex = new BrandPresetChannelMismatchException(1L, 2L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Brand Preset Error");
            assertThat(result.type().toString()).startsWith("/errors/brand-preset/");
        }

        @Test
        @DisplayName("에러 타입 URI에 소문자 에러 코드가 포함된다")
        void map_BrandPresetNotFoundException_TypeUriContainsLowercaseCode() {
            // given
            BrandPresetException ex = new BrandPresetNotFoundException(42L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString()).isEqualTo("/errors/brand-preset/brdpre-001");
        }
    }
}
