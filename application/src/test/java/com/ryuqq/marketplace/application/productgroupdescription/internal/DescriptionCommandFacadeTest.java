package com.ryuqq.marketplace.application.productgroupdescription.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPersistResult;
import com.ryuqq.marketplace.application.productgroupdescription.manager.DescriptionImageCommandManager;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionCommandManager;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
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
@DisplayName("DescriptionCommandFacade 단위 테스트")
class DescriptionCommandFacadeTest {

    @InjectMocks private DescriptionCommandFacade sut;

    @Mock private ProductGroupDescriptionCommandManager descriptionCommandManager;
    @Mock private DescriptionImageCommandManager imageCommandManager;

    @Nested
    @DisplayName("persist() - Description + Image 저장")
    class PersistTest {

        @Test
        @DisplayName("Description을 저장하고 descriptionId와 imageIds를 반환한다")
        void persist_ValidDescription_ReturnsDescriptionPersistResult() {
            // given
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            Long descriptionId = 1L;
            List<Long> imageIds = List.of(10L, 11L);

            given(descriptionCommandManager.persist(description)).willReturn(descriptionId);
            given(imageCommandManager.persistAll(description.images())).willReturn(imageIds);

            // when
            DescriptionPersistResult result = sut.persist(description);

            // then
            assertThat(result.descriptionId()).isEqualTo(descriptionId);
            assertThat(result.imageIds()).isEqualTo(imageIds);
        }
    }

    @Nested
    @DisplayName("persistDescription() - Description만 저장 (상태 업데이트)")
    class PersistDescriptionTest {

        @Test
        @DisplayName("Description만 저장한다 (이미지 없이)")
        void persistDescription_ValidDescription_PersistsDescriptionOnly() {
            // given
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            given(descriptionCommandManager.persist(description)).willReturn(1L);

            // when
            sut.persistDescription(description);

            // then
            then(descriptionCommandManager).should().persist(description);
            then(imageCommandManager).shouldHaveNoInteractions();
        }
    }
}
