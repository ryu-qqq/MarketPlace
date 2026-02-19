package com.ryuqq.marketplace.adapter.out.persistence.productgroup.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.ProductGroupJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper.ProductGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.ProductGroupJpaRepository;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ProductGroupCommandAdapterTest - 상품 그룹 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductGroupCommandAdapter 단위 테스트")
class ProductGroupCommandAdapterTest {

    @Mock private ProductGroupJpaRepository jpaRepository;

    @Mock private ProductGroupJpaEntityMapper mapper;

    @InjectMocks private ProductGroupCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("ProductGroup을 Entity로 변환 후 저장하고 ID를 반환합니다")
        void persist_WithValidProductGroup_SavesAndReturnsId() {
            // given
            ProductGroup domain = ProductGroupFixtures.newProductGroup();
            ProductGroupJpaEntity entityToSave = ProductGroupJpaEntityFixtures.newEntity();
            ProductGroupJpaEntity savedEntity = ProductGroupJpaEntityFixtures.activeEntity(100L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isEqualTo(100L);
            then(mapper).should().toEntity(domain);
            then(jpaRepository).should().save(entityToSave);
        }

        @Test
        @DisplayName("ACTIVE 상태 ProductGroup을 저장합니다")
        void persist_WithActiveProductGroup_Saves() {
            // given
            ProductGroup domain = ProductGroupFixtures.activeProductGroup();
            ProductGroupJpaEntity entityToSave = ProductGroupJpaEntityFixtures.newEntity();
            ProductGroupJpaEntity savedEntity = ProductGroupJpaEntityFixtures.activeEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("DRAFT 상태 ProductGroup을 저장합니다")
        void persist_WithDraftProductGroup_Saves() {
            // given
            ProductGroup domain = ProductGroupFixtures.draftProductGroup(1L);
            ProductGroupJpaEntity entityToSave = ProductGroupJpaEntityFixtures.draftEntity();
            ProductGroupJpaEntity savedEntity = ProductGroupJpaEntityFixtures.activeEntity(2L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            ProductGroup domain = ProductGroupFixtures.newProductGroup();
            ProductGroupJpaEntity entity = ProductGroupJpaEntityFixtures.activeEntity();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(jpaRepository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(times(1)).toEntity(domain);
        }
    }
}
