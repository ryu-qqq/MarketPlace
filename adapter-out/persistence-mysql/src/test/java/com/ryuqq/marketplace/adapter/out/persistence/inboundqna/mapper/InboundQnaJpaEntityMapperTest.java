package com.ryuqq.marketplace.adapter.out.persistence.inboundqna.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.InboundQnaJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.entity.InboundQnaJpaEntity;
import com.ryuqq.marketplace.domain.inboundqna.InboundQnaFixtures;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import com.ryuqq.marketplace.domain.inboundqna.vo.InboundQnaStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * InboundQnaJpaEntityMapperTest - InboundQna Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("InboundQnaJpaEntityMapper 단위 테스트")
class InboundQnaJpaEntityMapperTest {

    private InboundQnaJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InboundQnaJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("RECEIVED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithReceivedDomain_ConvertsCorrectly() {
            // given
            InboundQna domain = InboundQnaFixtures.receivedInboundQna();

            // when
            InboundQnaJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSalesChannelId()).isEqualTo(domain.salesChannelId());
            assertThat(entity.getExternalQnaId()).isEqualTo(domain.externalQnaId());
            assertThat(entity.getQnaType()).isEqualTo(domain.qnaType().name());
            assertThat(entity.getQuestionContent()).isEqualTo(domain.questionContent());
            assertThat(entity.getQuestionAuthor()).isEqualTo(domain.questionAuthor());
            assertThat(entity.getRawPayload()).isEqualTo(domain.rawPayload());
            assertThat(entity.getStatus().name()).isEqualTo(domain.status().name());
            assertThat(entity.getInternalQnaId()).isNull();
            assertThat(entity.getFailureReason()).isNull();
        }

        @Test
        @DisplayName("CONVERTED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithConvertedDomain_ConvertsCorrectly() {
            // given
            InboundQna domain = InboundQnaFixtures.convertedInboundQna();

            // when
            InboundQnaJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(InboundQnaJpaEntity.Status.CONVERTED);
            assertThat(entity.getInternalQnaId()).isEqualTo(domain.internalQnaId());
            assertThat(entity.getFailureReason()).isNull();
        }

        @Test
        @DisplayName("FAILED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithFailedDomain_ConvertsCorrectly() {
            // given
            InboundQna domain = InboundQnaFixtures.failedInboundQna();

            // when
            InboundQnaJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(InboundQnaJpaEntity.Status.FAILED);
            assertThat(entity.getInternalQnaId()).isNull();
            assertThat(entity.getFailureReason()).isEqualTo(domain.failureReason());
        }

        @Test
        @DisplayName("신규 Domain(ID null)을 Entity로 변환합니다")
        void toEntity_WithNewDomain_ConvertsCorrectly() {
            // given
            InboundQna domain = InboundQnaFixtures.newInboundQna();

            // when
            InboundQnaJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getStatus()).isEqualTo(InboundQnaJpaEntity.Status.RECEIVED);
            assertThat(entity.getExternalQnaId())
                    .isEqualTo(InboundQnaFixtures.DEFAULT_EXTERNAL_QNA_ID);
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
            InboundQnaJpaEntity entity = InboundQnaJpaEntityFixtures.receivedEntity(1L);

            // when
            InboundQna domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.salesChannelId()).isEqualTo(entity.getSalesChannelId());
            assertThat(domain.externalQnaId()).isEqualTo(entity.getExternalQnaId());
            assertThat(domain.qnaType().name()).isEqualTo(entity.getQnaType());
            assertThat(domain.questionContent()).isEqualTo(entity.getQuestionContent());
            assertThat(domain.questionAuthor()).isEqualTo(entity.getQuestionAuthor());
            assertThat(domain.rawPayload()).isEqualTo(entity.getRawPayload());
            assertThat(domain.status().name()).isEqualTo(entity.getStatus().name());
            assertThat(domain.internalQnaId()).isNull();
            assertThat(domain.failureReason()).isNull();
        }

        @Test
        @DisplayName("CONVERTED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithConvertedEntity_ConvertsCorrectly() {
            // given
            InboundQnaJpaEntity entity = InboundQnaJpaEntityFixtures.convertedEntity(2L);

            // when
            InboundQna domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(InboundQnaStatus.CONVERTED);
            assertThat(domain.internalQnaId())
                    .isEqualTo(InboundQnaJpaEntityFixtures.DEFAULT_INTERNAL_QNA_ID);
            assertThat(domain.failureReason()).isNull();
        }

        @Test
        @DisplayName("FAILED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithFailedEntity_ConvertsCorrectly() {
            // given
            InboundQnaJpaEntity entity = InboundQnaJpaEntityFixtures.failedEntity(3L);

            // when
            InboundQna domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(InboundQnaStatus.FAILED);
            assertThat(domain.internalQnaId()).isNull();
            assertThat(domain.failureReason())
                    .isEqualTo(InboundQnaJpaEntityFixtures.DEFAULT_FAILURE_REASON);
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
            InboundQna original = InboundQnaFixtures.convertedInboundQna();

            // when
            InboundQnaJpaEntity entity = mapper.toEntity(original);
            InboundQna converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.salesChannelId()).isEqualTo(original.salesChannelId());
            assertThat(converted.externalQnaId()).isEqualTo(original.externalQnaId());
            assertThat(converted.qnaType()).isEqualTo(original.qnaType());
            assertThat(converted.questionContent()).isEqualTo(original.questionContent());
            assertThat(converted.questionAuthor()).isEqualTo(original.questionAuthor());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.internalQnaId()).isEqualTo(original.internalQnaId());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            InboundQnaJpaEntity original = InboundQnaJpaEntityFixtures.convertedEntity(1L);

            // when
            InboundQna domain = mapper.toDomain(original);
            InboundQnaJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSalesChannelId()).isEqualTo(original.getSalesChannelId());
            assertThat(converted.getExternalQnaId()).isEqualTo(original.getExternalQnaId());
            assertThat(converted.getQnaType()).isEqualTo(original.getQnaType());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getInternalQnaId()).isEqualTo(original.getInternalQnaId());
        }
    }
}
