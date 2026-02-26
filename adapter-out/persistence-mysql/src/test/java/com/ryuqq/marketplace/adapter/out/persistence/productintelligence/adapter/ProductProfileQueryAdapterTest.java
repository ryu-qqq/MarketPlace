package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.ProductIntelligenceJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.ProductProfileJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.mapper.ProductProfileJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.repository.ProductProfileQueryDslRepository;
import com.ryuqq.marketplace.domain.productintelligence.ProductIntelligenceFixtures;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import java.time.Instant;
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
 * ProductProfileQueryAdapterTest - 상품 프로파일 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductProfileQueryAdapter 단위 테스트")
class ProductProfileQueryAdapterTest {

    @Mock private ProductProfileQueryDslRepository queryDslRepository;

    @Mock private ProductProfileJpaEntityMapper mapper;

    @InjectMocks private ProductProfileQueryAdapter queryAdapter;

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
            Long profileId = 1L;
            ProductProfileJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntity(profileId, 100L);
            ProductProfile domain =
                    ProductIntelligenceFixtures.existingPendingProductProfile(profileId, 100L);

            given(queryDslRepository.findById(profileId)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<ProductProfile> result = queryAdapter.findById(profileId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(queryDslRepository).should().findById(profileId);
            then(mapper).should().toDomain(entity);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            Long profileId = 999L;
            given(queryDslRepository.findById(profileId)).willReturn(Optional.empty());

            // when
            Optional<ProductProfile> result = queryAdapter.findById(profileId);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findById(profileId);
            then(mapper).shouldHaveNoInteractions();
        }
    }

    // ========================================================================
    // 2. findLatestByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findLatestByProductGroupId 메서드 테스트")
    class FindLatestByProductGroupIdTest {

        @Test
        @DisplayName("상품그룹 ID로 최신 프로파일을 조회합니다")
        void findLatestByProductGroupId_WithExistingProductGroupId_ReturnsDomain() {
            // given
            Long productGroupId = 100L;
            ProductProfileJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntity(1L, productGroupId);
            ProductProfile domain =
                    ProductIntelligenceFixtures.existingPendingProductProfile(1L, productGroupId);

            given(queryDslRepository.findLatestByProductGroupId(productGroupId))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<ProductProfile> result =
                    queryAdapter.findLatestByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            then(queryDslRepository).should().findLatestByProductGroupId(productGroupId);
        }

        @Test
        @DisplayName("프로파일이 없으면 빈 Optional을 반환합니다")
        void findLatestByProductGroupId_WithNoProfile_ReturnsEmpty() {
            // given
            Long productGroupId = 999L;
            given(queryDslRepository.findLatestByProductGroupId(productGroupId))
                    .willReturn(Optional.empty());

            // when
            Optional<ProductProfile> result =
                    queryAdapter.findLatestByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
            then(mapper).shouldHaveNoInteractions();
        }
    }

    // ========================================================================
    // 3. findLatestActiveByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findLatestActiveByProductGroupId 메서드 테스트")
    class FindLatestActiveByProductGroupIdTest {

        @Test
        @DisplayName("활성 프로파일을 조회합니다 (expiredAt IS NULL)")
        void findLatestActiveByProductGroupId_WithActiveProfile_ReturnsDomain() {
            // given
            Long productGroupId = 100L;
            ProductProfileJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntity(1L, productGroupId);
            ProductProfile domain =
                    ProductIntelligenceFixtures.existingPendingProductProfile(1L, productGroupId);

            given(queryDslRepository.findLatestActiveByProductGroupId(productGroupId))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<ProductProfile> result =
                    queryAdapter.findLatestActiveByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            then(queryDslRepository).should().findLatestActiveByProductGroupId(productGroupId);
        }

