package com.ryuqq.marketplace.integration.inboundproduct;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.InboundProductJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.entity.InboundProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.repository.InboundProductJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.entity.InboundSourceJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.repository.InboundSourceJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * InboundProduct Repository 통합 테스트.
 *
 * <p>InboundProductJpaRepository의 쿼리 동작을 검증합니다.
 *
 * <p>테스트 대상: - findByInboundSourceIdAndExternalProductCode - 소스ID + 상품코드 복합 조회
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("inboundproduct")
@DisplayName("InboundProduct Repository 통합 테스트")
class InboundProductRepositoryE2ETest extends E2ETestBase {

    @Autowired private InboundProductJpaRepository inboundProductRepository;
    @Autowired private InboundSourceJpaRepository inboundSourceRepository;

    private Long savedSourceId;

    @BeforeEach
    void setUp() {
        inboundProductRepository.deleteAll();
        inboundSourceRepository.deleteAll();

        Instant now = Instant.now();
        InboundSourceJpaEntity source =
                InboundSourceJpaEntity.create(
                        null,
                        "TEST-SOURCE-001",
                        "테스트 소스",
                        "CRAWLING",
                        "ACTIVE",
                        "통합 테스트용 소스",
                        now,
                        now);
        savedSourceId = inboundSourceRepository.save(source).getId();
    }

    @AfterEach
    void tearDown() {
        inboundProductRepository.deleteAll();
        inboundSourceRepository.deleteAll();
    }

    // ========================================================================
    // 1. findByInboundSourceIdAndExternalProductCode 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByInboundSourceIdAndExternalProductCode 쿼리 테스트")
    class FindByInboundSourceIdAndExternalProductCodeTest {

        @Test
        @Tag("P0")
        @DisplayName("[R1-S01] 소스ID + 상품코드가 일치하는 레코드를 조회합니다")
        void findByInboundSourceIdAndExternalProductCode_ExistingRecord_ReturnsEntity() {
            // given
            InboundProductJpaEntity entity =
                    InboundProductJpaEntity.create(
                            null,
                            savedSourceId,
                            "EXT-PROD-001",
                            "EXT-BRAND-001",
                            "EXT-CAT-001",
                            null,
                            null,
                            null,
                            InboundProductJpaEntityFixtures.DEFAULT_SELLER_ID,
                            "RECEIVED",
                            null,
                            null,
                            null,
                            null,
                            Instant.now(),
                            Instant.now());
            inboundProductRepository.save(entity);

            // when
            Optional<InboundProductJpaEntity> result =
                    inboundProductRepository.findByInboundSourceIdAndExternalProductCode(
                            savedSourceId, "EXT-PROD-001");

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getInboundSourceId()).isEqualTo(savedSourceId);
            assertThat(result.get().getExternalProductCode()).isEqualTo("EXT-PROD-001");
            assertThat(result.get().getStatus()).isEqualTo("RECEIVED");
        }

