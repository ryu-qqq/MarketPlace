package com.ryuqq.marketplace.adapter.in.rest.session.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("UploadSessionErrorMapper 단위 테스트")
class UploadSessionErrorMapperTest {

    private final UploadSessionErrorMapper sut = new UploadSessionErrorMapper();

    private static DomainException uploadSessionException(String code, int httpStatus, String msg) {
        ErrorCode errorCode =
                new ErrorCode() {
                    @Override
                    public String getCode() {
                        return code;
                    }

                    @Override
                    public int getHttpStatus() {
                        return httpStatus;
                    }

                    @Override
                    public String getMessage() {
                        return msg;
                    }
                };
        return new DomainException(errorCode) {};
    }

    @Nested
    @DisplayName("supports() - 예외 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("UPLOAD_SESSION- 접두사를 가진 예외를 지원한다")
        void supports_UploadSessionException_ReturnsTrue() {
            // given
            DomainException ex = uploadSessionException("UPLOAD_SESSION-001", 400, "업로드 세션 오류");

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("UPLOAD_SESSION-002 코드를 가진 예외를 지원한다")
        void supports_UploadSessionExpired_ReturnsTrue() {
            // given
            DomainException ex =
                    uploadSessionException("UPLOAD_SESSION-002", 404, "업로드 세션을 찾을 수 없습니다");

            // when
            boolean result = sut.supports(ex);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("UPLOAD_SESSION- 접두사가 없는 예외는 지원하지 않는다")
        void supports_NonUploadSessionException_ReturnsFalse() {
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

        @Test
        @DisplayName("UPLOAD- 접두사만 가진 예외는 지원하지 않는다")
        void supports_PartialPrefixException_ReturnsFalse() {
            // given
            DomainException ex = uploadSessionException("UPLOAD-001", 400, "업로드 오류");

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
        @DisplayName("UPLOAD_SESSION-001 예외를 400 MappedError로 변환한다")
        void map_UploadSessionException_Returns400() {
            // given
            DomainException ex = uploadSessionException("UPLOAD_SESSION-001", 400, "업로드 세션 오류");

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Upload Session Error");
            assertThat(result.type().toString()).startsWith("/errors/upload-session/");
            assertThat(result.type().toString()).contains("upload_session-001");
        }

        @Test
        @DisplayName("UPLOAD_SESSION-002 예외를 404 MappedError로 변환한다")
        void map_UploadSessionNotFound_Returns404() {
            // given
            DomainException ex =
                    uploadSessionException("UPLOAD_SESSION-002", 404, "업로드 세션을 찾을 수 없습니다");

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Upload Session Error");
            assertThat(result.type().toString()).startsWith("/errors/upload-session/");
        }

        @Test
        @DisplayName("에러 타입 URI에 소문자 에러 코드가 포함된다")
        void map_UploadSessionException_TypeUriContainsLowercaseCode() {
            // given
            DomainException ex = uploadSessionException("UPLOAD_SESSION-001", 400, "업로드 세션 오류");

            // when
            ErrorMapper.MappedError result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result.type().toString())
                    .isEqualTo("/errors/upload-session/upload_session-001");
        }
    }
}
