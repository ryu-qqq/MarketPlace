package com.ryuqq.marketplace.application.legacy.qna.service;

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

    public LegacyQnaListQueryService(
            GetQnaListUseCase getQnaListUseCase,
            SellerReadManager sellerReadManager,
            ProductGroupReadManager productGroupReadManager) {
        this.getQnaListUseCase = getQnaListUseCase;
        this.sellerReadManager = sellerReadManager;
        this.productGroupReadManager = productGroupReadManager;
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

        List<ProductGroupId> pgIds =
                listResult.items().stream()
                        .filter(r -> r.productGroupId() != 0)
                        .map(r -> ProductGroupId.of(r.productGroupId()))
                        .distinct()
                        .toList();
        Map<Long, ProductGroup> pgMap =
                productGroupReadManager.findByIds(pgIds).stream()
                        .collect(Collectors.toMap(pg -> pg.id().value(), pg -> pg, (a, b) -> a));

        List<LegacyQnaDetailResult> items =
                listResult.items().stream()
                        .map(r -> toDetailResult(r, sellerNameMap, pgMap))
                        .toList();

        Long lastDomainId = items.isEmpty() ? null : items.getLast().qnaId();
        return new LegacyQnaPageResult(items, listResult.totalCount(), lastDomainId);
    }

    private LegacyQnaDetailResult toDetailResult(
            QnaResult r, Map<Long, String> sellerNameMap, Map<Long, ProductGroup> pgMap) {
        String sellerName = sellerNameMap.getOrDefault(r.sellerId(), "");
        ProductGroup pg = pgMap.get(r.productGroupId());
        String pgName = pg != null ? pg.productGroupName().value() : "";
        Long brandId = pg != null ? pg.brandId().value() : 0L;
        return LegacyQnaFromMarketAssembler.toDetailResult(r, sellerName, pgName, "", brandId, "");
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
