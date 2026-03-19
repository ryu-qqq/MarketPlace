package com.ryuqq.marketplace.adapter.out.persistence.settlement.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.SettlementEntryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.entry.adapter.SettlementEntryQueryAdapter;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.entry.entity.SettlementEntryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.entry.mapper.SettlementEntryJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.entry.repository.SettlementEntryQueryDslRepository;
import com.ryuqq.marketplace.domain.settlement.entry.SettlementEntryFixtures;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import com.ryuqq.marketplace.domain.settlement.entry.id.SettlementEntryId;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryStatus;
import java.time.Instant;
import java.util.List;
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
 * SettlementEntryQueryAdapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SettlementEntryQueryAdapter 단위 테스트")
class SettlementEntryQueryAdapterTest {

    @Mock private SettlementEntryQueryDslRepository repository;
    @Mock private SettlementEntryJpaEntityMapper mapper;

    @InjectMocks private SettlementEntryQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 SettlementEntry 도메인을 반환합니다")
        void findById_WithExistingId_ReturnsSettlementEntry() {
            // given
            String id = SettlementEntryJpaEntityFixtures.DEFAULT_ID;
            SettlementEntryId entryId = SettlementEntryId.of(id);
            SettlementEntryJpaEntity entity =
                    SettlementEntryJpaEntityFixtures.salesPendingEntity(id);
            SettlementEntry domain = SettlementEntryFixtures.salesEntry();

            given(repository.findById(id)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SettlementEntry> result = queryAdapter.findById(entryId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            String id = "01900000-9999-7000-9000-000000000000";
            SettlementEntryId entryId = SettlementEntryId.of(id);

            given(repository.findById(id)).willReturn(Optional.empty());

            // when
            Optional<SettlementEntry> result = queryAdapter.findById(entryId);

            // then
            assertThat(result).isEmpty();
            then(repository).should().findById(id);
        }

        @Test
        @DisplayName("findById 호출 시 repository에 ID 값이 전달됩니다")
        void findById_DelegatesToRepositoryWithIdValue() {
            // given
            String id = SettlementEntryJpaEntityFixtures.DEFAULT_ID;
            SettlementEntryId entryId = SettlementEntryId.of(id);

            given(repository.findById(id)).willReturn(Optional.empty());

            // when
            queryAdapter.findById(entryId);

            // then
            then(repository).should().findById(id);
        }
    }

    // ========================================================================
    // 2. findConfirmableEntries 테스트
    // ========================================================================

    @Nested
    @DisplayName("findConfirmableEntries 메서드 테스트")
    class FindConfirmableEntriesTest {

        @Test
        @DisplayName("cutoffTime과 limit으로 확정 가능한 Entry 목록을 반환합니다")
        void findConfirmableEntries_WithValidCutoffTimeAndLimit_ReturnsEntryList() {
            // given
            Instant cutoffTime = Instant.now();
            int limit = 100;

            SettlementEntryJpaEntity entity1 =
                    SettlementEntryJpaEntityFixtures.eligiblePendingEntity("id-001");
            SettlementEntryJpaEntity entity2 =
                    SettlementEntryJpaEntityFixtures.eligiblePendingEntity("id-002");
            SettlementEntry domain1 = SettlementEntryFixtures.salesEntry();
            SettlementEntry domain2 = SettlementEntryFixtures.salesEntry();

            given(repository.findConfirmableEntries(cutoffTime, limit))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<SettlementEntry> result = queryAdapter.findConfirmableEntries(cutoffTime, limit);

            // then
            assertThat(result).hasSize(2);
            then(repository).should().findConfirmableEntries(cutoffTime, limit);
        }

        @Test
        @DisplayName("확정 가능한 Entry가 없으면 빈 리스트를 반환합니다")
        void findConfirmableEntries_WithNoEligibleEntries_ReturnsEmptyList() {
            // given
            Instant cutoffTime = Instant.now();
            int limit = 100;

            given(repository.findConfirmableEntries(cutoffTime, limit)).willReturn(List.of());

            // when
            List<SettlementEntry> result = queryAdapter.findConfirmableEntries(cutoffTime, limit);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("cutoffTime과 limit이 repository에 그대로 전달됩니다")
        void findConfirmableEntries_DelegatesToRepositoryWithCorrectParams() {
            // given
            Instant cutoffTime = Instant.parse("2026-03-19T00:00:00Z");
            int limit = 50;

            given(repository.findConfirmableEntries(cutoffTime, limit)).willReturn(List.of());

            // when
            queryAdapter.findConfirmableEntries(cutoffTime, limit);

            // then
            then(repository).should().findConfirmableEntries(cutoffTime, limit);
        }
    }

    // ========================================================================
    // 3. findBySellerIdAndStatus 테스트
    // ========================================================================

    @Nested
    @DisplayName("findBySellerIdAndStatus 메서드 테스트")
    class FindBySellerIdAndStatusTest {

        @Test
        @DisplayName("sellerId와 상태로 조회 시 SettlementEntry 목록을 반환합니다")
        void findBySellerIdAndStatus_WithExistingSellerAndStatus_ReturnsEntryList() {
            // given
            long sellerId = SettlementEntryJpaEntityFixtures.DEFAULT_SELLER_ID;
            EntryStatus status = EntryStatus.PENDING;

            SettlementEntryJpaEntity entity1 =
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("id-001");
            SettlementEntryJpaEntity entity2 =
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("id-002");
            SettlementEntry domain1 = SettlementEntryFixtures.salesEntry();
            SettlementEntry domain2 = SettlementEntryFixtures.salesEntry();

            given(repository.findBySellerIdAndStatus(sellerId, status.name()))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<SettlementEntry> result = queryAdapter.findBySellerIdAndStatus(sellerId, status);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("해당하는 Entry가 없으면 빈 리스트를 반환합니다")
        void findBySellerIdAndStatus_WithNoResults_ReturnsEmptyList() {
            // given
            long sellerId = 999L;
            EntryStatus status = EntryStatus.CONFIRMED;

            given(repository.findBySellerIdAndStatus(sellerId, status.name()))
                    .willReturn(List.of());

            // when
            List<SettlementEntry> result = queryAdapter.findBySellerIdAndStatus(sellerId, status);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("status.name()이 repository에 문자열로 전달됩니다")
        void findBySellerIdAndStatus_PassesStatusNameToRepository() {
            // given
            long sellerId = SettlementEntryJpaEntityFixtures.DEFAULT_SELLER_ID;
            EntryStatus status = EntryStatus.PENDING;

            given(repository.findBySellerIdAndStatus(sellerId, "PENDING")).willReturn(List.of());

            // when
            queryAdapter.findBySellerIdAndStatus(sellerId, status);

            // then
            then(repository).should().findBySellerIdAndStatus(sellerId, "PENDING");
        }
    }

    // ========================================================================
    // 4. findByOrderItemId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByOrderItemId 메서드 테스트")
    class FindByOrderItemIdTest {

        @Test
        @DisplayName("orderItemId로 조회 시 SettlementEntry 목록을 반환합니다")
        void findByOrderItemId_WithExistingOrderItemId_ReturnsEntryList() {
            // given
            String orderItemId = SettlementEntryJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID;

            SettlementEntryJpaEntity entity =
                    SettlementEntryJpaEntityFixtures.pendingEntityWithOrderItemId(
                            "id-001", orderItemId);
            SettlementEntry domain = SettlementEntryFixtures.salesEntry();

            given(repository.findByOrderItemId(orderItemId)).willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<SettlementEntry> result = queryAdapter.findByOrderItemId(orderItemId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 orderItemId로 조회 시 빈 리스트를 반환합니다")
        void findByOrderItemId_WithNonExistingOrderItemId_ReturnsEmptyList() {
            // given
            String orderItemId = "oi-not-exist-999";

            given(repository.findByOrderItemId(orderItemId)).willReturn(List.of());

            // when
            List<SettlementEntry> result = queryAdapter.findByOrderItemId(orderItemId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("같은 orderItemId로 여러 Entry(매출+역분개)가 조회될 수 있습니다")
        void findByOrderItemId_WithMultipleEntries_ReturnsAllEntries() {
            // given
            String orderItemId = SettlementEntryJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID;

            SettlementEntryJpaEntity salesEntity =
                    SettlementEntryJpaEntityFixtures.pendingEntityWithOrderItemId(
                            "id-sales", orderItemId);
            SettlementEntryJpaEntity cancelEntity =
                    SettlementEntryJpaEntityFixtures.pendingEntityWithOrderItemId(
                            "id-cancel", orderItemId);
            SettlementEntry salesDomain = SettlementEntryFixtures.salesEntry();
            SettlementEntry cancelDomain = SettlementEntryFixtures.cancelReversalEntry();

            given(repository.findByOrderItemId(orderItemId))
                    .willReturn(List.of(salesEntity, cancelEntity));
            given(mapper.toDomain(salesEntity)).willReturn(salesDomain);
            given(mapper.toDomain(cancelEntity)).willReturn(cancelDomain);

            // when
            List<SettlementEntry> result = queryAdapter.findByOrderItemId(orderItemId);

            // then
            assertThat(result).hasSize(2);
            then(repository).should().findByOrderItemId(orderItemId);
        }

        @Test
        @DisplayName("findByOrderItemId 호출 시 repository에 orderItemId가 전달됩니다")
        void findByOrderItemId_DelegatesToRepositoryWithOrderItemId() {
            // given
            String orderItemId = "oi-delegate-test";

            given(repository.findByOrderItemId(orderItemId)).willReturn(List.of());

            // when
            queryAdapter.findByOrderItemId(orderItemId);

            // then
            then(repository).should().findByOrderItemId(orderItemId);
        }
    }
}
