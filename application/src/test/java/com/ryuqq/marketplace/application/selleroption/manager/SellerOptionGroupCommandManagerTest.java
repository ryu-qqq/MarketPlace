package com.ryuqq.marketplace.application.selleroption.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.marketplace.application.selleroption.port.out.command.SellerOptionGroupCommandPort;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
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
@DisplayName("SellerOptionGroupCommandManager 단위 테스트")
class SellerOptionGroupCommandManagerTest {

    @InjectMocks private SellerOptionGroupCommandManager sut;

    @Mock private SellerOptionGroupCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 단건 옵션 그룹 저장")
    class PersistTest {

        @Test
        @DisplayName("옵션 그룹을 저장하고 생성된 ID를 반환한다")
        void persist_ValidGroup_ReturnsGroupId() {
            // given
            SellerOptionGroup group = ProductGroupFixtures.defaultSellerOptionGroup();
            Long expectedId = 1L;

            given(commandPort.persist(group)).willReturn(expectedId);

            // when
            Long result = sut.persist(group);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(group);
        }
    }

    @Nested
    @DisplayName("persistAll() - 다건 옵션 그룹 저장")
    class PersistAllTest {

        @Test
        @DisplayName("옵션 그룹 목록을 일괄 저장한다")
        void persistAll_MultipleGroups_PersistsAll() {
            // given
            SellerOptionGroup group1 = ProductGroupFixtures.defaultSellerOptionGroup();
            SellerOptionGroup group2 = ProductGroupFixtures.mappedSellerOptionGroup();
            List<SellerOptionGroup> groups = List.of(group1, group2);

            willDoNothing().given(commandPort).persistAll(groups);

            // when
            sut.persistAll(groups);

            // then
            then(commandPort).should().persistAll(groups);
        }
    }
}
