package com.ryuqq.marketplace.application.cancel.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.cancel.CancelCommandFixtures;
import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand.SellerCancelItem;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory.CancelBundle;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory.OutboxWithHistory;
import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
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
@DisplayName("CancelCommandFactory 단위 테스트")
class CancelCommandFactoryTest {

    @InjectMocks private CancelCommandFactory sut;

    @Mock private TimeProvider timeProvider;
    @Mock private ClaimHistoryFactory historyFactory;

    @Nested
    @DisplayName("createSellerCancel() - 판매자 취소 번들 생성")
    class CreateSellerCancelTest {

        @Test
        @DisplayName("SellerCancelItem으로 Cancel, CancelOutbox, ClaimHistory 번들을 생성한다")
        void createSellerCancel_ValidItem_ReturnsCancelBundle() {
            // given
            SellerCancelItem item = CancelCommandFixtures.defaultSellerCancelItem();
            String requestedBy = "seller@marketplace.com";
            long sellerId = 10L;
            Instant now = Instant.now();
            ClaimHistory history = ClaimHistoryFixtures.cancelStatusChangeHistory();

            given(timeProvider.now()).willReturn(now);
            given(
                            historyFactory.createStatusChange(
                                    eq(ClaimType.CANCEL),
                                    any(),
                                    any(),
                                    eq(null),
                                    eq("REQUESTED"),
                                    eq(requestedBy),
                                    eq(requestedBy)))
                    .willReturn(history);

            // when
            CancelBundle bundle = sut.createSellerCancel(item, requestedBy, sellerId);

            // then
            assertThat(bundle).isNotNull();
            assertThat(bundle.cancel()).isNotNull();
            assertThat(bundle.outbox()).isNotNull();
            assertThat(bundle.history()).isEqualTo(history);
            assertThat(bundle.cancel().cancelQty()).isEqualTo(item.cancelQty());
        }
    }

    @Nested
    @DisplayName("createApproveBundle() - 승인 번들 생성")
    class CreateApproveBundleTest {

        @Test
        @DisplayName("승인 시 CancelOutbox와 ClaimHistory 번들을 생성한다")
        void createApproveBundle_ValidCancel_ReturnsOutboxWithHistory() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();
            String processedBy = "admin@marketplace.com";
            Instant now = Instant.now();
            ClaimHistory history = ClaimHistoryFixtures.cancelStatusChangeHistory();

            given(timeProvider.now()).willReturn(now);
            given(
                            historyFactory.createStatusChange(
                                    eq(ClaimType.CANCEL),
                                    eq(cancel.idValue()),
                                    eq(cancel.orderItemIdValue()),
                                    eq("REQUESTED"),
                                    eq("APPROVED"),
                                    eq(processedBy),
                                    eq(processedBy)))
                    .willReturn(history);

            // when
            OutboxWithHistory bundle = sut.createApproveBundle(cancel, processedBy);

            // then
            assertThat(bundle).isNotNull();
            assertThat(bundle.outbox()).isNotNull();
            assertThat(bundle.history()).isEqualTo(history);
        }
    }

    @Nested
    @DisplayName("createRejectBundle() - 거절 번들 생성")
    class CreateRejectBundleTest {

        @Test
        @DisplayName("거절 시 CancelOutbox와 ClaimHistory 번들을 생성한다")
        void createRejectBundle_ValidCancel_ReturnsOutboxWithHistory() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();
            String processedBy = "admin@marketplace.com";
            Instant now = Instant.now();
            ClaimHistory history = ClaimHistoryFixtures.cancelStatusChangeHistory();

            given(timeProvider.now()).willReturn(now);
            given(
                            historyFactory.createStatusChange(
                                    eq(ClaimType.CANCEL),
                                    eq(cancel.idValue()),
                                    eq(cancel.orderItemIdValue()),
                                    eq("REQUESTED"),
                                    eq("REJECTED"),
                                    eq(processedBy),
                                    eq(processedBy)))
                    .willReturn(history);

            // when
            OutboxWithHistory bundle = sut.createRejectBundle(cancel, processedBy);

            // then
            assertThat(bundle).isNotNull();
            assertThat(bundle.outbox()).isNotNull();
            assertThat(bundle.history()).isEqualTo(history);
        }
    }

    @Nested
    @DisplayName("now() - 현재 시간 반환")
    class NowTest {

        @Test
        @DisplayName("TimeProvider에서 현재 시간을 반환한다")
        void now_ReturnsCurrentInstant() {
            // given
            Instant expected = Instant.now();
            given(timeProvider.now()).willReturn(expected);

            // when
            Instant result = sut.now();

            // then
            assertThat(result).isEqualTo(expected);
        }
    }
}
