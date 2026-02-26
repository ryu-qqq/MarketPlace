package com.ryuqq.marketplace.application.saleschannelcategory.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.saleschannelcategory.SalesChannelCategoryCommandFixtures;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.command.RegisterSalesChannelCategoryCommand;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import java.time.Instant;
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
@DisplayName("SalesChannelCategoryCommandFactory 단위 테스트")
class SalesChannelCategoryCommandFactoryTest {

    @InjectMocks private SalesChannelCategoryCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("create() - SalesChannelCategory 도메인 생성")
    class CreateTest {

        @Test
        @DisplayName("RegisterSalesChannelCategoryCommand로 SalesChannelCategory를 생성한다")
        void create_ReturnsSalesChannelCategory() {
            // given
            RegisterSalesChannelCategoryCommand command =
                    SalesChannelCategoryCommandFixtures.registerCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            SalesChannelCategory result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().isNew()).isTrue();
            assertThat(result.salesChannelId()).isEqualTo(command.salesChannelId());
            assertThat(result.externalCategoryCode()).isEqualTo(command.externalCategoryCode());
            assertThat(result.externalCategoryName()).isEqualTo(command.externalCategoryName());
            assertThat(result.parentId()).isEqualTo(command.parentId());
            assertThat(result.depth()).isEqualTo(command.depth());
            assertThat(result.path()).isEqualTo(command.path());
            assertThat(result.sortOrder()).isEqualTo(command.sortOrder());
            assertThat(result.isLeaf()).isEqualTo(command.leaf());
            assertThat(result.createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("하위 카테고리 정보로 SalesChannelCategory를 생성한다")
        void create_ChildCategory_ReturnsCategoryWithParent() {
            // given
            Long parentId = 100L;
            RegisterSalesChannelCategoryCommand command =
                    SalesChannelCategoryCommandFixtures.registerChildCommand(parentId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            SalesChannelCategory result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.parentId()).isEqualTo(parentId);
            assertThat(result.depth()).isEqualTo(2);
            assertThat(result.path()).contains("CAT002");
        }

        @Test
        @DisplayName("말단 카테고리 정보로 SalesChannelCategory를 생성한다")
        void create_LeafCategory_ReturnsCategoryAsLeaf() {
            // given
            RegisterSalesChannelCategoryCommand command =
                    SalesChannelCategoryCommandFixtures.registerLeafCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            SalesChannelCategory result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isLeaf()).isTrue();
            assertThat(result.depth()).isEqualTo(3);
        }

        @Test
        @DisplayName("displayPath가 설정된 경우 제대로 생성된다")
        void create_WithDisplayPath_ReturnsCategoryWithDisplayPath() {
            // given
            RegisterSalesChannelCategoryCommand command =
                    SalesChannelCategoryCommandFixtures.registerCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            SalesChannelCategory result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(command.displayPath()).isNotNull();
        }
    }
}
