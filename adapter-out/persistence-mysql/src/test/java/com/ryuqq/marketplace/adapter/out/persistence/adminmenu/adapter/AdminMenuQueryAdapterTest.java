package com.ryuqq.marketplace.adapter.out.persistence.adminmenu.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.adminmenu.AdminMenuJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.adminmenu.entity.AdminMenuJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.adminmenu.mapper.AdminMenuJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.adminmenu.repository.AdminMenuQueryDslRepository;
import com.ryuqq.marketplace.domain.adminmenu.AdminMenuFixtures;
import com.ryuqq.marketplace.domain.adminmenu.aggregate.AdminMenu;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminMenuQueryAdapter 단위 테스트")
class AdminMenuQueryAdapterTest {

    @InjectMocks private AdminMenuQueryAdapter sut;

    @Mock private AdminMenuQueryDslRepository queryDslRepository;
    @Mock private AdminMenuJpaEntityMapper mapper;

    @Nested
    @DisplayName("findActiveByMaxRoleLevel() - 역할 레벨 이하 메뉴 조회")
    class FindActiveByMaxRoleLevelTest {

        @Test
        @DisplayName("Entity를 Domain으로 변환하여 반환한다")
        void findActiveByMaxRoleLevel_WithEntities_ReturnsDomainList() {
            // given
            int roleLevel = 2;
            AdminMenuJpaEntity groupEntity = AdminMenuJpaEntityFixtures.activeGroupEntity();
            AdminMenuJpaEntity itemEntity = AdminMenuJpaEntityFixtures.activeItemEntity(2L, 1L);
            AdminMenu groupDomain = AdminMenuFixtures.activeGroupMenu();
            AdminMenu itemDomain = AdminMenuFixtures.activeItemMenu(2L, 1L);

            given(queryDslRepository.findActiveByMaxRoleLevel(roleLevel))
                    .willReturn(List.of(groupEntity, itemEntity));
            given(mapper.toDomain(groupEntity)).willReturn(groupDomain);
            given(mapper.toDomain(itemEntity)).willReturn(itemDomain);

            // when
            List<AdminMenu> result = sut.findActiveByMaxRoleLevel(roleLevel);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository).should().findActiveByMaxRoleLevel(roleLevel);
            then(mapper).should().toDomain(groupEntity);
            then(mapper).should().toDomain(itemEntity);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록을 반환한다")
        void findActiveByMaxRoleLevel_NoResults_ReturnsEmptyList() {
            // given
            int roleLevel = 0;
            given(queryDslRepository.findActiveByMaxRoleLevel(roleLevel)).willReturn(List.of());

            // when
            List<AdminMenu> result = sut.findActiveByMaxRoleLevel(roleLevel);

            // then
            assertThat(result).isEmpty();
        }
    }
}
