package com.ryuqq.marketplace.adapter.out.client.sellic.adapter;

import com.ryuqq.marketplace.adapter.out.client.sellic.client.SellicCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicApiResponse;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicProductRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicProductUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.sellic.mapper.SellicCommerceProductMapper;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyProductIdMappingReadManager;
import com.ryuqq.marketplace.application.outboundsync.port.out.client.SalesChannelProductClient;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 셀릭 커머스 상품 등록/수정/삭제 클라이언트 어댑터.
 *
 * <p>{@link SalesChannelProductClient} 구현체. ProductGroupSyncData → Sellic 요청 DTO 변환 후 ApiClient를 통해
 * API 호출합니다.
 *
 * <p>인증: SellerSalesChannel.vendorId() → customer_id, SellerSalesChannel.apiKey() → api_key (Body
 * 인증)
 */
@Component
@Qualifier("sellicProductClient")
@ConditionalOnProperty(prefix = "sellic-commerce", name = "base-url")
public class SellicCommerceProductClientAdapter implements SalesChannelProductClient {

    private static final Logger log =
            LoggerFactory.getLogger(SellicCommerceProductClientAdapter.class);

    private final SellicCommerceApiClient apiClient;
    private final SellicCommerceProductMapper mapper;
    private final LegacyProductIdMappingReadManager legacyMappingReadManager;

    public SellicCommerceProductClientAdapter(
            SellicCommerceApiClient apiClient,
            SellicCommerceProductMapper mapper,
            LegacyProductIdMappingReadManager legacyMappingReadManager) {
        this.apiClient = apiClient;
        this.mapper = mapper;
        this.legacyMappingReadManager = legacyMappingReadManager;
    }

    private Long resolveLegacyProductGroupId(Long internalProductGroupId) {
        return legacyMappingReadManager
                .findByInternalProductGroupId(internalProductGroupId)
                .stream()
                .findFirst()
                .map(m -> m.legacyProductGroupId())
                .orElse(null);
    }

    @Override
    public String channelCode() {
        return "SELLIC";
    }

    @Override
    public String registerProduct(
            ProductGroupSyncData syncData,
            Long externalCategoryId,
            Long externalBrandId,
            SellerSalesChannel channel,
            Shop shop) {

        Long productGroupId = syncData.queryResult().id();
        String customerId = channel.vendorId();
        String apiKey = channel.apiKey();
        Long legacyId = resolveLegacyProductGroupId(productGroupId);

        SellicProductRegistrationRequest request =
                mapper.toRegistrationRequest(syncData, customerId, apiKey, legacyId);

        log.info("셀릭 커머스 상품 등록 요청: productGroupId={}", productGroupId);

        SellicApiResponse response = apiClient.registerProduct(request);
        validateResponse(response, "등록", productGroupId);

        log.info(
                "셀릭 커머스 상품 등록 성공: productGroupId={}, externalProductId={}",
                productGroupId,
                response.productId());

        return response.productId();
    }

    @Override
    public void updateProduct(
            ProductGroupSyncData syncData,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId,
            SellerSalesChannel channel,
            Set<ChangedArea> changedAreas) {

        Long productGroupId = syncData.queryResult().id();
        String customerId = channel.vendorId();
        String apiKey = channel.apiKey();
        Long legacyId = resolveLegacyProductGroupId(productGroupId);

        log.info(
                "셀릭 커머스 상품 수정 요청: productGroupId={}, externalProductId={}, changedAreas={}",
                productGroupId,
                externalProductId,
                changedAreas);

        // 재고만 변경된 경우 재고 수정 API 사용
        if (changedAreas != null
                && changedAreas.size() == 1
                && changedAreas.contains(ChangedArea.STOCK)) {

            SellicApiResponse response =
                    apiClient.updateStock(
                            mapper.toStockUpdateRequest(
                                    syncData, externalProductId, customerId, apiKey));
            validateResponse(response, "재고수정", productGroupId);
        } else {
            SellicProductUpdateRequest request =
                    mapper.toUpdateRequest(
                            syncData, externalProductId, customerId, apiKey, legacyId);
            SellicApiResponse response = apiClient.updateProduct(request);
            validateResponse(response, "수정", productGroupId);
        }

        log.info(
                "셀릭 커머스 상품 수정 성공: productGroupId={}, externalProductId={}",
                productGroupId,
                externalProductId);
    }

    @Override
    public void deleteProduct(String externalProductId, SellerSalesChannel channel) {
        String customerId = channel.vendorId();
        String apiKey = channel.apiKey();

        log.info("셀릭 커머스 상품 삭제(판매종료) 요청: externalProductId={}", externalProductId);

        SellicProductUpdateRequest deleteRequest =
                mapper.toDeleteRequest(externalProductId, customerId, apiKey);
        SellicApiResponse response = apiClient.updateProduct(deleteRequest);

        if (!response.isSuccess()) {
            log.warn(
                    "셀릭 커머스 상품 삭제(판매종료) 실패: externalProductId={}, message={}",
                    externalProductId,
                    response.message());
        }

        log.info("셀릭 커머스 상품 삭제(판매종료) 성공: externalProductId={}", externalProductId);
    }

    private void validateResponse(
            SellicApiResponse response, String operation, Long productGroupId) {
        if (response == null) {
            throw new IllegalStateException(
                    "셀릭 커머스 상품 " + operation + " 응답이 null: productGroupId=" + productGroupId);
        }
        if (!response.isSuccess()) {
            throw new IllegalStateException(
                    "셀릭 커머스 상품 "
                            + operation
                            + " 실패: productGroupId="
                            + productGroupId
                            + ", message="
                            + response.message());
        }
    }
}
