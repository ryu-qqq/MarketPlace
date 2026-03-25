package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.client.SetofCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.setof.config.SetofCommerceProperties;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofImagesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 상품 그룹 이미지 어댑터.
 *
 * <p>이미지 등록/수정 엔드포인트를 호출합니다. HTTP 호출은 {@link SetofCommerceApiClient}에 위임합니다.
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceImageAdapter {

    private static final Logger log = LoggerFactory.getLogger(SetofCommerceImageAdapter.class);

    private final SetofCommerceApiClient apiClient;
    private final SetofCommerceProperties properties;

    public SetofCommerceImageAdapter(
            SetofCommerceApiClient apiClient, SetofCommerceProperties properties) {
        this.apiClient = apiClient;
        this.properties = properties;
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
        log.info("세토프 커머스 이미지 등록: productGroupId={}", productGroupId);
        apiClient.registerImages(properties.getServiceToken(), productGroupId, request);
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
        log.info("세토프 커머스 이미지 수정: productGroupId={}", productGroupId);
        apiClient.updateImages(properties.getServiceToken(), productGroupId, request);
        log.info("세토프 커머스 이미지 수정 성공: productGroupId={}", productGroupId);
    }
}
