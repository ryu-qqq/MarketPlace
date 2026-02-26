package com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.SellerApplicationJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.entity.SellerApplicationJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.mapper.SellerApplicationJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.repository.SellerApplicationJpaRepository;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.BankAccount;
import com.ryuqq.marketplace.domain.seller.vo.CompanyName;
import com.ryuqq.marketplace.domain.seller.vo.ContactInfo;
import com.ryuqq.marketplace.domain.seller.vo.CsContact;
import com.ryuqq.marketplace.domain.seller.vo.Description;
import com.ryuqq.marketplace.domain.seller.vo.LogoUrl;
import com.ryuqq.marketplace.domain.seller.vo.RegistrationNumber;
import com.ryuqq.marketplace.domain.seller.vo.Representative;
import com.ryuqq.marketplace.domain.seller.vo.SaleReportNumber;
import com.ryuqq.marketplace.domain.seller.vo.SellerName;
import com.ryuqq.marketplace.domain.seller.vo.SettlementCycle;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import com.ryuqq.marketplace.domain.sellerapplication.id.SellerApplicationId;
import com.ryuqq.marketplace.domain.sellerapplication.vo.Agreement;
import com.ryuqq.marketplace.domain.sellerapplication.vo.ApplicationStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SellerApplicationCommandAdapterTest - 입점 신청 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-005: Domain -> Entity 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SellerApplicationCommandAdapter 단위 테스트")
class SellerApplicationCommandAdapterTest {

    @Mock private SellerApplicationJpaRepository jpaRepository;

    @Mock private SellerApplicationJpaEntityMapper mapper;

    @InjectMocks private SellerApplicationCommandAdapter sut;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("Domain을 Entity로 변환 후 저장하고 ID를 반환합니다")
        void persist_WithValidDomain_SavesAndReturnsId() {
            // given
            SellerApplication domain = createPendingApplication();
            SellerApplicationJpaEntity entityToSave =
                    SellerApplicationJpaEntityFixtures.pendingEntity();
            SellerApplicationJpaEntity savedEntity =
                    SellerApplicationJpaEntityFixtures.pendingEntity(100L, "123-45-67890");

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = sut.persist(domain);

            // then
            assertThat(savedId).isEqualTo(100L);
            then(mapper).should().toEntity(domain);
            then(jpaRepository).should().save(entityToSave);
        }

        @Test
        @DisplayName("대기 상태 입점 신청을 저장합니다")
        void persist_WithPendingApplication_Saves() {
            // given
            SellerApplication domain = createPendingApplication();
            SellerApplicationJpaEntity entityToSave =
                    SellerApplicationJpaEntityFixtures.pendingEntity();
            SellerApplicationJpaEntity savedEntity =
                    SellerApplicationJpaEntityFixtures.pendingEntity(1L, "123-45-67890");

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = sut.persist(domain);

            // then
            assertThat(savedId).isEqualTo(1L);
        }

        @Test
        @DisplayName("승인 상태 입점 신청을 저장합니다")
        void persist_WithApprovedApplication_Saves() {
            // given
            SellerApplication domain = createApprovedApplication(1L);
            SellerApplicationJpaEntity entityToSave =
                    SellerApplicationJpaEntityFixtures.approvedEntity(1L);
            SellerApplicationJpaEntity savedEntity =
                    SellerApplicationJpaEntityFixtures.pendingEntity(2L, "123-45-67890");

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = sut.persist(domain);

            // then
            assertThat(savedId).isEqualTo(2L);
        }

