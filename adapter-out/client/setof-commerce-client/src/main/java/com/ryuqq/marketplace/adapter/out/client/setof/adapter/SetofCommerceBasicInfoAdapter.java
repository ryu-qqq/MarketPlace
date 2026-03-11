package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupBasicInfoUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 세토프 커머스 상품 그룹 기본 정보 수정 어댑터.
 *
 * <p>PATCH /api/v2/admin/product-groups/{productGroupId}/basic-info
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceBasicInfoAdapter {

    private static final Logger log = LoggerFactory.getLogger(SetofCommerceBasicInfoAdapter.class);

    private final RestClient restClient;

    public SetofCommerceBasicInfoAdapter(RestClient setofCommerceRestClient) {
        this.restClient = setofCommerceRestClient;
    }

    /**
     * 상품 그룹 기본 정보 수정.
     *
     * @param externalProductGroupId 세토프 상품 그룹 ID
     * @param request 기본 정보 수정 요청
     */
    public void updateBasicInfo(
            String externalProductGroupId, SetofProductGroupBasicInfoUpdateRequest request) {
        log.info("세토프 커머스 상품 그룹 기본정보 수정 요청: externalProductGroupId={}", externalProductGroupId);

        restClient
                .patch()
                .uri(
                        "/api/v2/admin/product-groups/{productGroupId}/basic-info",
                        externalProductGroupId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        log.info("세토프 커머스 상품 그룹 기본정보 수정 성공: externalProductGroupId={}", externalProductGroupId);
    }
}
