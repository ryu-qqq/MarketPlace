package com.ryuqq.marketplace.application.seller.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.seller.port.out.query.SellerQueryPort;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.exception.SellerNotFoundException;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchCriteria;
import java.util.List;
import java.util.Optional;
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
@DisplayName("SellerReadManager 단위 테스트")
class SellerReadManagerTest {

    @InjectMocks private SellerReadManager sut;

    @Mock private SellerQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 Seller를 조회한다")
        void getById_Exists_ReturnsSeller() {
            // given
            SellerId id = SellerId.of(1L);
            Seller expected = SellerFixtures.activeSeller();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            Seller result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void getById_NotExists_ThrowsException() {
            // given
            SellerId id = SellerId.of(999L);

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(SellerNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getByIds() - ID 목록으로 조회")
    class GetByIdsTest {

        @Test
        @DisplayName("ID 목록으로 Seller들을 조회한다")
        void getByIds_ReturnsSellers() {
            // given
            List<SellerId> ids = List.of(SellerId.of(1L), SellerId.of(2L));
            List<Seller> expected =
                    List.of(SellerFixtures.activeSeller(1L), SellerFixtures.activeSeller(2L));

            given(queryPort.findByIds(ids)).willReturn(expected);

            // when
            List<Seller> result = sut.getByIds(ids);

            // then
            assertThat(result).hasSize(2);
            then(queryPort).should().findByIds(ids);
        }
    }

    @Nested
    @DisplayName("existsById() - ID 존재 여부 확인")
    class ExistsByIdTest {

        @Test
        @DisplayName("존재하는 ID면 true를 반환한다")
        void existsById_Exists_ReturnsTrue() {
            // given
            SellerId id = SellerId.of(1L);

            given(queryPort.existsById(id)).willReturn(true);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 ID면 false를 반환한다")
        void existsById_NotExists_ReturnsFalse() {
            // given
            SellerId id = SellerId.of(999L);

            given(queryPort.existsById(id)).willReturn(false);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsBySellerName() - 셀러명 존재 여부 확인")
    class ExistsBySellerNameTest {

        @Test
        @DisplayName("존재하는 셀러명이면 true를 반환한다")
        void existsBySellerName_Exists_ReturnsTrue() {
            // given
            String sellerName = "테스트 셀러";

            given(queryPort.existsBySellerName(sellerName)).willReturn(true);

            // when
            boolean result = sut.existsBySellerName(sellerName);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 셀러명이면 false를 반환한다")
        void existsBySellerName_NotExists_ReturnsFalse() {
            // given
            String sellerName = "없는 셀러";

            given(queryPort.existsBySellerName(sellerName)).willReturn(false);

            // when
            boolean result = sut.existsBySellerName(sellerName);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsBySellerNameExcluding() - 셀러명 중복 확인 (자기 제외)")
    class ExistsBySellerNameExcludingTest {

        @Test
        @DisplayName("다른 셀러가 같은 이름을 사용하면 true를 반환한다")
        void existsBySellerNameExcluding_Duplicate_ReturnsTrue() {
            // given
            String sellerName = "중복 셀러명";
            SellerId excludeId = SellerId.of(1L);

            given(queryPort.existsBySellerNameExcluding(sellerName, excludeId)).willReturn(true);

            // when
            boolean result = sut.existsBySellerNameExcluding(sellerName, excludeId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("다른 셀러가 같은 이름을 사용하지 않으면 false를 반환한다")
        void existsBySellerNameExcluding_NoDuplicate_ReturnsFalse() {
            // given
            String sellerName = "유일한 셀러명";
            SellerId excludeId = SellerId.of(1L);

            given(queryPort.existsBySellerNameExcluding(sellerName, excludeId)).willReturn(false);

            // when
            boolean result = sut.existsBySellerNameExcluding(sellerName, excludeId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 검색 조건으로 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 Seller 목록을 조회한다")
        void findByCriteria_ReturnsSellers() {
            // given
            SellerSearchCriteria criteria = SellerSearchCriteria.defaultCriteria();
            List<Seller> expected = List.of(SellerFixtures.activeSeller());

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<Seller> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findByCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 카운트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 Seller 수를 반환한다")
        void countByCriteria_ReturnsCount() {
            // given
            SellerSearchCriteria criteria = SellerSearchCriteria.defaultCriteria();
            long expected = 10L;

            given(queryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().countByCriteria(criteria);
        }
    }
}
