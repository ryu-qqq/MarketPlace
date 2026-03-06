package com.ryuqq.marketplace.application.outboundproduct.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.sellersaleschannel.manager.SellerSalesChannelReadManager;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import com.ryuqq.marketplace.domain.sellersaleschannel.id.SellerSalesChannelId;
import com.ryuqq.marketplace.domain.sellersaleschannel.vo.ConnectionStatus;
import java.time.Instant;
import java.util.List;
import java.util.Map;
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
@DisplayName("ManualSyncProductsValidator 단위 테스트")
class ManualSyncProductsValidatorTest {

    @InjectMocks private ManualSyncProductsValidator sut;
    @Mock private SellerSalesChannelReadManager sellerSalesChannelReadManager;

    @Nested
    @DisplayName("findConnectedChannelIdsBySellerIds() - 셀러별 CONNECTED 채널 조회")
    class FindConnectedChannelIdsBySellerIdsTest {

        @Test
        @DisplayName("여러 셀러의 CONNECTED 채널을 셀러별로 그루핑하여 반환한다")
        void findConnectedChannelIds_MultipleSellers_GroupsBySellerId() {
            // given
            Set<SellerId> sellerIds = Set.of(SellerId.of(1L), SellerId.of(2L));

            SellerSalesChannel seller1Channel10 = connectedChannel(1L, 1L, 10L);
            SellerSalesChannel seller1Channel20 = connectedChannel(2L, 1L, 20L);
            SellerSalesChannel seller2Channel10 = connectedChannel(3L, 2L, 10L);

            given(sellerSalesChannelReadManager.findConnectedBySellerIds(sellerIds))
                    .willReturn(List.of(seller1Channel10, seller1Channel20, seller2Channel10));

            // when
            Map<Long, Set<Long>> result = sut.findConnectedChannelIdsBySellerIds(sellerIds);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(1L)).containsExactlyInAnyOrder(10L, 20L);
            assertThat(result.get(2L)).containsExactlyInAnyOrder(10L);
        }

        @Test
        @DisplayName("CONNECTED 채널이 없으면 빈 Map을 반환한다")
        void findConnectedChannelIds_NoConnected_ReturnsEmptyMap() {
            // given
            Set<SellerId> sellerIds = Set.of(SellerId.of(1L));
            given(sellerSalesChannelReadManager.findConnectedBySellerIds(sellerIds))
                    .willReturn(List.of());

            // when
            Map<Long, Set<Long>> result = sut.findConnectedChannelIdsBySellerIds(sellerIds);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("단일 셀러에 여러 채널이 연결되면 해당 셀러 키에 모든 채널 ID를 포함한다")
        void findConnectedChannelIds_SingleSellerMultipleChannels_ContainsAll() {
            // given
            Set<SellerId> sellerIds = Set.of(SellerId.of(1L));

            SellerSalesChannel ch1 = connectedChannel(1L, 1L, 10L);
            SellerSalesChannel ch2 = connectedChannel(2L, 1L, 20L);
            SellerSalesChannel ch3 = connectedChannel(3L, 1L, 30L);

            given(sellerSalesChannelReadManager.findConnectedBySellerIds(sellerIds))
                    .willReturn(List.of(ch1, ch2, ch3));

            // when
            Map<Long, Set<Long>> result = sut.findConnectedChannelIdsBySellerIds(sellerIds);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(1L)).containsExactlyInAnyOrder(10L, 20L, 30L);
        }

        private SellerSalesChannel connectedChannel(Long id, Long sellerId, Long channelId) {
            Instant now = CommonVoFixtures.yesterday();
            return SellerSalesChannel.reconstitute(
                    SellerSalesChannelId.of(id),
                    SellerId.of(sellerId),
                    SalesChannelId.of(channelId),
                    "TEST",
                    ConnectionStatus.CONNECTED,
                    null,
                    null,
                    null,
                    null,
                    "테스트",
                    0L,
                    now,
                    now);
        }
    }
}
