package com.ryuqq.marketplace.adapter.out.persistence.inboundqna.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.InboundQnaJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.entity.InboundQnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.mapper.InboundQnaJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.repository.InboundQnaJpaRepository;
import com.ryuqq.marketplace.domain.inboundqna.InboundQnaFixtures;
import com.ryuqq.marketplace.domain.inboundqna.aggregate.InboundQna;
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
 * InboundQnaCommandAdapterTest - InboundQna Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-005: Domain -> Entity 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("InboundQnaCommandAdapter 단위 테스트")
class InboundQnaCommandAdapterTest {

    @Mock private InboundQnaJpaRepository repository;

    @Mock private InboundQnaJpaEntityMapper mapper;

    @InjectMocks private InboundQnaCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("Domain을 Entity로 변환 후 저장합니다")
        void persist_WithValidDomain_SavesEntity() {
            // given
            InboundQna domain = InboundQnaFixtures.receivedInboundQna();
            InboundQnaJpaEntity entity = InboundQnaJpaEntityFixtures.receivedEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should().toEntity(domain);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("RECEIVED 상태 Domain을 저장합니다")
        void persist_WithReceivedDomain_Saves() {
            // given
            InboundQna domain = InboundQnaFixtures.newInboundQna();
            InboundQnaJpaEntity entity = InboundQnaJpaEntityFixtures.receivedEntity();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("CONVERTED 상태 Domain을 저장합니다")
        void persist_WithConvertedDomain_Saves() {
            // given
            InboundQna domain = InboundQnaFixtures.convertedInboundQna();
            InboundQnaJpaEntity entity = InboundQnaJpaEntityFixtures.convertedEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("FAILED 상태 Domain을 저장합니다")
        void persist_WithFailedDomain_Saves() {
            // given
            InboundQna domain = InboundQnaFixtures.failedInboundQna();
            InboundQnaJpaEntity entity = InboundQnaJpaEntityFixtures.failedEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            InboundQna domain = InboundQnaFixtures.receivedInboundQna();
            InboundQnaJpaEntity entity = InboundQnaJpaEntityFixtures.receivedEntity(1L);

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
            InboundQna domain1 = InboundQnaFixtures.receivedInboundQna(1L);
            InboundQna domain2 = InboundQnaFixtures.receivedInboundQna(2L);
            List<InboundQna> domains = List.of(domain1, domain2);

            InboundQnaJpaEntity entity1 = InboundQnaJpaEntityFixtures.receivedEntity(1L);
            InboundQnaJpaEntity entity2 = InboundQnaJpaEntityFixtures.receivedEntity(2L);

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);
            given(repository.saveAll(List.of(entity1, entity2)))
                    .willReturn(List.of(entity1, entity2));

            // when
            commandAdapter.persistAll(domains);

            // then
            then(repository).should().saveAll(List.of(entity1, entity2));
        }

        @Test
        @DisplayName("빈 리스트를 저장하면 saveAll이 빈 리스트로 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAllWithEmpty() {
            // given
            List<InboundQna> emptyList = List.of();
            given(repository.saveAll(List.of())).willReturn(List.of());

            // when
            commandAdapter.persistAll(emptyList);

            // then
            then(repository).should().saveAll(List.of());
        }

        @Test
        @DisplayName("각 Domain에 대해 Mapper가 호출됩니다")
        void persistAll_CallsMapperForEachDomain() {
            // given
            InboundQna domain1 = InboundQnaFixtures.receivedInboundQna(1L);
            InboundQna domain2 = InboundQnaFixtures.convertedInboundQna(2L);
            InboundQna domain3 = InboundQnaFixtures.failedInboundQna();
            List<InboundQna> domains = List.of(domain1, domain2, domain3);

            InboundQnaJpaEntity entity = InboundQnaJpaEntityFixtures.entity();
            given(mapper.toEntity(domain1)).willReturn(entity);
            given(mapper.toEntity(domain2)).willReturn(entity);
            given(mapper.toEntity(domain3)).willReturn(entity);
            given(repository.saveAll(anyList())).willReturn(List.of(entity, entity, entity));

            // when
            commandAdapter.persistAll(domains);

            // then
            then(mapper).should(times(3)).toEntity(any(InboundQna.class));
        }
    }
}
