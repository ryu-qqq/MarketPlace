package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductListCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductMainImageDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductPriceStockDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductSyncInfoDto;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductListResult;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * OMS 상품 목록 Composition 매퍼.
 *
 * <p>composite + enrichment 데이터를 {@link OmsProductListResult}로 변환한다.
 */
@Component
public class OmsProductCompositionMapper {

    private static final OmsProductPriceStockDto EMPTY_PRICE_STOCK =
            new OmsProductPriceStockDto(0L, 0, 0);

    /**
     * composite + enrichment 데이터를 OmsProductListResult 목록으로 변환.
     *
     * @param composites base composite 목록
     * @param imageMap 상품그룹별 대표 이미지 URL
     * @param priceStockMap 상품그룹별 가격/재고
     * @param syncInfoMap 상품그룹별 연동상태
     * @return OmsProductListResult 목록
     */
    public List<OmsProductListResult> toResults(
            List<OmsProductListCompositeDto> composites,
            Map<Long, OmsProductMainImageDto> imageMap,
            Map<Long, OmsProductPriceStockDto> priceStockMap,
            Map<Long, OmsProductSyncInfoDto> syncInfoMap) {

        return composites.stream()
                .map(
                        c -> {
                            OmsProductMainImageDto img = imageMap.get(c.productGroupId());
                            String imageUrl = img != null ? img.imageUrl() : null;
                            OmsProductPriceStockDto ps =
                                    priceStockMap.getOrDefault(
                                            c.productGroupId(), EMPTY_PRICE_STOCK);
                            OmsProductSyncInfoDto si = syncInfoMap.get(c.productGroupId());

                            String statusLabel = resolveStatusLabel(c.status());

                            String syncStatus;
                            String syncStatusLabel;
                            java.time.Instant lastSyncAt;

                            if (si != null) {
                                syncStatus = mapSyncStatus(si.entityStatusName());
                                syncStatusLabel = resolveSyncStatusLabel(syncStatus);
                                lastSyncAt = si.processedAt();
                            } else {
                                syncStatus = "NONE";
                                syncStatusLabel = "미연동";
                                lastSyncAt = null;
                            }

                            return new OmsProductListResult(
                                    c.productGroupId(),
                                    "PG-" + c.productGroupId(),
                                    c.productGroupName(),
                                    imageUrl,
                                    ps.price(),
                                    ps.stock(),
                                    c.status(),
                                    statusLabel,
                                    c.sellerName(),
                                    c.createdAt(),
                                    syncStatus,
                                    syncStatusLabel,
                                    lastSyncAt);
                        })
                .toList();
    }

    String resolveStatusLabel(String status) {
        if (status == null) {
            return "";
        }
        try {
            return ProductGroupStatus.valueOf(status).displayName();
        } catch (IllegalArgumentException e) {
            return status;
        }
    }

    String mapSyncStatus(String entityStatusName) {
        SyncStatus syncStatus;
        try {
            syncStatus = SyncStatus.valueOf(entityStatusName);
        } catch (IllegalArgumentException e) {
            return "NONE";
        }
        return switch (syncStatus) {
            case COMPLETED -> "SUCCESS";
            case FAILED -> "FAILED";
            case PENDING, PROCESSING -> "PENDING";
        };
    }

    String resolveSyncStatusLabel(String syncStatus) {
        return switch (syncStatus) {
            case "SUCCESS" -> "연동완료";
            case "FAILED" -> "연동실패";
            case "PENDING" -> "연동대기";
            default -> "미연동";
        };
    }
}
