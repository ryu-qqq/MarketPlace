package com.ryuqq.marketplace.adapter.out.persistence.commoncode.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.CommonCodeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.condition.CommonCodeConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.entity.CommonCodeJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.CommonCodeTypeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.entity.CommonCodeTypeJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.commoncode.query.CommonCodeSearchCriteria;
import com.ryuqq.marketplace.domain.commoncode.query.CommonCodeSortKey;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
 * CommonCodeQueryDslRepositoryTest - 공통 코드 QueryDslRepository 통합 테스트.
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
@DisplayName("CommonCodeQueryDslRepository 통합 테스트")
class CommonCodeQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private CommonCodeQueryDslRepository repository() {
        return new CommonCodeQueryDslRepository(
                new JPAQueryFactory(entityManager), new CommonCodeConditionBuilder());
    }

    private CommonCodeJpaEntity persist(CommonCodeJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private CommonCodeTypeJpaEntity persistType(CommonCodeTypeJpaEntity entity) {
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
            CommonCodeJpaEntity saved = persist(CommonCodeJpaEntityFixtures.newEntity());

            var result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("삭제된 Entity는 findById로 조회되지 않습니다")
        void findById_WithDeleted_ReturnsEmpty() {
            Instant now = Instant.now();
            CommonCodeJpaEntity deleted =
                    CommonCodeJpaEntity.create(
                            null,
                            CommonCodeJpaEntityFixtures.DEFAULT_COMMON_CODE_TYPE_ID,
                            "DELETED_CODE",
                            "삭제 코드",
                            999,
                            true,
                            now,
                            now,
                            now);
            persist(deleted);

            var result = repository().findById(deleted.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByIds")
    class FindByIdsTest {

        @Test
        @DisplayName("ID 목록으로 여러 Entity를 조회합니다")
        void findByIds_WithMultipleIds_ReturnsEntities() {
            CommonCodeJpaEntity saved1 = persist(CommonCodeJpaEntityFixtures.newEntity());
            CommonCodeJpaEntity saved2 = persist(CommonCodeJpaEntityFixtures.newEntity());

            var result = repository().findByIds(List.of(saved1.getId(), saved2.getId()));

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("삭제된 Entity는 ID 목록 조회에서 제외됩니다")
        void findByIds_WithDeletedEntity_ExcludesDeleted() {
            CommonCodeJpaEntity saved = persist(CommonCodeJpaEntityFixtures.newEntity());
            Instant now = Instant.now();
            CommonCodeJpaEntity deleted =
                    CommonCodeJpaEntity.create(
                            null,
                            CommonCodeJpaEntityFixtures.DEFAULT_COMMON_CODE_TYPE_ID,
                            "DELETED_CODE",
                            "삭제 코드",
                            999,
                            true,
                            now,
                            now,
                            now);
            persist(deleted);

            var result = repository().findByIds(List.of(saved.getId(), deleted.getId()));

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo(saved.getId());
        }
    }

    @Nested
    @DisplayName("existsByCommonCodeTypeIdAndCode")
    class ExistsByCommonCodeTypeIdAndCodeTest {

        @Test
        @DisplayName("타입 ID와 코드가 일치하는 활성 Entity가 존재하면 true를 반환합니다")
        void existsByCommonCodeTypeIdAndCode_WithMatchingEntity_ReturnsTrue() {
            Long typeId = 1L;
            String code = "CARD_TYPE";
            persist(CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(typeId, code, "카드 타입"));

            boolean exists = repository().existsByCommonCodeTypeIdAndCode(typeId, code);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("타입 ID는 일치하지만 코드가 다르면 false를 반환합니다")
        void existsByCommonCodeTypeIdAndCode_WithDifferentCode_ReturnsFalse() {
            Long typeId = 1L;
            persist(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            typeId, "CARD_TYPE", "카드 타입"));

            boolean exists = repository().existsByCommonCodeTypeIdAndCode(typeId, "OTHER_CODE");

            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("삭제된 Entity는 존재하지 않는 것으로 처리됩니다")
        void existsByCommonCodeTypeIdAndCode_WithDeletedEntity_ReturnsFalse() {
            Long typeId = 1L;
            String code = "DELETED_CODE";
            Instant now = Instant.now();
            CommonCodeJpaEntity deleted =
                    CommonCodeJpaEntity.create(
                            null, typeId, code, "삭제 코드", 999, true, now, now, now);
            persist(deleted);

            boolean exists = repository().existsByCommonCodeTypeIdAndCode(typeId, code);

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("existsActiveByCommonCodeTypeId")
    class ExistsActiveByCommonCodeTypeIdTest {

        @Test
        @DisplayName("특정 타입 ID의 활성화된 코드가 존재하면 true를 반환합니다")
        void existsActiveByCommonCodeTypeId_WithActiveEntity_ReturnsTrue() {
            Long typeId = 1L;
            persist(CommonCodeJpaEntityFixtures.newEntityWithTypeId(typeId));

            boolean exists = repository().existsActiveByCommonCodeTypeId(typeId);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("비활성화된 코드만 있으면 false를 반환합니다")
        void existsActiveByCommonCodeTypeId_WithOnlyInactive_ReturnsFalse() {
            Long typeId = 1L;
            Instant now = Instant.now();
            CommonCodeJpaEntity inactive =
                    CommonCodeJpaEntity.create(
                            null, typeId, "INACTIVE_CODE", "비활성 코드", 999, false, now, now, null);
            persist(inactive);

            boolean exists = repository().existsActiveByCommonCodeTypeId(typeId);

            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("삭제된 활성 코드는 존재하지 않는 것으로 처리됩니다")
        void existsActiveByCommonCodeTypeId_WithDeletedActive_ReturnsFalse() {
            Long typeId = 1L;
            Instant now = Instant.now();
            CommonCodeJpaEntity deleted =
                    CommonCodeJpaEntity.create(
                            null, typeId, "DELETED_CODE", "삭제 코드", 999, true, now, now, now);
            persist(deleted);

            boolean exists = repository().existsActiveByCommonCodeTypeId(typeId);

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("타입 코드로 해당 타입의 모든 코드를 조회합니다")
        void findByCriteria_WithTypeCode_ReturnsMatchingCodes() {
            // given: CommonCodeType persist
            CommonCodeTypeJpaEntity type =
                    persistType(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));
            CommonCodeTypeJpaEntity otherType =
                    persistType(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "DELIVERY_TYPE", "배송유형"));

            persist(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            type.getId(), "CREDIT_CARD", "신용카드"));
            persist(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            type.getId(), "DEBIT_CARD", "체크카드"));
            persist(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            otherType.getId(), "FAST_DELIVERY", "빠른배송"));

            var criteria = CommonCodeSearchCriteria.defaultOf("PAYMENT_METHOD");
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("활성화 필터로 활성 코드만 조회합니다")
        void findByCriteria_WithActiveFilter_ReturnsOnlyActive() {
            CommonCodeTypeJpaEntity type =
                    persistType(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));

            persist(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            type.getId(), "CREDIT_CARD", "신용카드"));
            Instant now = Instant.now();
            persist(
                    CommonCodeJpaEntity.create(
                            null, type.getId(), "MOBILE_PAY", "모바일페이", 1, false, now, now, null));

            var criteria = CommonCodeSearchCriteria.activeOnly("PAYMENT_METHOD");
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().isActive()).isTrue();
        }

        @Test
        @DisplayName("commonCodeTypeCode가 null이면 전체 조회합니다")
        void findByCriteria_WithNullTypeCode_ReturnsAll() {
            CommonCodeTypeJpaEntity type =
                    persistType(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));

            persist(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            type.getId(), "CREDIT_CARD", "신용카드"));
            persist(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            type.getId(), "DEBIT_CARD", "체크카드"));

            var criteria = CommonCodeSearchCriteria.defaultOf(null);
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("페이징이 적용됩니다")
        void findByCriteria_WithPaging_ReturnsPagedResults() {
            CommonCodeTypeJpaEntity type =
                    persistType(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));

            for (int i = 0; i < 5; i++) {
                persist(
                        CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                                type.getId(), "CODE_" + i, "코드_" + i));
            }

            var criteria =
                    CommonCodeSearchCriteria.of(
                            "PAYMENT_METHOD",
                            null,
                            QueryContext.of(
                                    CommonCodeSortKey.defaultKey(),
                                    SortDirection.ASC,
                                    PageRequest.of(1, 2)));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("타입 코드로 해당 타입의 코드 개수를 반환합니다")
        void countByCriteria_WithTypeCode_ReturnsCount() {
            CommonCodeTypeJpaEntity type =
                    persistType(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));
            CommonCodeTypeJpaEntity otherType =
                    persistType(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "DELIVERY_TYPE", "배송유형"));

            persist(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            type.getId(), "CREDIT_CARD", "신용카드"));
            persist(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            type.getId(), "DEBIT_CARD", "체크카드"));
            persist(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            otherType.getId(), "FAST_DELIVERY", "빠른배송"));

            var criteria = CommonCodeSearchCriteria.defaultOf("PAYMENT_METHOD");
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("활성화 필터로 활성 코드 개수만 반환합니다")
        void countByCriteria_WithActiveFilter_ReturnsActiveCount() {
            CommonCodeTypeJpaEntity type =
                    persistType(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));

            persist(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            type.getId(), "CREDIT_CARD", "신용카드"));
            Instant now = Instant.now();
            persist(
                    CommonCodeJpaEntity.create(
                            null, type.getId(), "MOBILE_PAY", "모바일페이", 1, false, now, now, null));

            var criteria = CommonCodeSearchCriteria.activeOnly("PAYMENT_METHOD");
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("commonCodeTypeCode가 null이면 전체 개수를 반환합니다")
        void countByCriteria_WithNullTypeCode_ReturnsTotal() {
            CommonCodeTypeJpaEntity type =
                    persistType(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));

            persist(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            type.getId(), "CREDIT_CARD", "신용카드"));
            persist(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            type.getId(), "DEBIT_CARD", "체크카드"));

            var criteria = CommonCodeSearchCriteria.defaultOf(null);
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(2);
        }
    }
}
