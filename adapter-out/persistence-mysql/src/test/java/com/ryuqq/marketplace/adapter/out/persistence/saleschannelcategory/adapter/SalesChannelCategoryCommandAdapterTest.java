package com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.SalesChannelCategoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity.SalesChannelCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.mapper.SalesChannelCategoryJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.repository.SalesChannelCategoryJpaRepository;
import com.ryuqq.marketplace.domain.saleschannelcategory.SalesChannelCategoryFixtures;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SalesChannelCategoryCommandAdapterTest - SalesChannelCategory Command Adapter 단위 테스트.
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
@DisplayName("SalesChannelCategoryCommandAdapter 단위 테스트")
class SalesChannelCategoryCommandAdapterTest {

    @Mock private SalesChannelCategoryJpaRepository repository;

    @Mock private SalesChannelCategoryJpaEntityMapper mapper;

    @InjectMocks private SalesChannelCategoryCommandAdapter commandAdapter;

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
            SalesChannelCategory domain =
                    SalesChannelCategoryFixtures.newSalesChannelCategory();
            SalesChannelCategoryJpaEntity entityToSave =
                    SalesChannelCategoryJpaEntityFixtures.newEntity();
            SalesChannelCategoryJpaEntity savedEntity =
                    SalesChannelCategoryJpaEntityFixtures.activeEntity(100L);

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
        @DisplayName("활성 상태 SalesChannelCategory를 저장합니다")
        void persist_WithActiveSalesChannelCategory_Saves() {
            // given
            SalesChannelCategory domain =
                    SalesChannelCategoryFixtures.activeSalesChannelCategory();
            SalesChannelCategoryJpaEntity entityToSave =
                    SalesChannelCategoryJpaEntityFixtures.newEntity();
            SalesChannelCategoryJpaEntity savedEntity =
                    SalesChannelCategoryJpaEntityFixtures.activeEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
            assertThat(savedId).isEqualTo(1L);
        }

        @Test
        @DisplayName("비활성 상태 SalesChannelCategory를 저장합니다")
        void persist_WithInactiveSalesChannelCategory_Saves() {
            // given
            SalesChannelCategory domain =
                    SalesChannelCategoryFixtures.inactiveSalesChannelCategory();
            SalesChannelCategoryJpaEntity entityToSave =
                    SalesChannelCategoryJpaEntityFixtures.inactiveEntity();
            SalesChannelCategoryJpaEntity savedEntity =
                    SalesChannelCategoryJpaEntityFixtures.activeEntity(2L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
            assertThat(savedId).isEqualTo(2L);
        }

        @Test
        @DisplayName("말단 카테고리를 저장합니다")
        void persist_WithLeafCategory_Saves() {
            // given
            SalesChannelCategory domain = SalesChannelCategoryFixtures.leafCategory();
            SalesChannelCategoryJpaEntity entityToSave =
                    SalesChannelCategoryJpaEntityFixtures.leafEntity();
            SalesChannelCategoryJpaEntity savedEntity =
                    SalesChannelCategoryJpaEntityFixtures.activeEntity(3L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
            assertThat(savedId).isEqualTo(3L);
        }

        @Test
        @DisplayName("하위 카테고리를 저장합니다")
        void persist_WithChildCategory_Saves() {
            // given
            SalesChannelCategory domain =
                    SalesChannelCategoryFixtures.newChildCategory(100L);
            SalesChannelCategoryJpaEntity entityToSave =
                    SalesChannelCategoryJpaEntityFixtures.childEntity(100L);
            SalesChannelCategoryJpaEntity savedEntity =
                    SalesChannelCategoryJpaEntityFixtures.activeEntity(4L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
            assertThat(savedId).isEqualTo(4L);
        }
    }
}
