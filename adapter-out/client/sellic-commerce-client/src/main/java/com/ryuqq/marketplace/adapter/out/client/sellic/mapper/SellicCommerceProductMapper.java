package com.ryuqq.marketplace.adapter.out.client.sellic.mapper;

import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicProductRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicProductRegistrationRequest.SellicProductStock;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicProductStockUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicProductUpdateRequest;
import com.ryuqq.marketplace.application.product.dto.response.ProductOptionMappingResult;
import com.ryuqq.marketplace.application.product.dto.response.ProductResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData;
import com.ryuqq.marketplace.application.productgroup.dto.response.SellerOptionGroupResult;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageResult;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * ProductGroupSyncData -> Sellic Commerce 요청 DTO 변환 매퍼.
 *
 * <p>셀릭 OpenAPI 스펙에 맞춰 변환합니다.
 */
@Component
@ConditionalOnProperty(prefix = "sellic-commerce", name = "customer-id")
public class SellicCommerceProductMapper {

    /** 셀릭 판매상태: 판매중. */
    private static final int SALE_STATUS_ON_SALE = 2000;

    /** 셀릭 판매상태: 판매종료 (삭제 대용). */
    private static final int SALE_STATUS_TERMINATED = 2004;

    /** 셀릭 배송비: 무료. */
    private static final int DELIVERY_FREE = 1296;

    /** 셀릭 과세: 과세. */
    private static final int TAX_TAXABLE = 1286;

    /**
     * 상품 등록 요청 변환.
     *
     * @param syncData 상품 그룹 동기화 데이터
     * @param customerId 셀릭 고객사 ID
     * @param apiKey 셀릭 API Key
     * @return 셀릭 상품 등록 요청 DTO
     */
    public SellicProductRegistrationRequest toRegistrationRequest(
            ProductGroupSyncData syncData, String customerId, String apiKey) {

        ProductGroupDetailCompositeQueryResult query = syncData.queryResult();
        List<String> imageUrls = resolveImageUrls(syncData.images());
        List<SellerOptionGroupResult> optionGroups = syncData.optionGroups();
        List<String> optionNames = resolveOptionNames(optionGroups);
        List<SellicProductStock> stocks = resolveProductStocks(syncData.products(), optionGroups);

        int marketPrice = resolveMarketPrice(syncData.products());
        int salePrice = resolveSalePrice(syncData.products());

        return new SellicProductRegistrationRequest(
                customerId,
                apiKey,
                query.productGroupName(),
                String.valueOf(query.id()),
                0,
                null,
                null,
                SALE_STATUS_ON_SALE,
                DELIVERY_FREE,
                null,
                TAX_TAXABLE,
                query.brandName(),
                null,
                null,
                null,
                syncData.descriptionContent().orElse(""),
                marketPrice,
                salePrice,
                imageUrls.size() > 0 ? imageUrls.get(0) : null,
                imageUrls.size() > 1 ? imageUrls.get(1) : null,
                imageUrls.size() > 2 ? imageUrls.get(2) : null,
                imageUrls.size() > 3 ? imageUrls.get(3) : null,
                imageUrls.size() > 4 ? imageUrls.get(4) : null,
                imageUrls.size() > 5 ? imageUrls.get(5) : null,
                imageUrls.size() > 6 ? imageUrls.get(6) : null,
                imageUrls.size() > 7 ? imageUrls.get(7) : null,
                imageUrls.size() > 8 ? imageUrls.get(8) : null,
                imageUrls.size() > 9 ? imageUrls.get(9) : null,
                imageUrls.size() > 10 ? imageUrls.get(10) : null,
                imageUrls.size() > 11 ? imageUrls.get(11) : null,
                imageUrls.size() > 12 ? imageUrls.get(12) : null,
                imageUrls.size() > 13 ? imageUrls.get(13) : null,
                imageUrls.size() > 14 ? imageUrls.get(14) : null,
                imageUrls.size() > 15 ? imageUrls.get(15) : null,
                imageUrls.size() > 16 ? imageUrls.get(16) : null,
                null,
                null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                optionNames.size() > 0 ? optionNames.get(0) : "단품",
                optionNames.size() > 1 ? optionNames.get(1) : null,
                optionNames.size() > 2 ? optionNames.get(2) : null,
                optionNames.size() > 3 ? optionNames.get(3) : null,
                stocks);
    }

