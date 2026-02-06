package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.SellerAdminJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper.SellerAdminJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository.SellerAdminQueryDslRepository;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import com.ryuqq.marketplace.domain.selleradmin.query.SellerAdminSearchCriteria;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
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
 * SellerAdminQueryAdapterTest - 셀러 관리자 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SellerAdminQueryAdapter 단위 테스트")
class SellerAdminQueryAdapterTest {

    @Mock private SellerAdminQueryDslRepository queryDslRepository;

    @Mock private SellerAdminJpaEntityMapper mapper;

    @Mock private SellerAdminSearchCriteria criteria;

    @InjectMocks private SellerAdminQueryAdapter queryAdapter;

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
            SellerAdminId sellerAdminId = SellerAdminId.of(SellerAdminJpaEntityFixtures.DEFAULT_ID);
            SellerAdminJpaEntity entity = SellerAdminJpaEntityFixtures.activeEntity();
            SellerAdmin domain = SellerFixtures.activeSellerAdmin();

            given(queryDslRepository.findById(SellerAdminJpaEntityFixtures.DEFAULT_ID))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SellerAdmin> result = queryAdapter.findById(sellerAdminId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            String nonExistingId = "non-existing-id";
            SellerAdminId sellerAdminId = SellerAdminId.of(nonExistingId);
            given(queryDslRepository.findById(nonExistingId)).willReturn(Optional.empty());

            // when
            Optional<SellerAdmin> result = queryAdapter.findById(sellerAdminId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findBySellerIdAndId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findBySellerIdAndId 메서드 테스트")
    class FindBySellerIdAndIdTest {

        @Test
        @DisplayName("셀러 ID와 관리자 ID로 조회 시 Domain을 반환합니다")
        void findBySellerIdAndId_WithValidIds_ReturnsDomain() {
            // given
            SellerId sellerId = SellerId.of(1L);
            SellerAdminId sellerAdminId = SellerAdminId.of(SellerAdminJpaEntityFixtures.DEFAULT_ID);
            SellerAdminJpaEntity entity = SellerAdminJpaEntityFixtures.activeEntity();
            SellerAdmin domain = SellerFixtures.activeSellerAdmin();

            given(
                            queryDslRepository.findBySellerIdAndId(
                                    1L, SellerAdminJpaEntityFixtures.DEFAULT_ID))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SellerAdmin> result =
                    queryAdapter.findBySellerIdAndId(sellerId, sellerAdminId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findBySellerIdAndId_WithNonExistingIds_ReturnsEmpty() {
            // given
            SellerId sellerId = SellerId.of(999L);
            SellerAdminId sellerAdminId = SellerAdminId.of("non-existing-id");

            given(queryDslRepository.findBySellerIdAndId(999L, "non-existing-id"))
                    .willReturn(Optional.empty());

            // when
            Optional<SellerAdmin> result =
                    queryAdapter.findBySellerIdAndId(sellerId, sellerAdminId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findBySellerIdAndIdAndStatuses 테스트
    // ========================================================================

    @Nested
    @DisplayName("findBySellerIdAndIdAndStatuses 메서드 테스트")
    class FindBySellerIdAndIdAndStatusesTest {

        @Test
        @DisplayName("셀러 ID, 관리자 ID, 상태 목록으로 조회 시 Domain을 반환합니다")
        void findBySellerIdAndIdAndStatuses_WithValidParams_ReturnsDomain() {
            // given
            SellerId sellerId = SellerId.of(1L);
            SellerAdminId sellerAdminId = SellerAdminId.of(SellerAdminJpaEntityFixtures.DEFAULT_ID);
            List<SellerAdminStatus> statuses = List.of(SellerAdminStatus.ACTIVE);
            SellerAdminJpaEntity entity = SellerAdminJpaEntityFixtures.activeEntity();
            SellerAdmin domain = SellerFixtures.activeSellerAdmin();

            given(
                            queryDslRepository.findBySellerIdAndIdAndStatuses(
                                    1L, SellerAdminJpaEntityFixtures.DEFAULT_ID, statuses))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SellerAdmin> result =
                    queryAdapter.findBySellerIdAndIdAndStatuses(sellerId, sellerAdminId, statuses);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("조건에 맞지 않으면 빈 Optional을 반환합니다")
        void findBySellerIdAndIdAndStatuses_WithNoMatch_ReturnsEmpty() {
            // given
            SellerId sellerId = SellerId.of(1L);
            SellerAdminId sellerAdminId = SellerAdminId.of(SellerAdminJpaEntityFixtures.DEFAULT_ID);
            List<SellerAdminStatus> statuses = List.of(SellerAdminStatus.SUSPENDED);

            given(
                            queryDslRepository.findBySellerIdAndIdAndStatuses(
                                    1L, SellerAdminJpaEntityFixtures.DEFAULT_ID, statuses))
                    .willReturn(Optional.empty());

            // when
            Optional<SellerAdmin> result =
                    queryAdapter.findBySellerIdAndIdAndStatuses(sellerId, sellerAdminId, statuses);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 4. findByIdAndStatuses 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByIdAndStatuses 메서드 테스트")
    class FindByIdAndStatusesTest {

        @Test
        @DisplayName("관리자 ID와 상태 목록으로 조회 시 Domain을 반환합니다")
        void findByIdAndStatuses_WithValidParams_ReturnsDomain() {
            // given
            SellerAdminId sellerAdminId = SellerAdminId.of(SellerAdminJpaEntityFixtures.DEFAULT_ID);
            List<SellerAdminStatus> statuses = List.of(SellerAdminStatus.ACTIVE);
            SellerAdminJpaEntity entity = SellerAdminJpaEntityFixtures.activeEntity();
            SellerAdmin domain = SellerFixtures.activeSellerAdmin();

            given(
                            queryDslRepository.findByIdAndStatuses(
                                    SellerAdminJpaEntityFixtures.DEFAULT_ID, statuses))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SellerAdmin> result =
                    queryAdapter.findByIdAndStatuses(sellerAdminId, statuses);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("조건에 맞지 않으면 빈 Optional을 반환합니다")
        void findByIdAndStatuses_WithNoMatch_ReturnsEmpty() {
            // given
            SellerAdminId sellerAdminId = SellerAdminId.of(SellerAdminJpaEntityFixtures.DEFAULT_ID);
            List<SellerAdminStatus> statuses = List.of(SellerAdminStatus.REJECTED);

            given(
                            queryDslRepository.findByIdAndStatuses(
                                    SellerAdminJpaEntityFixtures.DEFAULT_ID, statuses))
                    .willReturn(Optional.empty());

            // when
            Optional<SellerAdmin> result =
                    queryAdapter.findByIdAndStatuses(sellerAdminId, statuses);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 5. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 셀러 관리자 목록을 조회합니다")
        void findByCriteria_WithValidCriteria_ReturnsDomainList() {
            // given
            SellerAdminJpaEntity entity1 =
                    SellerAdminJpaEntityFixtures.activeEntity(
                            "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60");
            SellerAdminJpaEntity entity2 =
                    SellerAdminJpaEntityFixtures.activeEntity(
                            "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f61");
            SellerAdmin domain1 = SellerFixtures.activeSellerAdmin();
            SellerAdmin domain2 = SellerFixtures.newSellerAdmin();

            given(queryDslRepository.findByCriteria(criteria))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<SellerAdmin> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            given(queryDslRepository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<SellerAdmin> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 6. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 셀러 관리자 개수를 반환합니다")
        void countByCriteria_WithValidCriteria_ReturnsCount() {
            // given
            given(queryDslRepository.countByCriteria(criteria)).willReturn(5L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            given(queryDslRepository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    // ========================================================================
    // 7. existsByLoginId 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsByLoginId 메서드 테스트")
    class ExistsByLoginIdTest {

        @Test
        @DisplayName("존재하는 로그인 ID로 조회 시 true를 반환합니다")
        void existsByLoginId_WithExistingLoginId_ReturnsTrue() {
            // given
            String loginId = "admin@test.com";
            given(queryDslRepository.existsByLoginId(loginId)).willReturn(true);

            // when
            boolean result = queryAdapter.existsByLoginId(loginId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 로그인 ID로 조회 시 false를 반환합니다")
        void existsByLoginId_WithNonExistingLoginId_ReturnsFalse() {
            // given
            String loginId = "non-existing@test.com";
            given(queryDslRepository.existsByLoginId(loginId)).willReturn(false);

            // when
            boolean result = queryAdapter.existsByLoginId(loginId);

            // then
            assertThat(result).isFalse();
        }
    }
}
