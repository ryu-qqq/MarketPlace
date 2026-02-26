package com.ryuqq.marketplace.adapter.out.persistence.inboundsource.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.InboundSourceJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.entity.InboundSourceJpaEntity;
import com.ryuqq.marketplace.domain.inboundsource.aggregate.InboundSource;
import com.ryuqq.marketplace.domain.inboundsource.id.InboundSourceId;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceCode;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceStatus;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceType;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * InboundSourceJpaEntityMapperTest - InboundSource Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("InboundSourceJpaEntityMapper 단위 테스트")
class InboundSourceJpaEntityMapperTest {

    private InboundSourceJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InboundSourceJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveDomain_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            InboundSource domain =
                    InboundSource.reconstitute(
                            InboundSourceId.of(1L),
                            InboundSourceCode.of("SETOF"),
                            "세토프 레거시",
                            InboundSourceType.LEGACY,
                            InboundSourceStatus.ACTIVE,
                            "레거시 Setof 상품 데이터 소스",
                            now,
                            now);

            // when
            InboundSourceJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getCode()).isEqualTo(domain.codeValue());
            assertThat(entity.getName()).isEqualTo(domain.name());
            assertThat(entity.getType()).isEqualTo(domain.type().name());
            assertThat(entity.getStatus()).isEqualTo(domain.status().name());
            assertThat(entity.getDescription()).isEqualTo(domain.description());
        }

        @Test
        @DisplayName("비활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithInactiveDomain_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            InboundSource domain =
                    InboundSource.reconstitute(
                            InboundSourceId.of(2L),
                            InboundSourceCode.of("INACTIVE_SRC"),
                            "비활성 소스",
                            InboundSourceType.LEGACY,
                            InboundSourceStatus.INACTIVE,
                            null,
                            now,
                            now);

            // when
            InboundSourceJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("CRAWLING 타입 Domain을 Entity로 변환합니다")
        void toEntity_WithCrawlingType_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            InboundSource domain =
                    InboundSource.reconstitute(
                            InboundSourceId.of(3L),
                            InboundSourceCode.of("COUPANG_CRAWL"),
                            "쿠팡 크롤링",
                            InboundSourceType.CRAWLING,
                            InboundSourceStatus.ACTIVE,
                            "쿠팡 크롤링 데이터 소스",
                            now,
                            now);

            // when
            InboundSourceJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getType()).isEqualTo("CRAWLING");
        }

        @Test
        @DisplayName("PARTNER 타입 Domain을 Entity로 변환합니다")
        void toEntity_WithPartnerType_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            InboundSource domain =
                    InboundSource.reconstitute(
                            InboundSourceId.of(4L),
                            InboundSourceCode.of("PARTNER_A"),
                            "파트너 A",
                            InboundSourceType.PARTNER,
                            InboundSourceStatus.ACTIVE,
                            "파트너 A 연동",
                            now,
                            now);

            // when
            InboundSourceJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getType()).isEqualTo("PARTNER");
        }

        @Test
        @DisplayName("설명이 없는 Domain을 Entity로 변환합니다")
        void toEntity_WithoutDescription_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            InboundSource domain =
                    InboundSource.reconstitute(
                            InboundSourceId.of(5L),
                            InboundSourceCode.of("NO_DESC"),
                            "설명없는 소스",
                            InboundSourceType.LEGACY,
                            InboundSourceStatus.ACTIVE,
                            null,
                            now,
                            now);

            // when
            InboundSourceJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getDescription()).isNull();
        }

        @Test
        @DisplayName("신규 Domain (ID 없음)을 Entity로 변환합니다")
        void toEntity_WithNewDomain_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            InboundSource domain =
                    InboundSource.forNew(
                            InboundSourceCode.of("NEW_SRC"),
                            "새 소스",
                            InboundSourceType.LEGACY,
                            "새 소스 설명",
                            now);

            // when
            InboundSourceJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getCode()).isEqualTo(domain.codeValue());
            assertThat(entity.getStatus()).isEqualTo("ACTIVE");
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithActiveEntity_ConvertsCorrectly() {
            // given
            InboundSourceJpaEntity entity = InboundSourceJpaEntityFixtures.activeEntity();

            // when
            InboundSource domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.codeValue()).isEqualTo(entity.getCode());
            assertThat(domain.name()).isEqualTo(entity.getName());
            assertThat(domain.type()).isEqualTo(InboundSourceType.LEGACY);
            assertThat(domain.status()).isEqualTo(InboundSourceStatus.ACTIVE);
            assertThat(domain.description()).isEqualTo(entity.getDescription());
            assertThat(domain.isActive()).isTrue();
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ConvertsCorrectly() {
            // given
            InboundSourceJpaEntity entity = InboundSourceJpaEntityFixtures.activeEntity(2L);
            // INACTIVE 상태로 직접 생성
            Instant now = Instant.now();
            InboundSourceJpaEntity inactiveEntity =
                    InboundSourceJpaEntity.create(
                            2L, "INACTIVE_CODE", "비활성 소스", "LEGACY", "INACTIVE", null, now, now);

            // when
            InboundSource domain = mapper.toDomain(inactiveEntity);

            // then
            assertThat(domain.status()).isEqualTo(InboundSourceStatus.INACTIVE);
            assertThat(domain.isActive()).isFalse();
        }

        @Test
        @DisplayName("CRAWLING 타입 Entity를 Domain으로 변환합니다")
        void toDomain_WithCrawlingEntity_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            InboundSourceJpaEntity entity =
                    InboundSourceJpaEntity.create(
                            3L, "COUPANG_CRAWL", "쿠팡 크롤링", "CRAWLING", "ACTIVE", null, now, now);

            // when
            InboundSource domain = mapper.toDomain(entity);

            // then
            assertThat(domain.type()).isEqualTo(InboundSourceType.CRAWLING);
        }

        @Test
        @DisplayName("PARTNER 타입 Entity를 Domain으로 변환합니다")
        void toDomain_WithPartnerEntity_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            InboundSourceJpaEntity entity =
                    InboundSourceJpaEntity.create(
                            4L, "PARTNER_A", "파트너 A", "PARTNER", "ACTIVE", null, now, now);

            // when
            InboundSource domain = mapper.toDomain(entity);

            // then
            assertThat(domain.type()).isEqualTo(InboundSourceType.PARTNER);
        }

        @Test
        @DisplayName("설명이 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutDescription_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            InboundSourceJpaEntity entity =
                    InboundSourceJpaEntity.create(
                            5L, "NO_DESC_CODE", "설명없는 소스", "LEGACY", "ACTIVE", null, now, now);

            // when
            InboundSource domain = mapper.toDomain(entity);

            // then
            assertThat(domain.description()).isNull();
        }

        @Test
        @DisplayName("ID가 null인 Entity를 Domain으로 변환 시 예외가 발생합니다")
        void toDomain_WithNullId_ThrowsException() {
            // given
            InboundSourceJpaEntity entity = InboundSourceJpaEntityFixtures.newEntity();

            // when / then
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
            Instant now = Instant.now();
            InboundSource original =
                    InboundSource.reconstitute(
                            InboundSourceId.of(1L),
                            InboundSourceCode.of("SETOF"),
                            "세토프 레거시",
                            InboundSourceType.LEGACY,
                            InboundSourceStatus.ACTIVE,
                            "레거시 Setof 상품 데이터 소스",
                            now,
                            now);

            // when
            InboundSourceJpaEntity entity = mapper.toEntity(original);
            InboundSource converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.codeValue()).isEqualTo(original.codeValue());
            assertThat(converted.name()).isEqualTo(original.name());
            assertThat(converted.type()).isEqualTo(original.type());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.description()).isEqualTo(original.description());
            assertThat(converted.isActive()).isEqualTo(original.isActive());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            InboundSourceJpaEntity original = InboundSourceJpaEntityFixtures.activeEntity();

            // when
            InboundSource domain = mapper.toDomain(original);
            InboundSourceJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getCode()).isEqualTo(original.getCode());
            assertThat(converted.getName()).isEqualTo(original.getName());
            assertThat(converted.getType()).isEqualTo(original.getType());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getDescription()).isEqualTo(original.getDescription());
        }
    }
}
