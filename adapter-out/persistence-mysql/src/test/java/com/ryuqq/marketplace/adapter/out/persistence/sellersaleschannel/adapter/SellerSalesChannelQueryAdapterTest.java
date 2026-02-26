package com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.SellerSalesChannelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.entity.SellerSalesChannelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.mapper.SellerSalesChannelJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.repository.SellerSalesChannelQueryDslRepository;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellersaleschannel.SellerSalesChannelFixtures;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SellerSalesChannelQueryAdapterTest - 셀러 판매채널 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SellerSalesChannelQueryAdapter 단위 테스트")
class SellerSalesChannelQueryAdapterTest {

    @Mock private SellerSalesChannelQueryDslRepository queryDslRepository;

    @Mock private SellerSalesChannelJpaEntityMapper mapper;

    @InjectMocks private SellerSalesChannelQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findConnectedBySellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findConnectedBySellerId 메서드 테스트")
    class FindConnectedBySellerIdTest {

        @Test
        @DisplayName("CONNECTED 상태 채널이 있으면 Domain 목록을 반환합니다")
        void findConnectedBySellerId_WithConnectedChannels_ReturnsDomainList() {
            // given
            SellerId sellerId = SellerId.of(1L);
            SellerSalesChannelJpaEntity entity1 =
                    SellerSalesChannelJpaEntityFixtures.connectedEntity(1L);
            SellerSalesChannelJpaEntity entity2 =
                    SellerSalesChannelJpaEntityFixtures.connectedEntity(2L);
            SellerSalesChannel domain1 = SellerSalesChannelFixtures.connectedSellerSalesChannel(1L);
            SellerSalesChannel domain2 = SellerSalesChannelFixtures.connectedSellerSalesChannel(2L);

            given(queryDslRepository.findConnectedBySellerId(1L))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<SellerSalesChannel> result = queryAdapter.findConnectedBySellerId(sellerId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
            then(queryDslRepository).should().findConnectedBySellerId(1L);
        }

        @Test
        @DisplayName("CONNECTED 상태 채널이 없으면 빈 리스트를 반환합니다")
        void findConnectedBySellerId_WithNoConnectedChannels_ReturnsEmptyList() {
            // given
            SellerId sellerId = SellerId.of(999L);
            given(queryDslRepository.findConnectedBySellerId(999L)).willReturn(List.of());

            // when
            List<SellerSalesChannel> result = queryAdapter.findConnectedBySellerId(sellerId);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findConnectedBySellerId(999L);
        }

        @Test
        @DisplayName("하나의 CONNECTED 채널만 있으면 단일 항목 리스트를 반환합니다")
        void findConnectedBySellerId_WithOneConnectedChannel_ReturnsSingleItemList() {
            // given
            SellerId sellerId = SellerId.of(1L);
            SellerSalesChannelJpaEntity entity =
                    SellerSalesChannelJpaEntityFixtures.connectedEntity(1L);
            SellerSalesChannel domain = SellerSalesChannelFixtures.connectedSellerSalesChannel(1L);

            given(queryDslRepository.findConnectedBySellerId(1L)).willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<SellerSalesChannel> result = queryAdapter.findConnectedBySellerId(sellerId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }

        @Test
        @DisplayName("Mapper가 각 Entity에 대해 호출됩니다")
        void findConnectedBySellerId_CallsMapperForEachEntity() {
            // given
            SellerId sellerId = SellerId.of(1L);
            SellerSalesChannelJpaEntity entity1 =
                    SellerSalesChannelJpaEntityFixtures.connectedEntity(1L);
            SellerSalesChannelJpaEntity entity2 =
                    SellerSalesChannelJpaEntityFixtures.connectedEntity(2L);
            SellerSalesChannel domain1 = SellerSalesChannelFixtures.connectedSellerSalesChannel(1L);
            SellerSalesChannel domain2 = SellerSalesChannelFixtures.connectedSellerSalesChannel(2L);

            given(queryDslRepository.findConnectedBySellerId(1L))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            queryAdapter.findConnectedBySellerId(sellerId);

            // then
            then(mapper).should().toDomain(entity1);
            then(mapper).should().toDomain(entity2);
        }
    }
}
