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
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.SettlementListItemApiResponse.HoldInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.settlement.dto.response.SettlementListItemApiResponse.SettlementAmountsApiResponse;
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

    // ==================== Result → API Response ====================

    /** 페이지 결과를 API 응답으로 변환합니다. */
    public PageApiResponse<SettlementListItemApiResponse> toPageResponse(
            SettlementEntryPageResult result) {
        List<SettlementListItemApiResponse> items =
                result.entries().stream().map(this::toListItemResponse).toList();
        return PageApiResponse.of(
                items,
                result.pageMeta().page(),
                result.pageMeta().size(),
                result.pageMeta().totalElements());
    }

    private SettlementListItemApiResponse toListItemResponse(SettlementEntryListResult entry) {
        String apiStatus = mapStatus(entry.entryStatus());
        boolean isCompleted = "COMPLETED".equals(apiStatus);

        SettlementAmountsApiResponse amounts =
                new SettlementAmountsApiResponse(
                        entry.salesAmount(),
                        entry.commissionAmount(),
                        entry.commissionRate(),
                        entry.settlementAmount(),
                        isCompleted ? entry.settlementAmount() : 0);

        HoldInfoApiResponse holdInfo = buildHoldInfo(entry);

        String expectedSettlementDay = formatDate(entry.eligibleAt());
        String settlementDay = isCompleted ? formatDate(entry.createdAt()) : null;

        return new SettlementListItemApiResponse(
                entry.entryId(),
                apiStatus,
                nullToEmpty(entry.orderItemId()), // V4 간극: orderItemId → orderId
                "", // orderNumber: Entry에 없음
                entry.sellerId(),
                amounts,
                "", // orderedAt: Entry에 없음
                null, // deliveredAt
                expectedSettlementDay,
                settlementDay,
                holdInfo);
    }

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

    private HoldInfoApiResponse buildHoldInfo(SettlementEntryListResult entry) {
        if (!"HOLD".equals(entry.entryStatus())) {
            return null;
        }
        String holdAt =
                entry.holdAt() != null ? DateTimeFormatUtils.formatIso8601(entry.holdAt()) : null;
        return new HoldInfoApiResponse(nullToEmpty(entry.holdReason()), holdAt);
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
                0, // ourMallOrderCount: 채널 구분 없음
                0, // externalMallOrderCount: 채널 구분 없음
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

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
}
