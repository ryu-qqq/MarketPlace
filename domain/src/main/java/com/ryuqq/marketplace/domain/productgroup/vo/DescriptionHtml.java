package com.ryuqq.marketplace.domain.productgroup.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 상품 상세설명 HTML Value Object. Persistence 레이어에서 별도 테이블(product_group_description)로 분리 저장.
 *
 * <p>이미지 URL 추출은 정규식 기반으로 처리합니다. HTML 파서(Jsoup 등) 대비 경량이지만 다음 제한사항이 있습니다:
 *
 * <ul>
 *   <li>속성 값 내 {@code >} 문자가 포함된 비정상 HTML은 태그 경계를 잘못 인식할 수 있음
 *   <li>인라인 style 외의 CSS(class, external stylesheet)로 숨겨진 이미지는 감지 불가
 * </ul>
 *
 * <p>현재 사용처(레거시 셀러 상품 HTML)에서는 정규식으로 충분하며, 복잡한 HTML 처리가 필요해질 경우 Jsoup 전환을 검토합니다.
 */
public record DescriptionHtml(String value) {

    private static final Pattern IMG_TAG_PATTERN =
            Pattern.compile("<img[^>]*>", Pattern.CASE_INSENSITIVE);

    private static final Pattern IMG_SRC_PATTERN =
            Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);

    /** style 속성 내부의 display:none만 매칭. 다른 속성(alt, title 등)에 포함된 문자열은 무시. */
    private static final Pattern HIDDEN_STYLE_PATTERN =
            Pattern.compile(
                    "style\\s*=\\s*[\"'][^\"']*display\\s*:\\s*none[^\"']*[\"']",
                    Pattern.CASE_INSENSITIVE);

    public DescriptionHtml {
        if (value != null) {
            value = value.trim();
            if (value.isEmpty()) {
                value = null;
            }
        }
    }

    public static DescriptionHtml of(String value) {
        return new DescriptionHtml(value);
    }

    public static DescriptionHtml empty() {
        return new DescriptionHtml(null);
    }

    public boolean isEmpty() {
        return value == null;
    }

    /**
     * HTML 콘텐츠에서 &lt;img&gt; 태그의 src URL을 등장 순서대로 추출합니다. {@code style="display:none"} 등 인라인 스타일로
     * 숨겨진 이미지는 제외합니다.
     *
     * @return 추출된 이미지 URL 목록 (불변)
     */
    public List<String> extractImageUrls() {
        return extractImageUrls(Set.of());
    }

    /**
     * HTML 콘텐츠에서 &lt;img&gt; 태그의 src URL을 등장 순서대로 추출합니다. {@code style="display:none"} 등 인라인 스타일로
     * 숨겨진 이미지와 제외 도메인에 해당하는 이미지는 건너뜁니다.
     *
     * @param excludeDomains 제외할 도메인 Set (예: 자체 CDN 도메인)
     * @return 추출된 이미지 URL 목록 (불변)
     */
    public List<String> extractImageUrls(Set<String> excludeDomains) {
        if (isEmpty()) {
            return Collections.emptyList();
        }
        Matcher tagMatcher = IMG_TAG_PATTERN.matcher(value);
        List<String> urls = new ArrayList<>();
        while (tagMatcher.find()) {
            String imgTag = tagMatcher.group();
            if (isHiddenImage(imgTag)) {
                continue;
            }
            Matcher srcMatcher = IMG_SRC_PATTERN.matcher(imgTag);
            if (srcMatcher.find()) {
                String url = srcMatcher.group(1);
                if (!containsExcludedDomain(url, excludeDomains)) {
                    urls.add(url);
                }
            }
        }
        return Collections.unmodifiableList(urls);
    }

    private static boolean containsExcludedDomain(String url, Set<String> excludeDomains) {
        if (excludeDomains.isEmpty()) {
            return false;
        }
        for (String domain : excludeDomains) {
            if (url.contains(domain)) {
                return true;
            }
        }
        return false;
    }

    /**
     * HTML 콘텐츠에서 {@code style="display:none"}으로 숨겨진 이미지의 URL을 추출합니다. computeImageDiff에서 hidden 이미지의
     * 오삭제를 방지하기 위해 사용합니다.
     *
     * @return 숨겨진 이미지 URL Set (불변)
     */
    public Set<String> extractHiddenImageUrls() {
        if (isEmpty()) {
            return Collections.emptySet();
        }
        Matcher tagMatcher = IMG_TAG_PATTERN.matcher(value);
        Set<String> urls = new HashSet<>();
        while (tagMatcher.find()) {
            String imgTag = tagMatcher.group();
            if (isHiddenImage(imgTag)) {
                Matcher srcMatcher = IMG_SRC_PATTERN.matcher(imgTag);
                if (srcMatcher.find()) {
                    urls.add(srcMatcher.group(1));
                }
            }
        }
        return Collections.unmodifiableSet(urls);
    }

    static boolean isHiddenImage(String imgTag) {
        return HIDDEN_STYLE_PATTERN.matcher(imgTag).find();
    }

    /**
     * HTML 콘텐츠 내 이미지 URL을 CDN URL로 치환한 새 DescriptionHtml을 반환합니다.
     *
     * @param urlMapping 원본 URL → CDN URL 매핑
     * @return URL이 치환된 새 DescriptionHtml
     */
    public DescriptionHtml replaceImageUrls(Map<String, String> urlMapping) {
        if (isEmpty() || urlMapping.isEmpty()) {
            return this;
        }
        String replaced = value;
        for (Map.Entry<String, String> entry : urlMapping.entrySet()) {
            replaced = replaced.replace(entry.getKey(), entry.getValue());
        }
        return DescriptionHtml.of(replaced);
    }
}