    /**
     * 상품 수정 요청 변환.
     *
     * @param syncData 상품 그룹 동기화 데이터
     * @param externalProductId 셀릭 외부 상품 ID
     * @param customerId 셀릭 고객사 ID
     * @param apiKey 셀릭 API Key
     * @return 셀릭 상품 수정 요청 DTO
     */
    public SellicProductUpdateRequest toUpdateRequest(
            ProductGroupSyncData syncData,
            String externalProductId,
            String customerId,
            String apiKey) {

        ProductGroupDetailCompositeQueryResult query = syncData.queryResult();
        List<String> imageUrls = resolveImageUrls(syncData.images());
        List<SellerOptionGroupResult> optionGroups = syncData.optionGroups();
        List<String> optionNames = resolveOptionNames(optionGroups);
        List<SellicProductStock> stocks = resolveProductStocks(syncData.products(), optionGroups);

        int marketPrice = resolveMarketPrice(syncData.products());
        int salePrice = resolveSalePrice(syncData.products());
        int saleStatus = syncData.soldout() ? SALE_STATUS_TERMINATED : SALE_STATUS_ON_SALE;

        return new SellicProductUpdateRequest(
                customerId,
                apiKey,
                externalProductId,
                query.productGroupName(),
                String.valueOf(query.id()),
                0,
                null,
                null,
                saleStatus,
                DELIVERY_FREE,
                null,
                TAX_TAXABLE,
                query.brandName(),
                null,
                null,
                null,
                syncData.descriptionContent().orElse(""),
                marketPrice,
                salePrice,
                imageUrls.size() > 0 ? imageUrls.get(0) : null,
                imageUrls.size() > 1 ? imageUrls.get(1) : null,
                imageUrls.size() > 2 ? imageUrls.get(2) : null,
                imageUrls.size() > 3 ? imageUrls.get(3) : null,
                imageUrls.size() > 4 ? imageUrls.get(4) : null,
                imageUrls.size() > 5 ? imageUrls.get(5) : null,
                imageUrls.size() > 6 ? imageUrls.get(6) : null,
                imageUrls.size() > 7 ? imageUrls.get(7) : null,
                imageUrls.size() > 8 ? imageUrls.get(8) : null,
                imageUrls.size() > 9 ? imageUrls.get(9) : null,
                imageUrls.size() > 10 ? imageUrls.get(10) : null,
                imageUrls.size() > 11 ? imageUrls.get(11) : null,
                imageUrls.size() > 12 ? imageUrls.get(12) : null,
                imageUrls.size() > 13 ? imageUrls.get(13) : null,
                imageUrls.size() > 14 ? imageUrls.get(14) : null,
                imageUrls.size() > 15 ? imageUrls.get(15) : null,
                imageUrls.size() > 16 ? imageUrls.get(16) : null,
                null,
                null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                optionNames.size() > 0 ? optionNames.get(0) : "단품",
                optionNames.size() > 1 ? optionNames.get(1) : null,
                optionNames.size() > 2 ? optionNames.get(2) : null,
                optionNames.size() > 3 ? optionNames.get(3) : null,
                stocks);
    }

    /**
     * 삭제 요청 변환 (sale_status=2004 판매종료).
     *
     * @param externalProductId 셀릭 외부 상품 ID
     * @param customerId 셀릭 고객사 ID
     * @param apiKey 셀릭 API Key
     * @return 셀릭 상품 수정 요청 DTO (최소 필드만 포함)
     */
    public SellicProductUpdateRequest toDeleteRequest(
            String externalProductId, String customerId, String apiKey) {

        // 57 fields: customerId(1), apiKey(2), productId(3), productName(4), ownCode(5),
        // origin(6), supplierName(7), categoryId(8), saleStatus(9), deliveryChargeType(10),
        // deliveryFee(11), tax(12), brand(13), model(14), modelNo(15), keyword(16),
        // detailNote(17), marketPrice(18), salePrice(19), image1(20), image7~22(21-36),
        // notifyCode(37), notify1~15(38-52), optionName1~4(53-56), productStocks(57)
        return new SellicProductUpdateRequest(
                customerId,                     // 1
                apiKey,                         // 2
                externalProductId,              // 3
                null, null, null, null, null,   // 4-8
                SALE_STATUS_TERMINATED,         // 9
                null, null, null,               // 10-12
                null, null, null, null, null,   // 13-17
                null, null,                     // 18-19
                null, null, null, null, null,   // 20-24
                null, null, null, null, null,   // 25-29
                null, null, null, null, null,   // 30-34
                null, null,                     // 35-36
                null,                           // 37
                null, null, null, null, null,   // 38-42
                null, null, null, null, null,   // 43-47
                null, null, null, null, null,   // 48-52
                null, null, null, null,         // 53-56
                null);                          // 57
    }

