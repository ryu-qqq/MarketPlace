package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.ProductGroupDescriptionJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.ProductGroupDescriptionJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.mapper.ProductGroupDescriptionJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository.ProductGroupDescriptionJpaRepository;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ProductGroupDescriptionCommandAdapterTest - 상품 그룹 상세설명 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductGroupDescriptionCommandAdapter 단위 테스트")
class ProductGroupDescriptionCommandAdapterTest {

    @Mock private ProductGroupDescriptionJpaRepository repository;

    @Mock private ProductGroupDescriptionJpaEntityMapper mapper;

    @InjectMocks private ProductGroupDescriptionCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("ProductGroupDescription을 Entity로 변환 후 저장하고 ID를 반환합니다")
        void persist_WithValidDescription_SavesAndReturnsId() {
            // given
            ProductGroupDescription domain = ProductGroupFixtures.defaultProductGroupDescription();
            ProductGroupDescriptionJpaEntity entityToSave =
                    ProductGroupDescriptionJpaEntityFixtures.newEntity();
            ProductGroupDescriptionJpaEntity savedEntity =
                    ProductGroupDescriptionJpaEntityFixtures.pendingEntity(100L, 1L);

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
        @DisplayName("PENDING 상태 Description을 저장합니다")
        void persist_WithPendingDescription_Saves() {
            // given
            ProductGroupDescription domain = ProductGroupFixtures.defaultProductGroupDescription();
            ProductGroupDescriptionJpaEntity entityToSave =
                    ProductGroupDescriptionJpaEntityFixtures.newEntity();
            ProductGroupDescriptionJpaEntity savedEntity =
                    ProductGroupDescriptionJpaEntityFixtures.pendingEntity(1L, 1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }
    }
}
