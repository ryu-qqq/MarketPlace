package com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.SellerApplicationJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.entity.SellerApplicationJpaEntity;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerApplicationJpaEntityMapperTest - 입점 신청 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerApplicationJpaEntityMapper 단위 테스트")
class SellerApplicationJpaEntityMapperTest {

    private SellerApplicationJpaEntityMapper sut;

    @BeforeEach
    void setUp() {
        sut = new SellerApplicationJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("대기 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithPendingApplication_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            SellerApplication domain = createPendingApplication(now);

            // when
            SellerApplicationJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSellerName()).isEqualTo(domain.sellerNameValue());
            assertThat(entity.getDisplayName()).isEqualTo(domain.displayNameValue());
            assertThat(entity.getStatus()).isEqualTo(domain.status());
            assertThat(entity.getAppliedAt()).isEqualTo(domain.appliedAt());
        }

        @Test
        @DisplayName("승인 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithApprovedApplication_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            SellerApplication domain = createApprovedApplication(now, 1L);

            // when
            SellerApplicationJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
            assertThat(entity.getProcessedAt()).isNotNull();
            assertThat(entity.getApprovedSellerId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("거절 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithRejectedApplication_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            SellerApplication domain = createRejectedApplication(now, "서류 미비");

            // when
            SellerApplicationJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
            assertThat(entity.getProcessedAt()).isNotNull();
            assertThat(entity.getRejectionReason()).isEqualTo("서류 미비");
        }

        @Test
        @DisplayName("로고 URL이 없는 Domain을 Entity로 변환합니다")
        void toEntity_WithoutLogoUrl_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            SellerApplication domain = createApplicationWithoutLogoUrl(now);

            // when
            SellerApplicationJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getLogoUrl()).isNull();
        }

        @Test
        @DisplayName("설명이 없는 Domain을 Entity로 변환합니다")
        void toEntity_WithoutDescription_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            SellerApplication domain = createApplicationWithoutDescription(now);

            // when
            SellerApplicationJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getDescription()).isNull();
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("대기 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithPendingEntity_ConvertsCorrectly() {
            // given
            SellerApplicationJpaEntity entity = SellerApplicationJpaEntityFixtures.pendingEntity();

            // when
            SellerApplication domain = sut.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerNameValue()).isEqualTo(entity.getSellerName());
            assertThat(domain.displayNameValue()).isEqualTo(entity.getDisplayName());
            assertThat(domain.status()).isEqualTo(entity.getStatus());
            assertThat(domain.appliedAt()).isEqualTo(entity.getAppliedAt());
            assertThat(domain.status()).isEqualTo(ApplicationStatus.PENDING);
        }

        @Test
        @DisplayName("승인 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithApprovedEntity_ConvertsCorrectly() {
            // given
            SellerApplicationJpaEntity entity =
                    SellerApplicationJpaEntityFixtures.approvedEntity(1L);

            // when
            SellerApplication domain = sut.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(ApplicationStatus.APPROVED);
            assertThat(domain.processedAt()).isNotNull();
            assertThat(domain.approvedSellerIdValue()).isEqualTo(1L);
        }

        @Test
        @DisplayName("거절 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithRejectedEntity_ConvertsCorrectly() {
            // given
            SellerApplicationJpaEntity entity = SellerApplicationJpaEntityFixtures.rejectedEntity();

            // when
            SellerApplication domain = sut.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(ApplicationStatus.REJECTED);
            assertThat(domain.processedAt()).isNotNull();
            assertThat(domain.rejectionReason()).isNotNull();
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain -> Entity -> Domain 변환 시 데이터가 보존됩니다")
        void roundTrip_DomainToEntityToDomain_PreservesData() {
            // given
            Instant now = Instant.now();
            SellerApplication original = createPendingApplication(now);

            // when
            SellerApplicationJpaEntity entity = sut.toEntity(original);
            SellerApplication converted = sut.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.sellerNameValue()).isEqualTo(original.sellerNameValue());
            assertThat(converted.displayNameValue()).isEqualTo(original.displayNameValue());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.appliedAt()).isEqualTo(original.appliedAt());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            SellerApplicationJpaEntity original =
                    SellerApplicationJpaEntityFixtures.pendingEntity();

            // when
            SellerApplication domain = sut.toDomain(original);
            SellerApplicationJpaEntity converted = sut.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSellerName()).isEqualTo(original.getSellerName());
            assertThat(converted.getDisplayName()).isEqualTo(original.getDisplayName());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private SellerApplication createPendingApplication(Instant now) {
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

    private SellerApplication createApprovedApplication(Instant now, Long sellerId) {
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

    private SellerApplication createRejectedApplication(Instant now, String rejectionReason) {
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

    private SellerApplication createApplicationWithoutLogoUrl(Instant now) {
        return SellerApplication.reconstitute(
                SellerApplicationId.forNew(),
                SellerName.of("테스트셀러"),
                com.ryuqq.marketplace.domain.seller.vo.DisplayName.of("테스트 브랜드"),
                null,
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

    private SellerApplication createApplicationWithoutDescription(Instant now) {
        return SellerApplication.reconstitute(
                SellerApplicationId.forNew(),
                SellerName.of("테스트셀러"),
                com.ryuqq.marketplace.domain.seller.vo.DisplayName.of("테스트 브랜드"),
                LogoUrl.of("https://example.com/logo.png"),
                null,
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
}
