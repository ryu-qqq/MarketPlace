package com.ryuqq.marketplace.adapter.out.persistence.settlement.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.SettlementJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.entity.SettlementJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.mapper.SettlementJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.repository.SettlementQueryDslRepository;
import com.ryuqq.marketplace.domain.settlement.SettlementFixtures;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementStatus;
import java.time.LocalDate;
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
 * SettlementQueryAdapter 단위 테스트.
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
@DisplayName("SettlementQueryAdapter 단위 테스트")
class SettlementQueryAdapterTest {

    @Mock private SettlementQueryDslRepository repository;
    @Mock private SettlementJpaEntityMapper mapper;

    @InjectMocks private SettlementQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Settlement 도메인을 반환합니다")
        void findById_WithExistingId_ReturnsSettlement() {
            // given
            String id = SettlementJpaEntityFixtures.DEFAULT_ID;
            SettlementId settlementId = SettlementId.of(id);
            SettlementJpaEntity entity = SettlementJpaEntityFixtures.calculatingEntity(id);
            Settlement domain = SettlementFixtures.calculatingSettlement();

            given(repository.findById(id)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<Settlement> result = queryAdapter.findById(settlementId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            String id = "01900000-9999-7000-8000-000000000000";
            SettlementId settlementId = SettlementId.of(id);

            given(repository.findById(id)).willReturn(Optional.empty());

            // when
            Optional<Settlement> result = queryAdapter.findById(settlementId);

            // then
            assertThat(result).isEmpty();
            then(repository).should().findById(id);
        }

        @Test
        @DisplayName("findById 호출 시 repository에 ID 값이 전달됩니다")
        void findById_DelegatesToRepositoryWithIdValue() {
            // given
            String id = SettlementJpaEntityFixtures.DEFAULT_ID;
            SettlementId settlementId = SettlementId.of(id);

            given(repository.findById(id)).willReturn(Optional.empty());

            // when
            queryAdapter.findById(settlementId);

            // then
            then(repository).should().findById(id);
        }
    }

    // ========================================================================
    // 2. findBySellerIdAndPeriod 테스트
    // ========================================================================

    @Nested
    @DisplayName("findBySellerIdAndPeriod 메서드 테스트")
    class FindBySellerIdAndPeriodTest {

        @Test
        @DisplayName("sellerId와 기간으로 조회 시 Settlement 도메인을 반환합니다")
        void findBySellerIdAndPeriod_WithExistingSellerAndPeriod_ReturnsSettlement() {
            // given
            long sellerId = SettlementJpaEntityFixtures.DEFAULT_SELLER_ID;
            LocalDate startDate = LocalDate.now().minusDays(7);
            LocalDate endDate = LocalDate.now();
            SettlementJpaEntity entity = SettlementJpaEntityFixtures.calculatingEntity();
            Settlement domain = SettlementFixtures.calculatingSettlement();

            given(repository.findBySellerIdAndPeriod(sellerId, startDate, endDate))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<Settlement> result =
                    queryAdapter.findBySellerIdAndPeriod(sellerId, startDate, endDate);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("해당하는 정산이 없으면 빈 Optional을 반환합니다")
        void findBySellerIdAndPeriod_WithNonExistingPeriod_ReturnsEmpty() {
            // given
            long sellerId = 999L;
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 1, 7);

            given(repository.findBySellerIdAndPeriod(sellerId, startDate, endDate))
                    .willReturn(Optional.empty());

            // when
            Optional<Settlement> result =
                    queryAdapter.findBySellerIdAndPeriod(sellerId, startDate, endDate);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("findBySellerIdAndPeriod 호출 시 파라미터가 repository에 그대로 전달됩니다")
        void findBySellerIdAndPeriod_DelegatesToRepositoryWithCorrectParams() {
            // given
            long sellerId = SettlementJpaEntityFixtures.DEFAULT_SELLER_ID;
            LocalDate startDate = LocalDate.now().minusDays(7);
            LocalDate endDate = LocalDate.now();

            given(repository.findBySellerIdAndPeriod(sellerId, startDate, endDate))
                    .willReturn(Optional.empty());

            // when
            queryAdapter.findBySellerIdAndPeriod(sellerId, startDate, endDate);

            // then
            then(repository).should().findBySellerIdAndPeriod(sellerId, startDate, endDate);
        }
    }

    // ========================================================================
    // 3. findBySellerIdAndStatus 테스트
    // ========================================================================

    @Nested
    @DisplayName("findBySellerIdAndStatus 메서드 테스트")
    class FindBySellerIdAndStatusTest {

        @Test
        @DisplayName("sellerId와 상태로 조회 시 Settlement 목록을 반환합니다")
        void findBySellerIdAndStatus_WithExistingSellerAndStatus_ReturnsSettlementList() {
            // given
            long sellerId = SettlementJpaEntityFixtures.DEFAULT_SELLER_ID;
            SettlementStatus status = SettlementStatus.CALCULATING;
            SettlementJpaEntity entity1 = SettlementJpaEntityFixtures.calculatingEntity("id-001");
            SettlementJpaEntity entity2 = SettlementJpaEntityFixtures.calculatingEntity("id-002");
            Settlement domain1 = SettlementFixtures.calculatingSettlement();
            Settlement domain2 = SettlementFixtures.calculatingSettlement();

            given(repository.findBySellerIdAndStatus(sellerId, status.name()))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<Settlement> result = queryAdapter.findBySellerIdAndStatus(sellerId, status);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("해당하는 정산이 없으면 빈 리스트를 반환합니다")
        void findBySellerIdAndStatus_WithNoResults_ReturnsEmptyList() {
            // given
            long sellerId = 999L;
            SettlementStatus status = SettlementStatus.CONFIRMED;

            given(repository.findBySellerIdAndStatus(sellerId, status.name()))
                    .willReturn(List.of());

            // when
            List<Settlement> result = queryAdapter.findBySellerIdAndStatus(sellerId, status);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("status.name()이 repository에 문자열로 전달됩니다")
        void findBySellerIdAndStatus_PassesStatusNameToRepository() {
            // given
            long sellerId = SettlementJpaEntityFixtures.DEFAULT_SELLER_ID;
            SettlementStatus status = SettlementStatus.CALCULATING;

            given(repository.findBySellerIdAndStatus(sellerId, "CALCULATING"))
                    .willReturn(List.of());

            // when
            queryAdapter.findBySellerIdAndStatus(sellerId, status);

            // then
            then(repository).should().findBySellerIdAndStatus(sellerId, "CALCULATING");
        }
    }

    // ========================================================================
    // 4. findByStatus 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByStatus 메서드 테스트")
    class FindByStatusTest {

        @Test
        @DisplayName("상태로 전체 조회 시 해당 상태의 Settlement 목록을 반환합니다")
        void findByStatus_WithExistingStatus_ReturnsSettlementList() {
            // given
            SettlementStatus status = SettlementStatus.CONFIRMED;
            SettlementJpaEntity entity1 =
                    SettlementJpaEntityFixtures.entityWithStatus("id-001", "CONFIRMED");
            SettlementJpaEntity entity2 =
                    SettlementJpaEntityFixtures.entityWithStatus("id-002", "CONFIRMED");
            Settlement domain1 = SettlementFixtures.confirmedSettlement();
            Settlement domain2 = SettlementFixtures.confirmedSettlement();

            given(repository.findByStatus(status.name())).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<Settlement> result = queryAdapter.findByStatus(status);

            // then
            assertThat(result).hasSize(2);
            then(repository).should().findByStatus("CONFIRMED");
        }

        @Test
        @DisplayName("해당 상태의 정산이 없으면 빈 리스트를 반환합니다")
        void findByStatus_WithNoResults_ReturnsEmptyList() {
            // given
            SettlementStatus status = SettlementStatus.COMPLETED;

            given(repository.findByStatus(status.name())).willReturn(List.of());

            // when
            List<Settlement> result = queryAdapter.findByStatus(status);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("findByStatus 호출 시 status.name()이 repository에 전달됩니다")
        void findByStatus_DelegatesToRepositoryWithStatusName() {
            // given
            SettlementStatus status = SettlementStatus.HOLD;

            given(repository.findByStatus("HOLD")).willReturn(List.of());

            // when
            queryAdapter.findByStatus(status);

            // then
            then(repository).should().findByStatus("HOLD");
        }
    }
}
