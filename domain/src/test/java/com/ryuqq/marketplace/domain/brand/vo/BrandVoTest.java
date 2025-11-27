package com.ryuqq.marketplace.domain.brand.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Brand VO 단위 테스트
 *
 * <p><strong>테스트 대상 (16개 VO)</strong>:</p>
 * <ul>
 *   <li>BrandId, BrandAliasId</li>
 *   <li>BrandCode, CanonicalName</li>
 *   <li>BrandName, BrandMeta</li>
 *   <li>BrandStatus, Department</li>
 *   <li>Country, DataQuality, DataQualityLevel</li>
 *   <li>Confidence</li>
 *   <li>AliasName, AliasSource, AliasSourceType, AliasStatus</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("Brand VO 단위 테스트")
@Tag("unit")
@Tag("domain")
@Tag("brand")
@Tag("vo")
class BrandVoTest {

    // ==================== BrandId 테스트 ====================

    @Nested
    @DisplayName("BrandId 테스트")
    class BrandIdTest {

        @Test
        @DisplayName("[성공] BrandId.of()로 유효한 ID 생성")
        void of_ValidValue_ShouldCreate() {
            // Given & When
            BrandId brandId = BrandId.of(1L);

            // Then
            assertNotNull(brandId);
            assertEquals(1L, brandId.value());
            assertFalse(brandId.isNew());
        }

        @Test
        @DisplayName("[성공] BrandId.forNew()로 신규 ID 생성")
        void forNew_ShouldCreateNewBrandId() {
            // Given & When
            BrandId brandId = BrandId.forNew();

            // Then
            assertNotNull(brandId);
            assertNull(brandId.value());
            assertTrue(brandId.isNew());
        }

