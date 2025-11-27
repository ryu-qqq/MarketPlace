package com.ryuqq.marketplace.domain.brand.fixture;

import com.ryuqq.marketplace.domain.brand.aggregate.brand.Brand;
import com.ryuqq.marketplace.domain.brand.aggregate.brand.BrandAlias;
import com.ryuqq.marketplace.domain.brand.vo.BrandCode;
import com.ryuqq.marketplace.domain.brand.vo.BrandId;
import com.ryuqq.marketplace.domain.brand.vo.BrandMeta;
import com.ryuqq.marketplace.domain.brand.vo.BrandName;
import com.ryuqq.marketplace.domain.brand.vo.BrandStatus;
import com.ryuqq.marketplace.domain.brand.vo.CanonicalName;
import com.ryuqq.marketplace.domain.brand.vo.Country;
import com.ryuqq.marketplace.domain.brand.vo.DataQuality;
import com.ryuqq.marketplace.domain.brand.vo.Department;

import java.util.ArrayList;
import java.util.List;

/**
 * Brand Aggregate Fixture
 *
 * <p>Brand Aggregate Root 테스트용 Fixture 클래스</p>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class BrandFixture {

    private BrandFixture() {
        // Utility class
    }

    // ==================== 기본 Fixture (신규 생성) ====================

    /**
     * 기본 Brand 생성 (Nike, ACTIVE)
     */
    public static Brand defaultBrand() {
        return Brand.create(
            BrandCode.of("NIKE"),
            CanonicalName.of("Nike"),
            BrandName.of("나이키", "Nike", "NK"),
            Country.of("US"),
            Department.FASHION,
            false
        );
    }

    /**
     * 럭셔리 Brand 생성
     */
    public static Brand luxuryBrand() {
        return Brand.create(
            BrandCode.of("GUCCI"),
            CanonicalName.of("Gucci"),
            BrandName.of("구찌", "Gucci", null),
            Country.of("IT"),
            Department.FASHION,
            true
        );
    }

    /**
     * 뷰티 브랜드 생성
     */
    public static Brand beautyBrand() {
        return Brand.create(
            BrandCode.of("LANEIGE"),
            CanonicalName.of("Laneige"),
            BrandName.ofKorean("라네즈"),
            Country.of("KR"),
            Department.BEAUTY,
            false
        );
    }

    /**
     * 한국 브랜드 생성
     */
    public static Brand koreanBrand() {
        return Brand.create(
            BrandCode.of("SAMSUNG"),
            CanonicalName.of("Samsung"),
            BrandName.of("삼성", "Samsung", null),
            Country.of("KR"),
            Department.DIGITAL,
            false
        );
    }

    // ==================== 상태별 Fixture ====================

    /**
     * INACTIVE 상태 Brand
     */
    public static Brand inactiveBrand() {
        Brand brand = Brand.reconstitute(
            BrandId.of(1L),
            BrandCode.of("INACTIVE_BRAND"),
            CanonicalName.of("Inactive Brand"),
            BrandName.ofEnglish("Inactive Brand"),
            Country.of("US"),
            Department.FASHION,
            false,
            BrandStatus.INACTIVE,
            BrandMeta.empty(),
            DataQuality.unknown(),
            new ArrayList<>(),
            0L
        );
        brand.clearDomainEvents();
        return brand;
    }

    /**
     * BLOCKED 상태 Brand
     */
    public static Brand blockedBrand() {
        Brand brand = Brand.reconstitute(
            BrandId.of(2L),
            BrandCode.of("BLOCKED_BRAND"),
            CanonicalName.of("Blocked Brand"),
            BrandName.ofEnglish("Blocked Brand"),
            Country.of("US"),
            Department.FASHION,
            false,
            BrandStatus.BLOCKED,
            BrandMeta.empty(),
            DataQuality.unknown(),
            new ArrayList<>(),
            0L
        );
        brand.clearDomainEvents();
        return brand;
    }

    // ==================== 재구성 Fixture (DB에서 로드된 상태) ====================

    /**
     * 재구성된 Brand (DB에서 로드된 상태, ACTIVE)
     */
    public static Brand reconstitutedBrand() {
        return Brand.reconstitute(
            BrandId.of(1L),
            BrandCode.of("NIKE"),
            CanonicalName.of("Nike"),
            BrandName.of("나이키", "Nike", "NK"),
            Country.of("US"),
            Department.FASHION,
            false,
            BrandStatus.ACTIVE,
            BrandMeta.of("https://www.nike.com", "https://cdn.example.com/nike.png", "Global Sports Brand"),
            DataQuality.fromScore(85),
            new ArrayList<>(),
            1L
        );
    }

    /**
     * 재구성된 Brand with Aliases
     */
    public static Brand reconstitutedBrandWithAliases() {
        List<BrandAlias> aliases = List.of(
            BrandAliasFixture.reconstitutedAlias(100L, 1L, "Nike Korea"),
            BrandAliasFixture.reconstitutedAlias(101L, 1L, "나이키 코리아")
        );

        return Brand.reconstitute(
            BrandId.of(1L),
            BrandCode.of("NIKE"),
            CanonicalName.of("Nike"),
            BrandName.of("나이키", "Nike", "NK"),
            Country.of("US"),
            Department.FASHION,
            false,
            BrandStatus.ACTIVE,
            BrandMeta.of("https://www.nike.com", "https://cdn.example.com/nike.png", "Global Sports Brand"),
            DataQuality.fromScore(85),
            aliases,
            1L
        );
    }

    // ==================== 커스텀 빌더 ====================

    /**
     * 커스텀 Brand 생성 (신규)
     */
    public static Brand customBrand(
        String code,
        String canonicalName,
        String nameKo,
        String nameEn,
        String countryCode,
        Department department,
        boolean isLuxury
    ) {
        return Brand.create(
            BrandCode.of(code),
            CanonicalName.of(canonicalName),
            BrandName.of(nameKo, nameEn, null),
            Country.of(countryCode),
            department,
            isLuxury
        );
    }

    /**
     * 커스텀 재구성 Brand
     */
    public static Brand customReconstitutedBrand(
        Long id,
        String code,
        String canonicalName,
        String nameKo,
        String nameEn,
        String countryCode,
        Department department,
        boolean isLuxury,
        BrandStatus status,
        long version
    ) {
        return Brand.reconstitute(
            BrandId.of(id),
            BrandCode.of(code),
            CanonicalName.of(canonicalName),
            BrandName.of(nameKo, nameEn, null),
            Country.of(countryCode),
            department,
            isLuxury,
            status,
            BrandMeta.empty(),
            DataQuality.unknown(),
            new ArrayList<>(),
            version
        );
    }

    // ==================== Builder Pattern (복잡한 조합용) ====================

    /**
     * Builder 시작
     */
    public static BrandBuilder builder() {
        return new BrandBuilder();
    }

    public static class BrandBuilder {
        private Long id = null;
        private String code = "TEST_CODE";
        private String canonicalName = "Test Brand";
        private String nameKo = "테스트 브랜드";
        private String nameEn = "Test Brand";
        private String shortName = null;
        private String countryCode = "KR";
        private Department department = Department.FASHION;
        private boolean isLuxury = false;
        private BrandStatus status = BrandStatus.ACTIVE;
        private String officialWebsite = null;
        private String logoUrl = null;
        private String description = null;
        private DataQuality dataQuality = DataQuality.unknown();
        private List<BrandAlias> aliases = new ArrayList<>();
        private long version = 0L;

        public BrandBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BrandBuilder code(String code) {
            this.code = code;
            return this;
        }

        public BrandBuilder canonicalName(String canonicalName) {
            this.canonicalName = canonicalName;
            return this;
        }

        public BrandBuilder nameKo(String nameKo) {
            this.nameKo = nameKo;
            return this;
        }

        public BrandBuilder nameEn(String nameEn) {
            this.nameEn = nameEn;
            return this;
        }

        public BrandBuilder shortName(String shortName) {
            this.shortName = shortName;
            return this;
        }

        public BrandBuilder country(String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        public BrandBuilder department(Department department) {
            this.department = department;
            return this;
        }

        public BrandBuilder luxury(boolean isLuxury) {
            this.isLuxury = isLuxury;
            return this;
        }

        public BrandBuilder status(BrandStatus status) {
            this.status = status;
            return this;
        }

        public BrandBuilder officialWebsite(String officialWebsite) {
            this.officialWebsite = officialWebsite;
            return this;
        }

        public BrandBuilder logoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
            return this;
        }

        public BrandBuilder description(String description) {
            this.description = description;
            return this;
        }

        public BrandBuilder dataQuality(DataQuality dataQuality) {
            this.dataQuality = dataQuality;
            return this;
        }

        public BrandBuilder aliases(List<BrandAlias> aliases) {
            this.aliases = aliases;
            return this;
        }

        public BrandBuilder version(long version) {
            this.version = version;
            return this;
        }

        /**
         * 신규 Brand 생성
         */
        public Brand create() {
            return Brand.create(
                BrandCode.of(code),
                CanonicalName.of(canonicalName),
                BrandName.of(nameKo, nameEn, shortName),
                Country.of(countryCode),
                department,
                isLuxury
            );
        }

        /**
         * 재구성 Brand 생성
         */
        public Brand reconstitute() {
            return Brand.reconstitute(
                id != null ? BrandId.of(id) : BrandId.forNew(),
                BrandCode.of(code),
                CanonicalName.of(canonicalName),
                BrandName.of(nameKo, nameEn, shortName),
                Country.of(countryCode),
                department,
                isLuxury,
                status,
                BrandMeta.of(officialWebsite, logoUrl, description),
                dataQuality,
                aliases,
                version
            );
        }
    }
}
