package com.ryuqq.marketplace.adapter.in.rest.settlement.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.DailySettlementApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementCompleteBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementEntryListApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementHoldBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.request.SettlementReleaseBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.DailySettlementApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.DailySettlementApiResponse.DiscountApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.DailySettlementApiResponse.FeeApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.DailySettlementApiResponse.MileageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.SettlementListItemApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.SettlementListItemApiResponse.HoldInfoV4;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.SettlementListItemApiResponse.SettlementAmountsV4;
import com.ryuqq.marketplace.adapter.in.rest.settlement.mapper.SettlementOrderEnricher.OrderContext;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.settlement.dto.query.DailySettlementSearchParams;
import com.ryuqq.marketplace.application.settlement.dto.response.DailySettlementResult;
import com.ryuqq.marketplace.application.settlement.dto.response.SettlementEntryPageResult;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.CompleteSettlementEntryBatchCommand;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.HoldSettlementEntryBatchCommand;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.ReleaseSettlementEntryBatchCommand;
import com.ryuqq.marketplace.application.settlement.entry.dto.query.SettlementEntrySearchParams;
import com.ryuqq.marketplace.application.settlement.entry.dto.response.SettlementEntryListResult;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Component;

/** 정산 API Mapper. Request ↔ Application, Result → Response 변환 담당. */
@Component
public class SettlementApiMapper {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    private final SettlementOrderEnricher enricher;

    public SettlementApiMapper(SettlementOrderEnricher enricher) {
        this.enricher = enricher;
    }

    // ==================== Request → Application Params ====================

    /**
     * GET 파라미터를 Application 검색 파라미터로 변환합니다.
     *
     * <p>프론트에서 PENDING/HOLD/COMPLETED 상태를 전달하면, COMPLETED → 내부 CONFIRMED + SETTLED 로 확장하여 조회합니다.
     */
    public SettlementEntrySearchParams toSearchParams(SettlementEntryListApiRequest request) {
        List<String> internalStatuses = expandStatuses(request.status());
        return new SettlementEntrySearchParams(
                internalStatuses,
                request.sellerIds(),
                request.searchField(),
                request.searchWord(),
                request.startDate(),
                request.endDate(),
                request.page() != null ? request.page() : 0,
                request.size() != null ? request.size() : 20);
    }

    // ==================== Result → API Response (V4 Enriched) ====================

    /**
     * 페이지 결과를 V4 API 응답으로 변환합니다.
     *
     * <p>SettlementOrderEnricher 를 사용하여 orderItemId 목록으로 주문 데이터를 배치 조회한 뒤, orderProduct / buyer /
     * seller / payment 중첩 필드를 채웁니다.
     */
    public PageApiResponse<SettlementListItemApiResponse> toPageResponse(
            SettlementEntryPageResult result) {

        List<SettlementEntryListResult> entries = result.entries();

        List<Long> orderItemIds =
                entries.stream()
                        .map(SettlementEntryListResult::orderItemId)
                        .filter(id -> id != null && id != 0L)
                        .toList();

        OrderContext ctx = enricher.loadOrderContext(orderItemIds);

        List<SettlementListItemApiResponse> items =
                entries.stream().map(entry -> toListItemResponse(entry, ctx)).toList();

        return PageApiResponse.of(
                items,
                result.pageMeta().page(),
                result.pageMeta().size(),
                result.pageMeta().totalElements());
    }

    private SettlementListItemApiResponse toListItemResponse(
            SettlementEntryListResult entry, OrderContext ctx) {

        String apiStatus = mapStatus(entry.entryStatus());
        boolean isCompleted = "COMPLETED".equals(apiStatus);
        Long orderItemId = entry.orderItemId();

        SettlementAmountsV4 amounts =
                new SettlementAmountsV4(
                        entry.salesAmount(),
                        entry.commissionAmount(),
                        entry.commissionRate(),
                        entry.settlementAmount(),
                        isCompleted ? entry.settlementAmount() : 0);

        HoldInfoV4 holdInfo = buildHoldInfo(entry);
        String expectedSettlementDay = formatDate(entry.eligibleAt());
        String settlementDay = isCompleted ? formatDate(entry.createdAt()) : null;

        OrderItemResult item = orderItemId != null ? ctx.getItem(orderItemId) : null;
        OrderListResult order = orderItemId != null ? ctx.getOrder(orderItemId) : null;

        return new SettlementListItemApiResponse(
                entry.entryId(),
                apiStatus,
                orderItemId != null ? String.valueOf(orderItemId) : "",
                order != null ? nullToEmpty(order.orderNumber()) : "",
                enricher.toOrderProductV4(item),
                enricher.toBuyerInfoV4(order),
                enricher.toSellerInfoV4(entry.sellerId(), item),
                enricher.toPaymentInfoV4(order),
                amounts,
                order != null ? formatInstant(order.createdAt()) : "",
                item != null ? "" : null,
                expectedSettlementDay,
                settlementDay,
                holdInfo);
    }

