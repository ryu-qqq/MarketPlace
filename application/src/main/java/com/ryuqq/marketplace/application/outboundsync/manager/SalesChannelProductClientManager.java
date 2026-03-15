package com.ryuqq.marketplace.application.outboundsync.manager;

import com.ryuqq.marketplace.application.outboundproductimage.dto.ResolvedExternalImages;
import com.ryuqq.marketplace.application.outboundsync.port.out.client.SalesChannelProductClient;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 판매채널 상품 클라이언트 매니저.
 *
 * <p>채널 코드 기반으로 적절한 {@link SalesChannelProductClient}를 라우팅합니다.
 */
@Component
public class SalesChannelProductClientManager {

    private final Map<String, SalesChannelProductClient> clientMap;

    public SalesChannelProductClientManager(List<SalesChannelProductClient> clients) {
        this.clientMap =
                clients.stream()
                        .collect(
                                Collectors.toMap(
                                        SalesChannelProductClient::channelCode,
                                        Function.identity()));
    }

    public String registerProduct(
            String channelCode,
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            SellerSalesChannel channel,
            Shop shop) {
        return resolve(channelCode)
                .registerProduct(bundle, externalCategoryId, externalBrandId, channel, shop);
    }

    public String registerProduct(
            String channelCode,
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            SellerSalesChannel channel,
            Shop shop,
            Long legacyProductGroupId,
            java.util.Map<Long, Long> legacyProductIdMap) {
        return resolve(channelCode)
                .registerProduct(
                        bundle,
                        externalCategoryId,
                        externalBrandId,
                        channel,
                        shop,
                        legacyProductGroupId,
                        legacyProductIdMap);
    }

    public String registerProduct(
            String channelCode,
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            SellerSalesChannel channel,
            Shop shop,
            ResolvedExternalImages resolvedImages) {
        return resolve(channelCode)
                .registerProduct(
                        bundle, externalCategoryId, externalBrandId, channel, shop, resolvedImages);
    }

    public void updateProduct(
            String channelCode,
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId,
            SellerSalesChannel channel,
            Set<ChangedArea> changedAreas) {
        resolve(channelCode)
                .updateProduct(
                        bundle,
                        externalCategoryId,
                        externalBrandId,
                        externalProductId,
                        channel,
                        changedAreas);
    }

    public void updateProduct(
            String channelCode,
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId,
            SellerSalesChannel channel,
            Set<ChangedArea> changedAreas,
            java.util.Map<Long, Long> legacyProductIdMap) {
        resolve(channelCode)
                .updateProduct(
                        bundle,
                        externalCategoryId,
                        externalBrandId,
                        externalProductId,
                        channel,
                        changedAreas,
                        legacyProductIdMap);
    }

    public void updateProduct(
            String channelCode,
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId,
            SellerSalesChannel channel,
            Set<ChangedArea> changedAreas,
            ResolvedExternalImages resolvedImages) {
        resolve(channelCode)
                .updateProduct(
                        bundle,
                        externalCategoryId,
                        externalBrandId,
                        externalProductId,
                        channel,
                        changedAreas,
                        resolvedImages);
    }

    public void deleteProduct(
            String channelCode, String externalProductId, SellerSalesChannel channel) {
        resolve(channelCode).deleteProduct(externalProductId, channel);
    }

    private SalesChannelProductClient resolve(String channelCode) {
        SalesChannelProductClient client = clientMap.get(channelCode);
        if (client == null) {
            throw new IllegalArgumentException("지원하지 않는 판매채널입니다: channelCode=" + channelCode);
        }
        return client;
    }
}
