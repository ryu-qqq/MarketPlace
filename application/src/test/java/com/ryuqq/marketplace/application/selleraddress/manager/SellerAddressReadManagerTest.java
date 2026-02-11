package com.ryuqq.marketplace.application.selleraddress.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.selleraddress.port.out.query.SellerAddressQueryPort;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.SellerAddressFixtures;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.exception.SellerAddressNotFoundException;
import com.ryuqq.marketplace.domain.selleraddress.id.SellerAddressId;
import com.ryuqq.marketplace.domain.selleraddress.query.SellerAddressSearchCriteria;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
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
@DisplayName("SellerAddressReadManager 단위 테스트")
class SellerAddressReadManagerTest {

    @InjectMocks private SellerAddressReadManager sut;

    @Mock private SellerAddressQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 조회")
    class GetByIdTest {

        @Test
        @DisplayName("ID로 SellerAddress를 조회한다")
        void getById_ReturnsAddress() {
            // given
            Long addressId = 1L;
            Long sellerId = 1L;
            SellerAddressId id = SellerAddressId.of(addressId);
            SellerAddress address = SellerAddressFixtures.defaultShippingAddress(addressId, sellerId);

            given(queryPort.findById(id)).willReturn(Optional.of(address));

            // when
            SellerAddress result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(address);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외를 발생시킨다")
        void getById_NotFound_ThrowsException() {
            // given
            SellerAddressId id = SellerAddressId.of(999L);

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(SellerAddressNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findAllBySellerId() - 셀러 ID로 전체 조회")
    class FindAllBySellerIdTest {

        @Test
        @DisplayName("셀러 ID로 모든 주소를 조회한다")
        void findAllBySellerId_ReturnsAddresses() {
            // given
            Long sellerId = 1L;
            SellerId id = SellerId.of(sellerId);
            List<SellerAddress> addresses =
                    List.of(
                            SellerAddressFixtures.defaultShippingAddress(1L, sellerId),
                            SellerAddressFixtures.defaultReturnAddress(2L, sellerId));

            given(queryPort.findAllBySellerId(id)).willReturn(addresses);

            // when
            List<SellerAddress> result = sut.findAllBySellerId(id);

            // then
            assertThat(result).hasSize(2);
            then(queryPort).should().findAllBySellerId(id);
        }
    }

    @Nested
    @DisplayName("findDefaultBySellerId() - 기본 주소 조회")
    class FindDefaultBySellerIdTest {

        @Test
        @DisplayName("셀러의 기본 SHIPPING 주소를 조회한다")
        void findDefaultBySellerId_ReturnsDefaultAddress() {
            // given
            Long sellerId = 1L;
            SellerId id = SellerId.of(sellerId);
            AddressType addressType = AddressType.SHIPPING;
            SellerAddress address = SellerAddressFixtures.defaultShippingAddress(1L, sellerId);

            given(queryPort.findDefaultBySellerId(id, addressType)).willReturn(Optional.of(address));

            // when
            Optional<SellerAddress> result = sut.findDefaultBySellerId(id, addressType);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(address);
        }

        @Test
        @DisplayName("기본 주소가 없으면 empty를 반환한다")
        void findDefaultBySellerId_NoDefault_ReturnsEmpty() {
            // given
            Long sellerId = 1L;
            SellerId id = SellerId.of(sellerId);
            AddressType addressType = AddressType.SHIPPING;

            given(queryPort.findDefaultBySellerId(id, addressType)).willReturn(Optional.empty());

            // when
            Optional<SellerAddress> result = sut.findDefaultBySellerId(id, addressType);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsBySellerId() - 존재 여부 확인")
    class ExistsBySellerIdTest {

        @Test
        @DisplayName("셀러에게 주소가 있으면 true를 반환한다")
        void existsBySellerId_HasAddresses_ReturnsTrue() {
            // given
            Long sellerId = 1L;
            SellerId id = SellerId.of(sellerId);

            given(queryPort.existsBySellerId(id)).willReturn(true);

            // when
            boolean result = sut.existsBySellerId(id);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("셀러에게 주소가 없으면 false를 반환한다")
        void existsBySellerId_NoAddresses_ReturnsFalse() {
            // given
            Long sellerId = 1L;
            SellerId id = SellerId.of(sellerId);

            given(queryPort.existsBySellerId(id)).willReturn(false);

            // when
            boolean result = sut.existsBySellerId(id);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("search() - 검색")
    class SearchTest {

        @Test
        @DisplayName("검색 조건으로 주소를 검색한다")
        void search_ReturnsAddresses() {
            // given
            Long sellerId = 1L;
            SellerAddressSearchCriteria criteria =
                    SellerAddressSearchCriteria.of(
                            List.of(SellerId.of(sellerId)), null, null, null, null);
            List<SellerAddress> addresses =
                    List.of(SellerAddressFixtures.defaultShippingAddress(1L, sellerId));

            given(queryPort.search(criteria)).willReturn(addresses);

            // when
            List<SellerAddress> result = sut.search(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().search(criteria);
        }
    }

    @Nested
    @DisplayName("count() - 개수 조회")
    class CountTest {

        @Test
        @DisplayName("검색 조건으로 주소 개수를 조회한다")
        void count_ReturnsCount() {
            // given
            Long sellerId = 1L;
            SellerAddressSearchCriteria criteria =
                    SellerAddressSearchCriteria.of(
                            List.of(SellerId.of(sellerId)), null, null, null, null);
            long expectedCount = 5L;

            given(queryPort.count(criteria)).willReturn(expectedCount);

            // when
            long result = sut.count(criteria);

            // then
            assertThat(result).isEqualTo(expectedCount);
            then(queryPort).should().count(criteria);
        }
    }
}