    /**
     * 재고 수정 요청 변환.
     *
     * @param syncData 상품 그룹 동기화 데이터
     * @param externalProductId 셀릭 외부 상품 ID
     * @param customerId 셀릭 고객사 ID
     * @param apiKey 셀릭 API Key
     * @return 셀릭 재고 수정 요청 DTO
     */
    public SellicProductStockUpdateRequest toStockUpdateRequest(
            ProductGroupSyncData syncData,
            String externalProductId,
            String customerId,
            String apiKey) {

        List<SellerOptionGroupResult> optionGroups = syncData.optionGroups();
        List<String> optionNames = resolveOptionNames(optionGroups);
        List<SellicProductStock> stocks = resolveProductStocks(syncData.products(), optionGroups);

        return new SellicProductStockUpdateRequest(
                customerId,
                apiKey,
                externalProductId,
                optionNames.size() > 0 ? optionNames.get(0) : "단품",
                optionNames.size() > 1 ? optionNames.get(1) : null,
                optionNames.size() > 2 ? optionNames.get(2) : null,
                optionNames.size() > 3 ? optionNames.get(3) : null,
                stocks);
    }

    // ===== 내부 메서드 =====

    private List<String> resolveImageUrls(List<ProductGroupImageResult> images) {
        return images.stream()
                .sorted(Comparator.comparingInt(ProductGroupImageResult::sortOrder))
                .map(img -> img.uploadedUrl() != null ? img.uploadedUrl() : img.originUrl())
                .toList();
    }

    private List<String> resolveOptionNames(List<SellerOptionGroupResult> optionGroups) {
        return optionGroups.stream()
                .sorted(Comparator.comparingInt(SellerOptionGroupResult::sortOrder))
                .map(SellerOptionGroupResult::optionGroupName)
                .toList();
    }

    private List<SellicProductStock> resolveProductStocks(
            List<ProductResult> products, List<SellerOptionGroupResult> optionGroups) {

        Map<Long, String> optionValueNameMap =
                optionGroups.stream()
                        .flatMap(g -> g.optionValues().stream())
                        .collect(Collectors.toMap(v -> v.id(), v -> v.optionValueName()));

        List<SellicProductStock> stocks = new ArrayList<>();

        for (ProductResult product : products) {
            List<ProductOptionMappingResult> mappings = product.optionMappings();

            String item1 = null;
            String item2 = null;
            String item3 = null;
            String item4 = null;

            if (mappings != null) {
                List<ProductOptionMappingResult> sorted =
                        mappings.stream()
                                .sorted(
                                        Comparator.comparing(
                                                ProductOptionMappingResult::optionGroupName))
                                .toList();

                if (sorted.size() > 0) item1 = sorted.get(0).optionValueName();
                if (sorted.size() > 1) item2 = sorted.get(1).optionValueName();
                if (sorted.size() > 2) item3 = sorted.get(2).optionValueName();
                if (sorted.size() > 3) item4 = sorted.get(3).optionValueName();
            }

            if (item1 == null) {
                item1 = "단품";
            }

            stocks.add(
                    new SellicProductStock(
                            product.stockQuantity(),
                            item1,
                            item2,
                            item3,
                            item4,
                            product.skuCode(),
                            null,
                            0));
        }

        return stocks;
    }

    private int resolveMarketPrice(List<ProductResult> products) {
        return products.stream()
                .mapToInt(ProductResult::regularPrice)
                .max()
                .orElse(0);
    }

    private int resolveSalePrice(List<ProductResult> products) {
        return products.stream()
                .mapToInt(ProductResult::currentPrice)
                .min()
                .orElse(0);
    }
}
