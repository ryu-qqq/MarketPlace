package com.ryuqq.marketplace.application.productgroupdescription.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroupdescription.port.out.command.ProductGroupDescriptionCommandPort;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
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
@DisplayName("ProductGroupDescriptionCommandManager 단위 테스트")
class ProductGroupDescriptionCommandManagerTest {

    @InjectMocks private ProductGroupDescriptionCommandManager sut;

    @Mock private ProductGroupDescriptionCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 상세설명 저장")
    class PersistTest {

        @Test
        @DisplayName("ProductGroupDescription을 저장하고 ID를 반환한다")
        void persist_ValidDescription_ReturnsId() {
            // given
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            Long expectedId = 1L;
            given(commandPort.persist(description)).willReturn(expectedId);

            // when
            Long result = sut.persist(description);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(description);
        }
    }
}
