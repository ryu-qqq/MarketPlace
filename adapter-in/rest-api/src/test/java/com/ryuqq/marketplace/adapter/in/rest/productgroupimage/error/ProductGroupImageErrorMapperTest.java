package com.ryuqq.marketplace.adapter.in.rest.productgroupimage.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import com.ryuqq.marketplace.domain.productgroup.exception.DescriptionImageNotFoundException;
import com.ryuqq.marketplace.domain.productgroupimage.exception.ProductGroupImageNotFoundException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("ProductGroupImageErrorMapper 단위 테스트")
class ProductGroupImageErrorMapperTest {

    private final ProductGroupImageErrorMapper sut = new ProductGroupImageErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("ProductGroupImageNotFoundException을 지원한다")
        void supports_ProductGroupImageNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new ProductGroupImageNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("DescriptionImageNotFoundException을 지원한다")
        void supports_DescriptionImageNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new DescriptionImageNotFoundException(1L);

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
        @DisplayName("ProductGroupImageNotFoundException을 404 MappedError로 변환한다")
        void map_ProductGroupImageNotFound_Returns404() {
            // given
            ProductGroupImageNotFoundException ex = new ProductGroupImageNotFoundException(10L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Product Group Image Not Found");
            assertThat(result.detail()).contains("10");
            assertThat(result.type().toString()).startsWith("/errors/product-group-image/");
        }

        @Test
        @DisplayName("DescriptionImageNotFoundException을 404 MappedError로 변환한다")
        void map_DescriptionImageNotFound_Returns404() {
            // given
            DescriptionImageNotFoundException ex = new DescriptionImageNotFoundException(20L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Description Image Not Found");
            assertThat(result.detail()).contains("20");
            assertThat(result.type().toString()).startsWith("/errors/product-group-image/");
        }
    }
}
