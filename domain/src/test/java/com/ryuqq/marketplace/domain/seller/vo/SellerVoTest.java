package com.ryuqq.marketplace.domain.seller.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Seller 도메인 Value Object 테스트")
class SellerVoTest {

    @Nested
    @DisplayName("SellerId 테스트")
    class SellerIdTest {

        @Test
        @DisplayName("유효한 값으로 SellerId를 생성한다")
        void createSellerIdWithValidValue() {
            // when
            SellerId sellerId = SellerId.of(1L);

            // then
            assertThat(sellerId.value()).isEqualTo(1L);
            assertThat(sellerId.isNew()).isFalse();
        }

        @Test
        @DisplayName("forNew()로 새로운 SellerId를 생성한다")
        void createNewSellerId() {
            // when
            SellerId sellerId = SellerId.forNew();

            // then
            assertThat(sellerId.value()).isNull();
            assertThat(sellerId.isNew()).isTrue();
        }

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void createWithNullThrowsException() {
            // when & then
            assertThatThrownBy(() -> SellerId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }

    @Nested
    @DisplayName("SellerName 테스트")
    class SellerNameTest {

        @Test
        @DisplayName("유효한 셀러명을 생성한다")
        void createValidSellerName() {
            // when
            SellerName name = SellerName.of("테스트 셀러");

            // then
            assertThat(name.value()).isEqualTo("테스트 셀러");
        }

        @Test
        @DisplayName("공백이 트림된다")
        void valueIsTrimmed() {
            // when
            SellerName name = SellerName.of("  테스트 셀러  ");

            // then
            assertThat(name.value()).isEqualTo("테스트 셀러");
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void nullThrowsException() {
            // when & then
            assertThatThrownBy(() -> SellerName.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void emptyStringThrowsException() {
            // when & then
            assertThatThrownBy(() -> SellerName.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("공백만 있으면 예외가 발생한다")
        void blankStringThrowsException() {
            // when & then
            assertThatThrownBy(() -> SellerName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("100자를 초과하면 예외가 발생한다")
        void tooLongThrowsException() {
            // given
            String longName = "a".repeat(101);

            // when & then
            assertThatThrownBy(() -> SellerName.of(longName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("100자");
        }
    }

    @Nested
    @DisplayName("DisplayName 테스트")
    class SellerDisplayNameTest {

        @Test
        @DisplayName("유효한 노출명을 생성한다")
        void createValidDisplayName() {
            // when
            var displayName = com.ryuqq.marketplace.domain.seller.vo.DisplayName.of("테스트 스토어");

            // then
            assertThat(displayName.value()).isEqualTo("테스트 스토어");
        }

        @Test
        @DisplayName("공백이 트림된다")
        void valueIsTrimmed() {
            // when
            var displayName = com.ryuqq.marketplace.domain.seller.vo.DisplayName.of("  테스트 스토어  ");

            // then
            assertThat(displayName.value()).isEqualTo("테스트 스토어");
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void nullThrowsException() {
            // when & then
            assertThatThrownBy(() -> com.ryuqq.marketplace.domain.seller.vo.DisplayName.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }
    }

    @Nested
    @DisplayName("LogoUrl 테스트")
    class LogoUrlTest {

        @Test
        @DisplayName("유효한 URL로 LogoUrl을 생성한다")
        void createValidLogoUrl() {
            // when
            LogoUrl logoUrl = LogoUrl.of("https://example.com/logo.png");

            // then
            assertThat(logoUrl.value()).isEqualTo("https://example.com/logo.png");
            assertThat(logoUrl.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("http URL도 허용된다")
        void httpUrlIsAllowed() {
            // when
            LogoUrl logoUrl = LogoUrl.of("http://example.com/logo.png");

            // then
            assertThat(logoUrl.value()).startsWith("http://");
        }

        @Test
        @DisplayName("null로 빈 LogoUrl을 생성한다")
        void nullCreatesEmptyLogoUrl() {
            // when
            LogoUrl logoUrl = LogoUrl.of(null);

            // then
            assertThat(logoUrl.isEmpty()).isTrue();
            assertThat(logoUrl.value()).isNull();
        }

        @Test
        @DisplayName("empty()로 빈 LogoUrl을 생성한다")
        void emptyCreatesEmptyLogoUrl() {
            // when
            LogoUrl logoUrl = LogoUrl.empty();

            // then
            assertThat(logoUrl.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("빈 문자열은 null로 처리된다")
        void blankStringBecomeNull() {
            // when
            LogoUrl logoUrl = LogoUrl.of("   ");

            // then
            assertThat(logoUrl.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("유효하지 않은 URL이면 예외가 발생한다")
        void invalidUrlThrowsException() {
            // when & then
            assertThatThrownBy(() -> LogoUrl.of("invalid-url"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은 URL");
        }

        @Test
        @DisplayName("500자를 초과하면 예외가 발생한다")
        void tooLongUrlThrowsException() {
            // given
            String longUrl = "https://example.com/" + "a".repeat(500);

            // when & then
            assertThatThrownBy(() -> LogoUrl.of(longUrl))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("500자");
        }
    }

    @Nested
    @DisplayName("Description 테스트")
    class DescriptionTest {

        @Test
        @DisplayName("유효한 설명을 생성한다")
        void createValidDescription() {
            // when
            Description description = Description.of("셀러 설명입니다.");

            // then
            assertThat(description.value()).isEqualTo("셀러 설명입니다.");
            assertThat(description.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("null로 빈 Description을 생성한다")
        void nullCreatesEmptyDescription() {
            // when
            Description description = Description.of(null);

            // then
            assertThat(description.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("empty()로 빈 Description을 생성한다")
        void emptyCreatesEmptyDescription() {
            // when
            Description description = Description.empty();

            // then
            assertThat(description.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("빈 문자열은 null로 처리된다")
        void blankStringBecomeNull() {
            // when
            Description description = Description.of("   ");

            // then
            assertThat(description.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("5000자를 초과하면 예외가 발생한다")
        void tooLongThrowsException() {
            // given
            String longDescription = "a".repeat(5001);

            // when & then
            assertThatThrownBy(() -> Description.of(longDescription))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("5000자");
        }
    }
}
