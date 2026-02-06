package com.ryuqq.marketplace.adapter.in.rest.commoncode.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.query.SearchCommonCodesPageApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.response.CommonCodeApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.commoncode.dto.query.CommonCodeSearchParams;
import com.ryuqq.marketplace.application.commoncode.dto.response.CommonCodePageResult;
import com.ryuqq.marketplace.application.commoncode.dto.response.CommonCodeResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * CommonCodeQueryApiMapper - 공통 코드 Query API 변환 매퍼.
 *
 * <p>API Request/Response와 Application Query/Result 간 변환을 담당합니다.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-003: Application Result -> API Response 변환.
 *
 * <p>API-MAP-004: Slice/Page 변환 지원.
 *
 * <p>API-MAP-005: 순수 변환 로직만.
 *
 * <p>API-DTO-005: 날짜 String 변환 필수.
 *
 * <p>CQRS 분리: Query 전용 Mapper (CommandApiMapper와 분리).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class CommonCodeQueryApiMapper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    /**
     * SearchCommonCodesPageApiRequest -> CommonCodeSearchParams 변환.
     *
     * <p>API-CTR-007: Controller 비즈니스 로직 금지 → Mapper에서 변환 처리.
     *
     * <p>CommonSearchParams 내부에서 기본값 처리를 수행하므로 Mapper는 단순 변환만 담당합니다.
     *
     * @param request 조회 요청 DTO
     * @return CommonCodeSearchParams 객체
     */
    public CommonCodeSearchParams toSearchParams(SearchCommonCodesPageApiRequest request) {
        int page = request.page() != null ? request.page() : DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;

        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        null, null, null, request.sortKey(), request.sortDirection(), page, size);

        return CommonCodeSearchParams.of(
                request.commonCodeTypeId(), request.active(), request.code(), searchParams);
    }

    /**
     * 단일 CommonCodeResult -> CommonCodeApiResponse 변환.
     *
     * <p>API-DTO-005: Response DTO는 String 타입으로 날짜/시간 표현.
     *
     * @param result CommonCodeResult
     * @return CommonCodeApiResponse
     */
    public CommonCodeApiResponse toResponse(CommonCodeResult result) {
        return new CommonCodeApiResponse(
                result.id(),
                result.commonCodeTypeId(),
                result.code(),
                result.displayName(),
                result.displayOrder(),
                result.active(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    /**
     * CommonCodeResult 목록 -> CommonCodeApiResponse 목록 변환.
     *
     * @param results CommonCodeResult 목록
     * @return CommonCodeApiResponse 목록
     */
    public List<CommonCodeApiResponse> toResponses(List<CommonCodeResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    /**
     * CommonCodePageResult -> PageApiResponse 변환.
     *
     * <p>API-CTR-011: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
     *
     * @param pageResult Application 페이지 결과 DTO
     * @return API 페이지 응답 DTO
     */
    public PageApiResponse<CommonCodeApiResponse> toPageResponse(CommonCodePageResult pageResult) {
        List<CommonCodeApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
