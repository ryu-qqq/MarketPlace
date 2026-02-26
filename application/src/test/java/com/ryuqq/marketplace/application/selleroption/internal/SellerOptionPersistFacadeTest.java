package com.ryuqq.marketplace.application.selleroption.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.selleroption.manager.SellerOptionGroupCommandManager;
import com.ryuqq.marketplace.application.selleroption.manager.SellerOptionValueCommandManager;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
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
@DisplayName("SellerOptionPersistFacade 단위 테스트")
class SellerOptionPersistFacadeTest {

    @InjectMocks private SellerOptionPersistFacade sut;

    @Mock private SellerOptionGroupCommandManager groupCommandManager;
    @Mock private SellerOptionValueCommandManager valueCommandManager;

    @Nested
    @DisplayName("persistAll() - 옵션 그룹 + 값 저장")
    class PersistAllTest {

        @Test
        @DisplayName("옵션 그룹과 값을 저장하고 생성된 SellerOptionValueId 목록을 반환한다")
        void persistAll_ValidGroups_ReturnsValueIds() {
            // given
            SellerOptionGroup group = ProductGroupFixtures.defaultSellerOptionGroup();
            List<SellerOptionGroup> groups = List.of(group);
            Long expectedGroupId = 1L;
            List<Long> valueIds = List.of(10L);

            given(groupCommandManager.persist(group)).willReturn(expectedGroupId);
            given(valueCommandManager.persistAllForGroup(eq(expectedGroupId), anyList()))
                    .willReturn(valueIds);

            // when
            List<SellerOptionValueId> result = sut.persistAll(groups);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).value()).isEqualTo(10L);
            then(groupCommandManager).should().persist(group);
            then(valueCommandManager).should().persistAllForGroup(eq(expectedGroupId), anyList());
        }

        @Test
        @DisplayName("빈 그룹 목록은 빈 ID 목록을 반환한다")
        void persistAll_EmptyGroups_ReturnsEmptyList() {
            // given
            List<SellerOptionGroup> emptyGroups = List.of();

            // when
            List<SellerOptionValueId> result = sut.persistAll(emptyGroups);

            // then
            assertThat(result).isEmpty();
            then(groupCommandManager).shouldHaveNoInteractions();
            then(valueCommandManager).shouldHaveNoInteractions();
        }
    }
}