        @Test
        @Tag("P0")
        @DisplayName("[R1-F01] 소스ID가 다르면 조회되지 않습니다")
        void findByInboundSourceIdAndExternalProductCode_DifferentSourceId_ReturnsEmpty() {
            // given
            InboundProductJpaEntity entity =
                    InboundProductJpaEntity.create(
                            null,
                            savedSourceId,
                            "EXT-PROD-001",
                            "EXT-BRAND-001",
                            "EXT-CAT-001",
                            null,
                            null,
                            null,
                            InboundProductJpaEntityFixtures.DEFAULT_SELLER_ID,
                            "RECEIVED",
                            null,
                            null,
                            null,
                            null,
                            Instant.now(),
                            Instant.now());
            inboundProductRepository.save(entity);

            // when
            Optional<InboundProductJpaEntity> result =
                    inboundProductRepository.findByInboundSourceIdAndExternalProductCode(
                            9999L, "EXT-PROD-001");

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @Tag("P0")
        @DisplayName("[R1-F02] 상품코드가 다르면 조회되지 않습니다")
        void findByInboundSourceIdAndExternalProductCode_DifferentProductCode_ReturnsEmpty() {
            // given
            InboundProductJpaEntity entity =
                    InboundProductJpaEntity.create(
                            null,
                            savedSourceId,
                            "EXT-PROD-001",
                            "EXT-BRAND-001",
                            "EXT-CAT-001",
                            null,
                            null,
                            null,
                            InboundProductJpaEntityFixtures.DEFAULT_SELLER_ID,
                            "RECEIVED",
                            null,
                            null,
                            null,
                            null,
                            Instant.now(),
                            Instant.now());
            inboundProductRepository.save(entity);

            // when
            Optional<InboundProductJpaEntity> result =
                    inboundProductRepository.findByInboundSourceIdAndExternalProductCode(
                            savedSourceId, "NON-EXIST-CODE");

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @Tag("P0")
        @DisplayName("[R1-F03] 동일 소스에 여러 상품이 있을 때 특정 상품코드로 정확히 조회됩니다")
        void findByInboundSourceIdAndExternalProductCode_MultipleProducts_ReturnsCorrectOne() {
            // given
            Instant now = Instant.now();
            inboundProductRepository.saveAll(
                    List.of(
                            InboundProductJpaEntity.create(
                                    null,
                                    savedSourceId,
                                    "EXT-PROD-001",
                                    "BRAND-A",
                                    "CAT-A",
                                    null,
                                    null,
                                    null,
                                    InboundProductJpaEntityFixtures.DEFAULT_SELLER_ID,
                                    "RECEIVED",
                                    null,
                                    null,
                                    null,
                                    null,
                                    now,
                                    now),
                            InboundProductJpaEntity.create(
                                    null,
                                    savedSourceId,
                                    "EXT-PROD-002",
                                    "BRAND-B",
                                    "CAT-B",
                                    null,
                                    null,
                                    null,
                                    InboundProductJpaEntityFixtures.DEFAULT_SELLER_ID,
                                    "RECEIVED",
                                    null,
                                    null,
                                    null,
                                    null,
                                    now,
                                    now),
                            InboundProductJpaEntity.create(
                                    null,
                                    savedSourceId,
                                    "EXT-PROD-003",
                                    "BRAND-C",
                                    "CAT-C",
                                    null,
                                    null,
                                    null,
                                    InboundProductJpaEntityFixtures.DEFAULT_SELLER_ID,
                                    "RECEIVED",
                                    null,
                                    null,
                                    null,
                                    null,
                                    now,
                                    now)));

            // when
            Optional<InboundProductJpaEntity> result =
                    inboundProductRepository.findByInboundSourceIdAndExternalProductCode(
                            savedSourceId, "EXT-PROD-002");

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getExternalProductCode()).isEqualTo("EXT-PROD-002");
            assertThat(result.get().getExternalBrandCode()).isEqualTo("BRAND-B");
        }
    }

    // ========================================================================
    // 2. save / saveAll 기본 동작 테스트
    // ========================================================================

    @Nested
    @DisplayName("save / saveAll 기본 동작 테스트")
    class SaveTest {

        @Test
        @Tag("P0")
        @DisplayName("[R2-S01] 단건 저장 후 ID가 채번됩니다")
        void save_SingleEntity_AssignsId() {
            // given
            InboundProductJpaEntity entity =
                    InboundProductJpaEntity.create(
                            null,
                            savedSourceId,
                            "EXT-PROD-SAVE-001",
                            "EXT-BRAND-001",
                            "EXT-CAT-001",
                            null,
                            null,
                            null,
                            InboundProductJpaEntityFixtures.DEFAULT_SELLER_ID,
                            "RECEIVED",
                            null,
                            null,
                            null,
                            null,
                            Instant.now(),
                            Instant.now());

            // when
            InboundProductJpaEntity saved = inboundProductRepository.save(entity);

            // then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getExternalProductCode()).isEqualTo("EXT-PROD-SAVE-001");
        }

        @Test
        @Tag("P0")
        @DisplayName("[R2-S02] 여러 건 저장 후 모두 조회됩니다")
        void saveAll_MultipleEntities_AllPersisted() {
            // given
            Instant now = Instant.now();
            List<InboundProductJpaEntity> entities =
                    List.of(
                            InboundProductJpaEntity.create(
                                    null,
                                    savedSourceId,
                                    "BATCH-001",
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    InboundProductJpaEntityFixtures.DEFAULT_SELLER_ID,
                                    "RECEIVED",
                                    null,
                                    null,
                                    null,
                                    null,
                                    now,
                                    now),
                            InboundProductJpaEntity.create(
                                    null,
                                    savedSourceId,
                                    "BATCH-002",
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    InboundProductJpaEntityFixtures.DEFAULT_SELLER_ID,
                                    "RECEIVED",
                                    null,
                                    null,
                                    null,
                                    null,
                                    now,
                                    now),
                            InboundProductJpaEntity.create(
                                    null,
                                    savedSourceId,
                                    "BATCH-003",
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    InboundProductJpaEntityFixtures.DEFAULT_SELLER_ID,
                                    "RECEIVED",
                                    null,
                                    null,
                                    null,
                                    null,
                                    now,
                                    now));

            // when
            List<InboundProductJpaEntity> saved = inboundProductRepository.saveAll(entities);

            // then
            assertThat(saved).hasSize(3);
            assertThat(saved).allMatch(e -> e.getId() != null);
            assertThat(inboundProductRepository.count()).isEqualTo(3);
        }

