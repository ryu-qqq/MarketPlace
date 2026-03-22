package com.ryuqq.marketplace.adapter.out.persistence.inboundqna.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.InboundQnaJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.entity.InboundQnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.mapper.InboundQnaJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.repository.InboundQnaQueryDslRepository;
import com.ryuqq.marketplace.domain.inboundqna.InboundQnaFixtures;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
import com.ryuqq.marketplace.domain.inboundqna.vo.InboundQnaStatus;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * InboundQnaQueryAdapterTest - InboundQna Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("InboundQnaQueryAdapter 단위 테스트")
class InboundQnaQueryAdapterTest {

    @Mock private InboundQnaQueryDslRepository queryDslRepository;

    @Mock private InboundQnaJpaEntityMapper mapper;

    @InjectMocks private InboundQnaQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Domain을 반환합니다")
        void findById_WithExistingId_ReturnsDomain() {
            // given
            long id = InboundQnaJpaEntityFixtures.DEFAULT_ID;
            InboundQnaJpaEntity entity = InboundQnaJpaEntityFixtures.receivedEntity(id);
            InboundQna domain = InboundQnaFixtures.receivedInboundQna(id);

            given(queryDslRepository.findById(id)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<InboundQna> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            long id = 9999L;
            given(queryDslRepository.findById(id)).willReturn(Optional.empty());

            // when
            Optional<InboundQna> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Entity가 존재할 때 Mapper가 호출됩니다")
        void findById_WhenEntityExists_CallsMapper() {
            // given
            long id = InboundQnaJpaEntityFixtures.DEFAULT_ID;
            InboundQnaJpaEntity entity = InboundQnaJpaEntityFixtures.receivedEntity(id);
            InboundQna domain = InboundQnaFixtures.receivedInboundQna(id);

            given(queryDslRepository.findById(id)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            queryAdapter.findById(id);

            // then
            then(mapper).should().toDomain(entity);
        }

        @Test
        @DisplayName("Entity가 없을 때 Mapper는 호출되지 않습니다")
        void findById_WhenEntityNotExists_DoesNotCallMapper() {
            // given
            long id = 9999L;
            given(queryDslRepository.findById(id)).willReturn(Optional.empty());

            // when
            queryAdapter.findById(id);

            // then
            then(mapper).shouldHaveNoInteractions();
        }
    }

    // ========================================================================
    // 2. existsBySalesChannelIdAndExternalQnaId 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsBySalesChannelIdAndExternalQnaId 메서드 테스트")
    class ExistsBySalesChannelIdAndExternalQnaIdTest {

        @Test
        @DisplayName("존재하는 경우 true를 반환합니다")
        void exists_WhenExists_ReturnsTrue() {
            // given
            long salesChannelId = InboundQnaFixtures.DEFAULT_SALES_CHANNEL_ID;
            String externalQnaId = InboundQnaFixtures.DEFAULT_EXTERNAL_QNA_ID;

            given(
                            queryDslRepository.existsBySalesChannelIdAndExternalQnaId(
                                    salesChannelId, externalQnaId))
                    .willReturn(true);

            // when
            boolean result =
                    queryAdapter.existsBySalesChannelIdAndExternalQnaId(
                            salesChannelId, externalQnaId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 경우 false를 반환합니다")
        void exists_WhenNotExists_ReturnsFalse() {
            // given
            long salesChannelId = 9999L;
            String externalQnaId = "NON-EXIST-QNA";

            given(
                            queryDslRepository.existsBySalesChannelIdAndExternalQnaId(
                                    salesChannelId, externalQnaId))
                    .willReturn(false);

            // when
            boolean result =
                    queryAdapter.existsBySalesChannelIdAndExternalQnaId(
                            salesChannelId, externalQnaId);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Repository가 정확히 한 번 호출됩니다")
        void exists_CallsRepositoryOnce() {
            // given
            long salesChannelId = InboundQnaFixtures.DEFAULT_SALES_CHANNEL_ID;
            String externalQnaId = InboundQnaFixtures.DEFAULT_EXTERNAL_QNA_ID;

            given(
                            queryDslRepository.existsBySalesChannelIdAndExternalQnaId(
                                    salesChannelId, externalQnaId))
                    .willReturn(false);

            // when
            queryAdapter.existsBySalesChannelIdAndExternalQnaId(salesChannelId, externalQnaId);

            // then
            then(queryDslRepository)
                    .should()
                    .existsBySalesChannelIdAndExternalQnaId(salesChannelId, externalQnaId);
        }
    }

    // ========================================================================
    // 3. findByStatus 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByStatus 메서드 테스트")
    class FindByStatusTest {

        @Test
        @DisplayName("RECEIVED 상태로 조회 시 해당 Domain 목록을 반환합니다")
        void findByStatus_WithReceivedStatus_ReturnsDomainList() {
            // given
            InboundQnaStatus status = InboundQnaStatus.RECEIVED;
            int limit = 10;
            InboundQnaJpaEntity entity1 = InboundQnaJpaEntityFixtures.receivedEntity(1L);
            InboundQnaJpaEntity entity2 = InboundQnaJpaEntityFixtures.receivedEntity(2L);
            InboundQna domain1 = InboundQnaFixtures.receivedInboundQna(1L);
            InboundQna domain2 = InboundQnaFixtures.receivedInboundQna(2L);

            given(
                            queryDslRepository.findByStatusOrderByIdAsc(
                                    InboundQnaJpaEntity.Status.RECEIVED, limit))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<InboundQna> result = queryAdapter.findByStatus(status, limit);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }

        @Test
        @DisplayName("해당 상태의 데이터가 없으면 빈 목록을 반환합니다")
        void findByStatus_WhenNoEntities_ReturnsEmptyList() {
            // given
            InboundQnaStatus status = InboundQnaStatus.RECEIVED;
            int limit = 10;

            given(
                            queryDslRepository.findByStatusOrderByIdAsc(
                                    InboundQnaJpaEntity.Status.RECEIVED, limit))
                    .willReturn(List.of());

            // when
            List<InboundQna> result = queryAdapter.findByStatus(status, limit);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Status 변환이 올바르게 이루어집니다")
        void findByStatus_ConvertsStatusCorrectly() {
            // given
            InboundQnaStatus status = InboundQnaStatus.CONVERTED;
            int limit = 5;

            given(
                            queryDslRepository.findByStatusOrderByIdAsc(
                                    InboundQnaJpaEntity.Status.CONVERTED, limit))
                    .willReturn(List.of());

            // when
            queryAdapter.findByStatus(status, limit);

            // then
            then(queryDslRepository)
                    .should()
                    .findByStatusOrderByIdAsc(InboundQnaJpaEntity.Status.CONVERTED, limit);
        }

        @Test
        @DisplayName("Mapper가 각 Entity마다 호출됩니다")
        void findByStatus_CallsMapperForEachEntity() {
            // given
            InboundQnaStatus status = InboundQnaStatus.RECEIVED;
            int limit = 10;
            InboundQnaJpaEntity entity1 = InboundQnaJpaEntityFixtures.receivedEntity(1L);
            InboundQnaJpaEntity entity2 = InboundQnaJpaEntityFixtures.receivedEntity(2L);

            given(
                            queryDslRepository.findByStatusOrderByIdAsc(
                                    InboundQnaJpaEntity.Status.RECEIVED, limit))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(InboundQnaFixtures.receivedInboundQna(1L));
            given(mapper.toDomain(entity2)).willReturn(InboundQnaFixtures.receivedInboundQna(2L));

            // when
            queryAdapter.findByStatus(status, limit);

            // then
            then(mapper).should().toDomain(entity1);
            then(mapper).should().toDomain(entity2);
        }
    }
}
