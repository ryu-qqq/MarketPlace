package com.ryuqq.marketplace.adapter.out.client.setof.support;

import com.ryuqq.marketplace.adapter.out.client.setof.client.SetofCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofSellerTokenRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofSellerTokenResponse;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 셀러 토큰 발급 및 캐싱 Provider.
 *
 * <p>Shop의 apiKey + apiSecret으로 세토프 토큰 발급 API를 호출하고, 발급받은 accessToken을 shopId 기반으로 인메모리 캐싱합니다. 토큰
 * 만료 시 자동 재발급합니다.
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofSellerTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(SetofSellerTokenProvider.class);

    private final SetofCommerceApiClient apiClient;
    private final Map<Long, String> tokenCache = new ConcurrentHashMap<>();

    public SetofSellerTokenProvider(SetofCommerceApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /** Shop의 셀러 토큰을 반환합니다. 캐시에 있으면 캐시 반환, 없으면 발급. */
    public String resolveToken(Shop shop) {
        Long shopId = shop.idValue();
        String cached = tokenCache.get(shopId);
        if (cached != null) {
            return cached;
        }
        return issueAndCache(shop);
    }

    /** 토큰 발급 실패 시 (401 등) 캐시를 무효화하고 재발급합니다. */
    public String refreshToken(Shop shop) {
        Long shopId = shop.idValue();
        tokenCache.remove(shopId);
        return issueAndCache(shop);
    }

    /** 특정 Shop의 캐시된 토큰을 제거합니다. */
    public void evict(Long shopId) {
        tokenCache.remove(shopId);
    }

    private String issueAndCache(Shop shop) {
        Long shopId = shop.idValue();
        log.info("세토프 셀러 토큰 발급: shopId={}", shopId);

        SetofSellerTokenRequest request =
                SetofSellerTokenRequest.of(shop.apiKey(), shop.apiSecret());
        SetofSellerTokenResponse response = apiClient.issueSellerToken(request);

        String accessToken = response.accessToken();
        tokenCache.put(shopId, accessToken);

        log.info("세토프 셀러 토큰 발급 성공: shopId={}", shopId);
        return accessToken;
    }
}
