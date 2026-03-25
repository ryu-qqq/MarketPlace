package com.ryuqq.marketplace.adapter.in.rest.cancel.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.cancel.CancelApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.ApproveCancelBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.CancelSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.RejectCancelBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.SellerCancelBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.response.CancelDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.response.CancelListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.response.CancelSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.application.cancel.dto.command.ApproveCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.RejectCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.query.CancelSearchParams;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelDetailResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelPageResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelSummaryResult;
import com.ryuqq.marketplace.application.claimhistory.dto.command.AddClaimHistoryMemoCommand;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelApiMapper 단위 테스트")
class CancelApiMapperTest {

    private CancelApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CancelApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("CancelSearchApiRequest를 CancelSearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            CancelSearchApiRequest request =
                    CancelApiFixtures.searchRequest(
                            List.of("REQUESTED"),
                            "CANCEL_NUMBER",
                            "CAN-001",
                            "CREATED_AT",
                            "DESC",
                            0,
                            20);

            // when
            CancelSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.statuses()).containsExactly("REQUESTED");
            assertThat(result.searchField()).isEqualTo("CANCEL_NUMBER");
            assertThat(result.searchWord()).isEqualTo("CAN-001");
            assertThat(result.sortKey()).isEqualTo("CREATED_AT");
            assertThat(result.sortDirection()).isEqualTo("DESC");
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("page/size가 null이면 기본값(0, 20)으로 변환한다")
        void toSearchParams_NullPageSize_UsesDefaults() {
            // given
            CancelSearchApiRequest request = CancelApiFixtures.searchRequest();

            // when
            CancelSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("statuses, types가 null이면 그대로 전달된다")
        void toSearchParams_NullFilters_PassedThrough() {
            // given
            CancelSearchApiRequest request = CancelApiFixtures.searchRequest();

            // when
            CancelSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.statuses()).isNull();
            assertThat(result.types()).isNull();
        }
    }

    @Nested
    @DisplayName("toSellerCancelBatchCommand() - 판매자 취소 명령 변환")
    class ToSellerCancelBatchCommandTest {

        @Test
        @DisplayName("SellerCancelBatchApiRequest를 SellerCancelBatchCommand로 변환한다")
        void toSellerCancelBatchCommand_ConvertsRequest_ReturnsCommand() {
            // given
            SellerCancelBatchApiRequest request = CancelApiFixtures.sellerCancelBatchRequest();
            String requestedBy = "seller01";
            long sellerId = 100L;

            // when
            SellerCancelBatchCommand command =
                    mapper.toSellerCancelBatchCommand(request, requestedBy, sellerId);

            // then
            assertThat(command.requestedBy()).isEqualTo(requestedBy);
            assertThat(command.sellerId()).isEqualTo(sellerId);
            assertThat(command.items()).hasSize(2);
        }

        @Test
        @DisplayName("items 각 항목의 orderId(=orderItemId), cancelQty, reasonType이 올바르게 매핑된다")
        void toSellerCancelBatchCommand_ItemFieldsMapped() {
            // given
            SellerCancelBatchApiRequest request = CancelApiFixtures.sellerCancelBatchRequest();

            // when
            SellerCancelBatchCommand command =
                    mapper.toSellerCancelBatchCommand(request, "seller01", 100L);

            // then
            SellerCancelBatchCommand.SellerCancelItem firstItem = command.items().get(0);
            assertThat(firstItem.orderItemId()).isEqualTo("01940001-0000-7000-8000-000000000001");
            assertThat(firstItem.cancelQty()).isEqualTo(1);
            assertThat(firstItem.reasonType().name())
                    .isEqualTo(CancelApiFixtures.DEFAULT_REASON_TYPE);
            assertThat(firstItem.reasonDetail()).isEqualTo(CancelApiFixtures.DEFAULT_REASON_DETAIL);
        }
    }

    @Nested
    @DisplayName("toApproveCancelBatchCommand() - 취소 승인 명령 변환")
    class ToApproveCancelBatchCommandTest {

        @Test
        @DisplayName("ApproveCancelBatchApiRequest를 ApproveCancelBatchCommand로 변환한다")
        void toApproveCancelBatchCommand_ConvertsRequest_ReturnsCommand() {
            // given
            ApproveCancelBatchApiRequest request = CancelApiFixtures.approveBatchRequest();
            String processedBy = "admin01";
            Long sellerId = null;

            // when
            ApproveCancelBatchCommand command =
                    mapper.toApproveCancelBatchCommand(request, processedBy, sellerId);

            // then
            assertThat(command.cancelIds()).hasSize(2);
            assertThat(command.processedBy()).isEqualTo(processedBy);
            assertThat(command.sellerId()).isNull();
        }
    }

    @Nested
    @DisplayName("toRejectCancelBatchCommand() - 취소 거절 명령 변환")
    class ToRejectCancelBatchCommandTest {

        @Test
        @DisplayName("RejectCancelBatchApiRequest를 RejectCancelBatchCommand로 변환한다")
        void toRejectCancelBatchCommand_ConvertsRequest_ReturnsCommand() {
            // given
            RejectCancelBatchApiRequest request = CancelApiFixtures.rejectBatchRequest();
            String processedBy = "admin01";
            Long sellerId = 100L;

            // when
            RejectCancelBatchCommand command =
                    mapper.toRejectCancelBatchCommand(request, processedBy, sellerId);

            // then
            assertThat(command.cancelIds()).hasSize(2);
            assertThat(command.processedBy()).isEqualTo(processedBy);
            assertThat(command.sellerId()).isEqualTo(sellerId);
        }
    }

    @Nested
    @DisplayName("toAddMemoCommand() - 수기 메모 명령 변환")
    class ToAddMemoCommandTest {

        @Test
        @DisplayName("cancelId, 메모, actorId, actorName을 AddClaimHistoryMemoCommand로 변환한다")
        void toAddMemoCommand_ConvertsRequest_ReturnsCommand() {
            // given
            String cancelId = CancelApiFixtures.DEFAULT_CANCEL_ID;
            AddClaimHistoryMemoApiRequest request = CancelApiFixtures.addMemoRequest();
            long actorId = 100L;
            String actorName = "seller01";

            // when
            AddClaimHistoryMemoCommand command =
                    mapper.toAddMemoCommand(cancelId, request, actorId, actorName);

            // then
            assertThat(command.claimId()).isEqualTo(cancelId);
            assertThat(command.message()).isEqualTo("수기 메모 내용입니다.");
            assertThat(command.actorId()).isEqualTo("100");
            assertThat(command.actorName()).isEqualTo(actorName);
        }
    }

    @Nested
    @DisplayName("toSummaryResponse() - 요약 응답 변환")
    class ToSummaryResponseTest {

        @Test
        @DisplayName("CancelSummaryResult를 CancelSummaryApiResponse로 변환한다")
        void toSummaryResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            CancelSummaryResult result = CancelApiFixtures.summaryResult();

            // when
            CancelSummaryApiResponse response = mapper.toSummaryResponse(result);

            // then
            assertThat(response.requested()).isEqualTo(result.requested());
            assertThat(response.approved()).isEqualTo(result.approved());
            assertThat(response.rejected()).isEqualTo(result.rejected());
            assertThat(response.completed()).isEqualTo(result.completed());
        }
    }

    @Nested
    @DisplayName("toListResponse() - 목록 단건 변환")
    class ToListResponseTest {

        @Test
        @DisplayName("CancelListResult를 CancelListApiResponse로 변환한다")
        void toListResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            com.ryuqq.marketplace.application.cancel.dto.response.CancelListResult result =
                    CancelApiFixtures.listResult(CancelApiFixtures.DEFAULT_CANCEL_ID);

            // when
            CancelListApiResponse response = mapper.toListResponse(result);

            // then
            assertThat(response).isNotNull();
            assertThat(response.cancelId()).isEqualTo(CancelApiFixtures.DEFAULT_CANCEL_ID);
            assertThat(response.cancelNumber()).isEqualTo(CancelApiFixtures.DEFAULT_CANCEL_NUMBER);
            assertThat(response.orderId()).isEqualTo(CancelApiFixtures.DEFAULT_ORDER_ITEM_ID);
            assertThat(response.cancelQty()).isEqualTo(1);
            assertThat(response.cancelType()).isEqualTo(CancelApiFixtures.DEFAULT_CANCEL_TYPE);
            assertThat(response.cancelStatus()).isEqualTo(CancelApiFixtures.DEFAULT_CANCEL_STATUS);
        }

        @Test
        @DisplayName("null 문자열 필드는 빈 문자열로 변환된다")
        void toListResponse_NullStringFields_ConvertedToEmpty() {
            // given
            com.ryuqq.marketplace.application.cancel.dto.response.CancelListResult result =
                    CancelApiFixtures.listResult(CancelApiFixtures.DEFAULT_CANCEL_ID);

            // when
            CancelListApiResponse response = mapper.toListResponse(result);

            // then
            assertThat(response.processedBy()).isEqualTo("");
        }

        @Test
        @DisplayName("null refundAmount는 0으로 변환된다")
        void toListResponse_NullRefundAmount_ConvertedToZero() {
            // given
            com.ryuqq.marketplace.application.cancel.dto.response.CancelListResult result =
                    new com.ryuqq.marketplace.application.cancel.dto.response.CancelListResult(
                            CancelApiFixtures.DEFAULT_CANCEL_ID,
                            CancelApiFixtures.DEFAULT_CANCEL_NUMBER,
                            CancelApiFixtures.DEFAULT_ORDER_ITEM_ID,
                            1,
                            CancelApiFixtures.DEFAULT_CANCEL_TYPE,
                            CancelApiFixtures.DEFAULT_CANCEL_STATUS,
                            CancelApiFixtures.DEFAULT_REASON_TYPE,
                            CancelApiFixtures.DEFAULT_REASON_DETAIL,
                            null,
                            null,
                            CancelApiFixtures.DEFAULT_REQUESTED_BY,
                            null,
                            CancelApiFixtures.DEFAULT_INSTANT,
                            null,
                            null);

            // when
            CancelListApiResponse response = mapper.toListResponse(result);

            // then
            assertThat(response.refundAmount()).isZero();
            assertThat(response.refundMethod()).isEqualTo("");
        }

        @Test
        @DisplayName("requestedAt이 ISO 8601 KST 형식으로 변환된다")
        void toListResponse_InstantFormattedAsKst() {
            // given
            com.ryuqq.marketplace.application.cancel.dto.response.CancelListResult result =
                    CancelApiFixtures.listResult(CancelApiFixtures.DEFAULT_CANCEL_ID);

            // when
            CancelListApiResponse response = mapper.toListResponse(result);

            // then
            assertThat(response.requestedAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        }

        @Test
        @DisplayName("null Instant는 null로 변환된다")
        void toListResponse_NullInstant_ReturnsNull() {
            // given
            com.ryuqq.marketplace.application.cancel.dto.response.CancelListResult result =
                    CancelApiFixtures.listResult(CancelApiFixtures.DEFAULT_CANCEL_ID);

            // when
            CancelListApiResponse response = mapper.toListResponse(result);

            // then
            assertThat(response.processedAt()).isNull();
            assertThat(response.completedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("toDetailResponse() - 상세 응답 변환")
    class ToDetailResponseTest {

        @Test
        @DisplayName("CancelDetailResult를 CancelDetailApiResponse로 변환한다")
        void toDetailResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            CancelDetailResult result =
                    CancelApiFixtures.detailResult(CancelApiFixtures.DEFAULT_CANCEL_ID);

            // when
            CancelDetailApiResponse response = mapper.toDetailResponse(result, null, null, null, null);

            // then
            assertThat(response).isNotNull();
            assertThat(response.cancelInfo().cancelId()).isEqualTo(CancelApiFixtures.DEFAULT_CANCEL_ID);
            assertThat(response.cancelInfo().cancelNumber()).isEqualTo(CancelApiFixtures.DEFAULT_CANCEL_NUMBER);
            assertThat(response.orderId()).isEqualTo(CancelApiFixtures.DEFAULT_ORDER_ITEM_ID);
            assertThat(response.cancelInfo().type()).isEqualTo(CancelApiFixtures.DEFAULT_CANCEL_TYPE);
        }

        @Test
        @DisplayName("환불 정보가 올바르게 매핑된다")
        void toDetailResponse_RefundInfoMapped() {
            // given
            CancelDetailResult result =
                    CancelApiFixtures.detailResult(CancelApiFixtures.DEFAULT_CANCEL_ID);

            // when
            CancelDetailApiResponse response = mapper.toDetailResponse(result, null, null, null, null);

            // then
            assertThat(response.cancelInfo().refundInfo()).isNotNull();
            assertThat(response.cancelInfo().refundInfo().finalAmount()).isEqualTo(15000);
            assertThat(response.cancelInfo().refundInfo().refundMethod()).isEqualTo("CARD");
        }

        @Test
        @DisplayName("환불 정보가 null이면 refundInfo가 null로 매핑된다")
        void toDetailResponse_NullRefundInfo_MappedToNull() {
            // given
            CancelDetailResult result =
                    CancelApiFixtures.detailResultWithoutRefundInfo(
                            CancelApiFixtures.DEFAULT_CANCEL_ID);

            // when
            CancelDetailApiResponse response = mapper.toDetailResponse(result, null, null, null, null);

            // then
            assertThat(response.cancelInfo().refundInfo()).isNull();
        }

        @Test
        @DisplayName("클레임 이력 목록이 올바르게 매핑된다")
        void toDetailResponse_ClaimHistoriesMapped() {
            // given
            CancelDetailResult result =
                    CancelApiFixtures.detailResult(CancelApiFixtures.DEFAULT_CANCEL_ID);

            // when
            CancelDetailApiResponse response = mapper.toDetailResponse(result, null, null, null, null);

            // then
            assertThat(response.cancelHistories()).hasSize(1);
            assertThat(response.cancelHistories().get(0).historyId()).isEqualTo("HIST-001");
            assertThat(response.cancelHistories().get(0).type()).isEqualTo("MEMO");
            assertThat(response.cancelHistories().get(0).actor().actorName()).isEqualTo("seller01");
        }

        @Test
        @DisplayName("클레임 이력이 없으면 빈 리스트로 매핑된다")
        void toDetailResponse_EmptyHistories_MappedToEmptyList() {
            // given
            CancelDetailResult result =
                    CancelApiFixtures.detailResultWithoutRefundInfo(
                            CancelApiFixtures.DEFAULT_CANCEL_ID);

            // when
            CancelDetailApiResponse response = mapper.toDetailResponse(result, null, null, null, null);

            // then
            assertThat(response.cancelHistories()).isEmpty();
        }

        @Test
        @DisplayName("refundedAt이 ISO 8601 KST 형식으로 변환된다")
        void toDetailResponse_RefundedAtFormattedAsKst() {
            // given
            CancelDetailResult result =
                    CancelApiFixtures.detailResult(CancelApiFixtures.DEFAULT_CANCEL_ID);

            // when
            CancelDetailApiResponse response = mapper.toDetailResponse(result, null, null, null, null);

            // then
            assertThat(response.cancelInfo().refundInfo().refundedAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 결과 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("CancelPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            CancelPageResult pageResult = CancelApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<CancelListApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(3);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("빈 결과이면 빈 페이지 응답을 반환한다")
        void toPageResponse_EmptyResult_ReturnsEmptyPage() {
            // given
            CancelPageResult pageResult = CancelApiFixtures.emptyPageResult();

            // when
            PageApiResponse<CancelListApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("toBatchResultResponse() - 일괄 처리 결과 변환")
    class ToBatchResultResponseTest {

        @Test
        @DisplayName("BatchProcessingResult를 BatchResultApiResponse로 변환한다")
        void toBatchResultResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            BatchProcessingResult<String> result = CancelApiFixtures.batchMixedResult();

            // when
            BatchResultApiResponse response = mapper.toBatchResultResponse(result);

            // then
            assertThat(response.totalCount()).isEqualTo(3);
            assertThat(response.successCount()).isEqualTo(2);
            assertThat(response.failureCount()).isEqualTo(1);
            assertThat(response.results()).hasSize(3);
        }

        @Test
        @DisplayName("전체 성공 배치 결과를 올바르게 변환한다")
        void toBatchResultResponse_AllSuccess_ReturnsAllSuccessResponse() {
            // given
            List<String> ids =
                    List.of(
                            "01940001-0000-7000-8000-000000000001",
                            "01940001-0000-7000-8000-000000000002");
            BatchProcessingResult<String> result = CancelApiFixtures.batchSuccessResult(ids);

            // when
            BatchResultApiResponse response = mapper.toBatchResultResponse(result);

            // then
            assertThat(response.totalCount()).isEqualTo(2);
            assertThat(response.successCount()).isEqualTo(2);
            assertThat(response.failureCount()).isZero();
            response.results().forEach(item -> assertThat(item.success()).isTrue());
        }
    }
}
