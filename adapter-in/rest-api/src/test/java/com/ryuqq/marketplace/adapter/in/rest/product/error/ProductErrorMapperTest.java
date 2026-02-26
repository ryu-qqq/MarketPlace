package com.ryuqq.marketplace.adapter.in.rest.product.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import com.ryuqq.marketplace.domain.product.exception.ProductNotFoundException;
import com.ryuqq.marketplace.domain.product.exception.ProductOwnershipViolationException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("ProductErrorMapper 단위 테스트")
class ProductErrorMapperTest {

    private final ProductErrorMapper sut = new ProductErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("ProductNotFoundException을 지원한다")
        void supports_ProductNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new ProductNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("ProductOwnershipViolationException을 지원한다")
        void supports_ProductOwnershipViolationException_ReturnsTrue() {
            // given
            DomainException ex = new ProductOwnershipViolationException(1L, 5, 3);

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
        @DisplayName("ProductNotFoundException을 404 MappedError로 변환한다")
        void map_NotFound_Returns404() {
            // given
            ProductNotFoundException ex = new ProductNotFoundException(99L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Product Not Found");
            assertThat(result.detail()).contains("99");
            assertThat(result.type().toString()).startsWith("/errors/product/");
        }

        @Test
        @DisplayName("ProductOwnershipViolationException을 403 MappedError로 변환한다")
        void map_OwnershipViolation_Returns403() {
            // given
            ProductOwnershipViolationException ex =
                    new ProductOwnershipViolationException(1L, 5, 3);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(result.title()).isEqualTo("Product Ownership Violation");
            assertThat(result.type().toString()).contains("/errors/product/");
        }

        @Test
        @DisplayName("기타 PRD- 코드 예외는 기본 타이틀로 변환한다")
        void map_OtherProductError_ReturnsDefaultTitle() {
            // given
            DomainException ex =
                    new DomainException(
                            new ErrorCode() {
                                @Override
                                public String getCode() {
                                    return "PRD-999";
                                }

                                @Override
                                public int getHttpStatus() {
                                    return 400;
                                }

                                @Override
                                public String getMessage() {
                                    return "Unknown product error";
                                }
                            },
                            "Unknown product error") {};

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Product Error");
            assertThat(result.type().toString()).contains("/errors/product/prd-999");
        }
    }
}
