package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerBusinessInfoJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerBusinessInfoJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerBusinessInfoJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerBusinessInfoQueryDslRepository;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.id.SellerBusinessInfoId;
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
 * SellerBusinessInfoQueryAdapterTest - 셀러 사업자 정보 Query Adapter 단위 테스트.
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
@DisplayName("SellerBusinessInfoQueryAdapter 단위 테스트")
class SellerBusinessInfoQueryAdapterTest {

    @Mock private SellerBusinessInfoQueryDslRepository queryDslRepository;

    @Mock private SellerBusinessInfoJpaEntityMapper mapper;

    @InjectMocks private SellerBusinessInfoQueryAdapter queryAdapter;

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
            SellerBusinessInfoId id = SellerBusinessInfoId.of(1L);
            SellerBusinessInfoJpaEntity entity = SellerBusinessInfoJpaEntityFixtures.activeEntity();
            SellerBusinessInfo domain = SellerFixtures.activeSellerBusinessInfo();

            given(queryDslRepository.findById(1L)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SellerBusinessInfo> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            SellerBusinessInfoId id = SellerBusinessInfoId.of(999L);
            given(queryDslRepository.findById(999L)).willReturn(Optional.empty());

            // when
            Optional<SellerBusinessInfo> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findBySellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findBySellerId 메서드 테스트")
    class FindBySellerIdTest {

        @Test
        @DisplayName("존재하는 셀러 ID로 조회 시 Domain을 반환합니다")
        void findBySellerId_WithExistingSellerId_ReturnsDomain() {
            // given
            SellerId sellerId = SellerId.of(1L);
            SellerBusinessInfoJpaEntity entity = SellerBusinessInfoJpaEntityFixtures.activeEntity();
            SellerBusinessInfo domain = SellerFixtures.activeSellerBusinessInfo();

            given(queryDslRepository.findBySellerId(1L)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SellerBusinessInfo> result = queryAdapter.findBySellerId(sellerId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 셀러 ID로 조회 시 빈 Optional을 반환합니다")
        void findBySellerId_WithNonExistingSellerId_ReturnsEmpty() {
            // given
            SellerId sellerId = SellerId.of(999L);
            given(queryDslRepository.findBySellerId(999L)).willReturn(Optional.empty());

            // when
            Optional<SellerBusinessInfo> result = queryAdapter.findBySellerId(sellerId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. existsBySellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsBySellerId 메서드 테스트")
    class ExistsBySellerIdTest {

        @Test
        @DisplayName("존재하는 셀러 ID로 조회 시 true를 반환합니다")
        void existsBySellerId_WithExistingSellerId_ReturnsTrue() {
            // given
            SellerId sellerId = SellerId.of(1L);
            given(queryDslRepository.existsBySellerId(1L)).willReturn(true);

            // when
            boolean result = queryAdapter.existsBySellerId(sellerId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 셀러 ID로 조회 시 false를 반환합니다")
        void existsBySellerId_WithNonExistingSellerId_ReturnsFalse() {
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
    // 4. existsByRegistrationNumber 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsByRegistrationNumber 메서드 테스트")
    class ExistsByRegistrationNumberTest {

        @Test
        @DisplayName("존재하는 사업자등록번호로 조회 시 true를 반환합니다")
        void existsByRegistrationNumber_WithExistingNumber_ReturnsTrue() {
            // given
            String registrationNumber = "123-45-67890";
            given(queryDslRepository.existsByRegistrationNumber(registrationNumber))
                    .willReturn(true);

            // when
            boolean result = queryAdapter.existsByRegistrationNumber(registrationNumber);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 사업자등록번호로 조회 시 false를 반환합니다")
        void existsByRegistrationNumber_WithNonExistingNumber_ReturnsFalse() {
            // given
            String registrationNumber = "999-99-99999";
            given(queryDslRepository.existsByRegistrationNumber(registrationNumber))
                    .willReturn(false);

            // when
            boolean result = queryAdapter.existsByRegistrationNumber(registrationNumber);

            // then
            assertThat(result).isFalse();
        }
    }

    // ========================================================================
    // 5. existsByRegistrationNumberExcluding 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsByRegistrationNumberExcluding 메서드 테스트")
    class ExistsByRegistrationNumberExcludingTest {

        @Test
        @DisplayName("특정 셀러 ID 제외하고 사업자등록번호 존재 확인 시 true를 반환합니다")
        void existsByRegistrationNumberExcluding_WithExistingNumber_ReturnsTrue() {
            // given
            String registrationNumber = "123-45-67890";
            SellerId excludeId = SellerId.of(1L);
            given(queryDslRepository.existsByRegistrationNumberExcluding(registrationNumber, 1L))
                    .willReturn(true);

            // when
            boolean result =
                    queryAdapter.existsByRegistrationNumberExcluding(registrationNumber, excludeId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("특정 셀러 ID 제외하고 사업자등록번호가 없으면 false를 반환합니다")
        void existsByRegistrationNumberExcluding_WithNonExistingNumber_ReturnsFalse() {
            // given
            String registrationNumber = "123-45-67890";
            SellerId excludeId = SellerId.of(1L);
            given(queryDslRepository.existsByRegistrationNumberExcluding(registrationNumber, 1L))
                    .willReturn(false);

            // when
            boolean result =
                    queryAdapter.existsByRegistrationNumberExcluding(registrationNumber, excludeId);

            // then
            assertThat(result).isFalse();
        }
    }
}
