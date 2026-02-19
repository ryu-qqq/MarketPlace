package com.ryuqq.marketplace.adapter.in.rest.category.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.category.exception.CategoryCodeDuplicateException;
import com.ryuqq.marketplace.domain.category.exception.CategoryNotFoundException;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("CategoryErrorMapper 단위 테스트")
class CategoryErrorMapperTest {

    private final CategoryErrorMapper sut = new CategoryErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("CategoryNotFoundException을 지원한다")
        void supports_CategoryNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new CategoryNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("CategoryCodeDuplicateException을 지원한다")
        void supports_CategoryCodeDuplicateException_ReturnsTrue() {
            // given
            DomainException ex = new CategoryCodeDuplicateException("CAT001");

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
        @DisplayName("CategoryNotFoundException을 404 MappedError로 변환한다")
        void map_NotFound_Returns404() {
            // given
            CategoryNotFoundException ex = new CategoryNotFoundException(1L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Category Error");
            assertThat(result.detail()).contains("1");
            assertThat(result.type().toString()).startsWith("/errors/category/");
        }

        @Test
        @DisplayName("CategoryCodeDuplicateException을 409 MappedError로 변환한다")
        void map_DuplicateCode_Returns409() {
            // given
            CategoryCodeDuplicateException ex = new CategoryCodeDuplicateException("CAT001");

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("Category Error");
            assertThat(result.type().toString()).contains("/errors/category/");
        }
    }
}
