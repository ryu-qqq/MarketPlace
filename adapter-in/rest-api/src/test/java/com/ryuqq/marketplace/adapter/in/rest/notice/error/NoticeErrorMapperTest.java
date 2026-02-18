package com.ryuqq.marketplace.adapter.in.rest.notice.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.notice.exception.NoticeCategoryNotFoundException;
import com.ryuqq.marketplace.domain.notice.exception.NoticeInvalidFieldException;
import com.ryuqq.marketplace.domain.notice.exception.NoticeRequiredFieldMissingException;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("NoticeErrorMapper 단위 테스트")
class NoticeErrorMapperTest {

    private final NoticeErrorMapper sut = new NoticeErrorMapper();

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("NoticeCategoryNotFoundException을 지원한다")
        void supports_NoticeCategoryNotFoundException_ReturnsTrue() {
            // given
            DomainException ex = new NoticeCategoryNotFoundException(1L);

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("NoticeInvalidFieldException을 지원한다")
        void supports_NoticeInvalidFieldException_ReturnsTrue() {
            // given
            DomainException ex = new NoticeInvalidFieldException(List.of(10L, 20L));

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("NoticeRequiredFieldMissingException을 지원한다")
        void supports_NoticeRequiredFieldMissingException_ReturnsTrue() {
            // given
            DomainException ex = new NoticeRequiredFieldMissingException(List.of(5L));

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
        @DisplayName("NoticeCategoryNotFoundException을 404 MappedError로 변환한다")
        void map_CategoryNotFound_Returns404() {
            // given
            NoticeCategoryNotFoundException ex = new NoticeCategoryNotFoundException(1L);

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Notice Error");
            assertThat(result.detail()).contains("1");
            assertThat(result.type().toString()).startsWith("/errors/notice/");
        }

        @Test
        @DisplayName("NoticeInvalidFieldException을 400 MappedError로 변환한다")
        void map_InvalidField_Returns400() {
            // given
            NoticeInvalidFieldException ex = new NoticeInvalidFieldException(List.of(10L, 20L));

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Notice Error");
            assertThat(result.type().toString()).contains("/errors/notice/");
        }

        @Test
        @DisplayName("NoticeRequiredFieldMissingException을 400 MappedError로 변환한다")
        void map_RequiredFieldMissing_Returns400() {
            // given
            NoticeRequiredFieldMissingException ex =
                    new NoticeRequiredFieldMissingException(List.of(5L));

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Notice Error");
            assertThat(result.type().toString()).contains("/errors/notice/");
        }
    }
}
