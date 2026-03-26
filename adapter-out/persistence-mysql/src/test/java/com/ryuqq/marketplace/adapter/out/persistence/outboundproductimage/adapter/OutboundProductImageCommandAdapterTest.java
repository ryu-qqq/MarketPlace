package com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.OutboundProductImageJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.entity.OutboundProductImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.mapper.OutboundProductImageJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.repository.OutboundProductImageJpaRepository;
import com.ryuqq.marketplace.domain.outboundproductimage.OutboundProductImageFixtures;
import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
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
 * OutboundProductImageCommandAdapterTest - Command Adapter 단위 테스트.
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
@DisplayName("OutboundProductImageCommandAdapter 단위 테스트")
class OutboundProductImageCommandAdapterTest {

    @Mock private OutboundProductImageJpaRepository repository;

    @Mock private OutboundProductImageJpaEntityMapper mapper;

    @InjectMocks private OutboundProductImageCommandAdapter commandAdapter;

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
            OutboundProductImage domain = OutboundProductImageFixtures.newThumbnailImage();
            OutboundProductImageJpaEntity entityToSave =
                    OutboundProductImageJpaEntityFixtures.newThumbnailEntity();
            OutboundProductImageJpaEntity savedEntity =
                    OutboundProductImageJpaEntityFixtures.entityWithId(100L);

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
        @DisplayName("THUMBNAIL 타입 이미지를 저장합니다")
        void persist_WithThumbnailImage_Saves() {
            // given
            OutboundProductImage domain = OutboundProductImageFixtures.newThumbnailImage();
            OutboundProductImageJpaEntity entity =
                    OutboundProductImageJpaEntityFixtures.entityWithId(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
            assertThat(savedId).isEqualTo(1L);
        }

        @Test
        @DisplayName("DETAIL 타입 이미지를 저장합니다")
        void persist_WithDetailImage_Saves() {
            // given
            OutboundProductImage domain = OutboundProductImageFixtures.activeDetailImage(2L, 1);
            OutboundProductImageJpaEntity entity =
                    OutboundProductImageJpaEntityFixtures.newDetailEntity();
            OutboundProductImageJpaEntity savedEntity =
                    OutboundProductImageJpaEntityFixtures.entityWithId(2L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            OutboundProductImage domain = OutboundProductImageFixtures.newThumbnailImage();
            OutboundProductImageJpaEntity entity =
                    OutboundProductImageJpaEntityFixtures.entityWithId(1L);

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
            OutboundProductImage domain = OutboundProductImageFixtures.newThumbnailImage();
            OutboundProductImageJpaEntity entity =
                    OutboundProductImageJpaEntityFixtures.entityWithId(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
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
        @DisplayName("여러 Domain을 한 번에 저장하고 ID 목록을 반환합니다")
        void persistAll_WithMultipleDomains_SavesAndReturnsIds() {
            // given
            OutboundProductImage domain1 = OutboundProductImageFixtures.newThumbnailImage();
            OutboundProductImage domain2 = OutboundProductImageFixtures.activeDetailImage(2L, 1);
            OutboundProductImageJpaEntity entity1 =
                    OutboundProductImageJpaEntityFixtures.newThumbnailEntity();
            OutboundProductImageJpaEntity entity2 =
                    OutboundProductImageJpaEntityFixtures.newDetailEntity();
            OutboundProductImageJpaEntity savedEntity1 =
                    OutboundProductImageJpaEntityFixtures.entityWithId(10L);
            OutboundProductImageJpaEntity savedEntity2 =
                    OutboundProductImageJpaEntityFixtures.entityWithId(11L);

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);
            given(repository.saveAll(List.of(entity1, entity2)))
                    .willReturn(List.of(savedEntity1, savedEntity2));

            // when
            List<Long> savedIds = commandAdapter.persistAll(List.of(domain1, domain2));

            // then
            assertThat(savedIds).hasSize(2);
            assertThat(savedIds).containsExactly(10L, 11L);
        }

        @Test
        @DisplayName("빈 목록을 전달하면 빈 ID 목록을 반환합니다")
        void persistAll_WithEmptyList_ReturnsEmptyIds() {
            // given
            given(repository.saveAll(List.of())).willReturn(List.of());

            // when
            List<Long> savedIds = commandAdapter.persistAll(List.of());

            // then
            assertThat(savedIds).isEmpty();
        }

        @Test
        @DisplayName("단일 Domain 목록을 저장합니다")
        void persistAll_WithSingleDomain_SavesAndReturnsOneId() {
            // given
            OutboundProductImage domain = OutboundProductImageFixtures.newThumbnailImage();
            OutboundProductImageJpaEntity entity =
                    OutboundProductImageJpaEntityFixtures.newThumbnailEntity();
            OutboundProductImageJpaEntity savedEntity =
                    OutboundProductImageJpaEntityFixtures.entityWithId(5L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.saveAll(List.of(entity))).willReturn(List.of(savedEntity));

            // when
            List<Long> savedIds = commandAdapter.persistAll(List.of(domain));

            // then
            assertThat(savedIds).hasSize(1);
            assertThat(savedIds).containsExactly(5L);
        }

        @Test
        @DisplayName("각 Domain에 대해 Mapper가 호출됩니다")
        void persistAll_CallsMapperForEachDomain() {
            // given
            OutboundProductImage domain1 = OutboundProductImageFixtures.newThumbnailImage();
            OutboundProductImage domain2 = OutboundProductImageFixtures.activeDetailImage(2L, 1);
            OutboundProductImageJpaEntity entity1 =
                    OutboundProductImageJpaEntityFixtures.entityWithId(1L);
            OutboundProductImageJpaEntity entity2 =
                    OutboundProductImageJpaEntityFixtures.entityWithId(2L);

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);
            given(repository.saveAll(List.of(entity1, entity2)))
                    .willReturn(List.of(entity1, entity2));

            // when
            commandAdapter.persistAll(List.of(domain1, domain2));

            // then
            then(mapper).should(times(1)).toEntity(domain1);
            then(mapper).should(times(1)).toEntity(domain2);
        }

        @Test
        @DisplayName("Repository의 saveAll이 정확히 한 번 호출됩니다")
        void persistAll_CallsRepositorySaveAllOnce() {
            // given
            OutboundProductImage domain = OutboundProductImageFixtures.newThumbnailImage();
            OutboundProductImageJpaEntity entity =
                    OutboundProductImageJpaEntityFixtures.entityWithId(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.saveAll(List.of(entity))).willReturn(List.of(entity));

            // when
            commandAdapter.persistAll(List.of(domain));

            // then
            then(repository).should(times(1)).saveAll(List.of(entity));
        }
    }
}
