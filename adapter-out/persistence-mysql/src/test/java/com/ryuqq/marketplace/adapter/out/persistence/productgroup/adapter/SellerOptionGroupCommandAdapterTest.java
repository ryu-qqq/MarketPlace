package com.ryuqq.marketplace.adapter.out.persistence.productgroup.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.ProductGroupJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper.ProductGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.SellerOptionGroupJpaRepository;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
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
 * SellerOptionGroupCommandAdapterTest - 셀러 옵션 그룹 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SellerOptionGroupCommandAdapter 단위 테스트")
class SellerOptionGroupCommandAdapterTest {

    @Mock private SellerOptionGroupJpaRepository repository;

    @Mock private ProductGroupJpaEntityMapper mapper;

    @InjectMocks private SellerOptionGroupCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("SellerOptionGroup을 Entity로 변환 후 저장하고 ID를 반환합니다")
        void persist_WithValidSellerOptionGroup_SavesAndReturnsId() {
            // given
            SellerOptionGroup domain = ProductGroupFixtures.defaultSellerOptionGroup();
            SellerOptionGroupJpaEntity entityToSave =
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntity(1L);
            SellerOptionGroupJpaEntity savedEntity =
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntityWithId(10L, 1L);

            given(mapper.toOptionGroupEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isEqualTo(10L);
            then(mapper).should().toOptionGroupEntity(domain);
            then(repository).should().save(entityToSave);
        }

        @Test
        @DisplayName("캐노니컬 매핑된 SellerOptionGroup을 저장합니다")
        void persist_WithMappedSellerOptionGroup_Saves() {
            // given
            SellerOptionGroup domain = ProductGroupFixtures.mappedSellerOptionGroup();
            SellerOptionGroupJpaEntity entityToSave =
                    ProductGroupJpaEntityFixtures.mappedOptionGroupEntity(1L, 10L);
            SellerOptionGroupJpaEntity savedEntity =
                    ProductGroupJpaEntityFixtures.mappedOptionGroupEntityWithId(20L, 1L, 10L);

            given(mapper.toOptionGroupEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isEqualTo(20L);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            SellerOptionGroup domain = ProductGroupFixtures.defaultSellerOptionGroup();
            SellerOptionGroupJpaEntity entity =
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntity(1L);
            SellerOptionGroupJpaEntity savedEntity =
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntityWithId(5L, 1L);

            given(mapper.toOptionGroupEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(times(1)).toOptionGroupEntity(domain);
        }
    }

    // ========================================================================
    // 2. persistAll 테스트
    // ========================================================================

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("여러 SellerOptionGroup을 일괄 저장합니다")
        void persistAll_WithMultipleGroups_SavesAll() {
            // given
            SellerOptionGroup group1 = ProductGroupFixtures.defaultSellerOptionGroup();
            SellerOptionGroup group2 = ProductGroupFixtures.mappedSellerOptionGroup();
            List<SellerOptionGroup> groups = List.of(group1, group2);

            SellerOptionGroupJpaEntity entity1 =
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntity(1L);
            SellerOptionGroupJpaEntity entity2 =
                    ProductGroupJpaEntityFixtures.mappedOptionGroupEntity(1L, 10L);

            given(mapper.toOptionGroupEntity(group1)).willReturn(entity1);
            given(mapper.toOptionGroupEntity(group2)).willReturn(entity2);
            given(repository.saveAll(List.of(entity1, entity2)))
                    .willReturn(List.of(entity1, entity2));

            // when
            commandAdapter.persistAll(groups);

            // then
            then(mapper).should().toOptionGroupEntity(group1);
            then(mapper).should().toOptionGroupEntity(group2);
            then(repository).should().saveAll(List.of(entity1, entity2));
        }

        @Test
        @DisplayName("빈 리스트로 persistAll 호출 시 saveAll을 빈 리스트로 호출합니다")
        void persistAll_WithEmptyList_CallsSaveAllWithEmpty() {
            // given
            List<SellerOptionGroup> emptyGroups = List.of();
            given(repository.saveAll(List.of())).willReturn(List.of());

            // when
            commandAdapter.persistAll(emptyGroups);

            // then
            then(repository).should().saveAll(List.of());
        }

        @Test
        @DisplayName("단일 항목 리스트도 정상 처리합니다")
        void persistAll_WithSingleGroup_SavesSuccessfully() {
            // given
            SellerOptionGroup group = ProductGroupFixtures.defaultSellerOptionGroup();
            SellerOptionGroupJpaEntity entity =
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntity(1L);

            given(mapper.toOptionGroupEntity(group)).willReturn(entity);
            given(repository.saveAll(List.of(entity))).willReturn(List.of(entity));

            // when
            commandAdapter.persistAll(List.of(group));

            // then
            then(repository).should().saveAll(List.of(entity));
        }
    }
}
