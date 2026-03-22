package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.client.NaverCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverCommerceBrand;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceBrandMapper;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.ExternalBrandResult;
import com.ryuqq.marketplace.application.saleschannelbrand.port.out.client.SalesChannelBrandClient;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

/**
 * Naver Commerce 브랜드 클라이언트 어댑터.
 *
 * <p>Naver Commerce API를 통해 브랜드를 검색합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceBrandClientAdapter implements SalesChannelBrandClient {

    private final NaverCommerceApiClient apiClient;
    private final NaverCommerceBrandMapper mapper;

    public NaverCommerceBrandClientAdapter(
            NaverCommerceApiClient apiClient, NaverCommerceBrandMapper mapper) {
        this.apiClient = apiClient;
        this.mapper = mapper;
    }

    @Override
    public List<ExternalBrandResult> searchBrands(String name) {
        List<NaverCommerceBrand> brands =
                apiClient.searchBrands(name, new ParameterizedTypeReference<>() {});

        if (brands == null) {
            return List.of();
        }

        return brands.stream().map(mapper::toExternalBrandResult).toList();
    }
}
