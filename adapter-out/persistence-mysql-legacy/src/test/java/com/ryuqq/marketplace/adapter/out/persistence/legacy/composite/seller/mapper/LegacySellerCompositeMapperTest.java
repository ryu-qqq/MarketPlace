package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.dto.LegacySellerCompositeQueryDto;
import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacySellerCompositeMapper 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("LegacySellerCompositeMapper 단위 테스트")
class LegacySellerCompositeMapperTest {

    private final LegacySellerCompositeMapper mapper = new LegacySellerCompositeMapper();

    @Nested
    @DisplayName("toResult 메서드 테스트")
    class ToResultTest {

        @Test
        @DisplayName("전체 필드가 올바르게 매핑됩니다")
        void toResult_WithFullDto_MapsAllFields() {
            // given
            LegacySellerCompositeQueryDto dto =
                    new LegacySellerCompositeQueryDto(
                            10L, "테스트 셀러", "logo.png", "셀러 설명", 15.5,
                            "123-45-67890", "테스트 회사", "홍길동", "2025-001",
                            "06123", "서울시 강남구", "4층",
                            "국민은행", "1234567890", "홍길동",
                            "02-1234-5678", "010-1234-5678", "cs@test.com");

            // when
            SellerAdminCompositeResult result = mapper.toResult(dto);

            // then - SellerInfo
            assertThat(result.seller().id()).isEqualTo(10L);
            assertThat(result.seller().sellerName()).isEqualTo("테스트 셀러");
            assertThat(result.seller().logoUrl()).isEqualTo("logo.png");
            assertThat(result.seller().description()).isEqualTo("셀러 설명");
            assertThat(result.seller().active()).isTrue();

            // then - BusinessInfo
            assertThat(result.businessInfo().registrationNumber()).isEqualTo("123-45-67890");
            assertThat(result.businessInfo().companyName()).isEqualTo("테스트 회사");
            assertThat(result.businessInfo().representative()).isEqualTo("홍길동");

            // then - CsInfo
            assertThat(result.csInfo().csPhone()).isEqualTo("02-1234-5678");
            assertThat(result.csInfo().csMobile()).isEqualTo("010-1234-5678");
            assertThat(result.csInfo().csEmail()).isEqualTo("cs@test.com");

            // then - ContractInfo
            assertThat(result.contractInfo().commissionRate())
                    .isEqualByComparingTo(BigDecimal.valueOf(15.5));

            // then - SettlementInfo
            assertThat(result.settlementInfo().bankName()).isEqualTo("국민은행");
            assertThat(result.settlementInfo().accountNumber()).isEqualTo("1234567890");
            assertThat(result.settlementInfo().accountHolderName()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("null 필드는 빈 문자열로 변환됩니다")
        void toResult_WithNullFields_DefaultsToEmptyString() {
            // given
            LegacySellerCompositeQueryDto dto =
                    new LegacySellerCompositeQueryDto(
                            10L, "셀러", null, null, null,
                            null, null, null, null,
                            null, null, null,
                            null, null, null,
                            null, null, null);

            // when
            SellerAdminCompositeResult result = mapper.toResult(dto);

            // then
            assertThat(result.seller().logoUrl()).isEmpty();
            assertThat(result.seller().description()).isEmpty();
            assertThat(result.businessInfo().registrationNumber()).isEmpty();
            assertThat(result.csInfo().csPhone()).isEmpty();
            assertThat(result.settlementInfo().bankName()).isEmpty();
        }

        @Test
        @DisplayName("commissionRate가 null인 경우 BigDecimal.ZERO로 변환됩니다")
        void toResult_WithNullCommissionRate_DefaultsToZero() {
            // given
            LegacySellerCompositeQueryDto dto =
                    new LegacySellerCompositeQueryDto(
                            10L, "셀러", null, null, null,
                            null, null, null, null,
                            null, null, null,
                            null, null, null,
                            null, null, null);

            // when
            SellerAdminCompositeResult result = mapper.toResult(dto);

            // then
            assertThat(result.contractInfo().commissionRate())
                    .isEqualByComparingTo(BigDecimal.ZERO);
        }
    }
}
