package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofImagesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 세토프 커머스 상품 그룹 이미지 어댑터.
 *
 * <p>이미지 등록/수정 엔드포인트를 호출합니다.
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceImageAdapter {

    private static final Logger log = LoggerFactory.getLogger(SetofCommerceImageAdapter.class);

    private final RestClient restClient;

    public SetofCommerceImageAdapter(RestClient setofCommerceRestClient) {
        this.restClient = setofCommerceRestClient;
    }

    /**
     * 이미지 등록.
     *
     * <p>POST /api/v2/admin/product-groups/{productGroupId}/images
     *
     * @param productGroupId 세토프 상품 그룹 ID
     * @param request 이미지 등록 요청
     */
    public void registerImages(Long productGroupId, SetofImagesRequest request) {
        log.info("세토프 커머스 이미지 등록 요청: productGroupId={}", productGroupId);

        restClient
                .post()
                .uri("/api/v2/admin/product-groups/{productGroupId}/images", productGroupId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        log.info("세토프 커머스 이미지 등록 성공: productGroupId={}", productGroupId);
    }

    /**
     * 이미지 수정.
     *
     * <p>PUT /api/v2/admin/product-groups/{productGroupId}/images
     *
     * @param productGroupId 세토프 상품 그룹 ID
     * @param request 이미지 수정 요청
     */
    public void updateImages(Long productGroupId, SetofImagesRequest request) {
        log.info("세토프 커머스 이미지 수정 요청: productGroupId={}", productGroupId);

        restClient
                .put()
                .uri("/api/v2/admin/product-groups/{productGroupId}/images", productGroupId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        log.info("세토프 커머스 이미지 수정 성공: productGroupId={}", productGroupId);
    }
}
