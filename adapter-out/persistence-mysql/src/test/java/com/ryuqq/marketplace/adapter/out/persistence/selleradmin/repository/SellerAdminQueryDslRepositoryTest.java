package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.SellerAdminJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.condition.SellerAdminConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminJpaEntity;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * SellerAdminQueryDslRepositoryTest - 셀러 관리자 QueryDslRepository 통합 테스트.
 *
 * <p>soft-delete(notDeleted) 필터 적용을 우선 검증합니다.
 */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("SellerAdminQueryDslRepository 통합 테스트")
class SellerAdminQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SellerAdminQueryDslRepository repository() {
        return new SellerAdminQueryDslRepository(
                new JPAQueryFactory(entityManager), new SellerAdminConditionBuilder());
    }

    private SellerAdminJpaEntity persist(SellerAdminJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("미삭제 Entity는 findById로 조회됩니다")
        void findById_WithNotDeleted_ReturnsEntity() {
            SellerAdminJpaEntity saved =
                    persist(SellerAdminJpaEntityFixtures.activeEntity("admin-001"));

            var result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("삭제된 Entity는 findById로 조회되지 않습니다")
        void findById_WithDeleted_ReturnsEmpty() {
            SellerAdminJpaEntity deleted =
                    persist(
                            SellerAdminJpaEntityFixtures.deletedEntity(
                                    "admin-deleted", "deleted@test.com"));

            var result = repository().findById(deleted.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findBySellerIdAndId")
    class FindBySellerIdAndIdTest {

        @Test
        @DisplayName("셀러 ID와 관리자 ID로 조회 성공")
        void findBySellerIdAndId_ReturnsEntity() {
            Long sellerId = 1L;
            String adminId = "admin-002";
            SellerAdminJpaEntity saved =
                    persist(SellerAdminJpaEntityFixtures.activeEntityWithSellerId(sellerId));

            var result = repository().findBySellerIdAndId(sellerId, saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getSellerId()).isEqualTo(sellerId);
        }

        @Test
        @DisplayName("삭제된 Entity는 findBySellerIdAndId로 조회되지 않습니다")
        void findBySellerIdAndId_WithDeleted_ReturnsEmpty() {
            Long sellerId = 1L;
            SellerAdminJpaEntity deleted =
                    persist(
                            SellerAdminJpaEntityFixtures.deletedEntity(
                                    "admin-deleted-2", "deleted2@test.com"));

            var result = repository().findBySellerIdAndId(sellerId, deleted.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findBySellerIdAndIdAndStatuses")
    class FindBySellerIdAndIdAndStatusesTest {

        @Test
        @DisplayName("셀러 ID와 관리자 ID와 상태 목록으로 조회 성공")
        void findBySellerIdAndIdAndStatuses_ReturnsEntity() {
            Long sellerId = 1L;
            SellerAdminJpaEntity saved =
                    persist(SellerAdminJpaEntityFixtures.activeEntityWithSellerId(sellerId));

            var result =
                    repository()
                            .findBySellerIdAndIdAndStatuses(
                                    sellerId, saved.getId(), List.of(SellerAdminStatus.ACTIVE));

            assertThat(result).isPresent();
            assertThat(result.get().getStatus()).isEqualTo(SellerAdminStatus.ACTIVE);
        }

        @Test
        @DisplayName("상태가 일치하지 않으면 조회되지 않습니다")
        void findBySellerIdAndIdAndStatuses_WithWrongStatus_ReturnsEmpty() {
            Long sellerId = 1L;
            SellerAdminJpaEntity saved =
                    persist(SellerAdminJpaEntityFixtures.activeEntityWithSellerId(sellerId));

            var result =
                    repository()
                            .findBySellerIdAndIdAndStatuses(
                                    sellerId, saved.getId(), List.of(SellerAdminStatus.SUSPENDED));

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByIdAndStatuses")
    class FindByIdAndStatusesTest {

        @Test
        @DisplayName("관리자 ID와 상태 목록으로 조회 성공")
        void findByIdAndStatuses_ReturnsEntity() {
            SellerAdminJpaEntity saved =
                    persist(SellerAdminJpaEntityFixtures.pendingApprovalEntity());

            var result =
                    repository()
                            .findByIdAndStatuses(
                                    saved.getId(), List.of(SellerAdminStatus.PENDING_APPROVAL));

            assertThat(result).isPresent();
            assertThat(result.get().getStatus()).isEqualTo(SellerAdminStatus.PENDING_APPROVAL);
        }

        @Test
        @DisplayName("여러 상태 중 하나와 일치하면 조회됩니다")
        void findByIdAndStatuses_WithMultipleStatuses_ReturnsEntity() {
            SellerAdminJpaEntity saved = persist(SellerAdminJpaEntityFixtures.activeEntity());

            var result =
                    repository()
                            .findByIdAndStatuses(
                                    saved.getId(),
                                    List.of(SellerAdminStatus.ACTIVE, SellerAdminStatus.SUSPENDED));

            assertThat(result).isPresent();
        }
    }

    @Nested
    @DisplayName("findAllByIds")
    class FindAllByIdsTest {

        @Test
        @DisplayName("ID 목록으로 여러 관리자를 조회합니다")
        void findAllByIds_ReturnsMultipleEntities() {
            SellerAdminJpaEntity admin1 =
                    persist(SellerAdminJpaEntityFixtures.activeEntity("admin-101"));
            SellerAdminJpaEntity admin2 =
                    persist(SellerAdminJpaEntityFixtures.activeEntity("admin-102"));

            var result = repository().findAllByIds(List.of(admin1.getId(), admin2.getId()));

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("삭제된 Entity는 findAllByIds에서 제외됩니다")
        void findAllByIds_WithDeleted_ExcludesDeleted() {
            SellerAdminJpaEntity active =
                    persist(SellerAdminJpaEntityFixtures.activeEntity("admin-201"));
            SellerAdminJpaEntity deleted =
                    persist(
                            SellerAdminJpaEntityFixtures.deletedEntity(
                                    "admin-202", "deleted@test.com"));

            var result = repository().findAllByIds(List.of(active.getId(), deleted.getId()));

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(active.getId());
        }
    }

    @Nested
    @DisplayName("existsByLoginId")
    class ExistsByLoginIdTest {

        @Test
        @DisplayName("미삭제 Entity의 loginId는 existsByLoginId에서 true입니다")
        void existsByLoginId_WithNotDeleted_ReturnsTrue() {
            String loginId = "existing@test.com";
            persist(SellerAdminJpaEntityFixtures.newActiveEntityWithLoginId("admin-301", loginId));

            boolean exists = repository().existsByLoginId(loginId);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("삭제된 Entity의 loginId는 existsByLoginId에서 false입니다")
        void existsByLoginId_WithDeleted_ReturnsFalse() {
            String loginId = "deleted-login@test.com";
            persist(SellerAdminJpaEntityFixtures.deletedEntity("admin-302", loginId));

            boolean exists = repository().existsByLoginId(loginId);

            assertThat(exists).isFalse();
        }
    }
}
