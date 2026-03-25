package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.client.SetofCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.setof.config.SetofCommerceProperties;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupBasicInfoUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 상품 그룹 기본 정보 수정 어댑터.
 *
 * <p>PATCH /api/v2/admin/product-groups/{productGroupId}/basic-info HTTP 호출은 {@link
 * SetofCommerceApiClient}에 위임합니다.
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceBasicInfoAdapter {

    private static final Logger log = LoggerFactory.getLogger(SetofCommerceBasicInfoAdapter.class);

    private final SetofCommerceApiClient apiClient;
    private final SetofCommerceProperties properties;

    public SetofCommerceBasicInfoAdapter(
            SetofCommerceApiClient apiClient, SetofCommerceProperties properties) {
        this.apiClient = apiClient;
        this.properties = properties;
    }

    /**
     * 상품 그룹 기본 정보 수정.
     *
     * @param externalProductGroupId 세토프 상품 그룹 ID
     * @param request 기본 정보 수정 요청
     */
    public void updateBasicInfo(
            String externalProductGroupId, SetofProductGroupBasicInfoUpdateRequest request) {
        log.info("세토프 커머스 상품 그룹 기본정보 수정: externalProductGroupId={}", externalProductGroupId);
        apiClient.updateBasicInfo(properties.getServiceToken(), externalProductGroupId, request);
        log.info("세토프 커머스 상품 그룹 기본정보 수정 성공: externalProductGroupId={}", externalProductGroupId);
    }
}
