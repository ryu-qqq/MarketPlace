package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductJpaRepository;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
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
 * LegacyProductCommandAdapterTest - 레거시 상품 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductCommandAdapter 단위 테스트")
class LegacyProductCommandAdapterTest {

    @Mock private LegacyProductJpaRepository repository;

    @Mock private LegacyProductCommandEntityMapper mapper;

    @InjectMocks private LegacyProductCommandAdapter commandAdapter;

    private LegacyProduct buildProduct() {
        return LegacyProduct.forNew(LegacyProductGroupId.of(1L), "N", "Y", 10, List.of());
    }

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("단일 상품을 저장하고 ID를 반환합니다")
        void persist_WithValidProduct_ReturnsSavedId() {
            // given
            LegacyProduct product = buildProduct();
            LegacyProductEntity entity = LegacyProductEntity.create(null, 1L, "N", "Y", "N");
            LegacyProductEntity savedEntity = LegacyProductEntity.create(100L, 1L, "N", "Y", "N");

            given(mapper.toEntity(product)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            Long result = commandAdapter.persist(product);

            // then
            assertThat(result).isEqualTo(100L);
            then(mapper).should().toEntity(product);
            then(repository).should().save(entity);
        }
    }

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("여러 상품을 일괄 저장합니다")
        void persistAll_WithMultipleProducts_SavesAll() {
            // given
            LegacyProduct product1 = buildProduct();
            LegacyProduct product2 = buildProduct();
            List<LegacyProduct> products = List.of(product1, product2);

            LegacyProductEntity entity1 = LegacyProductEntity.create(null, 1L, "N", "Y", "N");
            LegacyProductEntity entity2 = LegacyProductEntity.create(null, 1L, "N", "Y", "N");

            given(mapper.toEntity(product1)).willReturn(entity1);
            given(mapper.toEntity(product2)).willReturn(entity2);
            given(repository.saveAll(anyList())).willReturn(List.of(entity1, entity2));

            // when
            commandAdapter.persistAll(products);

            // then
            then(repository).should().saveAll(anyList());
        }

        @Test
        @DisplayName("빈 목록 저장 시 saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            given(repository.saveAll(any())).willReturn(List.of());

            // when
            commandAdapter.persistAll(List.of());

            // then
            then(repository).should().saveAll(any());
        }
    }
}
