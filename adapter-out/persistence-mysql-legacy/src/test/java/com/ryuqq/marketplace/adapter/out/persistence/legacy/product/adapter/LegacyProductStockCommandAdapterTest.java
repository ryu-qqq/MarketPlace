package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductStockEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductStockJpaRepository;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import com.ryuqq.marketplace.domain.legacy.product.id.LegacyProductId;
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
 * LegacyProductStockCommandAdapterTest - 레거시 상품 재고 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductStockCommandAdapter 단위 테스트")
class LegacyProductStockCommandAdapterTest {

    @Mock private LegacyProductStockJpaRepository repository;

    @Mock private LegacyProductCommandEntityMapper mapper;

    @InjectMocks private LegacyProductStockCommandAdapter commandAdapter;

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("상품 ID와 재고 수량으로 재고를 저장합니다")
        void persist_WithValidProductIdAndQuantity_SavesSuccessfully() {
            // given
            LegacyProductId productId = LegacyProductId.of(1L);
            int stockQuantity = 50;
            LegacyProductStockEntity entity = LegacyProductStockEntity.create(1L, 50);

            given(mapper.toEntity(productId, stockQuantity)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(productId, stockQuantity);

            // then
            then(mapper).should().toEntity(productId, stockQuantity);
            then(repository).should().save(entity);
        }
    }

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("여러 상품 재고를 일괄 저장합니다")
        void persistAll_WithMultipleProducts_SavesAll() {
            // given
            LegacyProduct product1 =
                    LegacyProduct.forNew(LegacyProductGroupId.of(1L), "N", "Y", 10, List.of());
            LegacyProduct product2 =
                    LegacyProduct.forNew(LegacyProductGroupId.of(1L), "N", "Y", 20, List.of());
            List<LegacyProduct> products = List.of(product1, product2);

            LegacyProductStockEntity entity1 = LegacyProductStockEntity.create(1L, 10);
            LegacyProductStockEntity entity2 = LegacyProductStockEntity.create(2L, 20);

            given(mapper.toStockEntity(product1)).willReturn(entity1);
            given(mapper.toStockEntity(product2)).willReturn(entity2);
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
