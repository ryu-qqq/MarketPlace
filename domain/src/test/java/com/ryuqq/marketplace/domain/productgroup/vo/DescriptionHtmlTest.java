package com.ryuqq.marketplace.domain.productgroup.vo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("DescriptionHtml 단위 테스트")
class DescriptionHtmlTest {

    @Nested
    @DisplayName("extractImageUrls() - 이미지 URL 추출")
    class ExtractImageUrlsTest {

        @Test
        @DisplayName("일반 img 태그에서 src URL을 추출한다")
        void extractsNormalImageUrls() {
            DescriptionHtml html =
                    DescriptionHtml.of(
                            "<img src=\"https://example.com/1.jpg\">"
                                    + "<p>설명</p>"
                                    + "<img src=\"https://example.com/2.jpg\">");

            List<String> urls = html.extractImageUrls();

            assertThat(urls).containsExactly(
                    "https://example.com/1.jpg", "https://example.com/2.jpg");
        }

        @Test
        @DisplayName("display:none 스타일의 img 태그는 제외한다")
        void excludesDisplayNoneImages() {
            DescriptionHtml html =
                    DescriptionHtml.of(
                            "<img src=\"https://example.com/visible.jpg\">"
                                    + "<img style=\"display: none;\" src=\"https://tracking.example.com/hidden.jpg\">");

            List<String> urls = html.extractImageUrls();

            assertThat(urls).containsExactly("https://example.com/visible.jpg");
        }

        @Test
        @DisplayName("display:none이 style 속성 내에 있을 때만 제외한다")
        void onlyExcludesDisplayNoneInStyleAttribute() {
            DescriptionHtml html =
                    DescriptionHtml.of(
                            "<img alt=\"display: none\" src=\"https://example.com/visible.jpg\">");

            List<String> urls = html.extractImageUrls();

            assertThat(urls).containsExactly("https://example.com/visible.jpg");
        }

        @Test
        @DisplayName("display:none 앞뒤 공백이 있어도 필터링한다")
        void handlesWhitespaceAroundDisplayNone() {
            DescriptionHtml html =
                    DescriptionHtml.of(
                            "<img style=\"display :  none\" src=\"https://example.com/hidden.jpg\">");

            List<String> urls = html.extractImageUrls();

            assertThat(urls).isEmpty();
        }

        @Test
        @DisplayName("src가 작은따옴표로 감싸져 있어도 추출한다")
        void extractsSingleQuotedSrc() {
            DescriptionHtml html =
                    DescriptionHtml.of("<img src='https://example.com/img.jpg'>");

            List<String> urls = html.extractImageUrls();

            assertThat(urls).containsExactly("https://example.com/img.jpg");
        }

        @Test
        @DisplayName("빈 HTML은 빈 목록을 반환한다")
        void emptyHtmlReturnsEmptyList() {
            DescriptionHtml html = DescriptionHtml.empty();

            assertThat(html.extractImageUrls()).isEmpty();
        }

        @Test
        @DisplayName("img 태그가 없는 HTML은 빈 목록을 반환한다")
        void noImgTagsReturnsEmptyList() {
            DescriptionHtml html = DescriptionHtml.of("<p>이미지 없는 설명</p>");

            assertThat(html.extractImageUrls()).isEmpty();
        }

        @Test
        @DisplayName("src 속성이 없는 img 태그는 무시한다")
        void imgWithoutSrcIsIgnored() {
            DescriptionHtml html = DescriptionHtml.of("<img alt=\"no source\">");

            assertThat(html.extractImageUrls()).isEmpty();
        }

