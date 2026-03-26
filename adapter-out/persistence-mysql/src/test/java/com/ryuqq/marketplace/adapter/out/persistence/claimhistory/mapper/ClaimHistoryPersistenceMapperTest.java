package com.ryuqq.marketplace.adapter.out.persistence.claimhistory.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.ClaimHistoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.entity.ClaimHistoryJpaEntity;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimHistoryType;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ClaimHistoryPersistenceMapperTest - 클레임 이력 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("ClaimHistoryPersistenceMapper 단위 테스트")
class ClaimHistoryPersistenceMapperTest {

    private ClaimHistoryPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ClaimHistoryPersistenceMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("상태 변경 이력 Domain을 Entity로 변환합니다")
        void toEntity_WithStatusChangeHistory_ConvertsCorrectly() {
            // given
            ClaimHistory domain = ClaimHistoryFixtures.statusChangeClaimHistory();

            // when
            ClaimHistoryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getClaimType()).isEqualTo(domain.claimType().name());
            assertThat(entity.getClaimId()).isEqualTo(domain.claimId());
            assertThat(entity.getHistoryType()).isEqualTo(domain.historyType().name());
            assertThat(entity.getTitle()).isEqualTo(domain.title());
            assertThat(entity.getMessage()).isEqualTo(domain.message());
            assertThat(entity.getActorType()).isEqualTo(domain.actor().actorType().name());
            assertThat(entity.getActorId()).isEqualTo(domain.actor().actorId());
            assertThat(entity.getActorName()).isEqualTo(domain.actor().actorName());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
        }

        @Test
        @DisplayName("수기 메모 이력 Domain을 Entity로 변환합니다")
        void toEntity_WithManualHistory_ConvertsCorrectly() {
            // given
            ClaimHistory domain = ClaimHistoryFixtures.manualClaimHistory();

            // when
            ClaimHistoryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getHistoryType()).isEqualTo(ClaimHistoryType.MANUAL.name());
            assertThat(entity.getTitle()).isEqualTo("CS 메모");
            assertThat(entity.getActorType()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("CANCEL 타입 이력 Domain을 Entity로 변환합니다")
        void toEntity_WithCancelType_ConvertsCorrectly() {
            // given
            ClaimHistory domain = ClaimHistoryFixtures.cancelStatusChangeHistory();

            // when
            ClaimHistoryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getClaimType()).isEqualTo(ClaimType.CANCEL.name());
        }

        @Test
        @DisplayName("REFUND 타입 이력 Domain을 Entity로 변환합니다")
        void toEntity_WithRefundType_ConvertsCorrectly() {
            // given
            ClaimHistory domain = ClaimHistoryFixtures.refundStatusChangeHistory();

            // when
            ClaimHistoryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getClaimType()).isEqualTo(ClaimType.REFUND.name());
        }

        @Test
        @DisplayName("EXCHANGE 타입 이력 Domain을 Entity로 변환합니다")
        void toEntity_WithExchangeType_ConvertsCorrectly() {
            // given
            ClaimHistory domain = ClaimHistoryFixtures.exchangeStatusChangeHistory();

            // when
            ClaimHistoryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getClaimType()).isEqualTo(ClaimType.EXCHANGE.name());
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("기본 Entity를 Domain으로 변환합니다")
        void toDomain_WithDefaultEntity_ConvertsCorrectly() {
            // given
            ClaimHistoryJpaEntity entity = ClaimHistoryJpaEntityFixtures.defaultEntity();

            // when
            ClaimHistory domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.claimType().name()).isEqualTo(entity.getClaimType());
            assertThat(domain.claimId()).isEqualTo(entity.getClaimId());
            assertThat(domain.historyType().name()).isEqualTo(entity.getHistoryType());
            assertThat(domain.title()).isEqualTo(entity.getTitle());
            assertThat(domain.message()).isEqualTo(entity.getMessage());
            assertThat(domain.actor().actorType().name()).isEqualTo(entity.getActorType());
            assertThat(domain.actor().actorId()).isEqualTo(entity.getActorId());
            assertThat(domain.actor().actorName()).isEqualTo(entity.getActorName());
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
        }

        @Test
        @DisplayName("CANCEL 타입 Entity를 Domain으로 변환합니다")
        void toDomain_WithCancelEntity_ConvertsCorrectly() {
            // given
            ClaimHistoryJpaEntity entity =
                    ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity("cancel-001");

            // when
            ClaimHistory domain = mapper.toDomain(entity);

            // then
            assertThat(domain.claimType()).isEqualTo(ClaimType.CANCEL);
            assertThat(domain.historyType()).isEqualTo(ClaimHistoryType.STATUS_CHANGE);
        }

        @Test
        @DisplayName("수기 메모 Entity를 Domain으로 변환합니다")
        void toDomain_WithManualEntity_ConvertsCorrectly() {
            // given
            ClaimHistoryJpaEntity entity =
                    ClaimHistoryJpaEntityFixtures.manualMemoEntity("refund-001");

            // when
            ClaimHistory domain = mapper.toDomain(entity);

            // then
            assertThat(domain.historyType()).isEqualTo(ClaimHistoryType.MANUAL);
            assertThat(domain.actor().actorType().name()).isEqualTo("ADMIN");
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
            ClaimHistory original = ClaimHistoryFixtures.reconstitutedClaimHistory();

            // when
            ClaimHistoryJpaEntity entity = mapper.toEntity(original);
            ClaimHistory converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.claimType()).isEqualTo(original.claimType());
            assertThat(converted.claimId()).isEqualTo(original.claimId());
            assertThat(converted.historyType()).isEqualTo(original.historyType());
            assertThat(converted.title()).isEqualTo(original.title());
            assertThat(converted.message()).isEqualTo(original.message());
            assertThat(converted.actor().actorType()).isEqualTo(original.actor().actorType());
            assertThat(converted.actor().actorId()).isEqualTo(original.actor().actorId());
            assertThat(converted.actor().actorName()).isEqualTo(original.actor().actorName());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            ClaimHistoryJpaEntity original = ClaimHistoryJpaEntityFixtures.defaultEntity();

            // when
            ClaimHistory domain = mapper.toDomain(original);
            ClaimHistoryJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getClaimType()).isEqualTo(original.getClaimType());
            assertThat(converted.getClaimId()).isEqualTo(original.getClaimId());
            assertThat(converted.getHistoryType()).isEqualTo(original.getHistoryType());
            assertThat(converted.getTitle()).isEqualTo(original.getTitle());
            assertThat(converted.getMessage()).isEqualTo(original.getMessage());
            assertThat(converted.getActorType()).isEqualTo(original.getActorType());
            assertThat(converted.getActorId()).isEqualTo(original.getActorId());
            assertThat(converted.getActorName()).isEqualTo(original.getActorName());
        }
    }
}
