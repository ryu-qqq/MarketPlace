package com.ryuqq.marketplace.adapter.out.persistence.adminmenu.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.adminmenu.AdminMenuJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.adminmenu.entity.AdminMenuJpaEntity;
import com.ryuqq.marketplace.domain.adminmenu.AdminMenuFixtures;
import com.ryuqq.marketplace.domain.adminmenu.aggregate.AdminMenu;
import com.ryuqq.marketplace.domain.adminmenu.vo.AdminRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("AdminMenuJpaEntityMapper 단위 테스트")
class AdminMenuJpaEntityMapperTest {

    private AdminMenuJpaEntityMapper sut;

    @BeforeEach
    void setUp() {
        sut = new AdminMenuJpaEntityMapper();
    }

    @Nested
    @DisplayName("toDomain() - Entity → Domain 변환")
    class ToDomainTest {

        @Test
        @DisplayName("그룹 Entity를 Domain으로 변환한다")
        void toDomain_GroupEntity_ConvertsToDomain() {
            // given
            AdminMenuJpaEntity entity = AdminMenuJpaEntityFixtures.activeGroupEntity();

            // when
            AdminMenu domain = sut.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.parentId()).isNull();
            assertThat(domain.title()).isEqualTo(entity.getTitle());
            assertThat(domain.url()).isNull();
            assertThat(domain.iconName()).isEqualTo(entity.getIconName());
            assertThat(domain.displayOrder()).isEqualTo(entity.getDisplayOrder());
            assertThat(domain.requiredRole()).isEqualTo(AdminRole.ADMIN);
            assertThat(domain.isActive()).isTrue();
            assertThat(domain.isGroup()).isTrue();
        }

        @Test
        @DisplayName("아이템 Entity를 Domain으로 변환한다")
        void toDomain_ItemEntity_ConvertsToDomain() {
            // given
            AdminMenuJpaEntity entity = AdminMenuJpaEntityFixtures.activeItemEntity(2L, 1L);

            // when
            AdminMenu domain = sut.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(2L);
            assertThat(domain.parentId()).isEqualTo(1L);
            assertThat(domain.url()).isNotNull();
            assertThat(domain.isGroup()).isFalse();
        }

        @Test
        @DisplayName("roleLevel 0을 VIEWER로 변환한다")
        void toDomain_ViewerLevel_ConvertsToViewerRole() {
            // given
            AdminMenuJpaEntity entity = AdminMenuJpaEntityFixtures.viewerItemEntity(10L, 1L);

            // when
            AdminMenu domain = sut.toDomain(entity);

            // then
            assertThat(domain.requiredRole()).isEqualTo(AdminRole.VIEWER);
        }
    }

    @Nested
    @DisplayName("toEntity() - Domain → Entity 변환")
    class ToEntityTest {

        @Test
        @DisplayName("그룹 Domain을 Entity로 변환한다")
        void toEntity_GroupDomain_ConvertsToEntity() {
            // given
            AdminMenu domain = AdminMenuFixtures.activeGroupMenu();

            // when
            AdminMenuJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getParentId()).isNull();
            assertThat(entity.getTitle()).isEqualTo(domain.title());
            assertThat(entity.getUrl()).isNull();
            assertThat(entity.getRequiredRoleLevel()).isEqualTo(AdminRole.ADMIN.level());
            assertThat(entity.isActive()).isTrue();
        }

        @Test
        @DisplayName("아이템 Domain을 Entity로 변환한다")
        void toEntity_ItemDomain_ConvertsToEntity() {
            // given
            AdminMenu domain = AdminMenuFixtures.activeItemMenu(2L, 1L);

            // when
            AdminMenuJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getParentId()).isEqualTo(1L);
            assertThat(entity.getUrl()).isNotNull();
        }
    }
}
