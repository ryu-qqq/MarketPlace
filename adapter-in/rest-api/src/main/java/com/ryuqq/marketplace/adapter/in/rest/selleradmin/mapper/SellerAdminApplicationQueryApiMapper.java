package com.ryuqq.marketplace.adapter.in.rest.selleradmin.mapper;

import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.query.SearchSellerAdminApplicationsApiRequest;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.selleradmin.dto.query.GetSellerAdminApplicationQuery;
import com.ryuqq.marketplace.application.selleradmin.dto.query.SellerAdminApplicationSearchParams;
import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * SellerAdminApplicationQueryApiMapper - API Request → Application Query 변환.
 *
 * <p>API-MAP-001: Mapper는 @Component로 정의.
 *
 * <p>API-MAP-002: Request → Query 변환만 담당.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Component
public class SellerAdminApplicationQueryApiMapper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final String DEFAULT_SORT_KEY = "createdAt";
    private static final String DEFAULT_SORT_DIRECTION = "DESC";

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 상세 조회 Path Variable을 GetQuery로 변환합니다.
     *
     * @param sellerAdminId 셀러 관리자 ID
     * @return Application GetQuery
     */
    public GetSellerAdminApplicationQuery toGetQuery(String sellerAdminId) {
        return GetSellerAdminApplicationQuery.of(sellerAdminId);
    }

    /**
     * 목록 조회 API Request를 SearchParams로 변환합니다.
     *
     * @param request API Request
     * @return Application SearchParams
     */
    public SellerAdminApplicationSearchParams toSearchParams(
            SearchSellerAdminApplicationsApiRequest request) {

        CommonSearchParams commonParams =
                CommonSearchParams.of(
                        false,
                        null,
                        null,
                        resolveString(request.sortKey(), DEFAULT_SORT_KEY),
                        resolveString(request.sortDirection(), DEFAULT_SORT_DIRECTION),
                        resolveInt(request.page(), DEFAULT_PAGE),
                        resolveInt(request.size(), DEFAULT_SIZE));

        List<SellerAdminStatus> status = parseStatusList(request.status());
        DateRange dateRange = parseDateRange(request.startDate(), request.endDate());

        return SellerAdminApplicationSearchParams.of(
                request.sellerIds(),
                status,
                request.searchField(),
                request.searchWord(),
                dateRange,
                commonParams);
    }

    private List<SellerAdminStatus> parseStatusList(List<String> statusList) {
        if (statusList == null || statusList.isEmpty()) {
            return List.of();
        }
        return statusList.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(this::parseStatus)
                .filter(Objects::nonNull)
                .toList();
    }

    private SellerAdminStatus parseStatus(String status) {
        try {
            return SellerAdminStatus.valueOf(status.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private DateRange parseDateRange(String startDateStr, String endDateStr) {
        LocalDate startDate = parseDate(startDateStr);
        LocalDate endDate = parseDate(endDateStr);

        if (startDate == null && endDate == null) {
            return null;
        }
        return DateRange.of(startDate, endDate);
    }

    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    private int resolveInt(Integer value, int defaultValue) {
        return value != null ? value : defaultValue;
    }

    private String resolveString(String value, String defaultValue) {
        return value != null && !value.isBlank() ? value : defaultValue;
    }
}
