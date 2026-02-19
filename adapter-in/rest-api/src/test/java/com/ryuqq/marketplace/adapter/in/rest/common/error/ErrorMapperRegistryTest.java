package com.ryuqq.marketplace.adapter.in.rest.common.error;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.common.exception.ErrorCode;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@Tag("unit")
@DisplayName("ErrorMapperRegistry 단위 테스트")
class ErrorMapperRegistryTest {

    @Nested
    @DisplayName("map() - 매핑 매퍼 조회")
    class MapTest {

        @Test
        @DisplayName("매칭되는 ErrorMapper가 있으면 MappedError를 반환한다")
        void map_WithMatchingMapper_ReturnsMappedError() {
            // given
            ErrorMapper stubMapper =
                    new ErrorMapper() {
                        @Override
                        public boolean supports(DomainException ex) {
                            return ex.code().startsWith("TEST-");
                        }

                        @Override
                        public MappedError map(DomainException ex, Locale locale) {
                            return new MappedError(
                                    HttpStatus.NOT_FOUND,
                                    "Test Not Found",
                                    ex.getMessage(),
                                    URI.create("/errors/test"));
                        }
                    };
            ErrorMapperRegistry sut = new ErrorMapperRegistry(List.of(stubMapper));
            DomainException ex = createDomainException("TEST-001", 404, "test error");

            // when
            Optional<ErrorMapper.MappedError> result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.get().title()).isEqualTo("Test Not Found");
        }

        @Test
        @DisplayName("매칭되는 ErrorMapper가 없으면 empty를 반환한다")
        void map_WithNoMatchingMapper_ReturnsEmpty() {
            // given
            ErrorMapper stubMapper =
                    new ErrorMapper() {
                        @Override
                        public boolean supports(DomainException ex) {
                            return ex.code().startsWith("OTHER-");
                        }

                        @Override
                        public MappedError map(DomainException ex, Locale locale) {
                            return new MappedError(
                                    HttpStatus.BAD_REQUEST,
                                    "Other",
                                    ex.getMessage(),
                                    URI.create("/errors/other"));
                        }
                    };
            ErrorMapperRegistry sut = new ErrorMapperRegistry(List.of(stubMapper));
            DomainException ex = createDomainException("TEST-001", 404, "test error");

            // when
            Optional<ErrorMapper.MappedError> result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("매퍼 목록이 비어있으면 empty를 반환한다")
        void map_WithEmptyMappers_ReturnsEmpty() {
            // given
            ErrorMapperRegistry sut = new ErrorMapperRegistry(List.of());
            DomainException ex = createDomainException("TEST-001", 404, "test error");

            // when
            Optional<ErrorMapper.MappedError> result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("여러 매퍼 중 첫 번째로 매칭되는 매퍼를 사용한다")
        void map_MultipleMappers_UsesFirst() {
            // given
            ErrorMapper firstMapper =
                    new ErrorMapper() {
                        @Override
                        public boolean supports(DomainException ex) {
                            return true;
                        }

                        @Override
                        public MappedError map(DomainException ex, Locale locale) {
                            return new MappedError(
                                    HttpStatus.NOT_FOUND,
                                    "First",
                                    ex.getMessage(),
                                    URI.create("/errors/first"));
                        }
                    };
            ErrorMapper secondMapper =
                    new ErrorMapper() {
                        @Override
                        public boolean supports(DomainException ex) {
                            return true;
                        }

                        @Override
                        public MappedError map(DomainException ex, Locale locale) {
                            return new MappedError(
                                    HttpStatus.BAD_REQUEST,
                                    "Second",
                                    ex.getMessage(),
                                    URI.create("/errors/second"));
                        }
                    };
            ErrorMapperRegistry sut = new ErrorMapperRegistry(List.of(firstMapper, secondMapper));
            DomainException ex = createDomainException("TEST-001", 404, "test error");

            // when
            Optional<ErrorMapper.MappedError> result = sut.map(ex, Locale.KOREA);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().title()).isEqualTo("First");
        }
    }

    @Nested
    @DisplayName("defaultMapping() - 기본 매핑")
    class DefaultMappingTest {

        @Test
        @DisplayName("DomainException의 httpStatus를 기반으로 기본 MappedError를 생성한다")
        void defaultMapping_ReturnsMappedErrorFromException() {
            // given
            ErrorMapperRegistry sut = new ErrorMapperRegistry(List.of());
            DomainException ex = createDomainException("TEST-001", 404, "Not found");

            // when
            ErrorMapper.MappedError result = sut.defaultMapping(ex);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(result.title()).isEqualTo("Not Found");
            assertThat(result.detail()).isEqualTo("Not found");
            assertThat(result.type().toString()).isEqualTo("about:blank");
        }

        @Test
        @DisplayName("메시지가 null이면 기본 메시지를 사용한다")
        void defaultMapping_NullMessage_UsesDefault() {
            // given
            ErrorMapperRegistry sut = new ErrorMapperRegistry(List.of());
            DomainException ex =
                    new DomainException(
                            new ErrorCode() {
                                @Override
                                public String getCode() {
                                    return "TEST-002";
                                }

                                @Override
                                public int getHttpStatus() {
                                    return 400;
                                }

                                @Override
                                public String getMessage() {
                                    return null;
                                }
                            }) {
                        @Override
                        public String getMessage() {
                            return null;
                        }
                    };

            // when
            ErrorMapper.MappedError result = sut.defaultMapping(ex);

            // then
            assertThat(result.detail()).isEqualTo("Invalid request");
        }

        @Test
        @DisplayName("400 상태 코드에 대해 Bad Request 타이틀을 반환한다")
        void defaultMapping_400_ReturnsBadRequest() {
            // given
            ErrorMapperRegistry sut = new ErrorMapperRegistry(List.of());
            DomainException ex = createDomainException("TEST-003", 400, "Bad input");

            // when
            ErrorMapper.MappedError result = sut.defaultMapping(ex);

            // then
            assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(result.title()).isEqualTo("Bad Request");
        }
    }

    private static DomainException createDomainException(
            String code, int httpStatus, String message) {
        return new DomainException(
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
                        return message;
                    }
                },
                message) {};
    }
}
