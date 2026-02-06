package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerAdminCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerCompositeDtoFixtures;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerAdminCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerCompositeResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerCompositeMapper 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerCompositeMapper 단위 테스트")
class SellerCompositeMapperTest {

    private final SellerCompositeMapper sut = new SellerCompositeMapper();

    @Nested
    @DisplayName("toResult")
    class ToResultTest {

        @Test
        @DisplayName("활성 셀러 DTO를 Result로 변환합니다")
        void toResult_WithActiveDto_ReturnsResult() {
            SellerCompositeDto dto = SellerCompositeDtoFixtures.activeSellerCompositeDto();

            SellerCompositeResult result = sut.toResult(dto);

            assertThat(result).isNotNull();
            assertThat(result.seller().id()).isEqualTo(dto.sellerId());
            assertThat(result.seller().sellerName()).isEqualTo(dto.sellerName());
            assertThat(result.seller().displayName()).isEqualTo(dto.displayName());
            assertThat(result.seller().logoUrl()).isEqualTo(dto.logoUrl());
            assertThat(result.seller().active()).isEqualTo(dto.active());
            assertThat(result.businessInfo().id()).isEqualTo(dto.businessInfoId());
            assertThat(result.businessInfo().registrationNumber())
                    .isEqualTo(dto.registrationNumber());
            assertThat(result.csInfo().id()).isEqualTo(dto.csId());
            assertThat(result.csInfo().operatingStartTime()).isEqualTo("09:00");
            assertThat(result.csInfo().operatingEndTime()).isEqualTo("18:00");
        }

        @Test
        @DisplayName("비활성 셀러 DTO(null 필드 포함)를 변환합니다")
        void toResult_WithInactiveDto_ReturnsResultWithNulls() {
            SellerCompositeDto dto = SellerCompositeDtoFixtures.inactiveSellerCompositeDto();

            SellerCompositeResult result = sut.toResult(dto);

            assertThat(result).isNotNull();
            assertThat(result.seller().active()).isFalse();
            assertThat(result.csInfo().operatingStartTime()).isNull();
            assertThat(result.csInfo().operatingEndTime()).isNull();
        }
    }

    @Nested
    @DisplayName("toAdminResult")
    class ToAdminResultTest {

        @Test
        @DisplayName("활성 Admin DTO를 Result로 변환합니다")
        void toAdminResult_WithActiveDto_ReturnsResult() {
            SellerAdminCompositeDto dto =
                    SellerCompositeDtoFixtures.activeSellerAdminCompositeDto();

            SellerAdminCompositeResult result = sut.toAdminResult(dto);

            assertThat(result).isNotNull();
            assertThat(result.seller().id()).isEqualTo(dto.sellerId());
            assertThat(result.businessInfo().registrationNumber())
                    .isEqualTo(dto.registrationNumber());
            assertThat(result.csInfo().operatingStartTime()).isEqualTo("09:00");
            assertThat(result.contractInfo().id()).isEqualTo(dto.contractId());
            assertThat(result.settlementInfo().bankCode()).isEqualTo(dto.bankCode());
            assertThat(result.settlementInfo().accountNumber()).isEqualTo(dto.accountNumber());
        }
    }
}
