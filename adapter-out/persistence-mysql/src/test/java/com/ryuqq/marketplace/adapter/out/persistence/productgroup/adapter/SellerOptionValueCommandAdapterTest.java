package com.ryuqq.marketplace.adapter.out.persistence.productgroup.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.ProductGroupJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionValueJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper.ProductGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.SellerOptionValueJpaRepository;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
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
 * SellerOptionValueCommandAdapterTest - 셀러 옵션 값 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SellerOptionValueCommandAdapter 단위 테스트")
class SellerOptionValueCommandAdapterTest {

    @Mock private SellerOptionValueJpaRepository repository;

    @Mock private ProductGroupJpaEntityMapper mapper;

    @InjectMocks private SellerOptionValueCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("SellerOptionValue를 Entity로 변환 후 저장하고 ID를 반환합니다")
        void persist_WithValidSellerOptionValue_SavesAndReturnsId() {
            // given
            SellerOptionValue domain = ProductGroupFixtures.defaultSellerOptionValue();
            SellerOptionValueJpaEntity entityToSave =
                    ProductGroupJpaEntityFixtures.activeOptionValueEntity(1L);
            SellerOptionValueJpaEntity savedEntity =
                    ProductGroupJpaEntityFixtures.activeOptionValueEntityWithId(100L, 1L);

            given(mapper.toOptionValueEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isEqualTo(100L);
            then(mapper).should().toOptionValueEntity(domain);
            then(repository).should().save(entityToSave);
        }

        @Test
        @DisplayName("캐노니컬 매핑된 SellerOptionValue를 저장합니다")
        void persist_WithMappedSellerOptionValue_Saves() {
            // given
            SellerOptionValue domain = ProductGroupFixtures.mappedSellerOptionValue();
            SellerOptionValueJpaEntity entityToSave =
                    ProductGroupJpaEntityFixtures.mappedOptionValueEntity(1L, 10L);
            SellerOptionValueJpaEntity savedEntity =
                    ProductGroupJpaEntityFixtures.mappedOptionValueEntityWithId(200L, 1L, 10L);

            given(mapper.toOptionValueEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isEqualTo(200L);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            SellerOptionValue domain = ProductGroupFixtures.defaultSellerOptionValue();
            SellerOptionValueJpaEntity entity =
                    ProductGroupJpaEntityFixtures.activeOptionValueEntity(1L);
            SellerOptionValueJpaEntity savedEntity =
                    ProductGroupJpaEntityFixtures.activeOptionValueEntityWithId(50L, 1L);

            given(mapper.toOptionValueEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(times(1)).toOptionValueEntity(domain);
        }
    }

    // ========================================================================
    // 2. persistAll 테스트
    // ========================================================================

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("여러 SellerOptionValue를 일괄 저장하고 ID 목록을 반환합니다")
        void persistAll_WithMultipleValues_SavesAllAndReturnsIds() {
            // given
            SellerOptionValue value1 = ProductGroupFixtures.defaultSellerOptionValue();
            SellerOptionValue value2 = ProductGroupFixtures.mappedSellerOptionValue();
            List<SellerOptionValue> values = List.of(value1, value2);

            SellerOptionValueJpaEntity entity1 =
                    ProductGroupJpaEntityFixtures.activeOptionValueEntity(1L);
            SellerOptionValueJpaEntity entity2 =
                    ProductGroupJpaEntityFixtures.mappedOptionValueEntity(1L, 10L);
            SellerOptionValueJpaEntity saved1 =
                    ProductGroupJpaEntityFixtures.activeOptionValueEntityWithId(101L, 1L);
            SellerOptionValueJpaEntity saved2 =
                    ProductGroupJpaEntityFixtures.activeOptionValueEntityWithId(102L, 1L);

            given(mapper.toOptionValueEntity(value1)).willReturn(entity1);
            given(mapper.toOptionValueEntity(value2)).willReturn(entity2);
            given(repository.saveAll(List.of(entity1, entity2)))
                    .willReturn(List.of(saved1, saved2));

            // when
            List<Long> savedIds = commandAdapter.persistAll(values);

            // then
            assertThat(savedIds).hasSize(2);
            assertThat(savedIds).containsExactly(101L, 102L);
            then(mapper).should().toOptionValueEntity(value1);
            then(mapper).should().toOptionValueEntity(value2);
            then(repository).should().saveAll(List.of(entity1, entity2));
        }

        @Test
        @DisplayName("빈 리스트로 persistAll 호출 시 빈 ID 목록을 반환합니다")
        void persistAll_WithEmptyList_ReturnsEmptyIds() {
            // given
            List<SellerOptionValue> emptyValues = List.of();
            given(repository.saveAll(List.of())).willReturn(List.of());

            // when
            List<Long> savedIds = commandAdapter.persistAll(emptyValues);

            // then
            assertThat(savedIds).isEmpty();
            then(repository).should().saveAll(List.of());
        }

        @Test
        @DisplayName("단일 SellerOptionValue 일괄 저장 시 ID 1개를 반환합니다")
        void persistAll_WithSingleValue_ReturnsSingleId() {
            // given
            SellerOptionValue value = ProductGroupFixtures.defaultSellerOptionValue();
            SellerOptionValueJpaEntity entity =
                    ProductGroupJpaEntityFixtures.activeOptionValueEntity(1L);
            SellerOptionValueJpaEntity saved =
                    ProductGroupJpaEntityFixtures.activeOptionValueEntityWithId(55L, 1L);

            given(mapper.toOptionValueEntity(value)).willReturn(entity);
            given(repository.saveAll(List.of(entity))).willReturn(List.of(saved));

            // when
            List<Long> savedIds = commandAdapter.persistAll(List.of(value));

            // then
            assertThat(savedIds).hasSize(1);
            assertThat(savedIds.get(0)).isEqualTo(55L);
        }
    }
}
