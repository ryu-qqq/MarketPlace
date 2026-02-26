package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.ProductIntelligenceJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.ProductProfileJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.mapper.ProductProfileJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.repository.ProductProfileJpaRepository;
import com.ryuqq.marketplace.domain.productintelligence.ProductIntelligenceFixtures;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ProductProfileCommandAdapterTest - 상품 프로파일 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductProfileCommandAdapter 단위 테스트")
class ProductProfileCommandAdapterTest {

    @Mock private ProductProfileJpaRepository repository;

    @Mock private ProductProfileJpaEntityMapper mapper;

    @InjectMocks private ProductProfileCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("ProductProfile을 Entity로 변환 후 저장하고 ID를 반환합니다")
        void persist_WithValidProductProfile_SavesAndReturnsId() {
            // given
            ProductProfile domain = ProductIntelligenceFixtures.pendingProductProfile();
            ProductProfileJpaEntity entityToSave =
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntity();
            ProductProfileJpaEntity savedEntity =
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntity(100L, 100L);

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
        @DisplayName("COMPLETED 상태 ProductProfile을 저장합니다")
        void persist_WithCompletedProductProfile_Saves() {
            // given
            ProductProfile domain = ProductIntelligenceFixtures.completedProductProfile();
            ProductProfileJpaEntity entityToSave =
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntity();
            ProductProfileJpaEntity savedEntity =
                    ProductIntelligenceJpaEntityFixtures.completedProfileEntity(1L, 100L);

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
            ProductProfile domain = ProductIntelligenceFixtures.pendingProductProfile();
            ProductProfileJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntity(1L, 100L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(times(1)).toEntity(domain);
        }

        @Test
        @DisplayName("저장 후 반환된 Entity의 ID를 반환합니다")
        void persist_ReturnsIdFromSavedEntity() {
            // given
            Long expectedId = 42L;
            ProductProfile domain = ProductIntelligenceFixtures.pendingProductProfile();
            ProductProfileJpaEntity entityToSave =
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntity();
            ProductProfileJpaEntity savedEntity =
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntity(expectedId, 100L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isEqualTo(expectedId);
        }
    }
}
