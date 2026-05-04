package com.endava.insurance.insurance_service.domain.model;

import com.endava.insurance.insurance_service.domain.enums.BuildingType;
import com.endava.insurance.insurance_service.domain.enums.RiskFactorType;
import com.endava.insurance.insurance_service.domain.exception.ValidationException;
import com.endava.insurance.insurance_service.domain.model.geography.City;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@DisplayName("Building domain")
class BuildingTest {

    private static final Client MOCK_OWNER = mock(Client.class);
    private static final City MOCK_CITY = mock(City.class);

    private static Building.BuildingAttributes attrs(int year, BuildingType type, int floors, double surface, double value) {
        return new Building.BuildingAttributes(year, type, floors, surface, value);
    }

    @Nested
    @DisplayName("Constructor - validation")
    class ConstructorValidation {

        private static final Building.BuildingAttributes VALID_ATTRS = new Building.BuildingAttributes(2000, BuildingType.RESIDENTIAL, 1, 50.0, 100_000.0);

        @Test
        @DisplayName("null owner should fail")
        void nullOwner_throws() {
            assertThatThrownBy(() -> new Building(null, MOCK_CITY, "Street", "1", VALID_ATTRS))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Owner (client) is required");
        }

        @Test
        @DisplayName("null city should fail")
        void nullCity_throws() {
            assertThatThrownBy(() -> new Building(MOCK_OWNER, null, "Street", "1", VALID_ATTRS))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("City is required");
        }

        @Test
        @DisplayName("null street should fail")
        void nullStreet_throws() {
            assertThatThrownBy(() -> new Building(MOCK_OWNER, MOCK_CITY, null, "1", VALID_ATTRS))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Street is required");
        }

        @Test
        @DisplayName("blank street should fail")
        void blankStreet_throws() {
            assertThatThrownBy(() -> new Building(MOCK_OWNER, MOCK_CITY, "  ", "1", VALID_ATTRS))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Street is required");
        }

        @Test
        @DisplayName("null number should fail")
        void nullNumber_throws() {
            assertThatThrownBy(() -> new Building(MOCK_OWNER, MOCK_CITY, "Street", null, VALID_ATTRS))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Number is required");
        }

        @Test
        @DisplayName("blank number should fail")
        void blankNumber_throws() {
            assertThatThrownBy(() -> new Building(MOCK_OWNER, MOCK_CITY, "Street", "", VALID_ATTRS))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Number is required");
        }

        @Test
        @DisplayName("null type should fail")
        void nullType_throws() {
            var attrsNullType = new Building.BuildingAttributes(2000, null, 1, 50.0, 100_000.0);
            assertThatThrownBy(() -> new Building(MOCK_OWNER, MOCK_CITY, "Street", "1", attrsNullType))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Building type is required");
        }

        @Test
        @DisplayName("construction year less than 1800 should fail")
        void constructionYearBefore1800_throws() {
            var attrsBadYear = attrs(1799, BuildingType.RESIDENTIAL, 1, 50.0, 100_000.0);
            assertThatThrownBy(() -> new Building(MOCK_OWNER, MOCK_CITY, "Street", "1", attrsBadYear))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Construction year must be valid");
        }

        @Test
        @DisplayName("number of floors zero or negative should fail")
        void numberOfFloorsZeroOrNegative_throws() {
            var attrsZeroFloors = attrs(2000, BuildingType.RESIDENTIAL, 0, 50.0, 100_000.0);
            assertThatThrownBy(() -> new Building(MOCK_OWNER, MOCK_CITY, "Street", "1", attrsZeroFloors))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Number of floors must be positive");

            var attrsNegFloors = attrs(2000, BuildingType.RESIDENTIAL, -1, 50.0, 100_000.0);
            assertThatThrownBy(() -> new Building(MOCK_OWNER, MOCK_CITY, "Street", "1", attrsNegFloors))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Number of floors must be positive");
        }

        @Test
        @DisplayName("surface area zero or negative should fail")
        void surfaceAreaZeroOrNegative_throws() {
            var attrsZeroSurface = attrs(2000, BuildingType.RESIDENTIAL, 1, 0.0, 100_000.0);
            assertThatThrownBy(() -> new Building(MOCK_OWNER, MOCK_CITY, "Street", "1", attrsZeroSurface))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Surface area must be greater than 0");

            var attrsNegSurface = attrs(2000, BuildingType.RESIDENTIAL, 1, -10.0, 100_000.0);
            assertThatThrownBy(() -> new Building(MOCK_OWNER, MOCK_CITY, "Street", "1", attrsNegSurface))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Surface area must be greater than 0");
        }