        @Test
        @Tag("P0")
        @DisplayName("[R2-S03] 내부 매핑 정보가 있는 MAPPED 상태 레코드를 저장합니다")
        void save_MappedEntity_PersistsMappingFields() {
            // given
            Instant now = Instant.now();
            InboundProductJpaEntity entity =
                    InboundProductJpaEntity.create(
                            null,
                            savedSourceId,
                            "EXT-PROD-MAPPED",
                            "EXT-BRAND-001",
                            "EXT-CAT-001",
                            InboundProductJpaEntityFixtures.DEFAULT_INTERNAL_BRAND_ID,
                            InboundProductJpaEntityFixtures.DEFAULT_INTERNAL_CATEGORY_ID,
                            null,
                            InboundProductJpaEntityFixtures.DEFAULT_SELLER_ID,
                            "MAPPED",
                            null,
                            InboundProductJpaEntityFixtures.DEFAULT_RESOLVED_SHIPPING_POLICY_ID,
                            InboundProductJpaEntityFixtures.DEFAULT_RESOLVED_REFUND_POLICY_ID,
                            InboundProductJpaEntityFixtures.DEFAULT_RESOLVED_NOTICE_CATEGORY_ID,
                            now,
                            now);

            // when
            InboundProductJpaEntity saved = inboundProductRepository.save(entity);

            // then
            Optional<InboundProductJpaEntity> found =
                    inboundProductRepository.findById(saved.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getStatus()).isEqualTo("MAPPED");
            assertThat(found.get().getInternalBrandId())
                    .isEqualTo(InboundProductJpaEntityFixtures.DEFAULT_INTERNAL_BRAND_ID);
            assertThat(found.get().getInternalCategoryId())
                    .isEqualTo(InboundProductJpaEntityFixtures.DEFAULT_INTERNAL_CATEGORY_ID);
            assertThat(found.get().getResolvedShippingPolicyId())
                    .isEqualTo(InboundProductJpaEntityFixtures.DEFAULT_RESOLVED_SHIPPING_POLICY_ID);
        }

        @Test
        @Tag("P0")
        @DisplayName("[R2-S04] CONVERTED 상태의 레코드를 저장합니다")
        void save_ConvertedEntity_PersistsInternalProductGroupId() {
            // given
            Instant now = Instant.now();
            InboundProductJpaEntity entity =
                    InboundProductJpaEntity.create(
                            null,
                            savedSourceId,
                            "EXT-PROD-CONVERTED",
                            "EXT-BRAND-001",
                            "EXT-CAT-001",
                            InboundProductJpaEntityFixtures.DEFAULT_INTERNAL_BRAND_ID,
                            InboundProductJpaEntityFixtures.DEFAULT_INTERNAL_CATEGORY_ID,
                            InboundProductJpaEntityFixtures.DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                            InboundProductJpaEntityFixtures.DEFAULT_SELLER_ID,
                            "CONVERTED",
                            null,
                            InboundProductJpaEntityFixtures.DEFAULT_RESOLVED_SHIPPING_POLICY_ID,
                            InboundProductJpaEntityFixtures.DEFAULT_RESOLVED_REFUND_POLICY_ID,
                            InboundProductJpaEntityFixtures.DEFAULT_RESOLVED_NOTICE_CATEGORY_ID,
                            now,
                            now);

            // when
            InboundProductJpaEntity saved = inboundProductRepository.save(entity);

            // then
            Optional<InboundProductJpaEntity> found =
                    inboundProductRepository.findById(saved.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getStatus()).isEqualTo("CONVERTED");
            assertThat(found.get().getInternalProductGroupId())
                    .isEqualTo(InboundProductJpaEntityFixtures.DEFAULT_INTERNAL_PRODUCT_GROUP_ID);
        }
    }
}
