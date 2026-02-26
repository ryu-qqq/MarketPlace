package com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.InboundProductJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.entity.InboundProductJpaEntity;
import com.ryuqq.marketplace.domain.inboundproduct.InboundProductFixtures;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * InboundProductJpaEntityMapperTest - InboundProduct Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("InboundProductJpaEntityMapper 단위 테스트")
class InboundProductJpaEntityMapperTest {

    private InboundProductJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InboundProductJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("RECEIVED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithReceivedProduct_ConvertsCorrectly() {
            // given
            InboundProduct domain = InboundProductFixtures.receivedProduct();

            // when
            InboundProductJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getInboundSourceId()).isEqualTo(domain.inboundSourceId());
            assertThat(entity.getExternalProductCode())
                    .isEqualTo(domain.externalProductCodeValue());
            assertThat(entity.getExternalBrandCode()).isEqualTo(domain.externalBrandCode());
            assertThat(entity.getExternalCategoryCode()).isEqualTo(domain.externalCategoryCode());
            assertThat(entity.getInternalBrandId()).isNull();
            assertThat(entity.getInternalCategoryId()).isNull();
            assertThat(entity.getInternalProductGroupId()).isNull();
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerId());
            assertThat(entity.getStatus()).isEqualTo(domain.status().name());
            assertThat(entity.getResolvedShippingPolicyId()).isNull();
            assertThat(entity.getResolvedRefundPolicyId()).isNull();
            assertThat(entity.getResolvedNoticeCategoryId()).isNull();
        }

        @Test
        @DisplayName("MAPPED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithMappedProduct_ConvertsCorrectly() {
            // given
            InboundProduct domain = InboundProductFixtures.mappedProduct();

            // when
            InboundProductJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getInternalBrandId()).isEqualTo(domain.internalBrandId());
            assertThat(entity.getInternalCategoryId()).isEqualTo(domain.internalCategoryId());
            assertThat(entity.getStatus()).isEqualTo("MAPPED");
            assertThat(entity.getResolvedShippingPolicyId())
                    .isEqualTo(domain.resolvedShippingPolicyId());
            assertThat(entity.getResolvedRefundPolicyId())
                    .isEqualTo(domain.resolvedRefundPolicyId());
            assertThat(entity.getResolvedNoticeCategoryId())
                    .isEqualTo(domain.resolvedNoticeCategoryId());
        }

        @Test
        @DisplayName("CONVERTED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithConvertedProduct_ConvertsCorrectly() {
            // given
            InboundProduct domain = InboundProductFixtures.convertedProduct();

            // when
            InboundProductJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getInternalProductGroupId())
                    .isEqualTo(domain.internalProductGroupId());
            assertThat(entity.getStatus()).isEqualTo("CONVERTED");
        }

        @Test
        @DisplayName("신규 Domain(ID null)을 Entity로 변환합니다")
        void toEntity_WithNewProduct_ConvertsCorrectly() {
            // given
            InboundProduct domain = InboundProductFixtures.newInboundProduct();

            // when
            InboundProductJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getStatus()).isEqualTo("RECEIVED");
            assertThat(entity.getExternalProductCode())
                    .isEqualTo(InboundProductFixtures.DEFAULT_EXTERNAL_PRODUCT_CODE);
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("RECEIVED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithReceivedEntity_ConvertsCorrectly() {
            // given
            InboundProductJpaEntity entity = InboundProductJpaEntityFixtures.receivedEntity(1L);

            // when
            InboundProduct domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.inboundSourceId()).isEqualTo(entity.getInboundSourceId());
            assertThat(domain.externalProductCodeValue())
                    .isEqualTo(entity.getExternalProductCode());
            assertThat(domain.externalBrandCode()).isEqualTo(entity.getExternalBrandCode());
            assertThat(domain.externalCategoryCode()).isEqualTo(entity.getExternalCategoryCode());
            assertThat(domain.internalBrandId()).isNull();
            assertThat(domain.internalCategoryId()).isNull();
            assertThat(domain.internalProductGroupId()).isNull();
            assertThat(domain.sellerId()).isEqualTo(entity.getSellerId());
            assertThat(domain.status().name()).isEqualTo(entity.getStatus());
        }

        @Test
        @DisplayName("MAPPED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithMappedEntity_ConvertsCorrectly() {
            // given
            InboundProductJpaEntity entity = InboundProductJpaEntityFixtures.mappedEntity(2L);

            // when
            InboundProduct domain = mapper.toDomain(entity);

            // then
            assertThat(domain.internalBrandId()).isEqualTo(entity.getInternalBrandId());
            assertThat(domain.internalCategoryId()).isEqualTo(entity.getInternalCategoryId());
            assertThat(domain.status().name()).isEqualTo("MAPPED");
            assertThat(domain.resolvedShippingPolicyId())
                    .isEqualTo(entity.getResolvedShippingPolicyId());
            assertThat(domain.resolvedRefundPolicyId())
                    .isEqualTo(entity.getResolvedRefundPolicyId());
            assertThat(domain.resolvedNoticeCategoryId())
                    .isEqualTo(entity.getResolvedNoticeCategoryId());
        }

        @Test
        @DisplayName("CONVERTED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithConvertedEntity_ConvertsCorrectly() {
            // given
            InboundProductJpaEntity entity = InboundProductJpaEntityFixtures.convertedEntity(3L);

            // when
            InboundProduct domain = mapper.toDomain(entity);

            // then
            assertThat(domain.internalProductGroupId())
                    .isEqualTo(entity.getInternalProductGroupId());
            assertThat(domain.status().name()).isEqualTo("CONVERTED");
            assertThat(domain.isConverted()).isTrue();
        }

        @Test
        @DisplayName("외부 브랜드 코드가 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutBrandCode_ConvertsCorrectly() {
            // given
            InboundProductJpaEntity entity =
                    InboundProductJpaEntityFixtures.entityWithoutBrandCode();

            // when - ID가 있어야 하므로 receivedEntity(id)로 생성
            InboundProductJpaEntity entityWithId =
                    InboundProductJpaEntityFixtures.receivedEntity(10L);
            InboundProduct domain = mapper.toDomain(entityWithId);

            // then
            assertThat(domain.idValue()).isNotNull();
        }

        @Test
        @DisplayName("ID가 null인 Entity를 toDomain 시 예외를 발생합니다")
        void toDomain_WithNullId_ThrowsException() {
            // given
            InboundProductJpaEntity entity = InboundProductJpaEntityFixtures.receivedEntity();

            // when & then
            assertThatThrownBy(() -> mapper.toDomain(entity))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("null일 수 없습니다");
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain -> Entity -> Domain 변환 시 데이터가 보존됩니다")
        void roundTrip_DomainToEntityToDomain_PreservesData() {
            // given
            InboundProduct original = InboundProductFixtures.mappedProduct();

            // when
            InboundProductJpaEntity entity = mapper.toEntity(original);
            InboundProduct converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.inboundSourceId()).isEqualTo(original.inboundSourceId());
            assertThat(converted.externalProductCodeValue())
                    .isEqualTo(original.externalProductCodeValue());
            assertThat(converted.externalBrandCode()).isEqualTo(original.externalBrandCode());
            assertThat(converted.externalCategoryCode()).isEqualTo(original.externalCategoryCode());
            assertThat(converted.internalBrandId()).isEqualTo(original.internalBrandId());
            assertThat(converted.internalCategoryId()).isEqualTo(original.internalCategoryId());
            assertThat(converted.sellerId()).isEqualTo(original.sellerId());
            assertThat(converted.status()).isEqualTo(original.status());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            InboundProductJpaEntity original = InboundProductJpaEntityFixtures.convertedEntity(1L);

            // when
            InboundProduct domain = mapper.toDomain(original);
            InboundProductJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getInboundSourceId()).isEqualTo(original.getInboundSourceId());
            assertThat(converted.getExternalProductCode())
                    .isEqualTo(original.getExternalProductCode());
            assertThat(converted.getInternalBrandId()).isEqualTo(original.getInternalBrandId());
            assertThat(converted.getInternalCategoryId())
                    .isEqualTo(original.getInternalCategoryId());
            assertThat(converted.getInternalProductGroupId())
                    .isEqualTo(original.getInternalProductGroupId());
            assertThat(converted.getSellerId()).isEqualTo(original.getSellerId());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
        }
    }
}
