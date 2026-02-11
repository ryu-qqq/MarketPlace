package com.ryuqq.marketplace.adapter.out.persistence.categorymapping.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.CategoryMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.entity.CategoryMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.mapper.CategoryMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.repository.CategoryMappingJpaRepository;
import com.ryuqq.marketplace.domain.categorymapping.CategoryMappingFixtures;
import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CategoryMappingCommandAdapterTest - CategoryMapping Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-005: Domain -> Entity 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryMappingCommandAdapter 단위 테스트")
class CategoryMappingCommandAdapterTest {

    @Mock private CategoryMappingJpaRepository repository;

    @Mock private CategoryMappingJpaEntityMapper mapper;

    @InjectMocks private CategoryMappingCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("Domain을 Entity로 변환 후 저장하고 ID를 반환합니다")
        void persist_WithValidDomain_SavesAndReturnsId() {
            // given
            CategoryMapping domain = CategoryMappingFixtures.newCategoryMapping();
            CategoryMappingJpaEntity entityToSave =
                    CategoryMappingJpaEntityFixtures.newEntity();
            CategoryMappingJpaEntity savedEntity =
                    CategoryMappingJpaEntityFixtures.activeEntity(100L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isEqualTo(100L);
            then(mapper).should().toEntity(domain);
            then(repository).should().save(entityToSave);
        }

        @Test
        @DisplayName("활성 상태 CategoryMapping을 저장합니다")
        void persist_WithActiveCategoryMapping_Saves() {
            // given
            CategoryMapping domain = CategoryMappingFixtures.activeCategoryMapping();
            CategoryMappingJpaEntity entityToSave =
                    CategoryMappingJpaEntityFixtures.newEntity();
            CategoryMappingJpaEntity savedEntity =
                    CategoryMappingJpaEntityFixtures.activeEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("비활성 상태 CategoryMapping을 저장합니다")
        void persist_WithInactiveCategoryMapping_Saves() {
            // given
            CategoryMapping domain = CategoryMappingFixtures.inactiveCategoryMapping();
            CategoryMappingJpaEntity entityToSave =
                    CategoryMappingJpaEntityFixtures.newEntity();
            CategoryMappingJpaEntity savedEntity =
                    CategoryMappingJpaEntityFixtures.activeEntity(2L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            CategoryMapping domain = CategoryMappingFixtures.newCategoryMapping();
            CategoryMappingJpaEntity entity =
                    CategoryMappingJpaEntityFixtures.activeEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(times(1)).toEntity(domain);
        }
    }

    // ========================================================================
    // 2. persistAll 테스트
    // ========================================================================

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("여러 Domain을 Entity로 변환 후 일괄 저장합니다")
        void persistAll_WithMultipleDomains_SavesAll() {
            // given
            CategoryMapping domain1 = CategoryMappingFixtures.activeCategoryMapping();
            CategoryMapping domain2 = CategoryMappingFixtures.inactiveCategoryMapping();
            List<CategoryMapping> domains = List.of(domain1, domain2);

            CategoryMappingJpaEntity entity1 =
                    CategoryMappingJpaEntityFixtures.activeEntity(1L);
            CategoryMappingJpaEntity entity2 =
                    CategoryMappingJpaEntityFixtures.inactiveEntity();

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);
            given(repository.saveAll(List.of(entity1, entity2)))
                    .willReturn(List.of(entity1, entity2));

            // when
            List<Long> savedIds = commandAdapter.persistAll(domains);

            // then
            assertThat(savedIds).hasSize(2);
            then(repository).should().saveAll(List.of(entity1, entity2));
        }

        @Test
        @DisplayName("빈 리스트를 저장해도 saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            List<CategoryMapping> emptyList = List.of();
            given(repository.saveAll(List.of())).willReturn(List.of());

            // when
            List<Long> savedIds = commandAdapter.persistAll(emptyList);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<CategoryMappingJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(repository).should().saveAll(captor.capture());
            assertThat(captor.getValue()).isEmpty();
            assertThat(savedIds).isEmpty();
        }

        @Test
        @DisplayName("각 Domain에 대해 Mapper가 호출됩니다")
        void persistAll_CallsMapperForEachDomain() {
            // given
            CategoryMapping domain1 = CategoryMappingFixtures.activeCategoryMapping();
            CategoryMapping domain2 = CategoryMappingFixtures.inactiveCategoryMapping();
            CategoryMapping domain3 = CategoryMappingFixtures.newCategoryMapping();
            List<CategoryMapping> domains = List.of(domain1, domain2, domain3);

            CategoryMappingJpaEntity entity =
                    CategoryMappingJpaEntityFixtures.activeEntity(1L);
            given(mapper.toEntity(domain1)).willReturn(entity);
            given(mapper.toEntity(domain2)).willReturn(entity);
            given(mapper.toEntity(domain3)).willReturn(entity);
            given(repository.saveAll(org.mockito.ArgumentMatchers.anyList()))
                    .willReturn(List.of(entity, entity, entity));

            // when
            commandAdapter.persistAll(domains);

            // then
            then(mapper)
                    .should(times(3))
                    .toEntity(org.mockito.ArgumentMatchers.any(CategoryMapping.class));
        }
    }

    // ========================================================================
    // 3. deleteAllByPresetId 테스트
    // ========================================================================

    @Nested
    @DisplayName("deleteAllByPresetId 메서드 테스트")
    class DeleteAllByPresetIdTest {

        @Test
        @DisplayName("PresetId로 모든 CategoryMapping을 삭제합니다")
        void deleteAllByPresetId_WithValidPresetId_DeletesAll() {
            // given
            Long presetId = 1L;

            // when
            commandAdapter.deleteAllByPresetId(presetId);

            // then
            then(repository).should().deleteAllByPresetId(presetId);
        }

        @Test
        @DisplayName("존재하지 않는 PresetId로 삭제를 시도해도 에러가 발생하지 않습니다")
        void deleteAllByPresetId_WithNonExistentPresetId_DoesNotThrow() {
            // given
            Long nonExistentPresetId = 999L;

            // when
            commandAdapter.deleteAllByPresetId(nonExistentPresetId);

            // then
            then(repository).should().deleteAllByPresetId(nonExistentPresetId);
        }
    }

    // ========================================================================
    // 4. deleteAllByPresetIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("deleteAllByPresetIds 메서드 테스트")
    class DeleteAllByPresetIdsTest {

        @Test
        @DisplayName("여러 PresetId로 모든 CategoryMapping을 삭제합니다")
        void deleteAllByPresetIds_WithValidPresetIds_DeletesAll() {
            // given
            List<Long> presetIds = List.of(1L, 2L, 3L);

            // when
            commandAdapter.deleteAllByPresetIds(presetIds);

            // then
            then(repository).should().deleteAllByPresetIdIn(presetIds);
        }

        @Test
        @DisplayName("빈 리스트로 삭제를 시도해도 에러가 발생하지 않습니다")
        void deleteAllByPresetIds_WithEmptyList_DoesNotThrow() {
            // given
            List<Long> emptyList = List.of();

            // when
            commandAdapter.deleteAllByPresetIds(emptyList);

            // then
            then(repository).should().deleteAllByPresetIdIn(emptyList);
        }
    }
}
