package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerOperationCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerOperationCompositeDto.AddressSummaryDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerOperationCompositeDto.PolicySummaryDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.repository.SellerOperationCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.selleraddress.dto.composite.SellerOperationCompositeResult;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SellerOperationCompositeQueryAdapterTest - 셀러 운영 메타데이터 Composite 조회 Adapter 단위 테스트.
 *
 * <p>주소, 배송정책, 환불정책의 집계 메타데이터 변환 검증.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SellerOperationCompositeQueryAdapter 단위 테스트")
class SellerOperationCompositeQueryAdapterTest {

    @InjectMocks private SellerOperationCompositeQueryAdapter sut;

    @Mock private SellerOperationCompositeQueryDslRepository repository;

    @Nested
    @DisplayName("findOperationMetadataBySellerId() - 셀러 운영 메타데이터 조회")
    class FindOperationMetadataBySellerIdTest {

        @Test
        @DisplayName("주소, 배송정책, 환불정책이 모두 있을 때 집계 결과를 반환한다")
        void findOperationMetadataBySellerId_WithAllData_ReturnsAggregatedResult() {
            // given
            Long sellerIdValue = 1L;
            SellerId sellerId = SellerId.of(sellerIdValue);

            List<AddressSummaryDto> addresses =
                    List.of(
                            new AddressSummaryDto("SHIPPING", true),
                            new AddressSummaryDto("SHIPPING", false),
                            new AddressSummaryDto("RETURN", true));
            List<PolicySummaryDto> shippingPolicies =
                    List.of(new PolicySummaryDto(true), new PolicySummaryDto(false));
            List<PolicySummaryDto> refundPolicies = List.of(new PolicySummaryDto(true));

            SellerOperationCompositeDto dto =
                    new SellerOperationCompositeDto(
                            sellerIdValue, addresses, shippingPolicies, refundPolicies);

            given(repository.findBySellerId(sellerIdValue)).willReturn(dto);

            // when
            SellerOperationCompositeResult result = sut.findOperationMetadataBySellerId(sellerId);

            // then
            assertThat(result.addressTotalCount()).isEqualTo(3L);
            assertThat(result.shippingAddressCount()).isEqualTo(2L);
            assertThat(result.returnAddressCount()).isEqualTo(1L);
            assertThat(result.hasDefaultShippingAddress()).isTrue();
            assertThat(result.hasDefaultReturnAddress()).isTrue();
            assertThat(result.shippingPolicyCount()).isEqualTo(2L);
            assertThat(result.hasDefaultShippingPolicy()).isTrue();
            assertThat(result.refundPolicyCount()).isEqualTo(1L);
            assertThat(result.hasDefaultRefundPolicy()).isTrue();

            then(repository).should().findBySellerId(sellerIdValue);
        }

        @Test
        @DisplayName("주소가 없을 때 주소 카운트가 0이다")
        void findOperationMetadataBySellerId_WithNoAddresses_ReturnsZeroAddressCount() {
            // given
            Long sellerIdValue = 1L;
            SellerId sellerId = SellerId.of(sellerIdValue);

            SellerOperationCompositeDto dto =
                    new SellerOperationCompositeDto(sellerIdValue, List.of(), List.of(), List.of());

            given(repository.findBySellerId(sellerIdValue)).willReturn(dto);

            // when
            SellerOperationCompositeResult result = sut.findOperationMetadataBySellerId(sellerId);

            // then
            assertThat(result.addressTotalCount()).isZero();
            assertThat(result.shippingAddressCount()).isZero();
            assertThat(result.returnAddressCount()).isZero();
            assertThat(result.hasDefaultShippingAddress()).isFalse();
            assertThat(result.hasDefaultReturnAddress()).isFalse();
            assertThat(result.shippingPolicyCount()).isZero();
            assertThat(result.hasDefaultShippingPolicy()).isFalse();
            assertThat(result.refundPolicyCount()).isZero();
            assertThat(result.hasDefaultRefundPolicy()).isFalse();
        }

