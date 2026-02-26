package com.ryuqq.marketplace.adapter.out.persistence.notice.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.notice.NoticeCategoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeFieldJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.notice.mapper.NoticeCategoryJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.notice.repository.NoticeCategoryQueryDslRepository;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.notice.NoticeFixtures;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySearchCriteria;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySortKey;
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
 * NoticeCategoryQueryAdapterTest - 공지사항 카테고리 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-002: QueryAdapter는 QueryDslRepository 사용.
 *
 * <p>PER-ADP-006: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("NoticeCategoryQueryAdapter 단위 테스트")
class NoticeCategoryQueryAdapterTest {

    @Mock private NoticeCategoryQueryDslRepository queryDslRepository;

    @Mock private NoticeCategoryJpaEntityMapper mapper;

    @InjectMocks private NoticeCategoryQueryAdapter queryAdapter;

    private NoticeCategorySearchCriteria createCriteria(
            Boolean active, String searchField, String searchWord) {
        QueryContext<NoticeCategorySortKey> queryContext =
                QueryContext.defaultOf(NoticeCategorySortKey.defaultKey());
        return new NoticeCategorySearchCriteria(active, searchField, searchWord, queryContext);
    }

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 조회 시 Entity를 Domain으로 변환하여 반환합니다")
        void findById_WithExistingId_ReturnsDomain() {
            // given
            Long id = 1L;
            NoticeCategoryId noticeCategoryId = NoticeCategoryId.of(id);
            NoticeCategoryJpaEntity entity = NoticeCategoryJpaEntityFixtures.activeEntity(id);
            List<NoticeFieldJpaEntity> fields = List.of();
            NoticeCategory domain = NoticeFixtures.activeNoticeCategory(id);

            given(queryDslRepository.findById(id)).willReturn(Optional.of(entity));
            given(queryDslRepository.findFieldsByCategoryId(entity.getId())).willReturn(fields);
            given(mapper.toDomain(entity, fields)).willReturn(domain);

            // when
            Optional<NoticeCategory> result = queryAdapter.findById(noticeCategoryId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(queryDslRepository).should().findById(id);
            then(mapper).should().toDomain(entity, fields);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            Long id = 999L;
            NoticeCategoryId noticeCategoryId = NoticeCategoryId.of(id);

            given(queryDslRepository.findById(id)).willReturn(Optional.empty());

            // when
            Optional<NoticeCategory> result = queryAdapter.findById(noticeCategoryId);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findById(id);
            then(mapper).shouldHaveNoInteractions();
        }
    }

    // ========================================================================
    // 2. findByCategoryGroup 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCategoryGroup 메서드 테스트")
    class FindByCategoryGroupTest {

        @Test
        @DisplayName("카테고리 그룹으로 조회 시 Entity를 Domain으로 변환하여 반환합니다")
        void findByCategoryGroup_WithExistingGroup_ReturnsDomain() {
            // given
            CategoryGroup categoryGroup = CategoryGroup.CLOTHING;
            NoticeCategoryJpaEntity entity =
                    NoticeCategoryJpaEntityFixtures.activeEntityWithCode("CLOTHING", "CLOTHING");
            List<NoticeFieldJpaEntity> fields = List.of();
            NoticeCategory domain = NoticeFixtures.activeNoticeCategory();

            given(queryDslRepository.findByTargetCategoryGroup(categoryGroup.name()))
                    .willReturn(Optional.of(entity));
            given(queryDslRepository.findFieldsByCategoryId(entity.getId())).willReturn(fields);
            given(mapper.toDomain(entity, fields)).willReturn(domain);

            // when
            Optional<NoticeCategory> result = queryAdapter.findByCategoryGroup(categoryGroup);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(queryDslRepository).should().findByTargetCategoryGroup(categoryGroup.name());
            then(mapper).should().toDomain(entity, fields);
        }

