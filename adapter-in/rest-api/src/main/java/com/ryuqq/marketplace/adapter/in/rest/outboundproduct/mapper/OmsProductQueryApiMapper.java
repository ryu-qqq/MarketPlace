package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query.SearchOmsProductsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query.SearchSyncHistoryApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse.BrandResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse.OptionResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse.PriceResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse.ProductGroupResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse.ProductResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse.ProductStatusResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse.SyncSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.SyncHistoryApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsProductSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.SyncHistorySearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductDetailResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductListResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductPageResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryListResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryPageResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncSummaryResult;
import com.ryuqq.marketplace.application.product.dto.response.ProductDetailResult;
import com.ryuqq.marketplace.application.product.dto.response.ResolvedProductOptionResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageResult;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** OMS Query API 매퍼. */
@Component
public class OmsProductQueryApiMapper {

    public OmsProductSearchParams toSearchParams(SearchOmsProductsApiRequest request) {
        CommonSearchParams commonParams =
                CommonSearchParams.of(
                        false,
                        parseDate(request.startDate()),
                        parseDate(request.endDate()),
                        request.sortKey(),
                        request.sortDirection(),
                        request.page() != null ? request.page() : 0,
                        request.size() != null ? request.size() : 10);

        return new OmsProductSearchParams(
                request.dateType(),
                request.statuses(),
                request.syncStatuses(),
                request.searchField(),
                request.searchWord(),
                request.shopIds(),
                request.partnerIds(),
                request.productCodes(),
                commonParams);
    }

    public SyncHistorySearchParams toSyncHistoryParams(
            long productGroupId, SearchSyncHistoryApiRequest request) {
        CommonSearchParams commonParams =
                CommonSearchParams.of(
                        false,
                        null,
                        null,
                        "createdAt",
                        "DESC",
                        request.page() != null ? request.page() : 0,
                        request.size() != null ? request.size() : 10);

        return new SyncHistorySearchParams(productGroupId, request.status(), commonParams);
    }

    public PageApiResponse<OmsProductApiResponse> toProductPageResponse(
            OmsProductPageResult pageResult) {
        List<OmsProductApiResponse> responses =
                pageResult.results().stream().map(this::toProductResponse).toList();
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }

    public OmsProductDetailApiResponse toDetailResponse(OmsProductDetailResult result) {
        ProductGroupDetailCompositeResult pg = result.productGroup();

        List<ProductDetailResult> productDetails =
                pg.optionProductMatrix() != null ? pg.optionProductMatrix().products() : List.of();

        ProductGroupResponse productGroup = toProductGroupResponse(pg, productDetails);

        int basePrice = !productDetails.isEmpty() ? productDetails.get(0).currentPrice() : 0;
        List<ProductResponse> products =
                productDetails.stream().map(p -> toDetailProductResponse(p, basePrice)).toList();

        SyncSummaryResult ss = result.syncSummary();
        SyncSummaryApiResponse syncSummary =
                new SyncSummaryApiResponse(
                        ss.totalSyncCount(),
                        ss.successCount(),
                        ss.failCount(),
                        ss.pendingCount(),
                        DateTimeFormatUtils.formatIso8601(ss.lastSyncAt()));

        return new OmsProductDetailApiResponse(productGroup, products, syncSummary);
    }

    public PageApiResponse<SyncHistoryApiResponse> toSyncHistoryPageResponse(
            SyncHistoryPageResult pageResult) {
        List<SyncHistoryApiResponse> responses =
                pageResult.results().stream().map(this::toSyncHistoryResponse).toList();
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }

    // -- private helpers --

