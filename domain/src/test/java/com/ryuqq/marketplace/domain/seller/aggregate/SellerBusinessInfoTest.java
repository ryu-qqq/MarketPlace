package com.ryuqq.marketplace.domain.seller.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerBusinessInfo Aggregate 테스트")
class SellerBusinessInfoTest {

    @Nested
    @DisplayName("forNew() - 신규 사업자 정보 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 셀러 사업자 정보를 생성한다")
        void createNewSellerBusinessInfo() {
            // when
            var businessInfo = SellerFixtures.newSellerBusinessInfo();

            // then
            assertThat(businessInfo.isNew()).isTrue();
            assertThat(businessInfo.sellerIdValue()).isEqualTo(1L);
            assertThat(businessInfo.registrationNumberValue()).isEqualTo("123-45-67890");
            assertThat(businessInfo.companyNameValue()).isEqualTo("테스트 주식회사");
            assertThat(businessInfo.representativeValue()).isEqualTo("홍길동");
            assertThat(businessInfo.isDeleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("활성 상태의 사업자 정보를 복원한다")
        void reconstituteActiveSellerBusinessInfo() {
            // when
            var businessInfo = SellerFixtures.activeSellerBusinessInfo();

            // then
            assertThat(businessInfo.isNew()).isFalse();
            assertThat(businessInfo.idValue()).isEqualTo(1L);
            assertThat(businessInfo.isDeleted()).isFalse();
            assertThat(businessInfo.deletedAt()).isNull();
        }

        @Test
        @DisplayName("삭제된 사업자 정보를 복원한다")
        void reconstituteDeletedSellerBusinessInfo() {
            // when
            var businessInfo = SellerFixtures.deletedSellerBusinessInfo();

            // then
            assertThat(businessInfo.isDeleted()).isTrue();
            assertThat(businessInfo.deletedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("update() - 사업자 정보 수정")
    class UpdateTest {

        @Test
        @DisplayName("사업자 정보를 수정한다")
        void updateSellerBusinessInfo() {
            // given
            var businessInfo = SellerFixtures.activeSellerBusinessInfo();
            var updateData = SellerFixtures.sellerBusinessInfoUpdateData();
            Instant now = CommonVoFixtures.now();

            // when
            businessInfo.update(updateData, now);

            // then
            assertThat(businessInfo.registrationNumberValue()).isEqualTo("987-65-43210");
            assertThat(businessInfo.companyNameValue()).isEqualTo("수정된 주식회사");
            assertThat(businessInfo.representativeValue()).isEqualTo("김철수");
            assertThat(businessInfo.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("delete() / restore() - 삭제 및 복원")
    class DeletionTest {

        @Test
        @DisplayName("사업자 정보를 삭제(Soft Delete)한다")
        void deleteSellerBusinessInfo() {
            // given
            var businessInfo = SellerFixtures.activeSellerBusinessInfo();
            Instant now = CommonVoFixtures.now();

            // when
            businessInfo.delete(now);

            // then
            assertThat(businessInfo.isDeleted()).isTrue();
            assertThat(businessInfo.deletedAt()).isEqualTo(now);
            assertThat(businessInfo.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("삭제된 사업자 정보를 복원한다")
        void restoreSellerBusinessInfo() {
            // given
            var businessInfo = SellerFixtures.deletedSellerBusinessInfo();
            Instant now = CommonVoFixtures.now();

            // when
            businessInfo.restore(now);

            // then
            assertThat(businessInfo.isDeleted()).isFalse();
            assertThat(businessInfo.deletedAt()).isNull();
            assertThat(businessInfo.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("주소 관련 getter들이 올바른 값을 반환한다")
        void returnsAddressValues() {
            var businessInfo = SellerFixtures.activeSellerBusinessInfo();

            assertThat(businessInfo.businessAddress()).isNotNull();
            assertThat(businessInfo.businessAddressZipCode()).isEqualTo("06141");
            assertThat(businessInfo.businessAddressRoad()).isEqualTo("서울시 강남구 테헤란로 123");
            assertThat(businessInfo.businessAddressDetail()).isEqualTo("테스트빌딩 5층");
        }

        @Test
        @DisplayName("통신판매업 신고번호를 반환한다")
        void returnsSaleReportNumber() {
            var businessInfo = SellerFixtures.activeSellerBusinessInfo();
            assertThat(businessInfo.saleReportNumberValue()).isEqualTo("2024-서울강남-0001");
        }

        @Test
        @DisplayName("등록 정보 VO들을 반환한다")
        void returnsRegistrationVOs() {
            var businessInfo = SellerFixtures.activeSellerBusinessInfo();

            assertThat(businessInfo.registrationNumber()).isNotNull();
            assertThat(businessInfo.companyName()).isNotNull();
            assertThat(businessInfo.representative()).isNotNull();
            assertThat(businessInfo.saleReportNumber()).isNotNull();
        }
    }
}
