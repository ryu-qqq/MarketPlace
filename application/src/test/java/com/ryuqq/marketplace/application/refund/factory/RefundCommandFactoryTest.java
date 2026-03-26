package com.ryuqq.marketplace.application.refund.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.refund.RefundCommandFixtures;
import com.ryuqq.marketplace.application.refund.dto.command.RequestRefundBatchCommand.RefundRequestItem;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory.OutboxWithHistory;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory.RefundBundle;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RefundCommandFactory 단위 테스트")
class RefundCommandFactoryTest {

    @InjectMocks private RefundCommandFactory sut;

    @Mock private TimeProvider timeProvider;
    @Mock private ClaimHistoryFactory historyFactory;

    @Nested
    @DisplayName("createRefundRequest() - 환불 요청 번들 생성")
    class CreateRefundRequestTest {

        @Test
        @DisplayName("RefundRequestItem으로 RefundClaim, RefundOutbox, ClaimHistory 번들을 생성한다")
        void createRefundRequest_ValidItem_ReturnsRefundBundle() {
            // given
            RefundRequestItem item = RefundCommandFixtures.defaultRefundRequestItem();
            String requestedBy = "customer@marketplace.com";
            long sellerId = 10L;
            Instant now = Instant.now();
            ClaimHistory history = ClaimHistoryFixtures.refundStatusChangeHistory();

            given(timeProvider.now()).willReturn(now);
            given(
                            historyFactory.createStatusChange(
                                    eq(ClaimType.REFUND),
                                    any(),
                                    eq(null),
                                    eq("REQUESTED"),
                                    eq(requestedBy),
                                    eq(requestedBy)))
                    .willReturn(history);

            // when
            RefundBundle bundle = sut.createRefundRequest(item, requestedBy, sellerId);

            // then
            assertThat(bundle).isNotNull();
            assertThat(bundle.claim()).isNotNull();
            assertThat(bundle.outbox()).isNotNull();
            assertThat(bundle.history()).isEqualTo(history);
            assertThat(bundle.claim().refundQty()).isEqualTo(item.refundQty());
        }
    }

    @Nested
    @DisplayName("createApproveBundle() - 승인 번들 생성")
    class CreateApproveBundleTest {

        @Test
        @DisplayName("승인 시 claim 상태를 변경하고 RefundOutbox와 ClaimHistory 번들을 생성한다")
        void createApproveBundle_ValidClaim_ReturnsOutboxWithHistory() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();
            String processedBy = "admin@marketplace.com";
            Instant now = Instant.now();
            ClaimHistory history = ClaimHistoryFixtures.refundStatusChangeHistory();

            given(timeProvider.now()).willReturn(now);
            given(
                            historyFactory.createStatusChange(
                                    eq(ClaimType.REFUND),
                                    eq(claim.idValue()),
                                    eq("REQUESTED"),
                                    eq("COLLECTING"),
                                    eq(processedBy),
                                    eq(processedBy)))
                    .willReturn(history);

            // when
            OutboxWithHistory bundle = sut.createApproveBundle(claim, processedBy);

            // then
            assertThat(bundle).isNotNull();
            assertThat(bundle.outbox()).isNotNull();
            assertThat(bundle.history()).isEqualTo(history);
            assertThat(claim.status().name()).isEqualTo("COLLECTING");
        }
    }

    @Nested
    @DisplayName("createRejectBundle() - 거절 번들 생성")
    class CreateRejectBundleTest {

        @Test
        @DisplayName("거절 시 claim 상태를 변경하고 RefundOutbox와 ClaimHistory 번들을 생성한다")
        void createRejectBundle_ValidClaim_ReturnsOutboxWithHistory() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();
            String processedBy = "admin@marketplace.com";
            Instant now = Instant.now();
            ClaimHistory history = ClaimHistoryFixtures.refundStatusChangeHistory();

            given(timeProvider.now()).willReturn(now);
            given(
                            historyFactory.createStatusChange(
                                    eq(ClaimType.REFUND),
                                    eq(claim.idValue()),
                                    eq("REQUESTED"),
                                    eq("REJECTED"),
                                    eq(processedBy),
                                    eq(processedBy)))
                    .willReturn(history);

            // when
            OutboxWithHistory bundle = sut.createRejectBundle(claim, processedBy);

            // then
            assertThat(bundle).isNotNull();
            assertThat(bundle.outbox()).isNotNull();
            assertThat(bundle.history()).isEqualTo(history);
            assertThat(claim.status().name()).isEqualTo("REJECTED");
        }
    }

    @Nested
    @DisplayName("createCollectBundle() - 수거 완료 번들 생성")
    class CreateCollectBundleTest {

        @Test
        @DisplayName("수거 완료 시 claim 상태를 변경하고 RefundOutbox와 ClaimHistory 번들을 생성한다")
        void createCollectBundle_ValidClaim_ReturnsOutboxWithHistory() {
            // given
            RefundClaim claim = RefundFixtures.collectingRefundClaim();
            String processedBy = "admin@marketplace.com";
            Instant now = Instant.now();
            ClaimHistory history = ClaimHistoryFixtures.refundStatusChangeHistory();

            given(timeProvider.now()).willReturn(now);
            given(
                            historyFactory.createStatusChange(
                                    eq(ClaimType.REFUND),
                                    eq(claim.idValue()),
                                    eq("COLLECTING"),
                                    eq("COLLECTED"),
                                    eq(processedBy),
                                    eq(processedBy)))
                    .willReturn(history);

            // when
            OutboxWithHistory bundle = sut.createCollectBundle(claim, processedBy);

            // then
            assertThat(bundle).isNotNull();
            assertThat(bundle.outbox()).isNotNull();
            assertThat(bundle.history()).isEqualTo(history);
            assertThat(claim.status().name()).isEqualTo("COLLECTED");
        }
    }
}