        @Test
        @DisplayName("null insured value should fail")
        void nullInsuredValue_throws() {
            var attrsNullValue = new Building.BuildingAttributes(2000, BuildingType.RESIDENTIAL, 1, 50.0, null);
            assertThatThrownBy(() -> new Building(MOCK_OWNER, MOCK_CITY, "Street", "1", attrsNullValue))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Insured value is required");
        }

        @Test
        @DisplayName("negative insured value should fail")
        void negativeInsuredValue_throws() {
            var attrsNegValue = attrs(2000, BuildingType.RESIDENTIAL, 1, 50.0, -100.0);
            assertThatThrownBy(() -> new Building(MOCK_OWNER, MOCK_CITY, "Street", "1", attrsNegValue))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Insured value");
        }

        @Test
        @DisplayName("zero insured value should fail")
        void zeroInsuredValue_throws() {
            var attrsZeroValue = attrs(2000, BuildingType.RESIDENTIAL, 1, 50.0, 0.0);
            assertThatThrownBy(() -> new Building(MOCK_OWNER, MOCK_CITY, "Street", "1", attrsZeroValue))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Insured value");
        }
    }

    @Nested
    @DisplayName("Constructor - success and trimming")
    class ConstructorSuccess {

        @Test
        @DisplayName("valid args create building and trim street and number")
        void validArgs_trimsStreetAndNumber() throws ValidationException {
            Building building = new Building(
                    MOCK_OWNER, MOCK_CITY,
                    "  Main Street  ", "  5A  ",
                    attrs(2000, BuildingType.OFFICE, 3, 120.0, 200_000.0)
            );
            assertThat(building.getOwner()).isEqualTo(MOCK_OWNER);
            assertThat(building.getCity()).isEqualTo(MOCK_CITY);
            assertThat(building.getStreet()).isEqualTo("Main Street");
            assertThat(building.getNumber()).isEqualTo("5A");
            assertThat(building.getConstructionYear()).isEqualTo(2000);
            assertThat(building.getType()).isEqualTo(BuildingType.OFFICE);
            assertThat(building.getNumberOfFloors()).isEqualTo(3);
            assertThat(building.getSurfaceArea()).isEqualTo(120.0);
            assertThat(building.getInsuredValue()).isEqualTo(200_000.0);
        }

        @Test
        @DisplayName("construction year 1800 is allowed")
        void constructionYear1800Allowed() throws ValidationException {
            Building building = new Building(
                    MOCK_OWNER, MOCK_CITY, "St", "1", attrs(1800, BuildingType.RESIDENTIAL, 1, 50.0, 100_000.0)
            );
            assertThat(building.getConstructionYear()).isEqualTo(1800);
        }

        @Test
        @DisplayName("all BuildingType values are accepted")
        void allBuildingTypesAccepted() throws ValidationException {
            for (BuildingType type : BuildingType.values()) {
                Building b = new Building(
                        MOCK_OWNER, MOCK_CITY, "St", "1", attrs(2000, type, 1, 50.0, 100_000.0)
                );
                assertThat(b.getType()).isEqualTo(type);
            }
        }
    }

    @Nested
    @DisplayName("updateDetails - validation")
    class UpdateDetailsValidation {

        private Building createValidBuilding() throws ValidationException {
            return new Building(
                    MOCK_OWNER, MOCK_CITY, "Street", "1", attrs(2000, BuildingType.RESIDENTIAL, 1, 50.0, 100_000.0)
            );
        }

        @Test
        @DisplayName("null street should fail")
        void nullStreet_throws() throws ValidationException {
            Building b = createValidBuilding();
            var a = attrs(2000, BuildingType.RESIDENTIAL, 1, 50.0, 100_000.0);
            assertThatThrownBy(() -> b.updateDetails(null, "1", MOCK_CITY, a))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Street is required");
        }

        @Test
        @DisplayName("null city should fail")
        void nullCity_throws() throws ValidationException {
            Building b = createValidBuilding();
            var a = attrs(2000, BuildingType.RESIDENTIAL, 1, 50.0, 100_000.0);
            assertThatThrownBy(() -> b.updateDetails("Street", "1", null, a))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("City is required");
        }

        @Test
        @DisplayName("construction year less than 1800 should fail")
        void constructionYearBefore1800_throws() throws ValidationException {
            Building b = createValidBuilding();
            var a = attrs(1799, BuildingType.RESIDENTIAL, 1, 50.0, 100_000.0);
            assertThatThrownBy(() -> b.updateDetails("Street", "1", MOCK_CITY, a))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Construction year must be valid");
        }

        @Test
        @DisplayName("negative insured value should fail")
        void negativeInsuredValue_throws() throws ValidationException {
            Building b = createValidBuilding();
            var a = attrs(2000, BuildingType.RESIDENTIAL, 1, 50.0, -1.0);
            assertThatThrownBy(() -> b.updateDetails("Street", "1", MOCK_CITY, a))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Insured value");
        }
    }

