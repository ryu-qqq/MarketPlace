package com.ryuqq.marketplace.adapter.out.persistence.selleraddress.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.SellerAddressJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.condition.SellerAddressConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.entity.SellerAddressJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.mapper.SellerAddressJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.repository.SellerAddressQueryDslRepository;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.SellerAddressFixtures;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
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

/**
 * SellerAddressQueryAdapterTest - 셀러 주소 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SellerAddressQueryAdapter 단위 테스트")
class SellerAddressQueryAdapterTest {

    @Mock private SellerAddressQueryDslRepository queryDslRepository;

    @Mock private SellerAddressJpaEntityMapper mapper;

    @Mock private SellerAddressConditionBuilder conditionBuilder;

    @Mock private SellerAddressSearchCriteria criteria;

    @InjectMocks private SellerAddressQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Domain을 반환합니다")
        void findById_WithExistingId_ReturnsDomain() {
            // given
            SellerAddressId id = SellerAddressId.of(1L);
            SellerAddressJpaEntity entity =
                    SellerAddressJpaEntityFixtures.shippingEntityWithId(1L, 1L, "본사 창고", true);
            SellerAddress domain = SellerAddressFixtures.defaultShippingAddress(1L, 1L);

            given(queryDslRepository.findById(1L)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SellerAddress> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            SellerAddressId id = SellerAddressId.of(999L);
            given(queryDslRepository.findById(999L)).willReturn(Optional.empty());

            // when
            Optional<SellerAddress> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findAllBySellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findAllBySellerId 메서드 테스트")
    class FindAllBySellerIdTest {

        @Test
        @DisplayName("셀러 ID로 조회 시 주소 목록을 반환합니다")
        void findAllBySellerId_WithValidSellerId_ReturnsList() {
            // given
            SellerId sellerId = SellerId.of(1L);
            SellerAddressJpaEntity entity1 =
                    SellerAddressJpaEntityFixtures.shippingEntityWithId(1L, 1L, "본사 창고", true);
            SellerAddressJpaEntity entity2 =
                    SellerAddressJpaEntityFixtures.shippingEntityWithId(2L, 1L, "반품 센터", false);
            SellerAddress domain1 = SellerAddressFixtures.defaultShippingAddress(1L, 1L);
            SellerAddress domain2 =
                    SellerAddressFixtures.nonDefaultShippingAddress(2L, 1L, "반품 센터");

            given(queryDslRepository.findAllBySellerId(1L)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<SellerAddress> result = queryAdapter.findAllBySellerId(sellerId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }

        @Test
        @DisplayName("존재하지 않는 셀러 ID로 조회 시 빈 리스트를 반환합니다")
        void findAllBySellerId_WithNonExistingSellerId_ReturnsEmptyList() {
            // given
            SellerId sellerId = SellerId.of(999L);
            given(queryDslRepository.findAllBySellerId(999L)).willReturn(List.of());

            // when
            List<SellerAddress> result = queryAdapter.findAllBySellerId(sellerId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findDefaultBySellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findDefaultBySellerId 메서드 테스트")
    class FindDefaultBySellerIdTest {

        @Test
        @DisplayName("기본 SHIPPING 주소를 조회합니다")
        void findDefaultBySellerId_WithShippingType_ReturnsDefaultAddress() {
            // given
            SellerId sellerId = SellerId.of(1L);
            AddressType addressType = AddressType.SHIPPING;
            SellerAddressJpaEntity entity =
                    SellerAddressJpaEntityFixtures.shippingEntityWithId(1L, 1L, "본사 창고", true);
            SellerAddress domain = SellerAddressFixtures.defaultShippingAddress(1L, 1L);

            given(queryDslRepository.findDefaultAddress(1L, "SHIPPING"))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SellerAddress> result =
                    queryAdapter.findDefaultBySellerId(sellerId, addressType);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("기본 주소가 없으면 빈 Optional을 반환합니다")
        void findDefaultBySellerId_WithNoDefaultAddress_ReturnsEmpty() {
            // given
            SellerId sellerId = SellerId.of(1L);
            AddressType addressType = AddressType.RETURN;
            given(queryDslRepository.findDefaultAddress(1L, "RETURN")).willReturn(Optional.empty());

            // when
            Optional<SellerAddress> result =
                    queryAdapter.findDefaultBySellerId(sellerId, addressType);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 4. existsBySellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsBySellerId 메서드 테스트")
    class ExistsBySellerIdTest {

        @Test
        @DisplayName("주소가 존재하면 true를 반환합니다")
        void existsBySellerId_WithExistingAddress_ReturnsTrue() {
            // given
            SellerId sellerId = SellerId.of(1L);
            given(queryDslRepository.existsBySellerId(1L)).willReturn(true);

            // when
            boolean result = queryAdapter.existsBySellerId(sellerId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("주소가 없으면 false를 반환합니다")
        void existsBySellerId_WithNoAddress_ReturnsFalse() {
            // given
            SellerId sellerId = SellerId.of(999L);
            given(queryDslRepository.existsBySellerId(999L)).willReturn(false);

            // when
            boolean result = queryAdapter.existsBySellerId(sellerId);

            // then
            assertThat(result).isFalse();
        }
    }

    // ========================================================================
    // 5. existsBySellerIdAndAddressTypeAndAddressName 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsBySellerIdAndAddressTypeAndAddressName 메서드 테스트")
    class ExistsBySellerIdAndAddressTypeAndAddressNameTest {

        @Test
        @DisplayName("중복된 주소가 존재하면 true를 반환합니다")
        void existsBySellerIdAndAddressTypeAndAddressName_WithDuplicate_ReturnsTrue() {
            // given
            SellerId sellerId = SellerId.of(1L);
            AddressType addressType = AddressType.SHIPPING;
            String addressName = "본사 창고";
            given(
                            queryDslRepository.existsBySellerIdAndAddressTypeAndAddressName(
                                    1L, "SHIPPING", addressName))
                    .willReturn(true);

            // when
            boolean result =
                    queryAdapter.existsBySellerIdAndAddressTypeAndAddressName(
                            sellerId, addressType, addressName);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("중복된 주소가 없으면 false를 반환합니다")
        void existsBySellerIdAndAddressTypeAndAddressName_WithNoDuplicate_ReturnsFalse() {
            // given
            SellerId sellerId = SellerId.of(1L);
            AddressType addressType = AddressType.SHIPPING;
            String addressName = "새 창고";
            given(
                            queryDslRepository.existsBySellerIdAndAddressTypeAndAddressName(
                                    1L, "SHIPPING", addressName))
                    .willReturn(false);

            // when
            boolean result =
                    queryAdapter.existsBySellerIdAndAddressTypeAndAddressName(
                            sellerId, addressType, addressName);

            // then
            assertThat(result).isFalse();
        }
    }

    // ========================================================================
    // 6. search 테스트
    // ========================================================================

    @Nested
    @DisplayName("search 메서드 테스트")
    class SearchTest {

        @Test
        @DisplayName("검색 조건으로 주소 목록을 조회합니다")
        void search_WithValidCriteria_ReturnsList() {
            // given
            SellerAddressJpaEntity entity1 =
                    SellerAddressJpaEntityFixtures.shippingEntityWithId(1L, 1L, "본사 창고", true);
            SellerAddressJpaEntity entity2 =
                    SellerAddressJpaEntityFixtures.shippingEntityWithId(2L, 1L, "반품 센터", false);
            SellerAddress domain1 = SellerAddressFixtures.defaultShippingAddress(1L, 1L);
            SellerAddress domain2 =
                    SellerAddressFixtures.nonDefaultShippingAddress(2L, 1L, "반품 센터");

            given(criteria.sellerIdValues()).willReturn(List.of(1L));
            given(criteria.hasAddressTypesFilter()).willReturn(false);
            given(criteria.hasDefaultFilter()).willReturn(false);
            given(criteria.hasKeyword()).willReturn(false);
            given(criteria.offset()).willReturn(0L);
            given(criteria.size()).willReturn(10);
            given(
                            queryDslRepository.search(
                                    org.mockito.ArgumentMatchers.any(),
                                    org.mockito.ArgumentMatchers.anyLong(),
                                    org.mockito.ArgumentMatchers.anyInt()))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<SellerAddress> result = queryAdapter.search(criteria);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository)
                    .should()
                    .search(
                            org.mockito.ArgumentMatchers.any(),
                            org.mockito.ArgumentMatchers.eq(0L),
                            org.mockito.ArgumentMatchers.eq(10));
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환합니다")
        void search_WithNoResults_ReturnsEmptyList() {
            // given
            given(criteria.sellerIdValues()).willReturn(List.of(999L));
            given(criteria.hasAddressTypesFilter()).willReturn(false);
            given(criteria.hasDefaultFilter()).willReturn(false);
            given(criteria.hasKeyword()).willReturn(false);
            given(criteria.offset()).willReturn(0L);
            given(criteria.size()).willReturn(10);
            given(
                            queryDslRepository.search(
                                    org.mockito.ArgumentMatchers.any(),
                                    org.mockito.ArgumentMatchers.anyLong(),
                                    org.mockito.ArgumentMatchers.anyInt()))
                    .willReturn(List.of());

            // when
            List<SellerAddress> result = queryAdapter.search(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 7. count 테스트
    // ========================================================================

    @Nested
    @DisplayName("count 메서드 테스트")
    class CountTest {

        @Test
        @DisplayName("검색 조건으로 주소 개수를 반환합니다")
        void count_WithValidCriteria_ReturnsCount() {
            // given
            given(criteria.sellerIdValues()).willReturn(List.of(1L));
            given(criteria.hasAddressTypesFilter()).willReturn(false);
            given(criteria.hasDefaultFilter()).willReturn(false);
            given(criteria.hasKeyword()).willReturn(false);
            given(queryDslRepository.count(org.mockito.ArgumentMatchers.any())).willReturn(5L);

            // when
            long result = queryAdapter.count(criteria);

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환합니다")
        void count_WithNoResults_ReturnsZero() {
            // given
            given(criteria.sellerIdValues()).willReturn(List.of(999L));
            given(criteria.hasAddressTypesFilter()).willReturn(false);
            given(criteria.hasDefaultFilter()).willReturn(false);
            given(criteria.hasKeyword()).willReturn(false);
            given(queryDslRepository.count(org.mockito.ArgumentMatchers.any())).willReturn(0L);

            // when
            long result = queryAdapter.count(criteria);

            // then
            assertThat(result).isZero();
        }
    }
}
