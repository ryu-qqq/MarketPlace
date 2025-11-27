package com.ryuqq.marketplace.domain.brand.aggregate.brand;

import com.ryuqq.marketplace.domain.brand.event.BrandAliasAddedEvent;
import com.ryuqq.marketplace.domain.brand.event.BrandAliasConfirmedEvent;
import com.ryuqq.marketplace.domain.brand.event.BrandCreatedEvent;
import com.ryuqq.marketplace.domain.brand.event.BrandStatusChangedEvent;
import com.ryuqq.marketplace.domain.brand.event.BrandUpdatedEvent;
import com.ryuqq.marketplace.domain.brand.exception.BrandAliasDuplicateException;
import com.ryuqq.marketplace.domain.brand.exception.BrandAliasNotFoundException;
import com.ryuqq.marketplace.domain.brand.exception.BrandBlockedException;
import com.ryuqq.marketplace.domain.brand.fixture.BrandAliasFixture;
import com.ryuqq.marketplace.domain.brand.fixture.BrandFixture;
import com.ryuqq.marketplace.domain.brand.fixture.BrandVoFixture;
import com.ryuqq.marketplace.domain.brand.vo.AliasName;
import com.ryuqq.marketplace.domain.brand.vo.AliasSource;
import com.ryuqq.marketplace.domain.brand.vo.AliasStatus;
import com.ryuqq.marketplace.domain.brand.vo.BrandAliasId;
import com.ryuqq.marketplace.domain.brand.vo.BrandCode;
import com.ryuqq.marketplace.domain.brand.vo.BrandMeta;
import com.ryuqq.marketplace.domain.brand.vo.BrandName;
import com.ryuqq.marketplace.domain.brand.vo.BrandStatus;
import com.ryuqq.marketplace.domain.brand.vo.CanonicalName;
import com.ryuqq.marketplace.domain.brand.vo.Confidence;
import com.ryuqq.marketplace.domain.brand.vo.Country;
import com.ryuqq.marketplace.domain.brand.vo.DataQuality;
import com.ryuqq.marketplace.domain.brand.vo.Department;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Brand Aggregate Root 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("domain")
@Tag("unit")
@DisplayName("Brand Aggregate 단위 테스트")
class BrandTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("[성공] Brand.create()로 신규 Brand 생성")
        void create_ShouldCreateNewBrand() {
            // given
            BrandCode code = BrandVoFixture.brandCode("NIKE");
            CanonicalName canonicalName = BrandVoFixture.canonicalName("Nike");
            BrandName name = BrandVoFixture.brandName();
            Country country = BrandVoFixture.country();
            Department department = BrandVoFixture.department();

            // when
            Brand brand = Brand.create(code, canonicalName, name, country, department, false);

            // then
            assertThat(brand.id().isNew()).isTrue();
            assertThat(brand.code().value()).isEqualTo("NIKE");
            assertThat(brand.canonicalName().value()).isEqualTo("Nike");
            assertThat(brand.status()).isEqualTo(BrandStatus.ACTIVE);
            assertThat(brand.isLuxury()).isFalse();
            assertThat(brand.aliases()).isEmpty();
            assertThat(brand.version()).isZero();
        }

        @Test
        @DisplayName("[성공] Brand.create()로 럭셔리 브랜드 생성")
        void create_LuxuryBrand_ShouldSetLuxuryTrue() {
            // given & when
            Brand brand = BrandFixture.luxuryBrand();

            // then
            assertThat(brand.isLuxury()).isTrue();
            assertThat(brand.code().value()).isEqualTo("GUCCI");
        }

        @Test
        @DisplayName("[성공] Brand.create() 시 BrandCreatedEvent 발행")
        void create_ShouldPublishBrandCreatedEvent() {
            // when
            Brand brand = BrandFixture.defaultBrand();

            // then
            assertThat(brand.domainEvents()).hasSize(1);
            assertThat(brand.domainEvents().get(0)).isInstanceOf(BrandCreatedEvent.class);
        }

        @Test
        @DisplayName("[성공] Brand.reconstitute()로 영속성에서 복원")
        void reconstitute_ShouldReconstituteBrand() {
            // when
            Brand brand = BrandFixture.reconstitutedBrand();

            // then
            assertThat(brand.id().value()).isEqualTo(1L);
            assertThat(brand.code().value()).isEqualTo("NIKE");
            assertThat(brand.version()).isEqualTo(1L);
            assertThat(brand.domainEvents()).isEmpty(); // 재구성 시 이벤트 없음
        }
    }

    @Nested
    @DisplayName("도메인 행위 테스트")
    class DomainBehaviorTest {

        @Test
        @DisplayName("[성공] canMapProduct()는 ACTIVE 상태에서 true 반환")
        void canMapProduct_ActiveStatus_ShouldReturnTrue() {
            // given
            Brand brand = BrandFixture.defaultBrand();

            // when & then
            assertThat(brand.canMapProduct()).isTrue();
        }

        @Test
        @DisplayName("[성공] canMapProduct()는 INACTIVE 상태에서 false 반환")
        void canMapProduct_InactiveStatus_ShouldReturnFalse() {
            // given
            Brand brand = BrandFixture.inactiveBrand();

            // when & then
            assertThat(brand.canMapProduct()).isFalse();
        }

        @Test
        @DisplayName("[성공] canMapProduct()는 BLOCKED 상태에서 false 반환")
        void canMapProduct_BlockedStatus_ShouldReturnFalse() {
            // given
            Brand brand = BrandFixture.blockedBrand();

            // when & then
            assertThat(brand.canMapProduct()).isFalse();
        }

        @Test
        @DisplayName("[실패] validateProductMapping()은 BLOCKED 상태에서 예외 발생")
        void validateProductMapping_BlockedStatus_ShouldThrowException() {
            // given
            Brand brand = BrandFixture.blockedBrand();

            // when & then
            assertThatThrownBy(brand::validateProductMapping)
                .isInstanceOf(BrandBlockedException.class);
        }
    }

    @Nested
    @DisplayName("수정 테스트")
    class UpdateTest {

        @Test
        @DisplayName("[성공] update()로 브랜드 정보 수정")
        void update_ShouldUpdateBrandInfo() {
            // given
            Brand brand = BrandFixture.reconstitutedBrand();
            brand.clearDomainEvents();

            BrandName newName = BrandName.of("뉴나이키", "New Nike", "NNK");
            Country newCountry = Country.of("KR");
            Department newDepartment = Department.BEAUTY;

            // when
            brand.update(newName, newCountry, newDepartment, true);

            // then
            assertThat(brand.nameKo()).isEqualTo("뉴나이키");
            assertThat(brand.nameEn()).isEqualTo("New Nike");
            assertThat(brand.country().code()).isEqualTo("KR");
            assertThat(brand.department()).isEqualTo(Department.BEAUTY);
            assertThat(brand.isLuxury()).isTrue();
        }

        @Test
        @DisplayName("[성공] update() 시 BrandUpdatedEvent 발행")
        void update_ShouldPublishBrandUpdatedEvent() {
            // given
            Brand brand = BrandFixture.reconstitutedBrand();
            brand.clearDomainEvents();

            // when
            brand.update(
                BrandVoFixture.brandName(),
                BrandVoFixture.country(),
                Department.FASHION,
                false
            );

            // then
            assertThat(brand.domainEvents()).hasSize(1);
            assertThat(brand.domainEvents().get(0)).isInstanceOf(BrandUpdatedEvent.class);
        }

        @Test
        @DisplayName("[성공] updateMeta()로 메타 정보 수정")
        void updateMeta_ShouldUpdateMetaInfo() {
            // given
            Brand brand = BrandFixture.reconstitutedBrand();
            brand.clearDomainEvents();

            BrandMeta newMeta = BrandMeta.of(
                "https://new.nike.com",
                "https://cdn.example.com/new-logo.png",
                "New Description"
            );

            // when
            brand.updateMeta(newMeta);

            // then
            assertThat(brand.officialWebsite()).isEqualTo("https://new.nike.com");
            assertThat(brand.logoUrl()).isEqualTo("https://cdn.example.com/new-logo.png");
            assertThat(brand.description()).isEqualTo("New Description");
        }

        @Test
        @DisplayName("[성공] changeStatus()로 상태 변경")
        void changeStatus_ShouldChangeStatus() {
            // given
            Brand brand = BrandFixture.reconstitutedBrand();
            brand.clearDomainEvents();

            // when
            brand.changeStatus(BrandStatus.INACTIVE);

            // then
            assertThat(brand.status()).isEqualTo(BrandStatus.INACTIVE);
        }

        @Test
        @DisplayName("[성공] changeStatus() 시 BrandStatusChangedEvent 발행")
        void changeStatus_ShouldPublishBrandStatusChangedEvent() {
            // given
            Brand brand = BrandFixture.reconstitutedBrand();
            brand.clearDomainEvents();

            // when
            brand.changeStatus(BrandStatus.BLOCKED);

            // then
            assertThat(brand.domainEvents()).hasSize(1);
            DomainEvent event = brand.domainEvents().get(0);
            assertThat(event).isInstanceOf(BrandStatusChangedEvent.class);
        }

        @Test
        @DisplayName("[성공] updateDataQuality()로 데이터 품질 수정")
        void updateDataQuality_ShouldUpdateDataQuality() {
            // given
            Brand brand = BrandFixture.reconstitutedBrand();
            DataQuality newQuality = BrandVoFixture.dataQualityHigh();

            // when
            brand.updateDataQuality(newQuality);

            // then
            assertThat(brand.dataQuality().score()).isEqualTo(85);
            assertThat(brand.dataQuality().isHighQuality()).isTrue();
        }
    }

    @Nested
    @DisplayName("Alias 관리 테스트")
    class AliasManagementTest {

        @Test
        @DisplayName("[성공] addAlias()로 별칭 추가")
        void addAlias_ShouldAddAlias() {
            // given
            Brand brand = BrandFixture.reconstitutedBrand();
            brand.clearDomainEvents();

            AliasName aliasName = AliasName.of("Nike Korea");
            AliasSource source = AliasSource.manual();
            Confidence confidence = Confidence.certain();
            AliasStatus status = AliasStatus.CONFIRMED;

            // when
            BrandAlias alias = brand.addAlias(aliasName, source, confidence, status);

            // then
            assertThat(brand.aliasCount()).isEqualTo(1);
            assertThat(alias.normalizedAlias()).isEqualTo("nikekorea");
            assertThat(alias.originalAlias()).isEqualTo("Nike Korea");
        }

        @Test
        @DisplayName("[성공] addAlias() 시 BrandAliasAddedEvent 발행")
        void addAlias_ShouldPublishBrandAliasAddedEvent() {
            // given
            Brand brand = BrandFixture.reconstitutedBrand();
            brand.clearDomainEvents();

            // when
            brand.addAlias(
                AliasName.of("New Alias"),
                AliasSource.manual(),
                Confidence.certain(),
                AliasStatus.CONFIRMED
            );

            // then
            assertThat(brand.domainEvents()).hasSize(1);
            assertThat(brand.domainEvents().get(0)).isInstanceOf(BrandAliasAddedEvent.class);
        }

        @Test
        @DisplayName("[실패] addAlias()로 중복 별칭 추가 시 예외 발생")
        void addAlias_DuplicateAlias_ShouldThrowException() {
            // given
            Brand brand = BrandFixture.reconstitutedBrand();
            AliasName aliasName = AliasName.of("Nike Korea");
            AliasSource source = AliasSource.manual();
            Confidence confidence = Confidence.certain();
            AliasStatus status = AliasStatus.CONFIRMED;

            brand.addAlias(aliasName, source, confidence, status);

            // when & then
            assertThatThrownBy(() -> brand.addAlias(aliasName, source, confidence, status))
                .isInstanceOf(BrandAliasDuplicateException.class);
        }

        @Test
        @DisplayName("[성공] 다른 scope의 별칭은 중복이 아님")
        void addAlias_DifferentScope_ShouldNotBeDuplicate() {
            // given
            Brand brand = BrandFixture.reconstitutedBrand();
            AliasName aliasName = AliasName.of("Nike Korea");

            // 첫 번째 별칭: GLOBAL
            brand.addAlias(aliasName, AliasSource.manual(), Confidence.certain(), AliasStatus.CONFIRMED);

            // when: 다른 mallCode로 동일 aliasName 추가
            BrandAlias secondAlias = brand.addAlias(
                aliasName,
                AliasSource.externalMall("BUYMA"),
                Confidence.certain(),
                AliasStatus.CONFIRMED
            );

            // then
            assertThat(brand.aliasCount()).isEqualTo(2);
            assertThat(secondAlias.mallCode()).isEqualTo("BUYMA");
        }

        @Test
        @DisplayName("[성공] REJECTED 상태 별칭은 중복 체크에서 제외")
        void addAlias_RejectedAliasScope_ShouldNotBeDuplicate() {
            // given
            Brand brand = BrandFixture.reconstitutedBrand();
            AliasName aliasName = AliasName.of("Nike Korea");

            // 첫 번째 별칭 추가 후 거부
            BrandAlias alias = brand.addAlias(
                aliasName,
                AliasSource.manual(),
                Confidence.certain(),
                AliasStatus.PENDING_REVIEW
            );

            // 재구성된 Brand에서 aliasId가 없으므로 직접 reject 호출
            alias.reject();

            // when: 동일한 aliasName으로 새 별칭 추가 (REJECTED는 무시됨)
            BrandAlias newAlias = brand.addAlias(
                aliasName,
                AliasSource.manual(),
                Confidence.certain(),
                AliasStatus.CONFIRMED
            );

            // then
            assertThat(brand.aliasCount()).isEqualTo(2);
            assertThat(newAlias.isConfirmed()).isTrue();
        }

        @Test
        @DisplayName("[성공] confirmAlias()로 별칭 확정")
        void confirmAlias_ShouldConfirmAlias() {
            // given
            Brand brand = BrandFixture.reconstitutedBrandWithAliases();
            brand.clearDomainEvents();

            BrandAlias alias = brand.aliases().get(0);
            BrandAliasId aliasId = alias.id();

            // when
            brand.confirmAlias(aliasId);

            // then
            assertThat(alias.isConfirmed()).isTrue();
        }

        @Test
        @DisplayName("[성공] confirmAlias() 시 BrandAliasConfirmedEvent 발행")
        void confirmAlias_ShouldPublishBrandAliasConfirmedEvent() {
            // given
            Brand brand = BrandFixture.reconstitutedBrandWithAliases();
            brand.clearDomainEvents();

            BrandAlias alias = brand.aliases().get(0);

            // when
            brand.confirmAlias(alias.id());

            // then
            assertThat(brand.domainEvents()).hasSize(1);
            assertThat(brand.domainEvents().get(0)).isInstanceOf(BrandAliasConfirmedEvent.class);
        }

        @Test
        @DisplayName("[실패] confirmAlias()로 존재하지 않는 별칭 확정 시 예외 발생")
        void confirmAlias_NotFound_ShouldThrowException() {
            // given
            Brand brand = BrandFixture.reconstitutedBrand();
            BrandAliasId nonExistentId = BrandAliasId.of(9999L);

            // when & then
            assertThatThrownBy(() -> brand.confirmAlias(nonExistentId))
                .isInstanceOf(BrandAliasNotFoundException.class);
        }

        @Test
        @DisplayName("[성공] rejectAlias()로 별칭 거부")
        void rejectAlias_ShouldRejectAlias() {
            // given
            Brand brand = BrandFixture.reconstitutedBrandWithAliases();
            BrandAlias alias = brand.aliases().get(0);

            // when
            brand.rejectAlias(alias.id());

            // then
            assertThat(alias.isRejected()).isTrue();
        }

        @Test
        @DisplayName("[성공] updateAliasConfidence()로 별칭 신뢰도 수정")
        void updateAliasConfidence_ShouldUpdateConfidence() {
            // given
            Brand brand = BrandFixture.reconstitutedBrandWithAliases();
            BrandAlias alias = brand.aliases().get(0);
            Confidence newConfidence = Confidence.of(0.5);

            // when
            brand.updateAliasConfidence(alias.id(), newConfidence);

            // then
            assertThat(alias.confidenceValue()).isEqualTo(0.5);
        }

        @Test
        @DisplayName("[성공] removeAlias()로 별칭 제거")
        void removeAlias_ShouldRemoveAlias() {
            // given
            Brand brand = BrandFixture.reconstitutedBrandWithAliases();
            int initialCount = brand.aliasCount();
            BrandAlias alias = brand.aliases().get(0);

            // when
            brand.removeAlias(alias.id());

            // then
            assertThat(brand.aliasCount()).isEqualTo(initialCount - 1);
        }
    }

    @Nested
    @DisplayName("Getter 테스트 (Law of Demeter 준수)")
    class GetterTest {

        @Test
        @DisplayName("[성공] nameKo()는 BrandName.nameKo()를 직접 반환")
        void nameKo_ShouldReturnDirectValue() {
            // given
            Brand brand = BrandFixture.defaultBrand();

            // when & then
            assertThat(brand.nameKo()).isEqualTo("나이키");
        }

        @Test
        @DisplayName("[성공] nameEn()는 BrandName.nameEn()를 직접 반환")
        void nameEn_ShouldReturnDirectValue() {
            // given
            Brand brand = BrandFixture.defaultBrand();

            // when & then
            assertThat(brand.nameEn()).isEqualTo("Nike");
        }

        @Test
        @DisplayName("[성공] displayName()은 우선순위에 따라 반환 (ko > en)")
        void displayName_ShouldReturnByPriority() {
            // given
            Brand koreanBrand = BrandFixture.defaultBrand();
            Brand englishOnlyBrand = Brand.create(
                BrandCode.of("TEST_EN"),
                CanonicalName.of("Test En"),
                BrandName.ofEnglish("English Only"),
                Country.of("US"),
                Department.FASHION,
                false
            );

            // when & then
            assertThat(koreanBrand.displayName()).isEqualTo("나이키");
            assertThat(englishOnlyBrand.displayName()).isEqualTo("English Only");
        }

        @Test
        @DisplayName("[성공] officialWebsite()는 BrandMeta.officialWebsite()를 직접 반환")
        void officialWebsite_ShouldReturnDirectValue() {
            // given
            Brand brand = BrandFixture.reconstitutedBrand();

            // when & then
            assertThat(brand.officialWebsite()).isEqualTo("https://www.nike.com");
        }

        @Test
        @DisplayName("[성공] aliases()는 불변 리스트 반환")
        void aliases_ShouldReturnUnmodifiableList() {
            // given
            Brand brand = BrandFixture.reconstitutedBrandWithAliases();

            // when & then
            assertThatThrownBy(() -> brand.aliases().add(BrandAliasFixture.defaultAlias()))
                .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("[성공] domainEvents()는 불변 리스트 반환")
        void domainEvents_ShouldReturnUnmodifiableList() {
            // given
            Brand brand = BrandFixture.defaultBrand();

            // when & then
            assertThatThrownBy(() -> brand.domainEvents().clear())
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("도메인 이벤트 관리 테스트")
    class DomainEventTest {

        @Test
        @DisplayName("[성공] clearDomainEvents()로 이벤트 목록 초기화")
        void clearDomainEvents_ShouldClearEvents() {
            // given
            Brand brand = BrandFixture.defaultBrand();
            assertThat(brand.domainEvents()).isNotEmpty();

            // when
            brand.clearDomainEvents();

            // then
            assertThat(brand.domainEvents()).isEmpty();
        }
    }
}
