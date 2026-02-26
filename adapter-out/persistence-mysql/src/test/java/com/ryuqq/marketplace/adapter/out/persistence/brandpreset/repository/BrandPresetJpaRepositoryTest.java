package com.ryuqq.marketplace.adapter.out.persistence.brandpreset.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.BrandPresetJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.entity.BrandPresetJpaEntity;
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
 * BrandPresetJpaRepositoryTest - BrandPreset JpaRepository 통합 테스트.
 *
 * <p>실제 데이터베이스 연동을 통한 저장/삭제 기능 검증.
 *
 * @author ryu-qqq
 * @since 1.0.0
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
@DisplayName("BrandPresetJpaRepository 통합 테스트")
class BrandPresetJpaRepositoryTest {

    @Autowired private BrandPresetJpaRepository repository;

    @Autowired private EntityManager entityManager;

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    @Nested
    @DisplayName("save")
    class SaveTest {

        @Test
        @DisplayName("새로운 BrandPreset Entity를 저장합니다")
        void save_WithNewEntity_PersistsSuccessfully() {
            // given
            BrandPresetJpaEntity entity = BrandPresetJpaEntityFixtures.newEntity();

            // when
            BrandPresetJpaEntity saved = repository.save(entity);
            flushAndClear();

            // then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getPresetName()).isEqualTo(entity.getPresetName());
        }

        @Test
        @DisplayName("기존 BrandPreset Entity를 업데이트합니다")
        void save_WithExistingEntity_UpdatesSuccessfully() {
            // given
            BrandPresetJpaEntity entity = BrandPresetJpaEntityFixtures.newEntity();
            BrandPresetJpaEntity saved = repository.save(entity);
            flushAndClear();

            // when
            BrandPresetJpaEntity updated = repository.save(saved);
            flushAndClear();

            // then
            assertThat(updated.getId()).isEqualTo(saved.getId());
        }
    }

    @Nested
    @DisplayName("saveAll")
    class SaveAllTest {

        @Test
        @DisplayName("BrandPreset Entity 목록을 일괄 저장합니다")
        void saveAll_WithEntityList_PersistsSuccessfully() {
            // given
            BrandPresetJpaEntity entity1 = BrandPresetJpaEntityFixtures.newEntity();
            BrandPresetJpaEntity entity2 = BrandPresetJpaEntityFixtures.newEntity();
            List<BrandPresetJpaEntity> entities = List.of(entity1, entity2);

            // when
            List<BrandPresetJpaEntity> saved = repository.saveAll(entities);
            flushAndClear();

            // then
            assertThat(saved).hasSize(2);
            assertThat(saved).allMatch(e -> e.getId() != null);
        }

        @Test
        @DisplayName("빈 목록을 저장하면 빈 목록을 반환합니다")
        void saveAll_WithEmptyList_ReturnsEmptyList() {
            // given
            List<BrandPresetJpaEntity> entities = List.of();

            // when
            List<BrandPresetJpaEntity> saved = repository.saveAll(entities);

            // then
            assertThat(saved).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("저장된 Entity를 ID로 조회합니다")
        void findById_WithSavedEntity_ReturnsEntity() {
            // given
            BrandPresetJpaEntity entity = BrandPresetJpaEntityFixtures.newEntity();
            BrandPresetJpaEntity saved = repository.save(entity);
            flushAndClear();

            // when
            var result = repository.findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // when
            var result = repository.findById(999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteByIdTest {

        @Test
        @DisplayName("저장된 Entity를 ID로 삭제합니다")
        void deleteById_WithSavedEntity_DeletesSuccessfully() {
            // given
            BrandPresetJpaEntity entity = BrandPresetJpaEntityFixtures.newEntity();
            BrandPresetJpaEntity saved = repository.save(entity);
            flushAndClear();

            // when
            repository.deleteById(saved.getId());
            flushAndClear();

            // then
            var result = repository.findById(saved.getId());
            assertThat(result).isEmpty();
        }
    }
}