        @Test
        @DisplayName("실제 storebot.info 트래킹 이미지 패턴을 필터링한다")
        void filtersRealWorldStorebotTrackingImage() {
            String longUrl =
                    "https://10.stock.storebot.info/stock/bontano/10/bot"
                            + "A".repeat(600)
                            + "store";
            DescriptionHtml html =
                    DescriptionHtml.of(
                            "<img src=\"https://store.img11.co.kr/normal.jpg\">"
                                    + "<img style=\"display: none;\" src=\""
                                    + longUrl
                                    + "\" alt=\"tracking\">");

            List<String> urls = html.extractImageUrls();

            assertThat(urls).containsExactly("https://store.img11.co.kr/normal.jpg");
        }
    }

    @Nested
    @DisplayName("extractHiddenImageUrls() - 숨겨진 이미지 URL 추출")
    class ExtractHiddenImageUrlsTest {

        @Test
        @DisplayName("display:none 이미지의 URL만 추출한다")
        void extractsOnlyHiddenImageUrls() {
            DescriptionHtml html =
                    DescriptionHtml.of(
                            "<img src=\"https://example.com/visible.jpg\">"
                                    + "<img style=\"display:none\" src=\"https://example.com/hidden.jpg\">");

            Set<String> hiddenUrls = html.extractHiddenImageUrls();

            assertThat(hiddenUrls).containsExactly("https://example.com/hidden.jpg");
        }

        @Test
        @DisplayName("숨겨진 이미지가 없으면 빈 Set을 반환한다")
        void returnsEmptySetWhenNoHiddenImages() {
            DescriptionHtml html =
                    DescriptionHtml.of("<img src=\"https://example.com/visible.jpg\">");

            assertThat(html.extractHiddenImageUrls()).isEmpty();
        }

        @Test
        @DisplayName("빈 HTML은 빈 Set을 반환한다")
        void emptyHtmlReturnsEmptySet() {
            assertThat(DescriptionHtml.empty().extractHiddenImageUrls()).isEmpty();
        }
    }

    @Nested
    @DisplayName("isHiddenImage() - 숨겨진 이미지 판별")
    class IsHiddenImageTest {

        @Test
        @DisplayName("style에 display:none이 있으면 hidden이다")
        void detectsHiddenImage() {
            assertThat(
                            DescriptionHtml.isHiddenImage(
                                    "<img style=\"display: none;\" src=\"url\">"))
                    .isTrue();
        }

        @Test
        @DisplayName("style이 없으면 hidden이 아니다")
        void normalImageIsNotHidden() {
            assertThat(DescriptionHtml.isHiddenImage("<img src=\"url\">")).isFalse();
        }

        @Test
        @DisplayName("alt 속성에 display:none이 있어도 hidden이 아니다")
        void displayNoneInAltIsNotHidden() {
            assertThat(
                            DescriptionHtml.isHiddenImage(
                                    "<img alt=\"display: none\" src=\"url\">"))
                    .isFalse();
        }

        @Test
        @DisplayName("대소문자를 구분하지 않는다")
        void caseInsensitive() {
            assertThat(
                            DescriptionHtml.isHiddenImage(
                                    "<img STYLE=\"DISPLAY: NONE\" src=\"url\">"))
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("replaceImageUrls() - 이미지 URL 치환")
    class ReplaceImageUrlsTest {

        @Test
        @DisplayName("매핑된 URL을 치환한다")
        void replacesMatchedUrls() {
            DescriptionHtml html =
                    DescriptionHtml.of("<img src=\"https://origin.com/img.jpg\">");

            DescriptionHtml replaced =
                    html.replaceImageUrls(
                            java.util.Map.of(
                                    "https://origin.com/img.jpg", "https://cdn.com/img.jpg"));

            assertThat(replaced.value()).contains("https://cdn.com/img.jpg");
            assertThat(replaced.value()).doesNotContain("https://origin.com/img.jpg");
        }

        @Test
        @DisplayName("빈 매핑이면 원본을 반환한다")
        void emptyMappingReturnsOriginal() {
            DescriptionHtml html = DescriptionHtml.of("<img src=\"url\">");

            assertThat(html.replaceImageUrls(java.util.Map.of())).isEqualTo(html);
        }
    }
}
