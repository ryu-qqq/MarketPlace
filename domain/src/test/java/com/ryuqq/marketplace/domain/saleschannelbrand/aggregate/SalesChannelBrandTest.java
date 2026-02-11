package com.ryuqq.marketplace.domain.saleschannelbrand.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.saleschannelbrand.SalesChannelBrandFixtures;
import com.ryuqq.marketplace.domain.saleschannelbrand.id.SalesChannelBrandId;
import com.ryuqq.marketplace.domain.saleschannelbrand.vo.SalesChannelBrandStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelBrand Aggregate 단위 테스트")
class SalesChannelBrandTest {

    @Nested
    @DisplayName("forNew 팩토리 메서드 테스트")
    class ForNewTest {

        @Test
        @DisplayName("필수 필드로 새 SalesChannelBrand를 생성한다")
        void createNewSalesChannelBrandWithRequiredFields() {
            // given
            Long salesChannelId = 1L;
            String externalBrandCode = "BRAND-001";
            String externalBrandName = "테스트 브랜드";
            Instant now = CommonVoFixtures.now();

            // when
            SalesChannelBrand brand =
                    SalesChannelBrand.forNew(salesChannelId, externalBrandCode, externalBrandName, now);

            // then
            assertThat(brand).isNotNull();
            assertThat(brand.id().isNew()).isTrue();
            assertThat(brand.salesChannelId()).isEqualTo(salesChannelId);
            assertThat(brand.externalBrandCode()).isEqualTo(externalBrandCode);
            assertThat(brand.externalBrandName()).isEqualTo(externalBrandName);
            assertThat(brand.status()).isEqualTo(SalesChannelBrandStatus.ACTIVE);
            assertThat(brand.isActive()).isTrue();
            assertThat(brand.createdAt()).isEqualTo(now);
            assertThat(brand.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("새 SalesChannelBrand는 기본적으로 ACTIVE 상태이다")
        void newSalesChannelBrandIsActiveByDefault() {
            // when
            SalesChannelBrand brand = SalesChannelBrandFixtures.newSalesChannelBrand();

            // then
            assertThat(brand.status()).isEqualTo(SalesChannelBrandStatus.ACTIVE);
            assertThat(brand.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("reconstitute 팩토리 메서드 테스트")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 활성 상태의 SalesChannelBrand를 복원한다")
        void reconstituteActiveSalesChannelBrand() {
            // given
            SalesChannelBrandId id = SalesChannelBrandFixtures.defaultSalesChannelBrandId();
            Long salesChannelId = 1L;
            String externalBrandCode = "BRAND-001";
            String externalBrandName = "활성 브랜드";
            SalesChannelBrandStatus status = SalesChannelBrandStatus.ACTIVE;
            Instant createdAt = CommonVoFixtures.yesterday();
            Instant updatedAt = CommonVoFixtures.yesterday();

            // when
            SalesChannelBrand brand =
                    SalesChannelBrand.reconstitute(
                            id,
                            salesChannelId,
                            externalBrandCode,
                            externalBrandName,
                            status,
                            createdAt,
                            updatedAt);

            // then
            assertThat(brand.id()).isEqualTo(id);
            assertThat(brand.id().isNew()).isFalse();
            assertThat(brand.salesChannelId()).isEqualTo(salesChannelId);
            assertThat(brand.externalBrandCode()).isEqualTo(externalBrandCode);
            assertThat(brand.externalBrandName()).isEqualTo(externalBrandName);
            assertThat(brand.status()).isEqualTo(status);
            assertThat(brand.isActive()).isTrue();
            assertThat(brand.createdAt()).isEqualTo(createdAt);
            assertThat(brand.updatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("영속성에서 비활성 상태의 SalesChannelBrand를 복원한다")
        void reconstituteInactiveSalesChannelBrand() {
            // given
            SalesChannelBrandId id = SalesChannelBrandId.of(2L);
            SalesChannelBrandStatus status = SalesChannelBrandStatus.INACTIVE;

            // when
            SalesChannelBrand brand =
                    SalesChannelBrand.reconstitute(
                            id,
                            1L,
                            "BRAND-002",
                            "비활성 브랜드",
                            status,
                            CommonVoFixtures.yesterday(),
                            CommonVoFixtures.yesterday());

            // then
            assertThat(brand.status()).isEqualTo(SalesChannelBrandStatus.INACTIVE);
            assertThat(brand.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            SalesChannelBrand brand = SalesChannelBrandFixtures.activeSalesChannelBrand(100L);

            // when
            Long idValue = brand.idValue();

            // then
            assertThat(idValue).isEqualTo(100L);
        }

        @Test
        @DisplayName("isActive()는 ACTIVE 상태일 때 true를 반환한다")
        void isActiveReturnsTrueForActiveStatus() {
            // given
            SalesChannelBrand brand = SalesChannelBrandFixtures.activeSalesChannelBrand();

            // then
            assertThat(brand.isActive()).isTrue();
        }

        @Test
        @DisplayName("isActive()는 INACTIVE 상태일 때 false를 반환한다")
        void isActiveReturnsFalseForInactiveStatus() {
            // given
            SalesChannelBrand brand = SalesChannelBrandFixtures.inactiveSalesChannelBrand();

            // then
            assertThat(brand.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("상태 확인 테스트")
    class StatusCheckTest {

        @Test
        @DisplayName("활성 상태 브랜드의 상태를 확인한다")
        void checkActiveStatus() {
            // given
            SalesChannelBrand brand = SalesChannelBrandFixtures.activeSalesChannelBrand();

            // then
            assertThat(brand.status()).isEqualTo(SalesChannelBrandStatus.ACTIVE);
            assertThat(brand.status().isActive()).isTrue();
        }

        @Test
        @DisplayName("비활성 상태 브랜드의 상태를 확인한다")
        void checkInactiveStatus() {
            // given
            SalesChannelBrand brand = SalesChannelBrandFixtures.inactiveSalesChannelBrand();

            // then
            assertThat(brand.status()).isEqualTo(SalesChannelBrandStatus.INACTIVE);
            assertThat(brand.status().isActive()).isFalse();
        }
    }
}
