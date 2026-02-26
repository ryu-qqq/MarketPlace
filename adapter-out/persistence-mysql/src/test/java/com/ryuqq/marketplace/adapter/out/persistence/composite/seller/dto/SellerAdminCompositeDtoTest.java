package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerAdminCompositeDtoTest - SellerAdminCompositeDto 단위 테스트.
 *
 * <p>Record DTO 생성 및 불변성 검증.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerAdminCompositeDto 단위 테스트")
class SellerAdminCompositeDtoTest {

    // ========================================================================
    // 1. 생성 테스트
    // ========================================================================

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("전체 필드로 DTO를 생성합니다")
        void create_WithAllFields_CreatesDto() {
            // given
            Instant now = Instant.now();
            Long sellerId = 1L;
            String sellerName = "테스트셀러";
            String displayName = "테스트 디스플레이명";
            String logoUrl = "https://logo.example.com/logo.png";
            String description = "테스트 셀러 설명입니다.";
            boolean active = true;

            // when
            SellerAdminCompositeDto dto =
                    new SellerAdminCompositeDto(
                            sellerId,
                            sellerName,
                            displayName,
                            logoUrl,
                            description,
                            active,
                            now,
                            now,
                            200L,
                            "123-45-67890",
                            "(주)테스트회사",
                            "홍길동",
                            "2024-서울강남-0001",
                            "06234",
                            "서울시 강남구 역삼동",
                            "456호",
                            300L,
                            "02-1234-5678",
                            "010-9876-5432",
                            "cs@test.com",
                            LocalTime.of(9, 0),
                            LocalTime.of(18, 0),
                            "MON,TUE,WED,THU,FRI",
                            "https://pf.kakao.com/test",
                            400L,
                            new BigDecimal("5.00"),
                            LocalDate.of(2024, 1, 1),
                            LocalDate.of(2024, 12, 31),
                            "ACTIVE",
                            "특별 약관",
                            now,
                            now,
                            500L,
                            "004",
                            "KB국민은행",
                            "12345678901234",
                            "홍길동",
                            "MONTHLY",
                            25,
                            true,
                            now,
                            now,
                            now);

            // then
            assertThat(dto).isNotNull();
            assertThat(dto.sellerId()).isEqualTo(sellerId);
            assertThat(dto.sellerName()).isEqualTo(sellerName);
            assertThat(dto.displayName()).isEqualTo(displayName);
            assertThat(dto.active()).isTrue();
        }

