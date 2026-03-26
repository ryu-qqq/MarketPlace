package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductOptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductOptionJpaRepository;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyProductOptionCommandAdapterTest - 레거시 상품 옵션 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductOptionCommandAdapter 단위 테스트")
class LegacyProductOptionCommandAdapterTest {

    @Mock private LegacyProductOptionJpaRepository repository;

    @InjectMocks private LegacyProductOptionCommandAdapter commandAdapter;

    private ProductOptionMapping buildProductOptionMapping() {
        return ProductOptionMapping.forNew(ProductId.of(1L), SellerOptionValueId.of(100L));
    }

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("상품 옵션을 저장합니다")
        void persist_WithValidProductOption_SavesSuccessfully() {
            // given
            ProductOptionMapping mapping = buildProductOptionMapping();
            LegacyProductOptionEntity entity = LegacyProductOptionEntity.create(1L, 100L, 100L, 0L);

            given(repository.save(any())).willReturn(entity);

            // when
            commandAdapter.persist(mapping);

            // then
            then(repository).should().save(any());
        }
    }
}