        @Test
        @DisplayName("존재하지 않는 카테고리 그룹으로 조회 시 빈 Optional을 반환합니다")
        void findByCategoryGroup_WithNonExistingGroup_ReturnsEmpty() {
            // given
            CategoryGroup categoryGroup = CategoryGroup.DIGITAL;

            given(queryDslRepository.findByTargetCategoryGroup(categoryGroup.name()))
                    .willReturn(Optional.empty());

            // when
            Optional<NoticeCategory> result = queryAdapter.findByCategoryGroup(categoryGroup);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findByTargetCategoryGroup(categoryGroup.name());
            then(mapper).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("FURNITURE 카테고리 그룹으로 조회합니다")
        void findByCategoryGroup_WithFurnitureGroup_ReturnsDomain() {
            // given
            CategoryGroup categoryGroup = CategoryGroup.FURNITURE;
            NoticeCategoryJpaEntity entity = NoticeCategoryJpaEntityFixtures.furnitureEntity();
            List<NoticeFieldJpaEntity> fields = List.of();
            NoticeCategory domain = NoticeFixtures.activeNoticeCategory();

            given(queryDslRepository.findByTargetCategoryGroup(categoryGroup.name()))
                    .willReturn(Optional.of(entity));
            given(queryDslRepository.findFieldsByCategoryId(entity.getId())).willReturn(fields);
            given(mapper.toDomain(entity, fields)).willReturn(domain);

            // when
            Optional<NoticeCategory> result = queryAdapter.findByCategoryGroup(categoryGroup);

            // then
            assertThat(result).isPresent();
        }
    }

    // ========================================================================
    // 3. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 조회 시 Entity 리스트를 Domain 리스트로 변환하여 반환합니다")
        void findByCriteria_WithCriteria_ReturnsDomainList() {
            // given
            NoticeCategorySearchCriteria criteria = createCriteria(true, null, null);

            NoticeCategoryJpaEntity entity1 = NoticeCategoryJpaEntityFixtures.activeEntity(1L);
            NoticeCategoryJpaEntity entity2 = NoticeCategoryJpaEntityFixtures.activeEntity(2L);
            List<NoticeCategoryJpaEntity> entities = List.of(entity1, entity2);

            NoticeCategory domain1 = NoticeFixtures.activeNoticeCategory(1L);
            NoticeCategory domain2 = NoticeFixtures.activeNoticeCategory(2L);

            given(queryDslRepository.findByCriteria(criteria)).willReturn(entities);
            given(
                            queryDslRepository.findFieldsByCategoryIds(
                                    List.of(entity1.getId(), entity2.getId())))
                    .willReturn(List.of());
            given(mapper.toDomain(entity1, List.of())).willReturn(domain1);
            given(mapper.toDomain(entity2, List.of())).willReturn(domain2);

            // when
            List<NoticeCategory> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
            then(queryDslRepository).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            NoticeCategorySearchCriteria criteria = createCriteria(false, null, null);

            given(queryDslRepository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<NoticeCategory> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findByCriteria(criteria);
            then(mapper).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("검색 필드와 검색어로 조회합니다")
        void findByCriteria_WithSearchFieldAndWord_ReturnsDomainList() {
            // given
            NoticeCategorySearchCriteria criteria = createCriteria(null, "CODE", "CLOTHING");

            NoticeCategoryJpaEntity entity = NoticeCategoryJpaEntityFixtures.activeEntity(1L);
            NoticeCategory domain = NoticeFixtures.activeNoticeCategory(1L);

            given(queryDslRepository.findByCriteria(criteria)).willReturn(List.of(entity));
            given(queryDslRepository.findFieldsByCategoryIds(List.of(entity.getId())))
                    .willReturn(List.of());
            given(mapper.toDomain(entity, List.of())).willReturn(domain);

            // when
            List<NoticeCategory> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }
    }

    // ========================================================================
    // 4. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 개수를 조회합니다")
        void countByCriteria_WithCriteria_ReturnsCount() {
            // given
            NoticeCategorySearchCriteria criteria = createCriteria(true, null, null);
            long expectedCount = 5L;

            given(queryDslRepository.countByCriteria(criteria)).willReturn(expectedCount);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expectedCount);
            then(queryDslRepository).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            NoticeCategorySearchCriteria criteria = createCriteria(false, null, null);

            given(queryDslRepository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
            then(queryDslRepository).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("빈 조건으로 전체 개수를 조회합니다")
        void countByCriteria_WithEmptyCriteria_ReturnsAllCount() {
            // given
            NoticeCategorySearchCriteria criteria = createCriteria(null, null, null);
            long expectedCount = 10L;

            given(queryDslRepository.countByCriteria(criteria)).willReturn(expectedCount);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expectedCount);
        }
    }
}
