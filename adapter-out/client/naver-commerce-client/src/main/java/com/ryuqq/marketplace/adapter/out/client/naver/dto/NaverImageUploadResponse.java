package com.ryuqq.marketplace.adapter.out.client.naver.dto;

import java.util.List;

/**
 * 네이버 커머스 이미지 업로드 응답 DTO.
 *
 * <p>POST /v1/product-images/upload 응답 본문.
 *
 * @see <a href="https://apicenter.commerce.naver.com/ko/basic/commerce-api">Naver Commerce API</a>
 */
public record NaverImageUploadResponse(List<UploadedImage> images) {

    /** 방어적 복사. */
    public NaverImageUploadResponse {
        images = images == null ? List.of() : List.copyOf(images);
    }

    /** 업로드된 개별 이미지 정보. */
    public record UploadedImage(String url) {}
}
