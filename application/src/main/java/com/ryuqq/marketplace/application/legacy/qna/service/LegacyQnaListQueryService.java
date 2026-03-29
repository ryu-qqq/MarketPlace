package com.ryuqq.marketplace.application.legacy.qna.service;

import com.ryuqq.marketplace.application.brand.manager.BrandReadManager;
import com.ryuqq.marketplace.application.legacy.order.resolver.LegacyOrderIdResolver;
import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacyProductIdResolver;
import com.ryuqq.marketplace.application.order.port.in.query.GetOrderDetailUseCase;
import com.ryuqq.marketplace.application.legacy.qna.dto.query.LegacyQnaSearchParams;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaDetailResult;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaPageResult;
import com.ryuqq.marketplace.application.legacy.qna.port.in.LegacyQnaListQueryUseCase;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.qna.dto.query.QnaSearchCondition;
import com.ryuqq.marketplace.application.qna.dto.result.QnaListResult;
import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;
import com.ryuqq.marketplace.application.qna.port.in.query.GetQnaListUseCase;
import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 QnA 목록 조회 서비스.
 *
 * <p>market 스키마의 표준 GetQnaListUseCase를 호출하고, 결과를 레거시 응답 형태로 변환합니다.
 */
@Service
public class LegacyQnaListQueryService implements LegacyQnaListQueryUseCase {

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    private final GetQnaListUseCase getQnaListUseCase;
    private final SellerReadManager sellerReadManager;
    private final ProductGroupReadManager productGroupReadManager;
    private final LegacyProductIdResolver productIdResolver;
    private final BrandReadManager brandReadManager;
    private final LegacyOrderIdResolver orderIdResolver;
    private final GetOrderDetailUseCase getOrderDetailUseCase;

    public LegacyQnaListQueryService(
            GetQnaListUseCase getQnaListUseCase,
            SellerReadManager sellerReadManager,
            ProductGroupReadManager productGroupReadManager,
            LegacyProductIdResolver productIdResolver,
            BrandReadManager brandReadManager,
            LegacyOrderIdResolver orderIdResolver,
            GetOrderDetailUseCase getOrderDetailUseCase) {
        this.getQnaListUseCase = getQnaListUseCase;
        this.sellerReadManager = sellerReadManager;
        this.productGroupReadManager = productGroupReadManager;
        this.productIdResolver = productIdResolver;
        this.brandReadManager = brandReadManager;
        this.orderIdResolver = orderIdResolver;
        this.getOrderDetailUseCase = getOrderDetailUseCase;
    }

    @Override
    @Transactional(readOnly = true)
    public LegacyQnaPageResult execute(LegacyQnaSearchParams params) {
        QnaSearchCondition condition = toSearchCondition(params);
        QnaListResult listResult = getQnaListUseCase.execute(condition);

        List<SellerId> sellerIds =
                listResult.items().stream().map(r -> SellerId.of(r.sellerId())).distinct().toList();
        Map<Long, String> sellerNameMap =
                sellerReadManager.getByIds(sellerIds).stream()
                        .collect(
                                Collectors.toMap(
                                        s -> s.id().value(),
                                        s -> s.sellerName().value(),
                                        (a, b) -> a));

        // QnA의 productGroupId는 레거시 PK → 내부 PK로 변환
        Map<Long, Long> legacyToInternalPgMap =
                listResult.items().stream()
                        .filter(r -> r.productGroupId() != 0)
                        .map(QnaResult::productGroupId)
                        .distinct()
                        .collect(Collectors.toMap(
                                id -> id,
                                id -> productIdResolver.resolveProductGroupId(id),
                                (a, b) -> a));

        List<ProductGroupId> internalPgIds =
                legacyToInternalPgMap.values().stream()
                        .distinct()
                        .map(ProductGroupId::of)
                        .toList();
        Map<Long, ProductGroup> pgMap =
                productGroupReadManager.findByIds(internalPgIds).stream()
                        .collect(Collectors.toMap(pg -> pg.id().value(), pg -> pg, (a, b) -> a));

        // brand 정보 조회
        Map<Long, String> brandNameMap =
                pgMap.values().stream()
                        .map(pg -> pg.brandId().value())
                        .distinct()
                        .collect(Collectors.toMap(
                                id -> id,
                                id -> {
                                    try {
                                        return brandReadManager.getById(BrandId.of(id)).nameKo();
                                    } catch (Exception e) {
                                        return "";
                                    }
                                },
                                (a, b) -> a));

        List<LegacyQnaDetailResult> items =
                listResult.items().stream()
                        .map(r -> toDetailResult(r, sellerNameMap, legacyToInternalPgMap, pgMap, brandNameMap))
                        .toList();

        Long lastDomainId = items.isEmpty() ? null : items.getLast().qnaId();
        return new LegacyQnaPageResult(items, listResult.totalCount(), lastDomainId);
    }

    private LegacyQnaDetailResult toDetailResult(
            QnaResult r,
            Map<Long, String> sellerNameMap,
            Map<Long, Long> legacyToInternalPgMap,
            Map<Long, ProductGroup> pgMap,
            Map<Long, String> brandNameMap) {
        String sellerName = sellerNameMap.getOrDefault(r.sellerId(), "");
        Long internalPgId = legacyToInternalPgMap.getOrDefault(r.productGroupId(), r.productGroupId());
        ProductGroup pg = pgMap.get(internalPgId);
        String pgName = pg != null ? pg.productGroupName().value() : "";
        String mainImageUrl = "";
        Long brandId = pg != null ? pg.brandId().value() : 0L;
        String brandName = brandNameMap.getOrDefault(brandId, "");

        // 주문문의: 상품 정보가 없으면 주문에서 보충
        if (pgName.isEmpty() && r.orderId() != null && r.orderId() > 0) {
            try {
                var mapping = orderIdResolver.resolve(r.orderId());
                if (mapping.isPresent()) {
                    var detail = getOrderDetailUseCase.execute(mapping.get().internalOrderItemId());
                    var product = detail.productOrder();
                    if (product != null) {
                        pgName = product.productGroupName() != null ? product.productGroupName() : "";
                        mainImageUrl = product.mainImageUrl() != null ? product.mainImageUrl() : "";
                        brandName = product.brandName() != null ? product.brandName() : "";
                        brandId = product.brandId() != null ? product.brandId() : 0L;
                    }
                }
            } catch (Exception ignored) {
            }
        }

        return LegacyQnaFromMarketAssembler.toDetailResult(
                r, sellerName, pgName, mainImageUrl, brandId, brandName);
    }

    private QnaSearchCondition toSearchCondition(LegacyQnaSearchParams params) {
        QnaStatus status = parseStatus(params.qnaStatus());
        QnaType qnaType = parseQnaType(params.qnaType());

        return new QnaSearchCondition(
                params.sellerId(),
                status,
                qnaType,
                params.searchKeyword(),
                params.startDate() != null ? params.startDate().atZone(ZONE_ID).toInstant() : null,
                params.endDate() != null ? params.endDate().atZone(ZONE_ID).toInstant() : null,
                params.lastDomainId(),
                params.size());
    }

    private QnaStatus parseStatus(String legacyStatus) {
        if (legacyStatus == null || legacyStatus.isEmpty()) {
            return null;
        }
        return switch (legacyStatus) {
            case "OPEN" -> QnaStatus.PENDING;
            case "CLOSED" -> QnaStatus.CLOSED;
            default -> null;
        };
    }

    private QnaType parseQnaType(String legacyType) {
        if (legacyType == null || legacyType.isEmpty()) {
            return null;
        }
        try {
            return QnaType.valueOf(legacyType);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