        @Test
        @DisplayName("거절 상태 입점 신청을 저장합니다")
        void persist_WithRejectedApplication_Saves() {
            // given
            SellerApplication domain = createRejectedApplication("서류 미비");
            SellerApplicationJpaEntity entityToSave =
                    SellerApplicationJpaEntityFixtures.rejectedEntity("서류 미비");
            SellerApplicationJpaEntity savedEntity =
                    SellerApplicationJpaEntityFixtures.pendingEntity(3L, "123-45-67890");

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = sut.persist(domain);

            // then
            assertThat(savedId).isEqualTo(3L);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            SellerApplication domain = createPendingApplication();
            SellerApplicationJpaEntity entity = SellerApplicationJpaEntityFixtures.pendingEntity();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(jpaRepository.save(entity)).willReturn(entity);

            // when
            sut.persist(domain);

            // then
            then(mapper).should(times(1)).toEntity(domain);
        }
    }

    // ========================================================================
    // 2. persistAll 테스트
    // ========================================================================

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("여러 Domain을 Entity로 변환 후 일괄 저장합니다")
        void persistAll_WithMultipleDomains_SavesAll() {
            // given
            SellerApplication domain1 = createPendingApplication();
            SellerApplication domain2 = createApprovedApplication(1L);
            List<SellerApplication> domains = List.of(domain1, domain2);

            SellerApplicationJpaEntity entity1 = SellerApplicationJpaEntityFixtures.pendingEntity();
            SellerApplicationJpaEntity entity2 =
                    SellerApplicationJpaEntityFixtures.approvedEntity(1L);

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);

            // when
            sut.persistAll(domains);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<SellerApplicationJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(jpaRepository).should().saveAll(captor.capture());

            List<SellerApplicationJpaEntity> savedEntities = captor.getValue();
            assertThat(savedEntities).hasSize(2);
            assertThat(savedEntities).contains(entity1, entity2);
        }

        @Test
        @DisplayName("빈 리스트를 저장해도 saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            List<SellerApplication> emptyList = List.of();

            // when
            sut.persistAll(emptyList);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<SellerApplicationJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(jpaRepository).should().saveAll(captor.capture());
            assertThat(captor.getValue()).isEmpty();
        }

        @Test
        @DisplayName("각 Domain에 대해 Mapper가 호출됩니다")
        void persistAll_CallsMapperForEachDomain() {
            // given
            SellerApplication domain1 = createPendingApplication();
            SellerApplication domain2 = createApprovedApplication(1L);
            SellerApplication domain3 = createRejectedApplication("서류 미비");
            List<SellerApplication> domains = List.of(domain1, domain2, domain3);

            SellerApplicationJpaEntity entity = SellerApplicationJpaEntityFixtures.pendingEntity();
            given(mapper.toEntity(domain1)).willReturn(entity);
            given(mapper.toEntity(domain2)).willReturn(entity);
            given(mapper.toEntity(domain3)).willReturn(entity);

            // when
            sut.persistAll(domains);

            // then
            then(mapper)
                    .should(times(3))
                    .toEntity(org.mockito.ArgumentMatchers.any(SellerApplication.class));
        }

        @Test
        @DisplayName("다양한 상태의 입점 신청을 일괄 저장합니다")
        void persistAll_WithMixedStatus_SavesAll() {
            // given
            SellerApplication pendingApp = createPendingApplication();
            SellerApplication approvedApp = createApprovedApplication(1L);
            SellerApplication rejectedApp = createRejectedApplication("중복 신청");
            List<SellerApplication> domains = List.of(pendingApp, approvedApp, rejectedApp);

            SellerApplicationJpaEntity pendingEntity =
                    SellerApplicationJpaEntityFixtures.pendingEntity();
            SellerApplicationJpaEntity approvedEntity =
                    SellerApplicationJpaEntityFixtures.approvedEntity(1L);
            SellerApplicationJpaEntity rejectedEntity =
                    SellerApplicationJpaEntityFixtures.rejectedEntity("중복 신청");

            given(mapper.toEntity(pendingApp)).willReturn(pendingEntity);
            given(mapper.toEntity(approvedApp)).willReturn(approvedEntity);
            given(mapper.toEntity(rejectedApp)).willReturn(rejectedEntity);

            // when
            sut.persistAll(domains);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<SellerApplicationJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(jpaRepository).should().saveAll(captor.capture());

            List<SellerApplicationJpaEntity> savedEntities = captor.getValue();
            assertThat(savedEntities).hasSize(3);
            assertThat(savedEntities)
                    .containsExactly(pendingEntity, approvedEntity, rejectedEntity);
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private SellerApplication createPendingApplication() {
        Instant now = Instant.now();
        return SellerApplication.reconstitute(
                SellerApplicationId.forNew(),
                SellerName.of("테스트셀러"),
                com.ryuqq.marketplace.domain.seller.vo.DisplayName.of("테스트 브랜드"),
                LogoUrl.of("https://example.com/logo.png"),
                Description.of("테스트 셀러 설명입니다."),
                RegistrationNumber.of("123-45-67890"),
                CompanyName.of("테스트컴퍼니"),
                Representative.of("홍길동"),
                SaleReportNumber.of("제2025-서울강남-1234호"),
                Address.of("12345", "서울시 강남구", "테헤란로 123"),
                CsContact.of("02-1234-5678", null, "cs@example.com"),
                ContactInfo.of("김담당", "010-9876-5432", "contact@example.com"),
                BankAccount.of("088", "신한은행", "110123456789", "홍길동"),
                SettlementCycle.MONTHLY,
                1,
                Agreement.reconstitute(now),
                ApplicationStatus.PENDING,
                now,
                null,
                null,
                null,
                null);
    }

    private SellerApplication createApprovedApplication(Long sellerId) {
        Instant now = Instant.now();
        Instant appliedAt = now.minusSeconds(3600);
        return SellerApplication.reconstitute(
                SellerApplicationId.of(1L),
                SellerName.of("테스트셀러"),
                com.ryuqq.marketplace.domain.seller.vo.DisplayName.of("테스트 브랜드"),
                LogoUrl.of("https://example.com/logo.png"),
                Description.of("테스트 셀러 설명입니다."),
                RegistrationNumber.of("123-45-67890"),
                CompanyName.of("테스트컴퍼니"),
                Representative.of("홍길동"),
                SaleReportNumber.of("제2025-서울강남-1234호"),
                Address.of("12345", "서울시 강남구", "테헤란로 123"),
                CsContact.of("02-1234-5678", null, "cs@example.com"),
                ContactInfo.of("김담당", "010-9876-5432", "contact@example.com"),
                BankAccount.of("088", "신한은행", "110123456789", "홍길동"),
                SettlementCycle.MONTHLY,
                1,
                Agreement.reconstitute(appliedAt),
                ApplicationStatus.APPROVED,
                appliedAt,
                now,
                "admin@example.com",
                null,
                SellerId.of(sellerId));
    }

    private SellerApplication createRejectedApplication(String rejectionReason) {
        Instant now = Instant.now();
        Instant appliedAt = now.minusSeconds(3600);
        return SellerApplication.reconstitute(
                SellerApplicationId.of(2L),
                SellerName.of("테스트셀러"),
                com.ryuqq.marketplace.domain.seller.vo.DisplayName.of("테스트 브랜드"),
                LogoUrl.of("https://example.com/logo.png"),
                Description.of("테스트 셀러 설명입니다."),
                RegistrationNumber.of("123-45-67890"),
                CompanyName.of("테스트컴퍼니"),
                Representative.of("홍길동"),
                SaleReportNumber.of("제2025-서울강남-1234호"),
                Address.of("12345", "서울시 강남구", "테헤란로 123"),
                CsContact.of("02-1234-5678", null, "cs@example.com"),
                ContactInfo.of("김담당", "010-9876-5432", "contact@example.com"),
                BankAccount.of("088", "신한은행", "110123456789", "홍길동"),
                SettlementCycle.MONTHLY,
                1,
                Agreement.reconstitute(appliedAt),
                ApplicationStatus.REJECTED,
                appliedAt,
                now,
                "admin@example.com",
                rejectionReason,
                null);
    }
}
