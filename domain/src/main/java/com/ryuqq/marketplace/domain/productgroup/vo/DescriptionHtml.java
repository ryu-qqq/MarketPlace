package com.ryuqq.marketplace.domain.productgroup.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 상품 상세설명 HTML Value Object. Persistence 레이어에서 별도 테이블(product_group_description)로 분리 저장. */
public record DescriptionHtml(String value) {

    private static final Pattern IMG_TAG_PATTERN =
            Pattern.compile("<img[^>]*>", Pattern.CASE_INSENSITIVE);

    private static final Pattern IMG_SRC_PATTERN =
            Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);

    private static final Pattern HIDDEN_IMG_PATTERN =
            Pattern.compile("display\\s*:\\s*none", Pattern.CASE_INSENSITIVE);

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

    /** HTML 콘텐츠에서 &lt;img&gt; 태그의 src URL을 등장 순서대로 추출합니다. display:none 등 숨겨진 이미지는 제외합니다. */
    public List<String> extractImageUrls() {
        if (isEmpty()) {
            return Collections.emptyList();
        }
        Matcher tagMatcher = IMG_TAG_PATTERN.matcher(value);
        List<String> urls = new ArrayList<>();
        while (tagMatcher.find()) {
            String imgTag = tagMatcher.group();
            if (HIDDEN_IMG_PATTERN.matcher(imgTag).find()) {
                continue;
            }
            Matcher srcMatcher = IMG_SRC_PATTERN.matcher(imgTag);
            if (srcMatcher.find()) {
                urls.add(srcMatcher.group(1));
            }
        }
        return Collections.unmodifiableList(urls);
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
