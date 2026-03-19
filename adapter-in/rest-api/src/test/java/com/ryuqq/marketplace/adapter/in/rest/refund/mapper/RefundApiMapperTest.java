package com.ryuqq.marketplace.adapter.in.rest.refund.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.RefundApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.ApproveRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.HoldRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.RefundSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.RejectRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.RequestRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.response.RefundDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.response.RefundListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.response.RefundSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.application.claimhistory.dto.command.AddClaimHistoryMemoCommand;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.refund.dto.command.ApproveRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.dto.command.HoldRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.dto.command.RejectRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.dto.command.RequestRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.dto.query.RefundSearchParams;
import com.ryuqq.marketplace.application.refund.dto.response.RefundDetailResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundListResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundPageResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundSummaryResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundApiMapper 단위 테스트")
class RefundApiMapperTest {

    private RefundApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RefundApiMapper();
    }

    // ===================== Command 변환 =====================

    @Nested
    @DisplayName("toRequestRefundBatchCommand() - 환불 요청 일괄 변환")
    class ToRequestRefundBatchCommandTest {

        @Test
        @DisplayName("RequestRefundBatchApiRequest를 RequestRefundBatchCommand로 변환한다")
        void toRequestRefundBatchCommand_ConvertsRequest_ReturnsCommand() {
            // given
            RequestRefundBatchApiRequest request = RefundApiFixtures.requestBatchRequest();
            String requestedBy = "seller01";
            long sellerId = 1L;

            // when
            RequestRefundBatchCommand command =
                    mapper.toRequestRefundBatchCommand(request, requestedBy, sellerId);

            // then
            assertThat(command.requestedBy()).isEqualTo("seller01");
            assertThat(command.sellerId()).isEqualTo(1L);
            assertThat(command.items()).hasSize(2);
        }

        @Test
        @DisplayName("개별 항목의 orderId가 orderItemId로 매핑된다 (V4 간극 패턴)")
        void toRequestRefundBatchCommand_OrderIdMappedToOrderItemId() {
            // given
            RequestRefundBatchApiRequest request = RefundApiFixtures.requestBatchRequest();

            // when
            RequestRefundBatchCommand command =
                    mapper.toRequestRefundBatchCommand(request, "seller01", 1L);

            // then
            RequestRefundBatchCommand.RefundRequestItem firstItem = command.items().get(0);
            assertThat(firstItem.orderItemId()).isEqualTo("01940001-0000-7000-8000-000000000001");
            assertThat(firstItem.refundQty()).isEqualTo(1);
            assertThat(firstItem.reasonDetail()).isEqualTo(RefundApiFixtures.DEFAULT_REASON_DETAIL);
        }

        @Test
        @DisplayName("reasonType이 RefundReasonType enum으로 변환된다")
        void toRequestRefundBatchCommand_ReasonTypeConverted() {
            // given
            RequestRefundBatchApiRequest request = RefundApiFixtures.requestBatchRequest();

            // when
            RequestRefundBatchCommand command =
                    mapper.toRequestRefundBatchCommand(request, "seller01", 1L);

            // then
            assertThat(command.items().get(0).reasonType().name())
                    .isEqualTo(RefundApiFixtures.DEFAULT_REASON_TYPE);
        }
    }

    @Nested
    @DisplayName("toApproveRefundBatchCommand() - 환불 승인 일괄 변환")
    class ToApproveRefundBatchCommandTest {

        @Test
        @DisplayName("ApproveRefundBatchApiRequest를 ApproveRefundBatchCommand로 변환한다")
        void toApproveRefundBatchCommand_ConvertsRequest_ReturnsCommand() {
            // given
            ApproveRefundBatchApiRequest request = RefundApiFixtures.approveBatchRequest();
            String processedBy = "admin01";
            Long sellerId = 1L;

            // when
            ApproveRefundBatchCommand command =
                    mapper.toApproveRefundBatchCommand(request, processedBy, sellerId);

            // then
            assertThat(command.refundClaimIds()).hasSize(2);
            assertThat(command.processedBy()).isEqualTo("admin01");
            assertThat(command.sellerId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("refundClaimIds가 Command에 올바르게 매핑된다")
        void toApproveRefundBatchCommand_RefundClaimIdsCorrectlyMapped() {
            // given
            List<String> ids =
                    List.of(
                            "01940001-0000-7000-8000-000000000001",
                            "01940001-0000-7000-8000-000000000002");
            ApproveRefundBatchApiRequest request = RefundApiFixtures.approveBatchRequest(ids);

            // when
            ApproveRefundBatchCommand command =
                    mapper.toApproveRefundBatchCommand(request, "admin01", null);

            // then
            assertThat(command.refundClaimIds())
                    .containsExactly(
                            "01940001-0000-7000-8000-000000000001",
                            "01940001-0000-7000-8000-000000000002");
        }
    }

    @Nested
    @DisplayName("toRejectRefundBatchCommand() - 환불 거절 일괄 변환")
    class ToRejectRefundBatchCommandTest {

        @Test
        @DisplayName("RejectRefundBatchApiRequest를 RejectRefundBatchCommand로 변환한다")
        void toRejectRefundBatchCommand_ConvertsRequest_ReturnsCommand() {
            // given
            RejectRefundBatchApiRequest request = RefundApiFixtures.rejectBatchRequest();
            String processedBy = "admin01";
            Long sellerId = 1L;

            // when
            RejectRefundBatchCommand command =
                    mapper.toRejectRefundBatchCommand(request, processedBy, sellerId);

            // then
            assertThat(command.refundClaimIds()).hasSize(2);
            assertThat(command.processedBy()).isEqualTo("admin01");
            assertThat(command.sellerId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("toHoldCommand() - 환불 보류 일괄 변환")
    class ToHoldCommandTest {

        @Test
        @DisplayName("HoldRefundBatchApiRequest(보류)를 HoldRefundBatchCommand로 변환한다")
        void toHoldCommand_HoldRequest_ReturnsCommand() {
            // given
            HoldRefundBatchApiRequest request = RefundApiFixtures.holdBatchRequest();

            // when
            HoldRefundBatchCommand command = mapper.toHoldCommand(request, "admin01", 1L);

            // then
            assertThat(command.refundClaimIds()).hasSize(2);
            assertThat(command.isHold()).isTrue();
            assertThat(command.memo()).isEqualTo("추가 확인 필요");
            assertThat(command.processedBy()).isEqualTo("admin01");
        }

        @Test
        @DisplayName("HoldRefundBatchApiRequest(보류 해제)를 HoldRefundBatchCommand로 변환한다")
        void toHoldCommand_ReleaseRequest_ReturnsCommand() {
            // given
            HoldRefundBatchApiRequest request = RefundApiFixtures.releaseBatchRequest();

            // when
            HoldRefundBatchCommand command = mapper.toHoldCommand(request, "admin01", 1L);

            // then
            assertThat(command.isHold()).isFalse();
            assertThat(command.memo()).isNull();
        }
    }

    @Nested
    @DisplayName("toAddMemoCommand() - 수기 메모 등록 변환")
    class ToAddMemoCommandTest {

        @Test
        @DisplayName("수기 메모 등록 요청을 AddClaimHistoryMemoCommand로 변환한다")
        void toAddMemoCommand_ConvertsRequest_ReturnsCommand() {
            // given
            String refundClaimId = RefundApiFixtures.DEFAULT_REFUND_CLAIM_ID;
            AddClaimHistoryMemoApiRequest request = RefundApiFixtures.addMemoRequest();
            long sellerId = 1L;
            String actorName = "seller01";

            // when
            AddClaimHistoryMemoCommand command =
                    mapper.toAddMemoCommand(refundClaimId, request, sellerId, actorName);

            // then
            assertThat(command.claimId()).isEqualTo(refundClaimId);
            assertThat(command.message()).isEqualTo("수기 메모 내용입니다.");
            assertThat(command.actorId()).isEqualTo("1");
            assertThat(command.actorName()).isEqualTo("seller01");
        }

        @Test
        @DisplayName("ClaimType이 REFUND로 설정된다")
        void toAddMemoCommand_ClaimTypeIsRefund() {
            // given
            AddClaimHistoryMemoApiRequest request = RefundApiFixtures.addMemoRequest();

            // when
            AddClaimHistoryMemoCommand command =
                    mapper.toAddMemoCommand(
                            RefundApiFixtures.DEFAULT_REFUND_CLAIM_ID, request, 1L, "seller01");

            // then
            assertThat(command.claimType().name()).isEqualTo("REFUND");
        }
    }

    // ===================== Query 변환 =====================

    @Nested
    @DisplayName("toSearchParams() - 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("RefundSearchApiRequest를 RefundSearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            RefundSearchApiRequest request =
                    RefundApiFixtures.searchRequest(
                            List.of("REQUESTED", "COLLECTING"),
                            "CLAIM_NUMBER",
                            "RC-001",
                            "CREATED_AT",
                            "DESC",
                            0,
                            20);

            // when
            RefundSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.statuses()).containsExactly("REQUESTED", "COLLECTING");
            assertThat(result.searchField()).isEqualTo("CLAIM_NUMBER");
            assertThat(result.searchWord()).isEqualTo("RC-001");
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
            assertThat(result.sortKey()).isEqualTo("CREATED_AT");
            assertThat(result.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("page/size가 null이면 기본값(0, 20)으로 변환한다")
        void toSearchParams_NullPageSize_UsesDefaults() {
            // given
            RefundSearchApiRequest request = RefundApiFixtures.searchRequest();

            // when
            RefundSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("statuses가 null이면 null로 전달된다")
        void toSearchParams_NullStatuses_ReturnsNullStatuses() {
            // given
            RefundSearchApiRequest request = RefundApiFixtures.searchRequest();

            // when
            RefundSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.statuses()).isNull();
            assertThat(result.searchField()).isNull();
            assertThat(result.searchWord()).isNull();
        }

        @Test
        @DisplayName("환불 상태 필터를 지정하면 해당 상태만 조회한다")
        void toSearchParams_WithStatuses_ReturnsFilteredStatuses() {
            // given
            List<String> statuses = List.of("REQUESTED", "COMPLETED", "REJECTED");
            RefundSearchApiRequest request = RefundApiFixtures.searchRequestWithStatuses(statuses);

            // when
            RefundSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.statuses()).containsExactly("REQUESTED", "COMPLETED", "REJECTED");
        }
    }

    // ===================== Response 변환 =====================

    @Nested
    @DisplayName("toSummaryResponse() - 요약 응답 변환")
    class ToSummaryResponseTest {

        @Test
        @DisplayName("RefundSummaryResult를 RefundSummaryApiResponse로 변환한다")
        void toSummaryResponse_ConvertsSummaryResult_ReturnsApiResponse() {
            // given
            RefundSummaryResult result = RefundApiFixtures.summaryResult();

            // when
            RefundSummaryApiResponse response = mapper.toSummaryResponse(result);

            // then
            assertThat(response.requested()).isEqualTo(10);
            assertThat(response.collecting()).isEqualTo(5);
            assertThat(response.collected()).isEqualTo(3);
            assertThat(response.completed()).isEqualTo(20);
            assertThat(response.rejected()).isEqualTo(2);
            assertThat(response.cancelled()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("toListResponse() - 목록 단건 변환")
    class ToListResponseTest {

        @Test
        @DisplayName("RefundListResult를 RefundListApiResponse로 변환한다")
        void toListResponse_ConvertsListResult_ReturnsApiResponse() {
            // given
            RefundListResult result =
                    RefundApiFixtures.listResult(RefundApiFixtures.DEFAULT_REFUND_CLAIM_ID);

            // when
            RefundListApiResponse response = mapper.toListResponse(result);

            // then
            assertThat(response.refundClaimId())
                    .isEqualTo(RefundApiFixtures.DEFAULT_REFUND_CLAIM_ID);
            assertThat(response.claimNumber()).isEqualTo(RefundApiFixtures.DEFAULT_CLAIM_NUMBER);
            assertThat(response.orderId()).isEqualTo(RefundApiFixtures.DEFAULT_ORDER_ITEM_ID);
            assertThat(response.refundQty()).isEqualTo(1);
            assertThat(response.refundStatus()).isEqualTo(RefundApiFixtures.DEFAULT_REFUND_STATUS);
        }

        @Test
        @DisplayName("null 문자열 필드가 빈 문자열로 변환된다 (V4 간극 패턴)")
        void toListResponse_NullStringFields_ConvertedToEmpty() {
            // given
            RefundListResult result =
                    RefundApiFixtures.listResult(RefundApiFixtures.DEFAULT_REFUND_CLAIM_ID);

            // when
            RefundListApiResponse response = mapper.toListResponse(result);

            // then
            assertThat(response.processedBy()).isEqualTo("");
        }

        @Test
        @DisplayName("null 금액 필드가 0으로 변환된다 (V4 간극 패턴)")
        void toListResponse_NullAmountFields_ConvertedToZero() {
            // given
            RefundListResult resultWithNullAmounts =
                    new RefundListResult(
                            RefundApiFixtures.DEFAULT_REFUND_CLAIM_ID,
                            RefundApiFixtures.DEFAULT_CLAIM_NUMBER,
                            RefundApiFixtures.DEFAULT_ORDER_ITEM_ID,
                            1,
                            RefundApiFixtures.DEFAULT_REFUND_STATUS,
                            RefundApiFixtures.DEFAULT_REASON_TYPE,
                            RefundApiFixtures.DEFAULT_REASON_DETAIL,
                            null,
                            null,
                            null,
                            RefundApiFixtures.DEFAULT_REQUESTED_BY,
                            null,
                            RefundApiFixtures.DEFAULT_INSTANT,
                            null,
                            null);

            // when
            RefundListApiResponse response = mapper.toListResponse(resultWithNullAmounts);

            // then
            assertThat(response.originalAmount()).isZero();
            assertThat(response.finalAmount()).isZero();
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 형식으로 변환된다")
        void toListResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            RefundListResult result =
                    RefundApiFixtures.listResult(RefundApiFixtures.DEFAULT_REFUND_CLAIM_ID);

            // when
            RefundListApiResponse response = mapper.toListResponse(result);

            // then
            assertThat(response.requestedAt()).contains("T");
            assertThat(response.requestedAt()).contains("+09:00");
        }

        @Test
        @DisplayName("null 날짜 필드는 null을 반환한다")
        void toListResponse_NullDateFields_ReturnsNull() {
            // given
            RefundListResult result =
                    RefundApiFixtures.listResult(RefundApiFixtures.DEFAULT_REFUND_CLAIM_ID);

            // when
            RefundListApiResponse response = mapper.toListResponse(result);

            // then
            assertThat(response.processedAt()).isNull();
            assertThat(response.completedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 결과 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("RefundPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            RefundPageResult pageResult = RefundApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<RefundListApiResponse> response = mapper.toPageResponse(pageResult);

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
            RefundPageResult pageResult = RefundApiFixtures.emptyPageResult();

            // when
            PageApiResponse<RefundListApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }

        @Test
        @DisplayName("목록의 각 항목이 올바르게 변환된다")
        void toPageResponse_ConvertsEachItem_ReturnsCorrectItems() {
            // given
            RefundPageResult pageResult = RefundApiFixtures.pageResult(2, 0, 20);

            // when
            PageApiResponse<RefundListApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            List<RefundListApiResponse> content = response.content();
            assertThat(content.get(0).refundClaimId())
                    .isEqualTo("01940001-0000-7000-8000-000000000001");
            assertThat(content.get(1).refundClaimId())
                    .isEqualTo("01940001-0000-7000-8000-000000000002");
        }
    }

    @Nested
    @DisplayName("toDetailResponse() - 상세 응답 변환")
    class ToDetailResponseTest {

        @Test
        @DisplayName("RefundDetailResult를 RefundDetailApiResponse로 변환한다")
        void toDetailResponse_ConvertsDetailResult_ReturnsApiResponse() {
            // given
            RefundDetailResult result =
                    RefundApiFixtures.detailResult(RefundApiFixtures.DEFAULT_REFUND_CLAIM_ID);

            // when
            RefundDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.refundClaimId())
                    .isEqualTo(RefundApiFixtures.DEFAULT_REFUND_CLAIM_ID);
            assertThat(response.claimNumber()).isEqualTo(RefundApiFixtures.DEFAULT_CLAIM_NUMBER);
            assertThat(response.orderId()).isEqualTo(RefundApiFixtures.DEFAULT_ORDER_ITEM_ID);
            assertThat(response.refundQty()).isEqualTo(1);
            assertThat(response.refundStatus()).isEqualTo(RefundApiFixtures.DEFAULT_REFUND_STATUS);
        }

        @Test
        @DisplayName("환불 정보(refundInfo)가 올바르게 변환된다")
        void toDetailResponse_WithRefundInfo_ReturnsRefundInfoResponse() {
            // given
            RefundDetailResult result =
                    RefundApiFixtures.detailResult(RefundApiFixtures.DEFAULT_REFUND_CLAIM_ID);

            // when
            RefundDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.refundInfo()).isNotNull();
            assertThat(response.refundInfo().originalAmount()).isEqualTo(15000);
            assertThat(response.refundInfo().finalAmount()).isEqualTo(15000);
            assertThat(response.refundInfo().deductionAmount()).isZero();
            assertThat(response.refundInfo().refundMethod()).isEqualTo("CARD");
        }

        @Test
        @DisplayName("환불 정보가 null이면 refundInfo도 null을 반환한다")
        void toDetailResponse_NullRefundInfo_ReturnsNullRefundInfo() {
            // given
            RefundDetailResult result =
                    RefundApiFixtures.detailResultWithoutRefundInfo(
                            RefundApiFixtures.DEFAULT_REFUND_CLAIM_ID);

            // when
            RefundDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.refundInfo()).isNull();
        }

        @Test
        @DisplayName("보류 정보(holdInfo)가 올바르게 변환된다")
        void toDetailResponse_WithHoldInfo_ReturnsHoldInfoResponse() {
            // given
            RefundDetailResult result =
                    RefundApiFixtures.detailResultWithHold(
                            RefundApiFixtures.DEFAULT_REFUND_CLAIM_ID);

            // when
            RefundDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.holdInfo()).isNotNull();
            assertThat(response.holdInfo().holdReason()).isEqualTo("추가 확인 필요");
            assertThat(response.holdInfo().holdAt()).isNotNull();
        }

        @Test
        @DisplayName("클레임 이력이 올바르게 변환된다")
        void toDetailResponse_WithHistories_ReturnsHistoriesResponse() {
            // given
            RefundDetailResult result =
                    RefundApiFixtures.detailResult(RefundApiFixtures.DEFAULT_REFUND_CLAIM_ID);

            // when
            RefundDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.claimHistories()).hasSize(1);
            assertThat(response.claimHistories().get(0).historyId()).isEqualTo("HIST-001");
            assertThat(response.claimHistories().get(0).actor().actorName()).isEqualTo("seller01");
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 형식으로 변환된다")
        void toDetailResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            RefundDetailResult result =
                    RefundApiFixtures.detailResult(RefundApiFixtures.DEFAULT_REFUND_CLAIM_ID);

            // when
            RefundDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.requestedAt()).contains("+09:00");
            assertThat(response.createdAt()).contains("+09:00");
            assertThat(response.updatedAt()).contains("+09:00");
        }
    }

    @Nested
    @DisplayName("toBatchResultResponse() - 일괄 처리 결과 응답 변환")
    class ToBatchResultResponseTest {

        @Test
        @DisplayName("BatchProcessingResult를 BatchResultApiResponse로 변환한다")
        void toBatchResultResponse_ConvertsMixedResult_ReturnsApiResponse() {
            // given
            BatchProcessingResult<String> result = RefundApiFixtures.batchMixedResult();

            // when
            BatchResultApiResponse response = mapper.toBatchResultResponse(result);

            // then
            assertThat(response.totalCount()).isEqualTo(3);
            assertThat(response.successCount()).isEqualTo(2);
            assertThat(response.failureCount()).isEqualTo(1);
            assertThat(response.results()).hasSize(3);
        }

        @Test
        @DisplayName("성공 항목의 결과가 올바르게 변환된다")
        void toBatchResultResponse_SuccessItem_ReturnsCorrectResponse() {
            // given
            BatchProcessingResult<String> result =
                    RefundApiFixtures.batchSuccessResult(
                            List.of(
                                    "01940001-0000-7000-8000-000000000001",
                                    "01940001-0000-7000-8000-000000000002"));

            // when
            BatchResultApiResponse response = mapper.toBatchResultResponse(result);

            // then
            assertThat(response.totalCount()).isEqualTo(2);
            assertThat(response.successCount()).isEqualTo(2);
            assertThat(response.failureCount()).isZero();

            BatchResultApiResponse.BatchResultItemApiResponse firstItem = response.results().get(0);
            assertThat(firstItem.id()).isEqualTo("01940001-0000-7000-8000-000000000001");
            assertThat(firstItem.success()).isTrue();
            assertThat(firstItem.errorCode()).isNull();
            assertThat(firstItem.errorMessage()).isNull();
        }

        @Test
        @DisplayName("실패 항목의 결과가 올바르게 변환된다")
        void toBatchResultResponse_FailureItem_ReturnsCorrectResponse() {
            // given
            BatchProcessingResult<String> result = RefundApiFixtures.batchMixedResult();

            // when
            BatchResultApiResponse response = mapper.toBatchResultResponse(result);

            // then
            BatchResultApiResponse.BatchResultItemApiResponse failedItem =
                    response.results().get(1);
            assertThat(failedItem.id()).isEqualTo("01940001-0000-7000-8000-000000000002");
            assertThat(failedItem.success()).isFalse();
            assertThat(failedItem.errorCode()).isEqualTo("INVALID_STATUS");
            assertThat(failedItem.errorMessage()).isEqualTo("이미 처리된 환불 건입니다.");
        }
    }
}
