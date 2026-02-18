package com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.ChannelOptionMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.entity.ChannelOptionMappingJpaEntity;
import com.ryuqq.marketplace.domain.channeloptionmapping.ChannelOptionMappingFixtures;
import com.ryuqq.marketplace.domain.channeloptionmapping.aggregate.ChannelOptionMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ChannelOptionMappingJpaEntityMapperTest - ChannelOptionMapping Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ChannelOptionMappingJpaEntityMapper 단위 테스트")
class ChannelOptionMappingJpaEntityMapperTest {

    private ChannelOptionMappingJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ChannelOptionMappingJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("새로운 Domain을 Entity로 변환합니다")
        void toEntity_WithNewChannelOptionMapping_ConvertsCorrectly() {
            // given
            ChannelOptionMapping domain = ChannelOptionMappingFixtures.newChannelOptionMapping();

            // when
            ChannelOptionMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getSalesChannelId()).isEqualTo(domain.salesChannelIdValue());
            assertThat(entity.getCanonicalOptionValueId())
                    .isEqualTo(domain.canonicalOptionValueIdValue());
            assertThat(entity.getExternalOptionCode()).isEqualTo(domain.externalOptionCodeValue());
        }

        @Test
        @DisplayName("기존 Domain을 Entity로 변환합니다")
        void toEntity_WithExistingChannelOptionMapping_ConvertsCorrectly() {
            // given
            ChannelOptionMapping domain =
                    ChannelOptionMappingFixtures.existingChannelOptionMapping();

            // when
            ChannelOptionMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSalesChannelId()).isEqualTo(domain.salesChannelIdValue());
            assertThat(entity.getCanonicalOptionValueId())
                    .isEqualTo(domain.canonicalOptionValueIdValue());
            assertThat(entity.getExternalOptionCode()).isEqualTo(domain.externalOptionCodeValue());
        }

        @Test
        @DisplayName("파라미터를 지정한 Domain을 Entity로 변환합니다")
        void toEntity_WithCustomParams_ConvertsCorrectly() {
            // given
            Long salesChannelId = 5L;
            Long canonicalOptionValueId = 200L;
            String externalCode = "CUSTOM-CODE-001";
            ChannelOptionMapping domain =
                    ChannelOptionMappingFixtures.newChannelOptionMapping(
                            salesChannelId, canonicalOptionValueId, externalCode);

            // when
            ChannelOptionMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getSalesChannelId()).isEqualTo(salesChannelId);
            assertThat(entity.getCanonicalOptionValueId()).isEqualTo(canonicalOptionValueId);
            assertThat(entity.getExternalOptionCode()).isEqualTo(externalCode);
        }

        @Test
        @DisplayName("변환 시 createdAt과 updatedAt이 보존됩니다")
        void toEntity_PreservesTimestamps() {
            // given
            ChannelOptionMapping domain =
                    ChannelOptionMappingFixtures.existingChannelOptionMapping();

            // when
            ChannelOptionMappingJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
            assertThat(entity.getUpdatedAt()).isEqualTo(domain.updatedAt());
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("Entity를 Domain으로 변환합니다")
        void toDomain_WithValidEntity_ConvertsCorrectly() {
            // given
            ChannelOptionMappingJpaEntity entity = ChannelOptionMappingJpaEntityFixtures.entity(1L);

            // when
            ChannelOptionMapping domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.salesChannelIdValue()).isEqualTo(entity.getSalesChannelId());
            assertThat(domain.canonicalOptionValueIdValue())
                    .isEqualTo(entity.getCanonicalOptionValueId());
            assertThat(domain.externalOptionCodeValue()).isEqualTo(entity.getExternalOptionCode());
        }

        @Test
        @DisplayName("완전한 정보를 가진 Entity를 Domain으로 변환합니다")
        void toDomain_WithCompleteEntity_ConvertsCorrectly() {
            // given
            ChannelOptionMappingJpaEntity entity =
                    ChannelOptionMappingJpaEntityFixtures.entityWithCompleteInfo(
                            10L, 2L, 300L, "OPTION-CODE-XYZ");

            // when
            ChannelOptionMapping domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(10L);
            assertThat(domain.salesChannelIdValue()).isEqualTo(2L);
            assertThat(domain.canonicalOptionValueIdValue()).isEqualTo(300L);
            assertThat(domain.externalOptionCodeValue()).isEqualTo("OPTION-CODE-XYZ");
        }

        @Test
        @DisplayName("ID가 null인 Entity는 변환 시 예외가 발생합니다")
        void toDomain_WithNullId_ThrowsException() {
            // given
            ChannelOptionMappingJpaEntity entity =
                    ChannelOptionMappingJpaEntityFixtures.newEntity();

            // when & then
            assertThatThrownBy(() -> mapper.toDomain(entity))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("영속화된 엔티티의 ID는 null일 수 없습니다");
        }

        @Test
        @DisplayName("변환 시 timestamps가 보존됩니다")
        void toDomain_PreservesTimestamps() {
            // given
            ChannelOptionMappingJpaEntity entity = ChannelOptionMappingJpaEntityFixtures.entity(1L);

            // when
            ChannelOptionMapping domain = mapper.toDomain(entity);

            // then
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.updatedAt()).isEqualTo(entity.getUpdatedAt());
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
            ChannelOptionMapping original =
                    ChannelOptionMappingFixtures.existingChannelOptionMapping();

            // when
            ChannelOptionMappingJpaEntity entity = mapper.toEntity(original);
            ChannelOptionMapping converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.salesChannelIdValue()).isEqualTo(original.salesChannelIdValue());
            assertThat(converted.canonicalOptionValueIdValue())
                    .isEqualTo(original.canonicalOptionValueIdValue());
            assertThat(converted.externalOptionCodeValue())
                    .isEqualTo(original.externalOptionCodeValue());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            ChannelOptionMappingJpaEntity original =
                    ChannelOptionMappingJpaEntityFixtures.entity(1L);

            // when
            ChannelOptionMapping domain = mapper.toDomain(original);
            ChannelOptionMappingJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSalesChannelId()).isEqualTo(original.getSalesChannelId());
            assertThat(converted.getCanonicalOptionValueId())
                    .isEqualTo(original.getCanonicalOptionValueId());
            assertThat(converted.getExternalOptionCode())
                    .isEqualTo(original.getExternalOptionCode());
        }
    }
}
