package com.ryuqq.marketplace.domain.brand.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.brand.BrandFixtures;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.brand.vo.BrandCode;
import com.ryuqq.marketplace.domain.brand.vo.BrandName;
import com.ryuqq.marketplace.domain.brand.vo.BrandStatus;
import com.ryuqq.marketplace.domain.brand.vo.LogoUrl;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Brand Aggregate 단위 테스트")
class BrandTest {

    @Nested
    @DisplayName("forNew 팩토리 메서드 테스트")
    class ForNewTest {
        @Test
        @DisplayName("필수 필드로 새 Brand를 생성한다")
        void createNewBrandWithRequiredFields() {
            // given
            BrandCode code = BrandCode.of("NEW_BRAND");
            BrandName name = BrandName.of("새 브랜드", "New Brand", "새브");
            LogoUrl logoUrl = LogoUrl.of("https://example.com/logo.png");
            Instant now = CommonVoFixtures.now();

            // when
            Brand brand = Brand.forNew(code, name, logoUrl, now);

            // then
            assertThat(brand).isNotNull();
            assertThat(brand.isNew()).isTrue();
            assertThat(brand.code()).isEqualTo(code);
            assertThat(brand.codeValue()).isEqualTo("NEW_BRAND");
            assertThat(brand.brandName()).isEqualTo(name);
            assertThat(brand.nameKo()).isEqualTo("새 브랜드");
            assertThat(brand.nameEn()).isEqualTo("New Brand");
            assertThat(brand.shortName()).isEqualTo("새브");
            assertThat(brand.status()).isEqualTo(BrandStatus.ACTIVE);
            assertThat(brand.isActive()).isTrue();
            assertThat(brand.logoUrl()).isEqualTo(logoUrl);
            assertThat(brand.logoUrlValue()).isEqualTo("https://example.com/logo.png");
            assertThat(brand.isDeleted()).isFalse();
            assertThat(brand.deletedAt()).isNull();
            assertThat(brand.createdAt()).isEqualTo(now);
            assertThat(brand.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("새로 생성된 Brand의 기본 상태는 ACTIVE다")
        void newBrandDefaultStatusIsActive() {
            // given & when
            Brand brand = BrandFixtures.newBrand();

            // then
            assertThat(brand.status()).isEqualTo(BrandStatus.ACTIVE);
            assertThat(brand.isActive()).isTrue();
        }

        @Test
        @DisplayName("새로 생성된 Brand는 삭제되지 않은 상태다")
        void newBrandIsNotDeleted() {
            // given & when
            Brand brand = BrandFixtures.newBrand();

            // then
            assertThat(brand.isDeleted()).isFalse();
            assertThat(brand.deletedAt()).isNull();
        }

        @Test
        @DisplayName("nullable 필드인 LogoUrl은 null일 수 있다")
        void newBrandCanHaveNullLogoUrl() {
            // given
            BrandCode code = BrandCode.of("NULL_LOGO");
            BrandName name = BrandName.of("로고 없음", null, null);
            LogoUrl logoUrl = null;
            Instant now = CommonVoFixtures.now();

            // when
            Brand brand = Brand.forNew(code, name, logoUrl, now);

            // then
            assertThat(brand.logoUrl()).isNull();
            assertThat(brand.logoUrlValue()).isNull();
        }
    }

    @Nested
    @DisplayName("reconstitute 팩토리 메서드 테스트")
    class ReconstituteTest {
        @Test
        @DisplayName("영속성에서 활성 상태의 Brand를 복원한다")
        void reconstituteActiveBrand() {
            // given
            BrandId id = BrandId.of(1L);
            BrandCode code = BrandCode.of("ACTIVE_BRAND");
            BrandName name = BrandName.of("활성 브랜드", "Active Brand", null);
            LogoUrl logoUrl = LogoUrl.of("https://example.com/active.png");
            Instant createdAt = CommonVoFixtures.yesterday();
            Instant updatedAt = CommonVoFixtures.yesterday();

            // when
            Brand brand =
                    Brand.reconstitute(
                            id,
                            code,
                            name,
                            BrandStatus.ACTIVE,
                            logoUrl,
                            null,
                            createdAt,
                            updatedAt);

            // then
            assertThat(brand.id()).isEqualTo(id);
            assertThat(brand.idValue()).isEqualTo(1L);
            assertThat(brand.isNew()).isFalse();
            assertThat(brand.code()).isEqualTo(code);
            assertThat(brand.brandName()).isEqualTo(name);
            assertThat(brand.status()).isEqualTo(BrandStatus.ACTIVE);
            assertThat(brand.isActive()).isTrue();
            assertThat(brand.logoUrl()).isEqualTo(logoUrl);
            assertThat(brand.isDeleted()).isFalse();
            assertThat(brand.deletedAt()).isNull();
            assertThat(brand.createdAt()).isEqualTo(createdAt);
            assertThat(brand.updatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("영속성에서 비활성 상태의 Brand를 복원한다")
        void reconstituteInactiveBrand() {
            // given
            BrandId id = BrandId.of(2L);
            BrandCode code = BrandCode.of("INACTIVE_BRAND");
            BrandName name = BrandName.of("비활성 브랜드", null, null);

            // when
            Brand brand =
                    Brand.reconstitute(
                            id,
                            code,
                            name,
                            BrandStatus.INACTIVE,
                            LogoUrl.empty(),
                            null,
                            CommonVoFixtures.yesterday(),
                            CommonVoFixtures.yesterday());

            // then
            assertThat(brand.status()).isEqualTo(BrandStatus.INACTIVE);
            assertThat(brand.isActive()).isFalse();
        }

        @Test
        @DisplayName("영속성에서 삭제된 Brand를 복원한다")
        void reconstituteDeletedBrand() {
            // given
            BrandId id = BrandId.of(3L);
            BrandCode code = BrandCode.of("DELETED_BRAND");
            BrandName name = BrandName.of("삭제된 브랜드", null, null);
            Instant deletedAt = CommonVoFixtures.yesterday();

            // when
            Brand brand =
                    Brand.reconstitute(
                            id,
                            code,
                            name,
                            BrandStatus.INACTIVE,
                            LogoUrl.empty(),
                            deletedAt,
                            CommonVoFixtures.yesterday(),
                            CommonVoFixtures.yesterday());

            // then
            assertThat(brand.isDeleted()).isTrue();
            assertThat(brand.deletedAt()).isEqualTo(deletedAt);
        }
    }

    @Nested
    @DisplayName("상태 변경 메서드 테스트")
    class StateChangeTest {
        @Test
        @DisplayName("활성 상태의 Brand를 비활성화한다")
        void deactivateActiveBrand() {
            // given
            Brand brand = BrandFixtures.activeBrand();
            Instant now = CommonVoFixtures.now();

            // when
            brand.deactivate(now);

            // then
            assertThat(brand.status()).isEqualTo(BrandStatus.INACTIVE);
            assertThat(brand.isActive()).isFalse();
            assertThat(brand.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("비활성 상태의 Brand를 활성화한다")
        void activateInactiveBrand() {
            // given
            Brand brand = BrandFixtures.inactiveBrand();
            Instant now = CommonVoFixtures.now();

            // when
            brand.activate(now);

            // then
            assertThat(brand.status()).isEqualTo(BrandStatus.ACTIVE);
            assertThat(brand.isActive()).isTrue();
            assertThat(brand.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("활성화된 Brand를 활성화해도 상태는 유지된다")
        void activateAlreadyActiveBrand() {
            // given
            Brand brand = BrandFixtures.activeBrand();
            Instant now = CommonVoFixtures.now();

            // when
            brand.activate(now);

            // then
            assertThat(brand.isActive()).isTrue();
            assertThat(brand.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("삭제 및 복원 메서드 테스트")
    class DeletionTest {
        @Test
        @DisplayName("활성 Brand를 삭제한다")
        void deleteActiveBrand() {
            // given
            Brand brand = BrandFixtures.activeBrand();
            Instant now = CommonVoFixtures.now();

            // when
            brand.delete(now);

            // then
            assertThat(brand.isDeleted()).isTrue();
            assertThat(brand.deletedAt()).isEqualTo(now);
            assertThat(brand.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("삭제된 Brand를 복원한다")
        void restoreDeletedBrand() {
            // given
            Brand brand = BrandFixtures.deletedBrand();
            Instant now = CommonVoFixtures.now();

            // when
            brand.restore(now);

            // then
            assertThat(brand.isDeleted()).isFalse();
            assertThat(brand.deletedAt()).isNull();
            assertThat(brand.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("활성 Brand를 복원해도 상태는 유지된다")
        void restoreActiveBrand() {
            // given
            Brand brand = BrandFixtures.activeBrand();
            Instant now = CommonVoFixtures.now();

            // when
            brand.restore(now);

            // then
            assertThat(brand.isDeleted()).isFalse();
            assertThat(brand.deletedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {
        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            Brand brand = BrandFixtures.activeBrand(100L);

            // when
            Long idValue = brand.idValue();

            // then
            assertThat(idValue).isEqualTo(100L);
        }

        @Test
        @DisplayName("codeValue()는 코드 값을 반환한다")
        void codeValueReturnsStringValue() {
            // given
            Brand brand = BrandFixtures.activeBrand(1L, "NIKE");

            // when
            String code = brand.codeValue();

            // then
            assertThat(code).isEqualTo("NIKE");
        }

        @Test
        @DisplayName("nameKo(), nameEn(), shortName()은 브랜드명 값을 반환한다")
        void brandNameReturnsStringValues() {
            // given
            BrandName name = BrandName.of("나이키", "Nike", "나");
            Brand brand =
                    Brand.reconstitute(
                            BrandId.of(1L),
                            BrandCode.of("NIKE"),
                            name,
                            BrandStatus.ACTIVE,
                            LogoUrl.empty(),
                            null,
                            CommonVoFixtures.yesterday(),
                            CommonVoFixtures.yesterday());

            // when & then
            assertThat(brand.nameKo()).isEqualTo("나이키");
            assertThat(brand.nameEn()).isEqualTo("Nike");
            assertThat(brand.shortName()).isEqualTo("나");
        }

        @Test
        @DisplayName("isActive()는 활성 상태 여부를 반환한다")
        void isActiveReturnsStatusFlag() {
            // given
            Brand active = BrandFixtures.activeBrand();
            Brand inactive = BrandFixtures.inactiveBrand();

            // when & then
            assertThat(active.isActive()).isTrue();
            assertThat(inactive.isActive()).isFalse();
        }

        @Test
        @DisplayName("logoUrlValue()는 로고 URL 값을 반환한다")
        void logoUrlValueReturnsStringValue() {
            // given
            Brand brand = BrandFixtures.activeBrand();

            // when
            String logoUrl = brand.logoUrlValue();

            // then
            assertThat(logoUrl).isEqualTo("https://example.com/logo.png");
        }

        @Test
        @DisplayName("logoUrl이 null이면 logoUrlValue()는 null을 반환한다")
        void logoUrlValueReturnsNullWhenLogoUrlIsNull() {
            // given
            Brand brand =
                    Brand.forNew(
                            BrandCode.of("NULL_LOGO"),
                            BrandName.of("로고 없음", null, null),
                            null,
                            CommonVoFixtures.now());

            // when
            String logoUrl = brand.logoUrlValue();

            // then
            assertThat(logoUrl).isNull();
        }

        @Test
        @DisplayName("isDeleted()는 삭제 상태 여부를 반환한다")
        void isDeletedReturnsDeletedFlag() {
            // given
            Brand active = BrandFixtures.activeBrand();
            Brand deleted = BrandFixtures.deletedBrand();

            // when & then
            assertThat(active.isDeleted()).isFalse();
            assertThat(deleted.isDeleted()).isTrue();
        }
    }
}