        @Test
        @DisplayName("선택적 필드가 null인 DTO를 생성합니다")
        void create_WithNullOptionalFields_CreatesDto() {
            // given
            Instant now = Instant.now();

            // when
            SellerAdminCompositeDto dto =
                    new SellerAdminCompositeDto(
                            1L,
                            "테스트셀러",
                            "테스트 디스플레이명",
                            null,
                            null,
                            true,
                            now,
                            now,
                            200L,
                            "123-45-67890",
                            "(주)테스트회사",
                            "홍길동",
                            null,
                            "06234",
                            "서울시 강남구 역삼동",
                            "456호",
                            300L,
                            "02-1234-5678",
                            null,
                            "cs@test.com",
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            false,
                            null,
                            null,
                            null);

            // then
            assertThat(dto).isNotNull();
            assertThat(dto.logoUrl()).isNull();
            assertThat(dto.description()).isNull();
            assertThat(dto.contractId()).isNull();
            assertThat(dto.settlementId()).isNull();
        }
    }

    // ========================================================================
    // 2. 필드 접근 테스트
    // ========================================================================

    @Nested
    @DisplayName("필드 접근 테스트")
    class FieldAccessTest {

        @Test
        @DisplayName("Seller 필드에 접근합니다")
        void access_SellerFields() {
            // given
            SellerAdminCompositeDto dto =
                    SellerCompositeDtoFixtures.activeSellerAdminCompositeDto();

            // when & then
            assertThat(dto.sellerId()).isNotNull();
            assertThat(dto.sellerName()).isNotBlank();
            assertThat(dto.displayName()).isNotBlank();
            assertThat(dto.active()).isTrue();
        }

        @Test
        @DisplayName("BusinessInfo 필드에 접근합니다")
        void access_BusinessInfoFields() {
            // given
            SellerAdminCompositeDto dto =
                    SellerCompositeDtoFixtures.activeSellerAdminCompositeDto();

            // when & then
            assertThat(dto.businessInfoId()).isNotNull();
            assertThat(dto.registrationNumber()).isNotBlank();
            assertThat(dto.companyName()).isNotBlank();
            assertThat(dto.representative()).isNotBlank();
        }

        @Test
        @DisplayName("CsInfo 필드에 접근합니다")
        void access_CsInfoFields() {
            // given
            SellerAdminCompositeDto dto =
                    SellerCompositeDtoFixtures.activeSellerAdminCompositeDto();

            // when & then
            assertThat(dto.csId()).isNotNull();
            assertThat(dto.csPhone()).isNotBlank();
            assertThat(dto.csEmail()).isNotBlank();
        }

        @Test
        @DisplayName("Contract 필드에 접근합니다")
        void access_ContractFields() {
            // given
            SellerAdminCompositeDto dto =
                    SellerCompositeDtoFixtures.activeSellerAdminCompositeDto();

            // when & then
            assertThat(dto.contractId()).isNotNull();
            assertThat(dto.commissionRate()).isNotNull();
            assertThat(dto.contractStatus()).isNotBlank();
        }

        @Test
        @DisplayName("Settlement 필드에 접근합니다")
        void access_SettlementFields() {
            // given
            SellerAdminCompositeDto dto =
                    SellerCompositeDtoFixtures.activeSellerAdminCompositeDto();

            // when & then
            assertThat(dto.settlementId()).isNotNull();
            assertThat(dto.bankCode()).isNotBlank();
            assertThat(dto.bankName()).isNotBlank();
            assertThat(dto.verified()).isTrue();
        }
    }

    // ========================================================================
    // 3. 불변성 테스트
    // ========================================================================

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("Record는 불변 객체입니다")
        void dto_IsImmutable() {
            // given
            SellerAdminCompositeDto dto1 =
                    SellerCompositeDtoFixtures.activeSellerAdminCompositeDto();
            SellerAdminCompositeDto dto2 =
                    SellerCompositeDtoFixtures.activeSellerAdminCompositeDto();

            // when & then
            assertThat(dto1).isNotSameAs(dto2);
            assertThat(dto1.sellerId()).isEqualTo(dto2.sellerId());
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작합니다")
        void dto_EqualsAndHashCode() {
            // given
            Instant now = Instant.now();
            SellerAdminCompositeDto dto1 = createDto(now);
            SellerAdminCompositeDto dto2 = createDto(now);

            // when & then
            assertThat(dto1).isEqualTo(dto2);
            assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        }

        private SellerAdminCompositeDto createDto(Instant now) {
            return new SellerAdminCompositeDto(
                    1L,
                    "테스트셀러",
                    "테스트 디스플레이명",
                    "https://logo.example.com/logo.png",
                    "테스트 셀러 설명입니다.",
                    true,
                    now,
                    now,
                    200L,
                    "123-45-67890",
                    "(주)테스트회사",
                    "홍길동",
                    "2024-서울강남-0001",
                    "06234",
                    "서울시 강남구 역삼동",
                    "456호",
                    300L,
                    "02-1234-5678",
                    "010-9876-5432",
                    "cs@test.com",
                    LocalTime.of(9, 0),
                    LocalTime.of(18, 0),
                    "MON,TUE,WED,THU,FRI",
                    "https://pf.kakao.com/test",
                    400L,
                    new BigDecimal("5.00"),
                    LocalDate.of(2024, 1, 1),
                    LocalDate.of(2024, 12, 31),
                    "ACTIVE",
                    "특별 약관",
                    now,
                    now,
                    500L,
                    "004",
                    "KB국민은행",
                    "12345678901234",
                    "홍길동",
                    "MONTHLY",
                    25,
                    true,
                    now,
                    now,
                    now);
        }
    }
}
