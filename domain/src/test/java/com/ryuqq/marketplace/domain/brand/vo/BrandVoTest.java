package com.ryuqq.marketplace.domain.brand.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.brand.BrandFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Brand Value Objects 단위 테스트")
class BrandVoTest {

    @Nested
    @DisplayName("BrandCode 테스트")
    class BrandCodeTest {
        @Nested
        @DisplayName("생성 테스트")
        class CreationTest {
            @Test
            @DisplayName("유효한 값으로 생성한다")
            void createWithValidValue() {
                // given
                String value = "VALID_CODE";

                // when
                BrandCode brandCode = BrandCode.of(value);

                // then
                assertThat(brandCode).isNotNull();
                assertThat(brandCode.value()).isEqualTo(value);
            }

            @Test
            @DisplayName("null 값으로 생성하면 예외가 발생한다")
            void createWithNullValue_ThrowsException() {
                // when & then
                assertThatThrownBy(() -> BrandCode.of(null))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("브랜드 코드는 필수입니다");
            }

            @Test
            @DisplayName("빈 문자열로 생성하면 예외가 발생한다")
            void createWithEmptyValue_ThrowsException() {
                // when & then
                assertThatThrownBy(() -> BrandCode.of(""))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("브랜드 코드는 필수입니다");
            }

            @Test
            @DisplayName("공백 문자열로 생성하면 예외가 발생한다")
            void createWithBlankValue_ThrowsException() {
                // when & then
                assertThatThrownBy(() -> BrandCode.of("   "))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("브랜드 코드는 필수입니다");
            }

            @Test
            @DisplayName("100자를 초과하면 예외가 발생한다")
            void createWithTooLongValue_ThrowsException() {
                // given
                String longValue = "A".repeat(101);

                // when & then
                assertThatThrownBy(() -> BrandCode.of(longValue))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("브랜드 코드는 100자 이내여야 합니다");
            }

            @Test
            @DisplayName("공백이 포함된 값은 trim된다")
            void createWithWhitespace_IsTrimmed() {
                // given
                String value = "  CODE  ";

                // when
                BrandCode brandCode = BrandCode.of(value);

                // then
                assertThat(brandCode.value()).isEqualTo("CODE");
            }
        }

        @Nested
        @DisplayName("동등성 테스트")
        class EqualityTest {
            @Test
            @DisplayName("같은 값이면 동일하다")
            void sameValueAreEqual() {
                // given
                BrandCode code1 = BrandCode.of("CODE");
                BrandCode code2 = BrandCode.of("CODE");

                // when & then
                assertThat(code1).isEqualTo(code2);
                assertThat(code1.hashCode()).isEqualTo(code2.hashCode());
            }

            @Test
            @DisplayName("다른 값이면 다르다")
            void differentValueAreNotEqual() {
                // given
                BrandCode code1 = BrandCode.of("CODE1");
                BrandCode code2 = BrandCode.of("CODE2");

                // when & then
                assertThat(code1).isNotEqualTo(code2);
            }
        }
    }

    @Nested
    @DisplayName("BrandName 테스트")
    class BrandNameTest {
        @Nested
        @DisplayName("생성 테스트")
        class CreationTest {
            @Test
            @DisplayName("모든 필드가 유효한 값으로 생성한다")
            void createWithAllValidValues() {
                // given
                String nameKo = "나이키";
                String nameEn = "Nike";
                String shortName = "나";

                // when
                BrandName brandName = BrandName.of(nameKo, nameEn, shortName);

                // then
                assertThat(brandName).isNotNull();
                assertThat(brandName.nameKo()).isEqualTo(nameKo);
                assertThat(brandName.nameEn()).isEqualTo(nameEn);
                assertThat(brandName.shortName()).isEqualTo(shortName);
            }

            @Test
            @DisplayName("모든 필드가 null이어도 생성할 수 있다")
            void createWithAllNullValues() {
                // when
                BrandName brandName = BrandName.of(null, null, null);

                // then
                assertThat(brandName).isNotNull();
                assertThat(brandName.nameKo()).isNull();
                assertThat(brandName.nameEn()).isNull();
                assertThat(brandName.shortName()).isNull();
            }

            @Test
            @DisplayName("empty()로 모든 필드가 null인 BrandName을 생성한다")
            void emptyCreatesAllNullValues() {
                // when
                BrandName brandName = BrandName.empty();

                // then
                assertThat(brandName.nameKo()).isNull();
                assertThat(brandName.nameEn()).isNull();
                assertThat(brandName.shortName()).isNull();
            }

            @Test
            @DisplayName("한글명이 255자를 초과하면 예외가 발생한다")
            void createWithTooLongNameKo_ThrowsException() {
                // given
                String longNameKo = "가".repeat(256);

                // when & then
                assertThatThrownBy(() -> BrandName.of(longNameKo, null, null))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("한글 브랜드명은 255자 이내여야 합니다");
            }

