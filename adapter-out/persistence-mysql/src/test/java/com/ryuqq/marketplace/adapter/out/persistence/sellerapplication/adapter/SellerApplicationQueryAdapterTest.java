package com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.SellerApplicationJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.entity.SellerApplicationJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.mapper.SellerApplicationJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.repository.SellerApplicationQueryDslRepository;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import com.ryuqq.marketplace.domain.sellerapplication.id.SellerApplicationId;
import com.ryuqq.marketplace.domain.sellerapplication.query.SellerApplicationSearchCriteria;
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
 * SellerApplicationQueryAdapterTest - 입점 신청 Query Adapter 단위 테스트.
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
@DisplayName("SellerApplicationQueryAdapter 단위 테스트")
class SellerApplicationQueryAdapterTest {

    @Mock private SellerApplicationQueryDslRepository queryDslRepository;

    @Mock private SellerApplicationJpaEntityMapper mapper;

    @Mock private SellerApplicationSearchCriteria criteria;

    @InjectMocks private SellerApplicationQueryAdapter sut;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Domain을 반환합니다")
        void findById_WithExistingId_ReturnsDomain() {
            // given
            SellerApplicationId applicationId = SellerApplicationId.of(1L);
            SellerApplicationJpaEntity entity =
                    SellerApplicationJpaEntityFixtures.pendingEntity(1L, "123-45-67890");
            SellerApplication domain = createPendingApplication(1L);

            given(queryDslRepository.findById(1L)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SellerApplication> result = sut.findById(applicationId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            SellerApplicationId applicationId = SellerApplicationId.of(999L);
            given(queryDslRepository.findById(999L)).willReturn(Optional.empty());

            // when
            Optional<SellerApplication> result = sut.findById(applicationId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. existsById 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsById 메서드 테스트")
    class ExistsByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 true를 반환합니다")
        void existsById_WithExistingId_ReturnsTrue() {
            // given
            SellerApplicationId applicationId = SellerApplicationId.of(1L);
            given(queryDslRepository.existsById(1L)).willReturn(true);

            // when
            boolean result = sut.existsById(applicationId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 false를 반환합니다")
        void existsById_WithNonExistingId_ReturnsFalse() {
            // given
            SellerApplicationId applicationId = SellerApplicationId.of(999L);
            given(queryDslRepository.existsById(999L)).willReturn(false);

            // when
            boolean result = sut.existsById(applicationId);

            // then
            assertThat(result).isFalse();
        }
    }

    // ========================================================================
    // 3. existsPendingByRegistrationNumber 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsPendingByRegistrationNumber 메서드 테스트")
    class ExistsPendingByRegistrationNumberTest {

        @Test
        @DisplayName("대기 중인 신청이 존재하면 true를 반환합니다")
        void existsPendingByRegistrationNumber_WithExistingPending_ReturnsTrue() {
            // given
            String registrationNumber = "123-45-67890";
            given(queryDslRepository.existsPendingByRegistrationNumber(registrationNumber))
                    .willReturn(true);

            // when
            boolean result = sut.existsPendingByRegistrationNumber(registrationNumber);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("대기 중인 신청이 없으면 false를 반환합니다")
        void existsPendingByRegistrationNumber_WithNoPending_ReturnsFalse() {
            // given
            String registrationNumber = "999-99-99999";
            given(queryDslRepository.existsPendingByRegistrationNumber(registrationNumber))
                    .willReturn(false);

            // when
            boolean result = sut.existsPendingByRegistrationNumber(registrationNumber);

            // then
            assertThat(result).isFalse();
        }
    }

    // ========================================================================
    // 4. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 입점 신청 목록을 조회합니다")
        void findByCriteria_WithValidCriteria_ReturnsDomainList() {
            // given
            SellerApplicationJpaEntity entity1 =
                    SellerApplicationJpaEntityFixtures.pendingEntity(1L, "123-45-67890");
            SellerApplicationJpaEntity entity2 =
                    SellerApplicationJpaEntityFixtures.pendingEntity(2L, "987-65-43210");
            SellerApplication domain1 = createPendingApplication(1L);
            SellerApplication domain2 = createPendingApplication(2L);

            given(queryDslRepository.findByCriteria(criteria))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<SellerApplication> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
            then(queryDslRepository).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            given(queryDslRepository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<SellerApplication> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 5. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 입점 신청 개수를 반환합니다")
        void countByCriteria_WithValidCriteria_ReturnsCount() {
            // given
            given(queryDslRepository.countByCriteria(criteria)).willReturn(5L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            given(queryDslRepository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    // ========================================================================
    // 6. findByApprovedSellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByApprovedSellerId 메서드 테스트")
    class FindByApprovedSellerIdTest {

        @Test
        @DisplayName("승인된 셀러 ID로 조회 시 Domain을 반환합니다")
        void findByApprovedSellerId_WithExistingId_ReturnsDomain() {
            // given
            SellerId sellerId = SellerId.of(1L);
            SellerApplicationJpaEntity entity =
                    SellerApplicationJpaEntityFixtures.approvedEntity(1L);
            SellerApplication domain = createPendingApplication(1L);

            given(queryDslRepository.findByApprovedSellerId(1L)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SellerApplication> result = sut.findByApprovedSellerId(sellerId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 셀러 ID로 조회 시 빈 Optional을 반환합니다")
        void findByApprovedSellerId_WithNonExistingId_ReturnsEmpty() {
            // given
            SellerId sellerId = SellerId.of(999L);
            given(queryDslRepository.findByApprovedSellerId(999L)).willReturn(Optional.empty());

            // when
            Optional<SellerApplication> result = sut.findByApprovedSellerId(sellerId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("repository에 sellerId의 value를 그대로 전달합니다")
        void findByApprovedSellerId_DelegatesToRepository() {
            // given
            SellerId sellerId = SellerId.of(42L);
            given(queryDslRepository.findByApprovedSellerId(42L)).willReturn(Optional.empty());

            // when
            sut.findByApprovedSellerId(sellerId);

            // then
            then(queryDslRepository).should().findByApprovedSellerId(42L);
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private SellerApplication createPendingApplication(Long id) {
        java.time.Instant now = java.time.Instant.now();
        return SellerApplication.reconstitute(
                SellerApplicationId.of(id),
                com.ryuqq.marketplace.domain.seller.vo.SellerName.of("테스트셀러"),
                com.ryuqq.marketplace.domain.seller.vo.DisplayName.of("테스트 브랜드"),
                com.ryuqq.marketplace.domain.seller.vo.LogoUrl.of("https://example.com/logo.png"),
                com.ryuqq.marketplace.domain.seller.vo.Description.of("테스트 셀러 설명입니다."),
                com.ryuqq.marketplace.domain.seller.vo.RegistrationNumber.of("123-45-67890"),
                com.ryuqq.marketplace.domain.seller.vo.CompanyName.of("테스트컴퍼니"),
                com.ryuqq.marketplace.domain.seller.vo.Representative.of("홍길동"),
                com.ryuqq.marketplace.domain.seller.vo.SaleReportNumber.of("제2025-서울강남-1234호"),
                com.ryuqq.marketplace.domain.common.vo.Address.of("12345", "서울시 강남구", "테헤란로 123"),
                com.ryuqq.marketplace.domain.seller.vo.CsContact.of(
                        "02-1234-5678", null, "cs@example.com"),
                com.ryuqq.marketplace.domain.seller.vo.ContactInfo.of(
                        "김담당", "010-9876-5432", "contact@example.com"),
                com.ryuqq.marketplace.domain.seller.vo.BankAccount.of(
                        "088", "신한은행", "110123456789", "홍길동"),
                com.ryuqq.marketplace.domain.seller.vo.SettlementCycle.MONTHLY,
                1,
                com.ryuqq.marketplace.domain.sellerapplication.vo.Agreement.reconstitute(now),
                com.ryuqq.marketplace.domain.sellerapplication.vo.ApplicationStatus.PENDING,
                now,
                null,
                null,
                null,
                null);
    }
}
