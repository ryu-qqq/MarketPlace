package com.ryuqq.marketplace.adapter.out.persistence.brandpreset.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.BrandPresetJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.entity.BrandPresetJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.mapper.BrandPresetJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.repository.BrandPresetJpaRepository;
import com.ryuqq.marketplace.domain.brandpreset.BrandPresetFixtures;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
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
 * BrandPresetCommandAdapterTest - BrandPreset 커맨드 어댑터 단위 테스트.
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
@DisplayName("BrandPresetCommandAdapter 단위 테스트")
class BrandPresetCommandAdapterTest {

    @Mock private BrandPresetJpaRepository repository;

    @Mock private BrandPresetJpaEntityMapper mapper;

    @InjectMocks private BrandPresetCommandAdapter adapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("BrandPreset을 저장하고 생성된 ID를 반환합니다")
        void persist_WithNewBrandPreset_ReturnsGeneratedId() {
            // given
            BrandPreset domain = BrandPresetFixtures.newBrandPreset();
            BrandPresetJpaEntity entity = BrandPresetJpaEntityFixtures.newEntity();
            BrandPresetJpaEntity savedEntity = BrandPresetJpaEntityFixtures.activeEntity(1L);

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
        @DisplayName("기존 BrandPreset을 업데이트하고 ID를 반환합니다")
        void persist_WithExistingBrandPreset_ReturnsId() {
            // given
            BrandPreset domain = BrandPresetFixtures.activeBrandPreset(1L);
            BrandPresetJpaEntity entity = BrandPresetJpaEntityFixtures.activeEntity(1L);
            BrandPresetJpaEntity savedEntity = BrandPresetJpaEntityFixtures.activeEntity(1L);

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
        @DisplayName("BrandPreset 목록을 저장하고 생성된 ID 목록을 반환합니다")
        void persistAll_WithBrandPresetList_ReturnsGeneratedIds() {
            // given
            BrandPreset domain1 = BrandPresetFixtures.newBrandPreset();
            BrandPreset domain2 = BrandPresetFixtures.newBrandPreset();
            List<BrandPreset> domains = List.of(domain1, domain2);

            BrandPresetJpaEntity entity1 = BrandPresetJpaEntityFixtures.newEntity();
            BrandPresetJpaEntity entity2 = BrandPresetJpaEntityFixtures.newEntity();
            List<BrandPresetJpaEntity> entities = List.of(entity1, entity2);

            BrandPresetJpaEntity savedEntity1 = BrandPresetJpaEntityFixtures.activeEntity(1L);
            BrandPresetJpaEntity savedEntity2 = BrandPresetJpaEntityFixtures.activeEntity(2L);
            List<BrandPresetJpaEntity> savedEntities = List.of(savedEntity1, savedEntity2);

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
        @DisplayName("빈 BrandPreset 목록을 저장하면 빈 ID 목록을 반환합니다")
        void persistAll_WithEmptyList_ReturnsEmptyList() {
            // given
            List<BrandPreset> domains = List.of();
            List<BrandPresetJpaEntity> entities = List.of();
            List<BrandPresetJpaEntity> savedEntities = List.of();

            given(repository.saveAll(entities)).willReturn(savedEntities);

            // when
            List<Long> resultIds = adapter.persistAll(domains);

            // then
            assertThat(resultIds).isEmpty();
            then(repository).should(times(1)).saveAll(entities);
        }
    }
}
