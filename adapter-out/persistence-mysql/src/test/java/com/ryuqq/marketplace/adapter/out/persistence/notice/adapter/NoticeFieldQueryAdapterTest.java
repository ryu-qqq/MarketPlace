package com.ryuqq.marketplace.adapter.out.persistence.notice.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.notice.NoticeFieldJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeFieldJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.notice.mapper.NoticeFieldJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.notice.repository.NoticeFieldJpaRepository;
import com.ryuqq.marketplace.domain.notice.NoticeFixtures;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * NoticeFieldQueryAdapterTest - 공지사항 필드 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-002: QueryAdapter는 JpaRepository 사용.
 *
 * <p>PER-ADP-006: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("NoticeFieldQueryAdapter 단위 테스트")
class NoticeFieldQueryAdapterTest {

    @Mock private NoticeFieldJpaRepository fieldJpaRepository;

    @Mock private NoticeFieldJpaEntityMapper mapper;

    @InjectMocks private NoticeFieldQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findByNoticeCategoryId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByNoticeCategoryId 메서드 테스트")
    class FindByNoticeCategoryIdTest {

        @Test
        @DisplayName("카테고리 ID로 조회 시 정렬된 필드 리스트를 반환합니다")
        void findByNoticeCategoryId_WithExistingCategoryId_ReturnsSortedDomainList() {
            // given
            Long noticeCategoryId = 1L;
            NoticeFieldJpaEntity entity1 =
                    NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(noticeCategoryId);
            NoticeFieldJpaEntity entity2 =
                    NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(noticeCategoryId);
            List<NoticeFieldJpaEntity> entities = List.of(entity1, entity2);

            NoticeField domain1 = NoticeFixtures.activeNoticeField(1L);
            NoticeField domain2 = NoticeFixtures.activeNoticeField(2L);

            given(fieldJpaRepository.findByNoticeCategoryIdOrderBySortOrder(noticeCategoryId))
                    .willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<NoticeField> result = queryAdapter.findByNoticeCategoryId(noticeCategoryId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
            then(fieldJpaRepository)
                    .should()
                    .findByNoticeCategoryIdOrderBySortOrder(noticeCategoryId);
            then(mapper).should().toDomain(entity1);
            then(mapper).should().toDomain(entity2);
        }

        @Test
        @DisplayName("필드가 없는 카테고리 ID로 조회 시 빈 리스트를 반환합니다")
        void findByNoticeCategoryId_WithNoFields_ReturnsEmptyList() {
            // given
            Long noticeCategoryId = 999L;

            given(fieldJpaRepository.findByNoticeCategoryIdOrderBySortOrder(noticeCategoryId))
                    .willReturn(List.of());

            // when
            List<NoticeField> result = queryAdapter.findByNoticeCategoryId(noticeCategoryId);

            // then
            assertThat(result).isEmpty();
            then(fieldJpaRepository)
                    .should()
                    .findByNoticeCategoryIdOrderBySortOrder(noticeCategoryId);
            then(mapper).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("단일 필드만 있는 카테고리를 조회합니다")
        void findByNoticeCategoryId_WithSingleField_ReturnsSingleElementList() {
            // given
            Long noticeCategoryId = 1L;
            NoticeFieldJpaEntity entity =
                    NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(noticeCategoryId);
            NoticeField domain = NoticeFixtures.activeNoticeField();

            given(fieldJpaRepository.findByNoticeCategoryIdOrderBySortOrder(noticeCategoryId))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<NoticeField> result = queryAdapter.findByNoticeCategoryId(noticeCategoryId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }
    }

    // ========================================================================
    // 2. findGroupedByNoticeCategoryIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findGroupedByNoticeCategoryIds 메서드 테스트")
    class FindGroupedByNoticeCategoryIdsTest {

        @Test
        @DisplayName("여러 카테고리 ID로 조회 시 카테고리별로 그룹화된 필드 맵을 반환합니다")
        void findGroupedByNoticeCategoryIds_WithMultipleCategoryIds_ReturnsGroupedMap() {
            // given
            Long categoryId1 = 1L;
            Long categoryId2 = 2L;
            List<Long> categoryIds = List.of(categoryId1, categoryId2);

            NoticeFieldJpaEntity entity1 =
                    NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(categoryId1);
            NoticeFieldJpaEntity entity2 =
                    NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(categoryId1);
            NoticeFieldJpaEntity entity3 =
                    NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(categoryId2);
            List<NoticeFieldJpaEntity> entities = List.of(entity1, entity2, entity3);

            NoticeField domain1 = NoticeFixtures.activeNoticeField(1L);
            NoticeField domain2 = NoticeFixtures.activeNoticeField(2L);
            NoticeField domain3 = NoticeFixtures.activeNoticeField(3L);

            given(fieldJpaRepository.findByNoticeCategoryIdInOrderBySortOrder(categoryIds))
                    .willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);
            given(mapper.toDomain(entity3)).willReturn(domain3);

            // when
            Map<Long, List<NoticeField>> result =
                    queryAdapter.findGroupedByNoticeCategoryIds(categoryIds);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(categoryId1)).hasSize(2);
            assertThat(result.get(categoryId2)).hasSize(1);
            assertThat(result.get(categoryId1)).containsExactly(domain1, domain2);
            assertThat(result.get(categoryId2)).containsExactly(domain3);
            then(fieldJpaRepository).should().findByNoticeCategoryIdInOrderBySortOrder(categoryIds);
        }

        @Test
        @DisplayName("필드가 없는 카테고리 ID 리스트로 조회 시 빈 맵을 반환합니다")
        void findGroupedByNoticeCategoryIds_WithNoFields_ReturnsEmptyMap() {
            // given
            List<Long> categoryIds = List.of(999L);

            given(fieldJpaRepository.findByNoticeCategoryIdInOrderBySortOrder(categoryIds))
                    .willReturn(List.of());

            // when
            Map<Long, List<NoticeField>> result =
                    queryAdapter.findGroupedByNoticeCategoryIds(categoryIds);

            // then
            assertThat(result).isEmpty();
            then(fieldJpaRepository).should().findByNoticeCategoryIdInOrderBySortOrder(categoryIds);
            then(mapper).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("빈 카테고리 ID 리스트로 조회 시 빈 맵을 반환합니다")
        void findGroupedByNoticeCategoryIds_WithEmptyList_ReturnsEmptyMap() {
            // given
            List<Long> emptyCategoryIds = List.of();

            given(fieldJpaRepository.findByNoticeCategoryIdInOrderBySortOrder(emptyCategoryIds))
                    .willReturn(List.of());

            // when
            Map<Long, List<NoticeField>> result =
                    queryAdapter.findGroupedByNoticeCategoryIds(emptyCategoryIds);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("단일 카테고리 ID로 조회 시 하나의 그룹만 반환합니다")
        void findGroupedByNoticeCategoryIds_WithSingleCategoryId_ReturnsSingleGroup() {
            // given
            Long categoryId = 1L;
            List<Long> categoryIds = List.of(categoryId);

            NoticeFieldJpaEntity entity1 =
                    NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(categoryId);
            NoticeFieldJpaEntity entity2 =
                    NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(categoryId);
            List<NoticeFieldJpaEntity> entities = List.of(entity1, entity2);

            NoticeField domain1 = NoticeFixtures.activeNoticeField(1L);
            NoticeField domain2 = NoticeFixtures.activeNoticeField(2L);

            given(fieldJpaRepository.findByNoticeCategoryIdInOrderBySortOrder(categoryIds))
                    .willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            Map<Long, List<NoticeField>> result =
                    queryAdapter.findGroupedByNoticeCategoryIds(categoryIds);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(categoryId)).hasSize(2);
        }

        @Test
        @DisplayName("일부 카테고리만 필드를 가지고 있어도 정상 동작합니다")
        void findGroupedByNoticeCategoryIds_WithPartialFields_WorksCorrectly() {
            // given
            Long categoryId1 = 1L;
            Long categoryId2 = 2L;
            Long categoryId3 = 3L;
            List<Long> categoryIds = List.of(categoryId1, categoryId2, categoryId3);

            NoticeFieldJpaEntity entity1 =
                    NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(categoryId1);
            List<NoticeFieldJpaEntity> entities = List.of(entity1);

            NoticeField domain1 = NoticeFixtures.activeNoticeField(1L);

            given(fieldJpaRepository.findByNoticeCategoryIdInOrderBySortOrder(categoryIds))
                    .willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);

            // when
            Map<Long, List<NoticeField>> result =
                    queryAdapter.findGroupedByNoticeCategoryIds(categoryIds);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(categoryId1)).hasSize(1);
            assertThat(result.get(categoryId2)).isNull();
            assertThat(result.get(categoryId3)).isNull();
        }
    }
}
