package com.ryuqq.marketplace.adapter.in.rest.categorypreset.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetChannelMismatchException;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetException;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetInternalCategoryNotFoundException;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetNotFoundException;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetSalesChannelCategoryNotFoundException;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("CategoryPresetErrorMapper 단위 테스트")
class CategoryPresetErrorMapperTest {

    private final CategoryPresetErrorMapper sut = new CategoryPresetErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("CategoryPresetNotFoundException을 지원한다")
        void supports_CategoryPresetNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new CategoryPresetNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("CategoryPresetChannelMismatchException을 지원한다")
        void supports_CategoryPresetChannelMismatchException_ReturnsTrue() {
            // given
            DomainException ex = new CategoryPresetChannelMismatchException(1L, 2L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("CategoryPresetInternalCategoryNotFoundException을 지원한다")
        void supports_CategoryPresetInternalCategoryNotFoundException_ReturnsTrue() {
            // given
            DomainException ex =
                    new CategoryPresetInternalCategoryNotFoundException(List.of(1L, 2L));

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("CategoryPresetSalesChannelCategoryNotFoundException을 지원한다")
        void supports_CategoryPresetSalesChannelCategoryNotFoundException_ReturnsTrue() {
            // given
            DomainException ex =
                    new CategoryPresetSalesChannelCategoryNotFoundException("TEST-CODE");

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
        @DisplayName("CategoryPresetNotFoundException을 404 MappedError로 변환한다")
        void map_CategoryPresetNotFoundException_Returns404() {
            // given
            CategoryPresetException ex = new CategoryPresetNotFoundException(1L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Category Preset Error");
            assertThat(result.type().toString()).startsWith("/errors/category-preset/");
            assertThat(result.type().toString()).contains("catpre-001");
        }

        @Test
        @DisplayName("CategoryPresetChannelMismatchException을 400 MappedError로 변환한다")
        void map_CategoryPresetChannelMismatchException_Returns400() {
            // given
            CategoryPresetException ex = new CategoryPresetChannelMismatchException(1L, 2L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Category Preset Error");
            assertThat(result.type().toString()).startsWith("/errors/category-preset/");
        }

        @Test
        @DisplayName("에러 타입 URI에 소문자 에러 코드가 포함된다")
        void map_CategoryPresetNotFoundException_TypeUriContainsLowercaseCode() {
            // given
            CategoryPresetException ex = new CategoryPresetNotFoundException(42L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString()).isEqualTo("/errors/category-preset/catpre-001");
        }
    }
}