        @Test
        @DisplayName("[실패] BrandId.of()에 null 전달 시 예외")
        void of_Null_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> BrandId.of(null));
        }

        @Test
        @DisplayName("[실패] BrandId.of()에 음수 전달 시 예외")
        void of_Negative_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> BrandId.of(-1L));
        }

        @Test
        @DisplayName("[실패] BrandId.of()에 0 전달 시 예외")
        void of_Zero_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> BrandId.of(0L));
        }
    }

    // ==================== BrandAliasId 테스트 ====================

    @Nested
    @DisplayName("BrandAliasId 테스트")
    class BrandAliasIdTest {

        @Test
        @DisplayName("[성공] BrandAliasId.of()로 유효한 ID 생성")
        void of_ValidValue_ShouldCreate() {
            // Given & When
            BrandAliasId aliasId = BrandAliasId.of(100L);

            // Then
            assertNotNull(aliasId);
            assertEquals(100L, aliasId.value());
            assertFalse(aliasId.isNew());
        }

        @Test
        @DisplayName("[성공] BrandAliasId.forNew()로 신규 ID 생성")
        void forNew_ShouldCreateNewAliasId() {
            // Given & When
            BrandAliasId aliasId = BrandAliasId.forNew();

            // Then
            assertNotNull(aliasId);
            assertNull(aliasId.value());
            assertTrue(aliasId.isNew());
        }

        @Test
        @DisplayName("[실패] BrandAliasId.of()에 null 전달 시 예외")
        void of_Null_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> BrandAliasId.of(null));
        }
    }

    // ==================== BrandCode 테스트 ====================

    @Nested
    @DisplayName("BrandCode 테스트")
    class BrandCodeTest {

        @Test
        @DisplayName("[성공] BrandCode.of()로 유효한 코드 생성")
        void of_ValidCode_ShouldCreate() {
            // Given & When
            BrandCode code = BrandCode.of("NIKE");

            // Then
            assertNotNull(code);
            assertEquals("NIKE", code.value());
        }

        @Test
        @DisplayName("[성공] 숫자와 언더스코어 포함 코드 생성")
        void of_WithNumbersAndUnderscore_ShouldCreate() {
            assertDoesNotThrow(() -> BrandCode.of("BRAND_123"));
            assertDoesNotThrow(() -> BrandCode.of("AB"));
        }

        @Test
        @DisplayName("[성공] 공백 트림 처리")
        void of_WithWhitespace_ShouldTrim() {
            BrandCode code = BrandCode.of("  NIKE  ");
            assertEquals("NIKE", code.value());
        }

        @Test
        @DisplayName("[실패] null 코드 예외")
        void of_Null_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> BrandCode.of(null));
        }

        @Test
        @DisplayName("[실패] 빈 코드 예외")
        void of_Empty_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> BrandCode.of(""));
            assertThrows(IllegalArgumentException.class, () -> BrandCode.of("   "));
        }

        @Test
        @DisplayName("[실패] 소문자로 시작하는 코드 예외")
        void of_StartWithLowercase_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> BrandCode.of("nike"));
        }

        @Test
        @DisplayName("[실패] 숫자로 시작하는 코드 예외")
        void of_StartWithNumber_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> BrandCode.of("1NIKE"));
        }

        @Test
        @DisplayName("[실패] 특수문자 포함 코드 예외")
        void of_WithSpecialChars_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> BrandCode.of("NIKE-BRAND"));
            assertThrows(IllegalArgumentException.class, () -> BrandCode.of("NIKE@BRAND"));
        }

        @Test
        @DisplayName("[실패] 1자 코드 예외 (최소 2자)")
        void of_TooShort_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> BrandCode.of("A"));
        }
    }

    // ==================== CanonicalName 테스트 ====================

    @Nested
    @DisplayName("CanonicalName 테스트")
    class CanonicalNameTest {

        @Test
        @DisplayName("[성공] CanonicalName.of()로 유효한 이름 생성")
        void of_ValidName_ShouldCreate() {
            // Given & When
            CanonicalName name = CanonicalName.of("Nike");

            // Then
            assertNotNull(name);
            assertEquals("Nike", name.value());
        }

        @Test
        @DisplayName("[성공] 정규화된 이름 반환 (소문자 + 영숫자만)")
        void normalized_ShouldReturnLowercaseAlphanumericOnly() {
            CanonicalName name = CanonicalName.of("Nike Korea");
            // 정규화: 소문자 변환 + 알파벳/숫자만 남김
            assertEquals("nikekorea", name.normalized());
        }

        @Test
        @DisplayName("[실패] null 이름 예외")
        void of_Null_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> CanonicalName.of(null));
        }

        @Test
        @DisplayName("[실패] 빈 이름 예외")
        void of_Empty_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> CanonicalName.of(""));
        }
    }

    // ==================== BrandName 테스트 ====================

    @Nested
    @DisplayName("BrandName 테스트")
    class BrandNameTest {

        @Test
        @DisplayName("[성공] BrandName.of()로 전체 이름 생성")
        void of_AllNames_ShouldCreate() {
            // Given & When
            BrandName name = BrandName.of("나이키", "Nike", "NK");

            // Then
            assertNotNull(name);
            assertEquals("나이키", name.nameKo());
            assertEquals("Nike", name.nameEn());
            assertEquals("NK", name.shortName());
        }

        @Test
        @DisplayName("[성공] BrandName.ofKorean()로 한글명만 생성")
        void ofKorean_ShouldCreate() {
            BrandName name = BrandName.ofKorean("나이키");

            assertEquals("나이키", name.nameKo());
            assertNull(name.nameEn());
            assertNull(name.shortName());
        }

        @Test
        @DisplayName("[성공] BrandName.ofEnglish()로 영문명만 생성")
        void ofEnglish_ShouldCreate() {
            BrandName name = BrandName.ofEnglish("Nike");

            assertNull(name.nameKo());
            assertEquals("Nike", name.nameEn());
            assertNull(name.shortName());
        }

        @Test
        @DisplayName("[성공] displayName()은 한글명 우선 반환")
        void displayName_KoreanFirst() {
            BrandName name = BrandName.of("나이키", "Nike", null);
            assertEquals("나이키", name.displayName());
        }

        @Test
        @DisplayName("[성공] displayName()은 한글명 없으면 영문명 반환")
        void displayName_EnglishFallback() {
            BrandName name = BrandName.ofEnglish("Nike");
            assertEquals("Nike", name.displayName());
        }

        @Test
        @DisplayName("[실패] 한글명/영문명 모두 없으면 예외")
        void of_NoNames_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> BrandName.of(null, null, null));
            assertThrows(IllegalArgumentException.class, () -> BrandName.of("", "", null));
        }
    }

    // ==================== BrandMeta 테스트 ====================

    @Nested
    @DisplayName("BrandMeta 테스트")
    class BrandMetaTest {

        @Test
        @DisplayName("[성공] BrandMeta.of()로 메타 정보 생성")
        void of_ValidMeta_ShouldCreate() {
            // Given & When
            BrandMeta meta = BrandMeta.of(
                "https://www.nike.com",
                "https://cdn.example.com/nike.png",
                "글로벌 스포츠 브랜드"
            );

            // Then
            assertNotNull(meta);
            assertEquals("https://www.nike.com", meta.officialWebsite());
            assertEquals("https://cdn.example.com/nike.png", meta.logoUrl());
            assertEquals("글로벌 스포츠 브랜드", meta.description());
        }

        @Test
        @DisplayName("[성공] BrandMeta.empty()로 빈 메타 생성")
        void empty_ShouldCreateEmpty() {
            BrandMeta meta = BrandMeta.empty();

            assertNotNull(meta);
            assertNull(meta.officialWebsite());
            assertNull(meta.logoUrl());
            assertNull(meta.description());
        }

        @Test
        @DisplayName("[성공] hasMetaInfo() 검증 - 웹사이트 있음")
        void hasMetaInfo_WithWebsite_ShouldReturnTrue() {
            BrandMeta withWebsite = BrandMeta.of("https://www.nike.com", null, null);

            assertTrue(withWebsite.hasMetaInfo());
        }

        @Test
        @DisplayName("[성공] hasMetaInfo() 검증 - 로고 있음")
        void hasMetaInfo_WithLogo_ShouldReturnTrue() {
            BrandMeta withLogo = BrandMeta.of(null, "https://cdn.example.com/logo.png", null);

            assertTrue(withLogo.hasMetaInfo());
        }

        @Test
        @DisplayName("[성공] hasMetaInfo() 검증 - 빈 메타")
        void hasMetaInfo_Empty_ShouldReturnFalse() {
            BrandMeta empty = BrandMeta.empty();

            assertFalse(empty.hasMetaInfo());
        }
    }

    // ==================== BrandStatus 테스트 ====================

    @Nested
    @DisplayName("BrandStatus 테스트")
    class BrandStatusTest {

        @Test
        @DisplayName("[성공] ACTIVE 상태 검증")
        void active_ShouldBeUsable() {
            assertTrue(BrandStatus.ACTIVE.isUsable());
            assertFalse(BrandStatus.ACTIVE.isBlocked());
        }

        @Test
        @DisplayName("[성공] INACTIVE 상태 검증")
        void inactive_ShouldNotBeUsable() {
            assertFalse(BrandStatus.INACTIVE.isUsable());
            assertFalse(BrandStatus.INACTIVE.isBlocked());
        }

        @Test
        @DisplayName("[성공] BLOCKED 상태 검증")
        void blocked_ShouldBeBlocked() {
            assertFalse(BrandStatus.BLOCKED.isUsable());
            assertTrue(BrandStatus.BLOCKED.isBlocked());
        }

        @Test
        @DisplayName("[성공] fromString() 검증")
        void fromString_ShouldWork() {
            assertEquals(BrandStatus.ACTIVE, BrandStatus.fromString("ACTIVE"));
            assertEquals(BrandStatus.INACTIVE, BrandStatus.fromString("inactive"));
            assertEquals(BrandStatus.BLOCKED, BrandStatus.fromString("Blocked"));
        }

        @Test
        @DisplayName("[실패] fromString()에 잘못된 값 전달 시 예외")
        void fromString_Invalid_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> BrandStatus.fromString("INVALID"));
            assertThrows(IllegalArgumentException.class, () -> BrandStatus.fromString(null));
        }
    }

    // ==================== Country 테스트 ====================

    @Nested
    @DisplayName("Country 테스트")
    class CountryTest {

        @Test
        @DisplayName("[성공] Country.of()로 유효한 국가 코드 생성")
        void of_ValidCode_ShouldCreate() {
            assertDoesNotThrow(() -> Country.of("KR"));
            assertDoesNotThrow(() -> Country.of("US"));
            assertDoesNotThrow(() -> Country.of("FR"));
        }

        @Test
        @DisplayName("[성공] countryName() 반환 - 한국")
        void countryName_Korea_ShouldWork() {
            Country kr = Country.of("KR");
            assertEquals("대한민국", kr.countryName());
        }

        @Test
        @DisplayName("[성공] countryName() 반환 - 미국")
        void countryName_USA_ShouldWork() {
            assertEquals("미국", Country.of("US").countryName());
        }

        @Test
        @DisplayName("[성공] countryName() 반환 - 프랑스")
        void countryName_France_ShouldWork() {
            assertEquals("프랑스", Country.of("FR").countryName());
        }

        @Test
        @DisplayName("[성공] countryName() 반환 - 이탈리아")
        void countryName_Italy_ShouldWork() {
            assertEquals("이탈리아", Country.of("IT").countryName());
        }

        @Test
        @DisplayName("[성공] countryName() 반환 - 영국")
        void countryName_UK_ShouldWork() {
            assertEquals("영국", Country.of("UK").countryName());
        }

        @Test
        @DisplayName("[성공] countryName() 반환 - 일본")
        void countryName_Japan_ShouldWork() {
            assertEquals("일본", Country.of("JP").countryName());
        }

        @Test
        @DisplayName("[성공] countryName() 반환 - 중국")
        void countryName_China_ShouldWork() {
            assertEquals("중국", Country.of("CN").countryName());
        }

        @Test
        @DisplayName("[성공] countryName() 반환 - 독일")
        void countryName_Germany_ShouldWork() {
            assertEquals("독일", Country.of("DE").countryName());
        }

        @Test
        @DisplayName("[성공] countryName() 반환 - 스페인")
        void countryName_Spain_ShouldWork() {
            assertEquals("스페인", Country.of("ES").countryName());
        }

        @Test
        @DisplayName("[성공] countryName() 반환 - 기타 국가들")
        void countryName_OtherCountries_ShouldWork() {
            assertEquals("캐나다", Country.of("CA").countryName());
            assertEquals("호주", Country.of("AU").countryName());
            assertEquals("네덜란드", Country.of("NL").countryName());
            assertEquals("스웨덴", Country.of("SE").countryName());
            assertEquals("스위스", Country.of("CH").countryName());
            assertEquals("벨기에", Country.of("BE").countryName());
        }

        @Test
        @DisplayName("[성공] validCodes() 반환")
        void validCodes_ShouldReturnAllCodes() {
            assertNotNull(Country.validCodes());
            assertTrue(Country.validCodes().contains("KR"));
            assertTrue(Country.validCodes().contains("US"));
        }

        @Test
        @DisplayName("[실패] null 코드 예외")
        void of_Null_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> Country.of(null));
        }

        @Test
        @DisplayName("[실패] 유효하지 않은 국가 코드 예외")
        void of_Invalid_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> Country.of("XX"));
            assertThrows(IllegalArgumentException.class, () -> Country.of("KOREA"));
        }
    }

    // ==================== Confidence 테스트 ====================

    @Nested
    @DisplayName("Confidence 테스트")
    class ConfidenceTest {

        @Test
        @DisplayName("[성공] Confidence.of()로 유효한 신뢰도 생성")
        void of_ValidValue_ShouldCreate() {
            Confidence conf = Confidence.of(0.8);
            assertEquals(0.8, conf.value(), 0.001);
        }

        @Test
        @DisplayName("[성공] Confidence.certain()은 1.0 반환")
        void certain_ShouldReturnOne() {
            Confidence conf = Confidence.certain();
            assertEquals(1.0, conf.value(), 0.001);
        }

        @Test
        @DisplayName("[성공] Confidence.uncertain()은 0.5 반환")
        void uncertain_ShouldReturnHalf() {
            Confidence conf = Confidence.uncertain();
            assertEquals(0.5, conf.value(), 0.001);
        }

        @Test
        @DisplayName("[성공] Confidence.none()은 0.0 반환")
        void none_ShouldReturnZero() {
            Confidence conf = Confidence.none();
            assertEquals(0.0, conf.value(), 0.001);
        }

        @Test
        @DisplayName("[성공] isHighConfidence() 검증 (0.7 이상)")
        void isHighConfidence_ShouldWork() {
            assertTrue(Confidence.of(0.7).isHighConfidence());
            assertTrue(Confidence.of(0.8).isHighConfidence());
            assertFalse(Confidence.of(0.69).isHighConfidence());
        }

        @Test
        @DisplayName("[성공] isLowConfidence() 검증 (0.5 미만)")
        void isLowConfidence_ShouldWork() {
            assertTrue(Confidence.of(0.49).isLowConfidence());
            assertTrue(Confidence.of(0.3).isLowConfidence());
            assertFalse(Confidence.of(0.5).isLowConfidence());
        }

        @Test
        @DisplayName("[성공] toPercent() 검증")
        void toPercent_ShouldWork() {
            assertEquals(80, Confidence.of(0.8).toPercent());
            assertEquals(100, Confidence.certain().toPercent());
            assertEquals(0, Confidence.none().toPercent());
        }

        @Test
        @DisplayName("[실패] 범위를 벗어난 값 예외")
        void of_OutOfRange_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> Confidence.of(-0.1));
            assertThrows(IllegalArgumentException.class, () -> Confidence.of(1.1));
        }

        @Test
        @DisplayName("[성공] 경계값 테스트")
        void of_BoundaryValues_ShouldWork() {
            assertDoesNotThrow(() -> Confidence.of(0.0));
            assertDoesNotThrow(() -> Confidence.of(1.0));
        }
    }

    // ==================== DataQuality 테스트 ====================

    @Nested
    @DisplayName("DataQuality 테스트")
    class DataQualityTest {

        @Test
        @DisplayName("[성공] DataQuality.of()로 생성")
        void of_ValidValues_ShouldCreate() {
            DataQuality quality = DataQuality.of(DataQualityLevel.HIGH, 85);
            assertEquals(DataQualityLevel.HIGH, quality.level());
            assertEquals(85, quality.score());
        }

        @Test
        @DisplayName("[성공] DataQuality.fromScore()로 레벨 자동 결정")
        void fromScore_ShouldDetermineLevel() {
            assertEquals(DataQualityLevel.HIGH, DataQuality.fromScore(85).level());
            assertEquals(DataQualityLevel.MID, DataQuality.fromScore(55).level());
            assertEquals(DataQualityLevel.LOW, DataQuality.fromScore(25).level());
        }

        @Test
        @DisplayName("[성공] DataQuality.unknown()은 기본값 반환")
        void unknown_ShouldReturnDefault() {
            DataQuality quality = DataQuality.unknown();
            assertEquals(DataQualityLevel.UNKNOWN, quality.level());
            assertEquals(0, quality.score());
        }

        @Test
        @DisplayName("[실패] 범위를 벗어난 스코어 예외")
        void of_InvalidScore_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> DataQuality.of(DataQualityLevel.HIGH, -1));
            assertThrows(IllegalArgumentException.class, () -> DataQuality.of(DataQualityLevel.HIGH, 101));
        }
    }

    // ==================== DataQualityLevel 테스트 ====================

    @Nested
    @DisplayName("DataQualityLevel 테스트")
    class DataQualityLevelTest {

        @Test
        @DisplayName("[성공] 모든 DataQualityLevel 값 확인")
        void allValues_ShouldExist() {
            assertNotNull(DataQualityLevel.UNKNOWN);
            assertNotNull(DataQualityLevel.LOW);
            assertNotNull(DataQualityLevel.MID);
            assertNotNull(DataQualityLevel.HIGH);
        }

        @Test
        @DisplayName("[성공] fromString() 검증")
        void fromString_ShouldWork() {
            assertEquals(DataQualityLevel.HIGH, DataQualityLevel.fromString("HIGH"));
            assertEquals(DataQualityLevel.MID, DataQualityLevel.fromString("mid"));
            assertEquals(DataQualityLevel.LOW, DataQualityLevel.fromString("Low"));
            assertEquals(DataQualityLevel.UNKNOWN, DataQualityLevel.fromString("UNKNOWN"));
        }

        @Test
        @DisplayName("[실패] fromString()에 잘못된 값 전달 시 예외")
        void fromString_Invalid_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> DataQualityLevel.fromString("INVALID"));
            assertThrows(IllegalArgumentException.class, () -> DataQualityLevel.fromString(null));
            assertThrows(IllegalArgumentException.class, () -> DataQualityLevel.fromString(""));
        }
    }

    // ==================== Department 테스트 ====================

    @Nested
    @DisplayName("Department 테스트")
    class DepartmentTest {

        @Test
        @DisplayName("[성공] 모든 Department 값 확인")
        void allValues_ShouldExist() {
            assertNotNull(Department.FASHION);
            assertNotNull(Department.BEAUTY);
            assertNotNull(Department.LIVING);
            assertNotNull(Department.DIGITAL);
            assertNotNull(Department.ETC);
        }

        @Test
        @DisplayName("[성공] fromString() 검증")
        void fromString_ShouldWork() {
            assertEquals(Department.FASHION, Department.fromString("FASHION"));
            assertEquals(Department.BEAUTY, Department.fromString("beauty"));
        }

        @Test
        @DisplayName("[실패] fromString()에 잘못된 값 전달 시 예외")
        void fromString_Invalid_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> Department.fromString("INVALID"));
        }
    }

    // ==================== AliasName 테스트 ====================

    @Nested
    @DisplayName("AliasName 테스트")
    class AliasNameTest {

        @Test
        @DisplayName("[성공] AliasName.of()로 별칭 생성")
        void of_ValidName_ShouldCreate() {
            AliasName name = AliasName.of("Nike Korea");
            assertEquals("Nike Korea", name.original());
            assertNotNull(name.normalized());
        }

        @Test
        @DisplayName("[성공] 정규화 검증 (소문자, 특수문자/공백 제거)")
        void normalized_ShouldWork() {
            AliasName name = AliasName.of("Nike Korea!");
            // 정규화: 소문자 변환 + 알파벳/숫자만 남김
            assertEquals("nikekorea", name.normalized());
        }

        @Test
        @DisplayName("[실패] null 별칭 예외")
        void of_Null_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> AliasName.of(null));
        }

        @Test
        @DisplayName("[실패] 빈 별칭 예외")
        void of_Empty_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> AliasName.of(""));
        }
    }

    // ==================== AliasSource 테스트 ====================

    @Nested
    @DisplayName("AliasSource 테스트")
    class AliasSourceTest {

        @Test
        @DisplayName("[성공] AliasSource.manual()로 수동 소스 생성")
        void manual_ShouldCreate() {
            AliasSource source = AliasSource.manual();
            assertEquals(AliasSourceType.MANUAL, source.sourceType());
            assertEquals(0L, source.sellerId());
            assertEquals("GLOBAL", source.mallCode());
        }

        @Test
        @DisplayName("[성공] AliasSource.seller()로 셀러 소스 생성")
        void seller_ShouldCreate() {
            AliasSource source = AliasSource.seller(123L);
            assertEquals(AliasSourceType.SELLER, source.sourceType());
            assertEquals(123L, source.sellerId());
        }

        @Test
        @DisplayName("[성공] AliasSource.externalMall()로 외부몰 소스 생성")
        void externalMall_ShouldCreate() {
            AliasSource source = AliasSource.externalMall("COUPANG");
            assertEquals(AliasSourceType.EXTERNAL_MALL, source.sourceType());
            assertEquals("COUPANG", source.mallCode());
        }

        @Test
        @DisplayName("[성공] AliasSource.system()로 시스템 소스 생성")
        void system_ShouldCreate() {
            AliasSource source = AliasSource.system();
            assertEquals(AliasSourceType.SYSTEM, source.sourceType());
        }
    }

    // ==================== AliasSourceType 테스트 ====================

    @Nested
    @DisplayName("AliasSourceType 테스트")
    class AliasSourceTypeTest {

        @Test
        @DisplayName("[성공] 모든 AliasSourceType 값 확인")
        void allValues_ShouldExist() {
            assertNotNull(AliasSourceType.MANUAL);
            assertNotNull(AliasSourceType.SELLER);
            assertNotNull(AliasSourceType.EXTERNAL_MALL);
            assertNotNull(AliasSourceType.SYSTEM);
        }

        @Test
        @DisplayName("[성공] isManual() 검증")
        void isManual_ShouldWork() {
            assertTrue(AliasSourceType.MANUAL.isManual());
            assertFalse(AliasSourceType.SELLER.isManual());
        }

        @Test
        @DisplayName("[성공] isAutoGenerated() 검증")
        void isAutoGenerated_ShouldWork() {
            assertTrue(AliasSourceType.SYSTEM.isAutoGenerated());
            assertFalse(AliasSourceType.MANUAL.isAutoGenerated());
        }
    }

    // ==================== AliasStatus 테스트 ====================

    @Nested
    @DisplayName("AliasStatus 테스트")
    class AliasStatusTest {

        @Test
        @DisplayName("[성공] 모든 AliasStatus 값 확인")
        void allValues_ShouldExist() {
            assertNotNull(AliasStatus.AUTO_SUGGESTED);
            assertNotNull(AliasStatus.PENDING_REVIEW);
            assertNotNull(AliasStatus.CONFIRMED);
            assertNotNull(AliasStatus.REJECTED);
        }

        @Test
        @DisplayName("[성공] isActive() 검증")
        void isActive_ShouldWork() {
            assertTrue(AliasStatus.CONFIRMED.isActive());
            assertTrue(AliasStatus.AUTO_SUGGESTED.isActive());
            assertFalse(AliasStatus.PENDING_REVIEW.isActive());
            assertFalse(AliasStatus.REJECTED.isActive());
        }

        @Test
        @DisplayName("[성공] needsReview() 검증")
        void needsReview_ShouldWork() {
            assertTrue(AliasStatus.PENDING_REVIEW.needsReview());
            assertFalse(AliasStatus.CONFIRMED.needsReview());
        }

        @Test
        @DisplayName("[성공] isRejected() 검증")
        void isRejected_ShouldWork() {
            assertTrue(AliasStatus.REJECTED.isRejected());
            assertFalse(AliasStatus.CONFIRMED.isRejected());
        }

        @Test
        @DisplayName("[성공] isConfirmed() 검증")
        void isConfirmed_ShouldWork() {
            assertTrue(AliasStatus.CONFIRMED.isConfirmed());
            assertFalse(AliasStatus.REJECTED.isConfirmed());
        }

        @Test
        @DisplayName("[성공] fromString() 검증")
        void fromString_ShouldWork() {
            assertEquals(AliasStatus.CONFIRMED, AliasStatus.fromString("CONFIRMED"));
            assertEquals(AliasStatus.REJECTED, AliasStatus.fromString("rejected"));
        }

        @Test
        @DisplayName("[실패] fromString()에 잘못된 값 전달 시 예외")
        void fromString_Invalid_ShouldThrow() {
            assertThrows(IllegalArgumentException.class, () -> AliasStatus.fromString("INVALID"));
        }
    }
}
