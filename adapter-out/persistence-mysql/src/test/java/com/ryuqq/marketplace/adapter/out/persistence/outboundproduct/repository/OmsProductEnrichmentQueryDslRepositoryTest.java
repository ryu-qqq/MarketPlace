package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductMainImageDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductPriceStockDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite.OmsProductSyncInfoDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.condition.OmsProductEnrichmentConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.ProductGroupImageJpaEntity;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * OmsProductEnrichmentQueryDslRepositoryTest - OMS 상품 enrichment QueryDSL 레포지토리 통합 테스트.
 *
 * <p>대표 이미지(fetchMainImages), 가격/재고(fetchPriceStock), 최신 연동상태(fetchLatestSyncInfo) 조회 검증.
 */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("OmsProductEnrichmentQueryDslRepository 통합 테스트")
class OmsProductEnrichmentQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private OmsProductEnrichmentQueryDslRepository repository;

    // 공통 테스트 데이터
    private static final Long PG_ID_1 = 100L;
    private static final Long PG_ID_2 = 200L;
    private static final Long PG_ID_NO_DATA = 999L;

    @BeforeEach
    void setUp() {
        repository =
                new OmsProductEnrichmentQueryDslRepository(
                        new JPAQueryFactory(entityManager),
                        new OmsProductEnrichmentConditionBuilder());
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private ProductGroupImageJpaEntity createThumbnailImage(Long productGroupId, String imageUrl) {
        return ProductGroupImageJpaEntity.create(
                null, productGroupId, imageUrl, null, "THUMBNAIL", 1, false, null);
    }

    private ProductGroupImageJpaEntity createDetailImage(Long productGroupId, String imageUrl) {
        return ProductGroupImageJpaEntity.create(
                null, productGroupId, imageUrl, null, "DETAIL", 2, false, null);
    }

    private ProductGroupImageJpaEntity createDeletedThumbnailImage(Long productGroupId) {
        return ProductGroupImageJpaEntity.create(
                null,
                productGroupId,
                "https://deleted.example.com/img.jpg",
                null,
                "THUMBNAIL",
                1,
                true,
                Instant.now());
    }

    private ProductJpaEntity createProduct(
            Long productGroupId, int currentPrice, int stockQuantity) {
        Instant now = Instant.now();
        return ProductJpaEntity.create(
                null,
                productGroupId,
                "SKU-" + productGroupId + "-" + stockQuantity,
                currentPrice,
                currentPrice,
                null,
                0,
                stockQuantity,
                "ACTIVE",
                1,
                now,
                now);
    }

    private OutboundSyncOutboxJpaEntity createOutbox(
            Long productGroupId,
            OutboundSyncOutboxJpaEntity.Status status,
            Instant processedAt,
            String idempotencyKey) {
        Instant now = Instant.now();
        return OutboundSyncOutboxJpaEntity.of(
                null,
                productGroupId,
                1L,
                1L,
                1L,
                OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                status,
                "{}",
                0,
                3,
                now,
                now,
                processedAt,
                null,
                0L,
                idempotencyKey);
    }

    // ========================================================================
    // 1. fetchMainImages 테스트
    // ========================================================================

    @Nested
    @DisplayName("fetchMainImages 메서드 테스트")
    class FetchMainImagesTest {

        @Test
        @DisplayName("THUMBNAIL 타입 이미지가 있으면 productGroupId 기준으로 맵을 반환합니다")
        void fetchMainImages_WithThumbnailImage_ReturnsImageMap() {
            // given
            persist(createThumbnailImage(PG_ID_1, "https://example.com/img1.jpg"));

            // when
            Map<Long, OmsProductMainImageDto> result = repository.fetchMainImages(List.of(PG_ID_1));

            // then
            assertThat(result).hasSize(1);
            assertThat(result).containsKey(PG_ID_1);
            assertThat(result.get(PG_ID_1).imageUrl()).isEqualTo("https://example.com/img1.jpg");
        }

        @Test
        @DisplayName("DETAIL 타입 이미지는 조회되지 않습니다")
        void fetchMainImages_WithDetailImage_ReturnsEmptyMap() {
            // given
            persist(createDetailImage(PG_ID_1, "https://example.com/detail.jpg"));

            // when
            Map<Long, OmsProductMainImageDto> result = repository.fetchMainImages(List.of(PG_ID_1));

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("삭제된 이미지는 조회되지 않습니다")
        void fetchMainImages_WithDeletedImage_ReturnsEmptyMap() {
            // given
            persist(createDeletedThumbnailImage(PG_ID_1));

            // when
            Map<Long, OmsProductMainImageDto> result = repository.fetchMainImages(List.of(PG_ID_1));

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("여러 상품그룹의 이미지를 각각 조회합니다")
        void fetchMainImages_WithMultipleProductGroups_ReturnsAllImages() {
            // given
            persist(createThumbnailImage(PG_ID_1, "https://example.com/img1.jpg"));
            persist(createThumbnailImage(PG_ID_2, "https://example.com/img2.jpg"));

            // when
            Map<Long, OmsProductMainImageDto> result =
                    repository.fetchMainImages(List.of(PG_ID_1, PG_ID_2));

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(PG_ID_1).imageUrl()).isEqualTo("https://example.com/img1.jpg");
            assertThat(result.get(PG_ID_2).imageUrl()).isEqualTo("https://example.com/img2.jpg");
        }

        @Test
        @DisplayName("이미지가 여러 개여도 첫 번째만 반환합니다 (중복 productGroupId 처리)")
        void fetchMainImages_WithMultipleImagesForSameGroup_ReturnsFirstImage() {
            // given
            persist(
                    ProductGroupImageJpaEntity.create(
                            null,
                            PG_ID_1,
                            "https://example.com/first.jpg",
                            null,
                            "THUMBNAIL",
                            1,
                            false,
                            null));
            persist(
                    ProductGroupImageJpaEntity.create(
                            null,
                            PG_ID_1,
                            "https://example.com/second.jpg",
                            null,
                            "THUMBNAIL",
                            2,
                            false,
                            null));

            // when
            Map<Long, OmsProductMainImageDto> result = repository.fetchMainImages(List.of(PG_ID_1));

            // then
            assertThat(result).hasSize(1);
            assertThat(result).containsKey(PG_ID_1);
        }

        @Test
        @DisplayName("조회 대상에 없는 상품그룹은 맵에 포함되지 않습니다")
        void fetchMainImages_WithNoMatchingProductGroup_ReturnsEmptyMap() {
            // given
            persist(createThumbnailImage(PG_ID_1, "https://example.com/img1.jpg"));

            // when
            Map<Long, OmsProductMainImageDto> result =
                    repository.fetchMainImages(List.of(PG_ID_NO_DATA));

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("빈 목록으로 조회하면 빈 맵을 반환합니다")
        void fetchMainImages_WithEmptyIds_ReturnsEmptyMap() {
            // when
            Map<Long, OmsProductMainImageDto> result = repository.fetchMainImages(List.of());

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. fetchPriceStock 테스트
    // ========================================================================

    @Nested
    @DisplayName("fetchPriceStock 메서드 테스트")
    class FetchPriceStockTest {

        @Test
        @DisplayName("상품이 있으면 최저가/총재고를 반환합니다")
        void fetchPriceStock_WithProducts_ReturnsPriceAndStock() {
            // given
            persist(createProduct(PG_ID_1, 50000, 100));

            // when
            Map<Long, OmsProductPriceStockDto> result =
                    repository.fetchPriceStock(List.of(PG_ID_1));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(PG_ID_1).price()).isEqualTo(50000);
            assertThat(result.get(PG_ID_1).stock()).isEqualTo(100);
        }

        @Test
        @DisplayName("상품이 여러 개이면 최저가격과 총합 재고를 반환합니다")
        void fetchPriceStock_WithMultipleProducts_ReturnsMinPriceAndTotalStock() {
            // given
            persist(createProduct(PG_ID_1, 50000, 30));
            persist(createProduct(PG_ID_1, 40000, 70));

            // when
            Map<Long, OmsProductPriceStockDto> result =
                    repository.fetchPriceStock(List.of(PG_ID_1));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(PG_ID_1).price()).isEqualTo(40000); // 최저가
            assertThat(result.get(PG_ID_1).stock()).isEqualTo(100); // 총합 재고
        }

        @Test
        @DisplayName("여러 상품그룹의 가격/재고를 각각 조회합니다")
        void fetchPriceStock_WithMultipleProductGroups_ReturnsEachPriceStock() {
            // given
            persist(createProduct(PG_ID_1, 10000, 50));
            persist(createProduct(PG_ID_2, 20000, 200));

            // when
            Map<Long, OmsProductPriceStockDto> result =
                    repository.fetchPriceStock(List.of(PG_ID_1, PG_ID_2));

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(PG_ID_1).price()).isEqualTo(10000);
            assertThat(result.get(PG_ID_2).price()).isEqualTo(20000);
        }

        @Test
        @DisplayName("재고가 0인 상품도 조회됩니다")
        void fetchPriceStock_WithZeroStock_ReturnsZeroStock() {
            // given
            persist(createProduct(PG_ID_1, 30000, 0));

            // when
            Map<Long, OmsProductPriceStockDto> result =
                    repository.fetchPriceStock(List.of(PG_ID_1));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(PG_ID_1).stock()).isZero();
        }

        @Test
        @DisplayName("상품이 없는 상품그룹은 맵에 포함되지 않습니다")
        void fetchPriceStock_WithNoProducts_ReturnsEmptyMap() {
            // when
            Map<Long, OmsProductPriceStockDto> result =
                    repository.fetchPriceStock(List.of(PG_ID_NO_DATA));

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("빈 목록으로 조회하면 빈 맵을 반환합니다")
        void fetchPriceStock_WithEmptyIds_ReturnsEmptyMap() {
            // when
            Map<Long, OmsProductPriceStockDto> result = repository.fetchPriceStock(List.of());

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. fetchLatestSyncInfo 테스트
    // ========================================================================

    @Nested
    @DisplayName("fetchLatestSyncInfo 메서드 테스트")
    class FetchLatestSyncInfoTest {

        @Test
        @DisplayName("COMPLETED 상태 outbox가 있으면 연동상태를 반환합니다")
        void fetchLatestSyncInfo_WithCompletedOutbox_ReturnsSyncInfo() {
            // given
            Instant processedAt = Instant.now().minusSeconds(3600).truncatedTo(ChronoUnit.MICROS);
            persist(
                    createOutbox(
                            PG_ID_1,
                            OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                            processedAt,
                            "key-" + UUID.randomUUID()));

            // when
            Map<Long, OmsProductSyncInfoDto> result =
                    repository.fetchLatestSyncInfo(List.of(PG_ID_1));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(PG_ID_1).entityStatusName()).isEqualTo("COMPLETED");
            assertThat(result.get(PG_ID_1).processedAt()).isEqualTo(processedAt);
        }

        @Test
        @DisplayName("여러 outbox가 있으면 processedAt이 가장 최신인 것을 반환합니다")
        void fetchLatestSyncInfo_WithMultipleOutboxes_ReturnsLatestOne() {
            // given
            Instant older = Instant.now().minusSeconds(7200).truncatedTo(ChronoUnit.MICROS);
            Instant newer = Instant.now().minusSeconds(3600).truncatedTo(ChronoUnit.MICROS);
            persist(
                    createOutbox(
                            PG_ID_1,
                            OutboundSyncOutboxJpaEntity.Status.FAILED,
                            older,
                            "key-old-" + UUID.randomUUID()));
            persist(
                    createOutbox(
                            PG_ID_1,
                            OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                            newer,
                            "key-new-" + UUID.randomUUID()));

            // when
            Map<Long, OmsProductSyncInfoDto> result =
                    repository.fetchLatestSyncInfo(List.of(PG_ID_1));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(PG_ID_1).entityStatusName()).isEqualTo("COMPLETED");
            assertThat(result.get(PG_ID_1).processedAt()).isEqualTo(newer);
        }

        @Test
        @DisplayName("FAILED 상태 outbox도 조회됩니다")
        void fetchLatestSyncInfo_WithFailedOutbox_ReturnsSyncInfo() {
            // given
            Instant processedAt = Instant.now().minusSeconds(1800);
            persist(
                    createOutbox(
                            PG_ID_1,
                            OutboundSyncOutboxJpaEntity.Status.FAILED,
                            processedAt,
                            "key-" + UUID.randomUUID()));

            // when
            Map<Long, OmsProductSyncInfoDto> result =
                    repository.fetchLatestSyncInfo(List.of(PG_ID_1));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(PG_ID_1).entityStatusName()).isEqualTo("FAILED");
        }

        @Test
        @DisplayName("여러 상품그룹의 연동상태를 각각 조회합니다")
        void fetchLatestSyncInfo_WithMultipleProductGroups_ReturnsEachSyncInfo() {
            // given
            Instant processedAt1 = Instant.now().minusSeconds(3600);
            Instant processedAt2 = Instant.now().minusSeconds(1800);
            persist(
                    createOutbox(
                            PG_ID_1,
                            OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                            processedAt1,
                            "key-pg1-" + UUID.randomUUID()));
            persist(
                    createOutbox(
                            PG_ID_2,
                            OutboundSyncOutboxJpaEntity.Status.FAILED,
                            processedAt2,
                            "key-pg2-" + UUID.randomUUID()));

            // when
            Map<Long, OmsProductSyncInfoDto> result =
                    repository.fetchLatestSyncInfo(List.of(PG_ID_1, PG_ID_2));

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(PG_ID_1).entityStatusName()).isEqualTo("COMPLETED");
            assertThat(result.get(PG_ID_2).entityStatusName()).isEqualTo("FAILED");
        }

        @Test
        @DisplayName("processedAt이 null인 PENDING 상태 outbox는 서브쿼리에서 매칭되지 않아 조회되지 않습니다")
        void fetchLatestSyncInfo_WithPendingOutboxNullProcessedAt_ReturnsEmptyMap() {
            // given
            persist(
                    createOutbox(
                            PG_ID_1,
                            OutboundSyncOutboxJpaEntity.Status.PENDING,
                            null,
                            "key-" + UUID.randomUUID()));

            // when
            Map<Long, OmsProductSyncInfoDto> result =
                    repository.fetchLatestSyncInfo(List.of(PG_ID_1));

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("연동 이력이 없는 상품그룹은 맵에 포함되지 않습니다")
        void fetchLatestSyncInfo_WithNoOutbox_ReturnsEmptyMap() {
            // when
            Map<Long, OmsProductSyncInfoDto> result =
                    repository.fetchLatestSyncInfo(List.of(PG_ID_NO_DATA));

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("빈 목록으로 조회하면 빈 맵을 반환합니다")
        void fetchLatestSyncInfo_WithEmptyIds_ReturnsEmptyMap() {
            // when
            Map<Long, OmsProductSyncInfoDto> result = repository.fetchLatestSyncInfo(List.of());

            // then
            assertThat(result).isEmpty();
        }
    }
}