            @Test
            @DisplayName("영문명이 255자를 초과하면 예외가 발생한다")
            void createWithTooLongNameEn_ThrowsException() {
                // given
                String longNameEn = "A".repeat(256);

                // when & then
                assertThatThrownBy(() -> BrandName.of(null, longNameEn, null))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("영문 브랜드명은 255자 이내여야 합니다");
            }

            @Test
            @DisplayName("약칭이 100자를 초과하면 예외가 발생한다")
            void createWithTooLongShortName_ThrowsException() {
                // given
                String longShortName = "가".repeat(101);

                // when & then
                assertThatThrownBy(() -> BrandName.of(null, null, longShortName))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("브랜드 약칭은 100자 이내여야 합니다");
            }

            @Test
            @DisplayName("공백이 포함된 값은 trim된다")
            void createWithWhitespace_IsTrimmed() {
                // given
                String nameKo = "  나이키  ";
                String nameEn = "  Nike  ";
                String shortName = "  나  ";

                // when
                BrandName brandName = BrandName.of(nameKo, nameEn, shortName);

                // then
                assertThat(brandName.nameKo()).isEqualTo("나이키");
                assertThat(brandName.nameEn()).isEqualTo("Nike");
                assertThat(brandName.shortName()).isEqualTo("나");
            }
        }

        @Nested
        @DisplayName("동등성 테스트")
        class EqualityTest {
            @Test
            @DisplayName("같은 값이면 동일하다")
            void sameValueAreEqual() {
                // given
                BrandName name1 = BrandName.of("나이키", "Nike", "나");
                BrandName name2 = BrandName.of("나이키", "Nike", "나");

                // when & then
                assertThat(name1).isEqualTo(name2);
                assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
            }

            @Test
            @DisplayName("다른 값이면 다르다")
            void differentValueAreNotEqual() {
                // given
                BrandName name1 = BrandName.of("나이키", "Nike", null);
                BrandName name2 = BrandName.of("아디다스", "Adidas", null);

                // when & then
                assertThat(name1).isNotEqualTo(name2);
            }
        }
    }

    @Nested
    @DisplayName("BrandStatus 테스트")
    class BrandStatusTest {
        @Test
        @DisplayName("ACTIVE 상태는 isActive()가 true다")
        void activeStatusIsActive() {
            // given
            BrandStatus status = BrandStatus.ACTIVE;

            // when
            boolean isActive = status.isActive();

            // then
            assertThat(isActive).isTrue();
        }

        @Test
        @DisplayName("INACTIVE 상태는 isActive()가 false다")
        void inactiveStatusIsNotActive() {
            // given
            BrandStatus status = BrandStatus.INACTIVE;

            // when
            boolean isActive = status.isActive();

            // then
            assertThat(isActive).isFalse();
        }

        @Test
        @DisplayName("fromString()은 대소문자 구분 없이 변환한다")
        void fromStringIsCaseInsensitive() {
            // when & then
            assertThat(BrandStatus.fromString("active")).isEqualTo(BrandStatus.ACTIVE);
            assertThat(BrandStatus.fromString("ACTIVE")).isEqualTo(BrandStatus.ACTIVE);
            assertThat(BrandStatus.fromString("Active")).isEqualTo(BrandStatus.ACTIVE);
            assertThat(BrandStatus.fromString("inactive")).isEqualTo(BrandStatus.INACTIVE);
            assertThat(BrandStatus.fromString("INACTIVE")).isEqualTo(BrandStatus.INACTIVE);
        }

        @Test
        @DisplayName("fromString()은 null이면 ACTIVE를 반환한다")
        void fromStringReturnsActiveWhenNull() {
            // when
            BrandStatus status = BrandStatus.fromString(null);

            // then
            assertThat(status).isEqualTo(BrandStatus.ACTIVE);
        }

        @Test
        @DisplayName("fromString()은 빈 문자열이면 ACTIVE를 반환한다")
        void fromStringReturnsActiveWhenBlank() {
            // when
            BrandStatus status = BrandStatus.fromString("  ");

            // then
            assertThat(status).isEqualTo(BrandStatus.ACTIVE);
        }

