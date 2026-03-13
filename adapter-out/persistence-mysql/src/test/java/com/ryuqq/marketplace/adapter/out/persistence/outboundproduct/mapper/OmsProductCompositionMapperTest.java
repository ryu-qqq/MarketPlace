package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.OmsProductCompositeDtoFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductListCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductMainImageDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductPriceStockDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductShopInfoDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductSyncInfoDto;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductListResult;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OmsProductCompositionMapperTest - OMS 상품 Composition 매퍼 단위 테스트.
 *
 * <p>composite + enrichment 데이터를 OmsProductListResult로 변환하는 로직 검증.
 */
@Tag("unit")
@DisplayName("OmsProductCompositionMapper 단위 테스트")
class OmsProductCompositionMapperTest {

    private OmsProductCompositionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OmsProductCompositionMapper();
    }

    // ========================================================================
    // 1. toResults 테스트
    // ========================================================================

    @Nested
    @DisplayName("toResults 메서드 테스트")
    class ToResultsTest {

        @Test
        @DisplayName("모든 enrichment 데이터가 있으면 완전한 OmsProductListResult 목록을 반환합니다")
        void toResults_WithAllEnrichmentData_ReturnsCompleteResults() {
            // given
            Long pgId = 100L;
            List<OmsProductListCompositeDto> composites =
                    List.of(OmsProductCompositeDtoFixtures.activeCompositeDto(pgId));
            Map<Long, OmsProductMainImageDto> imageMap =
                    Map.of(pgId, new OmsProductMainImageDto(pgId, "https://example.com/img.jpg"));
            Map<Long, OmsProductPriceStockDto> priceStockMap =
                    Map.of(pgId, new OmsProductPriceStockDto(pgId, 50000, 100));
            Map<Long, OmsProductSyncInfoDto> syncInfoMap =
                    Map.of(
                            pgId,
                            new OmsProductSyncInfoDto(
                                    pgId, "COMPLETED", Instant.now().minusSeconds(3600)));
            Map<Long, OmsProductShopInfoDto> shopInfoMap =
                    Map.of(pgId, new OmsProductShopInfoDto(pgId, 1L, "스마트스토어"));

            // when
            List<OmsProductListResult> results =
                    mapper.toResults(composites, imageMap, priceStockMap, syncInfoMap, shopInfoMap);

            // then
            assertThat(results).hasSize(1);
            OmsProductListResult result = results.get(0);
            assertThat(result.id()).isEqualTo(pgId);
            assertThat(result.productCode()).isEqualTo("PG-" + pgId);
            assertThat(result.imageUrl()).isEqualTo("https://example.com/img.jpg");
            assertThat(result.price()).isEqualTo(50000);
            assertThat(result.stock()).isEqualTo(100);
            assertThat(result.syncStatus()).isEqualTo("SUCCESS");
            assertThat(result.syncStatusLabel()).isEqualTo("연동완료");
            assertThat(result.lastSyncAt()).isNotNull();
            assertThat(result.shopId()).isEqualTo(1L);
            assertThat(result.shopName()).isEqualTo("스마트스토어");
        }

        @Test
        @DisplayName("이미지 정보가 없으면 imageUrl은 null을 반환합니다")
        void toResults_WithNoImageData_ReturnsNullImageUrl() {
            // given
            Long pgId = 100L;
            List<OmsProductListCompositeDto> composites =
                    List.of(OmsProductCompositeDtoFixtures.activeCompositeDto(pgId));
            Map<Long, OmsProductMainImageDto> imageMap = Map.of();
            Map<Long, OmsProductPriceStockDto> priceStockMap =
                    Map.of(pgId, new OmsProductPriceStockDto(pgId, 50000, 100));
            Map<Long, OmsProductSyncInfoDto> syncInfoMap = Map.of();
            Map<Long, OmsProductShopInfoDto> shopInfoMap = Map.of();

            // when
            List<OmsProductListResult> results =
                    mapper.toResults(composites, imageMap, priceStockMap, syncInfoMap, shopInfoMap);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).imageUrl()).isNull();
        }

        @Test
        @DisplayName("가격/재고 정보가 없으면 기본값(0)을 사용합니다")
        void toResults_WithNoPriceStockData_UsesDefaultZeroValues() {
            // given
            Long pgId = 100L;
            List<OmsProductListCompositeDto> composites =
                    List.of(OmsProductCompositeDtoFixtures.activeCompositeDto(pgId));
            Map<Long, OmsProductMainImageDto> imageMap = Map.of();
            Map<Long, OmsProductPriceStockDto> priceStockMap = Map.of();
            Map<Long, OmsProductSyncInfoDto> syncInfoMap = Map.of();
            Map<Long, OmsProductShopInfoDto> shopInfoMap = Map.of();

            // when
            List<OmsProductListResult> results =
                    mapper.toResults(composites, imageMap, priceStockMap, syncInfoMap, shopInfoMap);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).price()).isZero();
            assertThat(results.get(0).stock()).isZero();
        }

        @Test
        @DisplayName("연동 정보가 없으면 syncStatus는 NONE, syncStatusLabel은 미연동을 반환합니다")
        void toResults_WithNoSyncInfoData_ReturnsNoneStatus() {
            // given
            Long pgId = 100L;
            List<OmsProductListCompositeDto> composites =
                    List.of(OmsProductCompositeDtoFixtures.activeCompositeDto(pgId));
            Map<Long, OmsProductMainImageDto> imageMap = Map.of();
            Map<Long, OmsProductPriceStockDto> priceStockMap = Map.of();
            Map<Long, OmsProductSyncInfoDto> syncInfoMap = Map.of();
            Map<Long, OmsProductShopInfoDto> shopInfoMap = Map.of();

            // when
            List<OmsProductListResult> results =
                    mapper.toResults(composites, imageMap, priceStockMap, syncInfoMap, shopInfoMap);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).syncStatus()).isEqualTo("NONE");
            assertThat(results.get(0).syncStatusLabel()).isEqualTo("미연동");
            assertThat(results.get(0).lastSyncAt()).isNull();
        }

        @Test
        @DisplayName("빈 composites 목록 입력 시 빈 결과를 반환합니다")
        void toResults_WithEmptyComposites_ReturnsEmptyList() {
            // when
            List<OmsProductListResult> results =
                    mapper.toResults(List.of(), Map.of(), Map.of(), Map.of(), Map.of());

            // then
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("여러 상품에 대해 각각의 enrichment 데이터를 올바르게 매핑합니다")
        void toResults_WithMultipleComposites_MapsEachCorrectly() {
            // given
            Long pgId1 = 100L;
            Long pgId2 = 200L;
            List<OmsProductListCompositeDto> composites =
                    List.of(
                            OmsProductCompositeDtoFixtures.activeCompositeDto(pgId1),
                            OmsProductCompositeDtoFixtures.activeCompositeDto(pgId2));
            Map<Long, OmsProductMainImageDto> imageMap =
                    Map.of(
                            pgId1, new OmsProductMainImageDto(pgId1, "https://example.com/1.jpg"),
                            pgId2, new OmsProductMainImageDto(pgId2, "https://example.com/2.jpg"));
            Map<Long, OmsProductPriceStockDto> priceStockMap =
                    Map.of(
                            pgId1, new OmsProductPriceStockDto(pgId1, 10000, 50),
                            pgId2, new OmsProductPriceStockDto(pgId2, 20000, 200));
            Map<Long, OmsProductSyncInfoDto> syncInfoMap = Map.of();
            Map<Long, OmsProductShopInfoDto> shopInfoMap = Map.of();

            // when
            List<OmsProductListResult> results =
                    mapper.toResults(composites, imageMap, priceStockMap, syncInfoMap, shopInfoMap);

            // then
            assertThat(results).hasSize(2);
            OmsProductListResult first =
                    results.stream().filter(r -> r.id() == pgId1).findFirst().orElseThrow();
            OmsProductListResult second =
                    results.stream().filter(r -> r.id() == pgId2).findFirst().orElseThrow();
            assertThat(first.price()).isEqualTo(10000);
            assertThat(second.price()).isEqualTo(20000);
        }
    }

    // ========================================================================
    // 2. resolveStatusLabel 테스트
    // ========================================================================

    @Nested
    @DisplayName("resolveStatusLabel 메서드 테스트")
    class ResolveStatusLabelTest {

        @Test
        @DisplayName("ACTIVE 상태의 라벨을 반환합니다")
        void resolveStatusLabel_WithActive_ReturnsLabel() {
            // when
            String label = mapper.resolveStatusLabel("ACTIVE");

            // then
            assertThat(label).isNotBlank();
        }

        @Test
        @DisplayName("null 상태 입력 시 빈 문자열을 반환합니다")
        void resolveStatusLabel_WithNull_ReturnsEmptyString() {
            // when
            String label = mapper.resolveStatusLabel(null);

            // then
            assertThat(label).isEmpty();
        }

        @Test
        @DisplayName("알 수 없는 상태 입력 시 원본 값을 반환합니다")
        void resolveStatusLabel_WithUnknownStatus_ReturnsRawStatus() {
            // when
            String label = mapper.resolveStatusLabel("UNKNOWN_STATUS");

            // then
            assertThat(label).isEqualTo("UNKNOWN_STATUS");
        }
    }

    // ========================================================================
    // 3. mapSyncStatus 테스트
    // ========================================================================

    @Nested
    @DisplayName("mapSyncStatus 메서드 테스트")
    class MapSyncStatusTest {

        @Test
        @DisplayName("COMPLETED는 SUCCESS로 매핑됩니다")
        void mapSyncStatus_WithCompleted_ReturnsSuccess() {
            // when
            String result = mapper.mapSyncStatus("COMPLETED");

            // then
            assertThat(result).isEqualTo("SUCCESS");
        }

        @Test
        @DisplayName("FAILED는 FAILED로 매핑됩니다")
        void mapSyncStatus_WithFailed_ReturnsFailed() {
            // when
            String result = mapper.mapSyncStatus("FAILED");

            // then
            assertThat(result).isEqualTo("FAILED");
        }

        @Test
        @DisplayName("PENDING은 PENDING으로 매핑됩니다")
        void mapSyncStatus_WithPending_ReturnsPending() {
            // when
            String result = mapper.mapSyncStatus("PENDING");

            // then
            assertThat(result).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("PROCESSING은 PENDING으로 매핑됩니다")
        void mapSyncStatus_WithProcessing_ReturnsPending() {
            // when
            String result = mapper.mapSyncStatus("PROCESSING");

            // then
            assertThat(result).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("알 수 없는 상태는 NONE으로 매핑됩니다")
        void mapSyncStatus_WithUnknownStatus_ReturnsNone() {
            // when
            String result = mapper.mapSyncStatus("UNKNOWN");

            // then
            assertThat(result).isEqualTo("NONE");
        }
    }

    // ========================================================================
    // 4. resolveSyncStatusLabel 테스트
    // ========================================================================

    @Nested
    @DisplayName("resolveSyncStatusLabel 메서드 테스트")
    class ResolveSyncStatusLabelTest {

        @Test
        @DisplayName("SUCCESS는 연동완료를 반환합니다")
        void resolveSyncStatusLabel_WithSuccess_ReturnsCompletedLabel() {
            // when
            String label = mapper.resolveSyncStatusLabel("SUCCESS");

            // then
            assertThat(label).isEqualTo("연동완료");
        }

        @Test
        @DisplayName("FAILED는 연동실패를 반환합니다")
        void resolveSyncStatusLabel_WithFailed_ReturnsFailedLabel() {
            // when
            String label = mapper.resolveSyncStatusLabel("FAILED");

            // then
            assertThat(label).isEqualTo("연동실패");
        }

        @Test
        @DisplayName("PENDING은 연동대기를 반환합니다")
        void resolveSyncStatusLabel_WithPending_ReturnsPendingLabel() {
            // when
            String label = mapper.resolveSyncStatusLabel("PENDING");

            // then
            assertThat(label).isEqualTo("연동대기");
        }

        @Test
        @DisplayName("NONE은 미연동을 반환합니다")
        void resolveSyncStatusLabel_WithNone_ReturnsNotSyncedLabel() {
            // when
            String label = mapper.resolveSyncStatusLabel("NONE");

            // then
            assertThat(label).isEqualTo("미연동");
        }

        @Test
        @DisplayName("알 수 없는 상태는 미연동을 반환합니다")
        void resolveSyncStatusLabel_WithUnknownStatus_ReturnsNotSyncedLabel() {
            // when
            String label = mapper.resolveSyncStatusLabel("UNKNOWN");

            // then
            assertThat(label).isEqualTo("미연동");
        }
    }
}
