package com.ryuqq.marketplace.adapter.out.persistence.categorypreset.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.CategoryPresetJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.entity.CategoryPresetJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.mapper.CategoryPresetJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.repository.CategoryPresetJpaRepository;
import com.ryuqq.marketplace.domain.categorypreset.CategoryPresetFixtures;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CategoryPresetCommandAdapterTest - CategoryPreset 커맨드 어댑터 단위 테스트.
 *
 * <p>PER-ADP-002: Port 인터페이스 구현 + Repository 호출.
 *
 * <p>PER-ADP-003: Mapper를 통한 변환 + 비즈니스 로직 없음.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryPresetCommandAdapter 단위 테스트")
class CategoryPresetCommandAdapterTest {

    @Mock private CategoryPresetJpaRepository repository;

    @Mock private CategoryPresetJpaEntityMapper mapper;

    @InjectMocks private CategoryPresetCommandAdapter adapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("CategoryPreset을 저장하고 생성된 ID를 반환합니다")
        void persist_WithNewCategoryPreset_ReturnsGeneratedId() {
            // given
            CategoryPreset domain = CategoryPresetFixtures.newCategoryPreset();
            CategoryPresetJpaEntity entity = CategoryPresetJpaEntityFixtures.newEntity();
            CategoryPresetJpaEntity savedEntity = CategoryPresetJpaEntityFixtures.activeEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            Long resultId = adapter.persist(domain);

            // then
            assertThat(resultId).isEqualTo(1L);
            then(mapper).should(times(1)).toEntity(domain);
            then(repository).should(times(1)).save(entity);
        }

        @Test
        @DisplayName("기존 CategoryPreset을 업데이트하고 ID를 반환합니다")
        void persist_WithExistingCategoryPreset_ReturnsId() {
            // given
            CategoryPreset domain = CategoryPresetFixtures.activeCategoryPreset(1L);
            CategoryPresetJpaEntity entity = CategoryPresetJpaEntityFixtures.activeEntity(1L);
            CategoryPresetJpaEntity savedEntity = CategoryPresetJpaEntityFixtures.activeEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            Long resultId = adapter.persist(domain);

            // then
            assertThat(resultId).isEqualTo(1L);
            then(mapper).should(times(1)).toEntity(domain);
            then(repository).should(times(1)).save(entity);
        }
    }

    // ========================================================================
    // 2. persistAll 테스트
    // ========================================================================

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("CategoryPreset 목록을 저장하고 생성된 ID 목록을 반환합니다")
        void persistAll_WithCategoryPresetList_ReturnsGeneratedIds() {
            // given
            CategoryPreset domain1 = CategoryPresetFixtures.newCategoryPreset();
            CategoryPreset domain2 = CategoryPresetFixtures.newCategoryPreset();
            List<CategoryPreset> domains = List.of(domain1, domain2);

            CategoryPresetJpaEntity entity1 = CategoryPresetJpaEntityFixtures.newEntity();
            CategoryPresetJpaEntity entity2 = CategoryPresetJpaEntityFixtures.newEntity();
            List<CategoryPresetJpaEntity> entities = List.of(entity1, entity2);

            CategoryPresetJpaEntity savedEntity1 = CategoryPresetJpaEntityFixtures.activeEntity(1L);
            CategoryPresetJpaEntity savedEntity2 = CategoryPresetJpaEntityFixtures.activeEntity(2L);
            List<CategoryPresetJpaEntity> savedEntities = List.of(savedEntity1, savedEntity2);

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);
            given(repository.saveAll(entities)).willReturn(savedEntities);

            // when
            List<Long> resultIds = adapter.persistAll(domains);

            // then
            assertThat(resultIds).hasSize(2);
            assertThat(resultIds).containsExactly(1L, 2L);
            then(mapper).should(times(2)).toEntity(org.mockito.ArgumentMatchers.any());
            then(repository).should(times(1)).saveAll(entities);
        }

        @Test
        @DisplayName("빈 CategoryPreset 목록을 저장하면 빈 ID 목록을 반환합니다")
        void persistAll_WithEmptyList_ReturnsEmptyList() {
            // given
            List<CategoryPreset> domains = List.of();
            List<CategoryPresetJpaEntity> entities = List.of();
            List<CategoryPresetJpaEntity> savedEntities = List.of();

            given(repository.saveAll(entities)).willReturn(savedEntities);

            // when
            List<Long> resultIds = adapter.persistAll(domains);

            // then
            assertThat(resultIds).isEmpty();
            then(repository).should(times(1)).saveAll(entities);
        }
    }
}