    @Nested
    @DisplayName("updateDetails - success")
    class UpdateDetailsSuccess {

        @Test
        @DisplayName("updates all fields and trims street and number")
        void updatesFieldsAndTrims() throws ValidationException {
            Building building = new Building(
                    MOCK_OWNER, MOCK_CITY, "Old St", "1", attrs(2000, BuildingType.RESIDENTIAL, 1, 50.0, 100_000.0)
            );
            City newCity = mock(City.class);
            building.updateDetails(
                    "  New Street  ", "  10  ", newCity,
                    attrs(2010, BuildingType.INDUSTRIAL, 2, 100.0, 150_000.0)
            );
            assertThat(building.getStreet()).isEqualTo("New Street");
            assertThat(building.getNumber()).isEqualTo("10");
            assertThat(building.getCity()).isEqualTo(newCity);
            assertThat(building.getConstructionYear()).isEqualTo(2010);
            assertThat(building.getType()).isEqualTo(BuildingType.INDUSTRIAL);
            assertThat(building.getNumberOfFloors()).isEqualTo(2);
            assertThat(building.getSurfaceArea()).isEqualTo(100.0);
            assertThat(building.getInsuredValue()).isEqualTo(150_000.0);
        }
    }

    @Nested
    @DisplayName("getRiskFactors")
    class GetRiskFactors {

        @Test
        @DisplayName("returns unmodifiable set")
        void returnsUnmodifiableSet() throws ValidationException {
            Building building = new Building(
                    MOCK_OWNER, MOCK_CITY, "St", "1", attrs(2000, BuildingType.RESIDENTIAL, 1, 50.0, 100_000.0)
            );
            Set<RiskFactor> set = building.getRiskFactors();
            assertThat(set).isNotNull();
            RiskFactor factor = new RiskFactor(RiskFactorType.FLOOD_ZONE);
            assertThatThrownBy(() -> set.add(factor))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("returns empty set when no risk factors")
        void returnsEmptyWhenNone() throws ValidationException {
            Building building = new Building(
                    MOCK_OWNER, MOCK_CITY, "St", "1", attrs(2000, BuildingType.RESIDENTIAL, 1, 50.0, 100_000.0)
            );
            assertThat(building.getRiskFactors()).isEmpty();
        }
    }

    @Nested
    @DisplayName("replaceRiskFactors")
    class ReplaceRiskFactors {

        @Test
        @DisplayName("replace with collection adds all")
        void replaceWithCollection_setsRiskFactors() throws ValidationException {
            Building building = new Building(
                    MOCK_OWNER, MOCK_CITY, "St", "1", attrs(2000, BuildingType.RESIDENTIAL, 1, 50.0, 100_000.0)
            );
            RiskFactor rf1 = new RiskFactor(RiskFactorType.FLOOD_ZONE);
            RiskFactor rf2 = new RiskFactor(RiskFactorType.EARTHQUAKE_RISK_ZONE);
            building.replaceRiskFactors(Set.of(rf1, rf2));
            assertThat(building.getRiskFactors()).containsExactlyInAnyOrder(rf1, rf2);
        }

        @Test
        @DisplayName("replace with null clears risk factors")
        void replaceWithNull_clearsRiskFactors() throws ValidationException {
            Building building = new Building(
                    MOCK_OWNER, MOCK_CITY, "St", "1", attrs(2000, BuildingType.RESIDENTIAL, 1, 50.0, 100_000.0)
            );
            building.replaceRiskFactors(Set.of(new RiskFactor(RiskFactorType.FLOOD_ZONE)));
            assertThat(building.getRiskFactors()).hasSize(1);
            building.replaceRiskFactors(null);
            assertThat(building.getRiskFactors()).isEmpty();
        }

        @Test
        @DisplayName("replace overwrites previous risk factors")
        void replaceOverwritesPrevious() throws ValidationException {
            Building building = new Building(
                    MOCK_OWNER, MOCK_CITY, "St", "1", attrs(2000, BuildingType.RESIDENTIAL, 1, 50.0, 100_000.0)
            );
            building.replaceRiskFactors(Set.of(new RiskFactor(RiskFactorType.FLOOD_ZONE)));
            building.replaceRiskFactors(Set.of(new RiskFactor(RiskFactorType.WINDSTORM_ZONE)));
            assertThat(building.getRiskFactors()).hasSize(1);
            assertThat(building.getRiskFactors().iterator().next().getType()).isEqualTo(RiskFactorType.WINDSTORM_ZONE);
        }
    }
}
