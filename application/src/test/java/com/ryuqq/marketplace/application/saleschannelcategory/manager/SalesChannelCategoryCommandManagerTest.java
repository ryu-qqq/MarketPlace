package com.ryuqq.marketplace.application.saleschannelcategory.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.saleschannelcategory.port.out.command.SalesChannelCategoryCommandPort;
import com.ryuqq.marketplace.domain.saleschannelcategory.SalesChannelCategoryFixtures;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
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
@DisplayName("SalesChannelCategoryCommandManager 단위 테스트")
class SalesChannelCategoryCommandManagerTest {

    @InjectMocks private SalesChannelCategoryCommandManager sut;

    @Mock private SalesChannelCategoryCommandPort commandPort;

    @Nested
    @DisplayName("persist() - SalesChannelCategory 저장")
    class PersistTest {

        @Test
        @DisplayName("SalesChannelCategory를 저장하고 ID를 반환한다")
        void persist_ReturnsCategoryId() {
            // given
            SalesChannelCategory category = SalesChannelCategoryFixtures.newSalesChannelCategory();
            Long expectedId = 1L;

            given(commandPort.persist(category)).willReturn(expectedId);

            // when
            Long result = sut.persist(category);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(category);
        }

        @Test
        @DisplayName("하위 카테고리를 저장한다")
        void persist_ChildCategory_ReturnsCategoryId() {
            // given
            SalesChannelCategory category = SalesChannelCategoryFixtures.newChildCategory(100L);
            Long expectedId = 2L;

            given(commandPort.persist(category)).willReturn(expectedId);

            // when
            Long result = sut.persist(category);

            // then
            assertThat(result).isEqualTo(expectedId);
            assertThat(category.parentId()).isNotNull();
        }

        @Test
        @DisplayName("말단 카테고리를 저장한다")
        void persist_LeafCategory_ReturnsCategoryId() {
            // given
            SalesChannelCategory category = SalesChannelCategoryFixtures.leafCategory();
            Long expectedId = 3L;

            given(commandPort.persist(category)).willReturn(expectedId);

            // when
            Long result = sut.persist(category);

            // then
            assertThat(result).isEqualTo(expectedId);
            assertThat(category.isLeaf()).isTrue();
        }
    }
}
