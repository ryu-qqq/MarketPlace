package com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupDescriptionNotFoundException;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("ProductGroupDescriptionErrorMapper 단위 테스트")
class ProductGroupDescriptionErrorMapperTest {

    private final ProductGroupDescriptionErrorMapper sut = new ProductGroupDescriptionErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("ProductGroupDescriptionNotFoundException을 지원한다")
        void supports_ProductGroupDescriptionNotFoundException_ReturnsTrue() {
            // given
            DomainException ex =
                    new ProductGroupDescriptionNotFoundException(ProductGroupId.of(1L));

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
        @DisplayName("ProductGroupDescriptionNotFoundException을 404 MappedError로 변환한다")
        void map_NotFound_Returns404() {
            // given
            ProductGroupDescriptionNotFoundException ex =
                    new ProductGroupDescriptionNotFoundException(ProductGroupId.of(5L));

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Product Group Description Not Found");
            assertThat(result.detail()).contains("5");
            assertThat(result.type().toString()).startsWith("/errors/product-group-description/");
        }
    }
}
