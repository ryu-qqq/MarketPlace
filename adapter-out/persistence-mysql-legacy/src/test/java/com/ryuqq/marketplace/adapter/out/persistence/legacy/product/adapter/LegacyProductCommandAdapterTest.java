package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductJdbcRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductJpaRepository;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
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

    @Mock private LegacyProductJdbcRepository jdbcRepository;

    @InjectMocks private LegacyProductCommandAdapter commandAdapter;

    private Product buildProduct() {
        return Product.forNew(
                ProductGroupId.of(1L),
                SkuCode.of("SKU001"),
                Money.of(50000),
                Money.of(45000),
                10,
                0,
                List.of(),
                Instant.now());
    }

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("단일 상품을 저장하고 ID를 반환합니다")
        void persist_WithValidProduct_ReturnsSavedId() {
            // given
            Product product = buildProduct();
            LegacyProductEntity savedEntity =
                    LegacyProductEntity.create(100L, 1L, "N", "Y", 10, "N");

            given(repository.save(any())).willReturn(savedEntity);

            // when
            Long result = commandAdapter.persist(product);

            // then
            assertThat(result).isEqualTo(100L);
            then(repository).should().save(any());
        }
    }
}
