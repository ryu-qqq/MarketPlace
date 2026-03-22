package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.client.SetofCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofDescriptionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 상품 그룹 상세설명 어댑터.
 *
 * <p>상세설명 등록/수정 엔드포인트를 호출합니다. HTTP 호출은 {@link SetofCommerceApiClient}에 위임합니다.
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceDescriptionAdapter {

    private static final Logger log =
            LoggerFactory.getLogger(SetofCommerceDescriptionAdapter.class);

    private final SetofCommerceApiClient apiClient;

    public SetofCommerceDescriptionAdapter(SetofCommerceApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * 상세설명 등록.
     *
     * <p>POST /api/v2/admin/product-groups/{productGroupId}/description
     *
     * @param productGroupId 세토프 상품 그룹 ID
     * @param request 상세설명 등록 요청
     */
    public void registerDescription(Long productGroupId, SetofDescriptionRequest request) {
        log.info("세토프 커머스 상세설명 등록: productGroupId={}", productGroupId);
        apiClient.registerDescription(productGroupId, request);
        log.info("세토프 커머스 상세설명 등록 성공: productGroupId={}", productGroupId);
    }

    /**
     * 상세설명 수정.
     *
     * <p>PUT /api/v2/admin/product-groups/{productGroupId}/description
     *
     * @param productGroupId 세토프 상품 그룹 ID
     * @param request 상세설명 수정 요청
     */
    public void updateDescription(Long productGroupId, SetofDescriptionRequest request) {
        log.info("세토프 커머스 상세설명 수정: productGroupId={}", productGroupId);
        apiClient.updateDescription(productGroupId, request);
        log.info("세토프 커머스 상세설명 수정 성공: productGroupId={}", productGroupId);
    }
}
