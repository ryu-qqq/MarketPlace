package com.ryuqq.marketplace.application.shippingpolicy.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.shippingpolicy.port.out.query.ShippingPolicyQueryPort;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.ShippingPolicyFixtures;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.ShippingPolicyException;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import com.ryuqq.marketplace.domain.shippingpolicy.query.ShippingPolicySearchCriteria;
import java.util.Collections;
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

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ShippingPolicyReadManager 단위 테스트")
class ShippingPolicyReadManagerTest {

    @InjectMocks private ShippingPolicyReadManager sut;

    @Mock private ShippingPolicyQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 배송 정책 조회")
    class GetByIdTest {

        @Test
        @DisplayName("ID로 배송 정책을 조회한다")
        void getById_ExistingPolicy_ReturnsPolicy() {
            // given
            ShippingPolicyId id = ShippingPolicyId.of(1L);
            ShippingPolicy expected = ShippingPolicyFixtures.activeShippingPolicy();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            ShippingPolicy result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void getById_NonExistingPolicy_ThrowsException() {
            // given
            ShippingPolicyId id = ShippingPolicyId.of(999L);

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(ShippingPolicyException.class);
        }
    }

    @Nested
    @DisplayName("findDefaultBySellerId() - 셀러의 기본 배송 정책 조회")
    class FindDefaultBySellerIdTest {

        @Test
        @DisplayName("셀러의 기본 배송 정책을 조회한다")
        void findDefaultBySellerId_ExistingDefault_ReturnsPolicy() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            ShippingPolicy expected = ShippingPolicyFixtures.activeShippingPolicy();

            given(queryPort.findDefaultBySellerId(sellerId)).willReturn(Optional.of(expected));

            // when
            Optional<ShippingPolicy> result = sut.findDefaultBySellerId(sellerId);

            // then
            assertThat(result).isPresent().contains(expected);
        }

        @Test
        @DisplayName("기본 배송 정책이 없으면 빈 Optional을 반환한다")
        void findDefaultBySellerId_NoDefault_ReturnsEmpty() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();

            given(queryPort.findDefaultBySellerId(sellerId)).willReturn(Optional.empty());

            // when
            Optional<ShippingPolicy> result = sut.findDefaultBySellerId(sellerId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 검색 조건으로 배송 정책 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 배송 정책 목록을 조회한다")
        void findByCriteria_ReturnsMatchingPolicies() {
            // given
            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.defaultCriteria(
                            CommonVoFixtures.defaultSellerId());
            List<ShippingPolicy> expected =
                    List.of(
                            ShippingPolicyFixtures.activeShippingPolicy(),
                            ShippingPolicyFixtures.newPaidShippingPolicy());

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<ShippingPolicy> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void findByCriteria_NoResults_ReturnsEmptyList() {
            // given
            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.defaultCriteria(
                            CommonVoFixtures.defaultSellerId());

            given(queryPort.findByCriteria(criteria)).willReturn(Collections.emptyList());

            // when
            List<ShippingPolicy> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 배송 정책 수 조회")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건에 맞는 배송 정책 수를 반환한다")
        void countByCriteria_ReturnsCount() {
            // given
            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.defaultCriteria(
                            CommonVoFixtures.defaultSellerId());
            long expectedCount = 5L;

            given(queryPort.countByCriteria(criteria)).willReturn(expectedCount);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expectedCount);
        }
    }

    @Nested
    @DisplayName("countActiveBySellerId() - 셀러의 활성 정책 수 조회")
    class CountActiveBySellerIdTest {

        @Test
        @DisplayName("셀러의 활성 배송 정책 수를 반환한다")
        void countActiveBySellerId_ReturnsActiveCount() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            long expectedCount = 3L;

            given(queryPort.countActiveBySellerId(sellerId)).willReturn(expectedCount);

            // when
            long result = sut.countActiveBySellerId(sellerId);

            // then
            assertThat(result).isEqualTo(expectedCount);
        }
    }
}