        @Test
        @DisplayName("기본 출고지가 없을 때 hasDefaultShippingAddress가 false이다")
        void findOperationMetadataBySellerId_WithNoDefaultShippingAddress_ReturnsFalse() {
            // given
            Long sellerIdValue = 2L;
            SellerId sellerId = SellerId.of(sellerIdValue);

            List<AddressSummaryDto> addresses =
                    List.of(
                            new AddressSummaryDto("SHIPPING", false),
                            new AddressSummaryDto("SHIPPING", false));
            List<PolicySummaryDto> shippingPolicies = List.of(new PolicySummaryDto(false));
            List<PolicySummaryDto> refundPolicies = List.of();

            SellerOperationCompositeDto dto =
                    new SellerOperationCompositeDto(
                            sellerIdValue, addresses, shippingPolicies, refundPolicies);

            given(repository.findBySellerId(sellerIdValue)).willReturn(dto);

            // when
            SellerOperationCompositeResult result = sut.findOperationMetadataBySellerId(sellerId);

            // then
            assertThat(result.shippingAddressCount()).isEqualTo(2L);
            assertThat(result.hasDefaultShippingAddress()).isFalse();
            assertThat(result.hasDefaultShippingPolicy()).isFalse();
        }

        @Test
        @DisplayName("기본 반품지가 없을 때 hasDefaultReturnAddress가 false이다")
        void findOperationMetadataBySellerId_WithNoDefaultReturnAddress_ReturnsFalse() {
            // given
            Long sellerIdValue = 3L;
            SellerId sellerId = SellerId.of(sellerIdValue);

            List<AddressSummaryDto> addresses =
                    List.of(
                            new AddressSummaryDto("SHIPPING", true),
                            new AddressSummaryDto("RETURN", false));
            List<PolicySummaryDto> shippingPolicies = List.of(new PolicySummaryDto(true));
            List<PolicySummaryDto> refundPolicies = List.of(new PolicySummaryDto(false));

            SellerOperationCompositeDto dto =
                    new SellerOperationCompositeDto(
                            sellerIdValue, addresses, shippingPolicies, refundPolicies);

            given(repository.findBySellerId(sellerIdValue)).willReturn(dto);

            // when
            SellerOperationCompositeResult result = sut.findOperationMetadataBySellerId(sellerId);

            // then
            assertThat(result.hasDefaultShippingAddress()).isTrue();
            assertThat(result.hasDefaultReturnAddress()).isFalse();
            assertThat(result.hasDefaultShippingPolicy()).isTrue();
            assertThat(result.hasDefaultRefundPolicy()).isFalse();
        }

        @Test
        @DisplayName("여러 정책이 있을 때 카운트가 정확하다")
        void findOperationMetadataBySellerId_WithMultiplePolicies_ReturnsCorrectCount() {
            // given
            Long sellerIdValue = 4L;
            SellerId sellerId = SellerId.of(sellerIdValue);

            List<AddressSummaryDto> addresses = List.of(new AddressSummaryDto("SHIPPING", true));
            List<PolicySummaryDto> shippingPolicies =
                    List.of(
                            new PolicySummaryDto(true),
                            new PolicySummaryDto(false),
                            new PolicySummaryDto(false));
            List<PolicySummaryDto> refundPolicies =
                    List.of(new PolicySummaryDto(false), new PolicySummaryDto(false));

            SellerOperationCompositeDto dto =
                    new SellerOperationCompositeDto(
                            sellerIdValue, addresses, shippingPolicies, refundPolicies);

            given(repository.findBySellerId(sellerIdValue)).willReturn(dto);

            // when
            SellerOperationCompositeResult result = sut.findOperationMetadataBySellerId(sellerId);

            // then
            assertThat(result.shippingPolicyCount()).isEqualTo(3L);
            assertThat(result.hasDefaultShippingPolicy()).isTrue();
            assertThat(result.refundPolicyCount()).isEqualTo(2L);
            assertThat(result.hasDefaultRefundPolicy()).isFalse();
        }
    }
}
