package com.ryuqq.marketplace.adapter.out.persistence.product.adapter;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.product.ProductJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductOptionMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.product.mapper.ProductJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.product.repository.ProductOptionMappingJpaRepository;
import com.ryuqq.marketplace.domain.product.ProductFixtures;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ProductOptionMappingCommandAdapterTest - 상품 옵션 매핑 Command Adapter 단위 테스트.
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
@DisplayName("ProductOptionMappingCommandAdapter 단위 테스트")
class ProductOptionMappingCommandAdapterTest {

    @Mock private ProductOptionMappingJpaRepository repository;

    @Mock private ProductJpaEntityMapper mapper;

    @InjectMocks private ProductOptionMappingCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("Domain을 Entity로 변환 후 저장합니다")
        void persist_WithValidDomain_SavesEntity() {
            // given
            ProductOptionMapping domain = ProductFixtures.defaultOptionMapping();
            ProductOptionMappingJpaEntity entity =
                    ProductJpaEntityFixtures.defaultOptionMappingEntity();

            given(mapper.toMappingEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should().toMappingEntity(domain);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            ProductOptionMapping domain = ProductFixtures.defaultOptionMapping();
            ProductOptionMappingJpaEntity entity =
                    ProductJpaEntityFixtures.defaultOptionMappingEntity();

            given(mapper.toMappingEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(org.mockito.Mockito.times(1)).toMappingEntity(domain);
        }

        @Test
        @DisplayName("특정 productId와 sellerOptionValueId를 가진 매핑을 저장합니다")
        void persist_WithSpecificIds_SavesCorrectly() {
            // given
            ProductOptionMapping domain = ProductFixtures.optionMapping(10L, 200L);
            ProductOptionMappingJpaEntity entity =
                    ProductJpaEntityFixtures.optionMappingEntity(10L, 200L);

            given(mapper.toMappingEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should().toMappingEntity(domain);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("Repository의 save 메서드가 정확히 한 번 호출됩니다")
        void persist_CallsRepositorySaveOnce() {
            // given
            ProductOptionMapping domain = ProductFixtures.defaultOptionMapping();
            ProductOptionMappingJpaEntity entity =
                    ProductJpaEntityFixtures.defaultOptionMappingEntity();

            given(mapper.toMappingEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(repository).should(org.mockito.Mockito.times(1)).save(entity);
        }
    }
}
