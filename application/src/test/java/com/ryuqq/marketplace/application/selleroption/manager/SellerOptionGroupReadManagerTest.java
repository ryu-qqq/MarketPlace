package com.ryuqq.marketplace.application.selleroption.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.selleroption.port.out.query.SellerOptionGroupQueryPort;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroups;
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
@DisplayName("SellerOptionGroupReadManager 단위 테스트")
class SellerOptionGroupReadManagerTest {

    @InjectMocks private SellerOptionGroupReadManager sut;

    @Mock private SellerOptionGroupQueryPort queryPort;

    @Nested
    @DisplayName("getByProductGroupId() - ProductGroupId로 옵션 그룹 조회")
    class GetByProductGroupIdTest {

        @Test
        @DisplayName("ProductGroupId로 옵션 그룹 목록을 조회하고 SellerOptionGroups를 반환한다")
        void getByProductGroupId_ExistingProductGroup_ReturnsSellerOptionGroups() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            SellerOptionGroup group1 = ProductGroupFixtures.defaultSellerOptionGroup();
            SellerOptionGroup group2 = ProductGroupFixtures.mappedSellerOptionGroup();

            given(queryPort.findByProductGroupId(productGroupId))
                    .willReturn(List.of(group1, group2));

            // when
            SellerOptionGroups result = sut.getByProductGroupId(productGroupId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.groups()).hasSize(2);
        }

        @Test
        @DisplayName("옵션이 없는 ProductGroup은 빈 SellerOptionGroups를 반환한다")
        void getByProductGroupId_NoOptions_ReturnsEmptyGroups() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            given(queryPort.findByProductGroupId(productGroupId)).willReturn(List.of());

            // when
            SellerOptionGroups result = sut.getByProductGroupId(productGroupId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.groups()).isEmpty();
        }
    }
}
