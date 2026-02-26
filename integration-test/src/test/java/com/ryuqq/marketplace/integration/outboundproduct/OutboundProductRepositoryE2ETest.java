package com.ryuqq.marketplace.integration.outboundproduct;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.OutboundProductJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.entity.OutboundProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.repository.OutboundProductJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
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
 * OutboundProduct Repository 통합 테스트.
 *
 * <p>OutboundProductJpaRepository의 쿼리 동작을 검증합니다.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>existsByProductGroupIdAndSalesChannelId - 복합 키 존재 여부 조회
 *   <li>findByProductGroupIdAndSalesChannelId - 복합 키 단건 조회
 *   <li>save / saveAll - 저장 기본 동작
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("outboundproduct")
@DisplayName("OutboundProduct Repository 통합 테스트")
class OutboundProductRepositoryE2ETest extends E2ETestBase {

    @Autowired private OutboundProductJpaRepository outboundProductRepository;

    @BeforeEach
    void setUp() {
        outboundProductRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        outboundProductRepository.deleteAll();
    }

    // ========================================================================
    // 1. existsByProductGroupIdAndSalesChannelId 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsByProductGroupIdAndSalesChannelId 쿼리 테스트")
    class ExistsByProductGroupIdAndSalesChannelIdTest {

