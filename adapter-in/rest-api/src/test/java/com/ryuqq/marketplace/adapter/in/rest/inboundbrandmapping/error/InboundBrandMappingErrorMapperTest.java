package com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import com.ryuqq.marketplace.domain.inboundbrandmapping.exception.InboundBrandMappingDuplicateException;
import com.ryuqq.marketplace.domain.inboundbrandmapping.exception.InboundBrandMappingErrorCode;
import com.ryuqq.marketplace.domain.inboundbrandmapping.exception.InboundBrandMappingNotFoundException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("InboundBrandMappingErrorMapper 단위 테스트")
class InboundBrandMappingErrorMapperTest {

    private final InboundBrandMappingErrorMapper sut = new InboundBrandMappingErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("InboundBrandMappingNotFoundException을 지원한다")
        void supports_InboundBrandMappingNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new InboundBrandMappingNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("InboundBrandMappingDuplicateException을 지원한다")
        void supports_InboundBrandMappingDuplicateException_ReturnsTrue() {
            // given
            DomainException ex = new InboundBrandMappingDuplicateException(1L, "NV_BRAND_001");

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
                            new ErrorCode() {
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
        @DisplayName("InboundBrandMappingNotFoundException을 404 MappedError로 변환한다")
        void map_NotFoundException_Returns404() {
            // given
            InboundBrandMappingNotFoundException ex = new InboundBrandMappingNotFoundException(1L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Inbound Brand Mapping Error");
            assertThat(result.type().toString()).startsWith("/errors/inbound-brand-mapping/");
            assertThat(result.type().toString())
                    .contains(
                            InboundBrandMappingErrorCode.EXTERNAL_BRAND_MAPPING_NOT_FOUND
                                    .getCode()
                                    .toLowerCase(Locale.ROOT));
        }

        @Test
        @DisplayName("InboundBrandMappingDuplicateException을 409 MappedError로 변환한다")
        void map_DuplicateException_Returns409() {
            // given
            InboundBrandMappingDuplicateException ex =
                    new InboundBrandMappingDuplicateException(1L, "NV_BRAND_001");

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("Inbound Brand Mapping Error");
            assertThat(result.type().toString()).startsWith("/errors/inbound-brand-mapping/");
            assertThat(result.type().toString())
                    .contains(
                            InboundBrandMappingErrorCode.EXTERNAL_BRAND_MAPPING_DUPLICATE
                                    .getCode()
                                    .toLowerCase(Locale.ROOT));
        }

        @Test
        @DisplayName("에러 타입 URI에 소문자 에러 코드가 포함된다")
        void map_TypeUriContainsLowercaseErrorCode() {
            // given
            InboundBrandMappingNotFoundException ex = new InboundBrandMappingNotFoundException(42L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString())
                    .isEqualTo(
                            "/errors/inbound-brand-mapping/"
                                    + InboundBrandMappingErrorCode.EXTERNAL_BRAND_MAPPING_NOT_FOUND
                                            .getCode()
                                            .toLowerCase(Locale.ROOT));
        }
    }
}
