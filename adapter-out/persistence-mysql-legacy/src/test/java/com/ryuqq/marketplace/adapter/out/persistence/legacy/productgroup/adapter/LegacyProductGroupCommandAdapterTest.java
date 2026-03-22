package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.LegacyProductGroupEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.entity.LegacyProductGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.mapper.LegacyProductGroupEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.repository.LegacyProductGroupJdbcRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.repository.LegacyProductGroupJpaRepository;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
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
 * LegacyProductGroupCommandAdapterTest - 레거시 상품 그룹 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductGroupCommandAdapter 단위 테스트")
class LegacyProductGroupCommandAdapterTest {

    @Mock private LegacyProductGroupJpaRepository jpaRepository;

    @Mock private LegacyProductGroupJdbcRepository jdbcRepository;

    @Mock private LegacyProductGroupEntityMapper mapper;

    @InjectMocks private LegacyProductGroupCommandAdapter commandAdapter;

    private ProductGroup buildProductGroup() {
        return ProductGroup.forNew(
                SellerId.of(10L),
                BrandId.of(20L),
                CategoryId.of(30L),
                ShippingPolicyId.of(1L),
                RefundPolicyId.of(1L),
                ProductGroupName.of("테스트 상품"),
                OptionType.SINGLE,
                Instant.now());
    }

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("상품 그룹을 저장하고 ID를 반환합니다")
        void persist_WithValidProductGroup_ReturnsSavedId() {
            // given
            ProductGroup productGroup = buildProductGroup();
            long regularPrice = 50000L;
            long currentPrice = 45000L;

            LegacyProductGroupEntity entity = LegacyProductGroupEntityFixtures.newEntity();
            Long expectedId = 100L;
            LegacyProductGroupEntity savedEntity =
                    LegacyProductGroupEntityFixtures.entityWithId(expectedId);

            given(mapper.toEntity(productGroup, regularPrice, currentPrice)).willReturn(entity);
            given(jpaRepository.save(entity)).willReturn(savedEntity);

            // when
            Long result = commandAdapter.persist(productGroup, regularPrice, currentPrice);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(mapper).should().toEntity(productGroup, regularPrice, currentPrice);
            then(jpaRepository).should().save(entity);
        }

        @Test
        @DisplayName("Mapper가 먼저 호출되고 Repository가 저장합니다")
        void persist_CallsMapperBeforeRepository() {
            // given
            ProductGroup productGroup = buildProductGroup();
            long regularPrice = 50000L;
            long currentPrice = 45000L;

            LegacyProductGroupEntity entity = LegacyProductGroupEntityFixtures.newEntity();
            LegacyProductGroupEntity savedEntity =
                    LegacyProductGroupEntityFixtures.entityWithId(1L);

            given(mapper.toEntity(productGroup, regularPrice, currentPrice)).willReturn(entity);
            given(jpaRepository.save(entity)).willReturn(savedEntity);

            // when
            commandAdapter.persist(productGroup, regularPrice, currentPrice);

            // then (BDD 스타일 검증)
            then(mapper).should().toEntity(productGroup, regularPrice, currentPrice);
            then(jpaRepository).should().save(entity);
        }
    }
}
