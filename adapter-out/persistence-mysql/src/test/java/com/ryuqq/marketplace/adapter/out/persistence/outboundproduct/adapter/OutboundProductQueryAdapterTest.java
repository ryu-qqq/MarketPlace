package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.OutboundProductJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.entity.OutboundProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.mapper.OutboundProductJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.repository.OutboundProductJpaRepository;
import com.ryuqq.marketplace.domain.outboundproduct.OutboundProductFixtures;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OutboundProductQueryAdapterTest - OutboundProduct Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: QueryAdapter는 JpaRepository만 사용 (QueryDSL 없음).
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OutboundProductQueryAdapter 단위 테스트")
class OutboundProductQueryAdapterTest {

    @Mock private OutboundProductJpaRepository jpaRepository;

    @Mock private OutboundProductJpaEntityMapper mapper;

    @InjectMocks private OutboundProductQueryAdapter queryAdapter;

    // ========================================================================
    // 1. existsByProductGroupIdAndSalesChannelId 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsByProductGroupIdAndSalesChannelId 메서드 테스트")
    class ExistsByProductGroupIdAndSalesChannelIdTest {

        @Test
        @DisplayName("존재하는 productGroupId + salesChannelId 조합에서 true를 반환합니다")
        void existsByProductGroupIdAndSalesChannelId_WithExistingCombination_ReturnsTrue() {
            // given
            Long productGroupId = OutboundProductJpaEntityFixtures.DEFAULT_PRODUCT_GROUP_ID;
            Long salesChannelId = OutboundProductJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID;

            given(
                            jpaRepository.existsByProductGroupIdAndSalesChannelId(
                                    productGroupId, salesChannelId))
                    .willReturn(true);

            // when
            boolean result =
                    queryAdapter.existsByProductGroupIdAndSalesChannelId(
                            productGroupId, salesChannelId);

            // then
            assertThat(result).isTrue();
            then(jpaRepository)
                    .should()
                    .existsByProductGroupIdAndSalesChannelId(productGroupId, salesChannelId);
        }

        @Test
        @DisplayName("존재하지 않는 productGroupId + salesChannelId 조합에서 false를 반환합니다")
        void existsByProductGroupIdAndSalesChannelId_WithNonExistingCombination_ReturnsFalse() {
            // given
            Long productGroupId = 9999L;
            Long salesChannelId = 8888L;

            given(
                            jpaRepository.existsByProductGroupIdAndSalesChannelId(
                                    productGroupId, salesChannelId))
                    .willReturn(false);

            // when
            boolean result =
                    queryAdapter.existsByProductGroupIdAndSalesChannelId(
                            productGroupId, salesChannelId);

            // then
            assertThat(result).isFalse();
        }
    }

    // ========================================================================
    // 2. findByProductGroupIdAndSalesChannelId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupIdAndSalesChannelId 메서드 테스트")
    class FindByProductGroupIdAndSalesChannelIdTest {

        @Test
        @DisplayName("존재하는 조합으로 조회 시 Domain을 반환합니다")
        void findByProductGroupIdAndSalesChannelId_WithExistingCombination_ReturnsDomain() {
            // given
            Long productGroupId = OutboundProductJpaEntityFixtures.DEFAULT_PRODUCT_GROUP_ID;
            Long salesChannelId = OutboundProductJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID;

            OutboundProductJpaEntity entity = OutboundProductJpaEntityFixtures.registeredEntity(1L);
            OutboundProduct domain = OutboundProductFixtures.registeredProduct();

            given(
                            jpaRepository.findByProductGroupIdAndSalesChannelId(
                                    productGroupId, salesChannelId))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<OutboundProduct> result =
                    queryAdapter.findByProductGroupIdAndSalesChannelId(
                            productGroupId, salesChannelId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(jpaRepository)
                    .should()
                    .findByProductGroupIdAndSalesChannelId(productGroupId, salesChannelId);
        }

        @Test
        @DisplayName("존재하지 않는 조합으로 조회 시 빈 Optional을 반환합니다")
        void findByProductGroupIdAndSalesChannelId_WithNonExistingCombination_ReturnsEmpty() {
            // given
            Long productGroupId = 9999L;
            Long salesChannelId = 8888L;

            given(
                            jpaRepository.findByProductGroupIdAndSalesChannelId(
                                    productGroupId, salesChannelId))
                    .willReturn(Optional.empty());

            // when
            Optional<OutboundProduct> result =
                    queryAdapter.findByProductGroupIdAndSalesChannelId(
                            productGroupId, salesChannelId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("조회 후 Mapper를 통해 Domain으로 변환합니다")
        void findByProductGroupIdAndSalesChannelId_WhenFound_UsesMapper() {
            // given
            Long productGroupId = OutboundProductJpaEntityFixtures.DEFAULT_PRODUCT_GROUP_ID;
            Long salesChannelId = OutboundProductJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID;

            OutboundProductJpaEntity entity = OutboundProductJpaEntityFixtures.registeredEntity(1L);
            OutboundProduct domain = OutboundProductFixtures.registeredProduct();

            given(
                            jpaRepository.findByProductGroupIdAndSalesChannelId(
                                    productGroupId, salesChannelId))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            queryAdapter.findByProductGroupIdAndSalesChannelId(productGroupId, salesChannelId);

            // then
            then(mapper).should().toDomain(entity);
        }

        @Test
        @DisplayName("조회 결과가 없으면 Mapper가 호출되지 않습니다")
        void findByProductGroupIdAndSalesChannelId_WhenNotFound_DoesNotCallMapper() {
            // given
            Long productGroupId = 9999L;
            Long salesChannelId = 8888L;

            given(
                            jpaRepository.findByProductGroupIdAndSalesChannelId(
                                    productGroupId, salesChannelId))
                    .willReturn(Optional.empty());

            // when
            queryAdapter.findByProductGroupIdAndSalesChannelId(productGroupId, salesChannelId);

            // then
            then(mapper).shouldHaveNoInteractions();
        }
    }
}
