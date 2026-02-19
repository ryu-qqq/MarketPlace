package com.ryuqq.marketplace.application.selleroption.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.canonicaloption.port.out.query.CanonicalOptionGroupQueryPort;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
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
@DisplayName("SellerOptionGroupValidator 단위 테스트")
class SellerOptionGroupValidatorTest {

    @InjectMocks private SellerOptionGroupValidator sut;

    @Mock private CanonicalOptionGroupQueryPort canonicalOptionGroupQueryPort;

    @Nested
    @DisplayName("validate() - 전체 검증 (도메인 불변식 + 외부 FK)")
    class ValidateTest {

        @Test
        @DisplayName("캐노니컬 참조 없는 유효한 옵션 그룹은 검증을 통과한다")
        void validate_ValidGroupsWithoutCanonical_DoesNotThrow() {
            // given
            SellerOptionGroups groups =
                    SellerOptionGroups.of(List.of(ProductGroupFixtures.defaultSellerOptionGroup()));

            // when & then
            assertThatCode(() -> sut.validate(groups, OptionType.SINGLE))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("캐노니컬 참조가 있고 유효하면 검증을 통과한다")
        void validate_ValidGroupsWithCanonical_DoesNotThrow() {
            // given
            SellerOptionGroups groups =
                    SellerOptionGroups.of(List.of(ProductGroupFixtures.mappedSellerOptionGroup()));

            CanonicalOptionGroup canonicalGroup =
                    org.mockito.Mockito.mock(CanonicalOptionGroup.class);
            given(canonicalGroup.idValue()).willReturn(1L);

            com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionValue
                    canonicalValue =
                            org.mockito.Mockito.mock(
                                    com.ryuqq.marketplace.domain.canonicaloption.aggregate
                                            .CanonicalOptionValue.class);
            given(canonicalValue.idValue()).willReturn(1L);
            given(canonicalGroup.values()).willReturn(List.of(canonicalValue));

            given(canonicalOptionGroupQueryPort.findByIds(List.of(CanonicalOptionGroupId.of(1L))))
                    .willReturn(List.of(canonicalGroup));

            // when & then
            assertThatCode(() -> sut.validate(groups, OptionType.SINGLE))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("validate(SellerOptionGroups, OptionType) - 그룹 수 불일치 검증")
    class ValidateGroupCountTest {

        @Test
        @DisplayName("SINGLE 옵션 타입에 2개의 그룹이 있으면 예외를 던진다")
        void validate_SingleOptionTypeWithMultipleGroups_ThrowsException() {
            // given
            SellerOptionGroups groups =
                    SellerOptionGroups.of(
                            List.of(
                                    ProductGroupFixtures.defaultSellerOptionGroup(),
                                    ProductGroupFixtures.defaultSellerOptionGroup()));

            // when & then
            assertThatThrownBy(() -> sut.validate(groups, OptionType.SINGLE))
                    .isInstanceOf(Exception.class);
        }
    }
}