        @Test
        @DisplayName("fromString()은 잘못된 값이면 ACTIVE를 반환한다")
        void fromStringReturnsActiveWhenInvalid() {
            // when
            BrandStatus status = BrandStatus.fromString("INVALID");

            // then
            assertThat(status).isEqualTo(BrandStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("LogoUrl 테스트")
    class LogoUrlTest {
        @Nested
        @DisplayName("생성 테스트")
        class CreationTest {
            @Test
            @DisplayName("유효한 HTTP URL로 생성한다")
            void createWithValidHttpUrl() {
                // given
                String url = "http://example.com/logo.png";

                // when
                LogoUrl logoUrl = LogoUrl.of(url);

                // then
                assertThat(logoUrl).isNotNull();
                assertThat(logoUrl.value()).isEqualTo(url);
                assertThat(logoUrl.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("유효한 HTTPS URL로 생성한다")
            void createWithValidHttpsUrl() {
                // given
                String url = "https://example.com/logo.png";

                // when
                LogoUrl logoUrl = LogoUrl.of(url);

                // then
                assertThat(logoUrl.value()).isEqualTo(url);
            }

            @Test
            @DisplayName("null 값으로 생성하면 빈 LogoUrl이 된다")
            void createWithNullValue() {
                // when
                LogoUrl logoUrl = LogoUrl.of(null);

                // then
                assertThat(logoUrl).isNotNull();
                assertThat(logoUrl.value()).isNull();
                assertThat(logoUrl.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("empty()로 빈 LogoUrl을 생성한다")
            void emptyCreatesEmptyLogoUrl() {
                // when
                LogoUrl logoUrl = LogoUrl.empty();

                // then
                assertThat(logoUrl.value()).isNull();
                assertThat(logoUrl.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("빈 문자열은 null로 변환된다")
            void createWithEmptyString_BecomesNull() {
                // when
                LogoUrl logoUrl = LogoUrl.of("");

                // then
                assertThat(logoUrl.value()).isNull();
                assertThat(logoUrl.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("공백 문자열은 null로 변환된다")
            void createWithBlankString_BecomesNull() {
                // when
                LogoUrl logoUrl = LogoUrl.of("   ");

                // then
                assertThat(logoUrl.value()).isNull();
                assertThat(logoUrl.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("500자를 초과하면 예외가 발생한다")
            void createWithTooLongValue_ThrowsException() {
                // given
                String longUrl = "https://example.com/" + "a".repeat(500);

                // when & then
                assertThatThrownBy(() -> LogoUrl.of(longUrl))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("로고 URL은 500자를 초과할 수 없습니다");
            }

            @Test
            @DisplayName("유효하지 않은 URL 형식이면 예외가 발생한다")
            void createWithInvalidUrlFormat_ThrowsException() {
                // when & then
                assertThatThrownBy(() -> LogoUrl.of("invalid-url"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("유효하지 않은 URL 형식입니다");
            }

            @Test
            @DisplayName("http/https가 아닌 프로토콜이면 예외가 발생한다")
            void createWithInvalidProtocol_ThrowsException() {
                // when & then
                assertThatThrownBy(() -> LogoUrl.of("ftp://example.com/logo.png"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("유효하지 않은 URL 형식입니다");
            }

            @Test
            @DisplayName("공백이 포함된 값은 trim된다")
            void createWithWhitespace_IsTrimmed() {
                // given
                String url = "  https://example.com/logo.png  ";

                // when
                LogoUrl logoUrl = LogoUrl.of(url);

                // then
                assertThat(logoUrl.value()).isEqualTo("https://example.com/logo.png");
            }
        }

        @Nested
        @DisplayName("동등성 테스트")
        class EqualityTest {
            @Test
            @DisplayName("같은 값이면 동일하다")
            void sameValueAreEqual() {
                // given
                LogoUrl url1 = LogoUrl.of("https://example.com/logo.png");
                LogoUrl url2 = LogoUrl.of("https://example.com/logo.png");

                // when & then
                assertThat(url1).isEqualTo(url2);
                assertThat(url1.hashCode()).isEqualTo(url2.hashCode());
            }

            @Test
            @DisplayName("다른 값이면 다르다")
            void differentValueAreNotEqual() {
                // given
                LogoUrl url1 = LogoUrl.of("https://example.com/logo1.png");
                LogoUrl url2 = LogoUrl.of("https://example.com/logo2.png");

                // when & then
                assertThat(url1).isNotEqualTo(url2);
            }

            @Test
            @DisplayName("둘 다 null이면 동일하다")
            void bothNullAreEqual() {
                // given
                LogoUrl url1 = LogoUrl.empty();
                LogoUrl url2 = LogoUrl.empty();

                // when & then
                assertThat(url1).isEqualTo(url2);
                assertThat(url1.hashCode()).isEqualTo(url2.hashCode());
            }
        }

        @Nested
        @DisplayName("isEmpty() 메서드 테스트")
        class IsEmptyTest {
            @Test
            @DisplayName("값이 null이면 isEmpty()는 true다")
            void isEmptyReturnsTrueWhenNull() {
                // given
                LogoUrl logoUrl = LogoUrl.empty();

                // when
                boolean isEmpty = logoUrl.isEmpty();

                // then
                assertThat(isEmpty).isTrue();
            }

            @Test
            @DisplayName("값이 존재하면 isEmpty()는 false다")
            void isEmptyReturnsFalseWhenValueExists() {
                // given
                LogoUrl logoUrl = BrandFixtures.defaultLogoUrl();

                // when
                boolean isEmpty = logoUrl.isEmpty();

                // then
                assertThat(isEmpty).isFalse();
            }
        }
    }
}
