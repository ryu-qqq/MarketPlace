package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.OutboundProductJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.entity.OutboundProductJpaEntity;
import com.ryuqq.marketplace.domain.outboundproduct.OutboundProductFixtures;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundproduct.vo.OutboundProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OutboundProductJpaEntityMapperTest - OutboundProduct Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("OutboundProductJpaEntityMapper 단위 테스트")
class OutboundProductJpaEntityMapperTest {

    private OutboundProductJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OutboundProductJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("PENDING 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithPendingProduct_ConvertsCorrectly() {
            // given
            OutboundProduct domain = OutboundProductFixtures.pendingProduct();

            // when
            OutboundProductJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getProductGroupId()).isEqualTo(domain.productGroupIdValue());
            assertThat(entity.getSalesChannelId()).isEqualTo(domain.salesChannelIdValue());
            assertThat(entity.getExternalProductId()).isNull();
            assertThat(entity.getStatus())
                    .isEqualTo(OutboundProductStatus.PENDING_REGISTRATION.name());
        }

        @Test
        @DisplayName("REGISTERED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithRegisteredProduct_ConvertsCorrectly() {
            // given
            OutboundProduct domain = OutboundProductFixtures.registeredProduct();

            // when
            OutboundProductJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getExternalProductId()).isEqualTo(domain.externalProductId());
            assertThat(entity.getStatus()).isEqualTo(OutboundProductStatus.REGISTERED.name());
        }

        @Test
        @DisplayName("REGISTRATION_FAILED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithFailedProduct_ConvertsCorrectly() {
            // given
            OutboundProduct domain = OutboundProductFixtures.failedProduct();

            // when
            OutboundProductJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus())
                    .isEqualTo(OutboundProductStatus.REGISTRATION_FAILED.name());
        }

        @Test
        @DisplayName("신규 Domain (ID null)을 Entity로 변환합니다")
        void toEntity_WithNewProduct_ConvertsCorrectly() {
            // given
            OutboundProduct domain = OutboundProductFixtures.newPendingProduct();

            // when
            OutboundProductJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getProductGroupId()).isEqualTo(domain.productGroupIdValue());
            assertThat(entity.getSalesChannelId()).isEqualTo(domain.salesChannelIdValue());
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("PENDING 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithPendingEntity_ConvertsCorrectly() {
            // given
            OutboundProductJpaEntity entity = OutboundProductJpaEntityFixtures.pendingEntity(1L);

            // when
            OutboundProduct domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.productGroupIdValue()).isEqualTo(entity.getProductGroupId());
            assertThat(domain.salesChannelIdValue()).isEqualTo(entity.getSalesChannelId());
            assertThat(domain.externalProductId()).isNull();
            assertThat(domain.status()).isEqualTo(OutboundProductStatus.PENDING_REGISTRATION);
        }

        @Test
        @DisplayName("REGISTERED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithRegisteredEntity_ConvertsCorrectly() {
            // given
            OutboundProductJpaEntity entity = OutboundProductJpaEntityFixtures.registeredEntity(1L);

            // when
            OutboundProduct domain = mapper.toDomain(entity);

            // then
            assertThat(domain.externalProductId()).isEqualTo(entity.getExternalProductId());
            assertThat(domain.status()).isEqualTo(OutboundProductStatus.REGISTERED);
            assertThat(domain.isRegistered()).isTrue();
        }

        @Test
        @DisplayName("REGISTRATION_FAILED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithFailedEntity_ConvertsCorrectly() {
            // given
            OutboundProductJpaEntity entity = OutboundProductJpaEntityFixtures.failedEntity();
            // failedEntity는 ID null이므로 ID를 가진 엔티티를 직접 생성
            OutboundProductJpaEntity entityWithId =
                    OutboundProductJpaEntity.create(
                            1L,
                            OutboundProductJpaEntityFixtures.DEFAULT_PRODUCT_GROUP_ID,
                            OutboundProductJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                            1L,
                            null,
                            OutboundProductJpaEntityFixtures.STATUS_FAILED,
                            java.time.Instant.now(),
                            java.time.Instant.now());

            // when
            OutboundProduct domain = mapper.toDomain(entityWithId);

            // then
            assertThat(domain.status()).isEqualTo(OutboundProductStatus.REGISTRATION_FAILED);
        }

        @Test
        @DisplayName("ID가 null인 Entity를 변환 시 IllegalStateException이 발생합니다")
        void toDomain_WithNullId_ThrowsIllegalStateException() {
            // given
            OutboundProductJpaEntity entityWithNullId =
                    OutboundProductJpaEntityFixtures.pendingEntity();

            // when & then
            assertThatThrownBy(() -> mapper.toDomain(entityWithNullId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ID는 null일 수 없습니다");
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
            OutboundProduct original = OutboundProductFixtures.registeredProduct();

            // when
            OutboundProductJpaEntity entity = mapper.toEntity(original);
            OutboundProduct converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.productGroupIdValue()).isEqualTo(original.productGroupIdValue());
            assertThat(converted.salesChannelIdValue()).isEqualTo(original.salesChannelIdValue());
            assertThat(converted.externalProductId()).isEqualTo(original.externalProductId());
            assertThat(converted.status()).isEqualTo(original.status());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            OutboundProductJpaEntity original =
                    OutboundProductJpaEntityFixtures.registeredEntity(1L);

            // when
            OutboundProduct domain = mapper.toDomain(original);
            OutboundProductJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getProductGroupId()).isEqualTo(original.getProductGroupId());
            assertThat(converted.getSalesChannelId()).isEqualTo(original.getSalesChannelId());
            assertThat(converted.getExternalProductId()).isEqualTo(original.getExternalProductId());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
        }
    }
}
