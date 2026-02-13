package com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.query;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;

/** 판매채널 검색 API 요청 DTO. */
public record SearchSalesChannelsApiRequest(
        @Parameter(description = "상태 필터 (ACTIVE, INACTIVE)") List<String> statuses,
        @Parameter(description = "검색 필드 (CHANNEL_NAME)") String searchField,
        @Parameter(description = "검색어") String searchWord,
        @Parameter(description = "정렬 키 (createdAt, channelName)") String sortKey,
        @Parameter(description = "정렬 방향 (ASC, DESC)") String sortDirection,
        @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
                @Schema(description = "페이지 번호 (0부터 시작)", minimum = "0")
                @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
                Integer page,
        @Parameter(description = "페이지 크기", example = "20")
                @Schema(description = "페이지 크기", minimum = "1", maximum = "100")
                @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
                @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
                Integer size) {}
