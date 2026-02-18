package com.ryuqq.marketplace.adapter.out.persistence.imagevariant.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.ImageVariantJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.entity.ImageVariantJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.mapper.ImageVariantJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.repository.ImageVariantJpaRepository;
import com.ryuqq.marketplace.domain.imagevariant.ImageVariantFixtures;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ImageVariantCommandAdapterTest - 이미지 Variant Command Adapter 단위 테스트.
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
@DisplayName("ImageVariantCommandAdapter 단위 테스트")
class ImageVariantCommandAdapterTest {

    @Mock private ImageVariantJpaRepository repository;

    @Mock private ImageVariantJpaEntityMapper mapper;

    @InjectMocks private ImageVariantCommandAdapter commandAdapter;

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
            ImageVariant domain = ImageVariantFixtures.newSmallWebpVariant();
            ImageVariantJpaEntity entityToSave = ImageVariantJpaEntityFixtures.newSmallWebpEntity();
            ImageVariantJpaEntity savedEntity = ImageVariantJpaEntityFixtures.entityWithId(100L);

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
        @DisplayName("SMALL_WEBP 타입 Variant를 저장합니다")
        void persist_WithSmallWebpVariant_Saves() {
            // given
            ImageVariant domain = ImageVariantFixtures.newSmallWebpVariant();
            ImageVariantJpaEntity entityToSave = ImageVariantJpaEntityFixtures.newSmallWebpEntity();
            ImageVariantJpaEntity savedEntity = ImageVariantJpaEntityFixtures.entityWithId(1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("ORIGINAL_WEBP 타입 Variant를 저장합니다")
        void persist_WithOriginalWebpVariant_Saves() {
            // given
            ImageVariant domain = ImageVariantFixtures.newOriginalWebpVariant();
            ImageVariantJpaEntity entityToSave =
                    ImageVariantJpaEntityFixtures.newOriginalWebpEntity();
            ImageVariantJpaEntity savedEntity = ImageVariantJpaEntityFixtures.entityWithId(2L);

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
            ImageVariant domain = ImageVariantFixtures.newSmallWebpVariant();
            ImageVariantJpaEntity entity = ImageVariantJpaEntityFixtures.entityWithId(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(times(1)).toEntity(domain);
        }

        @Test
        @DisplayName("Repository의 save가 정확히 한 번 호출됩니다")
        void persist_CallsRepositorySaveOnce() {
            // given
            ImageVariant domain = ImageVariantFixtures.newSmallWebpVariant();
            ImageVariantJpaEntity entity = ImageVariantJpaEntityFixtures.entityWithId(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(repository).should(times(1)).save(entity);
        }
    }
}
