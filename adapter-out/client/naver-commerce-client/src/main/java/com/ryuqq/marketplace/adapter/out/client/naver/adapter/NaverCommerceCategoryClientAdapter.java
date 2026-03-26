package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.client.NaverCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverCommerceCategory;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceCategoryMapper;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.ExternalCategoryResult;
import com.ryuqq.marketplace.application.saleschannelcategory.port.out.client.SalesChannelCategoryClient;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

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

    private final NaverCommerceApiClient apiClient;
    private final NaverCommerceCategoryMapper mapper;

    public NaverCommerceCategoryClientAdapter(
            NaverCommerceApiClient apiClient, NaverCommerceCategoryMapper mapper) {
        this.apiClient = apiClient;
        this.mapper = mapper;
    }

    @Override
    public List<ExternalCategoryResult> fetchAllCategories() {
        List<NaverCommerceCategory> categories =
                apiClient.getCategories(new ParameterizedTypeReference<>() {});

        if (categories == null) {
            return List.of();
        }

        return categories.stream().map(mapper::toExternalCategoryResult).toList();
    }
}
