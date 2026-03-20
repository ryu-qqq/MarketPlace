package com.ryuqq.marketplace.adapter.in.rest.exchange.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.ExchangeApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.ApproveExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.CollectExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.CompleteExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.ConvertToRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.ExchangeSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.HoldExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.PrepareExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.RejectExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.RequestExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.ShipExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.response.ExchangeSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.application.claimhistory.dto.command.AddClaimHistoryMemoCommand;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.command.ApproveExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.CollectExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.CompleteExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.ConvertToRefundBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.HoldExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.PrepareExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.RejectExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.RequestExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.ShipExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.query.ExchangeSearchParams;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeDetailResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangePageResult;
import com.ryuqq.marketplace.application.exchange.dto.response.ExchangeSummaryResult;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeReasonType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeApiMapper 단위 테스트")
class ExchangeApiMapperTest {

    private ExchangeApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ExchangeApiMapper();
    }

    @Nested
    @DisplayName("toRequestExchangeBatchCommand() - 교환 요청 일괄 변환")
    class ToRequestExchangeBatchCommandTest {

        @Test
        @DisplayName("RequestExchangeBatchApiRequest를 RequestExchangeBatchCommand로 변환한다")
        void toRequestExchangeBatchCommand_ConvertsRequest_ReturnsCommand() {
            // given
            RequestExchangeBatchApiRequest request = ExchangeApiFixtures.requestBatchRequest();
            String requestedBy = "seller@test.com";
            long sellerId = ExchangeApiFixtures.DEFAULT_SELLER_ID;

            // when
            RequestExchangeBatchCommand command =
                    mapper.toRequestExchangeBatchCommand(request, requestedBy, sellerId);

            // then
            assertThat(command.requestedBy()).isEqualTo(requestedBy);
            assertThat(command.sellerId()).isEqualTo(sellerId);
            assertThat(command.items()).hasSize(1);
        }

        @Test
        @DisplayName("요청 항목의 각 필드가 올바르게 변환된다 (V4 간극: orderId = orderItemId)")
        void toRequestExchangeBatchCommand_ConvertsItem_ReturnsCorrectFields() {
            // given
            RequestExchangeBatchApiRequest request = ExchangeApiFixtures.requestBatchRequest();

            // when
            RequestExchangeBatchCommand command =
                    mapper.toRequestExchangeBatchCommand(request, "user", 1L);

            // then
            RequestExchangeBatchCommand.ExchangeRequestItem item = command.items().get(0);
            assertThat(item.orderItemId()).isEqualTo(ExchangeApiFixtures.DEFAULT_ORDER_ITEM_ID);
            assertThat(item.exchangeQty()).isEqualTo(ExchangeApiFixtures.DEFAULT_EXCHANGE_QTY);
            assertThat(item.reasonType()).isEqualTo(ExchangeReasonType.SIZE_CHANGE);
            assertThat(item.reasonDetail()).isEqualTo(ExchangeApiFixtures.DEFAULT_REASON_DETAIL);
            assertThat(item.originalProductId())
                    .isEqualTo(ExchangeApiFixtures.DEFAULT_ORIGINAL_PRODUCT_ID);
            assertThat(item.originalSkuCode())
                    .isEqualTo(ExchangeApiFixtures.DEFAULT_ORIGINAL_SKU_CODE);
            assertThat(item.targetProductGroupId())
                    .isEqualTo(ExchangeApiFixtures.DEFAULT_TARGET_PRODUCT_GROUP_ID);
            assertThat(item.targetProductId())
                    .isEqualTo(ExchangeApiFixtures.DEFAULT_TARGET_PRODUCT_ID);
            assertThat(item.targetSkuCode()).isEqualTo(ExchangeApiFixtures.DEFAULT_TARGET_SKU_CODE);
            assertThat(item.targetQuantity())
                    .isEqualTo(ExchangeApiFixtures.DEFAULT_TARGET_QUANTITY);
        }
    }

    @Nested
    @DisplayName("toApproveExchangeBatchCommand() - 교환 승인 일괄 변환")
    class ToApproveExchangeBatchCommandTest {

        @Test
        @DisplayName("ApproveExchangeBatchApiRequest를 ApproveExchangeBatchCommand로 변환한다")
        void toApproveExchangeBatchCommand_ConvertsRequest_ReturnsCommand() {
            // given
            ApproveExchangeBatchApiRequest request = ExchangeApiFixtures.approveBatchRequest();
            String processedBy = ExchangeApiFixtures.DEFAULT_PROCESSED_BY;
            Long sellerId = ExchangeApiFixtures.DEFAULT_SELLER_ID;

            // when
            ApproveExchangeBatchCommand command =
                    mapper.toApproveExchangeBatchCommand(request, processedBy, sellerId);

            // then
            assertThat(command.exchangeClaimIds())
                    .containsExactly(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID);
            assertThat(command.processedBy()).isEqualTo(processedBy);
            assertThat(command.sellerId()).isEqualTo(sellerId);
        }

        @Test
        @DisplayName("sellerId가 null이면 슈퍼어드민 처리로 변환된다")
        void toApproveExchangeBatchCommand_NullSellerId_AllowsNull() {
            // given
            ApproveExchangeBatchApiRequest request = ExchangeApiFixtures.approveBatchRequest();

            // when
            ApproveExchangeBatchCommand command =
                    mapper.toApproveExchangeBatchCommand(request, "admin", null);

            // then
            assertThat(command.sellerId()).isNull();
        }
    }

    @Nested
    @DisplayName("toCollectExchangeBatchCommand() - 교환 수거 완료 일괄 변환")
    class ToCollectExchangeBatchCommandTest {

        @Test
        @DisplayName("CollectExchangeBatchApiRequest를 CollectExchangeBatchCommand로 변환한다")
        void toCollectExchangeBatchCommand_ConvertsRequest_ReturnsCommand() {
            // given
            CollectExchangeBatchApiRequest request = ExchangeApiFixtures.collectBatchRequest();

            // when
            CollectExchangeBatchCommand command =
                    mapper.toCollectExchangeBatchCommand(
                            request, ExchangeApiFixtures.DEFAULT_PROCESSED_BY, 1L);

            // then
            assertThat(command.exchangeClaimIds())
                    .containsExactly(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID);
        }
    }

    @Nested
    @DisplayName("toPrepareExchangeBatchCommand() - 교환 준비 완료 일괄 변환")
    class ToPrepareExchangeBatchCommandTest {

        @Test
        @DisplayName("PrepareExchangeBatchApiRequest를 PrepareExchangeBatchCommand로 변환한다")
        void toPrepareExchangeBatchCommand_ConvertsRequest_ReturnsCommand() {
            // given
            PrepareExchangeBatchApiRequest request = ExchangeApiFixtures.prepareBatchRequest();

            // when
            PrepareExchangeBatchCommand command =
                    mapper.toPrepareExchangeBatchCommand(
                            request, ExchangeApiFixtures.DEFAULT_PROCESSED_BY, 1L);

            // then
            assertThat(command.exchangeClaimIds())
                    .containsExactly(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID);
        }
    }

    @Nested
    @DisplayName("toRejectExchangeBatchCommand() - 교환 거절 일괄 변환")
    class ToRejectExchangeBatchCommandTest {

        @Test
        @DisplayName("RejectExchangeBatchApiRequest를 RejectExchangeBatchCommand로 변환한다")
        void toRejectExchangeBatchCommand_ConvertsRequest_ReturnsCommand() {
            // given
            RejectExchangeBatchApiRequest request = ExchangeApiFixtures.rejectBatchRequest();

            // when
            RejectExchangeBatchCommand command =
                    mapper.toRejectExchangeBatchCommand(
                            request, ExchangeApiFixtures.DEFAULT_PROCESSED_BY, 1L);

            // then
            assertThat(command.exchangeClaimIds())
                    .containsExactly(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID);
        }
    }

    @Nested
    @DisplayName("toShipCommand() - 교환 재배송 일괄 변환")
    class ToShipCommandTest {

        @Test
        @DisplayName("ShipExchangeBatchApiRequest를 ShipExchangeBatchCommand로 변환한다")
        void toShipCommand_ConvertsRequest_ReturnsCommand() {
            // given
            ShipExchangeBatchApiRequest request = ExchangeApiFixtures.shipBatchRequest();

            // when
            ShipExchangeBatchCommand command =
                    mapper.toShipCommand(request, ExchangeApiFixtures.DEFAULT_PROCESSED_BY, 1L);

            // then
            assertThat(command.items()).hasSize(1);
        }

        @Test
        @DisplayName("ShipItem 각 필드가 올바르게 변환된다")
        void toShipCommand_ConvertsEachItem_ReturnsCorrectFields() {
            // given
            ShipExchangeBatchApiRequest request = ExchangeApiFixtures.shipBatchRequest();

            // when
            ShipExchangeBatchCommand command =
                    mapper.toShipCommand(request, ExchangeApiFixtures.DEFAULT_PROCESSED_BY, 1L);

            // then
            ShipExchangeBatchCommand.ShipItem item = command.items().get(0);
            assertThat(item.exchangeClaimId())
                    .isEqualTo(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID);
            assertThat(item.linkedOrderId()).isEqualTo(ExchangeApiFixtures.DEFAULT_LINKED_ORDER_ID);
            assertThat(item.deliveryCompany())
                    .isEqualTo(ExchangeApiFixtures.DEFAULT_DELIVERY_COMPANY);
            assertThat(item.trackingNumber())
                    .isEqualTo(ExchangeApiFixtures.DEFAULT_TRACKING_NUMBER);
        }
    }

    @Nested
    @DisplayName("toCompleteCommand() - 교환 완료 일괄 변환")
    class ToCompleteCommandTest {

        @Test
        @DisplayName("CompleteExchangeBatchApiRequest를 CompleteExchangeBatchCommand로 변환한다")
        void toCompleteCommand_ConvertsRequest_ReturnsCommand() {
            // given
            CompleteExchangeBatchApiRequest request = ExchangeApiFixtures.completeBatchRequest();

            // when
            CompleteExchangeBatchCommand command =
                    mapper.toCompleteCommand(request, ExchangeApiFixtures.DEFAULT_PROCESSED_BY, 1L);

            // then
            assertThat(command.exchangeClaimIds())
                    .containsExactly(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID);
        }
    }

    @Nested
    @DisplayName("toConvertToRefundCommand() - 환불 전환 일괄 변환")
    class ToConvertToRefundCommandTest {

        @Test
        @DisplayName("ConvertToRefundBatchApiRequest를 ConvertToRefundBatchCommand로 변환한다")
        void toConvertToRefundCommand_ConvertsRequest_ReturnsCommand() {
            // given
            ConvertToRefundBatchApiRequest request =
                    ExchangeApiFixtures.convertToRefundBatchRequest();

            // when
            ConvertToRefundBatchCommand command =
                    mapper.toConvertToRefundCommand(
                            request, ExchangeApiFixtures.DEFAULT_PROCESSED_BY, 1L);

            // then
            assertThat(command.exchangeClaimIds())
                    .containsExactly(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID);
        }
    }

    @Nested
    @DisplayName("toHoldCommand() - 교환 보류 일괄 변환")
    class ToHoldCommandTest {

        @Test
        @DisplayName("isHold=true인 HoldExchangeBatchApiRequest를 HoldExchangeBatchCommand로 변환한다")
        void toHoldCommand_HoldTrue_ReturnsHoldCommand() {
            // given
            HoldExchangeBatchApiRequest request = ExchangeApiFixtures.holdBatchRequest();

            // when
            HoldExchangeBatchCommand command =
                    mapper.toHoldCommand(request, ExchangeApiFixtures.DEFAULT_PROCESSED_BY, 1L);

            // then
            assertThat(command.exchangeClaimIds())
                    .containsExactly(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID);
            assertThat(command.isHold()).isTrue();
            assertThat(command.memo()).isEqualTo("보류 처리 메모");
        }

        @Test
        @DisplayName("isHold=false인 요청은 보류 해제 커맨드로 변환된다")
        void toHoldCommand_HoldFalse_ReturnsReleaseCommand() {
            // given
            HoldExchangeBatchApiRequest request = ExchangeApiFixtures.releaseBatchRequest();

            // when
            HoldExchangeBatchCommand command =
                    mapper.toHoldCommand(request, ExchangeApiFixtures.DEFAULT_PROCESSED_BY, 1L);

            // then
            assertThat(command.isHold()).isFalse();
        }
    }

    @Nested
    @DisplayName("toAddMemoCommand() - 수기 메모 등록 변환")
    class ToAddMemoCommandTest {

        @Test
        @DisplayName("exchangeClaimId와 메모 요청을 AddClaimHistoryMemoCommand로 변환한다")
        void toAddMemoCommand_ConvertsRequest_ReturnsCommand() {
            // given
            String exchangeClaimId = ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID;
            long sellerId = ExchangeApiFixtures.DEFAULT_SELLER_ID;
            String actorName = ExchangeApiFixtures.DEFAULT_REQUESTED_BY;

            // when
            AddClaimHistoryMemoCommand command =
                    mapper.toAddMemoCommand(
                            exchangeClaimId,
                            ExchangeApiFixtures.addMemoRequest(),
                            sellerId,
                            actorName);

            // then
            assertThat(command.claimId()).isEqualTo(exchangeClaimId);
            assertThat(command.message()).isEqualTo("수기 메모 내용입니다.");
            assertThat(command.actorId()).isEqualTo(String.valueOf(sellerId));
            assertThat(command.actorName()).isEqualTo(actorName);
        }
    }

    @Nested
    @DisplayName("toSearchParams() - 교환 목록 검색 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("ExchangeSearchApiRequest를 ExchangeSearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsParams() {
            // given
            ExchangeSearchApiRequest request =
                    ExchangeApiFixtures.searchRequest(
                            List.of("REQUESTED", "COLLECTING"), "CLAIM_NUMBER", "EXC-001", 0, 20);

            // when
            ExchangeSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.statuses()).containsExactly("REQUESTED", "COLLECTING");
            assertThat(params.searchField()).isEqualTo("CLAIM_NUMBER");
            assertThat(params.searchWord()).isEqualTo("EXC-001");
            assertThat(params.page()).isZero();
            assertThat(params.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("null 값은 resolvedPage/resolvedSize 기본값(0, 20)으로 변환된다")
        void toSearchParams_NullPageSize_UsesDefaults() {
            // given
            ExchangeSearchApiRequest request = ExchangeApiFixtures.searchRequest();

            // when
            ExchangeSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.page()).isZero();
            assertThat(params.size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("toSummaryResponse() - 교환 요약 응답 변환")
    class ToSummaryResponseTest {

        @Test
        @DisplayName("ExchangeSummaryResult를 ExchangeSummaryApiResponse로 변환한다")
        void toSummaryResponse_ConvertsResult_ReturnsResponse() {
            // given
            ExchangeSummaryResult result = ExchangeApiFixtures.summaryResult();

            // when
            ExchangeSummaryApiResponse response = mapper.toSummaryResponse(result);

            // then
            assertThat(response.requested()).isEqualTo(5);
            assertThat(response.collecting()).isEqualTo(3);
            assertThat(response.collected()).isEqualTo(2);
            assertThat(response.preparing()).isEqualTo(4);
            assertThat(response.shipping()).isEqualTo(1);
            assertThat(response.completed()).isEqualTo(10);
            assertThat(response.rejected()).isEqualTo(2);
            assertThat(response.cancelled()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 교환 목록 페이지 응답 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("ExchangePageResult를 PageApiResponse<ExchangeListApiResponse>로 변환한다")
        void toPageResponse_ConvertsResult_ReturnsPageResponse() {
            // given
            ExchangePageResult result = ExchangeApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<ExchangeListApiResponse> response = mapper.toPageResponse(result);

            // then
            assertThat(response.content()).hasSize(3);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("빈 결과를 빈 PageApiResponse로 변환한다")
        void toPageResponse_EmptyResult_ReturnsEmptyResponse() {
            // given
            ExchangePageResult result = ExchangeApiFixtures.emptyPageResult();

            // when
            PageApiResponse<ExchangeListApiResponse> response = mapper.toPageResponse(result);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }

        @Test
        @DisplayName("null 문자열 필드는 빈 문자열로 변환된다 (V4 간극)")
        void toPageResponse_NullStringFields_ReturnsEmptyString() {
            // given
            ExchangePageResult result = ExchangeApiFixtures.pageResult(1, 0, 20);

            // when
            PageApiResponse<ExchangeListApiResponse> response = mapper.toPageResponse(result);

            // then
            ExchangeListApiResponse item = response.content().get(0);
            assertThat(item.exchangeClaimId()).isNotNull();
            assertThat(item.orderId()).isNotNull();
        }
    }

    @Nested
    @DisplayName("toDetailResponse() - 교환 상세 응답 변환")
    class ToDetailResponseTest {

        @Test
        @DisplayName("ExchangeDetailResult를 ExchangeDetailApiResponse로 변환한다")
        void toDetailResponse_ConvertsResult_ReturnsResponse() {
            // given
            ExchangeDetailResult result = ExchangeApiFixtures.detailResult();

            // when
            ExchangeDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.exchangeClaimId())
                    .isEqualTo(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID);
            assertThat(response.claimNumber()).isEqualTo(ExchangeApiFixtures.DEFAULT_CLAIM_NUMBER);
            assertThat(response.orderId()).isEqualTo(ExchangeApiFixtures.DEFAULT_ORDER_ITEM_ID);
            assertThat(response.sellerId()).isEqualTo(ExchangeApiFixtures.DEFAULT_SELLER_ID);
            assertThat(response.exchangeQty()).isEqualTo(ExchangeApiFixtures.DEFAULT_EXCHANGE_QTY);
            assertThat(response.exchangeStatus())
                    .isEqualTo(ExchangeApiFixtures.DEFAULT_EXCHANGE_STATUS);
            assertThat(response.reasonType()).isEqualTo(ExchangeApiFixtures.DEFAULT_REASON_TYPE);
        }

        @Test
        @DisplayName("exchangeOption이 존재할 때 올바르게 변환된다")
        void toDetailResponse_WithExchangeOption_ConvertsOption() {
            // given
            ExchangeDetailResult result = ExchangeApiFixtures.detailResult();

            // when
            ExchangeDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.exchangeOption()).isNotNull();
            assertThat(response.exchangeOption().originalProductId())
                    .isEqualTo(ExchangeApiFixtures.DEFAULT_ORIGINAL_PRODUCT_ID);
            assertThat(response.exchangeOption().targetProductId())
                    .isEqualTo(ExchangeApiFixtures.DEFAULT_TARGET_PRODUCT_ID);
            assertThat(response.exchangeOption().targetSkuCode())
                    .isEqualTo(ExchangeApiFixtures.DEFAULT_TARGET_SKU_CODE);
        }

        @Test
        @DisplayName("exchangeOption이 null이면 응답에도 null로 변환된다")
        void toDetailResponse_NullExchangeOption_ReturnsNullOption() {
            // given
            ExchangeDetailResult result = ExchangeApiFixtures.detailResultWithoutOption();

            // when
            ExchangeDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.exchangeOption()).isNull();
            assertThat(response.amountAdjustment()).isNull();
        }

        @Test
        @DisplayName("클레임 이력 목록이 올바르게 변환된다")
        void toDetailResponse_WithHistories_ConvertsHistories() {
            // given
            ExchangeDetailResult result = ExchangeApiFixtures.detailResult();

            // when
            ExchangeDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.claimHistories()).hasSize(1);
            assertThat(response.claimHistories().get(0).historyId())
                    .isEqualTo(ExchangeApiFixtures.DEFAULT_HISTORY_ID);
            assertThat(response.claimHistories().get(0).actor()).isNotNull();
            assertThat(response.claimHistories().get(0).actor().actorType()).isEqualTo("SELLER");
        }

        @Test
        @DisplayName("이력이 없으면 빈 목록으로 변환된다")
        void toDetailResponse_EmptyHistories_ReturnsEmptyList() {
            // given
            ExchangeDetailResult result = ExchangeApiFixtures.detailResultWithoutOption();

            // when
            ExchangeDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.claimHistories()).isEmpty();
        }

        @Test
        @DisplayName("Instant 타입 날짜가 KST ISO 8601 형식으로 변환된다")
        void toDetailResponse_InstantFields_FormattedAsKst() {
            // given
            ExchangeDetailResult result = ExchangeApiFixtures.detailResult();

            // when
            ExchangeDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.requestedAt())
                    .isEqualTo(ExchangeApiFixtures.DEFAULT_FORMATTED_TIME);
            assertThat(response.processedAt())
                    .isEqualTo(ExchangeApiFixtures.DEFAULT_FORMATTED_TIME);
            assertThat(response.createdAt()).isEqualTo(ExchangeApiFixtures.DEFAULT_FORMATTED_TIME);
        }
    }

    @Nested
    @DisplayName("toBatchResultResponse() - 일괄 처리 결과 응답 변환")
    class ToBatchResultResponseTest {

        @Test
        @DisplayName("BatchProcessingResult를 BatchResultApiResponse로 변환한다")
        void toBatchResultResponse_ConvertsMixedResult_ReturnsApiResponse() {
            // given
            BatchProcessingResult<String> result = ExchangeApiFixtures.batchMixedResult();

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
                    ExchangeApiFixtures.batchSuccessResult(
                            List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID));

            // when
            BatchResultApiResponse response = mapper.toBatchResultResponse(result);

            // then
            assertThat(response.totalCount()).isEqualTo(1);
            assertThat(response.successCount()).isEqualTo(1);
            assertThat(response.failureCount()).isZero();

            BatchResultApiResponse.BatchResultItemApiResponse item = response.results().get(0);
            assertThat(item.id()).isEqualTo(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID);
            assertThat(item.success()).isTrue();
            assertThat(item.errorCode()).isNull();
            assertThat(item.errorMessage()).isNull();
        }

        @Test
        @DisplayName("실패 항목의 에러 정보가 올바르게 변환된다")
        void toBatchResultResponse_FailureItem_ReturnsCorrectErrorInfo() {
            // given
            BatchProcessingResult<String> result = ExchangeApiFixtures.batchMixedResult();

            // when
            BatchResultApiResponse response = mapper.toBatchResultResponse(result);

            // then
            BatchResultApiResponse.BatchResultItemApiResponse failedItem =
                    response.results().get(1);
            assertThat(failedItem.success()).isFalse();
            assertThat(failedItem.errorCode()).isEqualTo("INVALID_STATUS");
            assertThat(failedItem.errorMessage()).isEqualTo("현재 상태에서 처리할 수 없습니다.");
        }
    }
}
