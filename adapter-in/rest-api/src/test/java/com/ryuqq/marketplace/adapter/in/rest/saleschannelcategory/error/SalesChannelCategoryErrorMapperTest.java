package com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.saleschannelcategory.exception.SalesChannelCategoryCodeDuplicateException;
import com.ryuqq.marketplace.domain.saleschannelcategory.exception.SalesChannelCategoryException;
import com.ryuqq.marketplace.domain.saleschannelcategory.exception.SalesChannelCategoryNotFoundException;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("SalesChannelCategoryErrorMapper 단위 테스트")
class SalesChannelCategoryErrorMapperTest {

    private final SalesChannelCategoryErrorMapper sut = new SalesChannelCategoryErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("SalesChannelCategoryNotFoundException을 지원한다")
        void supports_SalesChannelCategoryNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new SalesChannelCategoryNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("SalesChannelCategoryCodeDuplicateException을 지원한다")
        void supports_SalesChannelCategoryCodeDuplicateException_ReturnsTrue() {
            // given
            DomainException ex = new SalesChannelCategoryCodeDuplicateException("CAT-CODE");

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
        @DisplayName("SalesChannelCategoryNotFoundException을 404 MappedError로 변환한다")
        void map_SalesChannelCategoryNotFoundException_Returns404() {
            // given
            SalesChannelCategoryException ex = new SalesChannelCategoryNotFoundException(1L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Sales Channel Category Error");
            assertThat(result.type().toString()).startsWith("/errors/sales-channel-category/");
            assertThat(result.type().toString()).contains("sccat-001");
        }

        @Test
        @DisplayName("SalesChannelCategoryCodeDuplicateException을 409 MappedError로 변환한다")
        void map_SalesChannelCategoryCodeDuplicateException_Returns409() {
            // given
            SalesChannelCategoryException ex =
                    new SalesChannelCategoryCodeDuplicateException("CAT-CODE");

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(result.title()).isEqualTo("Sales Channel Category Error");
            assertThat(result.type().toString()).startsWith("/errors/sales-channel-category/");
        }

        @Test
        @DisplayName("에러 타입 URI에 소문자 에러 코드가 포함된다")
        void map_SalesChannelCategoryNotFoundException_TypeUriContainsLowercaseCode() {
            // given
            SalesChannelCategoryException ex = new SalesChannelCategoryNotFoundException(42L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString())
                    .isEqualTo("/errors/sales-channel-category/sccat-001");
        }
    }
}