        @Test
        @DisplayName("활성 프로파일이 없으면 빈 Optional을 반환합니다")
        void findLatestActiveByProductGroupId_WithNoActiveProfile_ReturnsEmpty() {
            // given
            Long productGroupId = 100L;
            given(queryDslRepository.findLatestActiveByProductGroupId(productGroupId))
                    .willReturn(Optional.empty());

            // when
            Optional<ProductProfile> result =
                    queryAdapter.findLatestActiveByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 4. findLatestCompletedByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findLatestCompletedByProductGroupId 메서드 테스트")
    class FindLatestCompletedByProductGroupIdTest {

        @Test
        @DisplayName("COMPLETED 상태의 최신 프로파일을 조회합니다")
        void findLatestCompletedByProductGroupId_WithCompletedProfile_ReturnsDomain() {
            // given
            Long productGroupId = 100L;
            ProductProfileJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.completedProfileEntity(1L, productGroupId);
            ProductProfile domain = ProductIntelligenceFixtures.completedProductProfile();

            given(queryDslRepository.findLatestCompletedByProductGroupId(productGroupId))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<ProductProfile> result =
                    queryAdapter.findLatestCompletedByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
        }

        @Test
        @DisplayName("COMPLETED 프로파일이 없으면 빈 Optional을 반환합니다")
        void findLatestCompletedByProductGroupId_WithNoCompletedProfile_ReturnsEmpty() {
            // given
            Long productGroupId = 100L;
            given(queryDslRepository.findLatestCompletedByProductGroupId(productGroupId))
                    .willReturn(Optional.empty());

            // when
            Optional<ProductProfile> result =
                    queryAdapter.findLatestCompletedByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 5. findAllByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findAllByProductGroupId 메서드 테스트")
    class FindAllByProductGroupIdTest {

        @Test
        @DisplayName("상품그룹 ID로 모든 프로파일 이력을 조회합니다")
        void findAllByProductGroupId_WithExistingId_ReturnsDomainList() {
            // given
            Long productGroupId = 100L;
            ProductProfileJpaEntity entity1 =
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntity(1L, productGroupId);
            ProductProfileJpaEntity entity2 =
                    ProductIntelligenceJpaEntityFixtures.completedProfileEntity(2L, productGroupId);
            ProductProfile domain1 =
                    ProductIntelligenceFixtures.existingPendingProductProfile(1L, productGroupId);
            ProductProfile domain2 = ProductIntelligenceFixtures.completedProductProfile();

            given(queryDslRepository.findAllByProductGroupId(productGroupId))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<ProductProfile> result = queryAdapter.findAllByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository).should().findAllByProductGroupId(productGroupId);
        }

        @Test
        @DisplayName("프로파일이 없으면 빈 리스트를 반환합니다")
        void findAllByProductGroupId_WithNoProfiles_ReturnsEmptyList() {
            // given
            Long productGroupId = 999L;
            given(queryDslRepository.findAllByProductGroupId(productGroupId)).willReturn(List.of());

            // when
            List<ProductProfile> result = queryAdapter.findAllByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 6. findStuckAnalyzingProfiles 테스트
    // ========================================================================

    @Nested
    @DisplayName("findStuckAnalyzingProfiles 메서드 테스트")
    class FindStuckAnalyzingProfilesTest {

        @Test
        @DisplayName("stuck 상태의 ANALYZING 프로파일 목록을 조회합니다")
        void findStuckAnalyzingProfiles_WithStuckProfiles_ReturnsDomainList() {
            // given
            Instant threshold = Instant.now().minusSeconds(3600);
            int limit = 10;
            ProductProfileJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.stuckAnalyzingProfileEntity(1L, 100L);
            ProductProfile domain =
                    ProductIntelligenceFixtures.existingPendingProductProfile(1L, 100L);

            given(queryDslRepository.findStuckAnalyzingProfiles(threshold, limit))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<ProductProfile> result = queryAdapter.findStuckAnalyzingProfiles(threshold, limit);

            // then
            assertThat(result).hasSize(1);
            then(queryDslRepository).should().findStuckAnalyzingProfiles(threshold, limit);
        }

        @Test
        @DisplayName("stuck 프로파일이 없으면 빈 리스트를 반환합니다")
        void findStuckAnalyzingProfiles_WithNoStuckProfiles_ReturnsEmptyList() {
            // given
            Instant threshold = Instant.now().minusSeconds(3600);
            int limit = 10;
            given(queryDslRepository.findStuckAnalyzingProfiles(threshold, limit))
                    .willReturn(List.of());

            // when
            List<ProductProfile> result = queryAdapter.findStuckAnalyzingProfiles(threshold, limit);

            // then
            assertThat(result).isEmpty();
        }
    }
}
