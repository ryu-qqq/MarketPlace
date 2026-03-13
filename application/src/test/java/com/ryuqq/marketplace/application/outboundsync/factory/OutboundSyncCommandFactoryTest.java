package com.ryuqq.marketplace.application.outboundsync.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import java.util.List;
import java.util.Set;
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
@DisplayName("OutboundSyncCommandFactory 단위 테스트")
class OutboundSyncCommandFactoryTest {

    @InjectMocks private OutboundSyncCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    private static final Instant NOW = Instant.parse("2026-03-10T12:00:00Z");

    private static OutboundProduct outboundProduct(long salesChannelId, long shopId) {
        return OutboundProduct.forNew(
                ProductGroupId.of(1L), SalesChannelId.of(salesChannelId), shopId, NOW);
    }

    @Nested
    @DisplayName("createOutboxesForSync() - changedAreas 포함")
    class CreateOutboxesForSyncWithChangedAreasTest {

        @Test
        @DisplayName("changedAreas가 비어있으면 payload는 빈 JSON")
        void emptyChangedAreasProducesEmptyPayload() {
            given(timeProvider.now()).willReturn(NOW);

            List<OutboundSyncOutbox> result =
                    sut.createOutboxesForSync(
                            ProductGroupId.of(1L),
                            SellerId.of(1L),
                            List.of(outboundProduct(1L, 1L)),
                            SyncType.UPDATE,
                            Set.of());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).payload()).isEqualTo("{}");
        }

        @Test
        @DisplayName("changedAreas가 있으면 payload에 JSON 배열로 직렬화")
        void changedAreasSerializedToPayload() {
            given(timeProvider.now()).willReturn(NOW);

            List<OutboundSyncOutbox> result =
                    sut.createOutboxesForSync(
                            ProductGroupId.of(1L),
                            SellerId.of(1L),
                            List.of(outboundProduct(1L, 1L)),
                            SyncType.UPDATE,
                            Set.of(ChangedArea.PRICE, ChangedArea.STOCK));

            assertThat(result).hasSize(1);
            String payload = result.get(0).payload();
            assertThat(payload).contains("\"changedAreas\"");
            assertThat(payload).contains("\"PRICE\"");
            assertThat(payload).contains("\"STOCK\"");
        }

        @Test
        @DisplayName("채널 수만큼 Outbox 생성")
        void createsOutboxPerChannel() {
            given(timeProvider.now()).willReturn(NOW);

            List<OutboundSyncOutbox> result =
                    sut.createOutboxesForSync(
                            ProductGroupId.of(1L),
                            SellerId.of(1L),
                            List.of(outboundProduct(1L, 1L), outboundProduct(2L, 2L)),
                            SyncType.UPDATE,
                            Set.of(ChangedArea.IMAGE));

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("changedAreas가 null이면 빈 payload")
        void nullChangedAreasProducesEmptyPayload() {
            given(timeProvider.now()).willReturn(NOW);

            List<OutboundSyncOutbox> result =
                    sut.createOutboxesForSync(
                            ProductGroupId.of(1L),
                            SellerId.of(1L),
                            List.of(outboundProduct(1L, 1L)),
                            SyncType.UPDATE,
                            null);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).payload()).isEqualTo("{}");
        }

        @Test
        @DisplayName("payload의 changedAreas는 알파벳순 정렬")
        void payloadSortedAlphabetically() {
            given(timeProvider.now()).willReturn(NOW);

            List<OutboundSyncOutbox> result =
                    sut.createOutboxesForSync(
                            ProductGroupId.of(1L),
                            SellerId.of(1L),
                            List.of(outboundProduct(1L, 1L)),
                            SyncType.UPDATE,
                            Set.of(ChangedArea.STOCK, ChangedArea.BASIC_INFO));

            String payload = result.get(0).payload();
            int basicInfoIndex = payload.indexOf("BASIC_INFO");
            int stockIndex = payload.indexOf("STOCK");
            assertThat(basicInfoIndex).isLessThan(stockIndex);
        }
    }

    @Nested
    @DisplayName("createOutboxesForSync() - changedAreas 미포함 (하위호환)")
    class CreateOutboxesForSyncWithoutChangedAreasTest {

        @Test
        @DisplayName("changedAreas 없는 오버로드는 빈 payload 생성")
        void withoutChangedAreasProducesEmptyPayload() {
            given(timeProvider.now()).willReturn(NOW);

            List<OutboundSyncOutbox> result =
                    sut.createOutboxesForSync(
                            ProductGroupId.of(1L),
                            SellerId.of(1L),
                            List.of(outboundProduct(1L, 1L)),
                            SyncType.UPDATE);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).payload()).isEqualTo("{}");
        }
    }
}
