package com.ryuqq.marketplace.application.productgroup.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroup.port.out.command.ProductGroupCommandPort;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
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
@DisplayName("ProductGroupCommandManager 단위 테스트")
class ProductGroupCommandManagerTest {

    @InjectMocks private ProductGroupCommandManager sut;

    @Mock private ProductGroupCommandPort commandPort;

    @Nested
    @DisplayName("persist() - ProductGroup 저장")
    class PersistTest {

        @Test
        @DisplayName("신규 ProductGroup을 저장하고 ID를 반환한다")
        void persist_NewProductGroup_ReturnsSavedId() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.newProductGroup();
            Long expectedId = 1L;

            given(commandPort.persist(productGroup)).willReturn(expectedId);

            // when
            Long result = sut.persist(productGroup);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(productGroup);
        }

        @Test
        @DisplayName("기존 ProductGroup을 수정 저장하고 ID를 반환한다")
        void persist_ExistingProductGroup_ReturnsSavedId() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.activeProductGroup();
            Long expectedId = productGroup.idValue();

            given(commandPort.persist(productGroup)).willReturn(expectedId);

            // when
            Long result = sut.persist(productGroup);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(productGroup);
        }
    }
}
