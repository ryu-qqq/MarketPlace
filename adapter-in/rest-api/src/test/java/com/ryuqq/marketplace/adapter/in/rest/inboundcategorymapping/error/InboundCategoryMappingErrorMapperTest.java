package com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import com.ryuqq.marketplace.domain.inboundcategorymapping.exception.InboundCategoryMappingDuplicateException;
import com.ryuqq.marketplace.domain.inboundcategorymapping.exception.InboundCategoryMappingErrorCode;
import com.ryuqq.marketplace.domain.inboundcategorymapping.exception.InboundCategoryMappingNotFoundException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("InboundCategoryMappingErrorMapper 단위 테스트")
class InboundCategoryMappingErrorMapperTest {

    private final InboundCategoryMappingErrorMapper sut = new InboundCategoryMappingErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("InboundCategoryMappingNotFoundException을 지원한다")
        void supports_InboundCategoryMappingNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new InboundCategoryMappingNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("InboundCategoryMappingDuplicateException을 지원한다")
        void supports_InboundCategoryMappingDuplicateException_ReturnsTrue() {
            // given
            DomainException ex = new InboundCategoryMappingDuplicateException(1L, "NV_CAT_001");

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
        @DisplayName("InboundCategoryMappingNotFoundException을 404 MappedError로 변환한다")
        void map_NotFoundException_Returns404() {
            // given
            InboundCategoryMappingNotFoundException ex =
                    new InboundCategoryMappingNotFoundException(1L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Inbound Category Mapping Error");
            assertThat(result.type().toString()).startsWith("/errors/inbound-category-mapping/");
            assertThat(result.type().toString())
                    .contains(
                            InboundCategoryMappingErrorCode.EXTERNAL_CATEGORY_MAPPING_NOT_FOUND
                                    .getCode()
                                    .toLowerCase(Locale.ROOT));
        }

        @Test
        @DisplayName("InboundCategoryMappingDuplicateException을 409 MappedError로 변환한다")
        void map_DuplicateException_Returns409() {
            // given
            InboundCategoryMappingDuplicateException ex =
                    new InboundCategoryMappingDuplicateException(1L, "NV_CAT_001");

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("Inbound Category Mapping Error");
            assertThat(result.type().toString()).startsWith("/errors/inbound-category-mapping/");
            assertThat(result.type().toString())
                    .contains(
                            InboundCategoryMappingErrorCode.EXTERNAL_CATEGORY_MAPPING_DUPLICATE
                                    .getCode()
                                    .toLowerCase(Locale.ROOT));
        }

        @Test
        @DisplayName("에러 타입 URI에 소문자 에러 코드가 포함된다")
        void map_TypeUriContainsLowercaseErrorCode() {
            // given
            InboundCategoryMappingNotFoundException ex =
                    new InboundCategoryMappingNotFoundException(42L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString())
                    .isEqualTo(
                            "/errors/inbound-category-mapping/"
                                    + InboundCategoryMappingErrorCode
                                            .EXTERNAL_CATEGORY_MAPPING_NOT_FOUND
                                            .getCode()
                                            .toLowerCase(Locale.ROOT));
        }
    }
}
