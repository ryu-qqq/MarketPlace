package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.auth.NaverCommerceTokenManager;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverCommerceCategory;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceCategoryMapper;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.ExternalCategoryResult;
import com.ryuqq.marketplace.application.saleschannelcategory.port.out.client.SalesChannelCategoryClient;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Naver Commerce 카테고리 클라이언트 어댑터.
 *
 * <p>Naver Commerce API를 통해 전체 카테고리를 조회합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceCategoryClientAdapter implements SalesChannelCategoryClient {

    private final RestClient restClient;
    private final NaverCommerceTokenManager tokenManager;
    private final NaverCommerceCategoryMapper mapper;

    public NaverCommerceCategoryClientAdapter(
            RestClient naverCommerceRestClient,
            NaverCommerceTokenManager tokenManager,
            NaverCommerceCategoryMapper mapper) {
        this.restClient = naverCommerceRestClient;
        this.tokenManager = tokenManager;
        this.mapper = mapper;
    }

    @Override
    public List<ExternalCategoryResult> fetchAllCategories() {
        String token = tokenManager.getAccessToken();

        List<NaverCommerceCategory> categories =
                restClient
                        .get()
                        .uri("/v1/categories")
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

        if (categories == null) {
            return List.of();
        }

        return categories.stream().map(mapper::toExternalCategoryResult).toList();
    }
}
