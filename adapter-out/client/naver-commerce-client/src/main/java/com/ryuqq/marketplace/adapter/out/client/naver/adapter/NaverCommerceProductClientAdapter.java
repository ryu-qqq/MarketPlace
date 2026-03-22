package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.client.NaverCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.naver.config.NaverCommerceProperties;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductDetailResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductSearchRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductSearchResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceProductMapper;
import com.ryuqq.marketplace.application.outboundproduct.dto.vo.ExternalProductEntry;
import com.ryuqq.marketplace.application.outboundproduct.port.out.client.SalesChannelProductSearchClient;
import com.ryuqq.marketplace.application.outboundsync.port.out.client.SalesChannelProductClient;
import com.ryuqq.marketplace.application.outboundproductimage.dto.ResolvedExternalImages;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 상품 등록/수정 클라이언트 어댑터.
 *
 * <p>ProductGroupSyncData → NaverProductRegistrationRequest 변환 후 {@link NaverCommerceApiClient}를
 * 통해 API 호출합니다.
 */
@Component
@Qualifier("naverProductClient")
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceProductClientAdapter
        implements SalesChannelProductClient, SalesChannelProductSearchClient {

    private static final Logger log =
            LoggerFactory.getLogger(NaverCommerceProductClientAdapter.class);

    private final NaverCommerceApiClient apiClient;
    private final NaverCommerceProductMapper mapper;
    private final NaverCommerceProperties properties;

    public NaverCommerceProductClientAdapter(
            NaverCommerceApiClient apiClient,
            NaverCommerceProductMapper mapper,
            NaverCommerceProperties properties) {
        this.apiClient = apiClient;
        this.mapper = mapper;
        this.properties = properties;
    }

    @Override
    public String channelCode() {
        return "NAVER";
    }

    @Override
    public String registerProduct(
            ProductGroupSyncData syncData,
            Long externalCategoryId,
            Long externalBrandId,
            SellerSalesChannel channel,
            Shop shop) {

        if (!properties.isEnabled()) {
            log.info("NaverCommerceClient disabled. 상품 등록 스킵: productGroupId={}", syncData.queryResult().id());
            return null;
        }

        NaverProductRegistrationRequest request =
                mapper.toRegistrationRequest(syncData, externalCategoryId, externalBrandId);
        return executeRegister(request, syncData.queryResult().id(), externalCategoryId);
    }

    @Override
    public String registerProduct(
            ProductGroupSyncData syncData,
            Long externalCategoryId,
            Long externalBrandId,
            SellerSalesChannel channel,
            Shop shop,
            ResolvedExternalImages resolvedImages) {

        if (!properties.isEnabled()) {
            log.info("NaverCommerceClient disabled. 상품 등록(이미지) 스킵: productGroupId={}", syncData.queryResult().id());
            return null;
        }

        NaverProductRegistrationRequest request =
                mapper.toRegistrationRequest(
                        syncData, externalCategoryId, externalBrandId, resolvedImages);
        return executeRegister(request, syncData.queryResult().id(), externalCategoryId);
    }

    @Override
    public void updateProduct(
            ProductGroupSyncData syncData,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId,
            SellerSalesChannel channel,
            Set<ChangedArea> changedAreas) {

        if (!properties.isEnabled()) {
            log.info("NaverCommerceClient disabled. 상품 수정 스킵: externalProductId={}", externalProductId);
            return;
        }

        NaverProductDetailResponse existing = fetchExistingProduct(externalProductId);
        NaverProductRegistrationRequest request =
                mapper.toUpdateRequest(
                        syncData, externalCategoryId, externalBrandId, existing, changedAreas);
        apiClient.updateProduct(request, externalProductId);
        log.info(
                "네이버 커머스 상품 수정 성공: productGroupId={}, externalProductId={}",
                syncData.queryResult().id(),
                externalProductId);
    }

    @Override
    public void updateProduct(
            ProductGroupSyncData syncData,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId,
            SellerSalesChannel channel,
            Set<ChangedArea> changedAreas,
            ResolvedExternalImages resolvedImages) {

        if (!properties.isEnabled()) {
            log.info("NaverCommerceClient disabled. 상품 수정(이미지) 스킵: externalProductId={}", externalProductId);
            return;
        }

        NaverProductDetailResponse existing = fetchExistingProduct(externalProductId);
        NaverProductRegistrationRequest request =
                mapper.toUpdateRequest(
                        syncData,
                        externalCategoryId,
                        externalBrandId,
                        resolvedImages,
                        existing,
                        changedAreas);
        apiClient.updateProduct(request, externalProductId);
        log.info(
                "네이버 커머스 상품 수정 성공: productGroupId={}, externalProductId={}",
                syncData.queryResult().id(),
                externalProductId);
    }

    @Override
    public void deleteProduct(String externalProductId, SellerSalesChannel channel) {
        if (!properties.isEnabled()) {
            log.info("NaverCommerceClient disabled. 상품 삭제 스킵: externalProductId={}", externalProductId);
            return;
        }

        apiClient.deleteProduct(externalProductId);
        log.info("네이버 커머스 상품 삭제 성공: externalProductId={}", externalProductId);
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

    /** 네이버 커머스 상품 목록을 단일 페이지로 조회합니다. */
    public NaverProductSearchResponse searchProducts(NaverProductSearchRequest request) {
        NaverProductSearchResponse response = apiClient.searchProducts(request);

        if (response == null) {
            throw new IllegalStateException("네이버 커머스 상품 목록 조회 응답이 null입니다");
        }

        log.info(
                "네이버 커머스 상품 목록 조회 성공: 조회건수={}, 전체={}",
                response.contents() != null ? response.contents().size() : 0,
                response.totalElements());

        return response;
    }

    private String executeRegister(
            NaverProductRegistrationRequest request, Long productGroupId, Long categoryId) {
        log.info(
                "네이버 커머스 상품 등록 요청: productGroupId={}, categoryId={}",
                productGroupId,
                categoryId);

        NaverProductRegistrationResponse response = apiClient.registerProduct(request);

        if (response == null || response.originProductNo() == null) {
            throw new IllegalStateException(
                    "네이버 커머스 상품 등록 응답이 null입니다: productGroupId=" + productGroupId);
        }

        log.info(
                "네이버 커머스 상품 등록 성공: productGroupId={}, originProductNo={}",
                productGroupId,
                response.originProductNo());

        return String.valueOf(response.originProductNo());
    }

    private NaverProductDetailResponse fetchExistingProduct(String externalProductId) {
        log.info("네이버 커머스 기존 상품 조회: externalProductId={}", externalProductId);

        NaverProductDetailResponse response = apiClient.getProductDetail(externalProductId);

        if (response == null || response.originProduct() == null) {
            log.warn(
                    "네이버 기존 상품 조회 실패, 전체 교체 모드로 진행: externalProductId={}",
                    externalProductId);
            return null;
        }

        log.info("네이버 기존 상품 조회 성공: externalProductId={}", externalProductId);
        return response;
    }

    /** 네이버 커머스에 등록된 전체 상품을 페이지 단위로 조회합니다. */
    private List<NaverProductSearchResponse.ProductContent> searchAllProducts() {
        List<NaverProductSearchResponse.ProductContent> allProducts = new ArrayList<>();

        int page = 1;
        int size = 500;
        boolean hasMore = true;

        while (hasMore) {
            NaverProductSearchRequest request = NaverProductSearchRequest.allProducts(page, size);

            log.info("네이버 커머스 상품 목록 조회 요청: page={}, size={}", page, size);

            NaverProductSearchResponse response = apiClient.searchProducts(request);

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
}