        @Test
        @Tag("P0")
        @DisplayName("[R1-S01] 저장된 조합으로 존재 확인 시 true를 반환합니다")
        void existsByProductGroupIdAndSalesChannelId_ExistingRecord_ReturnsTrue() {
            // given
            OutboundProductJpaEntity entity =
                    OutboundProductJpaEntityFixtures.pendingEntityWith(100L, 10L);
            outboundProductRepository.save(entity);

            // when
            boolean result =
                    outboundProductRepository.existsByProductGroupIdAndSalesChannelId(100L, 10L);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @Tag("P0")
        @DisplayName("[R1-F01] 존재하지 않는 조합으로 존재 확인 시 false를 반환합니다")
        void existsByProductGroupIdAndSalesChannelId_NonExistingRecord_ReturnsFalse() {
            // given - 데이터 없음

            // when
            boolean result =
                    outboundProductRepository.existsByProductGroupIdAndSalesChannelId(9999L, 8888L);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @Tag("P0")
        @DisplayName("[R1-F02] productGroupId만 다른 경우 false를 반환합니다")
        void existsByProductGroupIdAndSalesChannelId_DifferentProductGroupId_ReturnsFalse() {
            // given
            OutboundProductJpaEntity entity =
                    OutboundProductJpaEntityFixtures.pendingEntityWith(100L, 10L);
            outboundProductRepository.save(entity);

            // when
            boolean result =
                    outboundProductRepository.existsByProductGroupIdAndSalesChannelId(200L, 10L);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @Tag("P0")
        @DisplayName("[R1-F03] salesChannelId만 다른 경우 false를 반환합니다")
        void existsByProductGroupIdAndSalesChannelId_DifferentSalesChannelId_ReturnsFalse() {
            // given
            OutboundProductJpaEntity entity =
                    OutboundProductJpaEntityFixtures.pendingEntityWith(100L, 10L);
            outboundProductRepository.save(entity);

            // when
            boolean result =
                    outboundProductRepository.existsByProductGroupIdAndSalesChannelId(100L, 20L);

            // then
            assertThat(result).isFalse();
        }
    }

    // ========================================================================
    // 2. findByProductGroupIdAndSalesChannelId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupIdAndSalesChannelId 쿼리 테스트")
    class FindByProductGroupIdAndSalesChannelIdTest {

        @Test
        @Tag("P0")
        @DisplayName("[R2-S01] 저장된 조합으로 조회 시 엔티티를 반환합니다")
        void findByProductGroupIdAndSalesChannelId_ExistingRecord_ReturnsEntity() {
            // given
            OutboundProductJpaEntity entity =
                    OutboundProductJpaEntityFixtures.registeredEntityWith(100L, 10L);
            outboundProductRepository.save(entity);

            // when
            Optional<OutboundProductJpaEntity> result =
                    outboundProductRepository.findByProductGroupIdAndSalesChannelId(100L, 10L);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getProductGroupId()).isEqualTo(100L);
            assertThat(result.get().getSalesChannelId()).isEqualTo(10L);
            assertThat(result.get().getStatus())
                    .isEqualTo(OutboundProductJpaEntityFixtures.STATUS_REGISTERED);
        }

        @Test
        @Tag("P0")
        @DisplayName("[R2-F01] 존재하지 않는 조합으로 조회 시 빈 Optional을 반환합니다")
        void findByProductGroupIdAndSalesChannelId_NonExistingRecord_ReturnsEmpty() {
            // given - 데이터 없음

            // when
            Optional<OutboundProductJpaEntity> result =
                    outboundProductRepository.findByProductGroupIdAndSalesChannelId(9999L, 8888L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @Tag("P0")
        @DisplayName("[R2-S02] 여러 레코드 중 특정 조합만 조회됩니다")
        void findByProductGroupIdAndSalesChannelId_MultipleRecords_ReturnsCorrectOne() {
            // given
            outboundProductRepository.saveAll(
                    List.of(
                            OutboundProductJpaEntityFixtures.registeredEntityWith(100L, 10L),
                            OutboundProductJpaEntityFixtures.registeredEntityWith(200L, 10L),
                            OutboundProductJpaEntityFixtures.registeredEntityWith(100L, 20L)));

            // when
            Optional<OutboundProductJpaEntity> result =
                    outboundProductRepository.findByProductGroupIdAndSalesChannelId(200L, 10L);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getProductGroupId()).isEqualTo(200L);
            assertThat(result.get().getSalesChannelId()).isEqualTo(10L);
        }
    }

    // ========================================================================
    // 3. save / saveAll 기본 동작 테스트
    // ========================================================================

    @Nested
    @DisplayName("save / saveAll 기본 동작 테스트")
    class SaveTest {

        @Test
        @Tag("P0")
        @DisplayName("[R3-S01] 단건 저장 후 ID가 채번됩니다")
        void save_SingleEntity_AssignsId() {
            // given
            OutboundProductJpaEntity entity = OutboundProductJpaEntityFixtures.pendingEntity();

            // when
            OutboundProductJpaEntity saved = outboundProductRepository.save(entity);

            // then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getStatus())
                    .isEqualTo(OutboundProductJpaEntityFixtures.STATUS_PENDING);
        }

        @Test
        @Tag("P0")
        @DisplayName("[R3-S02] REGISTERED 상태 엔티티 저장 후 externalProductId가 보존됩니다")
        void save_RegisteredEntity_PreservesExternalProductId() {
            // given
            OutboundProductJpaEntity entity = OutboundProductJpaEntityFixtures.registeredEntity();

            // when
            OutboundProductJpaEntity saved = outboundProductRepository.save(entity);

            // then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getExternalProductId()).isNotNull();
            assertThat(saved.getStatus())
                    .isEqualTo(OutboundProductJpaEntityFixtures.STATUS_REGISTERED);
        }

        @Test
        @Tag("P0")
        @DisplayName("[R3-S03] 여러 건 저장 후 모두 조회됩니다")
        void saveAll_MultipleEntities_AllPersisted() {
            // given
            List<OutboundProductJpaEntity> entities =
                    List.of(
                            OutboundProductJpaEntityFixtures.pendingEntityWith(101L, 10L),
                            OutboundProductJpaEntityFixtures.pendingEntityWith(102L, 10L),
                            OutboundProductJpaEntityFixtures.pendingEntityWith(103L, 10L));

            // when
            List<OutboundProductJpaEntity> saved = outboundProductRepository.saveAll(entities);

            // then
            assertThat(saved).hasSize(3);
            assertThat(saved).allMatch(e -> e.getId() != null);
            assertThat(outboundProductRepository.count()).isEqualTo(3);
        }

        @Test
        @Tag("P0")
        @DisplayName("[R3-S04] externalProductId가 null인 PENDING 상태 엔티티를 저장합니다")
        void save_PendingEntityWithNullExternalId_Persists() {
            // given
            OutboundProductJpaEntity entity =
                    OutboundProductJpaEntityFixtures.pendingEntityWith(200L, 20L);

            // when
            OutboundProductJpaEntity saved = outboundProductRepository.save(entity);

            // then
            Optional<OutboundProductJpaEntity> found =
                    outboundProductRepository.findById(saved.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getExternalProductId()).isNull();
            assertThat(found.get().getStatus())
                    .isEqualTo(OutboundProductJpaEntityFixtures.STATUS_PENDING);
        }
    }
}
