package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerCsJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerCsJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerCsJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerCsQueryDslRepository;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import com.ryuqq.marketplace.domain.seller.id.SellerCsId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
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
 * SellerCsQueryAdapterTest - 셀러 CS 정보 Query Adapter 단위 테스트.
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
@DisplayName("SellerCsQueryAdapter 단위 테스트")
class SellerCsQueryAdapterTest {

    @Mock private SellerCsQueryDslRepository queryDslRepository;

    @Mock private SellerCsJpaEntityMapper mapper;

    @InjectMocks private SellerCsQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 CS 정보를 조회하여 Domain으로 변환합니다")
        void findById_WithValidId_ReturnsDomain() {
            // given
            SellerCsId id = SellerCsId.of(1L);
            SellerCsJpaEntity entity = SellerCsJpaEntityFixtures.activeEntity();
            SellerCs domain = SellerFixtures.activeSellerCs();

            given(queryDslRepository.findById(id.value())).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SellerCs> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(queryDslRepository).should().findById(id.value());
            then(mapper).should().toDomain(entity);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 Optional.empty()를 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // given
            SellerCsId id = SellerCsId.of(999L);

            given(queryDslRepository.findById(id.value())).willReturn(Optional.empty());

            // when
            Optional<SellerCs> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findById(id.value());
        }
    }

    // ========================================================================
    // 2. findBySellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findBySellerId 메서드 테스트")
    class FindBySellerIdTest {

        @Test
        @DisplayName("셀러 ID로 CS 정보를 조회하여 Domain으로 변환합니다")
        void findBySellerId_WithValidSellerId_ReturnsDomain() {
            // given
            SellerId sellerId = SellerId.of(1L);
            SellerCsJpaEntity entity = SellerCsJpaEntityFixtures.activeEntity();
            SellerCs domain = SellerFixtures.activeSellerCs();

            given(queryDslRepository.findBySellerId(sellerId.value()))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SellerCs> result = queryAdapter.findBySellerId(sellerId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(queryDslRepository).should().findBySellerId(sellerId.value());
            then(mapper).should().toDomain(entity);
        }

        @Test
        @DisplayName("존재하지 않는 셀러 ID로 조회 시 Optional.empty()를 반환합니다")
        void findBySellerId_WithNonExistentSellerId_ReturnsEmpty() {
            // given
            SellerId sellerId = SellerId.of(999L);

            given(queryDslRepository.findBySellerId(sellerId.value())).willReturn(Optional.empty());

            // when
            Optional<SellerCs> result = queryAdapter.findBySellerId(sellerId);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findBySellerId(sellerId.value());
        }
    }

    // ========================================================================
    // 3. existsBySellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsBySellerId 메서드 테스트")
    class ExistsBySellerIdTest {

        @Test
        @DisplayName("셀러 ID로 CS 정보가 존재하면 true를 반환합니다")
        void existsBySellerId_WithExistingSellerId_ReturnsTrue() {
            // given
            SellerId sellerId = SellerId.of(1L);

            given(queryDslRepository.existsBySellerId(sellerId.value())).willReturn(true);

            // when
            boolean result = queryAdapter.existsBySellerId(sellerId);

            // then
            assertThat(result).isTrue();
            then(queryDslRepository).should().existsBySellerId(sellerId.value());
        }

        @Test
        @DisplayName("셀러 ID로 CS 정보가 존재하지 않으면 false를 반환합니다")
        void existsBySellerId_WithNonExistentSellerId_ReturnsFalse() {
            // given
            SellerId sellerId = SellerId.of(999L);

            given(queryDslRepository.existsBySellerId(sellerId.value())).willReturn(false);

            // when
            boolean result = queryAdapter.existsBySellerId(sellerId);

            // then
            assertThat(result).isFalse();
            then(queryDslRepository).should().existsBySellerId(sellerId.value());
        }
    }
}
