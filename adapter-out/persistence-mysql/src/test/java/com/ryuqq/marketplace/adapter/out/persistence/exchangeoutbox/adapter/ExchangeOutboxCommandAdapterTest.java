package com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.ExchangeOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.entity.ExchangeOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.mapper.ExchangeOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.repository.ExchangeOutboxJpaRepository;
import com.ryuqq.marketplace.domain.exchange.outbox.ExchangeOutboxFixtures;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ExchangeOutboxCommandAdapterTest - 교환 아웃박스 Command Adapter 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ExchangeOutboxCommandAdapter 단위 테스트")
class ExchangeOutboxCommandAdapterTest {

    @Mock private ExchangeOutboxJpaRepository repository;
    @Mock private ExchangeOutboxJpaEntityMapper mapper;

    @InjectMocks private ExchangeOutboxCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("Domain을 Entity로 변환 후 저장하고 ID를 반환합니다")
        void persist_WithValidDomain_SavesAndReturnsId() {
            // given
            ExchangeOutbox domain = ExchangeOutboxFixtures.pendingExchangeOutbox();
            ExchangeOutboxJpaEntity entity = ExchangeOutboxJpaEntityFixtures.pendingEntity();
            ExchangeOutboxJpaEntity savedEntity =
                    ExchangeOutboxJpaEntityFixtures.pendingEntity(
                            ExchangeOutboxJpaEntityFixtures.DEFAULT_ID);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            Long result = commandAdapter.persist(domain);

            // then
            assertThat(result).isEqualTo(savedEntity.getId());
            then(mapper).should().toEntity(domain);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("persist 후 Domain의 version이 갱신됩니다")
        void persist_AfterSave_RefreshesVersionOnDomain() {
            // given
            ExchangeOutbox domain = ExchangeOutboxFixtures.pendingExchangeOutbox();
            ExchangeOutboxJpaEntity entity = ExchangeOutboxJpaEntityFixtures.pendingEntity();
            ExchangeOutboxJpaEntity savedEntity = ExchangeOutboxJpaEntityFixtures.completedEntity();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            long versionBefore = domain.version();

            // when
            commandAdapter.persist(domain);

            // then
            assertThat(domain.version()).isEqualTo(savedEntity.getVersion());
            assertThat(domain.version()).isNotEqualTo(versionBefore);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            ExchangeOutbox domain = ExchangeOutboxFixtures.pendingExchangeOutbox();
            ExchangeOutboxJpaEntity entity = ExchangeOutboxJpaEntityFixtures.pendingEntity();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(times(1)).toEntity(domain);
        }
    }

    // ========================================================================
    // 2. persistAll 테스트
    // ========================================================================

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("여러 Domain을 Entity로 변환 후 일괄 저장합니다")
        void persistAll_WithMultipleDomains_SavesAll() {
            // given
            ExchangeOutbox domain1 = ExchangeOutboxFixtures.pendingExchangeOutbox();
            ExchangeOutbox domain2 = ExchangeOutboxFixtures.pendingExchangeOutbox();
            List<ExchangeOutbox> domains = List.of(domain1, domain2);

            ExchangeOutboxJpaEntity entity1 = ExchangeOutboxJpaEntityFixtures.pendingEntity(1L);
            ExchangeOutboxJpaEntity entity2 = ExchangeOutboxJpaEntityFixtures.pendingEntity(2L);

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);
            given(repository.saveAll(List.of(entity1, entity2)))
                    .willReturn(List.of(entity1, entity2));

            // when
            commandAdapter.persistAll(domains);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<ExchangeOutboxJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(repository).should().saveAll(captor.capture());
            assertThat(captor.getValue()).hasSize(2);
        }

        @Test
        @DisplayName("persistAll 후 각 Domain의 version이 갱신됩니다")
        void persistAll_AfterSave_RefreshesVersionOnEachDomain() {
            // given
            ExchangeOutbox domain1 = ExchangeOutboxFixtures.pendingExchangeOutbox();
            ExchangeOutbox domain2 = ExchangeOutboxFixtures.pendingExchangeOutbox();
            List<ExchangeOutbox> domains = List.of(domain1, domain2);

            ExchangeOutboxJpaEntity entity1 = ExchangeOutboxJpaEntityFixtures.pendingEntity(1L);
            ExchangeOutboxJpaEntity entity2 = ExchangeOutboxJpaEntityFixtures.pendingEntity(2L);
            ExchangeOutboxJpaEntity saved1 = ExchangeOutboxJpaEntityFixtures.completedEntity();
            ExchangeOutboxJpaEntity saved2 = ExchangeOutboxJpaEntityFixtures.completedEntity();

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);
            given(repository.saveAll(List.of(entity1, entity2)))
                    .willReturn(List.of(saved1, saved2));

            // when
            commandAdapter.persistAll(domains);

            // then
            assertThat(domain1.version()).isEqualTo(saved1.getVersion());
            assertThat(domain2.version()).isEqualTo(saved2.getVersion());
        }

        @Test
        @DisplayName("빈 리스트를 저장해도 saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            List<ExchangeOutbox> emptyList = List.of();
            given(repository.saveAll(List.of())).willReturn(List.of());

            // when
            commandAdapter.persistAll(emptyList);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<ExchangeOutboxJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(repository).should().saveAll(captor.capture());
            assertThat(captor.getValue()).isEmpty();
        }

        @Test
        @DisplayName("각 Domain에 대해 Mapper가 호출됩니다")
        void persistAll_CallsMapperForEachDomain() {
            // given
            ExchangeOutbox domain1 = ExchangeOutboxFixtures.pendingExchangeOutbox();
            ExchangeOutbox domain2 = ExchangeOutboxFixtures.pendingExchangeOutbox();
            ExchangeOutbox domain3 = ExchangeOutboxFixtures.pendingExchangeOutbox();
            List<ExchangeOutbox> domains = List.of(domain1, domain2, domain3);

            ExchangeOutboxJpaEntity entity = ExchangeOutboxJpaEntityFixtures.pendingEntity();
            given(mapper.toEntity(domain1)).willReturn(entity);
            given(mapper.toEntity(domain2)).willReturn(entity);
            given(mapper.toEntity(domain3)).willReturn(entity);
            given(repository.saveAll(List.of(entity, entity, entity)))
                    .willReturn(List.of(entity, entity, entity));

            // when
            commandAdapter.persistAll(domains);

            // then
            then(mapper)
                    .should(times(3))
                    .toEntity(org.mockito.ArgumentMatchers.any(ExchangeOutbox.class));
        }
    }
}
