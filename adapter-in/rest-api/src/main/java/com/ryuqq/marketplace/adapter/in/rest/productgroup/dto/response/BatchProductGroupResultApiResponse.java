package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response;

import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 상품 그룹 배치 등록 결과 API 응답.
 *
 * <p>배치 등록의 전체 결과와 항목별 성공/실패 상세를 반환합니다.
 *
 * @param totalCount 총 처리 건수
 * @param successCount 성공 건수
 * @param failureCount 실패 건수
 * @param results 항목별 결과 (요청 순서와 동일)
 * @author ryu-qqq
 * @since 1.1.0
 */
@Schema(description = "상품 그룹 배치 등록 결과")
public record BatchProductGroupResultApiResponse(
        @Schema(description = "총 처리 건수", example = "10") int totalCount,
        @Schema(description = "성공 건수", example = "8") int successCount,
        @Schema(description = "실패 건수", example = "2") int failureCount,
        @Schema(description = "항목별 결과") List<ItemResult> results) {

    /**
     * 개별 항목 결과.
     *
     * @param index 요청 목록 내 인덱스 (0-based)
     * @param productGroupId 생성된 상품 그룹 ID (실패 시 null)
     * @param productGroupName 요청한 상품 그룹명 (실패 항목 식별용)
     * @param success 성공 여부
     * @param errorCode 에러 코드 (성공 시 null)
     * @param errorMessage 에러 메시지 (성공 시 null)
     */
    @Schema(description = "개별 항목 결과")
    public record ItemResult(
            @Schema(description = "요청 인덱스 (0-based)", example = "0") int index,
            @Schema(description = "생성된 상품 그룹 ID (실패 시 null)", example = "12345")
                    Long productGroupId,
            @Schema(description = "요청한 상품 그룹명 (실패 항목 식별용)", example = "나이키 에어맥스 90")
                    String productGroupName,
            @Schema(description = "성공 여부", example = "true") boolean success,
            @Schema(description = "에러 코드 (성공 시 null)") String errorCode,
            @Schema(description = "에러 메시지 (성공 시 null)") String errorMessage) {}

    /**
     * BatchProcessingResult → API 응답 변환.
     *
     * @param result Application 레이어 배치 처리 결과
     * @return API 응답 DTO
     */
    public static BatchProductGroupResultApiResponse from(BatchProcessingResult<Long> result) {
        List<ItemResult> items =
                IntStream.range(0, result.results().size())
                        .mapToObj(
                                i -> {
                                    BatchItemResult<Long> item = result.results().get(i);
                                    return new ItemResult(
                                            i,
                                            item.id(),
                                            item.itemName(),
                                            item.success(),
                                            item.errorCode(),
                                            item.errorMessage());
                                })
                        .toList();
        return new BatchProductGroupResultApiResponse(
                result.totalCount(), result.successCount(), result.failureCount(), items);
    }
}