    private ProductGroupResponse toProductGroupResponse(
            ProductGroupDetailCompositeResult pg, List<ProductDetailResult> products) {
        BrandResponse brand = new BrandResponse(pg.brandId(), pg.brandName(), null);
        PriceResponse price = resolveGroupPrice(products);
        String mainImageUrl = resolveMainImageUrl(pg.images());
        ProductStatusResponse groupStatus = resolveProductStatus(pg.status());

        return new ProductGroupResponse(
                pg.id(),
                pg.productGroupName(),
                pg.sellerId(),
                pg.sellerName(),
                pg.categoryId(),
                pg.optionType(),
                null,
                brand,
                price,
                mainImageUrl,
                pg.categoryDisplayPath(),
                groupStatus,
                DateTimeFormatUtils.formatIso8601(pg.createdAt()),
                DateTimeFormatUtils.formatIso8601(pg.updatedAt()),
                null,
                null);
    }

    private PriceResponse resolveGroupPrice(List<ProductDetailResult> products) {
        if (products.isEmpty()) {
            return new PriceResponse(0, 0, 0, 0, 0, 0);
        }
        ProductDetailResult first = products.get(0);
        int salePrice = first.salePrice() != null ? first.salePrice() : first.currentPrice();
        return new PriceResponse(
                first.regularPrice(), first.currentPrice(), salePrice, 0, 0, first.discountRate());
    }

    private String resolveMainImageUrl(List<ProductGroupImageResult> images) {
        return images.stream()
                .filter(img -> "MAIN".equals(img.imageType()))
                .findFirst()
                .map(img -> img.uploadedUrl() != null ? img.uploadedUrl() : img.originUrl())
                .orElse(null);
    }

    private ProductStatusResponse resolveProductStatus(String status) {
        boolean soldOut = "SOLDOUT".equalsIgnoreCase(status);
        boolean display = "ACTIVE".equalsIgnoreCase(status);
        return new ProductStatusResponse(soldOut ? "Y" : "N", display ? "Y" : "N");
    }

    private ProductResponse toDetailProductResponse(ProductDetailResult p, int basePrice) {
        ProductStatusResponse productStatus = resolveProductStatus(p.status());
        String option =
                p.options().stream()
                        .map(ResolvedProductOptionResult::optionValueName)
                        .collect(Collectors.joining(" / "));
        List<OptionResponse> options =
                p.options().stream()
                        .map(
                                o ->
                                        new OptionResponse(
                                                o.sellerOptionGroupId(),
                                                o.sellerOptionValueId(),
                                                o.optionGroupName(),
                                                o.optionValueName()))
                        .toList();
        int additionalPrice = Math.max(p.currentPrice() - basePrice, 0);
        return new ProductResponse(
                p.id(), p.stockQuantity(), productStatus, option, options, additionalPrice);
    }

    private OmsProductApiResponse toProductResponse(OmsProductListResult r) {
        return new OmsProductApiResponse(
                r.id(),
                r.productCode(),
                r.productName(),
                r.imageUrl(),
                r.price(),
                r.stock(),
                r.status(),
                r.statusLabel(),
                r.partnerName(),
                DateTimeFormatUtils.formatIso8601(r.createdAt()),
                r.syncStatus(),
                r.syncStatusLabel(),
                DateTimeFormatUtils.formatIso8601(r.lastSyncAt()));
    }

    private SyncHistoryApiResponse toSyncHistoryResponse(SyncHistoryListResult r) {
        return new SyncHistoryApiResponse(
                r.id(),
                generateJobId(r.id(), r.requestedAt()),
                r.shopName(),
                r.accountId(),
                r.presetName(),
                r.status(),
                r.statusLabel(),
                DateTimeFormatUtils.formatIso8601(r.requestedAt()),
                DateTimeFormatUtils.formatIso8601(r.completedAt()),
                r.externalProductId(),
                r.errorMessage(),
                r.retryCount());
    }

    private String generateJobId(long outboxId, java.time.Instant requestedAt) {
        if (requestedAt == null) {
            return "SYNC-" + outboxId;
        }
        String dateStr =
                requestedAt
                        .atZone(java.time.ZoneId.of("Asia/Seoul"))
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("SYNC-%s-%03d", dateStr, outboxId);
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
