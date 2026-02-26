package com.ryuqq.marketplace.application.seller.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.exception.SellerNameDuplicateException;
import com.ryuqq.marketplace.domain.seller.exception.SellerNotFoundException;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
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
@DisplayName("SellerValidator 단위 테스트")
class SellerValidatorTest {

    @InjectMocks private SellerValidator sut;

    @Mock private SellerReadManager readManager;

    @Nested
    @DisplayName("findExistingOrThrow() - 존재하는 Seller 조회")
    class FindExistingOrThrowTest {

        @Test
        @DisplayName("존재하는 ID로 Seller를 반환한다")
        void findExistingOrThrow_Exists_ReturnsSeller() {
            // given
            SellerId id = SellerId.of(1L);
            Seller expected = SellerFixtures.activeSeller();

            given(readManager.getById(id)).willReturn(expected);

            // when
            Seller result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void findExistingOrThrow_NotExists_ThrowsException() {
            // given
            SellerId id = SellerId.of(999L);

            given(readManager.getById(id)).willThrow(new SellerNotFoundException(id.value()));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(SellerNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateSellerNameNotDuplicate() - 셀러명 중복 검증")
    class ValidateSellerNameNotDuplicateTest {

        @Test
        @DisplayName("중복되지 않은 셀러명은 예외 없이 통과한다")
        void validateSellerNameNotDuplicate_NoDuplicate_NoException() {
            // given
            String sellerName = "신규 셀러";

            given(readManager.existsBySellerName(sellerName)).willReturn(false);

            // when & then (no exception)
            sut.validateSellerNameNotDuplicate(sellerName);
        }

        @Test
        @DisplayName("중복된 셀러명이면 예외가 발생한다")
        void validateSellerNameNotDuplicate_Duplicate_ThrowsException() {
            // given
            String sellerName = "중복 셀러";

            given(readManager.existsBySellerName(sellerName)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateSellerNameNotDuplicate(sellerName))
                    .isInstanceOf(SellerNameDuplicateException.class);
        }
    }

    @Nested
    @DisplayName("validateSellerNameNotDuplicateExcluding() - 셀러명 중복 검증 (자기 제외)")
    class ValidateSellerNameNotDuplicateExcludingTest {

        @Test
        @DisplayName("자기 제외 시 중복되지 않으면 예외 없이 통과한다")
        void validateSellerNameNotDuplicateExcluding_NoDuplicate_NoException() {
            // given
            String sellerName = "수정된 셀러";
            SellerId excludeId = SellerId.of(1L);

            given(readManager.existsBySellerNameExcluding(sellerName, excludeId)).willReturn(false);

            // when & then (no exception)
            sut.validateSellerNameNotDuplicateExcluding(sellerName, excludeId);
        }

        @Test
        @DisplayName("자기 제외 시 중복되면 예외가 발생한다")
        void validateSellerNameNotDuplicateExcluding_Duplicate_ThrowsException() {
            // given
            String sellerName = "중복 셀러";
            SellerId excludeId = SellerId.of(1L);

            given(readManager.existsBySellerNameExcluding(sellerName, excludeId)).willReturn(true);

            // when & then
            assertThatThrownBy(
                            () ->
                                    sut.validateSellerNameNotDuplicateExcluding(
                                            sellerName, excludeId))
                    .isInstanceOf(SellerNameDuplicateException.class);
        }
    }
}
