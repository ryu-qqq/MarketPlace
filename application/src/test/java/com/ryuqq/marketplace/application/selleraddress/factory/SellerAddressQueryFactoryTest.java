package com.ryuqq.marketplace.application.selleraddress.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.selleraddress.SellerAddressQueryFixtures;
import com.ryuqq.marketplace.application.selleraddress.dto.query.SellerAddressSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.selleraddress.query.SellerAddressSearchCriteria;
import com.ryuqq.marketplace.domain.selleraddress.query.SellerAddressSortKey;
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
@DisplayName("SellerAddressQueryFactory 단위 테스트")
class SellerAddressQueryFactoryTest {

    @InjectMocks private SellerAddressQueryFactory sut;

    @Mock private CommonVoFactory commonVoFactory;

    @Nested
    @DisplayName("createSearchCriteria() - 검색 Criteria 생성")
    class CreateSearchCriteriaTest {

        @Test
        @DisplayName("SearchParams로 SellerAddressSearchCriteria를 생성한다")
        void createSearchCriteria_ReturnsCriteria() {
            // given
            Long sellerId = 1L;
            SellerAddressSearchParams params = SellerAddressQueryFixtures.searchParams(sellerId);
            PageRequest pageRequest = PageRequest.of(0, 20);
            SortDirection sortDirection = SortDirection.DESC;

            given(commonVoFactory.parseSortDirection(anyString())).willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(0, 20)).willReturn(pageRequest);
            given(commonVoFactory.createQueryContext(
                            any(SellerAddressSortKey.class),
                            any(SortDirection.class),
                            any(PageRequest.class),
                            any(Boolean.class)))
                    .willCallRealMethod();

            // when
            SellerAddressSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.sellerIds()).hasSize(1);
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("주소 타입 필터로 검색 Criteria를 생성한다")
        void createSearchCriteria_WithAddressTypes_ReturnsCriteria() {
            // given
            Long sellerId = 1L;
            List<String> addressTypes = List.of("SHIPPING");
            SellerAddressSearchParams params =
                    SellerAddressQueryFixtures.searchParams(sellerId, addressTypes);
            PageRequest pageRequest = PageRequest.of(0, 20);
            SortDirection sortDirection = SortDirection.DESC;

            given(commonVoFactory.parseSortDirection(anyString())).willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(0, 20)).willReturn(pageRequest);
            given(commonVoFactory.createQueryContext(
                            any(SellerAddressSortKey.class),
                            any(SortDirection.class),
                            any(PageRequest.class),
                            any(Boolean.class)))
                    .willCallRealMethod();

            // when
            SellerAddressSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.addressTypes()).hasSize(1);
            assertThat(result.addressTypes()).containsExactly(AddressType.SHIPPING);
        }

        @Test
        @DisplayName("기본 주소 필터로 검색 Criteria를 생성한다")
        void createSearchCriteria_WithDefaultAddress_ReturnsCriteria() {
            // given
            Long sellerId = 1L;
            SellerAddressSearchParams params =
                    SellerAddressQueryFixtures.searchParams(sellerId, true);
            PageRequest pageRequest = PageRequest.of(0, 20);
            SortDirection sortDirection = SortDirection.DESC;

            given(commonVoFactory.parseSortDirection(anyString())).willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(0, 20)).willReturn(pageRequest);
            given(commonVoFactory.createQueryContext(
                            any(SellerAddressSortKey.class),
                            any(SortDirection.class),
                            any(PageRequest.class),
                            any(Boolean.class)))
                    .willCallRealMethod();

            // when
            SellerAddressSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.defaultAddress()).isTrue();
        }

        @Test
        @DisplayName("페이징 정보로 검색 Criteria를 생성한다")
        void createSearchCriteria_WithPaging_ReturnsCriteria() {
            // given
            Long sellerId = 1L;
            int page = 1;
            int size = 10;
            SellerAddressSearchParams params =
                    SellerAddressQueryFixtures.searchParams(sellerId, page, size);
            PageRequest pageRequest = PageRequest.of(page, size);
            SortDirection sortDirection = SortDirection.DESC;

            given(commonVoFactory.parseSortDirection(anyString())).willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(page, size)).willReturn(pageRequest);
            given(commonVoFactory.createQueryContext(
                            any(SellerAddressSortKey.class),
                            any(SortDirection.class),
                            any(PageRequest.class),
                            any(Boolean.class)))
                    .willCallRealMethod();

            // when
            SellerAddressSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.page()).isEqualTo(page);
            assertThat(result.size()).isEqualTo(size);
        }
    }
}
