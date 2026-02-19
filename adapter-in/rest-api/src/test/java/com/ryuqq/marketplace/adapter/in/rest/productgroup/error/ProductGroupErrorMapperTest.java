package com.ryuqq.marketplace.adapter.in.rest.productgroup.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.productgroup.exception.DescriptionImageNotFoundException;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupDescriptionNotFoundException;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupErrorCode;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupNotFoundException;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupOwnershipViolationException;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupimage.exception.ProductGroupImageNotFoundException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("ProductGroupErrorMapper 단위 테스트")
class ProductGroupErrorMapperTest {

    private final ProductGroupErrorMapper sut = new ProductGroupErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("PRDGRP- 코드를 가진 ProductGroupNotFoundException을 지원한다")
        void supports_ProductGroupNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new ProductGroupNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("PRDGRP- 코드를 가진 ProductGroupOwnershipViolationException을 지원한다")
        void supports_ProductGroupOwnershipViolationException_ReturnsTrue() {
            // given
            DomainException ex = new ProductGroupOwnershipViolationException(1L, 3, 1);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("ProductGroupImageNotFoundException은 지원하지 않는다")
        void supports_ProductGroupImageNotFoundException_ReturnsFalse() {
            // given
            DomainException ex = new ProductGroupImageNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("DescriptionImageNotFoundException은 지원하지 않는다")
        void supports_DescriptionImageNotFoundException_ReturnsFalse() {
            // given
            DomainException ex = new DescriptionImageNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("ProductGroupDescriptionNotFoundException은 지원하지 않는다")
        void supports_ProductGroupDescriptionNotFoundException_ReturnsFalse() {
            // given
            DomainException ex =
                    new ProductGroupDescriptionNotFoundException(ProductGroupId.of(1L));

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isFalse();
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
        @DisplayName("ProductGroupNotFoundException을 404 MappedError로 변환한다")
        void map_ProductGroupNotFoundException_Returns404() {
            // given
            ProductGroupNotFoundException ex = new ProductGroupNotFoundException(1L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Product Group Not Found");
            assertThat(result.type().toString()).startsWith("/errors/product-group/");
            assertThat(result.type().toString())
                    .contains(
                            ProductGroupErrorCode.PRODUCT_GROUP_NOT_FOUND
                                    .getCode()
                                    .toLowerCase(Locale.ROOT));
        }

        @Test
        @DisplayName("ProductGroupOwnershipViolationException을 403 MappedError로 변환한다")
        void map_ProductGroupOwnershipViolationException_Returns403() {
            // given
            ProductGroupOwnershipViolationException ex =
                    new ProductGroupOwnershipViolationException(1L, 3, 1);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(result.title()).isEqualTo("Product Group Ownership Violation");
            assertThat(result.type().toString()).startsWith("/errors/product-group/");
        }

        @Test
        @DisplayName("그 외 PRDGRP 예외는 'Product Group Error' 제목으로 변환된다")
        void map_OtherProductGroupException_ReturnsGenericTitle() {
            // given
            DomainException ex =
                    new DomainException(
                            ProductGroupErrorCode.PRODUCT_GROUP_INVALID_STATUS_TRANSITION) {};

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Product Group Error");
            assertThat(result.type().toString()).startsWith("/errors/product-group/");
        }

        @Test
        @DisplayName("에러 타입 URI에 소문자 에러 코드가 포함된다")
        void map_ProductGroupNotFoundException_TypeUriContainsLowercaseCode() {
            // given
            ProductGroupNotFoundException ex = new ProductGroupNotFoundException(42L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString())
                    .isEqualTo(
                            "/errors/product-group/"
                                    + ProductGroupErrorCode.PRODUCT_GROUP_NOT_FOUND
                                            .getCode()
                                            .toLowerCase(Locale.ROOT));
        }
    }
}
