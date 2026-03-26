package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.dto.LegacySellerAuthQueryDto;
import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacySellerAuthResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacySellerAuthCompositeMapper 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("LegacySellerAuthCompositeMapper 단위 테스트")
class LegacySellerAuthCompositeMapperTest {

    private final LegacySellerAuthCompositeMapper mapper = new LegacySellerAuthCompositeMapper();

    @Nested
    @DisplayName("toResult 메서드 테스트")
    class ToResultTest {

        @Test
        @DisplayName("모든 필드가 올바르게 매핑됩니다")
        void toResult_WithValidDto_MapsAllFields() {
            // given
            LegacySellerAuthQueryDto dto =
                    new LegacySellerAuthQueryDto(
                            10L, "admin@test.com", "$2a$10$hashvalue", "MASTER", "APPROVED");

            // when
            LegacySellerAuthResult result = mapper.toResult(dto);

            // then
            assertThat(result.sellerId()).isEqualTo(10L);
            assertThat(result.email()).isEqualTo("admin@test.com");
            assertThat(result.passwordHash()).isEqualTo("$2a$10$hashvalue");
            assertThat(result.roleType()).isEqualTo("MASTER");
            assertThat(result.approvalStatus()).isEqualTo("APPROVED");
            assertThat(result.isApproved()).isTrue();
        }

        @Test
        @DisplayName("SELLER 역할이 올바르게 매핑됩니다")
        void toResult_WithSellerRole_MapsCorrectly() {
            // given
            LegacySellerAuthQueryDto dto =
                    new LegacySellerAuthQueryDto(
                            20L, "seller@test.com", "$2a$10$hash2", "SELLER", "APPROVED");

            // when
            LegacySellerAuthResult result = mapper.toResult(dto);

            // then
            assertThat(result.roleType()).isEqualTo("SELLER");
            assertThat(result.isApproved()).isTrue();
        }

        @Test
        @DisplayName("PENDING 승인 상태가 올바르게 매핑됩니다")
        void toResult_WithPendingStatus_MapsCorrectly() {
            // given
            LegacySellerAuthQueryDto dto =
                    new LegacySellerAuthQueryDto(
                            30L, "pending@test.com", "$2a$10$hash3", "MASTER", "PENDING");

            // when
            LegacySellerAuthResult result = mapper.toResult(dto);

            // then
            assertThat(result.approvalStatus()).isEqualTo("PENDING");
            assertThat(result.isApproved()).isFalse();
        }
    }
}