    // ==================== 상태 매핑 ====================

    /**
     * 내부 상태를 API 상태로 매핑합니다.
     *
     * <p>PENDING → PENDING, HOLD → HOLD, CONFIRMED/SETTLED → COMPLETED
     */
    private String mapStatus(String entryStatus) {
        if (entryStatus == null) {
            return "PENDING";
        }
        return switch (entryStatus) {
            case "CONFIRMED", "SETTLED" -> "COMPLETED";
            case "HOLD" -> "HOLD";
            default -> "PENDING";
        };
    }

    private HoldInfoV4 buildHoldInfo(SettlementEntryListResult entry) {
        if (!"HOLD".equals(entry.entryStatus())) {
            return null;
        }
        String holdAt =
                entry.holdAt() != null ? DateTimeFormatUtils.formatIso8601(entry.holdAt()) : null;
        return new HoldInfoV4(nullToEmpty(entry.holdReason()), holdAt);
    }

    // ==================== Daily 변환 ====================

    /** 일별 조회 요청을 Application 파라미터로 변환합니다. */
    public DailySettlementSearchParams toDailySearchParams(DailySettlementApiRequest request) {
        return new DailySettlementSearchParams(
                LocalDate.parse(request.startDate(), DATE_FORMATTER),
                LocalDate.parse(request.endDate(), DATE_FORMATTER),
                request.sellerIds() != null ? request.sellerIds() : List.of());
    }

    /** 일별 집계 결과를 페이지 API 응답으로 변환합니다. */
    public PageApiResponse<DailySettlementApiResponse> toDailyPageResponse(
            List<DailySettlementResult> results, int page, int size) {
        List<DailySettlementApiResponse> items =
                results.stream().map(this::toDailyResponse).toList();
        return PageApiResponse.of(items, page, size, items.size());
    }

    private DailySettlementApiResponse toDailyResponse(DailySettlementResult result) {
        int expectedAmount = result.totalSalesAmount() - result.totalCommissionAmount();
        return new DailySettlementApiResponse(
                result.settlementDay().format(DATE_FORMATTER),
                null,
                result.entryCount(),
                0,
                0,
                result.totalSalesAmount(),
                DiscountApiResponse.zero(),
                MileageApiResponse.zero(),
                new FeeApiResponse(result.totalCommissionAmount()),
                expectedAmount,
                result.totalSettlementAmount());
    }

    // ==================== Batch Request → Command ====================

    /** 일괄 완료 요청을 커맨드로 변환합니다. */
    public CompleteSettlementEntryBatchCommand toCompleteBatchCommand(
            SettlementCompleteBatchApiRequest request) {
        return new CompleteSettlementEntryBatchCommand(request.settlementIds());
    }

    /** 일괄 보류 요청을 커맨드로 변환합니다. */
    public HoldSettlementEntryBatchCommand toHoldBatchCommand(
            SettlementHoldBatchApiRequest request) {
        return new HoldSettlementEntryBatchCommand(request.settlementIds(), request.holdReason());
    }

    /** 일괄 보류 해제 요청을 커맨드로 변환합니다. */
    public ReleaseSettlementEntryBatchCommand toReleaseBatchCommand(
            SettlementReleaseBatchApiRequest request) {
        return new ReleaseSettlementEntryBatchCommand(request.settlementIds());
    }

    // ==================== 내부 유틸 ====================

    /**
     * 프론트 상태 목록을 내부 상태 목록으로 확장합니다.
     *
     * <p>COMPLETED → CONFIRMED + SETTLED
     */
    private List<String> expandStatuses(List<String> apiStatuses) {
        if (apiStatuses == null || apiStatuses.isEmpty()) {
            return List.of();
        }
        return apiStatuses.stream()
                .flatMap(
                        s ->
                                "COMPLETED".equals(s)
                                        ? List.of("CONFIRMED", "SETTLED").stream()
                                        : List.of(s).stream())
                .distinct()
                .toList();
    }

    private String formatDate(Instant instant) {
        if (instant == null) {
            return null;
        }
        LocalDate date = instant.atZone(ZONE_ID).toLocalDate();
        return date.format(DATE_FORMATTER);
    }

    private String formatInstant(Instant instant) {
        String formatted = DateTimeFormatUtils.formatDisplay(instant);
        return formatted != null ? formatted : "";
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
}
