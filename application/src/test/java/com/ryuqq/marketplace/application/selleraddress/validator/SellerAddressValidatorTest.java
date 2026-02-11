package com.ryuqq.marketplace.application.selleraddress.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressReadManager;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.SellerAddressFixtures;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.exception.CannotDeleteDefaultAddressException;
import com.ryuqq.marketplace.domain.selleraddress.exception.DuplicateAddressNameException;
import com.ryuqq.marketplace.domain.selleraddress.exception.SellerAddressNotFoundException;
import com.ryuqq.marketplace.domain.selleraddress.id.SellerAddressId;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
import java.util.List;
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
@DisplayName("SellerAddressValidator 단위 테스트")
class SellerAddressValidatorTest {

    @InjectMocks private SellerAddressValidator sut;

    @Mock private SellerAddressReadManager readManager;

    @Nested
    @DisplayName("findExistingOrThrow() - 존재 여부 검증")
    class FindExistingOrThrowTest {

        @Test
        @DisplayName("존재하는 주소는 Domain 객체를 반환한다")
        void findExistingOrThrow_ExistingAddress_ReturnsAddress() {
            // given
            Long addressId = 1L;
            Long sellerId = 1L;
            SellerAddressId id = SellerAddressId.of(addressId);
            SellerAddress address =
                    SellerAddressFixtures.defaultShippingAddress(addressId, sellerId);

            given(readManager.getById(id)).willReturn(address);

            // when
            SellerAddress result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(address);
            then(readManager).should().getById(id);
        }

        @Test
        @DisplayName("존재하지 않는 주소는 예외를 발생시킨다")
        void findExistingOrThrow_NotFound_ThrowsException() {
            // given
            SellerAddressId id = SellerAddressId.of(999L);

            given(readManager.getById(id)).willThrow(new SellerAddressNotFoundException());

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(SellerAddressNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateNotDefaultAddress() - 기본 주소 삭제 불가 검증")
    class ValidateNotDefaultAddressTest {

        @Test
        @DisplayName("비기본 주소는 삭제 가능하다")
        void validateNotDefaultAddress_NonDefaultAddress_DoesNotThrow() {
            // given
            Long addressId = 1L;
            Long sellerId = 1L;
            SellerAddress address =
                    SellerAddressFixtures.nonDefaultShippingAddress(addressId, sellerId, "비기본 주소");

            // when & then
            sut.validateNotDefaultAddress(address);
            // 예외가 발생하지 않으면 성공
        }

        @Test
        @DisplayName("마지막 기본 주소는 삭제 가능하다")
        void validateNotDefaultAddress_LastDefaultAddress_DoesNotThrow() {
            // given
            Long addressId = 1L;
            Long sellerId = 1L;
            SellerAddress address =
                    SellerAddressFixtures.defaultShippingAddress(addressId, sellerId);

            given(readManager.findAllBySellerId(address.sellerId())).willReturn(List.of(address));

            // when & then
            sut.validateNotDefaultAddress(address);
            // 예외가 발생하지 않으면 성공
        }

        @Test
        @DisplayName("다른 주소가 있는 기본 주소는 삭제 불가능하다")
        void validateNotDefaultAddress_DefaultAddressWithOthers_ThrowsException() {
            // given
            Long addressId = 1L;
            Long sellerId = 1L;
            SellerAddress defaultAddress =
                    SellerAddressFixtures.defaultShippingAddress(addressId, sellerId);
            SellerAddress otherAddress =
                    SellerAddressFixtures.nonDefaultShippingAddress(2L, sellerId, "다른 주소");

            given(readManager.findAllBySellerId(defaultAddress.sellerId()))
                    .willReturn(List.of(defaultAddress, otherAddress));

            // when & then
            assertThatThrownBy(() -> sut.validateNotDefaultAddress(defaultAddress))
                    .isInstanceOf(CannotDeleteDefaultAddressException.class);
        }

        @Test
        @DisplayName("다른 타입의 주소만 있으면 기본 주소 삭제 가능하다")
        void validateNotDefaultAddress_DifferentTypeOnly_DoesNotThrow() {
            // given
            Long addressId = 1L;
            Long sellerId = 1L;
            SellerAddress defaultShipping =
                    SellerAddressFixtures.defaultShippingAddress(addressId, sellerId);
            SellerAddress defaultReturn = SellerAddressFixtures.defaultReturnAddress(2L, sellerId);

            given(readManager.findAllBySellerId(defaultShipping.sellerId()))
                    .willReturn(List.of(defaultShipping, defaultReturn));

            // when & then
            sut.validateNotDefaultAddress(defaultShipping);
            // 다른 타입이므로 예외 발생하지 않음
        }
    }

    @Nested
    @DisplayName("validateNoDuplicateAddressName() - 주소명 중복 검증")
    class ValidateNoDuplicateAddressNameTest {

        @Test
        @DisplayName("중복되지 않은 주소명은 검증을 통과한다")
        void validateNoDuplicateAddressName_UniqueAddressName_DoesNotThrow() {
            // given
            Long sellerId = 1L;
            SellerId id = SellerId.of(sellerId);
            AddressType addressType = AddressType.SHIPPING;
            String addressName = "새로운 주소";

            given(
                            readManager.existsBySellerIdAndAddressTypeAndAddressName(
                                    id, addressType, addressName))
                    .willReturn(false);

            // when & then
            sut.validateNoDuplicateAddressName(id, addressType, addressName);
            // 예외가 발생하지 않으면 성공
        }

        @Test
        @DisplayName("중복된 주소명은 예외를 발생시킨다")
        void validateNoDuplicateAddressName_DuplicateAddressName_ThrowsException() {
            // given
            Long sellerId = 1L;
            SellerId id = SellerId.of(sellerId);
            AddressType addressType = AddressType.SHIPPING;
            String addressName = "본사 창고";

            given(
                            readManager.existsBySellerIdAndAddressTypeAndAddressName(
                                    id, addressType, addressName))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(
                            () -> sut.validateNoDuplicateAddressName(id, addressType, addressName))
                    .isInstanceOf(DuplicateAddressNameException.class);
        }

        @Test
        @DisplayName("다른 타입에 같은 이름이 있어도 검증을 통과한다")
        void validateNoDuplicateAddressName_SameNameDifferentType_DoesNotThrow() {
            // given
            Long sellerId = 1L;
            SellerId id = SellerId.of(sellerId);
            AddressType addressType = AddressType.RETURN;
            String addressName = "본사 창고";

            given(
                            readManager.existsBySellerIdAndAddressTypeAndAddressName(
                                    id, addressType, addressName))
                    .willReturn(false);

            // when & then
            sut.validateNoDuplicateAddressName(id, addressType, addressName);
            // 타입이 다르므로 예외 발생하지 않음
        }
    }
}
