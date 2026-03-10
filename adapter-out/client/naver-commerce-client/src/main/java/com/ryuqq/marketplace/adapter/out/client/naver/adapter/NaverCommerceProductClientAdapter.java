package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.auth.NaverCommerceTokenManager;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductSearchRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductSearchResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceProductMapper;
import com.ryuqq.marketplace.application.outboundproduct.dto.vo.ExternalProductEntry;
import com.ryuqq.marketplace.application.outboundproduct.port.out.client.SalesChannelProductSearchClient;
import com.ryuqq.marketplace.application.outboundsync.port.out.client.SalesChannelProductClient;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 네이버 커머스 상품 등록/수정 클라이언트 어댑터.
 *
 * <p>ProductGroupDetailBundle → NaverProductRegistrationRequest 변환 후 POST/PUT API 호출.
 */
@Component
@Qualifier("naverProductClient")
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceProductClientAdapter
        implements SalesChannelProductClient, SalesChannelProductSearchClient {

    private static final Logger log =
            LoggerFactory.getLogger(NaverCommerceProductClientAdapter.class);

    private final RestClient restClient;
    private final NaverCommerceTokenManager tokenManager;
    private final NaverCommerceProductMapper mapper;

    public NaverCommerceProductClientAdapter(
            RestClient naverCommerceRestClient,
            NaverCommerceTokenManager tokenManager,
            NaverCommerceProductMapper mapper) {
        this.restClient = naverCommerceRestClient;
        this.tokenManager = tokenManager;
        this.mapper = mapper;
    }

    @Override
    public String channelCode() {
        return "NAVER";
    }

    @Override
    public String registerProduct(
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            SellerSalesChannel channel) {

        NaverProductRegistrationRequest request =
                mapper.toRegistrationRequest(bundle, externalCategoryId, externalBrandId);

        String token = tokenManager.getAccessToken();

        log.info(
                "네이버 커머스 상품 등록 요청: productGroupId={}, categoryId={}",
                bundle.group().idValue(),
                externalCategoryId);

        NaverProductRegistrationResponse response =
                restClient
                        .post()
                        .uri("/v2/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .body(request)
                        .retrieve()
                        .body(NaverProductRegistrationResponse.class);

        if (response == null || response.originProductNo() == null) {
            throw new IllegalStateException(
                    "네이버 커머스 상품 등록 응답이 null입니다: productGroupId=" + bundle.group().idValue());
        }

        log.info(
                "네이버 커머스 상품 등록 성공: productGroupId={}, originProductNo={}",
                bundle.group().idValue(),
                response.originProductNo());

        return String.valueOf(response.originProductNo());
    }

    @Override
    public void updateProduct(
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId,
            SellerSalesChannel channel) {

        NaverProductRegistrationRequest request =
                mapper.toRegistrationRequest(bundle, externalCategoryId, externalBrandId);

        String token = tokenManager.getAccessToken();

        log.info(
                "네이버 커머스 상품 수정 요청: productGroupId={}, externalProductId={}",
                bundle.group().idValue(),
                externalProductId);

        restClient
                .put()
                .uri("/v2/products/origin-products/{originProductNo}", externalProductId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        log.info(
                "네이버 커머스 상품 수정 성공: productGroupId={}, externalProductId={}",
                bundle.group().idValue(),
                externalProductId);
    }

    @Override
    public void deleteProduct(String externalProductId, SellerSalesChannel channel) {
        String token = tokenManager.getAccessToken();

        log.info("네이버 커머스 상품 삭제 요청: externalProductId={}", externalProductId);

        restClient
                .delete()
                .uri("/v2/products/origin-products/{originProductNo}", externalProductId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();

        log.info("네이버 커머스 상품 삭제 성공: externalProductId={}", externalProductId);
    }

    /**
     * 네이버 커머스에 등록된 전체 상품을 페이지 단위로 조회합니다.
     *
     * <p>POST /v1/products/search API를 반복 호출하여 모든 페이지의 상품을 수집합니다.
     *
     * @return 전체 상품 목록
     */
    public List<NaverProductSearchResponse.ProductContent> searchAllProducts() {
        String token = tokenManager.getAccessToken();
        List<NaverProductSearchResponse.ProductContent> allProducts = new ArrayList<>();

        int page = 1;
        int size = 500;
        boolean hasMore = true;

        while (hasMore) {
            NaverProductSearchRequest request = NaverProductSearchRequest.allProducts(page, size);

            log.info("네이버 커머스 상품 목록 조회 요청: page={}, size={}", page, size);

            NaverProductSearchResponse response =
                    restClient
                            .post()
                            .uri("/v1/products/search")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .body(request)
                            .retrieve()
                            .body(NaverProductSearchResponse.class);

            if (response == null || response.contents() == null || response.contents().isEmpty()) {
                log.info("네이버 커머스 상품 목록 조회 완료: 더 이상 상품 없음");
                break;
            }

            allProducts.addAll(response.contents());
            log.info(
                    "네이버 커머스 상품 목록 조회 성공: page={}, 조회건수={}, 누적={}, 전체={}",
                    page,
                    response.contents().size(),
                    allProducts.size(),
                    response.totalElements());

            hasMore = Boolean.FALSE.equals(response.last());
            page++;
        }

        log.info("네이버 커머스 전체 상품 조회 완료: 총 {}건", allProducts.size());
        return allProducts;
    }

    @Override
    public List<ExternalProductEntry> fetchAllProducts() {
        List<NaverProductSearchResponse.ProductContent> naverProducts = searchAllProducts();
        List<ExternalProductEntry> entries = new ArrayList<>();

        for (NaverProductSearchResponse.ProductContent content : naverProducts) {
            if (content.channelProducts() == null) {
                continue;
            }
            for (NaverProductSearchResponse.ChannelProduct cp : content.channelProducts()) {
                entries.add(
                        new ExternalProductEntry(
                                String.valueOf(content.originProductNo()),
                                cp.sellerManagementCode(),
                                cp.name(),
                                cp.statusType()));
            }
        }

        log.info("네이버 커머스 ExternalProductEntry 변환 완료: {}건", entries.size());
        return entries;
    }

    /**
     * 네이버 커머스 상품 목록을 단일 페이지로 조회합니다.
     *
     * @param request 검색 요청
     * @return 검색 응답
     */
    public NaverProductSearchResponse searchProducts(NaverProductSearchRequest request) {
        String token = tokenManager.getAccessToken();

        log.info("네이버 커머스 상품 목록 조회 요청: page={}, size={}", request.page(), request.size());

        NaverProductSearchResponse response =
                restClient
                        .post()
                        .uri("/v1/products/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .body(request)
                        .retrieve()
                        .body(NaverProductSearchResponse.class);

        if (response == null) {
            throw new IllegalStateException("네이버 커머스 상품 목록 조회 응답이 null입니다");
        }

        log.info(
                "네이버 커머스 상품 목록 조회 성공: 조회건수={}, 전체={}",
                response.contents() != null ? response.contents().size() : 0,
                response.totalElements());

        return response;
    }
}
