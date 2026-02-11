package com.ryuqq.marketplace.application.selleraddress.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressPageResult;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressResult;
import com.ryuqq.marketplace.domain.selleraddress.SellerAddressFixtures;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAddressAssembler 단위 테스트")
class SellerAddressAssemblerTest {

    private SellerAddressAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new SellerAddressAssembler();
    }

    @Nested
    @DisplayName("toResult() - SellerAddress → SellerAddressResult 변환")
    class ToResultTest {

        @Test
        @DisplayName("기본 SHIPPING 주소를 Result로 변환한다")
        void toResult_DefaultShippingAddress_ReturnsResult() {
            // given
            Long addressId = 1L;
            Long sellerId = 1L;
            SellerAddress address =
                    SellerAddressFixtures.defaultShippingAddress(addressId, sellerId);

            // when
            SellerAddressResult result = sut.toResult(address);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(addressId);
            assertThat(result.sellerId()).isEqualTo(sellerId);
            assertThat(result.addressType()).isEqualTo("SHIPPING");
            assertThat(result.addressName()).isEqualTo(address.addressNameValue());
            assertThat(result.defaultAddress()).isTrue();
        }

        @Test
        @DisplayName("비기본 RETURN 주소를 Result로 변환한다")
        void toResult_NonDefaultReturnAddress_ReturnsResult() {
            // given
            Long addressId = 2L;
            Long sellerId = 1L;
            SellerAddress address =
                    SellerAddressFixtures.nonDefaultReturnAddress(addressId, sellerId, "반품센터");

            // when
            SellerAddressResult result = sut.toResult(address);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(addressId);
            assertThat(result.addressType()).isEqualTo("RETURN");
            assertThat(result.defaultAddress()).isFalse();
        }
    }

    @Nested
    @DisplayName("toResults() - SellerAddress List → Result List 변환")
    class ToResultsTest {

        @Test
        @DisplayName("SellerAddress 목록을 Result 목록으로 변환한다")
        void toResults_ReturnsList() {
            // given
            Long sellerId = 1L;
            List<SellerAddress> addresses =
                    List.of(
                            SellerAddressFixtures.defaultShippingAddress(1L, sellerId),
                            SellerAddressFixtures.nonDefaultShippingAddress(2L, sellerId, "추가 배송지"));

            // when
            List<SellerAddressResult> results = sut.toResults(addresses);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).id()).isEqualTo(1L);
            assertThat(results.get(0).defaultAddress()).isTrue();
            assertThat(results.get(1).id()).isEqualTo(2L);
            assertThat(results.get(1).defaultAddress()).isFalse();
        }

        @Test
        @DisplayName("빈 목록을 변환하면 빈 결과를 반환한다")
        void toResults_EmptyList_ReturnsEmpty() {
            // given
            List<SellerAddress> addresses = List.of();

            // when
            List<SellerAddressResult> results = sut.toResults(addresses);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResult() - 페이지 결과 생성")
    class ToPageResultTest {

        @Test
        @DisplayName("SellerAddress 목록으로 PageResult를 생성한다")
        void toPageResult_ReturnsPageResult() {
            // given
            Long sellerId = 1L;
            List<SellerAddress> addresses =
                    List.of(SellerAddressFixtures.defaultShippingAddress(1L, sellerId));
            int page = 0;
            int size = 20;
            long totalElements = 1L;

            // when
            SellerAddressPageResult result = sut.toPageResult(addresses, page, size, totalElements);

            // then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
            assertThat(result.pageMeta().page()).isEqualTo(page);
            assertThat(result.pageMeta().size()).isEqualTo(size);
            assertThat(result.pageMeta().hasNext()).isFalse();
        }

        @Test
        @DisplayName("빈 목록으로 빈 PageResult를 생성한다")
        void toPageResult_EmptyList_ReturnsEmptyPageResult() {
            // given
            List<SellerAddress> addresses = List.of();
            int page = 0;
            int size = 20;
            long totalElements = 0L;

            // when
            SellerAddressPageResult result = sut.toPageResult(addresses, page, size, totalElements);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("다음 페이지가 있으면 hasNext가 true이다")
        void toPageResult_HasMorePages_HasNextIsTrue() {
            // given
            Long sellerId = 1L;
            List<SellerAddress> addresses =
                    List.of(
                            SellerAddressFixtures.defaultShippingAddress(1L, sellerId),
                            SellerAddressFixtures.nonDefaultShippingAddress(2L, sellerId, "추가"));
            int page = 0;
            int size = 2;
            long totalElements = 10L;

            // when
            SellerAddressPageResult result = sut.toPageResult(addresses, page, size, totalElements);

            // then
            assertThat(result.pageMeta().hasNext()).isTrue();
        }

        @Test
        @DisplayName("마지막 페이지이면 hasNext가 false이다")
        void toPageResult_LastPage_HasNextIsFalse() {
            // given
            Long sellerId = 1L;
            List<SellerAddress> addresses =
                    List.of(SellerAddressFixtures.defaultShippingAddress(1L, sellerId));
            int page = 4;
            int size = 2;
            long totalElements = 10L;

            // when
            SellerAddressPageResult result = sut.toPageResult(addresses, page, size, totalElements);

            // then
            assertThat(result.pageMeta().hasNext()).isFalse();
        }
    }
}
