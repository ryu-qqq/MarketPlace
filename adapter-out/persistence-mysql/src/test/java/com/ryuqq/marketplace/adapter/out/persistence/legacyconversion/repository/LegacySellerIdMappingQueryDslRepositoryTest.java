package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.LegacySellerIdMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacySellerIdMappingJpaEntity;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/** LegacySellerIdMappingQueryDslRepositoryTest - 레거시 셀러 ID 매핑 QueryDslRepository 통합 테스트. */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("LegacySellerIdMappingQueryDslRepository 통합 테스트")
class LegacySellerIdMappingQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private LegacySellerIdMappingQueryDslRepository repository() {
        return new LegacySellerIdMappingQueryDslRepository(new JPAQueryFactory(entityManager));
    }

    private LegacySellerIdMappingJpaEntity persist(LegacySellerIdMappingJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findInternalSellerIdByLegacySellerId")
    class FindInternalSellerIdByLegacySellerIdTest {

        @Test
        @DisplayName("legacySellerId로 internalSellerId를 조회합니다")
        void findInternalSellerIdByLegacySellerId_WithExistingId_ReturnsInternalId() {
            // given
            long legacySellerId = 20001L;
            long internalSellerId = 3001L;
            persist(
                    LegacySellerIdMappingJpaEntityFixtures.entityWithInternalSellerId(
                            legacySellerId, internalSellerId, "테스트셀러A"));

            // when
            Optional<Long> result =
                    repository().findInternalSellerIdByLegacySellerId(legacySellerId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(internalSellerId);
        }

        @Test
        @DisplayName("존재하지 않는 legacySellerId로 조회 시 빈 Optional을 반환합니다")
        void findInternalSellerIdByLegacySellerId_WithNonExistingId_ReturnsEmpty() {
            // when
            Optional<Long> result = repository().findInternalSellerIdByLegacySellerId(99999999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findSellerNameByLegacySellerId")
    class FindSellerNameByLegacySellerIdTest {

        @Test
        @DisplayName("legacySellerId로 셀러명을 조회합니다")
        void findSellerNameByLegacySellerId_WithExistingId_ReturnsSellerName() {
            // given
            long legacySellerId = 20002L;
            String sellerName = "네이버셀러B";
            persist(
                    LegacySellerIdMappingJpaEntityFixtures.entityWithInternalSellerId(
                            legacySellerId, 3002L, sellerName));

            // when
            Optional<String> result = repository().findSellerNameByLegacySellerId(legacySellerId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(sellerName);
        }

        @Test
        @DisplayName("존재하지 않는 legacySellerId로 조회 시 빈 Optional을 반환합니다")
        void findSellerNameByLegacySellerId_WithNonExistingId_ReturnsEmpty() {
            // when
            Optional<String> result = repository().findSellerNameByLegacySellerId(99999998L);

            // then
            assertThat(result).isEmpty();
        }
    }
}
