package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.OmsProductCompositeDtoFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductListCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductMainImageDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductPriceStockDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductShopInfoDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductSyncInfoDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.mapper.OmsProductCompositionMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.repository.OmsProductCompositionQueryDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.repository.OmsProductEnrichmentQueryDslRepository;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductListResult;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSearchCriteria;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OmsProductCompositionQueryAdapterTest - OMS 상품 Composition 조회 어댑터 단위 테스트.
 *
 * <p>2-pass 전략: 1) base composite 조회 → 2) enrichment 조회 + 매핑 로직 검증.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OmsProductCompositionQueryAdapter 단위 테스트")
class OmsProductCompositionQueryAdapterTest {

    @Mock private OmsProductCompositionQueryDslRepository compositionRepository;

    @Mock private OmsProductEnrichmentQueryDslRepository enrichmentRepository;

    @Mock private OmsProductCompositionMapper mapper;

    @InjectMocks private OmsProductCompositionQueryAdapter adapter;

    // ========================================================================
    // 1. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("composites가 있으면 enrichment 3건을 조회하고 매핑하여 반환합니다")
        void findByCriteria_WithComposites_FetchesEnrichmentAndMapsResults() {
            // given
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();
            List<OmsProductListCompositeDto> composites =
                    List.of(OmsProductCompositeDtoFixtures.activeCompositeDto(100L));
            Map<Long, OmsProductMainImageDto> imageMap =
                    OmsProductCompositeDtoFixtures.mainImageMap(List.of(100L));
            Map<Long, OmsProductPriceStockDto> priceStockMap =
                    OmsProductCompositeDtoFixtures.priceStockMap(List.of(100L));
            Map<Long, OmsProductSyncInfoDto> syncInfoMap =
                    OmsProductCompositeDtoFixtures.completedSyncInfoMap(List.of(100L));
            Map<Long, OmsProductShopInfoDto> shopInfoMap =
                    OmsProductCompositeDtoFixtures.shopInfoMap(List.of(100L));
            List<OmsProductListResult> expected =
                    List.of(
                            new OmsProductListResult(
                                    100L,
                                    "PG-100",
                                    "테스트 상품",
                                    "https://example.com/image.jpg",
                                    50000,
                                    100,
                                    "ACTIVE",
                                    "활성",
                                    "테스트 셀러",
                                    java.time.Instant.now(),
                                    "SUCCESS",
                                    "연동완료",
                                    java.time.Instant.now(),
                                    1L,
                                    "스마트스토어"));

            given(compositionRepository.findByCriteria(criteria)).willReturn(composites);
            given(enrichmentRepository.fetchMainImages(anyList())).willReturn(imageMap);
            given(enrichmentRepository.fetchPriceStock(anyList())).willReturn(priceStockMap);
            given(enrichmentRepository.fetchLatestSyncInfo(anyList())).willReturn(syncInfoMap);
            given(enrichmentRepository.fetchShopInfo(anyList(), anyList())).willReturn(shopInfoMap);
            given(mapper.toResults(any(), any(), any(), any(), any())).willReturn(expected);

            // when
            List<OmsProductListResult> results = adapter.findByCriteria(criteria);

            // then
            assertThat(results).hasSize(1);
            then(compositionRepository).should().findByCriteria(criteria);
            then(enrichmentRepository).should().fetchMainImages(anyList());
            then(enrichmentRepository).should().fetchPriceStock(anyList());
            then(enrichmentRepository).should().fetchLatestSyncInfo(anyList());
            then(enrichmentRepository).should().fetchShopInfo(anyList(), anyList());
            then(mapper).should().toResults(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("composites가 비어있으면 enrichment 조회 없이 빈 목록을 반환합니다")
        void findByCriteria_WithEmptyComposites_ReturnsEmptyWithoutEnrichment() {
            // given
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();
            given(compositionRepository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<OmsProductListResult> results = adapter.findByCriteria(criteria);

            // then
            assertThat(results).isEmpty();
            then(compositionRepository).should().findByCriteria(criteria);
            then(enrichmentRepository).shouldHaveNoInteractions();
            then(mapper).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("여러 composites가 있으면 모든 productGroupId를 enrichment 조회에 전달합니다")
        void findByCriteria_WithMultipleComposites_PassesAllIdsToEnrichment() {
            // given
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();
            List<OmsProductListCompositeDto> composites =
                    OmsProductCompositeDtoFixtures.compositeDtoList(3);

            given(compositionRepository.findByCriteria(criteria)).willReturn(composites);
            given(enrichmentRepository.fetchMainImages(anyList())).willReturn(Map.of());
            given(enrichmentRepository.fetchPriceStock(anyList())).willReturn(Map.of());
            given(enrichmentRepository.fetchLatestSyncInfo(anyList())).willReturn(Map.of());
            given(enrichmentRepository.fetchShopInfo(anyList(), anyList())).willReturn(Map.of());
            given(mapper.toResults(any(), any(), any(), any(), any())).willReturn(List.of());

            // when
            adapter.findByCriteria(criteria);

            // then
            then(enrichmentRepository).should().fetchMainImages(anyList());
            then(enrichmentRepository).should().fetchPriceStock(anyList());
            then(enrichmentRepository).should().fetchLatestSyncInfo(anyList());
            then(enrichmentRepository).should().fetchShopInfo(anyList(), anyList());
        }
    }

    // ========================================================================
    // 2. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("compositionRepository의 countByCriteria를 그대로 위임합니다")
        void countByCriteria_DelegatesToRepository() {
            // given
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();
            given(compositionRepository.countByCriteria(criteria)).willReturn(42L);

            // when
            long count = adapter.countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(42L);
            then(compositionRepository).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("데이터가 없으면 0을 반환합니다")
        void countByCriteria_WithNoData_ReturnsZero() {
            // given
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();
            given(compositionRepository.countByCriteria(criteria)).willReturn(0L);

            // when
            long count = adapter.countByCriteria(criteria);

            // then
            assertThat(count).isZero();
        }
    }
}
