package com.ryuqq.marketplace.domain.selleraddress.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.SellerAddressFixtures;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressName;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAddress Aggregate 테스트")
class SellerAddressTest {

    @Nested
    @DisplayName("forNew() - 신규 주소 생성")
    class ForNewTest {

        @Test
        @DisplayName("SHIPPING 타입의 신규 주소를 생성한다")
        void createNewShippingAddress() {
            // given
            SellerId sellerId = SellerId.of(1L);
            AddressType addressType = AddressType.SHIPPING;
            AddressName addressName = AddressName.of("본사 창고");
            Address address = Address.of("06164", "서울 강남구 역삼로 123", "5층");
            Instant now = CommonVoFixtures.now();

            // when
            SellerAddress sellerAddress =
                    SellerAddress.forNew(sellerId, addressType, addressName, address, true, now);

            // then
            assertThat(sellerAddress.isNew()).isTrue();
            assertThat(sellerAddress.sellerId()).isEqualTo(sellerId);
            assertThat(sellerAddress.addressType()).isEqualTo(AddressType.SHIPPING);
            assertThat(sellerAddress.addressNameValue()).isEqualTo("본사 창고");
            assertThat(sellerAddress.addressZipCode()).isEqualTo("06164");
            assertThat(sellerAddress.addressRoad()).isEqualTo("서울 강남구 역삼로 123");
            assertThat(sellerAddress.addressDetail()).isEqualTo("5층");
            assertThat(sellerAddress.isDefaultAddress()).isTrue();
            assertThat(sellerAddress.isDeleted()).isFalse();
            assertThat(sellerAddress.createdAt()).isEqualTo(now);
            assertThat(sellerAddress.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("RETURN 타입의 신규 주소를 생성한다")
        void createNewReturnAddress() {
            // given
            SellerId sellerId = SellerId.of(1L);
            Instant now = CommonVoFixtures.now();

            // when
            SellerAddress sellerAddress =
                    SellerAddress.forNew(
                            sellerId,
                            AddressType.RETURN,
                            AddressName.of("반품 센터"),
                            Address.of("06164", "서울 강남구 역삼로 123", null),
                            false,
                            now);

            // then
            assertThat(sellerAddress.addressType()).isEqualTo(AddressType.RETURN);
            assertThat(sellerAddress.isDefaultAddress()).isFalse();
            assertThat(sellerAddress.isReturnAddress()).isTrue();
            assertThat(sellerAddress.isShippingAddress()).isFalse();
        }

        @Test
        @DisplayName("sellerId가 null이면 예외가 발생한다")
        void createWithNullSellerId_ThrowsException() {
            assertThatThrownBy(
                            () ->
                                    SellerAddress.forNew(
                                            null,
                                            AddressType.SHIPPING,
                                            AddressName.of("테스트"),
                                            Address.of("06164", "주소", null),
                                            true,
                                            Instant.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("SellerId는 필수");
        }
    }

    @Nested
    @DisplayName("update() - 주소 정보 수정")
    class UpdateTest {

        @Test
        @DisplayName("주소명과 주소를 수정한다")
        void updateAddressInfo() {
            // given
            SellerAddress address = SellerAddressFixtures.defaultShippingAddress(1L, 1L);
            AddressName newName = AddressName.of("수정된 창고");
            Address newAddress = Address.of("12345", "부산시 해운대구", "10층");
            Instant now = CommonVoFixtures.now();

            // when
            address.update(SellerAddressUpdateData.of(newName, newAddress), now);

            // then
            assertThat(address.addressNameValue()).isEqualTo("수정된 창고");
            assertThat(address.addressZipCode()).isEqualTo("12345");
            assertThat(address.addressRoad()).isEqualTo("부산시 해운대구");
            assertThat(address.addressDetail()).isEqualTo("10층");
            assertThat(address.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("markAsDefault() / unmarkDefault() - 기본 주소 설정/해제")
    class DefaultAddressTest {

        @Test
        @DisplayName("기본 주소로 설정한다")
        void markAsDefault() {
            // given
            SellerAddress address =
                    SellerAddressFixtures.nonDefaultShippingAddress(1L, 1L, "보조 창고");
            Instant now = CommonVoFixtures.now();

            // when
            address.markAsDefault(now);

            // then
            assertThat(address.isDefaultAddress()).isTrue();
            assertThat(address.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("기본 주소를 해제한다")
        void unmarkDefault() {
            // given
            SellerAddress address = SellerAddressFixtures.defaultShippingAddress(1L, 1L);
            Instant now = CommonVoFixtures.now();

            // when
            address.unmarkDefault(now);

            // then
            assertThat(address.isDefaultAddress()).isFalse();
            assertThat(address.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("delete() / restore() - 소프트 삭제/복원")
    class SoftDeleteTest {

        @Test
        @DisplayName("주소를 소프트 삭제한다")
        void softDelete() {
            // given
            SellerAddress address =
                    SellerAddressFixtures.nonDefaultShippingAddress(1L, 1L, "보조 창고");
            Instant now = CommonVoFixtures.now();

            // when
            address.delete(now);

            // then
            assertThat(address.isDeleted()).isTrue();
            assertThat(address.deletedAt()).isEqualTo(now);
            assertThat(address.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("삭제된 주소를 복원한다")
        void restoreDeletedAddress() {
            // given
            SellerAddress address = SellerAddressFixtures.deletedAddress(1L, 1L);
            assertThat(address.isDeleted()).isTrue();
            Instant now = CommonVoFixtures.now();

            // when
            address.restore(now);

            // then
            assertThat(address.isDeleted()).isFalse();
            assertThat(address.deletedAt()).isNull();
            assertThat(address.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("주소 타입 판별 메서드 테스트")
    class AddressTypeCheckTest {

        @Test
        @DisplayName("SHIPPING 주소인지 판별한다")
        void isShippingAddress() {
            SellerAddress address = SellerAddressFixtures.newShippingAddress(1L);
            assertThat(address.isShippingAddress()).isTrue();
            assertThat(address.isReturnAddress()).isFalse();
        }

        @Test
        @DisplayName("RETURN 주소인지 판별한다")
        void isReturnAddress() {
            SellerAddress address = SellerAddressFixtures.newReturnAddress(1L);
            assertThat(address.isReturnAddress()).isTrue();
            assertThat(address.isShippingAddress()).isFalse();
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("삭제 시각이 있으면 삭제 상태로 재구성한다")
        void reconstituteDeletedAddress() {
            SellerAddress address = SellerAddressFixtures.deletedAddress(1L, 1L);
            assertThat(address.isDeleted()).isTrue();
            assertThat(address.deletedAt()).isNotNull();
        }

        @Test
        @DisplayName("삭제 시각이 null이면 활성 상태로 재구성한다")
        void reconstituteActiveAddress() {
            SellerAddress address = SellerAddressFixtures.defaultShippingAddress(1L, 1L);
            assertThat(address.isDeleted()).isFalse();
            assertThat(address.deletedAt()).isNull();
        }
    }
}
